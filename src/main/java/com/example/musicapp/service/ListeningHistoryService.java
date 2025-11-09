package com.example.musicapp.service;

import com.example.musicapp.config.SecurityUtil;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.entity.Account;
import com.example.musicapp.entity.ListeningHistory;
import com.example.musicapp.entity.Song;
import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.repository.ListeningHistoryRepository;
import com.example.musicapp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListeningHistoryService {

    @Autowired
    private ListeningHistoryRepository historyRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService; // Dùng lại mapper

    // Lấy lịch sử (phân trang)
    public List<SongDTO> getMyHistory(int page, int size) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        Pageable pageable = PageRequest.of(page, size);

        Page<ListeningHistory> historyPage = historyRepository
                .findByAccountIdOrderByListenedAtDesc(currentUser.getId(), pageable);

        return historyPage.getContent().stream()
                .map(history -> songService.mapToDTO(history.getSong()))
                .collect(Collectors.toList());
    }

    // Ghi lại lịch sử
    @Transactional
    public void recordListening(Long songId) {
        Account currentUser = SecurityUtil.getCurrentUserAccount();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài hát ID: " + songId));

        ListeningHistory record = new ListeningHistory();
        record.setAccount(currentUser);
        record.setSong(song);
        record.setListenedAt(OffsetDateTime.now().toLocalDateTime());

        historyRepository.save(record);
    }
}
