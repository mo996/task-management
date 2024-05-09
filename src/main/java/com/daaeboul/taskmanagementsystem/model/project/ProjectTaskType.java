package com.daaeboul.taskmanagementsystem.model.project;

import com.daaeboul.taskmanagementsystem.model.BaseAuditEntity;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "project_task_type")
public class ProjectTaskType extends BaseAuditEntity {

    @EmbeddedId
    private ProjectTaskTypeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskTypeId")
    @JoinColumn(name = "task_type_id")
    private TaskType taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ProjectTaskTypeId implements Serializable {
        private Long projectId;
        private Long taskTypeId;
    }
}
