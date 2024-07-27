package com.example.AuthService.controller;

import com.example.AuthService.constants.FileConstants;
import com.example.AuthService.service.FileService;
import com.example.AuthService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
@RestController
@RequestMapping("/storage")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    private String uploadDir = FileConstants.DEFAULT_LOCATION_STORAGE;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        String url = fileService.upload(file, file.getOriginalFilename());
        return ResponseEntity.ok("Url: " + url);
    }

    @GetMapping("/display/{fileName:.+}")
    public ResponseEntity<Resource> displayImage(@PathVariable String fileName) throws MalformedURLException {
        // Carrega o arquivo como recurso
        Path filePath = Paths.get(uploadDir + File.separator + fileName);
        Resource resource = new UrlResource(filePath.toUri());

        // Retorna o recurso para exibição no navegador
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // ou MediaType.IMAGE_PNG, dependendo do tipo de imagem
                .body(resource);
    }

    // Visualizar apenas video
    @GetMapping("/video/{filename}")
    public ResponseEntity<byte[]> getVideo(@PathVariable("filename") String filename) throws IOException {
        Path videoPath = Paths.get(uploadDir + filename);

        byte[] videoBytes = Files.readAllBytes(videoPath);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename)
                .body(videoBytes);
    }

    @PreAuthorize("hasAnyAuthority('file:download')")
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) throws MalformedURLException {
        // Carrega o arquivo como recurso
        Path filePath = Paths.get(uploadDir + File.separator + fileName);
        Resource resource = new UrlResource(filePath.toUri());

        // Define o cabeçalho para fazer o download
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteImage(@PathVariable String username) {
        try {
            fileService.removeFileByUsername(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting file: " + e.getMessage());
        }
    }

}
