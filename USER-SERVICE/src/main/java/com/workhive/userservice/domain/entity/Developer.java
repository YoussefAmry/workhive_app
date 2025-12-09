package com.workhive.userservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "developers")
public class Developer extends User {
    
    @Override
    public String getRole() {
        return "DEVELOPER";
    }
}
