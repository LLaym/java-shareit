package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start", nullable = false)
    private LocalDateTime start;

    @Column(name = "end", nullable = false)
    private LocalDateTime end;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
