package com.example.AuthService.model.dto;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
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
    private List<Group> groups = new ArrayList<>();;
    private List<Permission> permissions = new ArrayList<>();
    private String[] authorities;
    private Set<String> authoritiesSet;

    public void updateAuthoritiesFromGroups() {
        // Atualiza authorities com permissões diretas do usuário
        this.authorities = this.permissions.stream()
                .map(Permission::getCodename)
                .toArray(String[]::new);

        // Atualiza authoritiesSet com permissões diretas do usuário
        this.authoritiesSet = this.permissions.stream()
                .map(Permission::getCodename)
                .collect(Collectors.toSet());

        // Adiciona permissões dos grupos
        if (this.groups != null) {
            this.groups.forEach(group -> {
                group.getPermissions().forEach(permission -> {
                    this.authoritiesSet.add(permission.getCodename());
                });
            });

            // Atualiza authorities com as permissões dos grupos
            this.authorities = this.authoritiesSet.toArray(new String[0]);
        }
    }
}
