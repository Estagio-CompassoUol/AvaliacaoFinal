package com.adobe.aem.guides.wknd.core.models;

public class MsnErro {
    private String erro;
    private String msg;

    public MsnErro(String erro, String msg) {
        this.erro = erro;
        this.msg = msg;
    }

    public String getErro() {
        return erro;
    }

    public String getMsg() {
        return msg;
    }
}
