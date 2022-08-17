package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.interfaces.ProdutoDao;
import com.adobe.aem.guides.wknd.core.interfaces.ProdutoService;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.adobe.aem.guides.wknd.core.service.ProdutoServiceImpl;
import com.adobe.aem.guides.wknd.core.servlets.ProdutoServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.service.metatype.annotations.Option;

import javax.json.Json;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class ProdutoServletTeste {
    @Mock
    private ProdutoDao produtoDaoMock;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    @Mock
    private ProdutoService produtoService;

    private ProdutoServlet produtoServlet;

    @BeforeEach
    void setup(AemContext context){
        MockitoAnnotations.openMocks(this);
        produtoServlet= new ProdutoServlet(produtoService);
        produtoService=new ProdutoServiceImpl(produtoDaoMock);

        request = context.request();
        response = context.response();
    }

    @Test
    void doGetRetornaProdutoComParametro(){
        request.addRequestParameter("id","13");

//        String json = "{\n\"id\":13,\n \"nome\":\"Batedeira\",\n \"categoria\":\"Eletrodoméstico\",\n \"preco\":359.99\n}";


        Produto produto= new Produto(13,"Batedeira","Eletrodoméstico", 359.99);
        String json = String.valueOf(produto);
        Mockito.when(produtoDaoMock.getFiltroId(13)).thenReturn(produto);

        try {
            produtoService.doGet(request,response);
        }catch (Exception e){
            Assertions.fail("Ocorreu um erro");
        }
//        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
//        Assertions.assertEquals("aplication/json;charset=UTF-8", response.getContentType());
//        Assertions.assertEquals(json, response.getOutputAsString());
    }

    @Test
    void doGetRetornaProdutoSemParametro(){
        Mockito.when(produtoDaoMock.getProdutos()).thenReturn(getListaProdutos());
        String json= String.valueOf(getListaProdutos());

        try {
            produtoService.doGet(request,response);
        }catch (Exception e){
            Assertions.fail("Ocorreu um erro");
        }

//        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
//        Assertions.assertEquals("aplication/json;charset=UTF-8", response.getContentType());
//        Assertions.assertEquals(json, response.getOutputAsString());
    }

    private static List<Produto> getListaProdutos() {
        List<Produto> listaProduto = new ArrayList<>();
        listaProduto.add(new Produto(13, "Batedeira", "Eletrodoméstico", 349.99));
        listaProduto.add(new Produto(15, "Manga Tommy /Kg", "Frutas", 4.99));

        return listaProduto;
    }

}

