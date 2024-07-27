package com.example.AuthService.model.response;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String cpf;
    private String email;
    private String profilePicture;
    private Boolean isActive;
    private Boolean isStaff;
    private Boolean isSuper;
    private Boolean isNotLocked;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Group> groups;
    private List<Permission> permissions;
    private String[] authorities;
    private Set<String> authoritiesSet;
}
