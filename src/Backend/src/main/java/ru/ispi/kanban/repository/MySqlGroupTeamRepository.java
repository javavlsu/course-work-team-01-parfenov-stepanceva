package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entity.GroupTeam;

@Repository
public interface MySqlGroupTeamRepository extends JpaRepository<GroupTeam, Integer> {
}
