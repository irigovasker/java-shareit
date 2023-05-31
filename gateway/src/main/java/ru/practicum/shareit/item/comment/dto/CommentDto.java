package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private int id;
    @NotBlank
    private String text;

    private int itemId;

    private int authorId;

    private String authorName;

    private LocalDateTime created;
}
