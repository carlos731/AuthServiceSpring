package com.example.AuthService.service;

import com.example.AuthService.model.dto.UserDTO;
import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {

    public UserDTO add(UserDTO userDTO, MultipartFile file) throws Exception;
    public UserDTO register(UserDTO userDTO, MultipartFile file) throws Exception;
    public List<UserDTO> findAll();
    public UserDTO update(Long id, UserDTO userDTO, MultipartFile file) throws Exception;
    public UserDTO updateNow(Long id, String firstName, String lastName, String cpf, String email, String profilePicture, Boolean isActive, Boolean isStaff, Boolean isSuper, List<Group> groups, List<Permission> permissions);
    public Optional<UserDTO> findById(Long id);
    public Optional<UserDTO> findByCpf(String cpf);
    public Optional<UserDTO> findByEmail(String email);
    public String updateProfilePicture(String userName, MultipartFile file) throws Exception;
    public void delete(Long id);
}
