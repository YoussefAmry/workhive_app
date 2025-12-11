package com.workhive.userservice.grpc;

import com.workhive.userservice.domain.entity.User;
import com.workhive.userservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserStatisticsServiceImpl extends UserStatisticsServiceGrpc.UserStatisticsServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUserCount(UserCountRequest request, StreamObserver<UserCountResponse> responseObserver) {
        log.info("gRPC Request: getUserCount");

        long totalUsers = userRepository.count();
        // For simplicity, treat all users as active since there's no enabled field
        long activeUsers = totalUsers;

        UserCountResponse response = UserCountResponse.newBuilder()
                .setTotalUsers((int) totalUsers)
                .setActiveUsers((int) activeUsers)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("gRPC Response: total={}, active={}", totalUsers, activeUsers);
    }

    @Override
    public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("gRPC Request: getUserById for userId={}", request.getUserId());

        Optional<User> userOpt = userRepository.findById(request.getUserId());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
                    .setLastName(user.getLastName() != null ? user.getLastName() : "")
                    .setActive(true) // All users are considered active
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("gRPC Response: User found - {}", user.getUsername());
        } else {
            responseObserver.onError(new RuntimeException("User not found with id: " + request.getUserId()));
            log.error("gRPC Error: User not found with id={}", request.getUserId());
        }
    }
}
