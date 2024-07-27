package com.example.AuthService.model.response;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private Long id;
    private String codename;
    private String name;
}
