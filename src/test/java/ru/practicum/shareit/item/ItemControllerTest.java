package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Factory;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.repositories.CommentsRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.item.sevices.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.user.services.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ItemControllerTest {
    public static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @MockBean
    private BookingRepository bookingRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ItemsRepository itemsRepository;
    @Autowired
    private CommentsRepository commentsRepository;

    @BeforeEach
    public void clearContext() {
        commentsRepository.deleteAll();
        itemsRepository.deleteAll();
        usersRepository.deleteAll();
    }

    @AfterEach
    public void clear() {
        clearContext();
    }

    @Test
    public void shouldAddItem() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        String response = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(Factory.createItemDto()))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemDto itemDto = mapper.readValue(response, ItemDto.class);
        assertNotNull(itemDto);
    }

    @Test
    public void shouldErrorBecauseAvailableNull() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);
        ItemDto item = Factory.createItemDto();
        item.setAvailable(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseNameNull() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);
        ItemDto item = Factory.createItemDto();
        item.setName(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseDescriptionNull() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);
        ItemDto item = Factory.createItemDto();
        item.setDescription(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        item.setName("anotherName");
        item.setDescription("anotherDescription");
        String response = mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemDto itemDto = mapper.readValue(response, ItemDto.class);

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());

        item.setName("anotherOneName");
        item.setDescription(null);
        response = mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        itemDto = mapper.readValue(response, ItemDto.class);

        assertEquals(item.getName(), itemDto.getName());

        item.setName(null);
        item.setDescription("anotherOneDescription");
        response = mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        itemDto = mapper.readValue(response, ItemDto.class);

        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    public void shouldErrorBecauseNotPassValidationNameEndEmail() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        item.setName("");
        mvc.perform(patch("/items/" + item.getId())
                .content(mapper.writeValueAsString(item))
                .header(USER_ID, user.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        item.setDescription("");
        mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldErrorBecauseUserNotOwner() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        item.setName("anotherName");
        mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, 123123)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseWrongIdInPath() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        item.setName("test value changed");
        mvc.perform(patch("/items/" + 100444)
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemById() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        String response = mvc.perform(get("/items/" + item.getId())
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ItemDto itemDto = mapper.readValue(response, ItemDto.class);

        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    public void shouldErrorBecauseItemNotFound() throws Exception {
        mvc.perform(get("/items/" + 131313)
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemByOwnerId() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        String response = mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<ItemDto> items = mapper.readValue(response, new TypeReference<>() {});

        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }


    @Test
    public void shouldGetAvailableItemsByName() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        String response = mvc.perform(get("/items/search?text=" + item.getName())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<ItemDto> items = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    public void shouldAddItemComment() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        when(bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(Booking.builder().booker(
                        User.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .build()
                ).build()));

        String response = mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(Factory.createCommentDto(item.getId())))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        CommentDto commentDto = mapper.readValue(response, CommentDto.class);
        assertNotNull(commentDto);
    }

    @Test
    public void shouldErrorBecauseCommentTextNull() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        when(bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new Booking()));

        CommentDto comment = Factory.createCommentDto(item.getId());
        comment.setText(null);
        mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldThrowCommentBadRequestExceptionByEmptyBooking() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        when(bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.empty());

        CommentDto comment = Factory.createCommentDto(item.getId());
        mvc.perform(post("/items/" + item.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemOwner() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        UserDto user2 = Factory.createUserDto();
        user2.setEmail("another@email.ru");
        user2 = userService.createUser(user2);
        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        String response = mvc.perform(get("/items/" + item.getId())
                .header(USER_ID, user2.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemOwnerDto dto = mapper.readValue(response, ItemOwnerDto.class);

        assertEquals(item.getId(), dto.getId());
    }

    @Test
    public void shouldGetItemWithComments() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemDto item = itemService.createItem(Factory.createItemDto(), user.getId());

        UserDto user2 = Factory.createUserDto();
        user2.setEmail("anotherOne@email.ru");
        user2 = userService.createUser(user2);

            when(bookingRepository.findFirstBookingByBooker_IdAndItem_IdAndEndBefore(anyInt(), anyInt(), any()))
                    .thenReturn(Optional.of(Booking.builder().booker(
                            User.builder()
                                    .id(user.getId())
                                    .name(user.getName())
                                    .email(user.getEmail())
                                    .build()
                    ).build()));

        itemService.createComment(Factory.createCommentDto(item.getId()), user2.getId(), item.getId());

        String response = mvc.perform(get("/items/" + item.getId())
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemOwnerDto dto = mapper.readValue(response, ItemOwnerDto.class);
        assertEquals("testText", dto.getComments().get(0).getText());
    }

    @Test
    public void shouldGetEmptyListWhenSearchTextIsBlank() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        String response = mvc.perform(get("/items/search?text= ")
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<ItemDto> items = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(0, items.size());
    }
}