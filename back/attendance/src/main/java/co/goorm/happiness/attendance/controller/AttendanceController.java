package co.goorm.happiness.attendance.controller;

import co.goorm.happiness.attendance.request.dto.AttendanceDataRequestDto;
import co.goorm.happiness.attendance.response.AttendanceResponse;
import co.goorm.happiness.attendance.response.dto.AttendanceCheckDto;
import co.goorm.happiness.attendance.response.dto.ParticipantDto;
import co.goorm.happiness.attendance.service.AttendanceService;
import co.goorm.happiness.attendance.utils.CsvConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(("/att"))
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final ResourceLoader resourceLoader;
    private static final String CSV_FILE_PATH = "participants_84143883166.csv";

    @PostMapping("/data/meeting")
    public ResponseEntity<?> processData(@RequestBody AttendanceDataRequestDto request){

        List<ParticipantDto> people = request.getParticipants();
        List<AttendanceCheckDto> attendanceCheck = attendanceService.checkAttendance(people);

        return ResponseEntity.ok(new AttendanceResponse<>(200, attendanceCheck));
    }

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

    @PostMapping("/json")
    public ResponseEntity<?> csvToJsonExternal(@RequestParam("file") MultipartFile file) throws IOException {
        try {

            String resourcePath = resourceLoader.getResource("classpath:data/").getURI().getPath();
            String destinationPath = resourcePath + file.getOriginalFilename();
            file.transferTo(new File(destinationPath));

            List<ParticipantDto> jsonResult = attendanceService.csvToJson(destinationPath);
            return ResponseEntity.ok(new AttendanceResponse<>(200, jsonResult));

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
            return ResponseEntity.status(500).body("An unexpected error occurred");
        }
    }
}