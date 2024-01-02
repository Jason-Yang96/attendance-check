package co.goorm.happiness.attendance.controller;

import co.goorm.happiness.attendance.response.AttendanceResponse;
import co.goorm.happiness.attendance.response.dto.ParticipantDto;
import co.goorm.happiness.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(("/att"))
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    private static final String CSV_FILE_PATH = "participants_84143883166.csv";

    @GetMapping("/json")
    public ResponseEntity<?> csvToJson() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_FILE_PATH)) {

            List<ParticipantDto> jsonResult =  attendanceService.csvToJson(inputStream);
            return ResponseEntity.ok(new AttendanceResponse<>(200, jsonResult));
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(500).body("An unexpected error occurred");
        }
    }
}