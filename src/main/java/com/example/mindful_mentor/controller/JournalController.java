package com.example.mindful_mentor.controller;

import com.example.mindful_mentor.dto.JournalDTO; // Ensure you import JournalDTO
import com.example.mindful_mentor.exception.DateAlreadyTakenException;
import com.example.mindful_mentor.exception.ErrorCodes;
import com.example.mindful_mentor.exception.ErrorMessages;
import com.example.mindful_mentor.exception.ErrorResponse;
import com.example.mindful_mentor.model.Journal;
import com.example.mindful_mentor.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journals")
public class JournalController {

    @Autowired
    private JournalService journalService;

    // Endpoint to create a new journal entry for a specific user
    @PostMapping("/user/{userId}")
    public ResponseEntity<Journal> createJournal(
            @PathVariable UUID userId,
            @RequestBody Journal journalDTO) { // Use JournalDTO here
    	Journal createdJournal = journalService.createJournal(userId, journalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJournal); // Respond with 201 status
    }

    // Endpoint to get all journal entries with sorting and order
    @GetMapping
    public ResponseEntity<List<JournalDTO>> getAllJournals(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {
        List<JournalDTO> journals = journalService.getAllJournals(sortBy, asc);
        return ResponseEntity.ok(journals); // Return a ResponseEntity with the list of JournalDTO
    }

    // Endpoint to get all journal entries for a specific user with sorting and order
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JournalDTO>> getJournalsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {
        List<JournalDTO> journals = journalService.getJournalsByUser(userId, sortBy, asc);
        return ResponseEntity.ok(journals); // Return a ResponseEntity with the list of JournalDTO
    }

    // Endpoint to get a single journal entry by id
    @GetMapping("/{id}")
    public ResponseEntity<JournalDTO> getJournalById(@PathVariable UUID id) {
        JournalDTO journal = journalService.getJournalById(id); // Ensure this returns JournalDTO
        return ResponseEntity.ok(journal);
    }

 // Endpoint to update an existing journal entry
    @PutMapping("/{id}")
    public ResponseEntity<JournalDTO> updateJournal(
            @PathVariable UUID id,
            @RequestBody JournalDTO journalDTO) { // Use JournalDTO here
        
        // Call the service to update the journal entry
        JournalDTO updatedJournal = journalService.updateJournal(id, journalDTO); // Ensure the service updates correctly

        // Check if the updated journal is not null
        if (updatedJournal != null) {
            return ResponseEntity.ok(updatedJournal); // Return the updated JournalDTO
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }
    }


    // Endpoint to delete a journal entry by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournal(@PathVariable UUID id) {
        journalService.deleteJournal(id);
        return ResponseEntity.noContent().build();
    }

    // Exception handling for DateAlreadyTakenException
    @ExceptionHandler(DateAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> handleDateAlreadyTakenException(DateAlreadyTakenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ErrorCodes.DATE_ALREADY_TAKEN.getCode(),
                ErrorMessages.DATE_ALREADY_TAKEN.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
