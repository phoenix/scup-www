package edu.scup.web.servlet.view.freemarker;

import freemarker.cache.URLTemplateLoader;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * &lt;#include "/*.ftl"/&gt;时,freemarker会把前面的/去掉然后再查找资源,因此在这里如果找不到的话,需要把/加回去再找一次
 */
public class ClassTemplateLoader extends URLTemplateLoader {

    @Override
    protected URL getURL(String name) {
        Class clz = ClassTemplateLoader.class;
        URL url = clz.getResource(name);
        if (url == null && !StringUtils.startsWith(name, "/")) {
            url = clz.getResource("/" + name);
        }
        return url;
    }
}
