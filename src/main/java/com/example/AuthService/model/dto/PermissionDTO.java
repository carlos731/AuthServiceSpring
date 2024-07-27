package com.example.AuthService.model.dto;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private Long id;
    private String codename;
    private String name;
    private List<Group> groups;
    private List<User> users;
}
