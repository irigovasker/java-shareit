package ru.practicum.shareit.item.sevices;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int ownerId);
    ItemDto updateItem(ItemDto itemDto, int ownerId);
    ItemDto getItemById(int itemId);
    List<ItemDto> getUserItems(int ownerId);
    List<ItemDto> findItems(String text);
}
