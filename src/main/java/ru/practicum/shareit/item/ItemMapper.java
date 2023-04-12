package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static Item toNewItem(ItemDto itemDto, int ownerId) {
        return new Item(itemDto.getName(), itemDto.getDescription(), true, ownerId, null);
    }
}
