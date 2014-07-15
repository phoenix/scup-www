package edu.scup.web.util;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class OmsCurrentUser {

    public static String getUserId() {
        return getLoginname();
    }

    public static String getLoginname() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        Assertion assertion = (Assertion) attributes.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, RequestAttributes.SCOPE_SESSION);
        return assertion == null ? null : assertion.getPrincipal().getName();
    }
}
