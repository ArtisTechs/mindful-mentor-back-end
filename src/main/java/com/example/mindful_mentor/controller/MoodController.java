package com.example.mindful_mentor.controller;

import com.example.mindful_mentor.dto.MoodDTO;
import com.example.mindful_mentor.dto.StudentWithMoodDTO;
import com.example.mindful_mentor.model.Mood;
import com.example.mindful_mentor.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    @Autowired
    private MoodService moodService;

    // Add a new Mood
    @PostMapping("/add")
    public Mood addMood(@RequestBody MoodDTO moodDTO) throws Exception {
        return moodService.addMood(moodDTO);
    }

    // Get Moods with filters
    @GetMapping
    public List<Mood> getMoods(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) String moodCode,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(defaultValue = "true") boolean sortAscending
    ) {
        return moodService.getFilteredMoods(userId, moodCode, startDate, endDate, sortAscending);
    }
    
    @GetMapping("/students-with-mood-today")
    public List<StudentWithMoodDTO> getStudentsWithMoodToday(
            @RequestParam(defaultValue = "lastName") String sortBy,  // default sorting by firstName
            @RequestParam(defaultValue = "true") boolean sortAscending, // default ascending order
            @RequestParam(defaultValue = "0") int page, // default page number
            @RequestParam(defaultValue = "10") int size, // default page size
            @RequestParam(defaultValue = "false") boolean ignorePagination // new parameter to ignore pagination
    ) {
        return moodService.getStudentsWithMoodToday(sortBy, sortAscending, page, size, ignorePagination);
    }
    
 // Update mood by ID
    @PutMapping("/update/{id}")
    public Mood updateMoodById(
        @PathVariable UUID id, 
        @RequestBody MoodDTO moodDTO) throws Exception {
        return moodService.updateMoodById(id, moodDTO);
    }
}
