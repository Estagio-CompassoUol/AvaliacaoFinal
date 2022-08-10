package com.adobe.aem.guides.wknd.core.servlets;


import com.adobe.aem.guides.wknd.core.interfaces.NotaFiscalService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/lojasgbr/notafiscal",
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE,
        SLING_SERVLET_EXTENSIONS + "=" + "json"
})
public class NotaFiscalServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;

    @Reference
    NotaFiscalService notaFiscalService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
//       response.getWriter().write("Get Funcionando");
       notaFiscalService.doGet(request, response);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        notaFiscalService.doPost(request, response);
    }

    @Override
    protected void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

    }
}
