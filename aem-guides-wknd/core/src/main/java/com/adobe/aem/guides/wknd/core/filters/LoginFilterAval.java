/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.aem.guides.wknd.core.filters;

import com.adobe.aem.guides.wknd.core.interfaces.MsgService;
import com.adobe.aem.guides.wknd.core.models.Cliente;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.apache.sling.engine.EngineConstants.SLING_FILTER_PATTERN;

/**
 * Simple servlet filter component that logs incoming requests.
 */
@Component(service = Filter.class,
           property = {
                   EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                   SLING_FILTER_PATTERN + "=/bin/lojasgbr/.*"
           })
@ServiceDescription("filter login requests")
@ServiceRanking(-750)
@ServiceVendor("Adobe")
public class LoginFilterAval implements Filter {

    @Reference
    private MsgService msgService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain filterChain) throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        String path =slingRequest.getRequestURI();
        HttpSession hs = slingRequest.getSession();
        if (hs.getAttribute("usuario")==null & hs.getAttribute("usuarioAdm")==null) {
            if (!path.equals("/bin/lojasgbr/login")) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write(msgService.msgJson("Usuário não logado"));
                return;
            }
        }
            filterChain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}