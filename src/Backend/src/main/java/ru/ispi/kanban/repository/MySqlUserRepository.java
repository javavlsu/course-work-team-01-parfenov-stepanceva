package ru.ispi.kanban.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entity.User;

import java.util.Optional;

@Repository
@Profile("local")
public interface MySqlUserRepository extends JpaRepository<User, Integer>,
        UserRepository{

    Optional<User> findByEmail(String email);
}
