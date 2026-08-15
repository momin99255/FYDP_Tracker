package com.momin.fydp_sync.repository;

import com.momin.fydp_sync.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
}