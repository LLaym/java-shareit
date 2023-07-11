package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                "2023-07-05T15:00:00.000000",
                "2023-07-06T20:00:00.000000",
                null,
                null,
                "APROVED"
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-05T15:00:00.000000");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-06T20:00:00.000000");
        assertThat(result).extractingJsonPathValue("$.item").isNull();
        assertThat(result).extractingJsonPathValue("$.booker").isNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APROVED");
    }
}