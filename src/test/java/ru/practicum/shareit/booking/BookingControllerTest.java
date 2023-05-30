package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Factory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.item.sevices.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.user.services.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class BookingControllerTest {
    public static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemsRepository itemRepository;

    @BeforeEach
    public void clearContext() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void clear() {
        clearContext();
    }

    @Test
    public void shouldAddBooking() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");

        booker = userService.createUser(booker);

        ItemDto item = itemService.createItem(Factory.createItemDto(), itemOwner.getId());

        String response = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(Factory.createBookingCreateDto(item.getId())))
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        BookingDto bookingDto = mapper.readValue(response, BookingDto.class);
        assertNotNull(bookingDto);
    }

    @Test
    public void shouldApproveBooking() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");

        booker = userService.createUser(booker);

        ItemDto item = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto booking = bookingService.createBooking(Factory.createBookingCreateDto(item.getId()), booker.getId());

        String response = mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(itemOwner))
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookingDto bookingDto = mapper.readValue(response, BookingDto.class);

        assertEquals(bookingDto.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void shouldErrorNotFoundUser() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto item = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto booking = bookingService.createBooking(Factory.createBookingCreateDto(item.getId()), booker.getId());

        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .header(USER_ID, 123123)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseStatusApproved() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto item = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto booking = bookingService.createBooking(Factory.createBookingCreateDto(item.getId()), booker.getId());

        bookingService.approveBooking(itemOwner.getId(), booking.getId(), true);

        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(itemOwner))
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseUserNotItemOwner() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto item = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto booking = bookingService.createBooking(Factory.createBookingCreateDto(item.getId()), booker.getId());

        bookingService.approveBooking(itemOwner.getId(), booking.getId(), true);

        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .content(mapper.writeValueAsString(itemOwner))
                        .header(USER_ID, 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseItemNotAvailable() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        itemDto.setAvailable(false);
        itemService.updateItem(itemDto, itemOwner.getId());
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(Factory.createBookingCreateDto(itemDto.getId())))
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseStartInPast() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());

        BookingCreateDto bookingCreateDto = Factory.createBookingCreateDto(itemDto.getId());
        bookingCreateDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseEndInPast() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingCreateDto bookingCreateDto = Factory.createBookingCreateDto(itemDto.getId());
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseOwnerIsBooker() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingCreateDto bookingCreateDto = Factory.createBookingCreateDto(itemDto.getId());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetBooking() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        String result = mvc.perform(get("/bookings/" + bookingDto.getId())
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookingDto dto = mapper.readValue(result, BookingDto.class);

        assertEquals(bookingDto.getId(), dto.getId());
    }

    @Test
    public void shouldErrorBecauseUserNotItemOwnerAndNotBooker() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        mvc.perform(get("/bookings/" + bookingDto.getId())
                        .header(USER_ID, 123123123)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseNotFoundBooking() throws Exception {
        mvc.perform(get("/bookings/" + 100000)
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetBookingForUserByAllState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        String result = mvc.perform(get("/bookings?state=ALL")
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByCurrentState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingCreateDto bookingDto = Factory.createBookingCreateDto(itemDto.getId());
        bookingDto.setStart(bookingDto.getStart().minusDays(2));
        BookingDto dto = bookingService.createBooking(bookingDto, booker.getId());

        String result = mvc.perform(get("/bookings?state=CURRENT")
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByPastState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingCreateDto bookingDto = Factory.createBookingCreateDto(itemDto.getId());
        bookingDto.setEnd(bookingDto.getEnd().minusDays(5));
        bookingDto.setStart(bookingDto.getStart().minusDays(10));
        BookingDto dto = bookingService.createBooking(bookingDto, booker.getId());

        String result = mvc.perform(get("/bookings?state=PAST")
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByWaitingState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        String result = mvc.perform(get("/bookings?state=WAITING")
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByFutureState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());

        BookingCreateDto createBookingDto = Factory.createBookingCreateDto(itemDto.getId());
        createBookingDto.setStart(createBookingDto.getStart().plusDays(1));
        createBookingDto.setEnd(createBookingDto.getEnd().plusDays(12));

        BookingDto bookingDto = bookingService.createBooking(createBookingDto, booker.getId());

        String result = mvc.perform(get("/bookings?state=FUTURE")
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByRejectedState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        bookingService.approveBooking(itemOwner.getId(), bookingDto.getId(), false);

        String result = mvc.perform(get("/bookings?state=REJECTED")
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldErrorBecauseStateIncorrect() throws Exception {
        mvc.perform(get("/bookings?state=TEST_INCORRECT")
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetBookingForUserByItemsByAllState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        String result = mvc.perform(get("/bookings/owner?state=ALL")
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldErrorBecauseOwnerStateIncorrect() throws Exception {
        mvc.perform(get("/bookings/owner?state=TEST_INCORRECT")
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetBookingForUserByItemsByFutureState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());

        BookingCreateDto createBookingDto = Factory.createBookingCreateDto(itemDto.getId());
        createBookingDto.setStart(createBookingDto.getStart().plusDays(1));
        createBookingDto.setEnd(createBookingDto.getEnd().plusDays(12));

        BookingDto booking = bookingService.createBooking(createBookingDto, booker.getId());

        String result = mvc.perform(get("/bookings/owner?state=FUTURE")
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByItemsByRejectedState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        bookingService.approveBooking(itemOwner.getId(), bookingDto.getId(), false);

        String result = mvc.perform(get("/bookings/owner?state=REJECTED")
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserByItemsByWaitingState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingDto bookingDto = bookingService.createBooking(Factory.createBookingCreateDto(itemDto.getId()), booker.getId());

        String result = mvc.perform(get("/bookings/owner?state=WAITING")
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserItemsByCurrentState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingCreateDto bookingCreateDto = Factory.createBookingCreateDto(itemDto.getId());
        bookingCreateDto.setStart(bookingCreateDto.getStart().minusDays(10));
        BookingDto bookingDto = bookingService.createBooking(bookingCreateDto, booker.getId());

        String result = mvc.perform(get("/bookings/owner?state=CURRENT")
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    public void shouldGetBookingForUserItemsByPastState() throws Exception {
        UserDto itemOwner = userService.createUser(Factory.createUserDto());
        UserDto booker = Factory.createUserDto();
        booker.setEmail("another@email.ru");
        booker = userService.createUser(booker);

        ItemDto itemDto = itemService.createItem(Factory.createItemDto(), itemOwner.getId());
        BookingCreateDto bookingCreateDto = Factory.createBookingCreateDto(itemDto.getId());
        bookingCreateDto.setStart(bookingCreateDto.getStart().minusDays(10));
        bookingCreateDto.setEnd(bookingCreateDto.getEnd().minusDays(5));
        BookingDto bookingDto = bookingService.createBooking(bookingCreateDto, booker.getId());

        String result = mvc.perform(get("/bookings/owner?state=PAST")
                        .header(USER_ID, itemOwner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<BookingDto> bookings = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }
}