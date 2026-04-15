package ru.ispi.kanban.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.BoardUser;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.entities.composiveKey.BoardUserId;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.exceptions.NotAdminException;
import ru.ispi.kanban.mappers.UserMapper;
import ru.ispi.kanban.repositories.BoardRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.services.impl.BoardUserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardUserServiceImplTest {

    @Mock BoardUserRepository boardUserRepository;

    @Mock GroupMemberService groupMemberService;

    @Mock UserMapper userMapper;

    @InjectMocks BoardUserServiceImpl boardUserService;

    @Test
    void getUserBoard_success() {
        Board board = new Board();
        BoardUser boardUser = new BoardUser();
        boardUser.setBoard(board);
        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.of(boardUser));

        assertThat(boardUserService.getUserBoard(1, 10)).isSameAs(board);
    }

    @Test
    void getUserBoard_noAccess_throws() {
        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardUserService.getUserBoard(1, 10))
                .isInstanceOf(BoardAccessDeniedException.class);
    }

    @Test
    void addUserToBoard_success() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);
        BoardUser adminBoardUser = new BoardUser();
        adminBoardUser.setBoard(board);
        User targetUser = new User();
        targetUser.setId(2);

        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.of(adminBoardUser));
        doNothing().when(groupMemberService).checkAdmin(5, 1);
        when(groupMemberService.getMemberUser(5, 2)).thenReturn(targetUser);
        when(boardUserRepository.existsById(any(BoardUserId.class))).thenReturn(false);

        boardUserService.addUserToBoard(1, 10, 2);

        verify(boardUserRepository).save(any(BoardUser.class));
    }

    @Test
    void addUserToBoard_notAdmin_throws() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);
        BoardUser adminBoardUser = new BoardUser();
        adminBoardUser.setBoard(board);

        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.of(adminBoardUser));
        doThrow(new NotAdminException("Not admin")).when(groupMemberService).checkAdmin(5, 1);

        assertThatThrownBy(() -> boardUserService.addUserToBoard(1, 10, 2))
                .isInstanceOf(NotAdminException.class);

        verify(boardUserRepository, never()).save(any());
    }

    @Test
    void removeUserFromBoard_success() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);
        BoardUser adminBoardUser = new BoardUser();
        adminBoardUser.setBoard(board);

        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.of(adminBoardUser));
        doNothing().when(groupMemberService).checkAdmin(5, 1);
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 2)).thenReturn(true);

        boardUserService.removeUserFromBoard(1, 10, 2);

        verify(boardUserRepository).deleteById(new BoardUserId(10, 2));
    }

    @Test
    void removeUserFromBoard_targetNotOnBoard_throws() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);
        BoardUser adminBoardUser = new BoardUser();
        adminBoardUser.setBoard(board);

        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.of(adminBoardUser));
        doNothing().when(groupMemberService).checkAdmin(5, 1);
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 2)).thenReturn(false);

        assertThatThrownBy(() -> boardUserService.removeUserFromBoard(1, 10, 2))
                .isInstanceOf(BoardAccessDeniedException.class);

        verify(boardUserRepository, never()).deleteById(any());
    }

    @Test
    void grantAccessToAdmins_grantsOnlyNewAdmins() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);

        User admin1 = new User();
        admin1.setId(1);
        User admin2 = new User();
        admin2.setId(2);
        when(groupMemberService.getAdmins(5)).thenReturn(List.of(admin1, admin2));

        BoardUser existingAccess = new BoardUser();
        existingAccess.setId(new BoardUserId(10, 1));
        when(boardUserRepository.findByBoardId(10)).thenReturn(List.of(existingAccess));

        boardUserService.grantAccessToAdmins(board);

        verify(boardUserRepository).saveAll(argThat(list -> {
            List<BoardUser> buList = (List<BoardUser>) list;
            return buList.size() == 1 && buList.get(0).getUser() == admin2;
        }));
    }

    @Test
    void grantAccessToAdmins_noAdmins_doesNothing() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);

        when(groupMemberService.getAdmins(5)).thenReturn(List.of());

        boardUserService.grantAccessToAdmins(board);

        verify(boardUserRepository, never()).saveAll(any());
    }

    @Test
    void getUsersBoard_success() {
        GroupTeam group = new GroupTeam();
        group.setId(5);
        Board board = new Board();
        board.setId(10);
        board.setGroup(group);
        BoardUser boardUser = new BoardUser();
        boardUser.setBoard(board);

        when(boardUserRepository.findByUserIdAndBoardId(1, 10)).thenReturn(Optional.of(boardUser));
        doNothing().when(groupMemberService).checkMember(5, 1);

        User user = new User();
        BoardUser memberAccess = new BoardUser();
        memberAccess.setUser(user);
        when(boardUserRepository.findByBoardId(10)).thenReturn(List.of(memberAccess));

        UserDto dto = new UserDto();
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserDto> result = boardUserService.getUsersBoard(1, 10);

        assertThat(result).containsExactly(dto);
    }
}
