package com.xs.activiti;

import com.xs.activiti.listener.ExectuionListenerDemo;
import com.xs.activiti.listener.TaskListenerDemo;
import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParallelDemo {

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
//    vars.put("submiter", "submiter1");
    TaskListenerDemo taskListenerDemo = new TaskListenerDemo();
    ExectuionListenerDemo exectuionListenerDemo = new ExectuionListenerDemo();
    vars.put("taskListenerDemo",taskListenerDemo);
    vars.put("exectuionListenerDemo",exectuionListenerDemo);
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("parallel_demo_key", "Key001", vars);

    System.out.println("流程实例ID = " + processInstance.getId()); // 5001
    System.out.println("正在活动的流程节点ID = " + processInstance.getActivityId()); // usertask
    System.out.println("流程定义ID = " + processInstance.getProcessDefinitionId()); // xs_doc_approve_key:1:4

    // 拟稿任务查询
    TaskService taskService = processEngine.getTaskService();
    List<Task> taskList1 = taskService.createTaskQuery().taskAssignee("submiter").orderByTaskCreateTime().desc().list();
    System.out.println("taskList1 = " + taskList1); //taskList1 = [Task[id=5005, name=拟稿]]

    Task task1 = taskList1.get(0);
    taskService.complete(task1.getId());

    // 审核1
    List<Task> taskList2 = taskService.createTaskQuery().taskAssignee("mishuzhang").orderByTaskCreateTime().desc().list();
    System.out.println("taskList2 = " + taskList2);
    taskService.complete(taskList2.get(0).getId());

    List<Task> taskList3 = taskService.createTaskQuery().taskAssignee("dept1").orderByTaskCreateTime().desc().list();
    System.out.println("taskList3 = " + taskList3);
    taskService.complete(taskList3.get(0).getId());

    List<Task> taskList4 = taskService.createTaskQuery().taskAssignee("dept2").orderByTaskCreateTime().desc().list();
    System.out.println("taskList4 = " + taskList4);
    vars.clear();
    ExectuionListenerDemo exectuionListenerDemo1 = new ExectuionListenerDemo();
    vars.put("exectuionListenerDemo",exectuionListenerDemo);
    taskService.complete(taskList4.get(0).getId());

    List<Task> taskList5 = taskService.createTaskQuery().taskAssignee("zonghe").orderByTaskCreateTime().desc().list();
    System.out.println("taskList5 = " + taskList5);
    taskService.complete(taskList5.get(0).getId());

    List<Task> taskList7 = taskService.createTaskQuery().list();
    System.out.println("taskList7 = " + taskList7);


  }

}
