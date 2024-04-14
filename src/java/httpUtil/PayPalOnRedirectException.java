/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpUtil;

/**
 *
 * Currently, not implemented.
 * Will be thrown by RedirectView on HttpServletResponse#sendRedirect
 * Requires error-page <exception-type> in web.xml, and a Spring handler
 */
public class PayPalOnRedirectException extends HttpConnectException {
    
    public PayPalOnRedirectException(Throwable cause, String message, String friendly, String method) {
        super(cause, message,Boolean.TRUE, friendly, method);
    }
    
}
