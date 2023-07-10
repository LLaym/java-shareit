package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    void testBookingDto() throws Exception {
        RequestDto requestDto = new RequestDto(
                1L,
                "Something like hammer",
                "2023-07-05T15:00:00.000000",
                1L,
                Collections.emptyList()
        );

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Something like hammer");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-07-05T15:00:00.000000");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}