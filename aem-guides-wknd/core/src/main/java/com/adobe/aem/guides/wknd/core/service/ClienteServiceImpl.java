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
import javax.servlet.http.HttpSession;
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
               boolean admin = clienteDao.admin(cliente);
               if(exis==false & admin==false) {
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
        HttpSession sessao = req.getSession();
        String idCliente = req.getParameter("id");
        String json="";

        try {
            if(sessao.getAttribute("usuario")!=null){
            Cliente usuario = (Cliente) sessao.getAttribute("usuario");
                if (idCliente==null){
                    Cliente cliente = clienteDao.getClientesById(usuario.getId());
                    json = gson.toJson(cliente);
                    resp.getWriter().write(json);
                }else{
                    resp.getWriter().write(msgService.msgJson ("permissão de parametro para administrador"));
                }
            }else if(sessao.getAttribute("usuarioAdm")!=null){
                if (idCliente!=null){
                    int id = Integer.parseInt(idCliente);
                    Cliente cliente = clienteDao.getClientesById(id);
                    if(cliente!=null){
                        json = gson.toJson(cliente);
                    }else{
                        throw new RuntimeException(msgService.msgJson ("Usuário não encontrado"));
                    }
                }else{
                    List<Cliente> list = clienteDao.getClientes();
                    json = gson.toJson(list);
                }
                resp.getWriter().write(json);
            }else{
                resp.getWriter().write(msgService.msgJson ("usuário não logado"));
            }

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
                resp.getWriter().write(msgService.msgDuploJson (e.getMessage(), "Clientes não localizados ou inexistentes"));
            } catch (IOException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    @Override
    public void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("aplication/json");
        HttpSession sessao = req.getSession();
        try {
            if(sessao.getAttribute("usuario")!=null) {
                Cliente cliente= (Cliente) sessao.getAttribute("usuario");
                String jsonAtual = IOUtils.toString(req.getReader());
                Cliente clienteAtualizar = gson.fromJson(jsonAtual, Cliente.class);
                boolean saoIguais= cliente.getId()==clienteAtualizar.getId();
                if(clienteAtualizar!=null & saoIguais==true) {
                   clienteDao.update(clienteAtualizar);
                   resp.getWriter().write(msgService.msgJson("Cliente Atualizado com Sucesso"));
                }else{
                    throw new RuntimeException("Dados diferentes do usuário logado");
                }
            } else if(sessao.getAttribute("usuarioAdm")!=null){
                    TypeToken tt = new TypeToken<List<Cliente>>() {
                    };
                    String jsonAtual = IOUtils.toString(req.getReader());
                    List<Cliente> listCliente = gson.fromJson(jsonAtual, tt.getType());
                    for (Cliente clienteD : listCliente) {
                        if (clienteD.getId() != 0 & clienteDao.getClientesById (clienteD.getId())!=null) {
                            clienteDao.update(clienteD);
                            resp.getWriter().write(msgService.msgJson("Clientes atualizados com sucesso"));
                        } else {
                            throw new NumberFormatException("cliente não localizado");
                        }
                    }
            }else{
                resp.getWriter().write(msgService.msgJson("E necessário logar"));
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
                resp.getWriter().write(msgService.msgDuploJson(e.getMessage(), "Usuário nao pode ser atualizado"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("aplication/json");
        HttpSession sessao = req.getSession();
        String id=req.getParameter("id");

        try {
            if(sessao.getAttribute("usuario")!=null) {
                Cliente cliente= (Cliente) sessao.getAttribute("usuario");
                TypeToken tt = new TypeToken<List<Cliente>>() {
                };
                String jsonStr = IOUtils.toString(req.getReader());
                List<Cliente> listaCliente = gson.fromJson(jsonStr, tt.getType());
                for (Cliente clienteVer:listaCliente) {
                    if (id==null & clienteVer.getId()!=0) {
                        boolean saoIguais= cliente.getId()==clienteVer.getId();
                        if(saoIguais==true) {
                            int idDel = clienteVer.getId();
                            if (clienteDao.getClientesById(idDel) != null) {
                                clienteDao.deletar(idDel);
                                resp.getWriter().write(msgService.msgJson("Cliente deletado"));
                            }
                        }else{
                            throw new RuntimeException("Atualizaçao de outros Cliente somente conta administrador");
                        }
                    }else{
                        resp.getWriter().write(msgService.msgJson ("permissão de parametro para administrador"));
                    }
                }
            } else if(sessao.getAttribute("usuarioAdm")!=null) {
                if (id == null) {
                    TypeToken tt = new TypeToken<List<Cliente>>() {
                    };
                    String jsonAtual = IOUtils.toString(req.getReader());
                    List<Cliente> listCliente = gson.fromJson(jsonAtual, tt.getType());
                    for (Cliente clienteD : listCliente) {
                        if (clienteD.getId() != 0) {
                            clienteDao.deletar(clienteD.getId());
                        } else {
                            throw new NumberFormatException();
                        }
                    }
                    resp.getWriter().write(msgService.msgJson("Clientes deletados com sucesso"));
                } else{
                    int idDel = Integer.parseInt(id);
                    if(clienteDao.getClientesById(idDel)!=null) {
                        clienteDao.deletar(idDel);
                        resp.getWriter().write(msgService.msgJson("Cliente deletado"));
                    }
                }
            }else{
                resp.getWriter().write(msgService.msgJson("E necessário logar"));
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
                resp.getWriter().write(msgService.msgDuploJson(e.getMessage(), "Cliente nao pode ser deletado"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
