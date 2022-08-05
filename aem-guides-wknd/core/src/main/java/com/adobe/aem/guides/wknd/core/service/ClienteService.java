package com.adobe.aem.guides.wknd.core.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface ClienteService {
    void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp);

    void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp);

    void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp);

    void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp);
}
