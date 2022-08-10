package com.adobe.aem.guides.wknd.core.interfaces;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;


public interface NotaFiscalService {

     void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response);

     void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response);

     void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response);
}
