package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Validator;

import javax.validation.Valid;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        Validator.validatePaginationParams(from, size);
        return itemClient.getUserItems(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemClient.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable int itemId, @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") int userId,
                              @PathVariable int itemId) {
        Validator.validatePatchItemDto(itemDto);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAvailableItemsByText(@RequestParam(name = "text") String text,
                                                  @RequestHeader(name = "X-Sharer-User-Id") int userId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "20") int size) {
        Validator.validatePaginationParams(from, size);
        return itemClient.findItems(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto comment,
                                    @RequestHeader(name = "X-Sharer-User-Id") int userId,
                                    @PathVariable int itemId) {
        return itemClient.createComment(comment, userId, itemId);
    }
}
