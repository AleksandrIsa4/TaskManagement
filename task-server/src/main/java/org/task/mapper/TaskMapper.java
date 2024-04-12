package org.task.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.task.dto.TaskRequest;
import org.task.dto.TaskResponse;
import org.task.entity.Task;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskMapper {

    public static Task toTask(TaskRequest taskRequest) {
        return Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .completed(taskRequest.getCompleted())
                .build();
    }

    public static TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .completed(task.getCompleted())
                .build();
    }
}
