package co.goorm.happiness.attendance.model;

public enum AttendanceStatus {

    ATTENDANCE(1),
    LATE(2),
    ABSENT(5);

    private final Integer code;

    AttendanceStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}