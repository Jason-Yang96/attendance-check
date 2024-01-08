package co.goorm.happiness.attendance.utils;

import co.goorm.happiness.attendance.model.AttendanceStatus;
import co.goorm.happiness.attendance.response.dto.AttendanceCheckDto;
import co.goorm.happiness.attendance.response.dto.ParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;


@Slf4j
@Component
public class AttendanceChecker {

    private static final List<LocalDateTimeRange> CLASS_SESSIONS = Arrays.asList(
            new LocalDateTimeRange(LocalTime.of(10, 10), LocalTime.of(10, 50)),
            new LocalDateTimeRange(LocalTime.of(11, 10), LocalTime.of(11, 50)),
            new LocalDateTimeRange(LocalTime.of(12, 10), LocalTime.of(12, 50)),
            new LocalDateTimeRange(LocalTime.of(14, 10), LocalTime.of(14, 50)),
            new LocalDateTimeRange(LocalTime.of(15, 10), LocalTime.of(15, 50)),
            new LocalDateTimeRange(LocalTime.of(16, 10), LocalTime.of(16, 50)),
            new LocalDateTimeRange(LocalTime.of(17, 10), LocalTime.of(17, 50)),
            new LocalDateTimeRange(LocalTime.of(18, 10), LocalTime.of(18, 49))
    );

    public List<AttendanceCheckDto> attendanceCheck(List<ParticipantDto> data) {
        List<AttendanceCheckDto> result = new ArrayList<>();
        Map<String, Integer[]> processedRecords = new HashMap<>();

        for (ParticipantDto participant : data) {
            String name = participant.getName();
            String id = participant.getId();
            Integer[] checkList;

            String participantKey = name + id;

            if (processedRecords.containsKey(participantKey)) {
                log.info("나왔던 이름입니다.");
                checkList = processedRecords.get(participantKey);
            } else {
                checkList = new Integer[8];
                Arrays.fill(checkList, -99);
            }

            for (int i = 0; i < CLASS_SESSIONS.size(); i++) {
                LocalDateTimeRange classSession = CLASS_SESSIONS.get(i);

                if (checkList[i] == -99) {
                    if (participant.getJoinTime().toLocalTime().isBefore(classSession.getStartTime()) &&
                            participant.getLeaveTime().toLocalTime().isAfter(classSession.getEndTime())) {
                        checkList[i] = AttendanceStatus.ATTENDANCE.getCode();
                    } else if (participant.getJoinTime().toLocalTime().isAfter(classSession.getStartTime()) &&
                            participant.getJoinTime().toLocalTime().isBefore(classSession.getEndTime()) &&
                            participant.getLeaveTime().toLocalTime().isAfter(classSession.getEndTime())) {
                        checkList[i] = AttendanceStatus.LATE.getCode();
                        if (i > 0 && Objects.equals(checkList[i - 1], AttendanceStatus.LATE.getCode())) {
                            checkList[i - 1] = AttendanceStatus.ABSENT.getCode();
                        }
                    }
                }
            }

            // 이미 처리된 참가자에 대한 정보를 맵에 저장
            processedRecords.put(participantKey, checkList);

            // 결과 리스트에 추가하기 전에 이미 존재하는 경우 수정
            boolean updated = false;
            for (AttendanceCheckDto dto : result) {
                if (dto.getName().equals(preprocessName(name))) {
                    dto.setCheckList(checkList);
                    updated = true;
                    break;
                }
            }

            // 존재하지 않는 경우에만 추가
            if (!updated) {
                result.add(AttendanceCheckDto.builder()
                        .name(preprocessName(name))
                        .checkList(checkList)
                        .build());
            }
        }

        for (AttendanceCheckDto dto : result) {
            Integer[] checkList = dto.getCheckList();

            for (int i = 0; i < checkList.length; i++) {
                if (checkList[i] == -99) {
                    checkList[i] = 5;
                }
            }


            // 지금의 룰... 2교시부터 7교시까지는....
//            for (int i = 1; i < checkList.length - 1; i++) {
//                if (checkList[i] != 1) {
//                    checkList[i] = 1;
//                }
//            }

            // 추가 결석 로직
            long countOf5 = Arrays.stream(checkList).filter(value -> value == 5).count();
            if (countOf5 >= 5) {
                Arrays.fill(checkList, 5);
            }
            if (checkList[0] == 5 && checkList[checkList.length - 1] == 5) {
                Arrays.fill(checkList, 5);
            }
            if (checkList[0] == 2 && checkList[checkList.length - 1] == 5) {
                Arrays.fill(checkList, 5);
            }
        }

        return result;
    }


    private String preprocessName(String value) {
        if (value.contains("(")) {
            value = value.substring(0, value.indexOf("("));
        }

        if (value.startsWith("풀")) {
            value = value.substring(value.length() - 3);
        }

        value = value.replaceAll("\\s", "");

        return value;
    }

    @Data
    @AllArgsConstructor
    @Builder
    private static class LocalDateTimeRange {
        private final LocalTime startTime;
        private final LocalTime endTime;
    }
}