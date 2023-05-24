package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

public class Factory {

    public static User createUser() {
        return User.builder()
                .id(1)
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .id(1)
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1)
                .available(true)
                .description("testDescription")
                .name("testName")
                .build();
    }

    public static CommentDto createCommentDto(int itemId) {
        return CommentDto.builder()
                .id(1)
                .itemId(itemId)
                .text("testText")
                .build();
    }

    public static BookingCreateDto createBookingCreateDto(int itemId) {
        return BookingCreateDto.builder()
                .id(1)
                .bookerId(2)
                .itemId(itemId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    public static ItemRequestDto createRequestDto() {
        return ItemRequestDto.builder()
                .id(1)
                .description("test")
                .build();
    }
}
