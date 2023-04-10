package ru.clevertec.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private Integer statusCode;
    private String timestamp;
    private String uri;
    private String message;

}
