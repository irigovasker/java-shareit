package ru.practicum.shareit.request.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UsersRepository usersRepository;

    @Override
    public List<ItemRequestDto> getUserRequests(int userId) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return requestRepository.findUsersRequests(userId).stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, int userId) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemRequestMapper.toDto(requestRepository.save(ItemRequestMapper.toNewItemRequest(itemRequestDto, userId)));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int from, int size, int userId) {
        return requestRepository.loadItemsInRequests(
                requestRepository.findItemRequestsByRequestorNotLike(
                        userId, PageRequest.of(from, size, Sort.by("created").descending())
                )).stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(int requestId, int userId) {
        usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemRequestMapper.toDto(
                requestRepository.findItemRequestByIdWithItems(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"))
        );
    }
}
