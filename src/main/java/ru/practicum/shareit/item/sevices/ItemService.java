package ru.practicum.shareit.item.sevices;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(ItemDto itemDto, int ownerId);

    ItemOwnerDto getItemById(int itemId, int userId);

    List<ItemOwnerDto> getUserItems(int ownerId, int from, int size);

    List<ItemDto> findItems(String text, int from, int size);

    CommentDto createComment(CommentDto comment, int userId, int itemId);
}
