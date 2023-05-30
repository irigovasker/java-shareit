package ru.practicum.shareit.item.comment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.models.Comment;

@Component
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
}
