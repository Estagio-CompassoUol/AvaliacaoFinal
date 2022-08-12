package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.ProdutoDao;
import com.adobe.aem.guides.wknd.core.interfaces.ProdutoService;
import com.adobe.aem.guides.wknd.core.models.Produto;
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

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(AemContextExtension.class)
public class ProdutoServletTeste {
    @Mock
    private ProdutoDao produtoDaoMock;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    private ProdutoServlet produtoServlet;

    @BeforeEach
    void setup(AemContext context){
        MockitoAnnotations.openMocks(this);
        request = context.request();
        response = context.response();
        produtoServlet= new ProdutoServlet(produtoDaoMock);
    }

    @Test
    void doGetRetornaProdutoSemParametro(){
//        request.addRequestParameter("id","13");
//
//        Produto produto= ;
        Mockito.when(produtoDaoMock.getProdutos()).thenReturn(getLista());

        try {
            produtoServlet.doGet(request,response);
        }catch (Exception e){
            Assertions.fail("Ocorreu um erro");
        }
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("application/json;charser=UTF-8",response.getContentType());
        Assertions.assertEquals("{\"message\": \"User not found\"}",response.getOutputAsString());
    }

    @Test
    void doPutRetornaAgumentoVazio(){
        try {
            produtoServlet.doPut(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("{\"message\": \"Missing JSON parameters\"}", response.getOutputAsString());
    }
    @Test
    void doDeleteShouldDeleteUserWhenRequestBodyIsValid() {
        request.addRequestParameter("id", "14");

        try {
            produtoServlet.doDelete(request, response);
        }
        catch (Exception e) {
            Assertions.fail();
        }

        Mockito.verify(produtoDaoMock, times(1)).deletar(1);

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals("application/json;charset=UTF-8", response.getContentType());
    }

    private static List<Produto> getLista(){
        List<Produto> produto=new ArrayList<>();
        produto.add(new Produto(13,"Batedeira","Eletrodoméstico", 359.99));
        produto.add(new Produto(19,"Furadeira","Ferramentas", 259.99));
        return produto;
    }
}

