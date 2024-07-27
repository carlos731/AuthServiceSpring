package com.example.AuthService.service.impl;

import com.example.AuthService.model.entity.Group;
import com.example.AuthService.model.entity.Permission;
import com.example.AuthService.model.entity.User;
import com.example.AuthService.repository.GroupRepository;
import com.example.AuthService.repository.PermissionRepository;
import com.example.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DbInitializerServiceImpl {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void initializeDefaultGroupsAndPermissions() {

        // Criação de permissões
        Permission permission1 = new Permission(null, "user:read", "Read users", null, null);
        Permission permission2 = new Permission(null, "user:create", "Add users", null, null);
        Permission permission3 = new Permission(null, "user:update", "Update users", null, null);
        Permission permission4 = new Permission(null, "user:delete", "Delete users", null, null);

        Permission permission5 = new Permission(null, "group:read", "Read groups", null, null);
        Permission permission6 = new Permission(null, "group:create", "Add groups", null, null);
        Permission permission7 = new Permission(null, "group:update", "Update groups", null, null);
        Permission permission8 = new Permission(null, "group:delete", "Delete groups", null, null);

        Permission permission9 = new Permission(null, "permission:read", "Read permissions", null, null);
        Permission permission10 = new Permission(null, "permission:create", "Add permissions", null, null);
        Permission permission11 = new Permission(null, "permission:update", "Update permissions", null, null);
        Permission permission12 = new Permission(null, "permission:delete", "Delete permissions", null, null);


        permissionRepository.saveAll(List.of(permission1, permission2, permission3, permission4, permission5, permission6, permission7, permission8, permission9, permission10, permission11, permission12));

        // Criação de grupos
        Group superUser = new Group(null, "superuser", List.of(permission1, permission2, permission3, permission4, permission5, permission6, permission7, permission8, permission9, permission10, permission11, permission12));
        Group adminGroup = new Group(null, "administrador", List.of(permission1, permission2, permission3, permission5, permission6, permission7, permission9, permission10, permission11));
        Group colaboradorGroup = new Group(null, "colaborador", List.of(permission1, permission2));
        Group userGroup = new Group(null, "usuario", List.of(permission1));
        Group visitanteGroup = new Group(null, "visitante", null);

        groupRepository.saveAll(List.of(superUser,adminGroup, colaboradorGroup, userGroup, visitanteGroup));

        // Criação do usuário
//        User user1 = new User();
//        user1.setFirstName("Carlos");
//        user1.setLastName("Henrique");
//        user1.setCpf("06369408700");
//        user1.setEmail("carlos.@gmail.com");
//        user1.setPassword("Teste@123");  // Use um hash seguro na prática
//        user1.setProfilePicture("profile_picture_url");
//        user1.setIsActive(true);
//        user1.setIsStaff(false);
//        user1.setIsSuper(true);
//        user1.setGroups(null);
//
//        User user2 = new User();
//        user2.setFirstName("John");
//        user2.setLastName("Doe");
//        user2.setCpf("12345678900");
//        user2.setEmail("john.doe@example.com");
//        user2.setPassword("Teste@123");  // Use um hash seguro na prática
//        user2.setProfilePicture("profile_picture_url");
//        user2.setIsActive(true);
//        user2.setIsStaff(false);
//        user2.setIsSuper(false);
//        user2.setGroups(List.of(adminGroup));
//        user2.setPermissions(List.of(permission4));
//
//        userRepository.saveAll(List.of(user1, user2));

    }

}
