package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter @Setter
@Entity
@Table(name = "requests")
public class Request implements Comparable<Request>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @OneToMany
    @JoinColumn(name = "request_id")
    private List<RequestAnswer> answers = new ArrayList<>();

    @Override
    public int compareTo(Request request) {
        // compare with created time
        return this.created.compareTo(request.getCreated());
    }
}
