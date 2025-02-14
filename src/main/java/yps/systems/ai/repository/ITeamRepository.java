package yps.systems.ai.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import yps.systems.ai.model.Team;

@Repository
public interface ITeamRepository extends Neo4jRepository<Team, String> {

    @Query("MATCH (t:Team), (p:Person) " +
            "WHERE elementId(t) = $teamElementId " +
            "AND elementId(p) = $personElementId " +
            "CREATE (t)-[:HAS_STUDENT]->(p)")
    void setStudentToTeam(String teamElementId, String personElementId);

    @Query("MATCH (t:Team), (p:Person) " +
            "WHERE elementId(t) = $teamElementId " +
            "AND elementId(p) = $personElementId " +
            "CREATE (t)-[:HAS_LEADER]->(p)")
    void setLeaderToTeam(String teamElementId, String personElementId);

    @Query("MATCH (t:Team) " +
            "WHERE elementId(t) = $teamElementId " +
            "MATCH (t)-[hs:HAS_STUDENT]->(:Person) " +
            "MATCH (t)-[hl:HAS_TEACHER]->(:Person) " +
            "DELETE hs, hl")
    void removePeopleFromTeam(String teamElementId);

    @Query("MATCH (t:Team)-[hs:HAS_STUDENT]->(p:Person) " +
            "WHERE elementId(t) = $teamElementId " +
            "AND elementId(p) = $personElementId " +
            "DELETE hs")
    void removeStudentFromTeam(String teamElementId, String personElementId);

    @Query("MATCH (t:Team)-[hl:HAS_LEADER]->(p:Person) " +
            "WHERE elementId(t) = $teamElementId " +
            "AND elementId(p) = $personElementId " +
            "DELETE hl")
    void removeLeaderFromTeam(String teamElementId, String personElementId);

}
