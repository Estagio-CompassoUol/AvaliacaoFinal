package com.adobe.aem.guides.wknd.core.service;


public class OperacaoService {

    public String getOpera(String numum, String numdois, String opera){
        if(opera.equalsIgnoreCase("soma")){
           return String.valueOf(Integer.parseInt(numum) + Integer.parseInt(numdois));
        } else if(opera.equalsIgnoreCase("sub")){
            return String.valueOf(Integer.parseInt(numum) - Integer.parseInt(numdois));
        } else if(opera.equalsIgnoreCase("mult")){
            return String.valueOf(Integer.parseInt(numum) * Integer.parseInt(numdois));
        }else if(opera.equalsIgnoreCase("div")){
            return String.valueOf(Integer.parseInt(numum) / Integer.parseInt(numdois));
        }
        return "Não realizado";
    }
}
