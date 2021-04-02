package com.xs.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

import java.io.Serializable;

public class ExectuionListenerDemo implements Serializable, ExecutionListener {

  private static final long serialVersionUID = 8513750196548027535L;
  private Expression message;

  public Expression getMessage() {
    return message;
  }

  public void setMessage(Expression message) {
    this.message = message;
  }


//  @Override
  public void notify(DelegateExecution execution) {
    System.out.println("流程监听器 : " + execution.toString());
    String eventName = execution.getEventName();
//start
    if ("start".equals(eventName)) {
      System.out.println("start=========");
    } else if ("end".equals(eventName)) {
      System.out.println("end=========");
    } else if ("take".equals(eventName)) {
      System.out.println("take=========");
    }

  }
}

