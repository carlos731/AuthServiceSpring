package com.example.AuthService.service.impl;

import com.example.AuthService.exception.domain.ObjectNotFoundException;
import com.example.AuthService.model.dto.PermissionDTO;
import com.example.AuthService.model.entity.Permission;
import com.example.AuthService.repository.PermissionRepository;
import com.example.AuthService.service.PermissionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    ModelMapper mapper = new ModelMapper();
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public PermissionDTO create(PermissionDTO permissionDTO) {
        permissionDTO.setId(null);
        Permission permission = mapper.map(permissionDTO, Permission.class);
        permission = permissionRepository.save(permission);
        permissionDTO.setId(permission.getId());
        LOGGER.info("Create permission: " + permission.getName());
        return permissionDTO;
    }

    @Override
    public List<PermissionDTO> findAll() {
        List<Permission> categories = permissionRepository.findAll();
        LOGGER.info("Finds: " + categories.size() + " permissions");
        return categories.stream().map(
                permission -> new ModelMapper().map(permission, PermissionDTO.class)).collect((Collectors.toList()));
    }

    @Override
    public Optional<PermissionDTO> findById(Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        if (permission.isEmpty()) {
            throw new ObjectNotFoundException("Permission with id: '" + id + "' not found! ");
        }
        PermissionDTO permissionDTO = new ModelMapper().map(permission.get(), PermissionDTO.class);
        LOGGER.info("Find permission with id: " + permissionDTO.getId());
        return Optional.of(permissionDTO);
    }

    @Override
    public PermissionDTO update(Long id, PermissionDTO permissionDTO) {
        Optional<Permission> permissionFind = permissionRepository.findById(id);
        if(permissionFind.isEmpty()){
            throw new ObjectNotFoundException("Permission with id: '" + id + "' not found.");
        }
        permissionDTO.setId(id);
        ModelMapper mapper = new ModelMapper();
        Permission permission = mapper.map(permissionDTO, Permission.class);
        permissionRepository.save(permission);
        LOGGER.info("Update permission: " + permission.getId());
        return permissionDTO;
    }

    @Override
    public void delete(Long id) {
        Optional<Permission> permissionFind = permissionRepository.findById(id);
        if(permissionFind.isEmpty()){
            throw new ObjectNotFoundException("Permission with id: '" + id + "' not found.");
        }
        LOGGER.info("Delete permission: " + id);
        permissionRepository.deleteById(id);
    }
}
