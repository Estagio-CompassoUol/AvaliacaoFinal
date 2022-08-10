package br.com.compassuol.core.servlets;

import br.com.compassuol.core.exceptions.EmailAlreadyRegisteredException;
import br.com.compassuol.core.exceptions.UserNotFoundException;
import br.com.compassuol.core.models.User;
import br.com.compassuol.core.services.UserService;
import com.google.gson.Gson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(AemContextExtension.class)
public class UserServletTest {

    private static final Gson GSON = new Gson();

    @Mock
    private UserService userServiceMock;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    private UserServlet userServlet;


    @BeforeEach
    void setup(AemContext context) {
        MockitoAnnotations.openMocks(this);
        userServlet = new UserServlet(userServiceMock);
        
        request = context.request();
        response = context.response();
    }

    @Test 
    void doGetShouldReturnAllUsersWhenQueryParamIsMissing() {
        Mockito.when(userServiceMock.findAllUsers()).thenReturn(getUserList());

        try {
            userServlet.doGet(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals(GSON.toJson(getUserList()), response.getOutputAsString());
    }


    @Test
    void doGetShouldReturnUserWhenQueryParamIsPassed() {
        request.addRequestParameter("id", "1");

        User user = new User(1, "John Doe", "johndoe@mail.com", "123456");
        Mockito.when(userServiceMock.findUserById(1)).thenReturn(user);

        try {
            userServlet.doGet(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals(GSON.toJson(user), response.getOutputAsString());
    }


    @Test
    void doGetShouldReturnNotFoundWhenUserIsNotRegistered() {
        request.addRequestParameter("id", "1");

        Mockito.when(userServiceMock.findUserById(1)).thenThrow(new UserNotFoundException("User not found"));

        try {
            userServlet.doGet(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"User not found\"}", response.getOutputAsString());
    }


    @Test 
    void doPostShouldSaveUserWhenRequestBodyIsValidAndEmailIsAvailable() {
        User user = getUserList().get(0);
        user.setId(null);
        
        String requestBodyJson = GSON.toJson(user);

        request.setContent(requestBodyJson.getBytes(StandardCharsets.UTF_8));
        
        try {
            userServlet.doPost(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());

        Mockito.verify(userServiceMock, times(1)).saveUser(userCaptor.capture());

        user = userCaptor.getValue();

        Assertions.assertNull(user.getId());
        Assertions.assertEquals("John Doe", user.getName());
        Assertions.assertEquals("johndoe@mail.com", user.getEmail());
        Assertions.assertEquals("123456", user.getPassword());
    }


    @Test
    void doPostShouldReturnConflictWhenEmailIsNotAvailable() {
        User user = getUserList().get(0);

        String requestBodyJson = GSON.toJson(user);

        request.setContent(requestBodyJson.getBytes(StandardCharsets.UTF_8));

        Mockito.doThrow(new EmailAlreadyRegisteredException("Email already registered"))
                .when(userServiceMock).saveUser(Mockito.any());

        try {
            userServlet.doPost(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"Email already registered\"}", response.getOutputAsString());
    }


    @Test 
    void doPostShouldReturnBadRequestWhenThereAreMissingArguments() {
        try {
            userServlet.doPost(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"Missing JSON parameters\"}", response.getOutputAsString());
    }


    @Test 
    void doPutShouldUpdateUserWhenRequestBodyIsValid() {
        String requestBodyJson = GSON.toJson(getUserList().get(0));

        request.setContent(requestBodyJson.getBytes(StandardCharsets.UTF_8));
        
        try {
            userServlet.doPut(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());

        Mockito.verify(userServiceMock, times(1)).updateUser(userCaptor.capture());

        User user = userCaptor.getValue();

        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals("John Doe", user.getName());
        Assertions.assertEquals("johndoe@mail.com", user.getEmail());
        Assertions.assertEquals("123456", user.getPassword());
    }


    @Test
    void doPutShouldReturnNotFoundWhenUserToBeUpdatedIsNotFound() {
        String requestBodyJson = GSON.toJson(getUserList().get(0));

        request.setContent(requestBodyJson.getBytes(StandardCharsets.UTF_8));

        Mockito.doThrow(new UserNotFoundException("User not found"))
                .when(userServiceMock).updateUser(Mockito.any());

        try {
            userServlet.doPut(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"User not found\"}", response.getOutputAsString());
    }


    @Test
    void doPutShouldReturnConflictWhenNewEmailIsNotAvailable() {
        User user = getUserList().get(0);

        String requestBodyJson = GSON.toJson(user);

        request.setContent(requestBodyJson.getBytes(StandardCharsets.UTF_8));

        Mockito.doThrow(new EmailAlreadyRegisteredException("Email already registered"))
                .when(userServiceMock).updateUser(Mockito.any());

        try {
            userServlet.doPut(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"Email already registered\"}", response.getOutputAsString());
    }


    @Test 
    void doPutShouldReturnBadRequestWhenThereAreMissingArguments() {
        try {
            userServlet.doPut(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"Missing JSON parameters\"}", response.getOutputAsString());
    }


    @Test 
    void doDeleteShouldDeleteUserWhenRequestBodyIsValid() {
        request.addRequestParameter("id", "1");
        
        try {
            userServlet.doDelete(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Mockito.verify(userServiceMock, times(1)).deleteUser(1);

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
    }


    @Test
    void doDeleteShouldReturnNotFoundWhenUserToBeDeletedIsNotRegistered() {
        request.addRequestParameter("id", "1");

        Mockito.doThrow(new UserNotFoundException("User not found"))
                .when(userServiceMock).deleteUser(Mockito.any());

        try {
            userServlet.doDelete(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"User not found\"}", response.getOutputAsString());
    }


    @Test
    void doDeleteShouldReturnBadRequestWhenQueryParamIsMissing() {
        try {
            userServlet.doDelete(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"Missing query parameter\"}", response.getOutputAsString());
    }


    private static List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "John Doe", "johndoe@mail.com", "123456"));
        userList.add(new User(2, "Mary", "mary@mail.com", "654321"));

        return userList;
    }
}
