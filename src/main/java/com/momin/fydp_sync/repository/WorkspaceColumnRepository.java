package com.momin.fydp_sync.repository;

import com.momin.fydp_sync.entity.Workspace;
import com.momin.fydp_sync.entity.WorkspaceColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkspaceColumnRepository extends JpaRepository<WorkspaceColumn, Long> {
    List<WorkspaceColumn> findByWorkspaceOrderByIdAsc(Workspace workspace);
}
