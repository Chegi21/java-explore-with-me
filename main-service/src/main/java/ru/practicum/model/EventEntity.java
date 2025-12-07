package ru.practicum.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation")
    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    CategoryEntity category;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(name = "description")
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    UserEntity initiator;

    @ManyToOne()
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    LocationEntity locationEntity;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participation_limit")
    Long participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "confirmed_requests")
    Long confirmedRequests;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    EventState state;

    @Column(name = "title")
    String title;

    public EventEntity(String annotation,
                       CategoryEntity category,
                       LocalDateTime createdOn,
                       String description,
                       LocalDateTime eventDate,
                       UserEntity initiator,
                       LocationEntity locationEntity,
                       Boolean paid,
                       Long participantLimit,
                       LocalDateTime publishedOn,
                       Long confirmedRequests,
                       Boolean requestModeration,
                       EventState state,
                       String title) {
        this.annotation = annotation;
        this.category = category;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.locationEntity = locationEntity;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.confirmedRequests = confirmedRequests;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
    }

    public EventEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        EventEntity that = (EventEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
