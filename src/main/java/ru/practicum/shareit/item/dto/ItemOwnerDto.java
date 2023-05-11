package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemOwnerDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int ownerId;
    private Integer requestId;
    private BookingCreateDto lastBooking;
    private BookingCreateDto nextBooking;
    private List<CommentDto> comments;
}
