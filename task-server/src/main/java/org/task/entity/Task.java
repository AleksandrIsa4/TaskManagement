package org.task.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.task.model.Status;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Schema(description = "Название задачи")
    String title;

    @Schema(description = "Описание задачи")
    String description;

    @Schema(description = "Статус задачи")
    @Enumerated(EnumType.STRING)
    Status completed;

    @Schema(description = "Срок выполнения")
    LocalDateTime dueDate;
}
