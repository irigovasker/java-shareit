package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.utils.BadRequestException;
import ru.practicum.shareit.utils.Validator;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                                @RequestBody BookingCreateDto bookingDto) {
        Validator.validateBookingDto(bookingDto);
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                     @PathVariable("bookingId") int bookingId,
                                     @RequestParam("approved") Boolean isApproved) {
        return bookingClient.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                 @PathVariable("bookingId") int bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersBookings(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "20") int size) {
        Validator.validatePaginationParams(from, size);
        try {
            return bookingClient.getUsersBookings(userId, State.valueOf(state), from, size);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUserItemsBookings(@RequestHeader(name = "X-Sharer-User-Id") int userId,
                                                 @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        Validator.validatePaginationParams(from, size);
        try {
            return bookingClient.getUserItemsBookings(userId, State.valueOf(state), from, size);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }
}
