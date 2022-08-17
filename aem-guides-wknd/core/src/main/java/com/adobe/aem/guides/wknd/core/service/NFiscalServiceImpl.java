package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.FormatacaoNfiscal;
import com.adobe.aem.guides.wknd.core.interfaces.MsgService;
import com.adobe.aem.guides.wknd.core.interfaces.NFiscaisDao;
import com.adobe.aem.guides.wknd.core.interfaces.NotaFiscalService;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.adobe.aem.guides.wknd.core.models.NotaFiscal;
import com.adobe.aem.guides.wknd.core.models.NotaFiscalFormat;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
        HttpSession sessao = request.getSession();
        try {
            if(sessao.getAttribute("usuarioAdm")!=null) {
                if (numeroNF != null & !numeroNF.equals("")) {
                  long numero = Long.parseLong(numeroNF);
                    List<NotaFiscalFormat> listFormatada;
                    listFormatada = formatacaoNfiscal.formatNF(numero);
                    if (listFormatada.get(0).getNumero() != 0) {
                        String json = gson.toJson(listFormatada);
                        response.getWriter().write(json);
                    } else {
                        throw new RuntimeException("Nota Fiscal não localizado");
                    }
                } else {
                    throw new RuntimeException("O numero da nota não pode ser vazio");
                }
            }else if (sessao.getAttribute("usuario")!=null){
                List<Produto> listaProd = new ArrayList<>();
                Cliente usuario = (Cliente) sessao.getAttribute("usuario");
                List<NotaFiscal> lista= new ArrayList<>();
                lista.addAll(nFiscaisDao.listaPorIdCliente(usuario.getId()));
                List<NotaFiscalFormat> listaFormat;
                if(lista.size()>0) {
                    long numNF = 0;
                    long numNFEncontrado = 0;
                    if (numeroNF != null) {
                        numNF = Long.parseLong(numeroNF);
                        for (int i = 0; i < lista.size(); i++) {
                            if (numNF == lista.get(i).getNumero()) {
                                numNFEncontrado=lista.get(i).getNumero();
                                i=lista.size()-1;
                            }
                        }
                        if(numNFEncontrado!=0) {
                            listaFormat = formatacaoNfiscal.formatNF(numNFEncontrado);
                            String json = gson.toJson(listaFormat);
                            response.getWriter().write(json);
                        }else {
                            throw new RuntimeException("Nota Fiscal não encontrada");
                        }
                    } else {
                        throw new RuntimeException("Digite o numero da NF");
                    }
                } else {
                    throw new RuntimeException("Nenhuma compra para esse usuário não foi localizada");
                }
            }
        } catch (IOException e) {
                throw new RuntimeException(e);
        } catch (NumberFormatException ne) {
                try {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(msgService.msgDuploJson(ne.getMessage(), "Não é um parâmetro válido"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        } catch (Exception e) {
                try {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(msgService.msgDuploJson(e.getMessage(), "Erro ao encontrar Nota Fiscal"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
        }
    }

    @Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("aplication/json");
        HttpSession sessao = request.getSession();
        try {
            if(sessao.getAttribute("usuario")!=null) {
                Cliente usuario = (Cliente) sessao.getAttribute("usuario");
                String jsonStr = IOUtils.toString(request.getReader());
                NotaFiscal notaFiscal = gson.fromJson(jsonStr, NotaFiscal.class);
                if (nFiscaisDao.existe(notaFiscal.getNumero())==true) {
                    response.getWriter().write(msgService.msgJson("Já existe Nota Fiscal com esse numero"));
                } else {
                    for (Produto produto : notaFiscal.getListaProdutos()) {
                        NotaFiscal nf = new NotaFiscal(notaFiscal.getNumero(), produto.getId(), usuario.getId(), produto.getPreco());
                        nFiscaisDao.salvarNF(nf);
                    }
                    response.getWriter().write(msgService.msgJson("Nota Fiscal lançada com sucesso"));
                }
            }else{
                response.getWriter().write(msgService.msgJson("Faça logout como Administrador e loga como usuário"));
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