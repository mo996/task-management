package com.daaeboul.taskmanagementsystem.model.task;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Getter
@Setter
@Entity
@Table(name = "task_type")
public class TaskType extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "task_type_name", nullable = false, unique = true)
    private String taskTypeName;

    @Column(name = "description")
    private String description;
}
