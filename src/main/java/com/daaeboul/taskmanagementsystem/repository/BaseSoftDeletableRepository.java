package com.daaeboul.taskmanagementsystem.repository;

import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseSoftDeletableRepository<T extends SoftDeletable, ID extends Serializable> extends JpaRepository<T, ID> {

    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id")
    void hardDeleteById(@Param("id") ID id);
}
