package com.example.mindful_mentor.mapper;

import com.example.mindful_mentor.dto.JournalDTO;
import com.example.mindful_mentor.model.Journal;

public class JournalMapper {
    
    // Convert Journal to JournalDTO
    public static JournalDTO toDTO(Journal journal) {
        if (journal == null) {
            return null; // Handle null case
        }
        JournalDTO dto = new JournalDTO();
        dto.setId(journal.getId());
        dto.setTitle(journal.getTitle());
        dto.setMessage(journal.getMessage());
        dto.setEntryDate(journal.getEntryDate());
        // Map other fields as necessary
        return dto;
    }

    // Convert JournalDTO to Journal
    public static Journal toEntity(JournalDTO dto) {
        if (dto == null) {
            return null; // Handle null case
        }
        Journal journal = new Journal();
        journal.setId(dto.getId());
        journal.setTitle(dto.getTitle());
        journal.setMessage(dto.getMessage());
        journal.setEntryDate(dto.getEntryDate());
        // Map other fields as necessary
        return journal;
    }
}
