package com.example.AuthService.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    public String upload(MultipartFile file, String name) throws Exception;
    void removeFileByUsername(String username) throws IOException;

}
