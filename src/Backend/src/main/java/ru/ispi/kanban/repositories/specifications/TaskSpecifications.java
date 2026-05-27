package ru.ispi.kanban.repositories.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.payloads.TaskPageQuery;

import java.util.ArrayList;
import java.util.List;

public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> forBoardWithFilters(Integer boardId, TaskPageQuery query) {
        return (root, q, cb) -> {
            Join<Object, Object> columnJoin = root.join("column", JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(columnJoin.get("board").get("id"), boardId));

            if (query.search() != null && !query.search().isBlank()) {
                String pattern = "%" + query.search().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), pattern)
                ));
            }
            if (query.priority() != null) {
                predicates.add(cb.equal(root.get("priority"), query.priority()));
            }
            if (query.status() != null) {
                predicates.add(cb.equal(root.get("status"), query.status()));
            }
            if (query.columnId() != null) {
                predicates.add(cb.equal(columnJoin.get("id"), query.columnId()));
            }
            if (query.assigneeId() != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), query.assigneeId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
