package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.item.comment.models.Comment;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private boolean available;
    @Column(name = "owner_id")
    private int owner;
    @Column(name = "request_id")
    private Integer request;
    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;
    @OneToMany(mappedBy = "item")
    private List<Booking> bookings;
}
