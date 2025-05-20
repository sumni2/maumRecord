package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.Diary;
import maumrecord.maumrecord.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Diary> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Diary> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    List<Diary> findByUser(User user);

    Optional<Diary> findFirstByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    List<Diary> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

}
