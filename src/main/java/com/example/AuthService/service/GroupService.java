package com.example.AuthService.service;

import com.example.AuthService.model.dto.GroupDTO;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    public GroupDTO create(GroupDTO groupDTO);
    public List<GroupDTO> findAll();
    public Optional<GroupDTO> findById(Long id);
    public GroupDTO update(Long id, GroupDTO groupDTO);
    public void delete(Long id);
}
