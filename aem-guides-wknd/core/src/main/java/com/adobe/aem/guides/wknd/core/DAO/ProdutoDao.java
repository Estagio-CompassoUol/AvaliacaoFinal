package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.models.Produto;

import java.util.List;

public interface ProdutoDao {
    List<Produto> getProdutos();

    void setSalvar(Produto produtos);

    void deletar(int id);

    void update(Produto produto);

    Produto getFiltroId(int idGet);

    List<Produto> getFiltroPreco();

    List<Produto> getFiltroCategoria(String categoria);

    List<Produto> getFiltroWordKey();
}