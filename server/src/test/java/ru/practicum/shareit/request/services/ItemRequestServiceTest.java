package ru.practicum.shareit.request.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.Factory.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    private ItemRequestService requestService;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        requestService = new ItemRequestServiceImpl(requestRepository, usersRepository);
    }

    @Test
    void shouldGetUserRequests() {
        User user = createUser(1);
        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findUsersRequests(anyInt())).thenReturn(List.of(createRequest(1, 1)));

        List<ItemRequestDto> result = requestService.getUserRequests(1);
        verify(usersRepository).findById(1);
        verify(requestRepository).findUsersRequests(1);
    }

    @Test
    void shouldErrorBecauseUserNotExistWhenTryGetUserRequest() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(1));
    }

    @Test
    void shouldCreateItemRequest() {
        ItemRequest request = createRequest(1, 1);
        User user = createUser(1);
        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto result = requestService.createItemRequest(createRequestDto(), 1);

        verify(usersRepository).findById(1);
        verify(requestRepository).save(any());
        assertEquals(1, result.getId());
    }

    @Test
    void shouldErrorBecauseUserNotExistWhenTryCreateRequest() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.createItemRequest(createRequestDto(), 1));
    }

    @Test
    void shouldGetItemRequestById() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(createUser(1)));
        when(requestRepository.findItemRequestByIdWithItems(anyInt())).thenReturn(Optional.of(createRequest(1, 2)));

        ItemRequestDto result = requestService.getItemRequestById(1, 1);

        verify(usersRepository).findById(1);
        verify(requestRepository).findItemRequestByIdWithItems(1);
        assertEquals(1, result.getId());
    }

    @Test
    void shouldErrorBecauseUserNotExistWhenGetItemRequestById() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getItemRequestById(1, 1));
        verify(usersRepository).findById(1);
        verify(requestRepository, never()).findItemRequestByIdWithItems(anyInt());
    }

    @Test
    void shouldErrorBecauseItemNotExistWhenTryGetItemRequestById() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(createUser(1)));
        when(requestRepository.findItemRequestByIdWithItems(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getItemRequestById(1, 1));

        verify(usersRepository).findById(1);
        verify(requestRepository).findItemRequestByIdWithItems(1);
    }
}