package com.sasip.quizz.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sasip.quizz.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
     boolean existsByPhone(String phone);
    @Query("SELECT u FROM User u WHERE " +
        "(:role IS NULL OR u.role = :role) AND " +
        "(:name IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
        "(:school IS NULL OR u.school = :school) AND " +
        "(:alYear IS NULL OR u.alYear = :alYear) AND " +
        "(:district IS NULL OR u.district = :district) AND " +
        "(:userStatus IS NULL OR u.userStatus = :userStatus)")
        Page<User> filterUsersWithPagination(
                @Param("role") String role,
                @Param("name") String name,
                @Param("school") String school,
                @Param("alYear") Integer alYear,
                @Param("district") String district,
                @Param("userStatus") String userStatus,
                Pageable pageable
        );

        Page<User> findByRole(String role, Pageable pageable);
        Page<User> findByRoleNot(String role, Pageable pageable);
        List<User> findByAlYear(Integer alYear);

@Query("SELECT COUNT(u) FROM User u WHERE u.alYear = :alYear AND u.userStatus = :userStatus")
int countUsersByAlYearAndUserStatus(@Param("alYear") String alYear, @Param("userStatus") String userStatus);

    @Query("SELECT DISTINCT ub.user FROM UserBadge ub")
    Page<User> findUsersWithBadges(Pageable pageable);
    
}