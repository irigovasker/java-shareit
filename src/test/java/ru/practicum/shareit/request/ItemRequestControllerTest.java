package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.repositories.ItemsRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.user.services.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ItemRequestControllerTest {
    public static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private ItemsRepository itemsRepository;

    @BeforeEach
    public void clearContext() {
        requestRepository.deleteAll();
        itemsRepository.deleteAll();
        usersRepository.deleteAll();
    }

    @AfterEach
    public void clear() {
        clearContext();
    }

    @Test
    public void shouldAddRequest() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemRequestDto request = Factory.createRequestDto();

        String result = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        ItemRequestDto savedRequest = mapper.readValue(result, ItemRequestDto.class);

        assertEquals(request.getDescription(), savedRequest.getDescription());
    }

    @Test
    public void shouldErrorBecauseDescriptionNull() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemRequestDto request = Factory.createRequestDto();
        request.setDescription(null);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetRequestById() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemRequestDto request = requestService.createItemRequest(Factory.createRequestDto(), user.getId());

        String response = mvc.perform(get("/requests/" + request.getId())
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ItemRequestDto savedRequest = mapper.readValue(response, ItemRequestDto.class);
        assertNotNull(savedRequest);
    }

    @Test
    public void shouldThrowRequestNotFoundExceptionWhenGetRequestById() throws Exception {
        mvc.perform(get("/requests/" + 99123)
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetAllRequestByRequestorId() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemRequestDto request = requestService.createItemRequest(Factory.createRequestDto(), user.getId());

        String result = mvc.perform(get("/requests")
                        .header(USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<ItemRequestDto> requests = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
    }

    @Test
    public void shouldGetAllRequest() throws Exception {
        UserDto user = userService.createUser(Factory.createUserDto());
        ItemRequestDto request = requestService.createItemRequest(Factory.createRequestDto(), user.getId());

        String result = mvc.perform(get("/requests/all")
                        .header(USER_ID, 999)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<ItemRequestDto> requests = mapper.readValue(result, new TypeReference<>() {
        });
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
    }
}