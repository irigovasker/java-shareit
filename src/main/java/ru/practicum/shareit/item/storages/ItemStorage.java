package ru.practicum.shareit.item.storages;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(ItemDto itemDto);

    Optional<Item> findItemById(int itemId);

    List<Item> findUserItems(int ownerId);

    List<Item> findItemsByString(String text);
}
