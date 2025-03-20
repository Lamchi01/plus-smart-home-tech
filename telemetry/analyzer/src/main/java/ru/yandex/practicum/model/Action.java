package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "actions")
@Getter
@Setter
@ToString
@SecondaryTable(
        name = "scenario_actions",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "action_id")
)
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionType type;

    private Integer value;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToMany(mappedBy = "actions")
    private List<Scenario> scenarios;
}