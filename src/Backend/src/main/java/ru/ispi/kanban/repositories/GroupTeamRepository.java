package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.GroupTeam;

@Repository
public interface GroupTeamRepository extends JpaRepository<GroupTeam, Integer> {
}
