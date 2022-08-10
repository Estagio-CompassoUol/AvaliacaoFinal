package br.com.compassuol.core.daos;

import br.com.compassuol.core.models.User;

import java.util.List;

public interface UserDao {

    User findById(int id);

    User findByEmail(String email);

    void save(User user);

    void update(User user);

    void delete(int id);

    List<User> findAll();
}
