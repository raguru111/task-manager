
package com.example.taskservice.domain.repository;
import com.example.taskservice.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TaskRepository extends JpaRepository<Task,Long>{}
