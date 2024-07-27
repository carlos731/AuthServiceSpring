package com.example.AuthService.service.impl;

import com.example.AuthService.exception.domain.ObjectNotFoundException;
import com.example.AuthService.model.dto.GroupDTO;
import com.example.AuthService.model.dto.PermissionDTO;
import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import com.example.AuthService.repository.GroupRepository;
import com.example.AuthService.service.GroupService;
import com.example.AuthService.service.PermissionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    ModelMapper mapper = new ModelMapper();
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionService permissionService;

    @Override
    public GroupDTO create(GroupDTO groupDTO) {
        groupDTO.setId(null);
        Group group = mapper.map(groupDTO, Group.class);

        for (int i = 0; i < groupDTO.getPermissions().size(); i++) {
            Long permissionId = group.getPermissions().get(i).getId();
            Optional<PermissionDTO> permissionDTO = permissionService.findById(permissionId);

            // Verifica se a permissão foi encontrada
            if (permissionDTO.isPresent()) {
                PermissionDTO dto = permissionDTO.get();
                groupDTO.getPermissions().get(i).setName(dto.getName());
                groupDTO.getPermissions().get(i).setCodename(dto.getCodename());
            } else {
                // Trate o caso em que a permissão não foi encontrada
                LOGGER.warn("Permission with ID " + permissionId + " not found.");
                throw new ObjectNotFoundException("Permission with id: '" + permissionId + "' not found! ");
            }
        }

        group = groupRepository.save(group);
        groupDTO.setId(group.getId());
        LOGGER.info("Create group: " + group.getName());
        return groupDTO;
    }

    @Override
    public List<GroupDTO> findAll() {
        List<Group> categories = groupRepository.findAll();
        LOGGER.info("Finds: " + categories.size() + " groups");
        return categories.stream().map(
                group -> new ModelMapper().map(group, GroupDTO.class)).collect((Collectors.toList()));
    }

    @Override
    public Optional<GroupDTO> findById(Long id) {
        Optional<Group> group = groupRepository.findById(id);
        if (group.isEmpty()) {
            throw new ObjectNotFoundException("Group with id: '" + id + "' not found! ");
        }
        GroupDTO groupDTO = new ModelMapper().map(group.get(), GroupDTO.class);
        LOGGER.info("Find group with id: " + groupDTO.getId());
        return Optional.of(groupDTO);
    }

    @Override
    public GroupDTO update(Long id, GroupDTO groupDTO) {
        Optional<Group> groupFind = groupRepository.findById(id);
        if(groupFind.isEmpty()){
            throw new ObjectNotFoundException("Group with id: '" + id + "' not found.");
        }
        groupDTO.setId(id);
        ModelMapper mapper = new ModelMapper();
        Group group = mapper.map(groupDTO, Group.class);

        for (int i = 0; i < groupDTO.getPermissions().size(); i++) {
            Long permissionId = group.getPermissions().get(i).getId();
            Optional<PermissionDTO> permissionDTO = permissionService.findById(permissionId);

            // Verifica se a permissão foi encontrada
            if (permissionDTO.isPresent()) {
                PermissionDTO dto = permissionDTO.get();
                groupDTO.getPermissions().get(i).setName(dto.getName());
                groupDTO.getPermissions().get(i).setCodename(dto.getCodename());
            } else {
                // Trate o caso em que a permissão não foi encontrada
                LOGGER.warn("Permission with ID " + permissionId + " not found.");
                throw new ObjectNotFoundException("Permission with id: '" + permissionId + "' not found! ");
            }
        }

        groupRepository.save(group);
        LOGGER.info("Update group: " + group.getId());
        return groupDTO;
    }

    @Override
    public void delete(Long id) {
        Optional<Group> groupFind = groupRepository.findById(id);
        if(groupFind.isEmpty()){
            throw new ObjectNotFoundException("Group with id: '" + id + "' not found.");
        }
        LOGGER.info("Delete group: " + id);
        groupRepository.deleteById(id);
    }

}
