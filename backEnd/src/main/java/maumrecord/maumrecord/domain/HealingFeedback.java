package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "healing_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealingFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback", nullable = false)
    private FeedbackType feedback; // 좋았다 = Good, 별로였다 = BAD

    @ManyToOne
    @JoinColumn(name = "healing_program_id", nullable = false)
    private HealingProgram healingProgram;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public enum FeedbackType {
        GOOD, BAD
    }
}

