package com.example.AuthService.service;


import com.example.AuthService.model.dto.PermissionDTO;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    public PermissionDTO create(PermissionDTO permissionDTO);
    public List<PermissionDTO> findAll();
    public Optional<PermissionDTO> findById(Long id);
    public PermissionDTO update(Long id, PermissionDTO permissionDTO);
    public void delete(Long id);
}
