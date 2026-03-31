package ru.ispi.kanban.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.User;

import java.util.Optional;

@Repository
@Profile("local")
public interface UserRepository extends JpaRepository<User, Integer>{

    Optional<User> findByEmail(String email);
}
