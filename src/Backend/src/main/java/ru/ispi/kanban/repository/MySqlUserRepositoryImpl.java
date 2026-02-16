package ru.ispi.kanban.repository;

import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public class MySqlUserRepositoryImpl implements UserRepository {
    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> FindById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> FindByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Integer id) {

    }
}
