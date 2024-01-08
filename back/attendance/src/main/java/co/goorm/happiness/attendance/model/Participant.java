package co.goorm.happiness.attendance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Participant {

    @Id
    private String id;

    private String name;
    private Integer duration;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;


}
