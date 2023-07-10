package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.CreationRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreationRequestDtoJsonTest {
    @Autowired
    private JacksonTester<CreationRequestDto> json;

    @Test
    void testCreationRequestDto() throws Exception {
        CreationRequestDto creationRequestDto = new CreationRequestDto(
                "Something like hammer"
        );

        JsonContent<CreationRequestDto> result = json.write(creationRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Something like hammer");
    }
}