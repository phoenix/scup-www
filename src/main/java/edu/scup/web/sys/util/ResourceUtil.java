package edu.scup.web.sys.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ResourceBundle;


/**
 * 项目参数工具类
 */
public class ResourceUtil {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("sysConfig");

    /**
     * 获取配置文件参数
     *
     * @param name
     * @return
     */
    public static String getConfigByName(String name) {
        return bundle.getString(name);
    }

    public static String getParameter(String field) {
        HttpServletRequest request = ContextHolderUtils.getRequest();
        return request.getParameter(field);
    }
}
