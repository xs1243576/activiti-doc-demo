package com.xs.activiti;

import com.sun.org.apache.xpath.internal.objects.XNull;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.DefaultTaskFormHandler;
import org.activiti.engine.impl.form.FormPropertyHandler;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
  public static FormService formService = null;

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
    if (formService == null) {
      formService = processEngine.getFormService();
    }
  }

  @Test
  void method1() {
//    init();
    // 流程部署
    Deployment deployment = repositoryService.createDeployment().addClasspathResource("xs_doc_approve_form20.bpmn")
      .name("xs_doc_approve")
      .category("")
      .deploy();
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
      .deploymentId(deployment.getId()).singleResult();
    System.out.println("流程名称 ： [" + processDefinition.getName() + "]， 流程ID ： ["
      + processDefinition.getId() + "], 流程KEY : " + processDefinition.getKey());

  }

  @Test
  void method111() {
//    init();
    // 流程部署
    Deployment deployment = repositoryService.createDeployment().addClasspathResource("parallel_demo.bpmn")
      .name("parallel_demo")
      .category("")
      .deploy();
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
      .deploymentId(deployment.getId()).singleResult();
    System.out.println("流程名称 ： [" + processDefinition.getName() + "]， 流程ID ： ["
      + processDefinition.getId() + "], 流程KEY : " + processDefinition.getKey());

  }

  @Test
  void method1111() {
//    init();
    // 流程部署
    Deployment deployment = repositoryService.createDeployment().addClasspathResource("qingjia1.bpmn")
      .name("qingjia1")
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
    repositoryService.deleteDeployment("52501");
//    List<String> deploymentList =repositoryService.getDeploymentResourceNames("");
  }

  @Test
  void method3() {
    // 展示流程定义
    ProcessDefinition processDefinition1 = repositoryService.createProcessDefinitionQuery()
      .deploymentId("120001").singleResult();//1
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
//    Log.info("=============删除流程实例============");
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
//          Log.info(msg);
          throw new ActivitiException(msg);
        }
        historyService.deleteHistoricProcessInstance(processInstanceId);
        return;
      }
      runtimeService.deleteProcessInstance(processInstanceId, "删除原因");
    }
  }

  @Test
  void method55() {
    //     查询所有的流程实例
    List<ProcessInstance> processInstances1 = runtimeService.createProcessInstanceQuery().list();
    for (int i = 0; i < processInstances1.size(); i++) {
      System.out.println(processInstances1.get(i).getId());

      List<Task> taskList9 = taskService.createTaskQuery().processInstanceId(processInstances1.get(i).getId()).list();
//    System.out.println("taskList9 = " + taskList9);
      for (int j = 0; j < taskList9.size(); j++) {
        Task task = taskList9.get(j);
        System.out.println("--" + task.getId());
        System.out.println("--" + task.getName());
        System.out.println("--" + task.getAssignee());
        System.out.println("--" + task.getProcessInstanceId());
      }
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
//    System.out.println("taskList9 = " + taskList9);
    for (int i = 0; i < taskList9.size(); i++) {
      Task task = taskList9.get(i);
      System.out.println("--" + task.getId());
      System.out.println("--" + task.getName());
      System.out.println("--" + task.getAssignee());
      System.out.println("--" + task.getProcessInstanceId());
      System.out.println("--" + task.getProcessDefinitionId());
    }
  }

  @Test
  void method77() {
    List<Task> taskList9 = taskService.createTaskQuery().taskCandidateOrAssigned("").list();
//    System.out.println("taskList9 = " + taskList9);
    for (int i = 0; i < taskList9.size(); i++) {
      Task task = taskList9.get(i);
      System.out.println("--" + task.getId());
      System.out.println("--" + task.getName());
      System.out.println("--" + task.getAssignee());
      System.out.println("--" + task.getProcessInstanceId());
    }
  }

  @Test
  void method777() {
    List<String> idList = new ArrayList<String>();
    idList.add("110001");
//    processInstanceId("110001").list();

//    List<Task> taskList9 = taskService.createTaskQuery().processDefinitionId("xs_doc_approve_key:1:52504").list();
//    List<Task> taskList9 = taskService.createTaskQuery().processDefinitionId("").list();
    List<Task> taskList9 = taskService.createTaskQuery().processInstanceId("110001").list();
//    System.out.println("taskList9 = " + taskList9);
    for (int i = 0; i < taskList9.size(); i++) {
      Task task = taskList9.get(i);
      System.out.println("--" + task.getId());
      System.out.println("--" + task.getName());
      System.out.println("--" + task.getAssignee());
      System.out.println("--" + task.getProcessInstanceId());
    }
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
    taskService.complete(taskList8.get(0).getId(), vars);

    List<Task> taskList9 = taskService.createTaskQuery().list();
    System.out.println("taskList9 = " + taskList9);
  }

  // 重新设置流程变量
  @Test
  void method11() {
    Map vars = new HashMap();

    List<String> assigneeList = new ArrayList<String>();
    assigneeList.add("shenhe1");
    assigneeList.add("shenhe2");
    assigneeList.add("shenhe3");
    assigneeList.add("shenhe4");
    vars.put("handlerlist", assigneeList);
    runtimeService.setVariables("95001", vars);

    System.out.println("--" + runtimeService.getVariableInstance("95001", "handlerlist").toString());
    System.out.println("--" + runtimeService.getVariables("95001").toString());

  }

  // 查询表单信息
  @Test
  void method12() {
//    Deployment deployment = repositoryService.createDeployment().addClasspathResource("xs_doc_approve_form20.bpmn")
//      .name("xs_doc_approve")
//      .category("")
//      .deploy();
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
      .deploymentId("120001").singleResult();

    String procDefId = processDefinition.getId();
    System.out.println("--" + procDefId);
    StartFormData startFormData = formService.getStartFormData(procDefId);
    List<FormProperty> formProperties = startFormData.getFormProperties();

//    formService.getTaskFormData(task.getId());

    for (FormProperty formProperty : formProperties) {
//打印表单属性
      System.out.println(formProperty.getName());
      System.out.println(formProperty.getId());
      System.out.println(formProperty.getType().getName());
    }
//提交表单（带参数）
//    formService.submitTaskFormData(task.getId(), map);

    ProcessDefinition pd = startFormData.getProcessDefinition();
    System.out.println("--" + formProperties.toString());
    System.out.println("--" + pd.toString());
  }

  // 查询任务节点的表单信息
  @Test
  void method13() {
    // 需要运行时的id才可以查询到结果
    String taskId = "usertask";
    Task task = processEngine.getTaskService().createTaskQuery() // 创建任务查询
      .taskId(taskId) // 根据任务id查询
      .singleResult();
    String processDefinitionId = task.getProcessDefinitionId(); // 获取流程定义id

    ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) processEngine.getRepositoryService()
      .getProcessDefinition(processDefinitionId);

    ActivityImpl activityImpl = processDefinitionEntity.findActivity(task.getTaskDefinitionKey()); // 根据活动id获取活动实例
    TaskDefinition taskDef = (TaskDefinition) activityImpl.getProperties().get("taskDefinition");

    DefaultTaskFormHandler fh = (DefaultTaskFormHandler) taskDef.getTaskFormHandler();

    List<FormPropertyHandler> flList = fh == null ? null : (List<FormPropertyHandler>) fh.getFormPropertyHandlers();

    String formKey = "";

    if (flList != null && flList.size() > 0) {
      formKey = ((FormPropertyHandler) flList.get(0)).getId();
    }


  }

}
