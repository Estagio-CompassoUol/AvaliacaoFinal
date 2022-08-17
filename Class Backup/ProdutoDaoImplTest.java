package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.interfaces.DatabaseService;
import com.adobe.aem.guides.wknd.core.interfaces.ProdutoDao;
import com.adobe.aem.guides.wknd.core.models.Produto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoDaoImplTest {

    @Mock
    private DatabaseService databaseService;

//    @Mock
//    private ProdutoDao produtoDaoMock;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
////        produtoDaoMock=new ProdutoDaoImpl(databaseService);
//    }
//
//    @Test
//    void getProdutos() {
//        Produto produto= getListaProdutos().get(0);
//    }
//
//    @Test
//    void getFiltroId() {
//        Produto produto= new Produto(13,"Batedeira","Eletrodoméstico", 359.99);
//
//
//        Produto produto1=produtoDaoMock.getFiltroId(13);
//        Assertions.assertEquals(produto,produto1);
//    }
//
//    private static List<Produto> getListaProdutos() {
//        List<Produto> listaProduto = new ArrayList<>();
//        listaProduto.add(new Produto(13, "Batedeira", "Eletrodoméstico", 349.99));
//        listaProduto.add(new Produto(15, "Manga Tommy /Kg", "Frutas", 4.99));
//
//        return listaProduto;
//    }
}