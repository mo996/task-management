package com.daaeboul.taskmanagementsystem.model.project;

import com.daaeboul.taskmanagementsystem.model.BaseAuditEntity;
import com.daaeboul.taskmanagementsystem.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "project_user")
@NoArgsConstructor
public class ProjectUser extends BaseAuditEntity {

    @EmbeddedId
    private ProjectUserId id;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_role_id")
    private ProjectRole projectRole;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectUserId implements Serializable {
        private Long projectId;
        private Long userId;
    }
}
