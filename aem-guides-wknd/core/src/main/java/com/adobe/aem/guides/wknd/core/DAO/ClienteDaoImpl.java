package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.adobe.aem.guides.wknd.core.service.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashSet;

import java.util.Set;

@Component(immediate = true,service = ClienteDao.class)
public class ClienteDaoImpl implements ClienteDao{
    @Reference
    private DatabaseService databaseService;

    Set<Cliente> lista = new LinkedHashSet<>();

    @Override
    public Set<Cliente> getClientes() {
        try(Connection connection= databaseService.getConnections()){
            String sql="SELECT ID,NOME FROM cliente";

            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()){
                int id = result.getInt("ID");
                String nome=result.getString("NOME");

                lista.add(new Cliente(id,nome));
            }
            return lista;
        } catch (Exception e) {
//            logger.debug("Erro na solicitação de busca : " + e.getMessage());
            e.getMessage();
            return null;
        }
    }

    @Override
    public void setSalvar(Cliente cliente) {

    }

    @Override
    public void deletar(int id) {

    }

    @Override
    public void update(int id, String preco) {

    }
}
