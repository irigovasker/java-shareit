package ru.practicum.shareit.utils;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.regex.Pattern;

public class Validator {
    public static void validatePatchUser(User user) {
        if (user.getName() != null) {
            validateUserName(user.getName());
        }

        if (user.getEmail() != null) {
            validateUserEmail(user.getEmail());
        }
    }

    private static void validateUserName(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Неверный формат имени");
        }
    }

    private static void validateUserEmail(String email) {
        if (!Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        ).matcher(email).find()) {
            throw new RuntimeException("Неверный формат электронной почты");
        }
    }

    public static void validateItem(ItemDto itemDto) {
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
}
