package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.Appointment;
import com.example.mindful_mentor.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    @Query("SELECT a FROM Appointment a WHERE (:userId IS NULL OR a.user.id = :userId) " +
           "AND (:startDate IS NULL OR a.scheduledDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.scheduledDate <= :endDate) " +
           "AND (:status IS NULL OR a.status = :status)")
    Page<Appointment> findAppointmentsByFilters(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") AppointmentStatus status,
        Pageable pageable
    );
}
