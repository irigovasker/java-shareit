package ru.practicum.shareit.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.models.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
}
