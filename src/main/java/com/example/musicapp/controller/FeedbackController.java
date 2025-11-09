package com.example.musicapp.controller;

import com.example.musicapp.dto.FeedbackRequestDTO;
import com.example.musicapp.dto.FeedbackResponseDTO;
import com.example.musicapp.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // POST /api/feedback (User gửi)
    @PostMapping
    public ResponseEntity<
            FeedbackResponseDTO> submitFeedback(@Valid @RequestBody FeedbackRequestDTO request) {
        FeedbackResponseDTO response = feedbackService.submitFeedback(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
