package edu.scup.web.sys;

import edu.scup.web.sys.util.ResourceUtil;

public class Globals {
    /**
     * 配置系统是否开启按钮权限控制
     */
    public static boolean BUTTON_AUTHORITY_CHECK = false;

    static {
        String button_authority_jeecg = ResourceUtil.getConfigByName("button.authority.jeecg");
        if ("true".equals(button_authority_jeecg)) {
            BUTTON_AUTHORITY_CHECK = true;
        }
    }
}
