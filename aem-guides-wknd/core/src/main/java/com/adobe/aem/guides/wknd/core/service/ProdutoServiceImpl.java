package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.DAO.ProdutoDao;
import com.adobe.aem.guides.wknd.core.models.MsnErro;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.List;


@Component(immediate = true,service = ProdutoService.class)
public class ProdutoServiceImpl implements ProdutoService {
    Gson gson = new Gson();
    @Reference
    private ProdutoDao produtoDao;

    @Override
    public void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try {
            TypeToken tt = new TypeToken<List<Produto>>() {
            };
            String jsonStr = IOUtils.toString(req.getReader());
            List<Produto> listaproduto = gson.fromJson(jsonStr, tt.getType());
            for (Produto produto:listaproduto) {
                int idteste = produto.getId();
                String nometeste = produto.getNome();
                String categoriateste = produto.getCategoria();
                System.out.println(idteste+" "+nometeste+" "+categoriateste);

                produtoDao.setSalvar(produto);
            }
            resp.setContentType("aplication/json");
            resp.getWriter().write("Produto(s) Cadastrado");
        } catch (IOException ex) {
            try {
                resp.setContentType("aplication/json");
                MsnErro msnErro = new MsnErro(ex.getMessage()," Conteudo vazio");
                resp.getWriter().write(String.valueOf(msnErro));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }catch (Exception e){
            try {
                resp.setContentType("aplication/json");
                MsnErro msnErro = new MsnErro(e.getMessage()," Conteudo nao e um json válido");
                resp.getWriter().write(String.valueOf(msnErro));

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try {
            String id = req.getParameter("id");
            if (id!=null & !(id.isEmpty()) & !(id.isBlank())){
                int idGet = Integer.parseInt(id);
                String json = gson.toJson(produtoDao.getFiltroId(idGet));
//                if(json!=null || json.isEmpty()){
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(json);
//                }else{
                    resp.setContentType("aplication/json");
                    resp.getWriter().write("parametro invalido");
//                }
//            }else if(id.isEmpty() | id.isBlank()) {
//                resp.setContentType("aplication/json");
//                resp.getWriter().write("parametro invalido");
            } else{
                List<Produto> list = produtoDao.getProdutos();
                String json = gson.toJson(list);
                resp.setContentType("aplication/json");
                resp.getWriter().write(json);
            }
        } catch (Exception e) {
            try {
                resp.setContentType("aplication/json");
                resp.getWriter().write(e.getMessage()+ " Erro ao listar Produtos");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try{
            String jsonAtual = IOUtils.toString(req.getReader());
            Produto produto = gson.fromJson(jsonAtual, Produto.class);
            produtoDao.update(produto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try{
            String id = req.getParameter("id");
            int idDel = Integer.parseInt(id);
            produtoDao.deletar(idDel);
            resp.getWriter().write("Produto deletado");
        } catch (Exception e) {
            try {
                resp.getWriter().write("Ocorreu um erro, produto nao pode ser deletado");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
