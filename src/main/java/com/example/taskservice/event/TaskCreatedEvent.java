
package com.example.taskservice.event;
public class TaskCreatedEvent {
 private final Long id;
 public TaskCreatedEvent(Long id){this.id=id;}
 public Long getId(){return id;}
}
