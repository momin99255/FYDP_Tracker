package com.momin.fydp_sync.service;

import com.momin.fydp_sync.entity.Project;
import com.momin.fydp_sync.entity.User;

import java.util.List;

public interface ProjectService {

    List<Project> findProjectsForDashboard(User user);
}