package com.example.Vox.Viridis.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contact {
    @NotNull(message = "First name is required!")
    @NotBlank(message = "First name is required!")
    private String firstName;
    @NotNull(message = "Last name is required!")
    @NotBlank(message = "Last name is required!")
    private String lastName;
    @NotNull(message = "Email is required!")
    @NotBlank(message = "Email is required!")
    @Email
    private String email;
    @NotNull(message = "Question is required!")
    @NotBlank(message = "Question is required!")
    private String question;
}
