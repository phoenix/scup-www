package edu.scup.web.shiro.cas;

import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.CleanUpTimerTask;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Timer;
import java.util.TimerTask;

public class ShiroCasFilter extends CasFilter {
    private static final Logger logger = LoggerFactory.getLogger(ShiroCasFilter.class);

    private String casServerUrlPrefix;
    private TicketValidator ticketValidator;
    private ProxyGrantingTicketStorage proxyGrantingTicketStorage = new ProxyGrantingTicketStorageImpl();

    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) {
        Subject subject = getSubject(servletRequest, servletResponse);
        return subject.isAuthenticated();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String ticket = CommonUtils.safeGetParameter(request, "ticket");
        if (CommonUtils.isNotBlank(ticket)) {
            return super.onAccessDenied(servletRequest, servletResponse);
        }

        WebUtils.saveRequest(request);
        final String serviceUrl = CasUtil.constructServiceUrl(request, response, true);

        final String modifiedServiceUrl;

        logger.debug("no ticket and no assertion found");
        modifiedServiceUrl = serviceUrl;

        logger.debug("Constructed service url: {}", modifiedServiceUrl);

        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(getLoginUrl(), "service", modifiedServiceUrl, false, false);

        logger.debug("redirecting to \"{}\"", urlToRedirectTo);

        response.sendRedirect(urlToRedirectTo);
        return false;
    }

    @PostConstruct
    public void init() {
        CommonUtils.assertNotNull(this.getLoginUrl(), "casServerLoginUrl cannot be null.");
        CommonUtils.assertNotNull(this.proxyGrantingTicketStorage, "proxyGrantingTicketStorage cannot be null.");
        CommonUtils.assertNotNull(this.ticketValidator, "ticketValidator cannot be null.");

        if (this.timer == null) {
            this.timer = new Timer(true);
        }

        if (this.timerTask == null) {
            this.timerTask = new CleanUpTimerTask(this.proxyGrantingTicketStorage);
        }
        this.timer.schedule(this.timerTask, 60000, 60000);
    }

    protected final TicketValidator getTicketValidator() {
        Cas20ServiceTicketValidator validator = new Cas20ServiceTicketValidator(casServerUrlPrefix);
        validator.setProxyGrantingTicketStorage(proxyGrantingTicketStorage);
        validator.setProxyRetriever(new Cas20ProxyRetriever(casServerUrlPrefix, null));
        validator.setRenew(false);
        validator.setHostnameVerifier(new AnyHostnameVerifier());

        return validator;
    }

    public void setCasServerUrlPrefix(String casServerUrlPrefix) {
        this.casServerUrlPrefix = casServerUrlPrefix;
        this.setLoginUrl(casServerUrlPrefix + (casServerUrlPrefix.endsWith("/") ? "" : "/") + "login");
        ticketValidator = getTicketValidator();
    }
}
