package co.goorm.happiness.attendance.service;


import co.goorm.happiness.attendance.response.dto.ParticipantDto;
import co.goorm.happiness.attendance.utils.CsvConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class AttendanceService {
    private final CsvConverter csvConverter;

    /**
     *  현재 리소스 디렉에 있는 csv 파일로 사용할 때
     *  이런 식으로 접근 가능함
     *
     *  나중에 csv를 직접 받아 올 때는 달라지는 걸로....
     *  일단은 이렇게...
     */
    public List<ParticipantDto> csvToJson(InputStream inputStream)  {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<String> csvRows = new BufferedReader(reader).lines().collect(Collectors.toList());
            if (!csvRows.isEmpty()) {
                try {
                    return csvConverter.fromCsvToJson(csvRows);

                } catch (JsonProcessingException e) {
                    log.error("Error converting CSV to JSON", e);
                    throw new IOException("Error converting CSV to JSON", e);
                }
            }
            throw new IOException("CSV file is empty");
        } catch (IOException e) {
            throw new RuntimeException("Something Wrong... I/O");
        }
    }
}
