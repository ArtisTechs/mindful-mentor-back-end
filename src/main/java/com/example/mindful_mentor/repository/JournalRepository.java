package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.Journal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {

    // Method to find all journal entries for a specific user
    List<Journal> findByUserId(UUID userId, Sort sort);
    boolean existsByUserIdAndEntryDate(UUID userId, Date date);
}
