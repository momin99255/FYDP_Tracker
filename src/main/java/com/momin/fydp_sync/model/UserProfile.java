package com.momin.fydp_sync.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "contact_no")
    private String contactNo;

    private String university;
    private String department;
    private String batch;
}