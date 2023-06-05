package ru.practicum.shareit.item.utils;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    private static BookingCreateDto last = null;
    private static BookingCreateDto next = null;

    public static Item toNewItem(ItemDto itemDto, int ownerId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(ownerId)
                .available(true)
                .request(itemDto.getRequestId() == null ? null : itemDto.getRequestId())
                .build();
    }

    public static ItemOwnerDto toItemOwnerDto(Item item, List<CommentDto> comments) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .ownerId(item.getOwner())
                .requestId(item.getRequest() == null ? null : item.getRequest())
                .comments(comments)
                .build();
    }

    public static ItemOwnerDto toItemOwnerDto(Item item, List<CommentDto> comments, List<BookingCreateDto> bookings) {
        updateLastAndNext(bookings);
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .ownerId(item.getOwner())
                .requestId(item.getRequest() == null ? null : item.getRequest())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest())
                .build();
    }


    public static Item updateFields(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    private static void updateLastAndNext(List<BookingCreateDto> bookings) {
        last = null;
        next = null;
        if (bookings == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (BookingCreateDto booking : bookings) {
            if (booking.getStart().isBefore(now)) {
                if (last == null) {
                    last = booking;
                    continue;
                }
                if (booking.getEnd().isAfter(last.getEnd())) {
                    last = booking;
                }
            } else if (booking.getStart().isAfter(now)) {
                if (next == null) {
                    next = booking;
                    continue;
                }
                if (booking.getStart().isBefore(next.getStart())) {
                    next = booking;
                }
            }
        }
    }
}
