package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.MsgService;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
@Component(immediate = true,service = MsgService.class)
public class MsgServiceImpl implements MsgService {
    Gson gson = new Gson();

    @Override
    public String msgJson(String msn) {
        String json = "{\"Mensagem de Retorno\":\""+msn+"\""+"}";
        return json;
    }

    @Override
    public String msgDuploJson(String msgAlerta, String msg) {
        String json = "{\"Erro\":\""+msg+"\",\"Mensagem Alerta\":\""+msgAlerta+"\""+"}";
        return json;
    }

}
