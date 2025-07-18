package com.castor.youtube.repository;

import com.castor.youtube.entity.SearchHistory;
import com.castor.youtube.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUser(User user);
} 