package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.utils.BadRequestException;
import ru.practicum.shareit.utils.Validator;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                    @RequestBody BookingCreateDto bookingDto) {
        Validator.validateBookingDto(bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                     @PathVariable("bookingId") int bookingId,
                                     @RequestParam("approved") Boolean isApproved) {
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                 @PathVariable("bookingId") int bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUsersBookings(
            @RequestHeader(name = "X-Sharer-User-Id") int userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        try {
            return bookingService.getUsersBookings(userId, State.valueOf(state));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemsBookings(
            @RequestHeader(name = "X-Sharer-User-Id") int userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        try {
            return bookingService.getUserItemsBookings(userId, State.valueOf(state));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }
}
