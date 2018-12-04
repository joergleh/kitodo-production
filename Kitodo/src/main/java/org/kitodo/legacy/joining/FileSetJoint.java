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

package org.kitodo.legacy.joining;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.ugh.ContentFileInterface;
import org.kitodo.api.ugh.DigitalDocumentInterface;
import org.kitodo.api.ugh.DocStructInterface;
import org.kitodo.api.ugh.DocStructTypeInterface;
import org.kitodo.api.ugh.FileSetInterface;
import org.kitodo.api.ugh.MetadataGroupInterface;
import org.kitodo.api.ugh.MetadataGroupTypeInterface;
import org.kitodo.api.ugh.MetadataInterface;
import org.kitodo.api.ugh.MetadataTypeInterface;
import org.kitodo.api.ugh.PersonInterface;
import org.kitodo.api.ugh.PrefsInterface;
import org.kitodo.api.ugh.ReferenceInterface;
import org.kitodo.api.ugh.VirtualFileGroupInterface;
import org.kitodo.api.ugh.exceptions.ContentFileNotLinkedException;
import org.kitodo.api.ugh.exceptions.MetadataTypeNotAllowedException;
import org.kitodo.api.ugh.exceptions.TypeNotAllowedAsChildException;
import org.kitodo.api.ugh.exceptions.TypeNotAllowedForParentException;

public class FileSetJoint implements FileSetInterface, DocStructInterface {
    private static final Logger logger = LogManager.getLogger(FileSetJoint.class);

    // FileSetInterface methods ==============================================

    @Override
    public void addFile(ContentFileInterface contentFile) {
        logger.log(Level.TRACE, "addFile(contentFile: {})", contentFile);
        // TODO Auto-generated method stub
    }

    @Override
    public void addVirtualFileGroup(VirtualFileGroupInterface virtualFileGroup) {
        logger.log(Level.TRACE, "addVirtualFileGroup(virtualFileGroup: virtualFileGroup)", virtualFileGroup);
        // TODO Auto-generated method stub
    }

