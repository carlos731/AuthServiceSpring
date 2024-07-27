package com.example.AuthService.exception;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FieldMessage  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String message;
}
