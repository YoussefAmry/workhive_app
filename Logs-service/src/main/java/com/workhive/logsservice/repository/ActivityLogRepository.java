package com.workhive.logsservice.repository;

import com.workhive.logsservice.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserId(Long userId);

    List<ActivityLog> findByUsername(String username);

    List<ActivityLog> findByAction(String action);

    List<ActivityLog> findByEntityType(String entityType);

    List<ActivityLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<ActivityLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM ActivityLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :startDate AND :endDate")
    List<ActivityLog> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT a FROM ActivityLog a WHERE a.action = :action AND a.timestamp BETWEEN :startDate AND :endDate")
    List<ActivityLog> findByActionAndDateRange(
            @Param("action") String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<ActivityLog> findTop50ByOrderByTimestampDesc();

    List<ActivityLog> findTop50ByUserIdOrderByTimestampDesc(Long userId);
}
