package com.example.mindful_mentor.service;

import com.example.mindful_mentor.dto.AppointmentDTO;
import com.example.mindful_mentor.dto.AppointmentDateUpdateDTO;
import com.example.mindful_mentor.dto.AppointmentStatusUpdateDTO;
import com.example.mindful_mentor.model.Appointment;
import com.example.mindful_mentor.model.AppointmentStatus;
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.AppointmentRepository;
import com.example.mindful_mentor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    public Appointment createAppointment(AppointmentDTO appointmentDTO) throws Exception {
        Optional<User> userOptional = userRepository.findById(appointmentDTO.getUserId());
        if (!userOptional.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOptional.get();
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setScheduledDate(appointmentDTO.getScheduledDate());
        appointment.setStatus(AppointmentStatus.REQUESTED);
        appointment.setReason(appointmentDTO.getReason());

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointmentStatus(UUID appointmentId, AppointmentStatusUpdateDTO statusUpdateDTO) throws Exception {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            throw new Exception("Appointment not found");
        }

        Appointment appointment = appointmentOptional.get();
        appointment.setStatus(statusUpdateDTO.getStatus());

        return appointmentRepository.save(appointment);
    }
    
    public Appointment updateAppointmentDate(UUID appointmentId, AppointmentDateUpdateDTO dateUpdateDTO) throws Exception {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            throw new Exception("Appointment not found");
        }

        Appointment appointment = appointmentOptional.get();
        appointment.setScheduledDate(dateUpdateDTO.getScheduledDate());

        return appointmentRepository.save(appointment);
    }
    
    // Method to get appointments with filters, sorting, and pagination
    public Page<Appointment> getAppointments(UUID userId, LocalDate startDate, LocalDate endDate, AppointmentStatus status, String sortBy, boolean sortAscending, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return appointmentRepository.findAppointmentsByFilters(userId, startDate, endDate, status, pageable);
    }
    
 // Delete an Appointment by ID
    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }
}
