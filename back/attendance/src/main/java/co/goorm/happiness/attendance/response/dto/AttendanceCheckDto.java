package co.goorm.happiness.attendance.response.dto;

import co.goorm.happiness.attendance.model.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceCheckDto {

//    private String id;

    private String name;

    private Integer[] checkList = new Integer[8];

}
