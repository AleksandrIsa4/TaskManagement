package org.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.task.model.Status;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class TaskResponse {

    @Schema(description = "id задачи")
    Long id;

    @Schema(description = "Название задачи")
    String title;

    @Schema(description = "Описание задачи")
    String description;

    @Schema(description = "Статус задачи")
    Status completed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Срок выполнения")
    LocalDateTime dueDate;
}
