/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.forms.createprocess;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.MetadataEntry;
import org.kitodo.api.dataeditor.rulesetmanagement.FunctionalMetadata;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.StructuralElementViewInterface;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.api.dataformat.Workpiece;
import org.kitodo.api.externaldatamanagement.ImportConfigurationType;
import org.kitodo.data.database.beans.ImportConfiguration;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.beans.Project;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.beans.Template;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.exceptions.CommandException;
import org.kitodo.exceptions.InvalidMetadataValueException;
import org.kitodo.exceptions.NoSuchMetadataFieldException;
import org.kitodo.exceptions.ProcessGenerationException;
import org.kitodo.exceptions.RulesetNotFoundException;
import org.kitodo.production.dto.ProcessDTO;
import org.kitodo.production.enums.ObjectType;
import org.kitodo.production.forms.BaseForm;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.helper.TempProcess;
import org.kitodo.production.interfaces.MetadataTreeTableInterface;
import org.kitodo.production.interfaces.RulesetSetupInterface;
import org.kitodo.production.metadata.MetadataEditor;
import org.kitodo.production.process.ProcessGenerator;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.ImportService;
import org.kitodo.production.services.data.ProcessService;
import org.kitodo.production.services.dataeditor.DataEditorService;
import org.primefaces.PrimeFaces;
import org.primefaces.model.TreeNode;

@Named("CreateProcessForm")
@ViewScoped
public class CreateProcessForm extends BaseForm implements MetadataTreeTableInterface, RulesetSetupInterface {

    private static final Logger logger = LogManager.getLogger(CreateProcessForm.class);

    private final CatalogImportDialog catalogImportDialog = new CatalogImportDialog(this);
    private final FileUploadDialog fileUploadDialog = new FileUploadDialog(this);
    private final SearchDialog searchDialog = new SearchDialog(this);
    private final ProcessDataTab processDataTab = new ProcessDataTab(this);
    private final TitleRecordLinkTab titleRecordLinkTab = new TitleRecordLinkTab(this);
    private final AddMetadataDialog addMetadataDialog = new AddMetadataDialog(this);

    private RulesetManagementInterface rulesetManagement;
    private final List<Locale.LanguageRange> priorityList;
    private final String acquisitionStage = "create";
    private Project project;
    private Template template;
    private LinkedList<TempProcess> processes = new LinkedList<>();
    private LinkedList<TempProcess> childProcesses = new LinkedList<>();
    private final String processListPath = MessageFormat.format(REDIRECT_PATH, "processes");
    private String referringView = "";
    private int progress;
    private TempProcess currentProcess;
    private String identifierMetadata;

    static final int TITLE_RECORD_LINK_TAB_INDEX = 1;

    public CreateProcessForm() {
        priorityList = ServiceManager.getUserService().getCurrentMetadataLanguage();
    }

    CreateProcessForm(List<Locale.LanguageRange> priorityList) {
        this.priorityList = priorityList;
    }

    /**
     * Returns the ruleset management to access the ruleset.
     *
     * @return the ruleset management
     */
    @Override
    public RulesetManagementInterface getRulesetManagement() {
        return rulesetManagement;
    }

    /**
     * Update ruleset and docType.
     *
     * @param ruleset
     *            as Ruleset
     * @throws IOException
     *             thrown if ruleset could not be read
     */
    public void updateRulesetAndDocType(Ruleset ruleset) throws IOException {
        rulesetManagement = ServiceManager.getRulesetService().openRuleset(ruleset);
        processDataTab.setAllDocTypes(getAllRulesetDivisions());
    }

