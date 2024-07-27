package com.example.AuthService.controller;

import com.example.AuthService.exception.domain.ObjectNotFoundException;
import com.example.AuthService.jwt.JWTTokenProvider;
import com.example.AuthService.jwt.TokenService;
import com.example.AuthService.model.auth.UserAuthentication;
import com.example.AuthService.model.dto.UserDTO;
import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import com.example.AuthService.model.entity.User;
import com.example.AuthService.model.request.LoginRequest;
import com.example.AuthService.model.response.LoginResponse;
import com.example.AuthService.model.response.UserResponse;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.FileService;
import com.example.AuthService.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.AuthService.constants.SecurityConstants.JWT_TOKEN_HEADER;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    ModelMapper mapper = new ModelMapper();
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> Login(@RequestBody @Valid LoginRequest credentials) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
        User user = userRepository.findUserByEmail(credentials.getEmail());
        if (user == null) {
            throw new ObjectNotFoundException("Email ou senha inválidos!");
        }
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
        user.updateAuthoritiesFromGroups();
        var auth = authenticationManager.authenticate(usernamePassword);
        UserAuthentication userAuthentication = new UserAuthentication(user);
        HttpHeaders tokenHeaders = getJwtHeader(user);
        LoginResponse response = new LoginResponse();
        var tokenBody = jwtTokenProvider.generateJwtToken(user);
        response.setToken(tokenBody);
        return new ResponseEntity<>(response, tokenHeaders, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName,
            @RequestParam("cpf") String cpf,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture
    ) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setUserName(extractUsernameFromEmail(email));
        userDTO.setCpf(cpf);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setIsActive(true);
        userDTO.setIsStaff(false);
        userDTO.setIsSuper(false);
        userDTO.setIsNotLocked(true);
        userDTO = userService.register(userDTO, profilePicture);
        UserResponse response = mapper.map(userDTO, UserResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<UserResponse> add(
            @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName,
            @RequestParam("cpf") String cpf,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "isActive", required = false) boolean isActive,
            @RequestParam(value = "isStaff", required = false) boolean isStaff,
            @RequestParam(value = "isSuper", required = false) boolean isSuper,
            @RequestParam(value = "isNotLocked", required = true) boolean isNotLocked,
            @RequestParam(value = "groups", required = false) List<Group> groups,
            @RequestParam(value = "permissions", required = false) List<Permission> permissions
    ) throws Exception {
        //String encryptedPassword = new BCryptPasswordEncoder().encode(password);
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setUserName(extractUsernameFromEmail(email));
        userDTO.setCpf(cpf);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setIsActive(isActive);
        userDTO.setIsStaff(isStaff);
        userDTO.setIsSuper(isSuper);
        userDTO.setIsNotLocked(isNotLocked);
        userDTO.setGroups(groups);
        userDTO.setPermissions(permissions);
        userDTO = userService.add(userDTO, profilePicture);
        UserResponse response = mapper.map(userDTO, UserResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName,
            @RequestParam("username") String username,
            @RequestParam("cpf") String cpf,
            @RequestParam("email") String email,
            @RequestParam(value = "profilePictureCurrent", required = false) String profilePictureCurrent,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "isActive", required = false) boolean isActive,
            @RequestParam(value = "isStaff", required = false) boolean isStaff,
            @RequestParam(value = "isSuper", required = false) boolean isSuper,
            @RequestParam(value = "isNotLocked", required = true) boolean isNotLocked,
            @RequestParam(value = "groups", required = false) List<Group> groups,
            @RequestParam(value = "permissions", required = false) List<Permission> permissions
    ) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setUserName(username);
        userDTO.setCpf(cpf);
        userDTO.setEmail(email);
        userDTO.setProfilePicture(profilePictureCurrent);
        userDTO.setUpdatedAt(LocalDateTime.now());
        userDTO.setIsActive(isActive);
        userDTO.setIsStaff(isStaff);
        userDTO.setIsSuper(isSuper);
        userDTO.setIsNotLocked(isNotLocked);
        userDTO.setGroups(groups);
        userDTO.setPermissions(permissions);

        userDTO = userService.update(id, userDTO, profilePicture);

        UserResponse response = mapper.map(userDTO, UserResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updateProfilePicture/{username}")
    public ResponseEntity<String> updateProfilePicture(
            @PathVariable String username,
            @RequestParam(value = "profilePicture", required = true) MultipartFile profilePicture
    ) throws Exception {
        String url = userService.updateProfilePicture(username, profilePicture);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> findAll(){
        List<UserDTO> categoriesDTO = userService.findAll();
        ModelMapper mapper = new ModelMapper();
        List<UserResponse> categoriesResponse = categoriesDTO.stream().map(
                user -> mapper.map(user, UserResponse.class)).collect(Collectors.toList());
        return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Optional<UserResponse>> findById(@PathVariable Long id){
        Optional<UserDTO> userDTO = userService.findById(id);
        UserResponse userResponse = new ModelMapper().map(userDTO.get(), UserResponse.class);
        return new ResponseEntity<>(Optional.of(userResponse), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('user:delete')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public HttpHeaders getJwtHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private String extractUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return email; // ou lançar uma exceção se o email não for válido
    }

}
