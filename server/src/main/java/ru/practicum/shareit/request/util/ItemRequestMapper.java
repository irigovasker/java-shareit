package ru.practicum.shareit.request.util;

import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() == null ?
                        Collections.emptyList() :
                        itemRequest.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
                )
                .build();
    }

    public static ItemRequest toNewItemRequest(ItemRequestDto itemRequestDto, int userId) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(userId)
                .created(itemRequestDto.getCreated())
                .build();
    }
}
