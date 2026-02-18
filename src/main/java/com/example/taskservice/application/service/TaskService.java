
package com.example.taskservice.application.service;

import com.example.taskservice.audit.AuditLogger;
import com.example.taskservice.domain.model.Task;
import com.example.taskservice.domain.repository.TaskRepository;
import com.example.taskservice.event.TaskCreatedEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

 private final TaskRepository repo;
 private final ApplicationEventPublisher publisher;
 private final AuditLogger audit;

 public TaskService(TaskRepository r,ApplicationEventPublisher p,AuditLogger a){
  repo=r;publisher=p;audit=a;
 }

 public Task create(Task t){
  Task saved=repo.save(t);
  audit.log("created "+saved.getId());
  publisher.publishEvent(new TaskCreatedEvent(saved.getId()));
  return saved;
 }

 public Task get(Long id) {
  return repo.findById(id)
   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
 }

 public Task update(Long id, Task input) {
  Task existing = repo.findById(id)
   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
  existing.setTitle(input.getTitle());
  existing.setDescription(input.getDescription());
  return repo.save(existing);
 }

 public void delete(Long id) {
  Task existing = repo.findById(id)
   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
  repo.delete(existing);
 }

 @Cacheable("tasks")
 @CircuitBreaker(name="taskService",fallbackMethod="fallback")
 @RateLimiter(name="taskLimiter")
 public Page<Task> list(Pageable pageable){
  return repo.findAll(pageable);
 }

 public Page<Task> fallback(Pageable pageable,Throwable t){
  return Page.empty();
 }
}
