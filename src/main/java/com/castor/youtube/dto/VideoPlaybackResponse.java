package com.castor.youtube.dto;

public class VideoPlaybackResponse {
    private String videoId;
    private String playbackUrl;
    private String title;
    private String description;

    // Getters y Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
    public String getPlaybackUrl() { return playbackUrl; }
    public void setPlaybackUrl(String playbackUrl) { this.playbackUrl = playbackUrl; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 