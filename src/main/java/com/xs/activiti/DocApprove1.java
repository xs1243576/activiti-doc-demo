package com.xs.activiti;

import com.oracle.tools.packager.Log;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 完整的公文审批流程
public class DocApprove1 {

  public static void main(String[] args) {
    ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
      .setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useSSL=false").setJdbcUsername("root").setJdbcPassword("123456")
      .setJdbcDriver("com.mysql.cj.jdbc.Driver")//com.mysql.cj.jdbc.Driver  com.mysql.jdbc.Driver
      .setJobExecutorActivate(true)
      .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    ProcessEngine processEngine = cfg.buildProcessEngine();
    String pName = processEngine.getName();
    String ver = ProcessEngine.VERSION;
    System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");

    RepositoryService repositoryService = processEngine.getRepositoryService();

    // 设置流程创建人
    IdentityService identityService = processEngine.getIdentityService();
    identityService.setAuthenticatedUserId("createUserId");

    // 发文拟稿 启动流程
    RuntimeService runtimeService = processEngine.getRuntimeService();
    Map vars = new HashMap();
    vars.put("submiter", "submiter1");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("xs_doc_approve_key", "Key001", vars);

    System.out.println("流程实例ID = " + processInstance.getId()); // 5001
    System.out.println("正在活动的流程节点ID = " + processInstance.getActivityId()); // usertask
    System.out.println("流程定义ID = " + processInstance.getProcessDefinitionId()); // xs_doc_approve_key:1:4

    // 拟稿任务查询
    TaskService taskService = processEngine.getTaskService();
    List<Task> taskList1 = taskService.createTaskQuery().taskAssignee("submiter1").orderByTaskCreateTime().desc().list();
    System.out.println("taskList1 = " + taskList1); //taskList1 = [Task[id=5005, name=拟稿]]

    // 添加审核人
    vars.clear();
//    vars.put("pass", "yes");
    // 并行
    vars.put("type", "parallel");
    // 串行
//    vars.put("type", "serial");
    // 设置审核人
    List<String> assigneeList = new ArrayList<String>();
    assigneeList.add("shenhe1");
    assigneeList.add("shenhe2");
    assigneeList.add("shenhe3");
    vars.put("handlerlist", assigneeList);

    Task task1 = taskList1.get(0);
    taskService.complete(task1.getId(), vars);

    // 审核1
    List<Task> taskList2 = taskService.createTaskQuery().taskAssignee("shenhe1").orderByTaskCreateTime().desc().list();
    System.out.println("taskList2 = " + taskList2);
    taskService.complete(taskList2.get(0).getId());

    // 审核2
    List<Task> taskList3 = taskService.createTaskQuery().taskAssignee("shenhe2").orderByTaskCreateTime().desc().list();
    System.out.println("taskList3 = " + taskList3);
    taskService.complete(taskList3.get(0).getId());

    // 审核3
    List<Task> taskList4 = taskService.createTaskQuery().taskAssignee("shenhe3").orderByTaskCreateTime().desc().list();
    System.out.println("taskList4 = " + taskList4);
//    taskService.complete(taskList4.get(0).getId());

    vars.clear();
    vars.put("handler2", "approver");
    vars.put("confirmPass", true);
    taskService.complete(taskList4.get(0).getId(), vars);

    List<Task> taskList5 = taskService.createTaskQuery().taskAssignee("approver").orderByTaskCreateTime().desc().list();
    System.out.println("taskList5 = " + taskList5);

    vars.clear();
    vars.put("qianfa", true);
    vars.put("handler3", "guidang");
    vars.put("back", false);
    taskService.complete(taskList5.get(0).getId(), vars);

    List<Task> taskList6 = taskService.createTaskQuery().taskAssignee("guidang").orderByTaskCreateTime().desc().list();
    System.out.println("taskList6 = " + taskList6);
    taskService.complete(taskList6.get(0).getId());

    List<Task> taskList7 = taskService.createTaskQuery().list();
    System.out.println("taskList7 = " + taskList7);


  }

}


