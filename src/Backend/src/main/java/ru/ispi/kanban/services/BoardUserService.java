package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.BoardUser;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;
import ru.ispi.kanban.repository.MySqlBoardUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardUserService {

    private final MySqlBoardUserRepository boardUserRepository;

    private final GroupMemberService groupMemberService;

    public boolean hasAccess(Integer boardId, Integer userId){
        return boardUserRepository
                .existsByIdBoardIdAndIdUserId(boardId, userId);
    }

    public void checkAccess(Integer boardId, Integer userId){

        boolean access = hasAccess(boardId, userId);

        if(!access){
            throw new RuntimeException("No access to this board");
        }
    }

    public List<Board> getUserBoards(Integer userId, Integer groupId){
        return boardUserRepository.findBoardsByUserAndGroup(userId, groupId);
    }

    public void grantAccess(Board board, User user){

        BoardUserId id = new BoardUserId(
                board.getId(),
                user.getId()
        );

        if(boardUserRepository.existsById(id)){
            return;
        }

        BoardUser boardUser = new BoardUser();
        boardUser.setId(id);
        boardUser.setBoard(board);
        boardUser.setUser(user);

        boardUserRepository.save(boardUser);
    }

    public void grantAccessToAdmins(Board board){

        List<User> admins =
                groupMemberService.getAdmins(board.getGroup().getId());

        for(User admin : admins){

            BoardUserId id =
                    new BoardUserId(board.getId(), admin.getId());

            if(boardUserRepository.existsById(id)){
                continue;
            }

            BoardUser access = new BoardUser();
            access.setId(id);
            access.setBoard(board);
            access.setUser(admin);

            boardUserRepository.save(access);
        }
    }

    public void removeAccess(Integer boardId, Integer userId){

        BoardUserId id = new BoardUserId(boardId, userId);

        boardUserRepository.deleteById(id);
    }
}
