package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.MsgErroService;
import com.adobe.aem.guides.wknd.core.interfaces.ProdutoDao;
import com.adobe.aem.guides.wknd.core.interfaces.ProdutoService;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.swing.plaf.IconUIResource;
import java.io.IOException;
import java.util.List;


@Component(immediate = true,service = ProdutoService.class)
public class ProdutoServiceImpl implements ProdutoService {

    Gson gson = new Gson();
    @Reference
    private ProdutoDao produtoDao;
    @Reference
    private MsgErroService msg;

    @Override
    public void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try {
            TypeToken tt = new TypeToken<List<Produto>>() {
            };
            String jsonStr = IOUtils.toString(req.getReader());
            List<Produto> listaproduto = gson.fromJson(jsonStr, tt.getType());
            for (Produto produto:listaproduto) {
                if(produtoDao.existe(produto.getNome())==false){
                    produtoDao.setSalvar(produto);
                }
            }
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("aplication/json");
            resp.getWriter().write(msg.msgJson("Produto(s) Cadastrado"));
        } catch (IOException ex) {
            try {
                resp.setContentType("aplication/json");
                resp.setCharacterEncoding("UTF-8");
                String msnErr = msg.msgErroJson (ex.getMessage()," Conteudo vazio");
                resp.getWriter().write(String.valueOf(msnErr));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }catch (Exception e){
            try {
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("aplication/json");
               String msnErr = msg.msgErroJson(e.getMessage()," Conteudo nao e um json válido");
                resp.getWriter().write(String.valueOf(msnErr));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try {
            String idReq = req.getParameter("id");
            String palavraChave = req.getParameter("pk");
            String categor = req.getParameter("categ");
            String orderByPreco = req.getParameter("orderPreco");

            if (idReq != null){
                int idGet = Integer.parseInt(idReq);
                String json = gson.toJson(produtoDao.getFiltroId(idGet));
                if(json.equals("null")) {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(msg.msgJson("Produto não localizado"));
                }else{
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(json);
                }
            } else if (palavraChave!=null) {
                String json = gson.toJson(produtoDao.getFiltroPalavraChave(palavraChave));
                if(json.equals("null")) {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(msg.msgJson("Produto não localizado"));
                }else {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(json);
                }
            }else if (orderByPreco!=null) {
                String json=null;
                if (orderByPreco.equalsIgnoreCase("menor")) {
                    json = gson.toJson(produtoDao.getFiltroMenorPreco());
                }else if (orderByPreco.equalsIgnoreCase("maior")) {
                    json = gson.toJson(produtoDao.getFiltroMaiorPreco());
                }
                if(json==(null)) {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(msg.msgJson("Parâmetro de Ordenação incorreta"));
                }else{
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(json);
                }
            }else if (categor!=null){
                String json = gson.toJson(produtoDao.getFiltroCategoria(categor));
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("aplication/json");
                resp.getWriter().write(json);
            } else{
                List<Produto> list = produtoDao.getProdutos();
                String json = gson.toJson(list);
                if(json.equalsIgnoreCase("[]")){
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(msg.msgJson("Não há produto no Banco de Dados"));
                }else {
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(json);
                }
            }
        } catch (Exception e) {
            try {
                resp.setContentType("aplication/json");
                resp.setCharacterEncoding("UTF-8");
                String ms=e.getMessage();
                resp.getWriter().write(msg.msgErroJson(ms, "Erro ao listar Produtos"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @Override
    public void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try{
            int cont=0;
            String msgAlerta="";
            String msgAviso="";
            TypeToken tt = new TypeToken<List<Produto>>() {
            };
            String jsonAtual = IOUtils.toString(req.getReader());
            List<Produto> listProduto = gson.fromJson(jsonAtual, tt.getType());
            for (Produto produto:listProduto) {
                cont++;
                if(produtoDao.getFiltroId(produto.getId())!=null) {
                    produtoDao.update(produto);
                    msgAviso="Produto(s) Atualizado(s)";
                }else{
                        msgAlerta="Algum(s) do(s) produto(s) não foram localizados";
                }
            }resp.setCharacterEncoding("UTF-8");
            resp.setContentType("aplication/json");
            resp.getWriter().write(msg.msgDuploJson(msgAlerta,msgAviso));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp) {
        try{
            String id = req.getParameter("id");
            if (id !=null){
                int idDel = Integer.parseInt(id);
                if (produtoDao.getFiltroId(idDel)!=null) {
                    produtoDao.deletar(idDel);
                    resp.setContentType("aplication/json");
                    resp.getWriter().write(msg.msgJson("Produto deletado"));
                }else{
                    resp.setContentType("aplication/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(msg.msgJson("Produto não existe, digite um id válido"));
                }
            }else{
                TypeToken tt = new TypeToken<List<Produto>>() {
                };
                String jsonAtual = IOUtils.toString(req.getReader());
                List<Produto> listProduto = gson.fromJson(jsonAtual, tt.getType());
                for (Produto produto:listProduto) {
                    produtoDao.deletar(produto.getId());
                }
                resp.setContentType("aplication/json");
                resp.getWriter().write(msg.msgJson("Produtos deletados com sucesso"));
            }
        } catch (Exception e) {
            try {
                resp.setContentType("aplication/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(msg.msgErroJson(e.getMessage(), "Ocorreu um erro, produto não pôde ser deletado, parametro vazio o inválido"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
