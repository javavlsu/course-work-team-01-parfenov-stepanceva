package ru.ispi.kanban.services;

public interface GroupPermissionService {

    void checkAdmin(Integer groupId, Integer userId);

    void checkMember(Integer groupId, Integer userId);

    boolean isMember(Integer groupId, Integer userId);
}
