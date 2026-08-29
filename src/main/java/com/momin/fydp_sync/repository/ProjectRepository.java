package com.momin.fydp_sync.repository;


import com.momin.fydp_sync.entity.Project;
import com.momin.fydp_sync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user = :user")
    List<Project> findProjectsForDashboard(@Param("user") User user);
}