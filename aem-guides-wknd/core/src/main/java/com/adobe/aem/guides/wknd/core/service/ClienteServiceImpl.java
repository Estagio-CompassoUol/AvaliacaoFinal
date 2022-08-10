package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.ClienteDao;
import com.adobe.aem.guides.wknd.core.interfaces.ClienteService;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component(immediate = true,service = ClienteService.class)
public class ClienteServiceImpl implements ClienteService{
    Gson gson = new Gson();

    @Reference
    ClienteDao clienteDao;

    @Override
    public void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        List<Cliente> listaCliente = new ArrayList<>();
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
                   resp.setContentType("aplication/json");
                   resp.getWriter().write(" - "+cliente.getNome() + " ja existe");
               }

           }
           resp.setContentType("aplication/json");
           resp.getWriter().write("Produto(s) Cadastrado");
       } catch (IOException e) {
           try {
               resp.setContentType("aplication/json");
               resp.getWriter().write(e.getMessage()+ " Erro ao salvar cliente no banco de dados");
           } catch (IOException ex) {
               throw new RuntimeException(ex);
           }
       }catch (Exception e){
           try {
               resp.setContentType("aplication/json");
               resp.getWriter().write(e.getMessage()+ " Erro ao salvar cliente no banco de dados");
           } catch (IOException ex) {
               throw new RuntimeException(ex);
           }
       }

    }

    @Override
    public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
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
            resp.setContentType("aplication/json");
            resp.getWriter().write(json);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try {
            resp.getWriter().write("Put Service funcionando");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try{
            String id = req.getParameter("id");
//            if (id !=null | !id.isEmpty()){
                int idDel = Integer.parseInt(id);
                clienteDao.deletar(idDel);

                resp.getWriter().write("Produto deletado");
//            }else{
//                TypeToken tt = new TypeToken<List<Produto>>() {
//                };
//                String jsonAtual = IOUtils.toString(req.getReader());
//                List<Produto> listProduto = gson.fromJson(jsonAtual, tt.getType());
//                for (Produto produto:listProduto) {
//                    clienteDao.deletar(produto.getId());
//                }
//                resp.getWriter().write(msgErroService.msgJson("Produtos deletados com sucesso"));
//            }

        } catch (Exception e) {
            try {
                resp.getWriter().write( e.getMessage()+ " Ocorreu um erro, produto nao pode ser deletado");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
