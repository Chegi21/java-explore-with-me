package ru.practicum.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestControllerAdvice
public class ExceptionErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации параметров: {}", e.getMessage());

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();

        ApiError errorDto = ApiError.builder()
                .errors(errors)
                .message("Ошибка валидации параметров")
                .reason("Неверные данные запроса")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("Ошибка валидации параметров: {}", e.getMessage());

        ApiError errorDto = ApiError.builder()
                .message("Ошибка валидации параметров")
                .reason("Неверные данные запроса")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidation(ValidationException e) {
        log.warn("Ошибка валидации параметров: {}", e.getMessage());

        ApiError errorDto = ApiError.builder()
                .message("Ошибка валидации параметров")
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception e) {
        log.error("НЕПРЕДВИДЕННАЯ ОШИБКА ОБРАБОТКИ: {}", e.getMessage());

        ApiError errorDto = ApiError.builder()
                .message("НЕПРЕДВИДЕННАЯ ОШИБКА")
                .reason("а пёс его знает...")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(NotFoundException e) {
        log.warn("Зафиксирован NOT FOUND: {}", e.getMessage());

        ApiError errorDto = ApiError.builder()
                .message(e.getMessage())
                .reason("Либо не нашли, либо неверно указан запрос")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);

    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(ConflictException e) {
        log.warn("Зафиксирован CONFLICT: {}", e.getMessage());

        ApiError errorDto = ApiError.builder()
                .message(e.getMessage())
                .reason("Пересекается с другими данными")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorDto);

    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(ForbiddenException e) {
        log.warn("Зафиксирован FORBIDDEN: {}", e.getMessage());

        ApiError errorDto = ApiError.builder()
                .message(e.getMessage())
                .reason("Запрещено трогать чужое")
                .status(HttpStatus.FORBIDDEN.name())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorDto);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.warn("Отсутствуют обязательные параметры запроса");

        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .reason("Отсутствуют обязательные параметры запроса")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }
}
