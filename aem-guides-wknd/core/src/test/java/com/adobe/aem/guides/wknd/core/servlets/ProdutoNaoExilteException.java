package com.adobe.aem.guides.wknd.core.servlets;

public class ProdutoNaoExilteException extends Exception {
    public ProdutoNaoExilteException(String menssagem) {
        super(menssagem);
    }
}
