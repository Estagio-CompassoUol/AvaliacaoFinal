package com.adobe.aem.guides.wknd.core.interfaces;

import com.adobe.aem.guides.wknd.core.models.Cliente;

import java.util.List;

public interface ClienteDao {
    List<Cliente> getClientes();
    Cliente getClientesById(int id);

    void setSalvar(Cliente cliente);

    void deletar(int id);

    void update(Cliente cliente);

    Cliente login(String email, String senha);

    public boolean existe(Cliente cliente);

    public boolean admin(Cliente cliente);
}
