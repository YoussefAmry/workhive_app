package com.workhive.teamservice.grpc;

import com.workhive.teamservice.domain.entity.Team;
import com.workhive.teamservice.repository.TeamRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class TeamStatisticsServiceImpl extends TeamStatisticsServiceGrpc.TeamStatisticsServiceImplBase {

    private final TeamRepository teamRepository;

    @Override
    public void getTeamCount(TeamCountRequest request, StreamObserver<TeamCountResponse> responseObserver) {
        log.info("gRPC Request: getTeamCount");

        long totalTeams = teamRepository.count();

        TeamCountResponse response = TeamCountResponse.newBuilder()
                .setTotalTeams((int) totalTeams)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("gRPC Response: total={}", totalTeams);
    }

    @Override
    public void getAverageTeamSize(AverageTeamSizeRequest request, StreamObserver<AverageTeamSizeResponse> responseObserver) {
        log.info("gRPC Request: getAverageTeamSize");

        double averageSize = teamRepository.findAll().stream()
                .mapToInt(team -> team.getDeveloperIds() != null ? team.getDeveloperIds().size() : 0)
                .average()
                .orElse(0.0);

        AverageTeamSizeResponse response = AverageTeamSizeResponse.newBuilder()
                .setAverageSize(averageSize)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("gRPC Response: avgSize={}", averageSize);
    }
}
