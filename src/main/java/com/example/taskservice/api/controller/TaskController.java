
package com.example.taskservice.api.controller;

import com.example.taskservice.application.service.TaskService;
import com.example.taskservice.domain.model.Task;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
 private final TaskService service;
 public TaskController(TaskService s){service=s;}

 @PostMapping public Task create(@RequestBody Task t){return service.create(t);}
 @GetMapping("/{id}") public Task get(@PathVariable Long id){return service.get(id);}
 @GetMapping public Page<Task> list(Pageable pageable){return service.list(pageable);}
 @PutMapping("/{id}") public Task update(@PathVariable Long id,@RequestBody Task t){
  return service.update(id,t);
 }
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){service.delete(id);}
}
