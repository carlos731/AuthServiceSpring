package com.example.AuthService.service.impl;

import com.example.AuthService.exception.domain.DataIntegrityViolationException;
import com.example.AuthService.exception.domain.ObjectNotFoundException;
import com.example.AuthService.model.auth.UserAuthentication;
import com.example.AuthService.model.dto.UserDTO;
import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import com.example.AuthService.model.entity.User;
import com.example.AuthService.repository.GroupRepository;
import com.example.AuthService.repository.PermissionRepository;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.FileService;
import com.example.AuthService.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    ModelMapper mapper = new ModelMapper();
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if(user == null) {
            LOGGER.error("No exist user found by with username: " + username);
            throw new UsernameNotFoundException("No user found by username: " + username);
        }
        LOGGER.info("User login with username: " + username);
        UserAuthentication userAuthentication = new UserAuthentication(user);
        return userAuthentication;
    }

    @Override
    public UserDTO add(UserDTO userDTO, MultipartFile file) throws Exception {
        userDTO.setId(null);
        validaPorCpfEEmail(userDTO);

        User user = mapper.map(userDTO, User.class);

        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);

        user = userRepository.save(user);

        if (!file.isEmpty()) {
            String url = fileService.upload(file, userDTO.getUserName());
            user.setProfilePicture(url);
            user = userRepository.save(user);
        }

        userDTO.setId(user.getId());
        userDTO = mapper.map(user, UserDTO.class);
        userDTO.updateAuthoritiesFromGroups();

        LOGGER.info("Create user: " + user.getEmail());
        return userDTO;
    }

    @Override
    public UserDTO register(UserDTO userDTO, MultipartFile file) throws Exception {
        userDTO.setId(null);
        userDTO.setGroups(List.of(groupRepository.findByName("usuario")));
        validaPorCpfEEmail(userDTO);

        User user = mapper.map(userDTO, User.class);

        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);

        user = userRepository.save(user);

        if (!file.isEmpty()) {
            String url = fileService.upload(file, userDTO.getUserName());
            user.setProfilePicture(url);
            user = userRepository.save(user);
        }

        userDTO.setId(user.getId());
        userDTO = mapper.map(user, UserDTO.class);

        LOGGER.info("Create user: " + user.getEmail());
        return userDTO;
    }


    @Override
    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        LOGGER.info("Finds: " + users.size() + " users");
        return users.stream().map(user -> {
            UserDTO userDTO = mapper.map(user, UserDTO.class);
            userDTO.updateAuthoritiesFromGroups();
            return userDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User with id: '" + id + "' not found! ");
        }
        UserDTO userDTO = new ModelMapper().map(user.get(), UserDTO.class);
        userDTO.updateAuthoritiesFromGroups();
        LOGGER.info("Find user with id: " + userDTO.getFirstName());
        return Optional.of(userDTO);
    }

    @Override
    public Optional<UserDTO> findByCpf(String cpf) {
        Optional<User> user = userRepository.findByCpf(cpf);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User with cpf: '" + cpf + "' not found! ");
        }
        UserDTO userDTO = new ModelMapper().map(user.get(), UserDTO.class);
        return Optional.of(userDTO);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User with email: '" + email + "' not found! ");
        }
        UserDTO userDTO = new ModelMapper().map(user.get(), UserDTO.class);
        return Optional.of(userDTO);
    }

    @Override
    public String updateProfilePicture(String userName, MultipartFile file) throws Exception {
        Optional<User> userFind = userRepository.findByUserName(userName);
        if(userFind.isEmpty()){
            throw new ObjectNotFoundException("User with username: '" + userName + "' not found.");
        }

        String url = null;

        if (!file.isEmpty()) {
            url = fileService.upload(file, userName);
        } else {
            fileService.removeFileByUsername(userName);
        }

        userFind.get().setProfilePicture(url);
        userRepository.save(userFind.get());
        return url;
    }

    @Override
    public UserDTO update(Long id, UserDTO userDTO, MultipartFile file) throws Exception {
        Optional<User> userFind = userRepository.findById(id);
        if(userFind.isEmpty()){
            throw new ObjectNotFoundException("User with id: '" + id + "' not found.");
        }

        userDTO.setId(id);
        userDTO.setCreatedAt(userFind.get().getCreatedAt());
        userDTO.setPassword(userFind.get().getPassword());
        userDTO.setUserName(userFind.get().getUserName());
        userDTO.setProfilePicture(userFind.get().getProfilePicture());
        userDTO.updateAuthoritiesFromGroups();

        validaPorCpfEEmail(userDTO);
        User user = mapper.map(userDTO, User.class);

        userRepository.save(user);

        if (!file.isEmpty()) {
            String url = fileService.upload(file, userDTO.getUserName());
            user.setProfilePicture(url);
            user = userRepository.save(user);
        }

        LOGGER.info("Update user: " + user.getEmail());
        return userDTO;
    }

    @Override
    public void delete(Long id) {
        Optional<User> userFind = userRepository.findById(id);
        if(userFind.isEmpty()){
            throw new ObjectNotFoundException("User with id: '" + id + "' not found.");
        }
        LOGGER.info("Delete user: " + id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO updateNow(Long id, String firstName, String lastName, String cpf, String email, String profilePicture, Boolean isActive, Boolean isStaff, Boolean isSuper, List<Group> groups, List<Permission> permissions) {
        Optional<User> userFind = userRepository.findById(id);
        if(userFind.isEmpty()){
            throw new ObjectNotFoundException("User with id: '" + id + "' not found.");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setCpf(cpf);
        userDTO.setEmail(email);
        userDTO.setProfilePicture(profilePicture);
        userDTO.setLastSeen(userFind.get().getLastSeen());
        userDTO.setCreatedAt(userFind.get().getCreatedAt());
        userDTO.setUpdatedAt(LocalDateTime.now());
        userDTO.setIsActive(isActive);
        userDTO.setIsStaff(isStaff);
        userDTO.setIsSuper(isSuper);
        userDTO.setGroups(groups);
        userDTO.setPermissions(permissions);

        validaPorCpfEEmail(userDTO);

        User user = mapper.map(userDTO, User.class);
        userRepository.save(user);

        LOGGER.info("Update user: " + user.getEmail());
        return userDTO;
    }

    //validar cpf e email
    private void validaPorCpfEEmail(UserDTO userDTO) {
        Optional<User> obj = userRepository.findByCpf(userDTO.getCpf());
        if(obj.isPresent() && obj.get().getId() != userDTO.getId()) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
        }

        obj = userRepository.findByEmail(userDTO.getEmail());
        if(obj.isPresent() && obj.get().getId() != userDTO.getId()) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema!");
        }
    }

    private String extractUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return email; // ou lançar uma exceção se o email não for válido
    }
    
}
