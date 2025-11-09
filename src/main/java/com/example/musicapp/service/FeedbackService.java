package com.example.musicapp.service;

import com.example.musicapp.config.SecurityUtil;
import com.example.musicapp.dto.FeedbackRequestDTO;
import com.example.musicapp.dto.FeedbackResponseDTO;
import com.example.musicapp.entity.Account;
import com.example.musicapp.entity.AdminFeedback;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.AdminFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private AdminFeedbackRepository feedbackRepository;

    // --- Chức năng User ---
    @Transactional
    public FeedbackResponseDTO submitFeedback(FeedbackRequestDTO request) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();

        AdminFeedback feedback = new AdminFeedback();
        feedback.setAccount(currentUser);
        feedback.setContent(request.getContent());
        feedback.setStatus("PENDING");

        AdminFeedback savedFeedback = feedbackRepository.save(feedback);
        return mapToDTO(savedFeedback);
    }

    // --- Chức năng Admin ---
    public List<FeedbackResponseDTO> getAllFeedback() {
        // Sắp xếp theo chưa giải quyết -> đã giải quyết -> mới nhất
        return feedbackRepository.findAllByOrderByStatusAscCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponseDTO markAsResolved(Long id) {
        AdminFeedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy feedback ID: " + id));

        feedback.setStatus("PENDING");
        AdminFeedback updatedFeedback = feedbackRepository.save(feedback);
        return mapToDTO(updatedFeedback);
    }


    // --- Helper Mapper ---
    private FeedbackResponseDTO mapToDTO(AdminFeedback feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setContent(feedback.getContent());
        dto.setStatus(feedback.getStatus());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setUserEmail(feedback.getAccount().getEmail());
        return dto;
    }
}
