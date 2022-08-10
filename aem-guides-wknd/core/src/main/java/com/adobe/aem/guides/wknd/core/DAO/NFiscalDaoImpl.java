package com.adobe.aem.guides.wknd.core.DAO;

import com.adobe.aem.guides.wknd.core.interfaces.NFiscaisDao;
import com.adobe.aem.guides.wknd.core.interfaces.ProdutoDao;
import com.adobe.aem.guides.wknd.core.models.NotaFiscal;
import com.adobe.aem.guides.wknd.core.models.Produto;
import com.adobe.aem.guides.wknd.core.interfaces.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true,service =NFiscaisDao.class)
public class NFiscalDaoImpl implements NFiscaisDao {
    @Reference
    private DatabaseService databaseService;

    @Reference
    private ProdutoDao produtoDao;

    @Override
    public void salvarNF(NotaFiscal notaFiscal) {
        try(Connection connection= databaseService.getConnections()){
            String sql="INSERT INTO nfiscais (NUMERO,IDPRODUTO,IDCLIENTE,VALOR) VALUES (?,?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setLong(1, notaFiscal.getNumero());
            pstm.setInt(2, notaFiscal.getListaProdutos().get(0).getId());
            pstm.setInt(3,notaFiscal.getIdCliente());
            pstm.setDouble(4,notaFiscal.getListaProdutos().get(0).getPreco());
            pstm.execute();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()+ " Erro ao salvar Nota Fiscal");
        }
    }

    @Override
    public void deletarNF(long numero) {

    }

    @Override
    public List<NotaFiscal> listaNF(long numero) {
        List<NotaFiscal> listaNotas = new ArrayList<>();
        List<Produto> listProd = new ArrayList<>();
        int idCliente=0;
        long numeroR=0;
        double valor=0;
        try(Connection connection= databaseService.getConnections()){
            String sql="SELECT NUMERO,IDPRODUTO,IDCLIENTE,VALOR FROM nfiscais WHERE NUMERO = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setLong(1,numero);
            pstm.execute();
            ResultSet result = pstm.getResultSet();
            while (result.next()){
                numeroR = result.getLong(1);
                int idProd=result.getInt(2);
                idCliente=result.getInt(3);
                valor=valor+result.getDouble(4);
                Produto produto=produtoDao.getFiltroId(idProd);
                listProd.add(new Produto(produto.getId(), produto.getNome(), produto.getPreco()));
            }
            listaNotas.add(new NotaFiscal(numeroR,idCliente,listProd));
            return listaNotas;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()+ " Erro ao listar Nota Fiscal");
        }
    }

}
