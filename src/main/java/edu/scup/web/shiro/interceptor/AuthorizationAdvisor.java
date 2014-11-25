package edu.scup.web.shiro.interceptor;

import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AuthorizationAdvisor extends AuthorizationAttributeSourceAdvisor {
    private static final long serialVersionUID = 4821796708160056810L;
    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
            new Class[]{
                    RequiresPermissions.class, RequiresRoles.class,
                    RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class
            };

    public boolean matches(Method method, Class targetClass) {
        boolean match = super.matches(method, targetClass);
        if (!match) {
            for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
                Annotation a = AnnotationUtils.findAnnotation(targetClass, annClass);
                if (a != null) {
                    return true;
                }
            }
        }
        return match;
    }
}
