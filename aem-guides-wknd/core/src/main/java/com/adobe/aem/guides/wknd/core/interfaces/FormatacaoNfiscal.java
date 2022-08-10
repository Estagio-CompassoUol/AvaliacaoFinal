package com.adobe.aem.guides.wknd.core.interfaces;

import com.adobe.aem.guides.wknd.core.models.NotaFiscalFormat;

import java.util.List;

public interface FormatacaoNfiscal {
    List<NotaFiscalFormat> formatNF(long numero);
}
