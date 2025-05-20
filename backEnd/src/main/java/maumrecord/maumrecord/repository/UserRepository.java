package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    long count();

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
