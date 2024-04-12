package org.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.task.dto.TaskRequest;
import org.task.dto.TaskResponse;
import org.task.entity.Task;
import org.task.exceptions.BadRequestException;
import org.task.mapper.TaskMapper;
import org.task.model.Status;
import org.task.service.TaskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получить список всех задач")
    @GetMapping
    public List<TaskResponse> getAllTask() {
        return taskService.getAllTask().stream().map(TaskMapper::toTaskResponse).toList();
    }

    @Operation(summary = "Получить информацию о задаче по её id")
    @GetMapping(value = "/{id}")
    public TaskResponse getTaskId(@PathVariable Long id) {
        return TaskMapper.toTaskResponse(taskService.getTask(id));
    }

    @Operation(summary = "Создать новую задачу")
    @PostMapping
    public TaskResponse postTask(@RequestBody @Valid TaskRequest taskRequest) {
        Task task = TaskMapper.toTask(taskRequest);
        task.setCompleted(Status.NEW);
        return TaskMapper.toTaskResponse(taskService.saveTask(task));
    }

    @Operation(summary = "Обновить информацию о задаче по её id")
    @PutMapping(value = "/{id}")
    public TaskResponse putTaskId(@PathVariable Long id, @RequestBody @Valid TaskRequest taskRequest) {
        if (taskRequest.getCompleted() == null) {
            throw new BadRequestException("Status в методе put не может быть null");
        }
        Task task = TaskMapper.toTask(taskRequest);
        return TaskMapper.toTaskResponse(taskService.putTask(id, task));
    }

    @Operation(summary = "Удалить задачу по её id")
    @DeleteMapping(value = "/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
