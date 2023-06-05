package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemOwnerDtoTest {
    @Autowired
    private JacksonTester<ItemOwnerDto> jacksonTester;

    @Test
    void test() throws IOException {
        ItemOwnerDto dto = ItemOwnerDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(1)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .requestId(2)
                .build();

        JsonContent<ItemOwnerDto> content = jacksonTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(content).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(content).extractingJsonPathValue("$.lastBooking").isEqualTo(null);
        assertThat(content).extractingJsonPathValue("$.nextBooking").isEqualTo(null);
        assertThat(content).extractingJsonPathArrayValue("$.comments").isEmpty();
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }
}