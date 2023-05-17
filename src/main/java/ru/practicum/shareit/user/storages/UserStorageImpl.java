package ru.practicum.shareit.user.storages;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {
    private int counter;
    private final Map<Integer, User> data;

    public UserStorageImpl() {
        data = new HashMap<>();
        counter = 1;
    }

    @Override
    public User createUser(User user) {
        if (hasMatchEmails(user.getEmail(), user.getId())) {
            throw new RuntimeException("Пользователь с таким адресом уже создан");
        }
        user.setId(counter++);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!data.containsKey(user.getId())) {
            throw new NotFoundException("Несуществующий пользователь");
        }

        if (user.getEmail() != null) {
            if (hasMatchEmails(user.getEmail(), user.getId())) {
                throw new RuntimeException("Пользователь с таким адресом уже создан");
            } else {
                data.get(user.getId()).setEmail(user.getEmail());
            }
        }
        if (user.getName() != null) {
            data.get(user.getId()).setName(user.getName());
        }
        return data.get(user.getId());
    }

    @Override
    public Optional<User> findUserById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void removeUser(int id) {
        data.remove(id);
    }

    private boolean hasMatchEmails(String email, int id) {
        if (data.containsKey(id) && data.get(id).getEmail().equals(email)) {
            return false;
        }
        return data.values().stream().map(User::getEmail).anyMatch(s -> s.equals(email));
    }
}
