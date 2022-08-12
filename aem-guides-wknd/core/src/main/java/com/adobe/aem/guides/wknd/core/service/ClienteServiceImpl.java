package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.ClienteDao;
import com.adobe.aem.guides.wknd.core.interfaces.ClienteService;
import com.adobe.aem.guides.wknd.core.interfaces.MsgService;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component(immediate = true,service = ClienteService.class)
public class ClienteServiceImpl implements ClienteService{
    Gson gson = new Gson();

    @Reference
    ClienteDao clienteDao;

    @Reference
    MsgService msgService;

    @Override
    public void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("aplication/json");

        List<Cliente> listaCliente = new ArrayList<>();
        int cont=0;
       try{
           TypeToken tt = new TypeToken<List<Cliente>>() {
           };
           String jsonStr = IOUtils.toString(req.getReader());
           listaCliente = gson.fromJson(jsonStr, tt.getType());
           for (Cliente cliente:listaCliente) {
               boolean exis = clienteDao.existe(cliente);
               if(exis==false) {
                   clienteDao.setSalvar(cliente);
               }else{
                   resp.getWriter().write(msgService.msgJson(cliente.getNome() + " ja existe"));
                   cont++;
               }
           }
           if (cont!= listaCliente.size()) {
               resp.getWriter().write(msgService.msgJson("Cliente(s) Cadastrado"));
           }
       }catch (IOException e) {
           try {
               resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               resp.getWriter().write(msgService.msgJson( "Dados obtidos não é um conteudo válido"));
           } catch (IOException ex) {
               throw new RuntimeException(ex);
           }
       }catch (Exception e){
           try {
               resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               resp.getWriter().write( msgService.msgJson("Dados incorreto, Erro ao salvar cliente no banco de dados"));
           } catch (IOException ex) {
               throw new RuntimeException(ex);
           }
       }
    }

    @Override
    public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("aplication/json");
        String json="";
        try {
            String idReq = req.getParameter("id");
            if (idReq != null){
                int id = Integer.parseInt(req.getParameter("id"));
                Cliente cliente = clienteDao.getClientesById(id);
                json = gson.toJson(cliente);
            }else{
                List<Cliente> list = clienteDao.getClientes();
                json = gson.toJson(list);
            }
            resp.getWriter().write(json);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (NumberFormatException nex){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                resp.getWriter().write(msgService.msgJson ("Parâmetro incorreto"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                resp.getWriter().write(msgService.msgDuploJson (e.getMessage(), "Produtos não localizados ou inexistentes"));
            } catch (IOException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    @Override
    public void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("aplication/json");
        try {
            resp.getWriter().write("Put Service funcionando");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("aplication/json");
        try{
            String id = req.getParameter("id");
            if (id !=null){
                int idDel = Integer.parseInt(id);
                clienteDao.deletar(idDel);
                resp.getWriter().write(msgService.msgJson("Produto deletado"));
            }else{
                TypeToken tt = new TypeToken<List<Cliente>>() {
                };
                String jsonAtual = IOUtils.toString(req.getReader());
                List<Cliente> listCliente = gson.fromJson(jsonAtual, tt.getType());
                for (Cliente cliente:listCliente) {
                    if(cliente.getId()!=0) {
                        clienteDao.deletar(cliente.getId());
                    }else{
                        throw new NumberFormatException();
                    }
                }
                resp.getWriter().write(msgService.msgJson("Produtos deletados com sucesso"));
            }
        }catch (NumberFormatException nex){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                resp.getWriter().write(msgService.msgJson ("Parâmetro incorreto"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            try {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(msgService.msgJson("Ocorreu um erro conexão com BD, produto nao pode ser deletado"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
