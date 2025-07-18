package com.castor.youtube.dto;

public class ChannelDetailsResponse {
    private String channelId;
    private String title;
    private String description;
    private Long subscriberCount;
    private String channelUrl;

    // Getters y Setters
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getSubscriberCount() { return subscriberCount; }
    public void setSubscriberCount(Long subscriberCount) { this.subscriberCount = subscriberCount; }
    public String getChannelUrl() { return channelUrl; }
    public void setChannelUrl(String channelUrl) { this.channelUrl = channelUrl; }
} 