package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.models.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

public class Factory {

    public static User createUser() {
        return User.builder()
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static User createUser(int id) {
        return User.builder()
                .id(id)
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static UserDto createUserDto(int id) {
        return UserDto.builder()
                .id(id)
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

    public static Item createItem(int ownerId) {
        return Item.builder()
                .name("name")
                .description("description")
                .owner(ownerId)
                .available(true)
                .build();
    }

    public static Item createItem(int ownerId, int id) {
        return Item.builder()
                .id(id)
                .name("name")
                .description("description")
                .owner(ownerId)
                .available(true)
                .build();
    }

    public static ItemDto createItemDto() {
        return ItemDto.builder()
                .available(true)
                .description("testDescription")
                .name("testName")
                .build();
    }

    public static ItemDto createItemDto(int id) {
        return ItemDto.builder()
                .id(id)
                .available(true)
                .description("testDescription")
                .name("testName")
                .build();
    }

    public static Comment createComment(int itemId) {
        return Comment.builder()
                .itemId(itemId)
                .text("testText")
                .build();
    }

    public static Comment createComment(int itemId, int id, User author) {
        return Comment.builder()
                .id(id)
                .author(author)
                .itemId(itemId)
                .text("testText")
                .build();
    }

    public static CommentDto createCommentDto(int itemId) {
        return CommentDto.builder()
                .itemId(itemId)
                .text("testText")
                .build();
    }

    public static CommentDto createCommentDto(int itemId, int id) {
        return CommentDto.builder()
                .id(id)
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

    public static BookingCreateDto createBookingCreateDto(int itemId, int id) {
        return BookingCreateDto.builder()
                .id(id)
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

    public static Booking createBooking(int id, User booker, Item item, BookingStatus status) {
        return Booking.builder()
                .id(id)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(status)
                .build();
    }

    public static ItemRequest createRequest(int id, int requestorId) {
        return ItemRequest.builder()
                .id(id)
                .requestor(requestorId)
                .created(LocalDateTime.now())
                .description("test")
                .build();
    }
}
