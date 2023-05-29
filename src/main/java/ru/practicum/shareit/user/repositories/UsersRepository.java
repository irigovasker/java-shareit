package ru.practicum.shareit.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.models.User;

@Component
public interface UsersRepository extends JpaRepository<User, Integer> {
}
