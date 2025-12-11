package com.workhive.logsservice.grpc;

import com.workhive.logsservice.repository.ActivityLogRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class LogsStatisticsServiceImpl extends LogsStatisticsServiceGrpc.LogsStatisticsServiceImplBase {

    private final ActivityLogRepository activityLogRepository;

    @Override
    public void getActivityCount(ActivityCountRequest request, StreamObserver<ActivityCountResponse> responseObserver) {
        log.info("gRPC Request: getActivityCount");

        long totalActivities = activityLogRepository.count();
        
        // Count recent activities (last 24 hours)
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        long recentActivities = activityLogRepository.findAll().stream()
                .filter(log -> log.getTimestamp() != null && log.getTimestamp().isAfter(oneDayAgo))
                .count();

        ActivityCountResponse response = ActivityCountResponse.newBuilder()
                .setTotalActivities((int) totalActivities)
                .setRecentActivities((int) recentActivities)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("gRPC Response: total={}, recent={}", totalActivities, recentActivities);
    }
}
