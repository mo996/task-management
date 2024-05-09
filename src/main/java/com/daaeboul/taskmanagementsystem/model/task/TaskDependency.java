package com.daaeboul.taskmanagementsystem.model.task;

import com.daaeboul.taskmanagementsystem.model.BaseAuditEntity;
import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "task_dependency")
public class TaskDependency extends BaseAuditEntity {

    @EmbeddedId
    private TaskDependencyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dependsOnTaskId")
    @JoinColumn(name = "depends_on_task_id")
    private Task dependsOnTask;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskDependencyId implements Serializable {
        private Long taskId;
        private Long dependsOnTaskId;
    }
}
