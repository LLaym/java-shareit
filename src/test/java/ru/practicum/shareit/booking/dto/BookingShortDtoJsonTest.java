package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingShortDtoJsonTest {
    @Autowired
    private JacksonTester<BookingShortDto> json;

    @Test
    void testBookingShortDto() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto(
                1L,
                1L
        );

        JsonContent<BookingShortDto> result = json.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}