package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.LeaderboardFilterRequest;
import com.sasip.quizz.dto.LeaderboardResponse;
import com.sasip.quizz.model.Leaderboard;
import com.sasip.quizz.repository.LeaderboardRepository;
import com.sasip.quizz.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    @Autowired
    public LeaderboardServiceImpl(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @Override
    public Page<LeaderboardResponse> getLeaderboard(LeaderboardFilterRequest request, Pageable pageable) {
        Page<Leaderboard> leaderboardPage;

        int alYear = request.getAlYear();
        String district = request.getDistrict();
        String school = request.getSchool();

        // Apply filtering based on available fields
        if (district != null && school != null) {
            leaderboardPage = leaderboardRepository.findByAlYearAndDistrictAndSchoolOrderByTotalPointsDesc(
                    alYear, district, school, pageable);
        } else if (district != null) {
            leaderboardPage = leaderboardRepository.findByAlYearAndDistrictOrderByTotalPointsDesc(
                    alYear, district, pageable);
        } else if (school != null) {
            leaderboardPage = leaderboardRepository.findByAlYearAndSchoolOrderByTotalPointsDesc(
                    alYear, school, pageable);
        } else {
            leaderboardPage = leaderboardRepository.findByAlYearOrderByTotalPointsDesc(
                    alYear, pageable);
        }

        // Convert to LeaderboardResponse list with ranks
        List<LeaderboardResponse> responseList = new ArrayList<>();
        int startRank = pageable.getPageNumber() * pageable.getPageSize() + 1;

        for (int i = 0; i < leaderboardPage.getContent().size(); i++) {
            Leaderboard entry = leaderboardPage.getContent().get(i);
            LeaderboardResponse res = new LeaderboardResponse();
            res.setUserId(entry.getUserId());
            res.setUsername(entry.getUsername());
            res.setSchool(entry.getSchool());
            res.setDistrict(entry.getDistrict());
            res.setAlYear(entry.getAlYear());
            res.setTotalPoints(entry.getTotalPoints());
            res.setRank(startRank + i);
            responseList.add(res);
        }

        return new PageImpl<>(responseList, pageable, leaderboardPage.getTotalElements());
    }
}
