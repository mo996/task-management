package com.daaeboul.taskmanagementsystem.model.transition;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Table(name = "status")
public class Status extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "status_name", nullable = false, unique = true, length = 50)
    private String statusName;

    @Column(name = "description")
    private String description;
}
