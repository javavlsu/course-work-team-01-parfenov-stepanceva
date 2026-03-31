package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entities.GroupMember;
import ru.ispi.kanban.entities.composiveKey.GroupMemberId;
import ru.ispi.kanban.enums.GroupRole;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findAllByGroupId(Integer groupId);

    List<GroupMember> findAllByUserId(Integer userId);

    Optional<GroupMember> findByGroupIdAndUserId(
            Integer groupId,
            Integer userId
    );

    boolean existsByGroupIdAndUserId(
            Integer groupId,
            Integer userId
    );

    void deleteByGroupIdAndUserId(
            Integer groupId,
            Integer userId
    );

    List<GroupMember> findByGroupIdAndRole(Integer groupId, GroupRole role);
}
