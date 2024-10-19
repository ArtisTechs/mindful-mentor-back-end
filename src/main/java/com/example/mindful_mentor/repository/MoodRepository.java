package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.Mood;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MoodRepository extends JpaRepository<Mood, UUID> {

    @Query("SELECT m FROM Mood m WHERE "
        + "(:userId IS NULL OR m.user.id = :userId) AND "
        + "(:moodCode IS NULL OR m.mood.code = :moodCode) AND "
        + "(:startDate IS NULL OR m.date >= :startDate) AND "
        + "(:endDate IS NULL OR m.date <= :endDate)")
    List<Mood> findMoodsByFilters(
        @Param("userId") UUID userId,
        @Param("moodCode") String moodCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Sort sort
    );
    
    List<Mood> findByUserIdAndDate(UUID userId, LocalDate date);
    @Transactional
    void deleteByUserId(UUID userId);

}
