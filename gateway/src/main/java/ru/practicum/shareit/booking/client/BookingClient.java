package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.utils.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingCreateDto bookingDto, int userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approveBooking(int userId, int bookingId, Boolean isApproved) {
        Map<String, Object> params = Map.of("approved", isApproved);
        return patch("/" + bookingId + "?approved={approved}", (long) userId, params, null);
    }

    public ResponseEntity<Object> getBookingById(int userId, int bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUsersBookings(int userId, State state, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", (long) userId, params);
    }

    public ResponseEntity<Object> getUserItemsBookings(int userId, State state, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", (long) userId, params);
    }
}
