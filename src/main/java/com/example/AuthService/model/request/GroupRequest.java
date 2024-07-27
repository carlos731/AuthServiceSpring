package com.example.AuthService.model.request;

import com.example.AuthService.model.entity.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequest {
    private Long id;
    @NotNull(message = "name é requerido!")
    private String name;
    private List<Permission> permissions;
}
