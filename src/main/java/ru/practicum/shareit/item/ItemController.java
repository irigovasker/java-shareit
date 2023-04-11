package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.sevices.ItemService;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.utils.Validator;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") int id) {
        return itemService.getUserItems(id);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(name = "X-Sharer-User-Id") int id) {
        userService.getUserById(id);
        return itemService.createItem(itemDto, id);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id) {
        return itemService.getItemById(id);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") int userId,
                              @PathVariable int id) {
        Validator.validateItem(itemDto);
        itemDto.setId(id);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findAvailableItemsByText(@RequestParam(name = "text") String text,
                                                  @RequestHeader(name = "X-Sharer-User-Id") int id) {
        return itemService.findItems(text);
    }
}
