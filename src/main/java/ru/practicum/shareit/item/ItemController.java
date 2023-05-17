package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.sevices.ItemService;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.utils.Validator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemOwnerDto> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.getUserItems(userId);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        userService.getUserById(userId);
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto getItemById(@PathVariable int itemId, @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") int userId,
                              @PathVariable int itemId) {
        Validator.validatePatchItemDto(itemDto);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findAvailableItemsByText(@RequestParam(name = "text") String text,
                                                  @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.findItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto comment,
                                 @RequestHeader(name = "X-Sharer-User-Id") int userId,
                                 @PathVariable int itemId) {
        return itemService.createComment(comment, userId, itemId);
    }
}
