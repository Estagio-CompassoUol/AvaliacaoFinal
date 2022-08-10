package br.com.compassuol.core.servlets;

import br.com.compassuol.core.exceptions.EmailAlreadyRegisteredException;
import br.com.compassuol.core.exceptions.UserNotFoundException;
import br.com.compassuol.core.models.User;
import br.com.compassuol.core.services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component(service = Servlet.class, property = {
        "sling.servlet.paths=" + "/bin/users",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.methods=" + HttpConstants.METHOD_PUT,
        "sling.servlet.methods=" + HttpConstants.METHOD_DELETE
})
public class UserServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    private final UserService userService;

    @Activate
    public UserServlet(@Reference UserService userService) {
        this.userService = userService;
    }


    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws IOException {

        int statusCode = HttpServletResponse.SC_OK;
        String jsonBody;

        String param = req.getParameter("id");

        try {
            if (param != null) {
                User user = userService.findUserById(Integer.parseInt(param));
                jsonBody = new Gson().toJson(user);
            }
            else {
                List<User> users = userService.findAllUsers();
                jsonBody = new Gson().toJson(users);
            }
        }
        catch (UserNotFoundException e) {
            statusCode = HttpServletResponse.SC_NOT_FOUND;
            jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
        }
        catch (Exception e) {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
        }
        buildResponse(resp, statusCode, jsonBody);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        int statusCode;
        String jsonBody = "";

        try {
            String body = IOUtils.toString(req.getReader());
            JsonObject parsedBody = JsonParser.parseString(body).getAsJsonObject();

            String name = parsedBody.get("name").getAsString();
            String email = parsedBody.get("email").getAsString();
            String password = parsedBody.get("password").getAsString();

            try {
                User user = new User(null, name, email, password);
                userService.saveUser(user);

                statusCode = HttpServletResponse.SC_CREATED;
            }
            catch (EmailAlreadyRegisteredException e) {
                statusCode = HttpServletResponse.SC_CONFLICT;
                jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
            }
            catch (Exception e) {
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
            }
        }
        catch (Exception e) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonBody = "{\"message\": \"Missing JSON parameters\"}";
        }

        buildResponse(resp, statusCode, jsonBody);
    }

    @Override
    protected void doPut(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        int statusCode = HttpServletResponse.SC_OK;
        String jsonBody = "";

        try {
            String body = IOUtils.toString(req.getReader());
            JsonObject parsedBody = JsonParser.parseString(body).getAsJsonObject();

            Integer id = parsedBody.get("id").getAsInt();
            String name = parsedBody.get("name").getAsString();
            String email = parsedBody.get("email").getAsString();
            String password = parsedBody.get("password").getAsString();

            try {
                User user = new User(id, name, email, password);
                userService.updateUser(user);
            }
            catch (UserNotFoundException e) {
                statusCode = HttpServletResponse.SC_NOT_FOUND;
                jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
            }
            catch (EmailAlreadyRegisteredException e) {
                statusCode = HttpServletResponse.SC_CONFLICT;
                jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
            }
        }
        catch (Exception e) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonBody = "{\"message\": \"Missing JSON parameters\"}";
        }
        buildResponse(resp, statusCode, jsonBody);
    }

    @Override
    protected void doDelete(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        int statusCode = HttpServletResponse.SC_OK;
        String jsonBody = "";

        try {
            int id = Integer.parseInt(req.getParameter("id"));

            try {
                userService.deleteUser(id);
            }
            catch (UserNotFoundException e) {
                statusCode = HttpServletResponse.SC_NOT_FOUND;
                jsonBody = "{\"message\": \"" + e.getMessage() + "\"}";
            }
        }
        catch (Exception e) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonBody = "{\"message\": \"Missing query parameter\"}";
        }
        buildResponse(resp, statusCode, jsonBody);
    }


    private static void buildResponse(SlingHttpServletResponse resp, int statusCode, String jsonBody) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(statusCode);
        resp.getWriter().write(jsonBody);
    }
}

