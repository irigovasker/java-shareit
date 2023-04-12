package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;

@Getter
@Setter
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private int owner;
    private ItemRequest request;

    public Item() {
    }

    public Item(String name, String description, boolean available, int owner, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
