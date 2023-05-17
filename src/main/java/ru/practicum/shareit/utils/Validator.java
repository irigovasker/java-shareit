package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Validator {
    public static void validatePatchUserDto(UserDto user) {
        if (user.getName() != null) {
            validateUserName(user.getName());
        }

        if (user.getEmail() != null) {
            validateUserEmail(user.getEmail());
        }
    }

    private static void validateUserName(String name) {
        if (name.isBlank()) {
            throw new BadRequestException("Неверный формат имени");
        }
    }

    private static void validateUserEmail(String email) {
        if (!Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        ).matcher(email).find()) {
            throw new BadRequestException("Неверный формат электронной почты");
        }
    }

    public static void validatePatchItemDto(ItemDto itemDto) {
        if (itemDto.getName() != null) {
            validateItemName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            validateItemDescription(itemDto.getDescription());
        }
    }

    private static void validateItemDescription(String description) {
        if (description.isBlank()) {
            throw new RuntimeException("Не должно быть пустыма");
        }
    }

    private static void validateItemName(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Неверный формат имени");
        }
    }

    public static void validateBookingDto(BookingCreateDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if (start == null) {
            throw new BadRequestException("Введите дату старта");
        }
        if (end == null) {
            throw new BadRequestException("Введите дату конца");
        }
        if (end.isBefore(now)) {
            throw new BadRequestException("Конец не может быть в прошлом");
        }
        if (end.isBefore(start)) {
            throw new BadRequestException("Конец не может быть раньше старта");
        }
        if (start.isEqual(end)) {
            throw new BadRequestException("Начало аренды не может совпадать с концом");
        }
        if (start.isBefore(now)) {
            throw new BadRequestException("Начало не может быть в прошлом");
        }
    }
}
