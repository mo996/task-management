package com.daaeboul.taskmanagementsystem.model.task;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "task_priority")
public class TaskPriority extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "priority_name", nullable = false, unique = true, length = 50)
    private String priorityName;
}
