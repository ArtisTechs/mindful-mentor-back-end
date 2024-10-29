package com.example.mindful_mentor.service;

import com.example.mindful_mentor.dto.JournalDTO;
import com.example.mindful_mentor.exception.DateAlreadyTakenException;
import com.example.mindful_mentor.exception.UserNotFoundException;
import com.example.mindful_mentor.mapper.JournalMapper;
import com.example.mindful_mentor.model.Journal;
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.JournalRepository;
import com.example.mindful_mentor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a journal entry linked to a specific user
    public Journal createJournal(UUID userId, Journal journal) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            // Check if the date is already taken for this user
            if (journalRepository.existsByUserIdAndEntryDate(userId, journal.getEntryDate())) {
                throw new DateAlreadyTakenException("A journal entry with this date already exists.");
            }
            journal.setUser(user.get()); // Link the journal entry to the user
            return journalRepository.save(journal);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Get all journal entries with sorting, returning only JournalDTOs
    public List<JournalDTO> getAllJournals(String sortBy, boolean asc) {
        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        return journalRepository.findAll(Sort.by(direction, sortBy)).stream()
                .map(journal -> new JournalDTO(journal.getId(), journal.getEntryDate(), journal.getTitle(), journal.getMessage()))
                .collect(Collectors.toList());
    }

    // Get all journal entries for a specific user with sorting, returning only JournalDTOs
    public List<JournalDTO> getJournalsByUser(UUID userId, String sortBy, boolean asc) {
        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return journalRepository.findByUserId(userId, Sort.by(direction, sortBy)).stream()
                    .map(journal -> new JournalDTO(journal.getId(), journal.getEntryDate(), journal.getTitle(), journal.getMessage()))
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Get a single journal entry by its ID without including the user data
    public JournalDTO getJournalById(UUID id) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Journal with ID " + id + " not found."));
        return new JournalDTO(journal.getId(), journal.getEntryDate(), journal.getTitle(), journal.getMessage());
    }

 // Update an existing journal entry
    public JournalDTO updateJournal(UUID id, JournalDTO journalDTO) {
        // Fetch the existing journal entry using the ID
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Journal with ID " + id + " not found."));

        // Log the existing and new entry dates for debugging
        System.out.println("Existing entry date: " + existingJournal.getEntryDate());
        System.out.println("New entry date from DTO: " + journalDTO.getEntryDate());

        // Convert the new entry date from the JournalDTO to the same type as the existing entry date
        java.util.Date newEntryDate = journalDTO.getEntryDate(); // Assuming it's a java.util.Date
        java.sql.Timestamp newEntryTimestamp = new java.sql.Timestamp(newEntryDate.getTime());

        // If the entry date is different, check for existing entries with the new date for the same user
        if (!existingJournal.getEntryDate().equals(newEntryTimestamp)) {
            boolean dateTaken = journalRepository.existsByUserIdAndEntryDate(existingJournal.getUser().getId(), newEntryTimestamp);
            System.out.println("Is new date already taken? " + dateTaken);
            
            if (dateTaken) {
                throw new DateAlreadyTakenException("A journal entry with this date already exists.");
            }
            // Only update the entry date if it's changed and not taken
            existingJournal.setEntryDate(newEntryTimestamp);
        }

        // Update the journal fields with the new data from JournalDTO
        existingJournal.setTitle(journalDTO.getTitle());
        existingJournal.setMessage(journalDTO.getMessage());

        // Save the updated journal entry back to the database
        Journal updatedJournal = journalRepository.save(existingJournal);
        
        // Return the updated journal as a DTO
        return JournalMapper.toDTO(updatedJournal); // Convert the entity to DTO before returning
    }


    // Delete a journal entry by its ID
    public void deleteJournal(UUID id) {
        journalRepository.deleteById(id);
    }
}
