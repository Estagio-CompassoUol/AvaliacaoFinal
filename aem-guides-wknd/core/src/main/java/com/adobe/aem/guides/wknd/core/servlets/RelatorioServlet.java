package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.interfaces.RelatorioService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/lojasgbr/relatorio",
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
})
public class RelatorioServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;

    @Reference
    private RelatorioService relatorioService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        relatorioService.doGet(request, response);
    }
}
