package com.castor.youtube.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.castor.youtube.dto.YouTubeSearchResult;
import com.castor.youtube.dto.VideoPlaybackResponse;
import com.castor.youtube.dto.ChannelDetailsResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.castor.youtube.repository.SearchHistoryRepository;
import com.castor.youtube.repository.UserRepository;
import com.castor.youtube.entity.SearchHistory;
import com.castor.youtube.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
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

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    public YouTubeService(SearchHistoryRepository searchHistoryRepository, UserRepository userRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.userRepository = userRepository;
    }

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

    public VideoPlaybackResponse getVideoPlaybackInfo(String videoId) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        YouTube.Videos.List request = youtube.videos().list(Arrays.asList("snippet"));
        request.setKey(apiKey);
        request.setId(Arrays.asList(videoId));
        VideoListResponse response = request.execute();
        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Video not found");
        }
        Video video = response.getItems().get(0);
        VideoPlaybackResponse playback = new VideoPlaybackResponse();
        playback.setVideoId(videoId);
        playback.setPlaybackUrl("https://www.youtube.com/watch?v=" + videoId);
        playback.setTitle(video.getSnippet().getTitle());
        playback.setDescription(video.getSnippet().getDescription());
        return playback;
    }

    public ChannelDetailsResponse getChannelDetails(String channelId) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        YouTube.Channels.List request = youtube.channels().list(Arrays.asList("snippet", "statistics"));
        request.setKey(apiKey);
        request.setId(Arrays.asList(channelId));
        ChannelListResponse response = request.execute();
        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Channel not found");
        }
        Channel channel = response.getItems().get(0);
        ChannelDetailsResponse details = new ChannelDetailsResponse();
        details.setChannelId(channelId);
        details.setTitle(channel.getSnippet().getTitle());
        details.setDescription(channel.getSnippet().getDescription());
        details.setSubscriberCount(channel.getStatistics().getSubscriberCount() != null ? channel.getStatistics().getSubscriberCount().longValue() : null);
        details.setChannelUrl("https://www.youtube.com/channel/" + channelId);
        return details;
    }

    public void saveSearchHistory(String query, String videoId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
        User user = userRepository.findByUsername(username).orElseThrow();
        SearchHistory history = new SearchHistory();
        history.setUser(user);
        history.setQuery(query);
        history.setVideoId(videoId);
        history.setSearchedAt(LocalDateTime.now());
        searchHistoryRepository.save(history);
    }
}