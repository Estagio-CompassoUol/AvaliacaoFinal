package br.com.compassuol.core.service;

import br.com.compassuol.core.daos.UserDao;
import br.com.compassuol.core.exceptions.EmailAlreadyRegisteredException;
import br.com.compassuol.core.models.User;
import br.com.compassuol.core.services.UserService;
import br.com.compassuol.core.services.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.never;

public class UserServiceTest {

    @Mock
    private UserDao userDaoMock;

    private UserService userService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService =  new UserServiceImpl(userDaoMock);
    }


    @Test
    void shouldNotSaveUserWhenEmailIsAlreadyRegistered() {
        User user = getUserList().get(0);

        Mockito.when(userDaoMock.findByEmail("john@mail.com"))
                .thenThrow(new EmailAlreadyRegisteredException("Email already registered"));

        try {
            userService.saveUser(user);
            Assertions.fail();
        }
        catch (EmailAlreadyRegisteredException e) {
            Assertions.assertEquals("Email already registered", e.getMessage());
        }
        finally {
            Mockito.verify(userDaoMock, never()).save(user);
        }
    }


    @Test
    void shouldNotUpdateUserWhenEmailIsAlreadyRegistered() {
        User user = getUserList().get(0);

        Mockito.when(userDaoMock.findByEmail("john@mail.com"))
                .thenThrow(new EmailAlreadyRegisteredException("Email already registered"));

        try {
            userService.updateUser(user);
            Assertions.fail();
        }
        catch (EmailAlreadyRegisteredException e) {
            Assertions.assertEquals("Email already registered", e.getMessage());
        }
        finally {
            Mockito.verify(userDaoMock, never()).update(user);
        }
    }


    // TODO: implement other cases


    List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "John", "john@mail.com", "123456"));
        userList.add(new User(2, "Mary", "mary@mail.com", "654321"));

        return userList;
    }
}