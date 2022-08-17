package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.*;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.adobe.aem.guides.wknd.core.models.NotaFiscal;
import com.adobe.aem.guides.wknd.core.models.Produto;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true,service = RelatorioService.class)
public class RelatorioServiceImpl implements RelatorioService {
    @Reference
    private NFiscaisDao nFiscaisDao;

    @Reference
    private FormatacaoNfiscal formatacaoNfiscal;

    @Reference
    private MsgService msgService;
    @Reference
    private ProdutoDao produtoDao;

    @Override
    public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException {
        String idCliente = req.getParameter("idCliente");
        HttpSession sessao = req.getSession();
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/html");
        List<NotaFiscal> listaNFCProdutos=new ArrayList<>();
        try {
            if (sessao.getAttribute("usuario") != null) {
                Cliente usuario = (Cliente) sessao.getAttribute("usuario");
                List<NotaFiscal> listaNF = new ArrayList<>();
                listaNF.addAll(nFiscaisDao.listaPorIdCliente(usuario.getId()));
                if (usuario != null & listaNF.size() > 0 & idCliente == null) {
                    for (NotaFiscal nf : listaNF) {
                        List<Produto> listaProd = new ArrayList<>();
                        int idProd = nf.getIdProduto();
                        Produto produto = produtoDao.getFiltroId(idProd);
                        listaProd.add(new Produto(produto.getId(), produto.getNome(), produto.getPreco()));
                        listaNFCProdutos.add(new NotaFiscal(nf.getNumero(), listaProd));
                    }
                    try {
                        retornoHTML(req, resp, listaNFCProdutos);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (idCliente != null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write(msgService.msgJson("Precisa esta logado como Administrador para acessar outro usuário"));
                } else {
                    try {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(msgService.msgJson("Parametro inválido ou Nenhuma compra localizada para esse usuário "));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (sessao.getAttribute("usuarioAdm") != null) {
                if (idCliente != null) {
                    int id = Integer.parseInt(idCliente);
                    List<NotaFiscal> listaNF = nFiscaisDao.listaPorIdCliente(id);
                    if (listaNF.get(0).getNumero() != 0) {
                        try {
                            retornoHTML(req, resp, listaNF);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write(msgService.msgJson("Parametro inválido ou Nenhuma compra localizada para esse usuário "));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    try {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(msgService.msgJson("Parametro inválido"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }catch (Exception e){
            try {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(msgService.msgDuploJson(e.getMessage(),"Erro ao buscar relatório"));
            } catch (IOException ie) {
                throw new RuntimeException(e);
            }
        }
    }

    public PrintWriter retornoHTML(SlingHttpServletRequest req, SlingHttpServletResponse resp, List<NotaFiscal> listaNF) {
        PrintWriter out;
        try {
            out = resp.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=ISO-8859-1>");
            out.println("<title>Relatório por cliente</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<br><h1> Relatorio Solicitado:</h1> <br> <br>");
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>NOME</th>");
            out.println("<th>PREÇO</th>");
            out.println("<th>NUMERO NF</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            for (NotaFiscal nf:listaNF) {
                out.println("<tr>");
                for (Produto prod:nf.getListaProdutos()) {
                    out.println("<th>"+prod.getId()+"</th>");
                    out.println("<th>"+prod.getNome()+"</th>");
                    out.println("<th>"+prod.getPreco()+"</th>");
                    out.println("<th>"+ nf.getNumero()+"</th>");
                }
            }
            out.println("</tr>");
            out.println("<tbody>");
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
                throw new RuntimeException(e);
        }
        return out;
    }
}
