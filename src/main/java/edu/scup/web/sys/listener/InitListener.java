package edu.scup.web.sys.listener;

import edu.scup.web.sys.service.SystemService;
import edu.scup.web.sys.util.ResourceUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;


/**
 * 系统初始化监听器,在系统启动时运行,进行一些初始化工作
 */
public class InitListener implements javax.servlet.ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {

    }

    public void contextInitialized(ServletContextEvent event) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        SystemService systemService = webApplicationContext.getBean(SystemService.class);

        /**
         * 第一部分：对数据字典进行缓存
         */
        systemService.initAllTypeGroups();

        /**
         * 第二部分：自动加载新增菜单和菜单操作权限
         * 说明：只会添加，不会删除（添加在代码层配置，但是在数据库层未配置的）
         */
        if ("true".equalsIgnoreCase(ResourceUtil.getConfigByName("auto.scan.menu.flag"))) {
            //MenuInitService menuInitService = (MenuInitService) webApplicationContext.getBean("menuInitService");
            //menuInitService.initMenu();
        }
    }
}
