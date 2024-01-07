package co.goorm.happiness.attendance.request.dto;

import co.goorm.happiness.attendance.response.dto.ParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * {
 * "customer_key":"349589LkJyeW",
 * "duration":259,
 * "failover":false,
 * "id":"30R7kT7bTIKSNUFEuH_Qlg",
 * "join_time":"2022-03-23T06:58:09Z",
 * "leave_time":"2022-03-23T07:02:28Z",
 * "name":"example",
 * "registrant_id":"abcdefghij0-klmnopq23456",
 * "status":"in_meeting",
 * "user_email":"jchill@example.com",
 * "user_id":"27423744",
 * "bo_mtg_id":"27423744",
 * "participant_user_id":"DYHrdpjrS3uaOf7dPkkg8w"
 * }
 *
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceDataRequestDto {

    private List<ParticipantDto> participants;

}
