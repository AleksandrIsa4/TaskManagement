package org.task.service;

import org.task.entity.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTask();

    Task getTask(Long id);

    Task saveTask(Task task);

    Task putTask(Long id, Task task);

    void deleteTask(Long id);
}
