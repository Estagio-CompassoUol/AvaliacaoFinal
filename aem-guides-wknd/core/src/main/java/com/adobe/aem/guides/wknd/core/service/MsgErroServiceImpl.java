package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.FormatacaoNfiscal;
import com.adobe.aem.guides.wknd.core.interfaces.MsgErroService;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
@Component(immediate = true,service = MsgErroService.class)
public class MsgErroServiceImpl implements MsgErroService {
    Gson gson = new Gson();

    @Override
    public String msgJson(String msn) {
        String json = "{\"Mensagem de Retorno\":\""+msn+"\""+"}";
        return json;
    }

    @Override
    public String msgDuploJson(String msgAlerta, String msg) {
        String json = "{\"Produtos\":\""+msg+"\",\"Mensagem Alerta\":\""+msgAlerta+"\""+"}";
        return json;
    }

    @Override
    public String msgErroJson(String msnErro, String erro) {
        String json = "{\"Log de Erro\":\""+erro+"\",\"Mensagem erro\":\""+msnErro+"\""+"}";
        return json;
    }

}
