package co.goorm.happiness.attendance.utils;

import co.goorm.happiness.attendance.response.dto.ParticipantDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class CsvConverter {

    private static final List<String> EXCLUDED_KEYS = Arrays.asList("사용자 이메일", "게스트", "대기실");
    private static final List<String> EXCLUDED_NAMES = Arrays.asList("구름", "관리자", "구름관리자", "goorm", "Goorm", "GOORM", "PC");

    public List<ParticipantDto> fromCsvToJson(List<String> csvList) throws JsonProcessingException {
        csvList.removeIf(String::isEmpty);

        if (csvList.size() <= 1) {
            return List.of();
        }

        String[] columns = csvList.get(0).split(",");

        Map<String, ParticipantDto> participantsMap = new HashMap<>();

        csvList.subList(1, csvList.size())
                .stream()
                .map(row -> row.split(","))
                .filter(row -> row.length == columns.length)
                .forEach(row -> {
                    Map<String, String> rowData = Arrays.stream(columns)
                            .filter(key -> !isExcludedKey(key))
                            .collect(Collectors.toMap(this::translateKey, key -> preprocessName(key, row[Arrays.asList(columns).indexOf(key)])));

                    String name = rowData.get("name");

                    if (!isExcludedName(name)) {
                        ParticipantDto existingParticipant = participantsMap.get(name);

                        if (existingParticipant == null) {
                            participantsMap.put(name, ParticipantDto.builder()
                                    .name(name)
                                    .duration(Integer.parseInt(rowData.get("duration")))
                                    .joinTime(LocalDateTime.parse(rowData.get("join_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")))
                                    .leaveTime(LocalDateTime.parse(rowData.get("leave_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")))
                                    .build());
                        } else {

                            if (LocalDateTime.parse(rowData.get("join_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"))
                                    .isBefore(existingParticipant.getJoinTime())) {
                                existingParticipant.setJoinTime(LocalDateTime.parse(rowData.get("join_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")));
                            }

                            if (LocalDateTime.parse(rowData.get("leave_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"))
                                    .isAfter(existingParticipant.getLeaveTime())) {
                                existingParticipant.setLeaveTime(LocalDateTime.parse(rowData.get("leave_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")));
                            }

                            Integer accumulate = existingParticipant.getDuration() + Integer.parseInt(rowData.get("duration"));
                            existingParticipant.setDuration(accumulate);
                        }
                    }
                });
        ArrayList<ParticipantDto> participantDto = new ArrayList<>(participantsMap.values());
        participantDto.sort(Comparator.comparing(ParticipantDto::getName));

        return participantDto;
    }

    private String preprocessName(String key, String value) {
        if ("name".equals(translateKey(key))) {
            if (value.contains("(")) {
                value = value.substring(0, value.indexOf("("));
            }

            if (value.startsWith("풀")) {
                value = value.substring(value.length() - 3);
            }

            value = value.replaceAll("\\s", "");
        }
        return value;
    }

    private boolean isExcludedName(String name) {
        return EXCLUDED_NAMES.contains(name);
    }

    private boolean isExcludedKey(String key) {
        return EXCLUDED_KEYS.contains(key);
    }

    private String translateKey(String originalKey) {
        return switch (originalKey) {
            case "이름(원래 이름)" -> "name";
            case "참가 시간" -> "join_time";
            case "나간 시간" -> "leave_time";
            case "기간(분)" -> "duration";
            default -> originalKey;
        };
    }
}