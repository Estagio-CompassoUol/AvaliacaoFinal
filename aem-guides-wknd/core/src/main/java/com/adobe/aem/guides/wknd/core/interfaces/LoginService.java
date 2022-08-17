package com.adobe.aem.guides.wknd.core.interfaces;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public interface LoginService {
    void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp);
    void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response);
}
