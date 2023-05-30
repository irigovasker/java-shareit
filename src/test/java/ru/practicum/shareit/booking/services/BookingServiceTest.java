package ru.practicum.shareit.booking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.utils.BadRequestException;
import ru.practicum.shareit.utils.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.Factory.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemsRepository itemsRepository;
    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, usersRepository, itemsRepository);
    }

    @Test
    void shouldCreateBooking() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(itemOwner.getId(), 1);
        item.setOwner(itemOwner.getId());

        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemsRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(createBooking(1, booker, item, BookingStatus.WAITING));

        BookingDto dto = bookingService.createBooking(createBookingCreateDto(item.getId()), booker.getId());

        verify(usersRepository).findById(booker.getId());
        verify(itemsRepository).findById(item.getId());
        verify(bookingRepository).save(any());

        assertEquals(booker.getId(), dto.getBooker().getId());
        assertEquals(item.getId(), dto.getItem().getId());
    }

    @Test
    void shouldErrorBecauseItemNotAvailableWhenTryCreateBooking() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(itemOwner.getId(), 1);
        item.setOwner(itemOwner.getId());
        item.setAvailable(false);

        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemsRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(createBookingCreateDto(1), 2));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldErrorBecauseBookerIsItemOwnerWhenTryCreateBooking() {
        User itemOwner = createUser(1);
        Item item = createItem(itemOwner.getId(), 1);
        item.setOwner(itemOwner.getId());
        item.setAvailable(false);

        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(itemOwner));
        when(itemsRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createBookingCreateDto(1), itemOwner.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldErrorBecauseBookerNotExistWhenTryCreateBooking() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createBookingCreateDto(1), 1));
        verify(itemsRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldErrorBecauseItemNotExistWhenTryCreateBooking() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.of(createUser(1)));
        when(itemsRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createBookingCreateDto(1), 1));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldApproveBooking() {
        Booking booking = createBooking(1, createUser(2), createItem(1, 1), BookingStatus.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(
                createBooking(1, createUser(1), createItem(1, 1), BookingStatus.APPROVED)
        );

        BookingDto result = bookingService.approveBooking(1, 1, true);

        verify(bookingRepository).findById(1);
        verify(bookingRepository).save(any());
        assertEquals(booking.getId(), result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldRejectedBooking() {
        Booking booking = createBooking(1, createUser(2), createItem(1, 1), BookingStatus.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(
                createBooking(1, createUser(1), createItem(1, 1), BookingStatus.REJECTED)
        );

        BookingDto result = bookingService.approveBooking(1, 1, false);

        verify(bookingRepository).findById(1);
        verify(bookingRepository).save(any());
        assertEquals(booking.getId(), result.getId());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void shouldErrorBecauseUserNotItemOwnerWhenTryToApproveBooking() {
        Booking booking = createBooking(1, createUser(2), createItem(1, 1), BookingStatus.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(2, 1, true));
        verify(bookingRepository).findById(1);
    }

    @Test
    void shouldErrorBecauseBookingAlreadyApproveWhenTryToApproveBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(
                Optional.of(
                        createBooking(1, createUser(2), createItem(1, 1), BookingStatus.APPROVED)
                )
        );

        assertThrows(BadRequestException.class, () -> bookingService.approveBooking(1, 1, true));
        verify(bookingRepository).findById(1);
    }

    @Test
    void shouldGetBookingByIdWhenUserIsOwnerOrBookerWhenTryGetBooking() {
        Booking booking = createBooking(1, createUser(2), createItem(1, 1), BookingStatus.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDto dto = bookingService.getBookingById(1, 1);
        assertEquals(booking.getId(), dto.getId());

        dto = bookingService.getBookingById(2, 1);
        assertEquals(booking.getId(), dto.getId());

        verify(bookingRepository, times(2)).findById(anyInt());
    }

    @Test
    void shouldErrorWhenUserNotBookerOrItemOwnerWhenTryGetBooking() {
        Booking booking = createBooking(1, createUser(2), createItem(1, 1), BookingStatus.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(3, 1));
        verify(bookingRepository, atLeast(1)).findById(1);
    }

    @Test
    void shouldErrorBecauseUserNotExistWhenTryGetUserBookings() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getUsersBookings(100, State.ALL, 0, 20));
    }

    @Test
    void shouldErrorBecauseUserNotExistWhenTryGetUserItemsBookings() {
        when(usersRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getUsersBookings(100, State.ALL, 0, 20));
    }
}