    private List<SelectItem> getAllRulesetDivisions() {
        List<SelectItem> allDocTypes = rulesetManagement
                .getStructuralElements(priorityList).entrySet()
                .stream().map(entry -> new SelectItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        if (allDocTypes.isEmpty()) {
            Helper.setErrorMessage("errorLoadingDocTypes");
        }
        return allDocTypes;
    }

    /**
     * Returns the current acquisition stage to adapt the displaying of fields
     * accordingly.
     *
     * @return the current acquisition stage
     */
    @Override
    public String getAcquisitionStage() {
        return acquisitionStage;
    }

    /**
     * Returns the language preference list of the editing user to display
     * labels and options in the user-preferred language.
     *
     * @return the language preference list
     */
    @Override
    public List<Locale.LanguageRange> getPriorityList() {
        return priorityList;
    }

    /**
     * Get importTab.
     *
     * @return value of catalogImportDialog
     */
    public CatalogImportDialog getCatalogImportDialog() {
        return catalogImportDialog;
    }

    /**
     * Get fileUploadDialog.
     *
     * @return value of fileUploadDialog
     */
    public FileUploadDialog getFileUploadDialog() {
        return fileUploadDialog;
    }

    /**
     * Get processDataTab.
     *
     * @return value of processDataTab
     */
    public ProcessDataTab getProcessDataTab() {
        return processDataTab;
    }

    /**
     * Get processMetadata.
     *
     * @return value of processMetadata
     */
    public ProcessMetadata getProcessMetadata() {
        return currentProcess.getProcessMetadata();
    }

    /**
     * Get searchDialog.
     *
     * @return value of searchDialog
     */
    public SearchDialog getSearchDialog() {
        return searchDialog;
    }

    /**
     * Get titleRecordLinkTab.
     *
     * @return value of titleRecordLinkTab
     */
    public TitleRecordLinkTab getTitleRecordLinkTab() {
        return titleRecordLinkTab;
    }

    /**
     * Get addMetadataDialog.
     * @return addMetadataDialog
     */
    public AddMetadataDialog getAddMetadataDialog() {
        return addMetadataDialog;
    }

    /**
     * Get newProcesses.
     *
     * @return value of newProcesses
     */
    public List<TempProcess> getProcesses() {
        return processes;
    }

    /**
     * Set newProcesses.
     *
     * @param processes as java.util.List of Process
     */
    public void setProcesses(LinkedList<TempProcess> processes) {
        this.processes = processes;
    }

    /**
     * Get child processes.
     *
     * @return childProcesses
     */
    public List<TempProcess> getChildProcesses() {
        return this.childProcesses;
    }

    /**
     * Set childProcesses.
     *
     * @param childProcesses as java.util.LinkedList of TempProcess
     */
    public void setChildProcesses(LinkedList<TempProcess> childProcesses) {
        this.childProcesses = childProcesses;
    }

    /**
     * Get the main Process that want to be created.
     *
     * @return value of first element in newProcesses
     */
    public Process getMainProcess() {
        if (processes.isEmpty()) {
            try {
                ProcessGenerator processGenerator = new ProcessGenerator();
                processGenerator.generateProcess(template.getId(), project.getId());
                processes.add(new TempProcess(processGenerator.getGeneratedProcess(), new Workpiece()));
            } catch (ProcessGenerationException exception) {
                Helper.setErrorMessage(exception.getLocalizedMessage(), logger, exception);
            }
        }
        return processes.get(0).getProcess();
    }

    /**
     * Get template.
     *
     * @return value of template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Set template.
     *
     * @param template as org.kitodo.data.database.beans.Template
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * Get project.
     *
     * @return value of project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set project.
     *
     * @param project as org.kitodo.data.database.beans.Project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Set identifierMetadata.
     *
     * @param identifierMetadata as java.lang.String
     */
    public void setIdentifierMetadata(String identifierMetadata) {
        this.identifierMetadata = identifierMetadata;
    }

    /**
     * Create the process and save the metadata.
     */
    public String createNewProcess() {
        try {
            if (!canCreateProcess()) {
                return this.stayOnCurrentPage;
            }
            createProcessHierarchy();
            if (Objects.nonNull(PrimeFaces.current()) && Objects.nonNull(FacesContext.getCurrentInstance())) {
                PrimeFaces.current().executeScript("PF('sticky-notifications').renderMessage({'summary':'"
                        + Helper.getTranslation("processSaving") + "','detail':'"
                        + Helper.getTranslation( "youWillBeRedirected") + "','severity':'info'});");
            }
            return processListPath;
        } catch (DataException e) {
            Helper.setErrorMessage("errorSaving", new Object[] {ObjectType.PROCESS.getTranslationSingular() },
                    logger, e);
        } catch (RulesetNotFoundException e) {
            String rulesetFile = "Process list is empty";
            if (!this.processes.isEmpty() && Objects.nonNull(getMainProcess().getRuleset())) {
                rulesetFile = getMainProcess().getRuleset().getFile();
            }
            Helper.setErrorMessage("rulesetNotFound", new Object[] {rulesetFile }, logger, e);
        } catch (IOException | ProcessGenerationException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return this.stayOnCurrentPage;
    }

    /**
     * Create new Process and reload current view to add another Process.
     *
     * @return path to reload current view
     */
    public String createNewProcessAndContinue() {
        String destination = createNewProcess();
        if (!destination.equals(processListPath)) {
            return destination;
        }
        Process parentProcess = titleRecordLinkTab.getTitleRecordProcess();
        return FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath()
                + "?referrer=" + referringView
                + "&templateId=" + template.getId()
                + "&projectId=" + project.getId()
                + (Objects.nonNull(parentProcess) ? "&parentId=" + parentProcess.getId() : "")
                + "&faces-redirect=true";
    }

    private boolean canCreateProcess() throws IOException {
        if (Objects.nonNull(titleRecordLinkTab.getTitleRecordProcess())) {
            if ((Objects.isNull(titleRecordLinkTab.getSelectedInsertionPosition())
                    || titleRecordLinkTab.getSelectedInsertionPosition().isEmpty())) {
                Helper.setErrorMessage("createProcessForm.createNewProcess.noInsertionPositionSelected");
                return false;
            }
            String forbiddenParentType = parentTypeIfForbidden();
            if (Objects.nonNull(forbiddenParentType)) {
                Helper.setErrorMessage(Helper.getTranslation("dataEditor.forbiddenChildElement",
                    processDataTab.getDocType(), forbiddenParentType));
                return false;
            }
        }
        return true;
    }

    private String parentTypeIfForbidden() throws IOException {
        URI metadataFileUri = ServiceManager.getProcessService()
                .getMetadataFileUri(titleRecordLinkTab.getTitleRecordProcess());
        Workpiece workpiece = ServiceManager.getMetsService().loadWorkpiece(metadataFileUri);
        List<String> indices = Arrays.asList(titleRecordLinkTab.getSelectedInsertionPosition()
                .split(Pattern.quote(MetadataEditor.INSERTION_POSITION_SEPARATOR)));
        LogicalDivision logicalDivision = workpiece.getLogicalStructure();
        for (int index = 0; index < indices.size(); index++) {
            if (index < indices.size() - 1) {
                logicalDivision = logicalDivision.getChildren().get(Integer.parseInt(indices.get(index)));
            } else {
                String parentType = logicalDivision.getType();
                StructuralElementViewInterface divisionView = rulesetManagement.getStructuralElementView(parentType,
                    acquisitionStage, priorityList);
                if (divisionView.getAllowedSubstructuralElements().containsKey(processDataTab.getDocType())) {
                    return null;
                } else {
                    return parentType;
                }
            }
        }
        return "";
    }

    /**
     * Prepare new process which will be created.
     *
     * @param templateId
     *            id of template to query from database
     * @param projectId
     *            id of project to query from database
     * @param referringView
     *            view the user was coming from
     */
    public void prepareProcess(int templateId, int projectId, String referringView, Integer parentId) {
        this.referringView = referringView;
        ProcessGenerator processGenerator = new ProcessGenerator();
        try {
            boolean generated = processGenerator.generateProcess(templateId, projectId);
            if (generated) {
                Workpiece workpiece = new Workpiece();
                processes = new LinkedList<>(Collections.singletonList(new TempProcess(
                        processGenerator.getGeneratedProcess(), workpiece)));
                currentProcess = processes.get(0);
                project = processGenerator.getProject();
                template = processGenerator.getTemplate();
                updateRulesetAndDocType(getMainProcess().getRuleset());
                if (Objects.nonNull(project) && Objects.nonNull(project.getDefaultImportConfiguration())) {
                    setDefaultImportConfiguration(project.getDefaultImportConfiguration());
                }
                if (Objects.nonNull(parentId) && parentId != 0) {
                    ProcessDTO parentProcess = ServiceManager.getProcessService().findById(parentId);
                    RulesetManagementInterface rulesetManagement = ServiceManager.getRulesetService()
                            .openRuleset(ServiceManager.getRulesetService().getById(parentProcess.getRuleset().getId()));
                    Map<String, String> allowedSubstructuralElements = rulesetManagement
                            .getStructuralElementView(parentProcess.getBaseType(), "", priorityList)
                            .getAllowedSubstructuralElements();
                    List<SelectItem> docTypes = allowedSubstructuralElements.entrySet()
                            .stream().map(entry -> new SelectItem(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                    processDataTab.setAllDocTypes(docTypes);
                    titleRecordLinkTab.setChosenParentProcess(String.valueOf(parentId));
                    titleRecordLinkTab.chooseParentProcess();
                    if (setChildCount(titleRecordLinkTab.getTitleRecordProcess(), rulesetManagement, workpiece)) {
                        updateRulesetAndDocType(getMainProcess().getRuleset());
                    }
                }
                processDataTab.prepare();
            }
        } catch (ProcessGenerationException | DataException | DAOException | IOException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
        }
    }

    private void setDefaultImportConfiguration(ImportConfiguration importConfiguration) {
        if (ImportConfigurationType.OPAC_SEARCH.name().equals(importConfiguration.getConfigurationType())) {
            catalogImportDialog.getHitModel().setImportConfiguration(importConfiguration);
            PrimeFaces.current().ajax().update("catalogSearchDialog");
        } else if (ImportConfigurationType.PROCESS_TEMPLATE.name().equals(importConfiguration.getConfigurationType())) {
            searchDialog.setOriginalProcess(importConfiguration.getDefaultTemplateProcess());
            PrimeFaces.current().ajax().update("searchEditDialog");
        } else if (ImportConfigurationType.FILE_UPLOAD.name().equals(importConfiguration.getConfigurationType())) {
            fileUploadDialog.setImportConfiguration(importConfiguration);
            PrimeFaces.current().ajax().update("fileUploadDialog");
        }
    }

    static boolean setChildCount(Process parent, RulesetManagementInterface ruleset, Workpiece workpiece) throws IOException {
        Collection<String> childCountKeys = ruleset.getFunctionalKeys(FunctionalMetadata.CHILD_COUND);
        if (childCountKeys.isEmpty()) {
            return false;
        }
        String childCount = Integer.toString(parent.getChildren().size() + 1);
        for (String childCountKey : childCountKeys) {
            MetadataEntry entry = new MetadataEntry();
            entry.setKey(childCountKey);
            entry.setValue(childCount);
            workpiece.getLogicalStructure().getMetadata().add(entry);
        }
        return true;
    }

    /**
     * Create process hierarchy.
     */
    private void createProcessHierarchy()
            throws DataException, ProcessGenerationException, IOException {
        // discard all processes in hierarchy except the first if parent process in
        // title record link tab is selected!
        if (this.processes.size() > 1 && Objects.nonNull(this.titleRecordLinkTab.getTitleRecordProcess())
                && Objects.nonNull(this.titleRecordLinkTab.getSelectedInsertionPosition())
                && !this.titleRecordLinkTab.getSelectedInsertionPosition().isEmpty()) {
            this.processes = new LinkedList<>(Collections.singletonList(this.processes.get(0)));
        }
        ProcessService.checkTasks(this.getMainProcess(), processDataTab.getDocType());
        processAncestors();
        processChildren();
        // main process and it's ancestors need to be saved, so they have IDs before creating their process directories
        ServiceManager.getProcessService().save(getMainProcess(), true);
        if (!createProcessesLocation(this.processes)) {
            throw new IOException("Unable to create directories for process hierarchy!");
        }

        if (this.catalogImportDialog.isImportChildren() && !createProcessesLocation(this.childProcesses)) {
            throw new IOException("Unable to create directories for child processes!");
        }
        saveProcessHierarchyMetadata();
        // TODO: do the same 'ensureNonEmptyTitles' for child processes?
        if (ImportService.ensureNonEmptyTitles(this.processes)) {
            // saving the main process automatically saves its parent and ancestor processes as well!
            ServiceManager.getProcessService().save(getMainProcess(), true);
        }

        // add links between child processes and main process
        this.saveChildProcessLinks();

        // if a process is selected in 'TitleRecordLinkTab' link it as parent with the first process in the list
        if (this.processes.size() > 0 && Objects.nonNull(titleRecordLinkTab.getTitleRecordProcess())) {
            MetadataEditor.addLink(titleRecordLinkTab.getTitleRecordProcess(),
                titleRecordLinkTab.getSelectedInsertionPosition(), this.processes.get(0).getProcess().getId());
            ProcessService.setParentRelations(titleRecordLinkTab.getTitleRecordProcess(),
                processes.get(0).getProcess());
            String summary = Helper.getTranslation("newProcess.catalogueSearch.linkedToExistingProcessSummary");
            String detail = Helper.getTranslation("newProcess.catalogueSearch.linkedToExistingProcessDetail",
                titleRecordLinkTab.getTitleRecordProcess().getTitle());
            catalogImportDialog.showGrowlMessage(summary, detail);
        } else {
            // add links between consecutive processes in list
            for (int i = 0; i < this.processes.size() - 1; i++) {
                TempProcess tempProcess = this.processes.get(i);
                MetadataEditor.addLink(this.processes.get(i + 1).getProcess(), "0", tempProcess.getProcess().getId());
            }
        }
        ServiceManager.getProcessService().save(getMainProcess(), true);
    }

    /**
     * Save links between child processes and main process.
     *
     * @throws DataException thrown if child process could not be saved
     * @throws IOException thrown if link between child and parent process could not be added
     */
    private void saveChildProcessLinks() throws IOException, DataException {
        this.progress = 0;
        if (Objects.nonNull(PrimeFaces.current()) && Objects.nonNull(FacesContext.getCurrentInstance())) {
            PrimeFaces.current().executeScript("PF('progressDialog')");
            PrimeFaces.current().ajax().update("progressForm:progressBar");
        }
        for (TempProcess childProcess : this.childProcesses) {
            int currentIndex = childProcesses.indexOf(childProcess);
            MetadataEditor.addLink(getMainProcess(), String.valueOf(currentIndex),
                    childProcess.getProcess().getId());
            ServiceManager.getProcessService().save(childProcess.getProcess());
            this.progress = (currentIndex + 1) * 100 / this.childProcesses.size();
            if (Objects.nonNull(PrimeFaces.current()) && Objects.nonNull(FacesContext.getCurrentInstance())) {
                PrimeFaces.current().ajax().update("progressForm:progressBar");
            }
        }
        if (Objects.nonNull(PrimeFaces.current()) && Objects.nonNull(FacesContext.getCurrentInstance())) {
            PrimeFaces.current().executeScript("PF('progressDialog')");
        }
    }

    private void processChildren() {
        // set parent relations between main process and its imported child processes!
        try {
            ImportService.processProcessChildren(getMainProcess(), childProcesses, rulesetManagement,
                    acquisitionStage, priorityList);
        } catch (DataException | InvalidMetadataValueException | NoSuchMetadataFieldException
                | ProcessGenerationException | IOException e) {
            Helper.setErrorMessage("Unable to attach child documents to process: " + e.getMessage());
        }
    }

    private void processAncestors() throws ProcessGenerationException {
        for (TempProcess tempProcess : this.processes) {
            Process process = tempProcess.getProcess();
            // set parent relations between all consecutive process pairs
            int index = processes.indexOf(tempProcess);
            if (index < processes.size() - 1) {
                ProcessService.setParentRelations(processes.get(index + 1).getProcess(), process);
            }
            if (Objects.nonNull(tempProcess.getMetadataNodes())) {
                try {
                    tempProcess.getProcessMetadata().preserve();
                    ImportService.processTempProcess(tempProcess, rulesetManagement, acquisitionStage, priorityList, null);
                } catch (InvalidMetadataValueException | NoSuchMetadataFieldException e) {
                    throw new ProcessGenerationException("Error creating process hierarchy: invalid metadata found!");
                } catch (RulesetNotFoundException e) {
                    throw new ProcessGenerationException(
                            "Ruleset not found:" + tempProcess.getProcess().getRuleset().getTitle());
                } catch (IOException e) {
                    throw new ProcessGenerationException("Error reading Ruleset: " + tempProcess.getProcess().getRuleset().getTitle());
                }
            }
        }
    }

    private void saveProcessHierarchyMetadata() {
        // save ancestor processes meta.xml files
        for (TempProcess tempProcess : this.processes) {
            if (this.processes.indexOf(tempProcess) == 0) {
                tempProcess.getProcessMetadata().preserve();
            }
            saveTempProcessMetadata(tempProcess);
        }
        // save child processes meta.xml files
        for (TempProcess tempProcess : this.childProcesses) {
            saveTempProcessMetadata(tempProcess);
        }
    }

    private void saveTempProcessMetadata(TempProcess tempProcess) {
        try (OutputStream out = ServiceManager.getFileService()
                .write(ServiceManager.getProcessService().getMetadataFileUri(tempProcess.getProcess()))) {
            tempProcess.getWorkpiece().setId(tempProcess.getProcess().getId().toString());
            ServiceManager.getMetsService().save(tempProcess.getWorkpiece(), out);
        } catch (IOException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
        }
    }

    private boolean createProcessesLocation(List<TempProcess> processes) {
        for (TempProcess tempProcess : processes) {
            if (processes.indexOf(tempProcess) > 0 && Objects.isNull(tempProcess.getMetadataNodes())) {
                // skip creating directories for processes that already exist!
                continue;
            }
            try {
                URI processBaseUri = ServiceManager.getFileService().createProcessLocation(tempProcess.getProcess());
                tempProcess.getProcess().setProcessBaseUri(processBaseUri);
            } catch (IOException | CommandException e) {
                Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
                try {
                    ServiceManager.getProcessService().remove(tempProcess.getProcess());
                } catch (DataException ex) {
                    Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
                }
                return false;
            }
        }
        return true;
    }

    public int getProgress() {
        return this.progress;
    }

    /**
     * Return referring view.
     *
     * @return referring view
     */
    public String getReferringView() {
        return this.referringView;
    }

    /**
     * Check and return whether the given ProcessDetail 'processDetail' is contained in the current list of addable
     * metadata types in the addDocStrucTypeDialog.
     *
     * @param treeNode treeNode to be added
     * @return whether the given ProcessDetail can be added or not
     */
    public boolean canBeAdded(TreeNode treeNode) throws InvalidMetadataValueException {
        if (Objects.isNull(treeNode.getParent().getParent())) {
            if (Objects.nonNull(currentProcess.getProcessMetadata().getSelectedMetadataTreeNode())
                    || Objects.isNull(addMetadataDialog.getAddableMetadata())) {
                this.addMetadataDialog.prepareAddableMetadataForStructure();
            }
        } else if (!Objects.equals(currentProcess.getProcessMetadata().getSelectedMetadataTreeNode(),
                treeNode.getParent()) || Objects.isNull(addMetadataDialog.getAddableMetadata())) {
            prepareAddableMetadataForGroup(treeNode.getParent());
        }
        if (Objects.nonNull(addMetadataDialog.getAddableMetadata())) {
            return addMetadataDialog.getAddableMetadata()
                    .stream()
                    .map(SelectItem::getValue)
                    .collect(Collectors.toList())
                    .contains(((ProcessDetail) treeNode.getData()).getMetadataID());
        }
        return false;
    }

    @Override
    public boolean canBeDeleted(ProcessDetail processDetail) {
        return processDetail.getOccurrences() > 1 && processDetail.getOccurrences() > processDetail.getMinOcc()
                || (!processDetail.isRequired() && !this.rulesetManagement.isAlwaysShowingForKey(processDetail.getMetadataID()));
    }

    /**
     * Check and return whether given TreeNode contains ProcessFieldedMetadata and if any further metadata can
     * be added to it or not.
     *
     * @param metadataNode TreeNode for which the check is performed
     * @return whether given TreeNode contains ProcessFieldedMetadata and if any further metadata can be added to it
     */
    public boolean metadataAddableToGroup(TreeNode metadataNode) {
        if (metadataNode.getData() instanceof ProcessFieldedMetadata) {
            return !(DataEditorService.getAddableMetadataForGroup(getMainProcess().getRuleset(), metadataNode).isEmpty());
        }
        return false;
    }

    /**
     * Prepare addable metadata for metadata group.
     * @param treeNode metadataGroup treeNode
     */
    public void prepareAddableMetadataForGroup(TreeNode treeNode) {
        addMetadataDialog.prepareAddableMetadataForGroup(getMainProcess().getRuleset(), treeNode);
    }

    /**
     * Get process ancestors.
     *
     * @return process ancestors
     */
    public List<TempProcess> getProcessAncestors() {
        return this.processes;
    }

    /**
     * Get process children.
     *
     * @return process children
     */
    public List<TempProcess> getProcessChildren() {
        return this.childProcesses;
    }

    /**
     * Get value of metadata configured to hold the catalog ID, e.g. "identifierMetadata"
     * (see OPACConfig.getIdentifierMetadata for details).
     *
     * @param tempProcess TempProcess whose ID metadata is returned
     * @return ID metadata
     */
    public String getCatalogId(TempProcess tempProcess) {
        if (Objects.nonNull(tempProcess) && Objects.nonNull(identifierMetadata)) {
            return tempProcess.getCatalogId(identifierMetadata);
        }
        return " - ";
    }

    /**
     * Fill metadata fields in metadata tab with metadata values of given temp process on successful import.
     * @param tempProcess TempProcess for which metadata is displayed
     */
    public void fillCreateProcessForm(TempProcess tempProcess)
            throws ProcessGenerationException, IOException {
        if (Objects.nonNull(tempProcess)) {
            currentProcess = tempProcess;
            if (Objects.nonNull(tempProcess.getWorkpiece())
                    && Objects.nonNull(tempProcess.getWorkpiece().getLogicalStructure())
                    && Objects.nonNull(tempProcess.getWorkpiece().getLogicalStructure().getType())) {
                tempProcess.verifyDocType();
                processDataTab.setDocType(tempProcess.getWorkpiece().getLogicalStructure().getType());
                processDataTab.updateProcessMetadata();
            }
        }
    }

    /**
     * Get currentProcess.
     *
     * @return value of currentProcess
     */
    public TempProcess getCurrentProcess() {
        return currentProcess;
    }

    /**
     * Set currentProcess.
     *
     * @param currentProcess as org.kitodo.production.helper.TempProcess
     */
    public void setCurrentProcess(TempProcess currentProcess) {
        this.currentProcess = currentProcess;
    }
}
