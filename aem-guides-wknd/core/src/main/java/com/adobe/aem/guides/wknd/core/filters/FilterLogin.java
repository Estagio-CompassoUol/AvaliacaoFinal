package com.adobe.aem.guides.wknd.core.filters;



public class FilterLogin {

//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
//
//        System.out.println("AutorizacaoFilter");
//
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        String paramAcao = request.getParameter("direct");
//
//        System.out.println(paramAcao);
//        HttpSession sessao = request.getSession();
//        boolean usuarioNaoEstaLogado = (sessao.getAttribute("usuarioLogado") == null);
//        boolean ehUmaAcaoProtegida = !(paramAcao.equals("Login") || paramAcao.equals("LoginForm") || paramAcao.equals("NovoCliente") || paramAcao.equals("SalvarCliente") || paramAcao.equals("ListaProdutos"));
//
//        if(ehUmaAcaoProtegida && usuarioNaoEstaLogado) {
//            response.sendRedirect("do?direct=LoginForm");
//            return;
//        }
//
//        chain.doFilter(request, response);
}
