package com.daaeboul.taskmanagementsystem.model.user;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "deleted_user")
public class DeletedUser {

    @Id
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;
}