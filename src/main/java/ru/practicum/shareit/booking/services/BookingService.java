package ru.practicum.shareit.booking.services;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.utils.State;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingCreateDto bookingDto, int userId);

    BookingDto approveBooking(int userId, int bookingId, Boolean isApproved);

    BookingDto getBookingById(int userId, int bookingId);

    List<BookingDto> getUsersBookings(int userId, State state, int from, int size);

    List<BookingDto> getUserItemsBookings(int userId, State state, int from, int size);
}
