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
@Table(name = "category")
public class Category extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Size(max = 225)
    @Column(name = "description", length = 255)
    private String description;
}
