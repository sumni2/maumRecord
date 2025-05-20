package maumrecord.maumrecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import maumrecord.maumrecord.domain.*;

import java.time.LocalDateTime;
import java.util.List;


public interface HealingFeedbackRepository extends JpaRepository<HealingFeedback, Long> {
    List<HealingFeedback> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

}

