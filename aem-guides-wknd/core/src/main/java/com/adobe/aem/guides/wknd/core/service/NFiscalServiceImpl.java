package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.FormatacaoNfiscal;
import com.adobe.aem.guides.wknd.core.interfaces.NFiscaisDao;
import com.adobe.aem.guides.wknd.core.interfaces.NotaFiscalService;
import com.adobe.aem.guides.wknd.core.models.NotaFiscal;
import com.adobe.aem.guides.wknd.core.models.NotaFiscalFormat;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true,service = NotaFiscalService.class)
public class NFiscalServiceImpl implements NotaFiscalService {

    private Gson gson = new Gson();
    @Reference
    private NFiscaisDao nFiscaisDao;
    @Reference
    private FormatacaoNfiscal formatacaoNfiscal;


    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String numeroNF = request.getParameter("numero");
        try {
            if (numeroNF != null) {
                long numero = Long.parseLong(numeroNF);
                List<NotaFiscalFormat> listFormatada = new ArrayList<>();
                listFormatada = formatacaoNfiscal.formatNF(numero);
                String json = gson.toJson(listFormatada);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("aplication/json");
                response.getWriter().write(json);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
//            String msgErro = msgErroService.msgJson(e.getMessage());
            try {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Erro ao encontratrar Nota Fiscal"+e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
//           TypeToken tt = new TypeToken<List<NotaFiscal>>() {
//           };
            request.setCharacterEncoding("UTF-8");
            String jsonStr = IOUtils.toString(request.getReader());
//           response.getWriter().write(jsonStr);
            NotaFiscal notaFiscal = gson.fromJson(jsonStr, NotaFiscal.class);
            if(nFiscaisDao.existe(notaFiscal.getNumero())){
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Já existe Nota Fiscal com esse numero");
            }else{
                for (Produto produto : notaFiscal.getListaProdutos()) {
                    NotaFiscal nf = new NotaFiscal(notaFiscal.getNumero(), produto.getId(), notaFiscal.getIdCliente(), produto.getPreco());
                    nFiscaisDao.salvarNF(notaFiscal);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
//           String msgErro = msgErroService.msgJson(e.getMessage());
            try {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String numero = request.getParameter("numero");
        try {
            if (numero != null & numero != "") {
                long numNF = Long.parseLong(numero);
                nFiscaisDao.deletarNF(numNF);
                response.getWriter().write("Produto deletado");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException ne) {
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Parâmetro não é um numero - Digite um numero válido: "+ne.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        } catch (Exception e) {
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Erro ao deletar NFiscal cod. erro: " + e.getMessage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

        }
    }

}