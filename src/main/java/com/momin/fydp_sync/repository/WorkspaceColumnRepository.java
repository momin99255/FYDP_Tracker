package com.momin.fydp_sync.repository;

import com.momin.fydp_sync.model.Workspace;
import com.momin.fydp_sync.model.WorkspaceColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkspaceColumnRepository extends JpaRepository<WorkspaceColumn, Long> {
    List<WorkspaceColumn> findByWorkspaceOrderByIdAsc(Workspace workspace);
}
