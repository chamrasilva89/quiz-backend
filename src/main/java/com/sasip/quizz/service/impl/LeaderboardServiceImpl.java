package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.LeaderboardFilterRequest;
import com.sasip.quizz.dto.LeaderboardResponse;
import com.sasip.quizz.model.Leaderboard;
import com.sasip.quizz.model.MonthlyLeaderboard;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.LeaderboardRepository;
import com.sasip.quizz.repository.MonthlyLeaderboardRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final MonthlyLeaderboardRepository monthlyLeaderboardRepository;
    private final UserRepository userRepository;

    @Autowired
    public LeaderboardServiceImpl(LeaderboardRepository leaderboardRepository,
                                  MonthlyLeaderboardRepository monthlyLeaderboardRepository,
                                  UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.monthlyLeaderboardRepository = monthlyLeaderboardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<LeaderboardResponse> getLeaderboard(LeaderboardFilterRequest request, Pageable pageable) {
        Page<?> leaderboardPage;
        int alYear = request.getAlYear();
        String district = request.getDistrict();
        String school = request.getSchool();
        String month = request.getMonth();

        // --- UPDATED LOGIC ---
        // Check if the filter strings are null or blank
        boolean isMonthly = (month != null && !month.isBlank());
        boolean hasDistrictFilter = (district != null && !district.isBlank());
        boolean hasSchoolFilter = (school != null && !school.isBlank());
        // --- END OF UPDATE ---

        // Logic to fetch the leaderboard page based on the presence of filters
        if (isMonthly) {
            if (hasDistrictFilter && hasSchoolFilter) {
                leaderboardPage = monthlyLeaderboardRepository.findByMonthAndAlYearAndDistrictAndSchool(
                        month, alYear, district, school, pageable);
            } else if (hasDistrictFilter) {
                leaderboardPage = monthlyLeaderboardRepository.findByMonthAndAlYearAndDistrict(
                        month, alYear, district, pageable);
            } else if (hasSchoolFilter) {
                leaderboardPage = monthlyLeaderboardRepository.findByMonthAndAlYearAndSchool(
                        month, alYear, school, pageable);
            } else {
                leaderboardPage = monthlyLeaderboardRepository.findByMonthAndAlYear(
                        month, alYear, pageable);
            }
        } else {
            if (hasDistrictFilter && hasSchoolFilter) {
                leaderboardPage = leaderboardRepository.findByAlYearAndDistrictAndSchoolOrderByTotalPointsDesc(
                        alYear, district, school, pageable);
            } else if (hasDistrictFilter) {
                leaderboardPage = leaderboardRepository.findByAlYearAndDistrictOrderByTotalPointsDesc(
                        alYear, district, pageable);
            } else if (hasSchoolFilter) {
                leaderboardPage = leaderboardRepository.findByAlYearAndSchoolOrderByTotalPointsDesc(
                        alYear, school, pageable);
            } else {
                leaderboardPage = leaderboardRepository.findByAlYearOrderByTotalPointsDesc(
                        alYear, pageable);
            }
        }

        List<LeaderboardResponse> responseList = new ArrayList<>();
        int startRank = pageable.getPageNumber() * pageable.getPageSize() + 1;

        List<?> entries = leaderboardPage.getContent();
        for (int i = 0; i < entries.size(); i++) {
            Object entry = entries.get(i);
            LeaderboardResponse res = new LeaderboardResponse();

            if (entry instanceof Leaderboard lb) {
                User user = userRepository.findByUsername(lb.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                res.setUserId(lb.getUserId());
                res.setUsername(user.getFirstName() + " " + user.getLastName());
                res.setSchool(lb.getSchool());
                res.setDistrict(lb.getDistrict());
                res.setAlYear(lb.getAlYear());
                res.setTotalPoints(lb.getTotalPoints());
                res.setAvatarUrl(user.getAvatarUrl());
                res.setProfileImageBase64(user.getProfileImageBase64());
            } else if (entry instanceof MonthlyLeaderboard mlb) {
                User user = userRepository.findByUsername(mlb.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                res.setUserId(mlb.getUserId());
                res.setUsername(user.getFirstName() + " " + user.getLastName());
                res.setSchool(mlb.getSchool());
                res.setDistrict(mlb.getDistrict());
                res.setAlYear(mlb.getAlYear());
                res.setTotalPoints(mlb.getTotalPoints());
                res.setProfileImageBase64(user.getProfileImageBase64());
                res.setAvatarUrl(user.getAvatarUrl());
            }

            res.setRank(startRank + i);
            responseList.add(res);
        }

        return new PageImpl<>(responseList, pageable, leaderboardPage.getTotalElements());
    }
}