package com.sasip.quizz.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.dto.LeaderboardFilterRequest;
import com.sasip.quizz.dto.LeaderboardResponse;

public interface LeaderboardService {
   Page<LeaderboardResponse> getLeaderboard(LeaderboardFilterRequest request, Pageable pageable);


}
