package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.utils.Validator;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        Validator.validatePaginationParams(from, size);
        return itemRequestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                             @PathVariable int requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
