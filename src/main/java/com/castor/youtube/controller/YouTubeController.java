package com.castor.youtube.controller;

import com.castor.youtube.service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.castor.youtube.dto.VideoPlaybackResponse;
import com.castor.youtube.dto.ChannelDetailsResponse;
import com.castor.youtube.repository.SearchHistoryRepository;
import com.castor.youtube.entity.User;
import com.castor.youtube.entity.SearchHistory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/youtube")
public class YouTubeController {

    @Autowired
    private YouTubeService youTubeService;
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    @Autowired
    private com.castor.youtube.repository.UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchVideos(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int maxResults) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("results", youTubeService.searchVideos(q, maxResults));
            // Guardar historial si hay resultados
            var results = (java.util.List<?>) response.get("results");
            if (!results.isEmpty() && results.get(0) instanceof com.castor.youtube.dto.YouTubeSearchResult r) {
                youTubeService.saveSearchHistory(q, r.getVideoId());
            }
            response.put("message", "Search completed successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>(); // Cambiado a Map<String, Object>
            error.put("message", "Error searching videos: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(500).body(error); // Ahora compatible
        }
    }

    @GetMapping("/play/{videoId}")
    public ResponseEntity<?> playVideo(@PathVariable String videoId) {
        try {
            VideoPlaybackResponse response = youTubeService.getVideoPlaybackInfo(videoId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error getting video playback info: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<?> getChannelDetails(@PathVariable String channelId) {
        try {
            ChannelDetailsResponse response = youTubeService.getChannelDetails(channelId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error getting channel details: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getSearchHistory() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
            User user = userRepository.findByUsername(username).orElseThrow();
            java.util.List<SearchHistory> history = searchHistoryRepository.findByUser(user);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error getting search history: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(500).body(error);
        }
    }
}