package com.castor.youtube.controller;

import com.castor.youtube.service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.castor.youtube.dto.VideoPlaybackResponse;
import com.castor.youtube.dto.ChannelDetailsResponse;

@RestController
@RequestMapping("/api/youtube")
public class YouTubeController {

    @Autowired
    private YouTubeService youTubeService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchVideos(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int maxResults) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("results", youTubeService.searchVideos(q, maxResults));
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
}