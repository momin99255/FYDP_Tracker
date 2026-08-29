package com.momin.fydp_sync.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "workspace_rows")
public class WorkspaceRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_id", nullable = false)
    private User addedBy;

    // NOTES / LINKS type workspace-er jonno - shudhu free text entry
    @Lob
    private String content;

    // TRAIN_MODEL / LITERATURE_REVIEW type workspace-er jonno - column-wise value
    @OneToMany(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceCellValue> cellValues = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Template theke ${row.getValueForColumn(col.id)} diye direct call kora jabe
    public String getValueForColumn(Long columnId) {
        return cellValues.stream()
                .filter(cv -> cv.getColumn().getId().equals(columnId))
                .map(WorkspaceCellValue::getValue)
                .findFirst()
                .orElse("");
    }
}