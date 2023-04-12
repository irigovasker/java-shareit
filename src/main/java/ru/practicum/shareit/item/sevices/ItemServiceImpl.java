package ru.practicum.shareit.item.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storages.ItemStorage;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        return ItemMapper.toItemDto(itemStorage.createItem(ItemMapper.toNewItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int ownerId) {
        Item item = itemStorage.findItemById(itemDto.getId()).orElseThrow(() -> new ObjectNotFoundException("Несуществующая вещь"));
        if (item.getOwner() != ownerId) {
            throw new ObjectNotFoundException("Нет доступа для редактирования");
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(itemDto));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return ItemMapper.toItemDto(
                itemStorage.findItemById(itemId).orElseThrow(() -> new ObjectNotFoundException("Несуществующая вещь")));
    }

    @Override
    public List<ItemDto> getUserItems(int ownerId) {
        return itemStorage.findUserItems(ownerId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.findItemsByString(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
