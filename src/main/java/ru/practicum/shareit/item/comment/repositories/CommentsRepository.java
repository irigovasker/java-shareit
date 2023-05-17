package ru.practicum.shareit.item.comment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comment.models.Comment;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByItemId(int itemId);
}
