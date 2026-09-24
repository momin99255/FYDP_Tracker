package com.momin.fydp_sync.implementation;

import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.entity.Workspace;
import com.momin.fydp_sync.entity.WorkspaceCellValue;
import com.momin.fydp_sync.entity.WorkspaceColumn;
import com.momin.fydp_sync.entity.WorkspaceRow;
import com.momin.fydp_sync.repository.WorkspaceColumnRepository;
import com.momin.fydp_sync.repository.WorkspaceRowRepository;
import com.momin.fydp_sync.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceColumnRepository columnRepository;
    private final WorkspaceRowRepository rowRepository;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "workspaceColumns", key = "#workspace.id")
    public List<WorkspaceColumn> getColumns(Workspace workspace) {
        return columnRepository.findByWorkspaceOrderByIdAsc(workspace);
    }

    @Override
    @Cacheable(value = "workspaceRows", key = "#workspace.id")
    public List<WorkspaceRow> getRows(Workspace workspace) {
        return rowRepository.findByWorkspaceOrderByCreatedAtDesc(workspace);
    }

    @Override
    @CacheEvict(value = "workspaceColumns", key = "#workspace.id")
    public WorkspaceColumn addColumn(Workspace workspace, String name) {
        WorkspaceColumn column = new WorkspaceColumn();
        column.setWorkspace(workspace);
        column.setName(name);

        return columnRepository.save(column);
    }

    // TRAIN_MODEL / LITERATURE_REVIEW
    @Override
    @CacheEvict(value = "workspaceRows", key = "#workspace.id")
    public WorkspaceRow addTableRow(
            Workspace workspace,
            User addedBy,
            Map<Long, String> cellValuesByColumnId,
            List<WorkspaceColumn> columns) {

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

    // NOTES / LINKS
    @Override
    @CacheEvict(value = "workspaceRows", key = "#workspace.id")
    public WorkspaceRow addSimpleRow(
            Workspace workspace,
            User addedBy,
            String content) {

        WorkspaceRow row = new WorkspaceRow();
        row.setWorkspace(workspace);
        row.setAddedBy(addedBy);
        row.setContent(content);

        return rowRepository.save(row);
    }

    @Override
    public WorkspaceRow getRow(Long rowId) {
        return rowRepository.findById(rowId).orElseThrow();
    }

    @Override
    @CacheEvict(value = "workspaceRows", key = "#row.workspace.id")
    public void updateTableRow(
            WorkspaceRow row,
            Map<Long, String> cellValuesByColumnId) {

        for (WorkspaceCellValue cell : row.getCellValues()) {
            String val = cellValuesByColumnId.get(
                    cell.getColumn().getId()
            );

            if (val != null) {
                cell.setValue(val);
            }
        }

        rowRepository.save(row);
    }

    @Override
    @CacheEvict(value = "workspaceRows", key = "#row.workspace.id")
    public void updateSimpleRow(
            WorkspaceRow row,
            String content) {

        row.setContent(content);
        rowRepository.save(row);
    }

    @Override
    public void deleteRow(Long rowId) {

        WorkspaceRow row = rowRepository.findById(rowId).orElseThrow();

        Long workspaceId = row.getWorkspace().getId();

        rowRepository.deleteById(rowId);

        Cache cache = cacheManager.getCache("workspaceRows");

        if (cache != null) {
            cache.evict(workspaceId);
        }
    }
}