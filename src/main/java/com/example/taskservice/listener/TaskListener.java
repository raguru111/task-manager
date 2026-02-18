
package com.example.taskservice.listener;
import com.example.taskservice.event.TaskCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TaskListener {
 @Async @EventListener
 public void on(TaskCreatedEvent e){
  System.out.println("Async event:"+e.getId());
 }
}
