package co.goorm.happiness.attendance.response;

public record AttendanceErrorResponse<T>(int status, T message) {

}