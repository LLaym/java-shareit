package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CreationCommentDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreationCommentDtoJsonTest {
    @Autowired
    private JacksonTester<CreationCommentDto> json;

    @Test
    void testCreationCommentDto() throws Exception {
        CreationCommentDto creationCommentDto = new CreationCommentDto(
                "That's the best tool"
        );

        JsonContent<CreationCommentDto> result = json.write(creationCommentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("That's the best tool");
    }
}