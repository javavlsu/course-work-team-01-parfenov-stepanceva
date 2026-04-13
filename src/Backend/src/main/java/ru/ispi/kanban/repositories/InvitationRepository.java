package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Invitation;
import ru.ispi.kanban.enums.InvitationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    // Активные email-приглашения для конкретного пользователя (для /my)
    @EntityGraph(attributePaths = {"group", "createdBy"})
    List<Invitation> findByEmailAndStatusAndExpiresAtAfter(
            String email, InvitationStatus status, LocalDateTime now);

    // Проверка дублирующего pending-приглашения для email+группа
    boolean existsByGroupIdAndEmailAndStatus(Integer groupId, String email, InvitationStatus status);

    // Поиск по токену — используется при переходе по ссылке
    @EntityGraph(attributePaths = {"group", "createdBy"})
    Optional<Invitation> findByInviteToken(String token);

    // Все приглашения группы — для просмотра администратором
    @EntityGraph(attributePaths = "createdBy")
    List<Invitation> findByGroupId(Integer groupId);

    // Поиск конкретного email-приглашения по id + email (для ответа)
    @EntityGraph(attributePaths = {"group", "createdBy"})
    Optional<Invitation> findByIdAndEmail(Integer id, String email);
}
