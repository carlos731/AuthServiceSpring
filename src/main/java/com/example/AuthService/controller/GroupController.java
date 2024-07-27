package com.example.AuthService.controller;

import com.example.AuthService.model.dto.GroupDTO;
import com.example.AuthService.model.request.GroupRequest;
import com.example.AuthService.model.response.GroupResponse;
import com.example.AuthService.service.GroupService;
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
@RequestMapping("/auth/group")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest groupRequest) {
        ModelMapper mapper = new ModelMapper();
        //Converter Request for DTO
        GroupDTO groupDTO = mapper.map(groupRequest, GroupDTO.class);
        groupDTO = groupService.create(groupDTO);
        // Converter DTO for Response
        GroupResponse groupResponse = mapper.map(groupDTO, GroupResponse.class);
        return new ResponseEntity<GroupResponse>(groupResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> findAll(){
        List<GroupDTO> categoriesDTO = groupService.findAll();
        ModelMapper mapper = new ModelMapper();
        List<GroupResponse> categoriesResponse = categoriesDTO.stream().map(
                group -> mapper.map(group, GroupResponse.class)).collect(Collectors.toList());
        return new ResponseEntity<List<GroupResponse>>(categoriesResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<GroupResponse>> findById(@PathVariable Long id){
        Optional<GroupDTO> groupDTO = groupService.findById(id);
        GroupResponse groupResponse = new ModelMapper().map(groupDTO.get(), GroupResponse.class);
        return new ResponseEntity<Optional<GroupResponse>>(Optional.of(groupResponse), HttpStatus.OK);
    }

    //  Pensar em passar o ID pelo request tbm
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> update(@Valid @RequestBody GroupRequest groupRequest, @PathVariable Long id) {
        ModelMapper mapper = new ModelMapper();
        GroupDTO groupDTO = mapper.map(groupRequest, GroupDTO.class);
        groupDTO = groupService.update(id, groupDTO);
        return new ResponseEntity<>(mapper.map(groupDTO, GroupResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        groupService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
