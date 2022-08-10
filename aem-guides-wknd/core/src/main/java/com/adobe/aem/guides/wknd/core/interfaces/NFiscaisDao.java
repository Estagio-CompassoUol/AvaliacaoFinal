package com.adobe.aem.guides.wknd.core.interfaces;

import com.adobe.aem.guides.wknd.core.models.NotaFiscal;
import com.adobe.aem.guides.wknd.core.models.Produto;

import java.util.List;

public interface NFiscaisDao {
    void salvarNF(NotaFiscal notaFiscal);
    void deletarNF(long numero);
    List<NotaFiscal> listaNF(long numero);
}
