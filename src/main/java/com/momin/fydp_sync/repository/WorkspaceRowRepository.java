package com.momin.fydp_sync.repository;

import com.momin.fydp_sync.entity.Workspace;
import com.momin.fydp_sync.entity.WorkspaceRow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkspaceRowRepository extends JpaRepository<WorkspaceRow, Long> {
    List<WorkspaceRow> findByWorkspaceOrderByCreatedAtDesc(Workspace workspace);
}