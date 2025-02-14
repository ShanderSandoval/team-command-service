package yps.systems.ai.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import yps.systems.ai.model.Team;
import yps.systems.ai.object.TeamPerson;
import yps.systems.ai.repository.ITeamRepository;

import java.util.Optional;

@RestController
@RequestMapping("/command/teamService")
public class TeamCommandController {

    private final ITeamRepository teamRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${env.kafka.topicEvent}")
    private String kafkaTopicEvent;

    @Autowired
    public TeamCommandController(ITeamRepository teamRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.teamRepository = teamRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public ResponseEntity<String> saveTeam(@RequestBody Team team) {
        Team teamSaved = teamRepository.save(team);
        Message<Team> message = MessageBuilder
                .withPayload(teamSaved)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "CREATE_TEAM")
                .setHeader("source", "teamService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Team saved with ID: " + teamSaved.getElementId(), HttpStatus.CREATED);
    }

    @PostMapping("/setStudent")
    public ResponseEntity<String> setStudentTo(@RequestBody TeamPerson teamPerson) {
        teamRepository.setStudentToTeam(teamPerson.teamElementId(), teamPerson.personElementId());
        Message<TeamPerson> message = MessageBuilder
                .withPayload(teamPerson)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "SET_STUDENT")
                .setHeader("source", "teamService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Team related to student with ID: " + teamPerson.personElementId(), HttpStatus.CREATED);
    }

    @PostMapping("/setLeader")
    public ResponseEntity<String> setLeaderTo(@RequestBody TeamPerson teamPerson) {
        teamRepository.setLeaderToTeam(teamPerson.teamElementId(), teamPerson.personElementId());
        Message<TeamPerson> message = MessageBuilder
                .withPayload(teamPerson)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "SET_LEADER")
                .setHeader("source", "teamService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Team related to leader with ID: " + teamPerson.personElementId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/removeStudent")
    public ResponseEntity<String> removeStudentFrom(@RequestBody TeamPerson teamPerson) {
        teamRepository.removeStudentFromTeam(teamPerson.teamElementId(), teamPerson.personElementId());
        Message<TeamPerson> message = MessageBuilder
                .withPayload(teamPerson)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "REMOVE_STUDENT")
                .setHeader("source", "teamService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Team remove related student with ID: " + teamPerson.personElementId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/removeLeader")
    public ResponseEntity<String> removeLeaderFrom(@RequestBody TeamPerson teamPerson) {
        teamRepository.removeLeaderFromTeam(teamPerson.teamElementId(), teamPerson.personElementId());
        Message<TeamPerson> message = MessageBuilder
                .withPayload(teamPerson)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "REMOVE_LEADER")
                .setHeader("source", "teamService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Team related removed to leader with ID: " + teamPerson.personElementId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/removePeople/{teamElementId}")
    public ResponseEntity<String> removePeopleFrom(@PathVariable String teamElementId) {
        teamRepository.removePeopleFromTeam(teamElementId);
        Message<String> message = MessageBuilder
                .withPayload(teamElementId)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "REMOVE_PEOPLE")
                .setHeader("source", "teamService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Team related removed to people", HttpStatus.CREATED);
    }

    @DeleteMapping("/{elementId}")
    public ResponseEntity<String> deleteTeam(@PathVariable String elementId) {
        Optional<Team> teamOptional = teamRepository.findById(elementId);
        if (teamOptional.isPresent()) {
            teamRepository.removePeopleFromTeam(teamOptional.get().getElementId());
            teamRepository.delete(teamOptional.get());
            Message<String> message = MessageBuilder
                    .withPayload(teamOptional.get().getElementId())
                    .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                    .setHeader("eventType", "REMOVE_TEAM")
                    .setHeader("source", "teamService")
                    .build();
            kafkaTemplate.send(message);
            return new ResponseEntity<>("Team deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Team not founded", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{elementId}")
    public ResponseEntity<String> updateTeam(@PathVariable String elementId, @RequestBody Team team) {
        Optional<Team> teamOptional = teamRepository.findById(elementId);
        if (teamOptional.isPresent()) {
            team.setElementId(teamOptional.get().getElementId());
            teamRepository.save(team);
            Message<Team> message = MessageBuilder
                    .withPayload(team)
                    .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                    .setHeader("eventType", "UPDATE_TEAM")
                    .setHeader("source", "teamService")
                    .build();
            kafkaTemplate.send(message);
            return new ResponseEntity<>("Team updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Team not founded", HttpStatus.NOT_FOUND);
        }
    }

}
