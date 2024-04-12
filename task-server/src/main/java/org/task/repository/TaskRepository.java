package org.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.task.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}