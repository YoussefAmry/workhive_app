package com.workhive.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String email;
    private String password;
}

