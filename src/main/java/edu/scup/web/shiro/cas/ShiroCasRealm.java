package edu.scup.web.shiro.cas;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;
import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class ShiroCasRealm extends CasRealm {
    private static final Logger logger = LoggerFactory.getLogger(ShiroCasRealm.class);
    final String roleAttributeNames = "rolename";
    private String encoding = "GBK";

    public ShiroCasRealm() {
        setRoleAttributeNames(roleAttributeNames);
    }

    @Override
    public String getCasService() {
        Subject subject = SecurityUtils.getSubject();
        WebSubject webSubject = (WebSubject) subject;
        String serviceUrl = CasUtil.constructServiceUrl((HttpServletRequest) webSubject.getServletRequest(),
                (HttpServletResponse) webSubject.getServletResponse(), true);
        return serviceUrl;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // retrieve user information
        SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) principals;
        List<Object> listPrincipals = principalCollection.asList();
        Map<String, String> attributes = (Map<String, String>) listPrincipals.get(1);

        attributes.put(roleAttributeNames, attributes.get(roleAttributeNames).replaceAll("\\[|\\]", ""));
        if (logger.isDebugEnabled()) {
            logger.debug("user {} has roles {}", listPrincipals.get(0), attributes.get(roleAttributeNames));
        }

        return super.doGetAuthorizationInfo(principals);
    }

    @Override
    protected TicketValidator createTicketValidator() {
        AbstractUrlBasedTicketValidator validator = (AbstractUrlBasedTicketValidator) super.createTicketValidator();
        validator.setEncoding(encoding);
        return validator;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
