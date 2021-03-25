package com.xs.activiti;

import com.oracle.tools.packager.Log;
import com.sun.org.apache.xpath.internal.objects.XNull;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiTest {

  public static ProcessEngineConfiguration cfg = null;
  public static ProcessEngine processEngine = null;
  public static RepositoryService repositoryService = null;
  public static RuntimeService runtimeService = null;
  public static HistoryService historyService = null;
  public static TaskService taskService = null;

  public static void main(String[] args) {

  }

  @BeforeEach
  public void init() {
    if (cfg == null) {
      cfg = new StandaloneProcessEngineConfiguration()
        .setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useSSL=false").setJdbcUsername("root").setJdbcPassword("123456")
        .setJdbcDriver("com.mysql.cj.jdbc.Driver")//com.mysql.cj.jdbc.Driver  com.mysql.jdbc.Driver
        .setJobExecutorActivate(true)
        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    }
    if (processEngine == null) {
      processEngine = cfg.buildProcessEngine();
    }
    if (repositoryService == null) {
      repositoryService = processEngine.getRepositoryService();
    }
    if (runtimeService == null) {
      runtimeService = processEngine.getRuntimeService();
    }
    if (historyService == null) {
      historyService = processEngine.getHistoryService();
    }
    if (taskService == null) {
      taskService = processEngine.getTaskService();
    }
  }

  @Test
  void method1() {
//    init();
    // 流程部署
    Deployment deployment = repositoryService.createDeployment().addClasspathResource("xs_doc_approve20.bpmn")
      .name("xs_doc_approve")
      .category("")
      .deploy();
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
      .deploymentId(deployment.getId()).singleResult();
    System.out.println("流程名称 ： [" + processDefinition.getName() + "]， 流程ID ： ["
      + processDefinition.getId() + "], 流程KEY : " + processDefinition.getKey());

  }

  @Test
  void method2() {
    // 删除部署id
    repositoryService.deleteDeployment("35001");
//    List<String> deploymentList =repositoryService.getDeploymentResourceNames("");
  }

  @Test
  void method3() {
    // 展示流程定义
    ProcessDefinition processDefinition1 = repositoryService.createProcessDefinitionQuery()
      .deploymentId("52501").singleResult();//1
    System.out.println("流程名称 ： [" + processDefinition1.getName() + "]， 流程ID ： ["
      + processDefinition1.getId() + "], 流程KEY : " + processDefinition1.getKey());
  }

  @Test
  void method4() {
    // 查询所有的流程实例
    List<ProcessInstance> processInstances1 = runtimeService.createProcessInstanceQuery().list();
    for (int i = 0; i < processInstances1.size(); i++) {
      System.out.println("--" + processInstances1.get(i).getId());
    }
  }

  @Test
  void method5() {
    Log.info("=============删除流程实例============");
    //     查询所有的流程实例
//    RuntimeService runtimeService = processEngine.getRuntimeService();
    List<ProcessInstance> processInstances1 = runtimeService.createProcessInstanceQuery().list();
    for (int i = 0; i < processInstances1.size(); i++) {
      System.out.println(processInstances1.get(i).getId());
      String processInstanceId = processInstances1.get(i).getId();
      List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).list();
      if (processInstances.isEmpty()) {
        // 历史流程实例
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
          .processInstanceId(processInstanceId).list();
        if (historicProcessInstances.isEmpty()) {
          String msg = "No process instances with the ID: " + processInstanceId;
          Log.info(msg);
          throw new ActivitiException(msg);
        }
        historyService.deleteHistoricProcessInstance(processInstanceId);
        return;
      }
      runtimeService.deleteProcessInstance(processInstanceId, "删除原因");
    }
  }

  @Test
  void method6() {
    // 历史任务查询
    List<HistoricActivityInstance> historicActivityInstances = processEngine.getHistoryService()
      .createHistoricActivityInstanceQuery()
      .orderByHistoricActivityInstanceStartTime()
      .asc()
      .list();
    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
      System.out.println("任务ID:" + historicActivityInstance.getId());
      System.out.println("流程实例ID:" + historicActivityInstance.getProcessInstanceId());
      System.out.println("活动名称：" + historicActivityInstance.getActivityName());
      System.out.println("办理人：" + historicActivityInstance.getAssignee());
      System.out.println("开始时间：" + historicActivityInstance.getStartTime());
      System.out.println("结束时间：" + historicActivityInstance.getEndTime());
      System.out.println("===========================");
    }
  }

  @Test
  void method7() {
    List<Task> taskList9 = taskService.createTaskQuery().list();
    System.out.println("taskList9 = " + taskList9);
  }

  @Test
  void method8() {
    List<Task> taskList8 = taskService.createTaskQuery().taskAssignee("guidang").orderByTaskCreateTime().desc().list();
    System.out.println("taskList8 = " + taskList8);
  }

  @Test
  void method9() {
    List<Task> taskList7 = taskService.createTaskQuery().taskAssignee("approver").orderByTaskCreateTime().desc().list();
    System.out.println("taskList7 = " + taskList7);
    Map vars = new HashMap();
    vars.clear();
    vars.put("qianfa", true);
    vars.put("handler3", "guidang");
    vars.put("back", false);
    taskService.complete(taskList7.get(0).getId(), vars);
  }

  @Test
  void method10() {
    List<Task> taskList8 = taskService.createTaskQuery().taskAssignee("guidang").orderByTaskCreateTime().desc().list();
    System.out.println("taskList8 = " + taskList8);
    Map vars = new HashMap();
    vars.clear();
    vars.put("confirmPass", true);
    taskService.complete(taskList8.get(0).getId(),vars);

    List<Task> taskList9 = taskService.createTaskQuery().list();
    System.out.println("taskList9 = " + taskList9);
  }

}
