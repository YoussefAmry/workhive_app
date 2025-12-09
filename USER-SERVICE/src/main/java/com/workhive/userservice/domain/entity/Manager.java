package com.workhive.userservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "managers")
public class Manager extends User {
    
    @Override
    public String getRole() {
        return "MANAGER";
    }
}
