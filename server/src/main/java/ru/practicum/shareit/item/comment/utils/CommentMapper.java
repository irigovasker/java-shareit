package ru.practicum.shareit.item.comment.utils;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.models.Comment;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toNewComment(CommentDto dto, User author) {
        return Comment.builder()
                .text(dto.getText())
                .itemId(dto.getItemId())
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItemId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
