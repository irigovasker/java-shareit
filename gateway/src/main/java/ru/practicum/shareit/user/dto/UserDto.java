package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class UserDto {
    private int id;
    @NotEmpty
    @NotBlank
    private String name;
    @NotEmpty
    @NotBlank
    @Email
    private String email;
}
