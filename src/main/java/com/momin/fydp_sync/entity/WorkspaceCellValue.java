package com.momin.fydp_sync.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workspace_cell_values")
public class WorkspaceCellValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_id", nullable = false)
    private WorkspaceRow row;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private WorkspaceColumn column;

    @Lob
    private String value;
}