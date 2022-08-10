package com.adobe.aem.guides.wknd.core.service;

import com.adobe.aem.guides.wknd.core.interfaces.MsgErroService;
import com.google.gson.Gson;

public class MsgErroServiceImpl implements MsgErroService {
    Gson gson = new Gson();

    @Override
    public String msgJson(String msn) {
        String json = gson.toJson(msn);
        return json;
    }
}
