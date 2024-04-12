package org.task.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.task.dto.TaskRequest;
import org.task.dto.TaskResponse;
import org.task.model.Status;
import org.task.service.TaskService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TaskService taskService;

    TaskRequest taskRequestStatusNull, taskRequestStatusNew, taskRequestStatusProgress, taskRequestStatusDone;

    TaskRequest taskRequestNotValidTime, taskRequestNotValidStatusNullPut, taskRequestNotValidTitleNull, taskRequestNotValidDescriptionBlank;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void init() {
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-12 07:47", formatter);
        taskRequestStatusNull = new TaskRequest("Название задачи 1", "Описание задачи 1", dateTime, null);
        taskRequestStatusNew = new TaskRequest("Название задачи 2", "Описание задачи 2", dateTime, Status.NEW);
        taskRequestStatusProgress = new TaskRequest("Название задачи 3", "Описание задачи 3", dateTime, Status.IN_PROGRESS);
        taskRequestStatusDone = new TaskRequest("Название задачи 4", "Описание задачи 4", dateTime, Status.DONE);
        LocalDateTime dateTimeNotValid = LocalDateTime.parse("2022-04-12 07:47", formatter);
        taskRequestNotValidTime = new TaskRequest("Название задачи 11", "Описание задачи 11", dateTimeNotValid, null);
        taskRequestNotValidStatusNullPut = new TaskRequest("Название задачи 12", "Описание задачи 12", dateTime, null);
        taskRequestNotValidTitleNull = new TaskRequest(null, "Описание задачи 13", dateTime, Status.DONE);
        taskRequestNotValidDescriptionBlank = new TaskRequest("Название задачи 14", "", dateTime, Status.DONE);
    }

    @Test
    @SneakyThrows
    void postTaskDone() {
        MvcResult resultStatusNull = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNull)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        MvcResult resultStatusDone = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusDone)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusNull = objectMapper.readValue(
                resultStatusNull.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        TaskResponse taskResponseStatusDone = objectMapper.readValue(
                resultStatusDone.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskResponseStatusNull.getDescription(), taskRequestStatusNull.getDescription()),
                () -> Assertions.assertEquals(taskResponseStatusNull.getCompleted(), Status.NEW),
                () -> Assertions.assertEquals(taskResponseStatusDone.getCompleted(), Status.NEW),
                () -> Assertions.assertEquals(taskResponseStatusDone.getDueDate(), LocalDateTime.parse("2025-04-12 07:47", formatter))
        );
    }

    @Test
    @SneakyThrows
    void postTaskNotValidDate() {
        Assertions.assertAll(
                () -> mockMvc.perform(MockMvcRequestBuilders
                                .post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskRequestNotValidTime)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(MockMvcRequestBuilders
                                .post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskRequestNotValidTitleNull)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(MockMvcRequestBuilders
                                .post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskRequestNotValidDescriptionBlank)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(MockMvcRequestBuilders
                                .put("/tasks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskRequestNotValidStatusNullPut)))
                        .andExpect(status().isBadRequest())
        );
    }

    @Test
    @SneakyThrows
    void putTaskDone() {
        MvcResult resultStatusNew = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNew)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusNew = objectMapper.readValue(
                resultStatusNew.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        String urlPut = "/tasks/" + taskResponseStatusNew.getId();
        MvcResult resultStatusDone = mockMvc.perform(MockMvcRequestBuilders
                        .put(urlPut)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusDone)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusDone = objectMapper.readValue(
                resultStatusDone.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskResponseStatusNew.getDescription(), taskRequestStatusNew.getDescription()),
                () -> Assertions.assertEquals(taskResponseStatusNew.getId(), taskResponseStatusDone.getId()),
                () -> Assertions.assertEquals(taskResponseStatusDone.getCompleted(), Status.DONE)
        );
    }

    @Test
    @SneakyThrows
    void getTaskDone() {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNull)))
                .andExpect(status().is2xxSuccessful());
        MvcResult resultStatusDone = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusDone)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusDone = objectMapper.readValue(
                resultStatusDone.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        String urlGet = "/tasks/" + taskResponseStatusDone.getId();
        MvcResult resultStatusDoneGet = mockMvc.perform(MockMvcRequestBuilders
                        .get(urlGet))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusDoneGet = objectMapper.readValue(
                resultStatusDoneGet.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskResponseStatusDoneGet.getDescription(), taskResponseStatusDone.getDescription()),
                () -> Assertions.assertEquals(taskResponseStatusDoneGet.getCompleted(), taskResponseStatusDone.getCompleted()),
                () -> Assertions.assertEquals(taskResponseStatusDoneGet.getId(), taskResponseStatusDone.getId())
        );
    }

    @Test
    @SneakyThrows
    void getTaskNotFoundId() {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNull)))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/tasks/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAllTaskDone() {
        MvcResult resultStatusNull = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNull)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusDone)))
                .andExpect(status().is2xxSuccessful());
        MvcResult resultGetAll = mockMvc.perform(MockMvcRequestBuilders
                        .get("/tasks"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusNull = objectMapper.readValue(
                resultStatusNull.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        List<TaskResponse> taskResponseGetAll = objectMapper.readValue(
                resultGetAll.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<List<TaskResponse>>() {
                });
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskResponseGetAll.size(), 2),
                () -> Assertions.assertEquals(taskResponseGetAll.get(1).getCompleted(), Status.NEW),
                () -> Assertions.assertEquals(taskResponseGetAll.get(0).getTitle(), taskResponseStatusNull.getTitle())
        );
    }

    @Test
    @SneakyThrows
    void deletelTaskDone() {
        MvcResult resultStatusNull = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNull)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        MvcResult resultStatusDone = mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusDone)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        TaskResponse taskResponseStatusNull = objectMapper.readValue(
                resultStatusNull.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        String urlDelete = "/tasks/" + taskResponseStatusNull.getId();
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlDelete))
                .andExpect(status().is2xxSuccessful());
        MvcResult resultGetAll = mockMvc.perform(MockMvcRequestBuilders
                        .get("/tasks"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        TaskResponse taskResponseStatusDone = objectMapper.readValue(
                resultStatusDone.getResponse().getContentAsString(StandardCharsets.UTF_8),
                TaskResponse.class);
        List<TaskResponse> taskResponseGetAll = objectMapper.readValue(
                resultGetAll.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<List<TaskResponse>>() {
                });
        Assertions.assertAll(
                () -> Assertions.assertEquals(taskResponseGetAll.size(), 1),
                () -> Assertions.assertEquals(taskResponseGetAll.get(0).getTitle(), taskResponseStatusDone.getTitle())
        );
    }

    @Test
    @SneakyThrows
    @Transactional
    void deletelTaskNotFound() {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusNull)))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestStatusDone)))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/tasks/22"))
                .andExpect(status().isNotFound());
    }
}