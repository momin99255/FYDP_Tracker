package com.momin.fydp_sync.service;

import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.entity.Workspace;
import com.momin.fydp_sync.entity.WorkspaceColumn;
import com.momin.fydp_sync.entity.WorkspaceRow;

import java.util.List;
import java.util.Map;

public interface WorkspaceService {

    List<WorkspaceColumn> getColumns(Workspace workspace);

    List<WorkspaceRow> getRows(Workspace workspace);

    WorkspaceColumn addColumn(Workspace workspace, String name);

    WorkspaceRow addTableRow(Workspace workspace, User addedBy, Map<Long, String> cellValuesByColumnId, List<WorkspaceColumn> columns);

    WorkspaceRow addSimpleRow(Workspace workspace, User addedBy, String content);

    WorkspaceRow getRow(Long rowId);

    void updateTableRow(WorkspaceRow row, Map<Long, String> cellValuesByColumnId);

    void updateSimpleRow(WorkspaceRow row, String content);

    void deleteRow(Long rowId);
}