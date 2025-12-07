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
@Table(name = "requests")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "created")
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    EventEntity event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    UserEntity requester;

    @Enumerated(EnumType.STRING)
    EventState status;

    public RequestEntity(LocalDateTime created, EventEntity event, UserEntity requester, EventState status) {
        this.created = created;
        this.event = event;
        this.requester = requester;
        this.status = status;
    }

    public RequestEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RequestEntity requestEntity = (RequestEntity) o;
        return Objects.equals(id, requestEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
