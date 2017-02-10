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

package org.kitodo;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.kitodo.data.database.beans.*;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.database.helper.enums.TaskEditType;
import org.kitodo.data.database.helper.enums.TaskStatus;
import org.kitodo.services.*;

import static org.kitodo.data.database.beans.Batch.Type.*;

/**
 * Insert data to test database.
 */
public class MockDatabase {

    public static void insertBatches() throws DAOException {
        BatchService batchService = new BatchService();

        Batch firstBatch = new Batch();
        firstBatch.setTitle("First batch");
        firstBatch.setType(LOGISTIC);
        batchService.save(firstBatch);

        Batch secondBatch = new Batch();
        secondBatch.setTitle("Second batch");
        secondBatch.setType(LOGISTIC);
        batchService.save(secondBatch);

        Batch thirdBatch = new Batch();
        thirdBatch.setTitle("Third batch");
        thirdBatch.setType(NEWSPAPER);
        batchService.save(thirdBatch);

        Batch fourthBatch = new Batch();
        fourthBatch.setType(SERIAL);
        batchService.save(fourthBatch);
    }

    public static void insertDockets() throws DAOException {
        DocketService docketService = new DocketService();

        Docket firstDocket = new Docket();
        firstDocket.setName("default");
        firstDocket.setFile("docket.xsl");
        docketService.save(firstDocket);

        Docket secondDocket = new Docket();
        secondDocket.setName("second");
        secondDocket.setFile("docket.xsl");
        docketService.save(secondDocket);
    }

    public static void insertLdapGroups() throws DAOException {
        LdapGroupService ldapGroupService = new LdapGroupService();

        LdapGroup firstLdapGroup = new LdapGroup();
        firstLdapGroup.setTitle("LG");
        firstLdapGroup.setHomeDirectory("..//test_directory/");
        firstLdapGroup.setDescription("Test LDAP group");
        firstLdapGroup.setDisplayName("Name");
        ldapGroupService.save(firstLdapGroup);
    }

    public static void insertProcesses() throws DAOException {
        BatchService batchService = new BatchService();
        DocketService docketService = new DocketService();
        ProcessService processService = new ProcessService();
        ProjectService projectService = new ProjectService();
        RulesetService rulesetService = new RulesetService();

        Process firstProcess = new Process();
        firstProcess.setTitle("First process");
        firstProcess.setOutputName("Test");
        firstProcess.setWikiField("wiki");
        LocalDate localDate = new LocalDate(2016,10,20);
        firstProcess.setCreationDate(localDate.toDate());

        List<Batch> batches = new ArrayList<>();
        Batch firstBatch = batchService.find(1);
        Batch secondBatch = batchService.find(3);
        List<Process> processes = new ArrayList<>();
        processes.add(firstProcess);
        firstBatch.setProcesses(processes);
        secondBatch.setProcesses(processes);
        batches.add(firstBatch);
        batches.add(secondBatch);
        firstProcess.setBatches(batches);

        firstProcess.setDocket(docketService.find(1));
        firstProcess.setProject(projectService.find(1));
        firstProcess.setRuleset(rulesetService.find(1));
        processService.save(firstProcess);

        Process secondProcess = new Process();
        secondProcess.setTitle("Second process");
        secondProcess.setOutputName("Testowy");
        secondProcess.setWikiField("field");
        localDate = new LocalDate(2017,1,20);
        secondProcess.setCreationDate(localDate.toDate());
        secondProcess.setDocket(docketService.find(1));
        secondProcess.setProject(projectService.find(1));
        secondProcess.setRuleset(rulesetService.find(1));
        processService.save(secondProcess);
    }

    public static void insertProjects() throws DAOException {
        ProjectService projectService = new ProjectService();

        Project firstProject = new Project();
        firstProject.setTitle("First project");
        firstProject.setUseDmsImport(true);
        LocalDate localDate = new LocalDate(2016,10,20);
        firstProject.setStartDate(localDate.toDate());
        localDate = new LocalDate(2017,10,20);
        firstProject.setEndDate(localDate.toDate());
        firstProject.setNumberOfPages(30);
        firstProject.setNumberOfVolumes(2);
        projectService.save(firstProject);

        Project secondProject = new Project();
        secondProject.setTitle("Second project");
        secondProject.setUseDmsImport(false);
        localDate = new LocalDate(2016,11,10);
        secondProject.setStartDate(localDate.toDate());
        localDate = new LocalDate(2017,9,15);
        secondProject.setEndDate(localDate.toDate());
        secondProject.setNumberOfPages(80);
        secondProject.setNumberOfVolumes(4);
        projectService.save(secondProject);

        Project thirdProject = new Project();
        thirdProject.setTitle("Archived project");
        localDate = new LocalDate(2014,11,10);
        thirdProject.setStartDate(localDate.toDate());
        localDate = new LocalDate(2016,9,15);
        thirdProject.setEndDate(localDate.toDate());
        thirdProject.setNumberOfPages(160);
        thirdProject.setNumberOfVolumes(5);
        thirdProject.setProjectIsArchived(true);
        projectService.save(thirdProject);
    }

    public static void insertRulesets() throws DAOException {
        RulesetService rulesetService = new RulesetService();

        Ruleset firstRuleset = new Ruleset();
        firstRuleset.setTitle("SLUBDD");
        firstRuleset.setFile("ruleset_slubdd.xml");
        firstRuleset.setOrderMetadataByRuleset(false);
        rulesetService.save(firstRuleset);

        Ruleset secondRuleset = new Ruleset();
        secondRuleset.setTitle("SLUBHH");
        secondRuleset.setFile("ruleset_slubhh.xml");
        secondRuleset.setOrderMetadataByRuleset(false);
        rulesetService.save(secondRuleset);
    }

