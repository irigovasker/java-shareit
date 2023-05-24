package ru.practicum.shareit.request.services;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getUserRequests(int userId);

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, int userId);

    List<ItemRequestDto> getAllRequests(int from, int size, int userId);

    ItemRequestDto getItemRequestById(int requestId, int userId);
}
