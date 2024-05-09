package com.daaeboul.taskmanagementsystem.model;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@SQLDelete(sql = "UPDATE entity_name SET deleted_at = NOW() WHERE id = ?")
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedFilter", condition = "deleted_at is null")
public interface SoftDeletable {
    LocalDateTime getDeletedAt();
    void setDeletedAt(LocalDateTime deletedAt);

}