package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

//로그 유형: signUp, login, diary, healing, inquiry
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    long countByActivityTimeBetween(LocalDateTime start, LocalDateTime end);

    List<UserActivityLog> findByActivityTimeBetween(LocalDateTime start, LocalDateTime end);

}
