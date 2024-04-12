package org.task.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.task.entity.Task;
import org.task.exceptions.DataNotFoundException;
import org.task.model.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TaskServiceImplTest {

    private final TaskService taskService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Task taskInit1, taskInit2, taskInitPut;

    @BeforeEach
    void init() {
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-12 07:47", formatter);
        LocalDateTime dateTimePut = LocalDateTime.parse("2026-04-12 07:47", formatter);
        taskInit1 = new Task(null, "Название задачи 1", "Описание задачи 1", Status.NEW, dateTime);
        taskInit2 = new Task(null, "Название задачи 2", "Описание задачи 2", Status.NEW, dateTime);
        taskInitPut = new Task(99L, "Название задачи 3", "Описание задачи 3", Status.DONE, dateTimePut);
    }

    @Test
    @SneakyThrows
    @Transactional
    void saveDone() {
        Task taskSave1 = taskService.saveTask(taskInit1);
        Task taskSave2 = taskService.saveTask(taskInit2);
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskSave1.getDescription(), taskInit1.getDescription()),
                () -> Assertions.assertEquals(taskSave1.getCompleted(), Status.NEW),
                () -> Assertions.assertEquals(taskSave2.getCompleted(), Status.NEW),
                () -> Assertions.assertNotEquals(taskSave1.getId(), taskSave2.getId())
        );
    }

    @Test
    @SneakyThrows
    @Transactional
    void putDone() {
        Task taskSave1 = taskService.saveTask(taskInit1);
        Task taskPut1 = taskService.putTask(taskSave1.getId(), taskInitPut);
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskPut1.getDescription(), taskInitPut.getDescription()),
                () -> Assertions.assertEquals(taskPut1.getCompleted(), taskInitPut.getCompleted()),
                () -> Assertions.assertEquals(taskPut1.getDueDate(), taskInitPut.getDueDate()),
                () -> Assertions.assertEquals(taskSave1.getId(), taskPut1.getId())
        );
    }

    @Test
    @SneakyThrows
    @Transactional
    void putErrorNotFoundId() {
        Task taskSave1 = taskService.saveTask(taskInit1);
        Assertions.assertThrows(DataNotFoundException.class, () -> {
            taskService.putTask(777L, taskInitPut);
        });
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteDone() {
        Task taskSave1 = taskService.saveTask(taskInit1);
        Task taskSave2 = taskService.saveTask(taskInit2);
        List<Task> taskListBeforeDelete = taskService.getAllTask();
        taskService.deleteTask(taskSave1.getId());
        List<Task> taskListAfterDelete = taskService.getAllTask();
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskListAfterDelete.size(), 1),
                () -> Assertions.assertEquals(taskListBeforeDelete.size(), 2),
                () -> Assertions.assertEquals(taskListAfterDelete.get(0).getDescription(), taskInit2.getDescription())
        );
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteErrorNotFoundId() {
        Assertions.assertThrows(DataNotFoundException.class, () -> {
            taskService.deleteTask(123L);
        });
    }

    @Test
    @SneakyThrows
    @Transactional
    void getErrorNotFoundId() {
        Assertions.assertThrows(DataNotFoundException.class, () -> {
            taskService.getTask(123L);
        });
    }

    @Test
    @SneakyThrows
    @Transactional
    void getDone() {
        Task taskSave1 = taskService.saveTask(taskInit1);
        Task taskSave2 = taskService.saveTask(taskInit2);
        Task taskGet2 = taskService.getTask(taskSave2.getId());
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskGet2.getId(), taskSave2.getId()),
                () -> Assertions.assertEquals(taskGet2.getTitle(), taskSave2.getTitle())
        );
    }

    @Test
    @SneakyThrows
    @Transactional
    void getAllDone() {
        Task taskSave1 = taskService.saveTask(taskInit1);
        Task taskSave2 = taskService.saveTask(taskInit2);
        List<Task> taskList = taskService.getAllTask();
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskList.size(), 2),
                () -> Assertions.assertEquals(taskList.get(0).getTitle(), taskSave1.getTitle()),
                () -> Assertions.assertEquals(taskList.get(1).getId(), taskSave2.getId())
        );
    }
}