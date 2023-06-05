package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void test() throws IOException {
        ItemDto dto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .requestId(2)
                .build();

        JsonContent<ItemDto> content = jacksonTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }
}