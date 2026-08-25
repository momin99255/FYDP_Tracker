package com.momin.fydp_sync.service;

import com.momin.fydp_sync.model.*;
import com.momin.fydp_sync.repository.WorkspaceColumnRepository;
import com.momin.fydp_sync.repository.WorkspaceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceColumnRepository columnRepository;
    private final WorkspaceRowRepository rowRepository;

    public List<WorkspaceColumn> getColumns(Workspace workspace) {
        return columnRepository.findByWorkspaceOrderByIdAsc(workspace);
    }

    public List<WorkspaceRow> getRows(Workspace workspace) {
        return rowRepository.findByWorkspaceOrderByCreatedAtDesc(workspace);
    }

    public WorkspaceColumn addColumn(Workspace workspace, String name) {
        WorkspaceColumn column = new WorkspaceColumn();
        column.setWorkspace(workspace);
        column.setName(name);
        return columnRepository.save(column);
    }

    // TRAIN_MODEL / LITERATURE_REVIEW - column-wise value diye notun row
    public WorkspaceRow addTableRow(Workspace workspace, User addedBy, Map<Long, String> cellValuesByColumnId, List<WorkspaceColumn> columns) {
        WorkspaceRow row = new WorkspaceRow();
        row.setWorkspace(workspace);
        row.setAddedBy(addedBy);
        row = rowRepository.save(row);

        for (WorkspaceColumn column : columns) {
            String val = cellValuesByColumnId.get(column.getId());
            WorkspaceCellValue cell = new WorkspaceCellValue();
            cell.setRow(row);
            cell.setColumn(column);
            cell.setValue(val != null ? val : "");
            row.getCellValues().add(cell);
        }
        return rowRepository.save(row);
    }

    // NOTES / LINKS - shudhu free text entry
    public WorkspaceRow addSimpleRow(Workspace workspace, User addedBy, String content) {
        WorkspaceRow row = new WorkspaceRow();
        row.setWorkspace(workspace);
        row.setAddedBy(addedBy);
        row.setContent(content);
        return rowRepository.save(row);
    }

    public WorkspaceRow getRow(Long rowId) {
        return rowRepository.findById(rowId).orElseThrow();
    }

    public void updateTableRow(WorkspaceRow row, Map<Long, String> cellValuesByColumnId) {
        for (WorkspaceCellValue cell : row.getCellValues()) {
            String val = cellValuesByColumnId.get(cell.getColumn().getId());
            if (val != null) cell.setValue(val);
        }
        rowRepository.save(row);
    }

    public void updateSimpleRow(WorkspaceRow row, String content) {
        row.setContent(content);
        rowRepository.save(row);
    }

    public void deleteRow(Long rowId) {
        rowRepository.deleteById(rowId);
    }
}