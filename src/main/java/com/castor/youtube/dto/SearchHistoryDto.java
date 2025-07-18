package com.castor.youtube.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryDto {
    private Long id;
    private String query;
    private String videoId;
    private LocalDateTime searchedAt;
} 