package co.goorm.happiness.attendance.response;

public record AttendanceResponse<T>(int status, T data) {

}
