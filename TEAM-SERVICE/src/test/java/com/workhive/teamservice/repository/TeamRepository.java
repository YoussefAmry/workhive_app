package com.workhive.teamservice.repository;

import com.workhive.teamservice.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}

