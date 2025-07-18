package com.castor.youtube.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDetailsResponse {
    private String channelId;
    private String title;
    private String description;
    private Long subscriberCount;
    private String channelUrl;
} 