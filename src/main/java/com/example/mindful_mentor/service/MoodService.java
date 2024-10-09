package com.example.mindful_mentor.service;

import com.example.mindful_mentor.dto.MoodDTO;
import com.example.mindful_mentor.dto.StudentWithMoodDTO;
import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.Mood;
import com.example.mindful_mentor.model.MoodDetail;
import com.example.mindful_mentor.model.Role;
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.MoodRepository;
import com.example.mindful_mentor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MoodService {

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private UserRepository userRepository;

    // Add Mood functionality
    public Mood addMood(MoodDTO moodDTO) throws Exception {
        Optional<User> userOptional = userRepository.findById(moodDTO.getUserId());
        if (!userOptional.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOptional.get();

        // Create Mood entity
        Mood mood = new Mood();
        mood.setUser(user);
        mood.setDate(moodDTO.getDate());

        // Set MoodDetail from MoodDTO
        MoodDetail moodDetail = moodDTO.getMood();
        if (moodDetail != null && moodDetail.getCode() != null && moodDetail.getDescription() != null) {
            mood.setMood(moodDetail); // Set the MoodDetail object with mood_code and description
        } else {
            throw new Exception("Mood code and description cannot be null");
        }

        // Save Mood
        return moodRepository.save(mood);
    }

    // Get Moods with filters
    public List<Mood> getFilteredMoods(UUID userId, String moodCode, LocalDate startDate, LocalDate endDate, boolean sortAscending) {
        Sort sort = sortAscending ? Sort.by("date").ascending() : Sort.by("date").descending();
        return moodRepository.findMoodsByFilters(userId, moodCode, startDate, endDate, sort);
    }
    
    public List<StudentWithMoodDTO> getStudentsWithMoodToday(String sortBy, boolean sortAscending, int page, int size) {
        Sort sort = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch active students with the role of 'STUDENT'
        Page<User> studentPage = userRepository.findByStatusAndRole(AccountStatus.ACTIVE, Role.STUDENT, pageable);
        List<User> students = studentPage.getContent();
        
        List<StudentWithMoodDTO> studentWithMoodDTOList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (User student : students) {
            StudentWithMoodDTO studentWithMoodDTO = new StudentWithMoodDTO();
            studentWithMoodDTO.setStudentId(student.getId());
            studentWithMoodDTO.setFirstName(student.getFirstName());
            studentWithMoodDTO.setMiddleName(student.getMiddleName());
            studentWithMoodDTO.setLastName(student.getLastName());
            studentWithMoodDTO.setEmail(student.getEmail());
            studentWithMoodDTO.setPhoneNumber(student.getPhoneNumber());
            studentWithMoodDTO.setStudentNumber(student.getStudentNumber());

            // Fetch mood for today (if exists)
            List<Mood> moods = moodRepository.findByUserIdAndDate(student.getId(), today);
            if (!moods.isEmpty()) {
                Mood mood = moods.get(0); // Use the first mood entry if there are multiple
                studentWithMoodDTO.setMoodDate(mood.getDate());
                studentWithMoodDTO.setMoodCode(mood.getMood().getCode());
                studentWithMoodDTO.setMoodDescription(mood.getMood().getDescription());
            } else {
                studentWithMoodDTO.setMoodDate(null);
                studentWithMoodDTO.setMoodCode(null);
                studentWithMoodDTO.setMoodDescription(null);
            }

            studentWithMoodDTOList.add(studentWithMoodDTO);
        }

        return studentWithMoodDTOList;
    }
}
