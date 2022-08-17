package com.adobe.aem.guides.wknd.core.servlets;


import com.adobe.aem.guides.wknd.core.interfaces.ProdutoService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/lojasgbr/produtos",
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
        SLING_SERVLET_EXTENSIONS + "=" + "json"
})
public class ProdutoServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;

    @Reference
    private ProdutoService produtoService;
    @Activate
    public ProdutoServlet(@Reference ProdutoService produtoService) {
        this.produtoService=produtoService;
    }

    @Override
    public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)  {
        produtoService.doGet(request,response);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        produtoService.doPost(request,response);
    }

    @Override
    public void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        produtoService.doDelete(request, response);
    }

    @Override
    public void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response){
        produtoService.doPut(request, response);
    }
}
