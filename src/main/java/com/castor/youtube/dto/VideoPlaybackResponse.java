package com.castor.youtube.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoPlaybackResponse {
    private String videoId;
    private String playbackUrl;
    private String title;
    private String description;
} 