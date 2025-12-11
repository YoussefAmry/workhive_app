package com.workhive.projectservice.grpc;

import com.workhive.projectservice.domain.entity.Project;
import com.workhive.projectservice.repository.ProjectRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class ProjectStatisticsServiceImpl extends ProjectStatisticsServiceGrpc.ProjectStatisticsServiceImplBase {

    private final ProjectRepository projectRepository;

    @Override
    public void getProjectCount(ProjectCountRequest request, StreamObserver<ProjectCountResponse> responseObserver) {
        log.info("gRPC Request: getProjectCount");

        long totalProjects = projectRepository.count();
        // Count projects with tasks as in-progress
        long inProgressProjects = projectRepository.findAll().stream()
                .filter(p -> p.getTasks() != null && !p.getTasks().isEmpty())
                .count();

        ProjectCountResponse response = ProjectCountResponse.newBuilder()
                .setTotalProjects((int) totalProjects)
                .setInProgressProjects((int) inProgressProjects)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("gRPC Response: total={}, inProgress={}", totalProjects, inProgressProjects);
    }

    @Override
    public void getCompletedProjects(CompletedProjectsRequest request, StreamObserver<CompletedProjectsResponse> responseObserver) {
        log.info("gRPC Request: getCompletedProjects");

        // Treat projects with no tasks as completed for demo purposes
        long completedProjects = projectRepository.findAll().stream()
                .filter(p -> p.getTasks() == null || p.getTasks().isEmpty())
                .count();

        CompletedProjectsResponse response = CompletedProjectsResponse.newBuilder()
                .setCompletedProjects((int) completedProjects)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("gRPC Response: completed={}", completedProjects);
    }
}
