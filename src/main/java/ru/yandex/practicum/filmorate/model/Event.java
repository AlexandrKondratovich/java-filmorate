package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "eventId")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    Long eventId;
    @NotNull
    Long userId;
    @NotNull
    EventType eventType;
    @NotNull
    Long entityId;
    @NotNull
    Operation operation;
    Long timestamp;
}
