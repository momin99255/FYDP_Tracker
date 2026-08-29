package com.momin.fydp_sync.repository;

import com.momin.fydp_sync.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}