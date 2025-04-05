package ru.volzhanin.deliverybackendapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_shifts")
public class UserShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Column(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    @Column(name = "user_id")
    private Shift shift;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;
}
