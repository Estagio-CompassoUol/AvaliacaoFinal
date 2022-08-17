package com.adobe.aem.guides.wknd.core.interfaces;

import com.adobe.xfa.Document;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import java.io.IOException;

public interface RelatorioService {
    void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException;

}
