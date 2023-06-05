package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private int id;
    private String text;

    private int itemId;

    private int authorId;

    private String authorName;

    private LocalDateTime created;
}
