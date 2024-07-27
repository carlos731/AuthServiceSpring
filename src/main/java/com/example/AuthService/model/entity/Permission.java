package com.example.AuthService.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "permission_codename")
    private String codename;

    @Column(name = "permission_name")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private List<Group> groups;

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private List<User> users;
}
