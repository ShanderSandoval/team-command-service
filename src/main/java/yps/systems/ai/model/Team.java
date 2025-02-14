package yps.systems.ai.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;

@Node("Team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class Team {

    @Id
    @GeneratedValue
    private String elementId;

    @Property("name")
    private String name;

    @Property("startDate")
    private LocalDate startDate;

    @Property("endDate")
    private LocalDate endDate;

    @Property("state")
    private Boolean state;

}
