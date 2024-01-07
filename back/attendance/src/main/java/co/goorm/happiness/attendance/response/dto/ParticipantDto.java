package co.goorm.happiness.attendance.response.dto;

import co.goorm.happiness.attendance.model.Participant;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipantDto {

    @Id
    private String id;

    private String name;
    private Integer duration;
    @JsonProperty("join_time")
    private LocalDateTime joinTime;
    @JsonProperty("leave_time")
    private LocalDateTime leaveTime;

    public static ParticipantDto fromEntity(Participant participant) {
        return ParticipantDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .duration(participant.getDuration())
                .joinTime(participant.getJoinTime())
                .leaveTime(participant.getLeaveTime())
                .build();
    }

    public Participant toEntity() {
        return Participant.builder()
                .id(this.id)
                .name(this.name)
                .duration(this.duration)
                .joinTime(this.joinTime)
                .leaveTime(this.leaveTime)
                .build();
    }


}
