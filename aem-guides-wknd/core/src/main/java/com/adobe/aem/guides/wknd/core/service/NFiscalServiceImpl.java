package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.FormatacaoNfiscal;
import com.adobe.aem.guides.wknd.core.interfaces.MsgService;
import com.adobe.aem.guides.wknd.core.interfaces.NFiscaisDao;
import com.adobe.aem.guides.wknd.core.interfaces.NotaFiscalService;
import com.adobe.aem.guides.wknd.core.models.NotaFiscal;
import com.adobe.aem.guides.wknd.core.models.NotaFiscalFormat;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component(immediate = true,service = NotaFiscalService.class)
public class NFiscalServiceImpl implements NotaFiscalService {

    private Gson gson = new Gson();
    @Reference
    private NFiscaisDao nFiscaisDao;
    @Reference
    private FormatacaoNfiscal formatacaoNfiscal;
    @Reference
    MsgService msgService;


    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("aplication/json");
        String numeroNF = request.getParameter("numero");
        try {
            if (numeroNF != null & !numeroNF.equals("")) {
                long numero = Long.parseLong(numeroNF);
                List<NotaFiscalFormat> listFormatada;
                listFormatada = formatacaoNfiscal.formatNF(numero);
                if(listFormatada.get(0).getNumero()!=0) {
                    String json = gson.toJson(listFormatada);
                    response.getWriter().write(json);
                }else{
                    throw new RuntimeException("Nota Fiscal não localizado");
                }
            }else{
                throw new RuntimeException("O numero da nota não pode ser vazio");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (NumberFormatException ne) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(msgService.msgDuploJson(ne.getMessage(), "Não é um parâmetro válido"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(msgService.msgDuploJson(e.getMessage(),"Erro ao encontrar Nota Fiscal"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("aplication/json");
        try {
            String jsonStr = IOUtils.toString(request.getReader());
            NotaFiscal notaFiscal = gson.fromJson(jsonStr, NotaFiscal.class);
            if(nFiscaisDao.existe(notaFiscal.getNumero())){
                response.getWriter().write(msgService.msgJson("Já existe Nota Fiscal com esse numero"));
            }else{
                for (Produto produto : notaFiscal.getListaProdutos()) {
                    NotaFiscal nf = new NotaFiscal(notaFiscal.getNumero(), produto.getId(), notaFiscal.getIdCliente(), produto.getPreco());
                    nFiscaisDao.salvarNF(nf);
                }
                response.getWriter().write(msgService.msgJson("Nota Fiscal lançada com sucesso"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (JsonSyntaxException jse) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write(msgService.msgDuploJson(jse.getMessage(),"Erro na estrutura do(s) dado(s) json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(msgService.msgDuploJson(e.getMessage(),"Erro ao salvar Nota Fiscal no banco de dados, Produto(s) nao localiazado(s)"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType("aplication/json");
        response.setCharacterEncoding("UTF-8");

        String numero = request.getParameter("numero");
        try {
            if (numero != null & numero != "") {
                long numNF = Long.parseLong(numero);
                if(nFiscaisDao.existe(numNF)) {
                    nFiscaisDao.deletarNF(numNF);
                    response.getWriter().write("Nota Fiscal deletada");
                } else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(msgService.msgJson("Nota Fiscal não localizado"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException ne) {
                try {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(msgService.msgDuploJson(ne.getMessage(), "Parâmetro não é um numero - Digite um numero válido"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        } catch (Exception e) {
                try {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(msgService.msgDuploJson(e.getMessage(),"Erro ao deletar NFiscal no BD"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
        }
    }

}