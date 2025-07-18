package com.castor.youtube.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "backend-castor-youtube-search";

    public List<YouTubeSearchResult> searchVideos(String query, int maxResults) throws GeneralSecurityException, IOException {
        try {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            
            YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();

            YouTube.Search.List search = youtube.search().list(Arrays.asList("snippet"));
        search.setKey(apiKey);
        search.setQ(query);
            search.setMaxResults((long) Math.min(maxResults, 50));
            search.setType(Arrays.asList("video"));

        SearchListResponse response = search.execute();
        List<SearchResult> searchResults = response.getItems();
        List<YouTubeSearchResult> results = new ArrayList<>();

        for (SearchResult result : searchResults) {
            YouTubeSearchResult youtubeResult = new YouTubeSearchResult();
            youtubeResult.setVideoId(result.getId().getVideoId());
            youtubeResult.setTitle(result.getSnippet().getTitle());
            youtubeResult.setDescription(result.getSnippet().getDescription());
                
                if (result.getSnippet().getThumbnails() != null && 
                    result.getSnippet().getThumbnails().getDefault() != null) {
            youtubeResult.setThumbnailUrl(result.getSnippet().getThumbnails().getDefault().getUrl());
                }
                
            youtubeResult.setChannelTitle(result.getSnippet().getChannelTitle());
            results.add(youtubeResult);
        }

        return results;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar videos en YouTube: " + e.getMessage(), e);
        }
    }
}

// Clase auxiliar YouTubeSearchResult
class YouTubeSearchResult {
    private String videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String channelTitle;

    // Getters y Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getChannelTitle() { return channelTitle; }
    public void setChannelTitle(String channelTitle) { this.channelTitle = channelTitle; }
}