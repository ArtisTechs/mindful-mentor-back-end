package com.example.mindful_mentor.controller;

import com.example.mindful_mentor.dto.AppointmentDTO;
import com.example.mindful_mentor.dto.AppointmentDateUpdateDTO;
import com.example.mindful_mentor.dto.AppointmentStatusUpdateDTO;
import com.example.mindful_mentor.model.Appointment;
import com.example.mindful_mentor.model.AppointmentStatus;
import com.example.mindful_mentor.service.AppointmentService;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    @GetMapping
    public Page<Appointment> getAppointments(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            @RequestParam(defaultValue = "true") boolean sortAscending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean ignorePagination // Add the ignorePagination parameter
    ) {
        return appointmentService.getAppointments(userId, startDate, endDate, status, sortBy, sortAscending, page, size, ignorePagination);
    }


    @PostMapping("/create")
    public Appointment createAppointment(@RequestBody AppointmentDTO appointmentDTO) throws Exception {
        return appointmentService.createAppointment(appointmentDTO);
    }
    
    @PutMapping("/status/{id}")
    public Appointment updateAppointmentStatus(
        @PathVariable UUID id, 
        @RequestBody AppointmentStatusUpdateDTO statusUpdateDTO
    ) throws Exception {
        return appointmentService.updateAppointmentStatus(id, statusUpdateDTO);
    }
    
    @PutMapping("/date/{id}")
    public Appointment updateAppointmentDate(
        @PathVariable UUID id, 
        @RequestBody AppointmentDateUpdateDTO dateUpdateDTO
    ) throws Exception {
        return appointmentService.updateAppointmentDate(id, dateUpdateDTO);
    }
    
 // Delete an Appointment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        
        // Create a response message
        return ResponseEntity.ok().body(Map.of("message", "Appointment deleted successfully."));
    }
}
