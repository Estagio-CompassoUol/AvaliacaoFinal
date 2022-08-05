package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.models.Cliente;

import java.util.Set;

public interface ClienteDao {
    Set<Cliente> getClientes();

    void setSalvar(Cliente cliente);

    void deletar(int id);

    void update(int id, String preco);
}
