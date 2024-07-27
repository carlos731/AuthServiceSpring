package com.example.AuthService.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_first_name")
    private String firstName;

    @Column(name = "user_last_name")
    private String lastName;

    @Column(name = "user_username")
    private String userName;

    //@CPF
    @Column(name = "user_cpf", unique = true)
    private String cpf;

    //@Email
    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "user_password", length = 60)
    private String password;

    @Column(name = "user_profile_picture")
    private String profilePicture;

    @Column(name = "user_is_active")
    private Boolean isActive;

    @Column(name = "user_is_staff")
    private Boolean isStaff;

    @Column(name = "user_is_super")
    private Boolean isSuper;

    @Column(name = "user_locked")
    private Boolean isNotLocked;

    @Column(name = "user_last_seen")
    private LocalDateTime lastSeen;

    //@CreationTimestamp
    @Column(name = "user_created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    //@UpdateTimestamp
    @Column(name = "user_updated_at")
    private LocalDateTime  updatedAt;

    @ManyToMany
    @JoinTable(
            name = "auth_user_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    )
    private List<Group> groups;

    @ManyToMany
    @JoinTable(
            name = "auth_user_permissions",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "permission_id")
    )
    private List<Permission> permissions;

    @JsonIgnore
    @Transient
    private String[] authorities;

    @JsonIgnore
    @Transient
    private Set<String> authoritiesSet;

    @JsonIgnore
    @Transient
    private String[] roles;

    @PrePersist
    public void prePersist() {
        if (this.email != null && !this.email.isEmpty()) {
            this.userName = extractUserNameFromEmail(this.email);
        }
    }

    private String extractUserNameFromEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        }
        return email; // Caso não encontre '@', retorna o email completo
    }

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
            Set<String> groupNames = this.groups.stream()
                    .map(Group::getName)
                    .collect(Collectors.toSet());

            this.groups.forEach(group -> {
                group.getPermissions().forEach(permission -> {
                    this.authoritiesSet.add(permission.getCodename());
                });
            });

            // Atualiza authorities com as permissões dos grupos
            this.authorities = this.authoritiesSet.toArray(new String[0]);

            // Atualiza roles com os nomes dos grupos
            this.roles = groupNames.toArray(new String[0]);
        }
    }

}
