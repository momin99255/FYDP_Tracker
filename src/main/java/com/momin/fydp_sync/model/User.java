package com.momin.fydp_sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name Is Required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email Is Required")
    @Column(nullable = false,unique = true)
    private String email;

    @NotBlank(message = "Choose an Unique Username")
    @Column(nullable = false,unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "Password strongly required!")
    @Column(nullable = false)
    private String password;

//    @JsonIgnore
//    @OneToMany(mappedBy = "creator")
//    private List<Project> projects = new ArrayList<>();
}
