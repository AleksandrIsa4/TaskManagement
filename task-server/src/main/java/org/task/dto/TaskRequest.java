package org.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.task.model.Status;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class TaskRequest {

    @NotBlank(message = "Название задачи не может отсутствовать")
    @NotNull(message = "Название задачи не может быть пустым")
    @Schema(description = "Название задачи")
    String title;

    @NotBlank(message = "Описание задачи не может отсутствовать")
    @NotNull(message = "Описание задачи не может быть пустым")
    @Schema(description = "Описание задачи")
    String description;

    @Future(message = "Срок не может быть в прошлом")
    @NotNull(message = "Срок выполнения не может отсутствовать")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Срок выполнения")
    LocalDateTime dueDate;

    @Schema(description = "Статус задачи")
    Status completed;
}
