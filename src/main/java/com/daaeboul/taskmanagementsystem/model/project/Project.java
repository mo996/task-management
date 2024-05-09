package com.daaeboul.taskmanagementsystem.model.project;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "project")
@SQLDelete(sql = "UPDATE project SET deleted_at = NOW() WHERE id = ?")
public class Project extends BaseEntity implements SoftDeletable {

    @NotBlank
    @Size(max = 225)
    @Column(name = "project_name", nullable = false, unique = true)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "project_start_date")
    private LocalDateTime projectStartDate;

    @Column(name = "project_end_date")
    private LocalDateTime projectEndDate;

    @OneToMany(mappedBy = "project")
    private Set<ProjectTaskType> projectTaskTypes;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Override
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
