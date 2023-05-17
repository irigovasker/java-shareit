package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user);

    UserDto getUserById(int id);

    void deleteUser(int userId);
}
