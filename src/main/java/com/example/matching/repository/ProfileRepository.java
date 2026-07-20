package com.example.matching.repository;

import com.example.matching.entity.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId); // user_id で1件検索
    boolean existsByUserId(Long userId);
    List<Profile> findByUserIdNot(Long userId);
}
