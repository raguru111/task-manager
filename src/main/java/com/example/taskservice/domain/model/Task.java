
package com.example.taskservice.domain.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Task {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String title;
 private String description;
 private LocalDateTime createdAt = LocalDateTime.now();

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public String getTitle() {
  return title;
 }

 public void setTitle(String title) {
  this.title = title;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }

 public LocalDateTime getCreatedAt() {
  return createdAt;
 }

 public void setCreatedAt(LocalDateTime createdAt) {
  this.createdAt = createdAt;
 }
}
