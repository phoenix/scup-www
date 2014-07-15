package edu.scup.web.shiro.cas;

import org.jasig.cas.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CasUtil {
    private static final Logger logger = LoggerFactory.getLogger(CasUtil.class);

    public static String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response, final boolean encode) {
        final StringBuilder buffer = new StringBuilder();

        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        String serverName = url.substring(0, url.indexOf(uri));
        buffer.append(serverName);
        buffer.append(uri);

        if (CommonUtils.isNotBlank(request.getQueryString())) {
            final int location = request.getQueryString().indexOf("ticket=");

            if (location == 0) {
                final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
                logger.debug("serviceUrl generated: {}", returnValue);
                return returnValue;
            }

            buffer.append("?");

            if (location == -1) {
                buffer.append(request.getQueryString());
            } else if (location > 0) {
                final int actualLocation = request.getQueryString()
                        .indexOf("&ticket=");

                if (actualLocation == -1) {
                    buffer.append(request.getQueryString());
                } else if (actualLocation > 0) {
                    buffer.append(request.getQueryString().substring(0,
                            actualLocation));
                }
            }
        }

        final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
        logger.debug("serviceUrl generated: {}", returnValue);
        return returnValue;
    }
}
