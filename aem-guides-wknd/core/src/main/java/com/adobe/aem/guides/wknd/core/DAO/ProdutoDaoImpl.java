package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.models.Produto;
import com.adobe.aem.guides.wknd.core.service.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true,service = ProdutoDao.class)
public class ProdutoDaoImpl implements ProdutoDao {
    @Reference
    private DatabaseService databaseService;

    List<Produto> listaProdutos = new ArrayList<>();
    @Override
    public List<Produto> getProdutos() {
        listaProdutos.clear();
        try(Connection connection= databaseService.getConnections()){
            String sql="SELECT ID,NOME,CATEGORIA,PRECO FROM produtos";

           PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.execute();
               ResultSet result = pstm.getResultSet();
                    while (result.next()){
                        int id = result.getInt("ID");
                        String nome=result.getString("NOME");
                        String categoria=result.getString("CATEGORIA");
                        double preco=result.getDouble("PRECO");


                        listaProdutos.add(new Produto(id,nome,categoria,preco));
                    }
            return listaProdutos;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void setSalvar(Produto produto) {
        try(Connection connection= databaseService.getConnections()){
            String sql="INSERT INTO produtos (NOME,CATEGORIA,PRECO) VALUES (?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, produto.getNome());
            pstm.setString(2, produto.getCategoria());
            pstm.setDouble(3,produto.getPreco());
            pstm.execute();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void deletar(int id){
        try(Connection connection= databaseService.getConnections()){
           String sql = "DELETE FROM produtos WHERE ID = ?";
           PreparedStatement pstm = connection.prepareStatement(sql);
           pstm.setInt(1,id);

           if (!pstm.execute()) throw new RuntimeException("Não foi possível deletar produto");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()+"Não foi possível deletar produto");
        }
    }

    @Override
    public void update(Produto produto) {
        try(Connection connection= databaseService.getConnections()) {
            String sql = "UPDATE produtos SET NOME = ?, CATEGORIA = ?, PRECO = ? WHERE ID = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, produto.getNome());
            pstm.setString(2, produto.getCategoria());
            pstm.setDouble(3,produto.getPreco());
            pstm.setInt(4, produto.getId());
            pstm.execute();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()+"Não foi possível atualizar produto");
        }
    }

    @Override
    public Produto getFiltroId(int idGet) {
        Produto produto = null;
        try(Connection connection= databaseService.getConnections()){
            String sql="SELECT ID, NOME,CATEGORIA,PRECO FROM produtos WHERE ID=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1,idGet);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()){
                int id = result.getInt("ID");
                String nome=result.getString("NOME");
                String categoria=result.getString("CATEGORIA");
                double preco=result.getDouble("PRECO");
                produto = new Produto(id,nome,categoria,preco);
            }
            return produto;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public List<Produto> getFiltroPreco() {
        List<Produto> produtos = new ArrayList<>();
        try(Connection connection= databaseService.getConnections()){
            String sql="SELECT ID,NOME,DESCRICAO,PRECO,QUANTIDADE FROM produtos ORDER BY PRECO";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()){
                int id = result.getInt("ID");
                String nome=result.getString("NOME");
                String categoria=result.getString("CATEGORIA");
                double preco=result.getDouble("PRECO");
                produtos.add(new Produto(id,nome,categoria,preco));
            }
            return produtos;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public List<Produto> getFiltroCategoria(String categoria) {
        List<Produto> produtosByCat = null;
        try(Connection connection= databaseService.getConnections()){
            String sql="SELECT ID,NOME,DESCRICAO,PRECO,QUANTIDADE FROM produtos WHERE CATEGORIA=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1,categoria);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()){
                int id = result.getInt("ID");
                String nome=result.getString("NOME");
                String categoriaR=result.getString("CATEGORIA");
                double preco=result.getDouble("PRECO");
                produtosByCat.add(new Produto(id,nome,categoriaR,preco));
            }
            return produtosByCat;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public List<Produto> getFiltroWordKey() {
        return null;
    }

}