package com.daaeboul.taskmanagementsystem.model.user;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_details")
@SQLDelete(sql = "UPDATE user_details SET deleted_at = NOW() WHERE id = ?")
public class UserDetails extends BaseEntity implements SoftDeletable {


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;


    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;


    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

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

    public UserDetails(String firstName, String lastName, String email, User user) {
        this.user = user;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
