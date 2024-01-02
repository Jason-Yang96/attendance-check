package co.goorm.happiness.attendance.response;

import lombok.Builder;


public record AttendanceResponse<T>(int status, T data) {

}
