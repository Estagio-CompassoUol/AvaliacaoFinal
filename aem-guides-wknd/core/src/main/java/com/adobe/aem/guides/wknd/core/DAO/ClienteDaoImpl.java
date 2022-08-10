package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.interfaces.ClienteDao;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.adobe.aem.guides.wknd.core.interfaces.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

@Component(immediate = true,service = ClienteDao.class)
public class ClienteDaoImpl implements ClienteDao {
    @Reference
    private DatabaseService databaseService;

    List<Cliente> lista = new ArrayList<>();

    @Override
    public List<Cliente> getClientes() {
        try (Connection connection = databaseService.getConnections()) {
            String sql = "SELECT ID,NOME, EMAIL FROM clientes";

            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()) {
                int id = result.getInt("ID");
                String nome = result.getString("NOME");

                lista.add(new Cliente(id, nome));
            }
            return lista;
        } catch (Exception e) {
            new RuntimeException(e.getMessage() + " " + "erro ao listar todos clientes do banco de dados");
        }
        return null;
    }

    @Override
    public Cliente getClientesById(int id) {
        Cliente cliente = null;
        try (Connection connection = databaseService.getConnections()) {
            String sql = "SELECT ID,NOME,EMAIL, SENHA FROM clientes WHERE ID = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1, id);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()) {
                cliente = new Cliente(result.getInt("ID"), result.getString("NOME"), result.getString("EMAIL"), result.getString("SENHA"));
            }
            return cliente;
        } catch (Exception e) {
            new RuntimeException(e.getMessage() + " " + "erro ao localizar cliente no banco de dados");
        }
        return null;
    }

    @Override
    public void setSalvar(Cliente cliente) {
        try (Connection connection = databaseService.getConnections()) {
            String sql = "INSERT INTO clientes (NOME,EMAIL,SENHA) VALUES (?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, cliente.getNome());
            pstm.setString(2, cliente.getEmail());
            pstm.setString(3, cliente.getSenha());
            pstm.execute();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void deletar(int id) {
        try (Connection connection = databaseService.getConnections()) {
            String sql = "DELETE FROM clientes WHERE ID = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1, id);
            pstm.execute();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + "Não foi possível deletar produto");
        }
    }

    @Override
    public void update(Cliente cliente) {
        try (Connection connection = databaseService.getConnections()) {
            String sql = "UPDATE clientes SET NOME = ?, EMAIL = ?, SENHA = ? WHERE ID = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, cliente.getNome());
            pstm.setString(2, cliente.getEmail());
            pstm.setString(3, cliente.getSenha());
            pstm.execute();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + "Não foi possível atualizar produto");
        }
    }

    public boolean existe(Cliente cliente) {

        try (Connection connection = databaseService.getConnections()) {
            String sql = "SELECT ID,NOME,EMAIL FROM clientes WHERE NOME = ? AND EMAIL = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, cliente.getNome());
            pstm.setString(2, cliente.getEmail());
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            if (result.next()) {
//                client = new Cliente(result.getString("NOME"), result.getString("EMAIL"));
                return true;
            }else
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}