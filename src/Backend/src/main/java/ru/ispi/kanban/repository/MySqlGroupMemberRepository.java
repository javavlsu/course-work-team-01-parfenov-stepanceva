package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entity.GroupMember;
import ru.ispi.kanban.entity.composiveKey.GroupMemberId;

import java.util.List;
import java.util.Optional;

public interface MySqlGroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

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
}
