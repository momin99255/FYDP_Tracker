package com.momin.fydp_sync.implementation;

import com.momin.fydp_sync.entity.Project;
import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.repository.ProjectRepository;
import com.momin.fydp_sync.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    @Cacheable(value = "dashboardProjects", key = "#user.id")
    public List<Project> findProjectsForDashboard(User user) {
        return projectRepository.findProjectsForDashboard(user);
    }
}