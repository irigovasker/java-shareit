package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Factory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repositories.UsersRepository;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.utils.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private UsersRepository usersRepository;


    @BeforeEach
    void clearContext() {
        usersRepository.deleteAll();
    }


    @Test
    public void saveUserTest() throws Exception {
        User user = Factory.createUser();
        String response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto userDto = mapper.readValue(response, UserDto.class);

        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    public void shouldStatusErrorWhenEmailIsIncorrect() throws Exception {
        User user = Factory.createUser();
        user.setEmail("emailFail");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldStatusErrorWhenEmailIsNull() throws Exception {
        User user = Factory.createUser();
        user.setEmail(null);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldServerErrorWhenUpdateDuplicateEmail() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);
        user.setId(123);
        user.setEmail("another@user.ru");
        user = userService.createUser(user);
        user.setEmail("name@yandex.ru");
        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        user.setName("anotherName");
        user.setEmail("update@Email.ru");
        String response = mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto userDto = mapper.readValue(response, UserDto.class);

        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        user.setName("anotherOneName");
        user.setEmail(null);
        response = mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        userDto = mapper.readValue(response, UserDto.class);

        assertEquals(user.getName(), userDto.getName());

        user.setName(null);
        user.setEmail("anotherOne@email.ru");
        response = mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        userDto = mapper.readValue(response, UserDto.class);

        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void shouldGetErrorsBecauseNotPassValidation() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        user.setName("");
        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user.setEmail("string");
        user.setName(null);
        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void should404ErrorWhenUpdateUnknownUser() throws Exception {
        UserDto user = Factory.createUserDto();
        userService.createUser(user);

        mvc.perform(patch("/users/" + 10000)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldGetUser() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        String response = mvc.perform(get("/users/" + user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto userDto = mapper.readValue(response, UserDto.class);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void shouldReturnListUser() throws Exception {
        UserDto user = Factory.createUserDto();
        userService.createUser(user);

        UserDto user2 = Factory.createUserDto();
        user2.setEmail("user@second.ru");
        userService.createUser(user2);

        UserDto user3 = Factory.createUserDto();
        user3.setEmail("user@thrid.ru");
        userService.createUser(user3);

        String response = mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<UserDto> users = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(3, users.size());
    }


    @Test
    public void shouldDeleteUser() throws Exception {
        UserDto user = Factory.createUserDto();
        user = userService.createUser(user);

        int userId = user.getId();

        mvc.perform(delete("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThrows(
                NotFoundException.class, () -> userService.getUserById(userId)
        );
    }
}