package edu.scup.web.servlet.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionResolver extends SimpleMappingExceptionResolver {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception ex) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        try {
            LOG.error("request headers {},params {}", OBJECT_MAPPER.writeValueAsString(headers)
                    , OBJECT_MAPPER.writeValueAsString(request.getParameterMap()), ex);
        } catch (JsonProcessingException ignored) {
        }
        return super.doResolveException(request, response, handler, ex);
    }
}
