package org.task.exceptions.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {

    HttpStatus status;
    String reason;
    String message;
    String timestamp;

    List<String> errors;
}