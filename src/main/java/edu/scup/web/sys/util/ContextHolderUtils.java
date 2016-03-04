package edu.scup.web.sys.util;

import edu.scup.web.sys.entity.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class ContextHolderUtils {
    /**
     * SpringMvc下获取request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * SpringMvc下获取session
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;
    }

    public static SysUser getCurrentUser() {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            return (SysUser) currentUser.getPrincipal();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