    @Override
    public Iterable<ContentFileInterface> getAllFiles() {
        logger.log(Level.TRACE, "getAllFiles()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public void removeFile(ContentFileInterface contentFile) {
        logger.log(Level.TRACE, "removeFile(contentFile: {})", contentFile);
        // TODO Auto-generated method stub
    }

    // DocStructInterface methods ============================================

    @Override
    public void addChild(DocStructInterface child) throws TypeNotAllowedAsChildException {
        logger.log(Level.TRACE, "addChild(child: {})", child);
        // TODO Auto-generated method stub
    }

    @Override
    public void addChild(Integer index, DocStructInterface child) throws TypeNotAllowedAsChildException {
        logger.log(Level.TRACE, "addChild(index: {}, child: {})", index, child);
        // TODO Auto-generated method stub
    }

    @Override
    public void addContentFile(ContentFileInterface contentFile) {
        logger.log(Level.TRACE, "addContentFile(contentFile: {})", contentFile);
        // TODO Auto-generated method stub
    }

    @Override
    public void addMetadata(MetadataInterface metadata) throws MetadataTypeNotAllowedException {
        logger.log(Level.TRACE, "addMetadata(metadata: {})", metadata);
        // TODO Auto-generated method stub
    }

    @Override
    public DocStructInterface addMetadata(String metadataType, String value) throws MetadataTypeNotAllowedException {
        logger.log(Level.TRACE, "addMetadata(metadataType: {}, value: {})", metadataType, value);
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public void addMetadataGroup(MetadataGroupInterface metadataGroup) throws MetadataTypeNotAllowedException {
        logger.log(Level.TRACE, "addMetadataGroup(metadataGroup: {})", metadataGroup);
        // TODO Auto-generated method stub
    }

    @Override
    public void addPerson(PersonInterface person) throws MetadataTypeNotAllowedException {
        logger.log(Level.TRACE, "addPerson(person: {})", person);
        // TODO Auto-generated method stub
    }

    @Override
    public ReferenceInterface addReferenceTo(DocStructInterface docStruct, String type) {
        logger.log(Level.TRACE, "()");
        // TODO Auto-generated method stub
        return new ReferenceJoint();
    }

    @Override
    public DocStructInterface copy(boolean copyMetaData, Boolean recursive) {
        logger.log(Level.TRACE, "()");
        // TODO Auto-generated method stub
        return new FileSetJoint();
    }

    @Override
    public DocStructInterface createChild(String docStructType, DigitalDocumentInterface digitalDocument,
            PrefsInterface prefs) throws TypeNotAllowedAsChildException, TypeNotAllowedForParentException {

        logger.log(Level.TRACE, "createChild(docStructType: \"{}\", digitalDocument: {}, prefs: {})", docStructType,
            digitalDocument, prefs);
        // TODO Auto-generated method stub
        return new InnerPhysicalDocStructJoint(); // returns the child
    }

    @Override
    public void deleteUnusedPersonsAndMetadata() {
        logger.log(Level.TRACE, "deleteUnusedPersonsAndMetadata()");
        // TODO Auto-generated method stub
    }

    @Override
    public List<MetadataGroupTypeInterface> getAddableMetadataGroupTypes() {
        logger.log(Level.TRACE, "getAddableMetadataGroupTypes()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<MetadataTypeInterface> getAddableMetadataTypes() {
        logger.log(Level.TRACE, "getAddableMetadataTypes()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<DocStructInterface> getAllChildren() {
        // logger.log(Level.TRACE, "getAllChildren()");
        // Methode wird zwar aufgerufen, weil für logische und physiche die
        // gleiche Schleife benutzt wird, muss aber leer zurückkommen
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<DocStructInterface> getAllChildrenByTypeAndMetadataType(String docStructType, String metaDataType) {
        logger.log(Level.TRACE, "getAllChildrenByTypeAndMetadataType(docStructType: \"{}\", metaDataType: \"{}\")",
            docStructType, metaDataType);
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<ContentFileInterface> getAllContentFiles() {
        logger.log(Level.TRACE, "getAllContentFiles()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<ReferenceInterface> getAllFromReferences() {
        logger.log(Level.TRACE, "getAllFromReferences()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<MetadataInterface> getAllIdentifierMetadata() {
        logger.log(Level.TRACE, "getAllIdentifierMetadata()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<MetadataInterface> getAllMetadata() {
        logger.log(Level.TRACE, "getAllMetadata()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<? extends MetadataInterface> getAllMetadataByType(MetadataTypeInterface metadataType) {
        logger.log(Level.TRACE, "getAllMetadataByType(metadataType: {})", metadataType);
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<MetadataGroupInterface> getAllMetadataGroups() {
        logger.log(Level.TRACE, "getAllMetadataGroups()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<PersonInterface> getAllPersons() {
        logger.log(Level.TRACE, "getAllPersons()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<PersonInterface> getAllPersonsByType(MetadataTypeInterface metadataType) {
        logger.log(Level.TRACE, "getAllPersonsByType(metadataType: {})", metadataType);
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<ReferenceInterface> getAllReferences(String direction) {
        // logger.log(Level.TRACE, "getAllReferences(direction: \"{}\")",
        // direction);
        // Methode wird zwar aufgerufen, weil für logische und physiche die
        // gleiche Schleife benutzt wird, muss aber leer zurückkommen
        return Collections.emptyList();
    }

    @Override
    public Collection<ReferenceInterface> getAllToReferences() {
        logger.log(Level.TRACE, "getAllToReferences()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public Collection<ReferenceInterface> getAllToReferences(String type) {
        logger.log(Level.TRACE, "getAllToReferences(type: \"{}\")", type);
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public Object getAllVisibleMetadata() {
        logger.log(Level.TRACE, "getAllVisibleMetadata()");
        // TODO Auto-generated method stub
        return null; // null -> false, new Object() -> true
    }

    @Override
    public String getAnchorClass() {
        logger.log(Level.TRACE, "getAnchorClass()");
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public DocStructInterface getChild(String type, String identifierField, String identifier) {
        logger.log(Level.TRACE, "getChild(type: \"{}\", identifierField: \"{}\", identifier: \"{}\")");
        // TODO Auto-generated method stub
        return new InnerPhysicalDocStructJoint();
    }

    @Override
    public List<MetadataTypeInterface> getDisplayMetadataTypes() {
        logger.log(Level.TRACE, "getDisplayMetadataTypes()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public String getImageName() {
        logger.log(Level.TRACE, "getImageName()");
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public DocStructInterface getNextChild(DocStructInterface predecessor) {
        logger.log(Level.TRACE, "getNextChild(predecessor: {})", predecessor);
        // TODO Auto-generated method stub
        return new InnerPhysicalDocStructJoint();
    }

    @Override
    public DocStructInterface getParent() {
        logger.log(Level.TRACE, "getParent()");
        // TODO Auto-generated method stub
        return null; // hier gibt’s kein parent
    }

    @Override
    public List<MetadataTypeInterface> getPossibleMetadataTypes() {
        logger.log(Level.TRACE, "getPossibleMetadataTypes()");
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public DocStructTypeInterface getDocStructType() {
        logger.log(Level.TRACE, "getDocStructType()");
        // TODO Auto-generated method stub
        return PageType.INSTANCE;
    }

    /**
     * This method is not part of the interface, but the JSF code digs in the
     * depths of the UGH and uses it on the guts.
     * 
     * @return Method delegated to {@link #getDocStructType()}
     */
    public DocStructTypeInterface getType() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        logger.log(Level.WARN, "Method {}.{}() invokes {}.{}(), bypassing the interface!",
            stackTrace[1].getClassName(), stackTrace[1].getMethodName(), stackTrace[0].getClassName(),
            stackTrace[0].getMethodName());
        return getDocStructType();
    }

    @Override
    public boolean isDocStructTypeAllowedAsChild(DocStructTypeInterface type) {
        logger.log(Level.TRACE, "isDocStructTypeAllowedAsChild(type: {})", type);
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeChild(DocStructInterface docStruct) {
        logger.log(Level.TRACE, "removeChild(docStruct: {})", docStruct);
        // TODO Auto-generated method stub
    }

    @Override
    public void removeContentFile(ContentFileInterface contentFile) throws ContentFileNotLinkedException {
        logger.log(Level.TRACE, "removeContentFile(contentFile: {})", contentFile);
        // TODO Auto-generated method stub
    }

    @Override
    public void removeMetadata(MetadataInterface metaDatum) {
        logger.log(Level.TRACE, "removeMetadata(metaDatum: {})", metaDatum);
        // TODO Auto-generated method stub
    }

    @Override
    public void removeMetadataGroup(MetadataGroupInterface metadataGroup) {
        logger.log(Level.TRACE, "removeMetadataGroup(metadataGroup: {})");
        // TODO Auto-generated method stub
    }

    @Override
    public void removePerson(PersonInterface person) {
        logger.log(Level.TRACE, "removePerson(person: {})", person);
        // TODO Auto-generated method stub
    }

    @Override
    public void removeReferenceTo(DocStructInterface target) {
        logger.log(Level.TRACE, "removeReferenceTo(target: {})", target);
        // TODO Auto-generated method stub

    }

    @Override
    public void setImageName(String imageName) {
        logger.log(Level.TRACE, "setImageName(imageName: {})", imageName);
        // TODO Auto-generated method stub
    }

    @Override
    public void setType(DocStructTypeInterface docStructType) {
        logger.log(Level.TRACE, "setType(docStructType: {})");
        // TODO Auto-generated method stub
    }

}
