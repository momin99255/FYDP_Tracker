package com.momin.fydp_sync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "workspaces")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // Enum bebohar kore category set kora
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceCategory category;

    // Ei workspace-ta kon Project-er under-e ache
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Koto tarikhe toiri hoise (Eita optional, kintu rakhle UI te sundor dekhabe)
    private LocalDateTime createdAt = LocalDateTime.now();
}