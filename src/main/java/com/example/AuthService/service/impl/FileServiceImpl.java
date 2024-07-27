package com.example.AuthService.service.impl;

import com.example.AuthService.constants.FileConstants;
import com.example.AuthService.exception.domain.ObjectNotFoundException;
import com.example.AuthService.model.entity.User;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.FileService;
import com.example.AuthService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private String uploadDir = FileConstants.DEFAULT_LOCATION_STORAGE;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String upload(MultipartFile file, String name) throws Exception {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if(filename.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + filename);
            }

            // Verifica se o diretório existe, se não, cria
            Path directory = Paths.get(uploadDir);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Salva o arquivo no diretório
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + File.separator + name + ".jpg");
            Files.write(filePath, file.getBytes());

            String url = null;
            url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/storage/display/")
                    .path(name + ".jpg")
                    .toUriString();

            return url;
        } catch (IOException e) {
            throw new Exception("Could not save File: " + file.getOriginalFilename());
        }
    }

    @Override
    public void removeFileByUsername(String username) throws IOException {
        Optional<User> userFind = userRepository.findByUserName(username);
        if(userFind.isEmpty()){
            throw new ObjectNotFoundException("Username '" + username + "' not found.");
        }
        Path filePath = Paths.get(uploadDir + File.separator + username + ".jpg");
        Files.deleteIfExists(filePath);
    }

    private String setFileImageUrl(String filename, String type) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(FileConstants.DEFAULT_LOCATION_STORAGE + filename)
                .toUriString();
    }

}
