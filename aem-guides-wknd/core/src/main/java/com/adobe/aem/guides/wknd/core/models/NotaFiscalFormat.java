package com.adobe.aem.guides.wknd.core.models;

import com.google.gson.annotations.Expose;
import org.apache.sling.models.annotations.Model;
import org.osgi.resource.Resource;

import java.util.List;

@Model(adaptables = Resource.class)
public class NotaFiscalFormat {
    @Expose
    private long numero;
    @Expose
    private int idCliente;
    @Expose
    private double valor;

    private int qtn;

    private List<ProdutoNFFormat> listaProdutosFormat;

    public NotaFiscalFormat(long numero, int idCliente, double valor, int qtn, List<ProdutoNFFormat> listaProdutosFormat) {
        this.numero = numero;
        this.idCliente = idCliente;
        this.valor = valor;
        this.qtn = qtn;
        this.listaProdutosFormat = listaProdutosFormat;
    }


    public long getNumero() {
        return numero;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public double getValor() {
        return valor;
    }

    public int getQtn() {
        return qtn;
    }

    public List<ProdutoNFFormat> getListaProdutosFormat() {
        return listaProdutosFormat;
    }
}
