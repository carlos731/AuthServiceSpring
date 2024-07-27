package com.example.AuthService.model.response;

import com.example.AuthService.model.entity.Permission;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private List<Permission> permissions;
}
