package com.example.AuthService.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "O campo email é requerido!")
    private String email;

    @NotNull(message = "O campo password é requerido!")
    private String password;

}
