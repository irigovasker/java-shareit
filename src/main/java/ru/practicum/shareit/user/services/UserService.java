package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    void deleteUser(int userId);
}