    public static void insertTasks() throws DAOException {
        ProcessService processService = new ProcessService();
        TaskService taskService = new TaskService();
        UserService userService = new UserService();
        UserGroupService userGroupService = new UserGroupService();

        Task firstTask = new Task();
        firstTask.setTitle("Testing");
        firstTask.setPriority(1);
        firstTask.setOrdering(1);
        firstTask.setEditTypeEnum(TaskEditType.ADMIN);
        LocalDate localDate = new LocalDate(2016,10,20);
        firstTask.setProcessingBegin(localDate.toDate());
        localDate = new LocalDate(2016,12,24);
        firstTask.setProcessingEnd(localDate.toDate());
        firstTask.setProcessingUser(userService.find(1));
        firstTask.setProcessingStatusEnum(TaskStatus.OPEN);
        firstTask.setProcess(processService.find(1));
        firstTask.setUsers(userService.findAll());
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(userGroupService.find(1));
        firstTask.setUserGroups(userGroups);
        taskService.save(firstTask);

        Task secondTask = new Task();
        secondTask.setTitle("Blocking");
        secondTask = taskService.setCorrectionStep(secondTask);
        secondTask.setOrdering(2);
        secondTask.setEditTypeEnum(TaskEditType.MANUAL_SINGLE);
        localDate = new LocalDate(2016,9,25);
        secondTask.setProcessingBegin(localDate.toDate());
        secondTask.setProcessingUser(userService.find(3));
        secondTask.setProcessingStatusEnum(TaskStatus.OPEN);
        secondTask.setProcess(processService.find(2));
        secondTask.setUsers(userService.findAll());
        secondTask.setScriptName1("scriptName");
        secondTask.setTypeAutomaticScriptPath("../type/automatic/script/path");
        secondTask.setScriptName2("secondScriptName");
        secondTask.setTypeAutomaticScriptPath2("../type/automatic/script/path2");
        secondTask.setScriptName3("thirdScriptName");
        secondTask.setTypeAutomaticScriptPath3("../type/automatic/script/path3");
        taskService.save(secondTask);

        Task thirdTask = new Task();
        thirdTask.setTitle("Testing and Blocking");
        thirdTask.setOrdering(3);
        thirdTask.setEditTypeEnum(TaskEditType.MANUAL_SINGLE);
        localDate = new LocalDate(2017,1,25);
        thirdTask.setProcessingBegin(localDate.toDate());
        thirdTask.setProcessingStatusEnum(TaskStatus.LOCKED);
        thirdTask.setProcess(processService.find(2));
        thirdTask.setUsers(userService.findAll());
        taskService.save(thirdTask);

        Task fourthTask = new Task();
        fourthTask.setTitle("Progress");
        fourthTask.setOrdering(4);
        fourthTask.setEditTypeEnum(TaskEditType.MANUAL_SINGLE);
        localDate = new LocalDate(2017,1,29);
        fourthTask.setProcessingBegin(localDate.toDate());
        fourthTask.setProcessingStatusEnum(TaskStatus.INWORK);
        fourthTask.setProcessingUser(userService.find(2));
        fourthTask.setProcess(processService.find(2));
        fourthTask.setUsers(userService.findAll());
        taskService.save(fourthTask);
    }

    public static void insertTemplates() throws DAOException {

    }

    public static void insertUsers() throws DAOException {
        LdapGroupService ldapGroupService = new LdapGroupService();
        UserService userService = new UserService();

        User firstUser = new User();
        firstUser.setName("Jan");
        firstUser.setSurname("Kowalski");
        firstUser.setLogin("kowal");
        firstUser.setPassword("test");
        firstUser.setLdapLogin("kowalLDP");
        firstUser.setTableSize(20);
        firstUser.setCss("/css/fancy.css");
        userService.save(firstUser);

        User secondUser = new User();
        secondUser.setName("Adam");
        secondUser.setSurname("Nowak");
        secondUser.setLogin("nowak");
        secondUser.setLdapLogin("nowakLDP");
        secondUser.setSessionTimeout(9000);
        secondUser.setLdapGroup(ldapGroupService.find(1));
        userService.save(secondUser);

        User thirdUser = new User();
        thirdUser.setName("Anna");
        thirdUser.setSurname("Dora");
        thirdUser.setLogin("dora");
        thirdUser.setLdapLogin("doraLDP");
        thirdUser.setActive(false);
        userService.save(thirdUser);
    }

    public static void insertUserGroups() throws DAOException {
        UserService userService = new UserService();
        UserGroupService userGroupService = new UserGroupService();

        UserGroup firstUserGroup = new UserGroup();
        firstUserGroup.setTitle("Admin");
        firstUserGroup.setPermission(1);
        List<User> users = new ArrayList<>();
        User user = userService.find(1);
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(firstUserGroup);
        user.setUserGroups(userGroups);
        users.add(userService.find(1));
        firstUserGroup.setUsers(users);
        userGroupService.save(firstUserGroup);

        UserGroup secondUserGroup = new UserGroup();
        secondUserGroup.setTitle("Random");
        secondUserGroup.setPermission(2);
        userGroupService.save(secondUserGroup);
    }

    public static void insertWorkpieces() throws DAOException {

    }
}
