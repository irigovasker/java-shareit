package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class Factory {
    public static UserDto createUserDto() {
        return UserDto.builder()
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static ItemDto createItemDto() {
        return ItemDto.builder()
                .available(true)
                .description("testDescription")
                .name("testName")
                .build();
    }

    public static CommentDto createCommentDto(int itemId) {
        return CommentDto.builder()
                .itemId(itemId)
                .text("testText")
                .build();
    }

    public static BookingCreateDto createBookingCreateDto(int itemId) {
        return BookingCreateDto.builder()
                .bookerId(2)
                .itemId(itemId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    public static ItemRequestDto createRequestDto() {
        return ItemRequestDto.builder()
                .description("test")
                .build();
    }
}
