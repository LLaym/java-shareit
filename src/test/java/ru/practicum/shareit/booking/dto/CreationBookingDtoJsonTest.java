package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreationBookingDtoJsonTest {
    @Autowired
    private JacksonTester<CreationBookingDto> json;

    @Test
    void testCreationBookingDto() throws Exception {
        CreationBookingDto creationBookingDto = new CreationBookingDto(
                1L,
                "2023-07-05T15:00:00.000000",
                "2023-07-06T20:00:00.000000"
        );

        JsonContent<CreationBookingDto> result = json.write(creationBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-05T15:00:00.000000");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-06T20:00:00.000000");
    }
}