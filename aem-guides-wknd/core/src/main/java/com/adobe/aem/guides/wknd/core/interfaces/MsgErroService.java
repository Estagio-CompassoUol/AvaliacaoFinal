package com.adobe.aem.guides.wknd.core.interfaces;

public interface MsgErroService {
       String msgJson(String msnErro);
       String msgDuploJson(String msgAlerta,String msg);
       String msgErroJson(String msnErro, String erro);
}
