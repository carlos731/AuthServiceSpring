package com.example.AuthService.controller;

import com.example.AuthService.model.dto.PermissionDTO;
import com.example.AuthService.model.request.PermissionRequest;
import com.example.AuthService.model.response.PermissionResponse;
import com.example.AuthService.service.PermissionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;
    
    @PostMapping
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionRequest permissionRequest) {
        ModelMapper mapper = new ModelMapper();
        //Converter Request for DTO
        PermissionDTO permissionDTO = mapper.map(permissionRequest, PermissionDTO.class);
        permissionDTO = permissionService.create(permissionDTO);
        // Converter DTO for Response
        PermissionResponse permissionResponse = mapper.map(permissionDTO, PermissionResponse.class);
        return new ResponseEntity<PermissionResponse>(permissionResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> findAll(){
        List<PermissionDTO> categoriesDTO = permissionService.findAll();
        ModelMapper mapper = new ModelMapper();
        List<PermissionResponse> categoriesResponse = categoriesDTO.stream().map(
                permission -> mapper.map(permission, PermissionResponse.class)).collect(Collectors.toList());
        return new ResponseEntity<List<PermissionResponse>>(categoriesResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PermissionResponse>> findById(@PathVariable Long id){
        Optional<PermissionDTO> permissionDTO = permissionService.findById(id);
        PermissionResponse permissionResponse = new ModelMapper().map(permissionDTO.get(), PermissionResponse.class);
        return new ResponseEntity<Optional<PermissionResponse>>(Optional.of(permissionResponse), HttpStatus.OK);
    }

    //  Pensar em passar o ID pelo request tbm
    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(@Valid @RequestBody PermissionRequest permissionRequest, @PathVariable Long id) {
        ModelMapper mapper = new ModelMapper();
        PermissionDTO permissionDTO = mapper.map(permissionRequest, PermissionDTO.class);
        permissionDTO = permissionService.update(id, permissionDTO);
        return new ResponseEntity<PermissionResponse>(mapper.map(permissionDTO, PermissionResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        permissionService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
