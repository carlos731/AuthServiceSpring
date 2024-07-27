package com.example.AuthService.model.request;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {
    private Long id;
    @NotNull(message = "codename é requerido!")
    private String codename;
    @NotNull(message = "name é requerido!")
    private String name;
    private List<Group> groups;
    private List<User> users;
}
