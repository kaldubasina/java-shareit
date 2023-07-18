package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", length = 1000, nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "owner_id")
    private User owner;
    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;
    @Transient
    private List<Comment> comments;
}
