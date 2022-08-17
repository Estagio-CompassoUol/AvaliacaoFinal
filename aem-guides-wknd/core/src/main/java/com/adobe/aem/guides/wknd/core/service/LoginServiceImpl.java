package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.ClienteDao;
import com.adobe.aem.guides.wknd.core.interfaces.LoginService;
import com.adobe.aem.guides.wknd.core.interfaces.MsgService;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component(immediate = true,service = LoginService.class)
public class LoginServiceImpl implements LoginService {
    private Gson gson = new Gson();
    @Reference
    private ClienteDao clienteDao;
    @Reference
    private MsgService msgService;

    @Override
    public void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        HttpSession sessao = req.getSession();
        try {
            String jsonStr = IOUtils.toString(req.getReader());
            Cliente cliente = gson.fromJson(jsonStr, Cliente.class);
           Cliente usuario = clienteDao.login(cliente.getEmail(), cliente.getSenha());
            boolean admin = clienteDao.admin(cliente);
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("aplication/json");
            if (usuario != null) {
                sessao.setAttribute("usuario", usuario);
                resp.getWriter().write(msgService.msgJson("Usuário Logado"));
            }else if(admin == true) {
                usuario=(new Cliente(cliente.getEmail(), cliente.getSenha()));
                sessao.setAttribute("usuarioAdm", usuario);
                resp.getWriter().write(msgService.msgJson("Usuário Logado"));
            }else{
                resp.getWriter().write(msgService.msgJson("Usuário ou senha errados"));
            }
        } catch (Exception ex) {
            try {
                resp.getWriter().write(msgService.msgDuploJson(ex.getMessage(),"É necessário fazer Login"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String close = request.getParameter("status");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("aplication/json");
        String id = request.getParameter("id");
        try {
            if (close!=null && close.equalsIgnoreCase("close")) {
                HttpSession sessao = request.getSession();
                if (sessao.getAttribute("usuarioAdm") != null) {
                    sessao.removeAttribute("usuarioAdm");
                } else {
                    sessao.removeAttribute("usuario");
                }
                try {
                    response.getWriter().write(msgService.msgJson("Usuário fez logout"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                throw new RuntimeException();
            }
        }  catch (Exception e) {
            try{
                response.getWriter().write(msgService.msgDuploJson( e.getMessage(),"Parametro não aceito"));
            } catch (IOException eo) {
                throw new RuntimeException(eo);
            }
        }
    }

}
