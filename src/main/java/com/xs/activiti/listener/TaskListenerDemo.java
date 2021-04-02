package com.xs.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import java.io.Serializable;

public class TaskListenerDemo implements Serializable, TaskListener {

  private Expression arg;

  public Expression getArg() {
    return arg;
  }

  public void setArg(Expression arg) {
    this.arg = arg;
  }

//  @Override
  public void notify(DelegateTask delegateTask) {
    System.out.println("任务监听器:" + delegateTask.toString());
    String eventName = delegateTask.getEventName();
    if ("create".endsWith(eventName)) {
      System.out.println("create=========");
    } else if ("assignment".endsWith(eventName)) {
      System.out.println("assignment========");
    } else if ("complete".endsWith(eventName)) {
      System.out.println("complete===========");
    } else if ("delete".endsWith(eventName)) {
      System.out.println("delete=============");
    }
  }
}

