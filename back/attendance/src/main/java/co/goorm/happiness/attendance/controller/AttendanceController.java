package co.goorm.happiness.attendance.controller;

import co.goorm.happiness.attendance.response.AttendanceErrorResponse;
import co.goorm.happiness.attendance.response.AttendanceResponse;
import co.goorm.happiness.attendance.utils.CsvConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(("/att"))
@RequiredArgsConstructor
public class AttendanceController {

    private final CsvConverter csvConverter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CSV_FILE_PATH = "participants_84143883166.csv";

    @GetMapping("/json")
    public ResponseEntity<?> csvToJson() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_FILE_PATH);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            List<String> csvRows = new BufferedReader(reader).lines().collect(Collectors.toList());

            if (!csvRows.isEmpty()) {
                try {
                    String jsonResult = csvConverter.fromCsvToJson(csvRows);
                    log.info(jsonResult);
                    return ResponseEntity.ok(new AttendanceResponse<>(200, jsonResult));

                } catch (JsonProcessingException e) {
                    log.error("Error converting CSV to JSON", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AttendanceErrorResponse<>(500, "Error converting to JSON"));
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AttendanceErrorResponse<>(400, "CSV file is empty"));
        } catch (IOException e) {
            log.error("Error reading CSV file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AttendanceErrorResponse<>(500, "Error reading CSV file"));
        }

    }
}
