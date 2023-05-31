package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Factory;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    public static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldErrorBecauseNotPassValidationNameEndEmail() throws Exception {
        ItemDto item = Factory.createItemDto();
        item.setName("");
        mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        item.setDescription("");
        mvc.perform(patch("/items/" + item.getId())
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldErrorBecauseAvailableNull() throws Exception {
        ItemDto item = Factory.createItemDto();
        item.setAvailable(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseNameNull() throws Exception {
        ItemDto item = Factory.createItemDto();
        item.setName(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseDescriptionNull() throws Exception {
        ItemDto item = Factory.createItemDto();
        item.setDescription(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldErrorBecauseCommentTextNull() throws Exception {
        CommentDto comment = Factory.createCommentDto(1);
        comment.setText(null);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}