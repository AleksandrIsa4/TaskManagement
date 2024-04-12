package org.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.task.entity.Task;
import org.task.exceptions.DataNotFoundException;
import org.task.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getTask(Long id) {
        return getExistingTask(id);
    }

    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task putTask(Long id, Task task) {
        getExistingTask(id);
        task.setId(id);
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        getExistingTask(id);
        taskRepository.deleteById(id);
    }

    private Task getExistingTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Задачи с id=" + id + " нет"));
    }
}
