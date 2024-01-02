package co.goorm.happiness.attendance.advice;

import co.goorm.happiness.attendance.response.AttendanceErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(IOException.class)
    public ResponseEntity<AttendanceErrorResponse<?>> handleIOException(IOException e) {
        log.error("IOException 발생: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AttendanceErrorResponse<>(500, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AttendanceErrorResponse<?>> handleException(Exception e) {
        log.error("예상치 못한 오류 발생: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AttendanceErrorResponse<>(500, "An unexpected error occurred"));
    }
}