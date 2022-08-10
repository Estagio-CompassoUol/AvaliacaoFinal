package br.com.compassuol.core.daos;

import br.com.compassuol.core.exceptions.UserNotFoundException;
import br.com.compassuol.core.models.User;
import org.osgi.service.component.annotations.Component;

import java.util.*;

@Component(service = UserDao.class, immediate = true)
public class UserDaoImpl implements UserDao {

    private static final Map<Integer, User> users;

    private static Integer lastId = 0;

    static {
        users = new HashMap<>();

        List<User> userList = new ArrayList<>();
        userList.add(new User(++lastId, "John Doe", "john.doe@mail.com", "123456"));
        userList.add(new User(++lastId, "Mary Jane", "mary.jane@mail.com", "123456"));
        userList.add(new User(++lastId, "Peter Parker", "peter.parker@mail.com", "123456"));
        userList.add(new User(++lastId, "Bruce Wayne", "bruce.wayne@mail.com", "123456"));
        userList.add(new User(++lastId, "Tony Stark", "tony.stark@mail.com", "123456"));

        userList.forEach(user -> {
            users.put(user.getId(), user);
        });
    }

    @Override
    public void save(User user) {
        int newId = ++lastId;

        user.setId(newId);
        users.put(newId, user);
    }

    @Override
    public User findById(int id) {
        User user = users.get(id);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User findByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (users.get(user.getId()) == null) {
            throw new UserNotFoundException("User not found");
        }
        users.put(user.getId(), user);
    }

    @Override
    public void delete(int id) {
        User removed = users.remove(id);

        if (removed == null) {
            throw new UserNotFoundException("User not found");
        }
    }
}