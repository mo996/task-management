package com.daaeboul.taskmanagementsystem.model.transition;

import com.daaeboul.taskmanagementsystem.model.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "workflow_step", uniqueConstraints = {@UniqueConstraint(name = "idx_workflow_sequence", columnNames = {"workflow_id", "sequence_number"})})
public class WorkflowStep extends BaseAuditEntity {

    @EmbeddedId
    private WorkflowStepId id;

    @MapsId("workflowId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", referencedColumnName = "id", nullable = false)
    private Workflow workflow;

    @MapsId("statusId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private Status status;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Data
    @NoArgsConstructor
    @Embeddable
    public static class WorkflowStepId implements Serializable {
        private Long workflowId;
        private Long statusId;
    }
}
