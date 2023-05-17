package ru.practicum.shareit.item.storages;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private int counter;
    private final Map<Integer, Item> data;

    public ItemStorageImpl() {
        counter = 1;
        data = new HashMap<>();
    }

    @Override
    public Item createItem(Item item) {
        item.setId(counter++);
        data.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(ItemDto itemDto) {
        if (!data.containsKey(itemDto.getId())) {
            throw new NotFoundException("Несуществующая вещь");
        }
        Item item = data.get(itemDto.getId());
        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.isAvailable());
        return item;
    }

    @Override
    public Optional<Item> findItemById(int itemId) {
        return Optional.ofNullable(data.get(itemId));
    }

    @Override
    public List<Item> findUserItems(int ownerId) {
        return data.values().stream()
                .filter(item -> item.getOwner() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsByString(String text) {
        String textLowerCase = text.toLowerCase();
        return data.values().stream()
                .filter(item -> item.isAvailable() && (
                        item.getName().toLowerCase().contains(textLowerCase) ||
                                item.getDescription().toLowerCase().contains(textLowerCase)
                )).collect(Collectors.toList());
    }
}
