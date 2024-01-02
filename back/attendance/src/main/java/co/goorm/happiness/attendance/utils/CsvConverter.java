package co.goorm.happiness.attendance.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class CsvConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> EXCLUDED_KEYS = Arrays.asList("사용자 이메일", "게스트", "대기실");
    private static final List<String> EXCLUDED_NAMES = Arrays.asList("구름", "관리자", "구름관리자");
    public String fromCsvToJson(List<String > csvList) throws JsonProcessingException {
//        csvList.removeIf(s -> s.trim().isEmpty());
//
//        if (csvList.size() <= 1) {
//            return "[]";
//        }
//
//        String[] columns = csvList.get(0).split(",");
//
//        StringBuilder json = new StringBuilder("[\n");
//        csvList.subList(1, csvList.size())
//                .stream()
//                .map(row -> row.split(","))
//                .filter(row -> row.length == columns.length)
//                .forEach(row -> {
//                    json.append("\t{\n");
//
//                    for (int i = 0; i < columns.length; i++) {
//                        json.append("\t\t\"")
//                                .append(columns[i])
//                                .append("\" : \"")
//                                .append(row[i])
//                                .append("\",\n");
//                    }
//
//                    json.replace(json.lastIndexOf(","), json.length(), "\n");
//                    json.append("\t},\n");
//                });
//
//        if (json.lastIndexOf(",") != -1) {
//            json.replace(json.lastIndexOf(","), json.length(), "\n");
//        }
//
//        json.append("]");
        //return json.toString();

        csvList.removeIf(String::isEmpty);

        if (csvList.size() <= 1) {
            return "[]";
        }

        String[] columns = csvList.get(0).split(",");

        List<Map<String, String>> jsonList = csvList.subList(1, csvList.size())
                .stream()
                .map(row -> row.split(","))
                .filter(row -> row.length == columns.length)
                .map(row -> {
                    Map<String, String> rowData = Arrays.stream(columns)
                            .filter(key -> !isExcludedKey(key))
                            .collect(Collectors.toMap(key -> translateKey(key), key -> preprocessName(key, row[Arrays.asList(columns).indexOf(key)])));
                    return rowData;
                })
                .filter(map -> !isExcludedName(map.get("name")))
                .collect(Collectors.toList());



        return objectMapper.writeValueAsString(jsonList);
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