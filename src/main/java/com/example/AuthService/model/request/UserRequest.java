package com.example.AuthService.model.request;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String cpf;
    private String email;
    private String password;
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
}
