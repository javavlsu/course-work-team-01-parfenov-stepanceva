package ru.ispi.kanban.entity.composiveKey;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardUserId implements Serializable {
    private Integer boardId;
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardUserId that)) return false;
        return Objects.equals(boardId, that.boardId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, userId);
    }
}

//переопределяем метод equals и hashCode потому что
//Hibernate хранит entity в cache/map типа Map<Id, Entity>
// из за этого он не понимает это тот же ключ или новый?
