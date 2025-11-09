package com.example.musicapp.controller;

import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.service.ListeningHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class ListeningHistoryController {

    @Autowired
    private ListeningHistoryService historyService;

    // GET /api/history?page=0&size=20
    @GetMapping
    public ResponseEntity<List<SongDTO>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(historyService.getMyHistory(page, size));
    }

    // POST /api/history/record
    // Frontend sẽ gọi API này khi một bài hát bắt đầu phát
    @PostMapping("/record")
    public ResponseEntity<Void> recordListening(@RequestBody Map<String, Long> payload) {
        Long songId = payload.get("songId");
        if (songId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        historyService.recordListening(songId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
