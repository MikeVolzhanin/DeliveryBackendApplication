package ru.volzhanin.deliverybackendapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "location_latitude")
    private Integer locationLatitude;

    @Column(name = "location_longitude")
    private Integer locationLongitude;

    @Column(name = "min_rate", nullable = false)
    private Integer minRate;

    @Column(name = "avg_rate", nullable = false)
    private Integer avgRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_delivery_type", nullable = false)
    private DeliveryType requiredDeliveryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShiftStatus status;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserShift> userShifts = new ArrayList<>();
}
