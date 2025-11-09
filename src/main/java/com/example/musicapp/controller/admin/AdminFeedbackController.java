package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.FeedbackResponseDTO;
import com.example.musicapp.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/feedback")
@PreAuthorize("hasRole('ADMIN')") // --- BẮT BUỘC QUYỀN ADMIN ---
public class AdminFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // GET /api/admin/feedback
    @GetMapping
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    // PUT /api/admin/feedback/1/resolve
    @PutMapping("/{id}/resolve")
    public ResponseEntity<FeedbackResponseDTO> markAsResolved(@PathVariable Long id) {
        FeedbackResponseDTO response = feedbackService.markAsResolved(id);
        return ResponseEntity.ok(response);
    }
}
