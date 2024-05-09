package com.daaeboul.taskmanagementsystem.model.project;

import com.daaeboul.taskmanagementsystem.model.BaseAuditEntity;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "project_group")
public class ProjectGroup extends BaseAuditEntity {

    @EmbeddedId
    private ProjectGroupId id;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_role_id")
    private ProjectRole projectRole;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class ProjectGroupId implements Serializable {
        private Long projectId;
        private Long groupId;
    }
}
