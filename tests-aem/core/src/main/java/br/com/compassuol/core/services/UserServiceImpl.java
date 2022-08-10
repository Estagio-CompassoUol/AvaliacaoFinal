package br.com.compassuol.core.services;

import br.com.compassuol.core.daos.UserDao;
import br.com.compassuol.core.exceptions.EmailAlreadyRegisteredException;
import br.com.compassuol.core.exceptions.UserNotFoundException;
import br.com.compassuol.core.models.User;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Objects;

@Component(service = UserService.class, immediate = true)
public class UserServiceImpl implements UserService {

    private final UserDao dao;

    @Activate
    public UserServiceImpl(@Reference UserDao dao) {
        this.dao = dao;
    }

    @Override
    public void saveUser(User user) {
        try {
            dao.findByEmail(user.getEmail());
            throw new EmailAlreadyRegisteredException("Email already registered");
        }
        catch (UserNotFoundException e) {
            dao.save(user);
        }
    }

    @Override
    public List<User> findAllUsers() {
        return dao.findAll();
    }

    @Override
    public User findUserById(Integer id) {
        return dao.findById(id);
    }

    @Override
    public void updateUser(User user) {
        User userToBeUpdated = dao.findById(user.getId());

        try {
            User emailOwner = dao.findByEmail(user.getEmail());
            boolean isSameUser = Objects.equals(emailOwner, userToBeUpdated);

            if (!isSameUser) {
                throw new EmailAlreadyRegisteredException("Email already registered");
            }
            dao.update(user);
        }
        catch (UserNotFoundException e) {
            dao.update(user);
        }
    }

    @Override
    public void deleteUser(Integer id) {
        dao.delete(id);
    }
}
