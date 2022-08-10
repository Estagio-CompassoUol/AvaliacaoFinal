package br.com.compassuol.core.services;

import br.com.compassuol.core.models.User;

import java.util.List;

public interface UserService {

    void saveUser(User user);

    List<User> findAllUsers();

    User findUserById(Integer id);

    void updateUser(User user);

    void deleteUser(Integer id);
}
