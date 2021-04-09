package com.xs.activiti;

import com.xs.activiti.listener.ExectuionListenerDemo;
import com.xs.activiti.listener.TaskListenerDemo;
import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QingJiaFormDemo {

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
    vars.put("handler1", "handler1");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia1_key", "Key001", vars);

    System.out.println("流程实例ID = " + processInstance.getId()); // 5001
    System.out.println("正在活动的流程节点ID = " + processInstance.getActivityId()); // usertask
    System.out.println("流程定义ID = " + processInstance.getProcessDefinitionId()); // xs_doc_approve_key:1:4

    // 拟稿任务查询
    TaskService taskService = processEngine.getTaskService();
    List<Task> taskList1 = taskService.createTaskQuery().taskAssignee("handler1").orderByTaskCreateTime().desc().list();
    System.out.println("taskList1 = " + taskList1); //taskList1 = [Task[id=5005, name=拟稿]]

    Task task1 = taskList1.get(0);

    FormService formService = processEngine.getFormService();
    Map<String, String> formProperties = new HashMap<String, String>();
    formProperties.put("fullname", "handler1");
    formProperties.put("days", "3");
    formService.saveFormData(task1.getId(), formProperties);

    List<FormProperty> formPropertyList1 = formService.getTaskFormData(task1.getId()).getFormProperties();
    for (int j = 0; j < formPropertyList1.size(); j++) {
      System.out.println("--task1formdata");
      System.out.println(formPropertyList1.get(j).toString());
    }

    vars.clear();
    vars.put("handler2", "handler2");
    taskService.complete(task1.getId(), vars);

    // 审核1
    List<Task> taskList2 = taskService.createTaskQuery().taskAssignee("handler2").orderByTaskCreateTime().desc().list();
    System.out.println("taskList2 = " + taskList2);

    formProperties.clear();
    formProperties.put("fullname", "handler2");
    formProperties.put("days", "4");
    formService.saveFormData(taskList2.get(0).getId(), formProperties);

    List<FormProperty> formPropertyList2 = formService.getTaskFormData(taskList2.get(0).getId()).getFormProperties();
    for (int j = 0; j < formPropertyList2.size(); j++) {
      System.out.println("--task2formdata");
      System.out.println(formPropertyList2.get(j).toString());
    }

    vars.clear();
    vars.put("handler3", "handler3");
    taskService.complete(taskList2.get(0).getId(), vars);

    List<Task> taskList3 = taskService.createTaskQuery().taskAssignee("handler3").orderByTaskCreateTime().desc().list();
    System.out.println("taskList3 = " + taskList3);

    formProperties.clear();
    formProperties.put("fullname", "handler3");
    formProperties.put("days", "5");
    formService.saveFormData(taskList3.get(0).getId(), formProperties);

    List<FormProperty> formPropertyList3 = formService.getTaskFormData(taskList3.get(0).getId()).getFormProperties();
    for (int j = 0; j < formPropertyList3.size(); j++) {
      System.out.println("--task3formdata");
      System.out.println(formPropertyList3.get(j).toString());
    }

    taskService.complete(taskList3.get(0).getId());

    List<Task> taskList7 = taskService.createTaskQuery().list();
    System.out.println("taskList7 = " + taskList7);

//    表单查询
    HistoryService historyService = processEngine.getHistoryService();
    List<HistoricDetail> list = historyService.createHistoricDetailQuery().processInstanceId(processInstance.getId()).formProperties().list();
    for (int i = 0; i < list.size(); i++) {
      HistoricDetail detail = list.get(i);
      System.out.println(detail);
//      System.out.println(detail.getProcessInstanceId());
//      System.out.println(detail.getTaskId());
//      System.out.println(detail.getActivityInstanceId());
//      System.out.println(detail.getExecutionId());
//      Task task = taskService.createTaskQuery().taskId(detail.getTaskId()).singleResult();
//      FormData formData = formService.getTaskFormData(task.getId());
//      List<FormProperty> formPropertyList = formData.getFormProperties();
//      for (int j = 0; j < formPropertyList.size(); j++) {
//        System.out.println(formPropertyList.get(j));
//      }
    }

  }

}
