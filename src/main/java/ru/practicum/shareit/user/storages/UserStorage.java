package ru.practicum.shareit.user.storages;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);
    User updateUser(User user);
    Optional<User> findUserById(int id);
    List<User> findAllUsers();
    void removeUser(int id);
}
