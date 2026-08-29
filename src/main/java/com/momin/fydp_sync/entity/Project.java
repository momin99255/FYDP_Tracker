package com.momin.fydp_sync.entity;

import com.momin.fydp_sync.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // 🔥 'creator' variable ta kete disi!

    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workspace> workspaces = new ArrayList<>();

    // Creator hok ba ordinary member, shobai ekhon ei list-er bhitor thakbe
    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    public String getCreatorName() {
        return members.stream()
                .filter(m -> m.getRole() == ProjectRole.CREATOR)
                .map(m -> m.getUser().getName())
                .findFirst()
                .orElse("Unknown");
    }

}