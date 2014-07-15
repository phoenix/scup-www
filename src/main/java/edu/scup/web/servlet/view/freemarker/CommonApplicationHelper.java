package edu.scup.web.servlet.view.freemarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonApplicationHelper {
    protected static final HashMap<String, TemplateMethodModelEx> helpers = new HashMap<>();
    protected static final String appRoot = new File(CommonApplicationHelper.class.getResource("/").getFile()).getParentFile().getParent();

    static {
        helpers.put("file_last_modified_at", new FileLastModifiedAt());
    }

    private static class FileLastModifiedAt implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() == 1) {
                File file = new File(appRoot + arguments.get(0));
                return String.valueOf(file.lastModified() / 1000);
            }
            return "";
        }
    }

    public static Map getAttributesMap() {
        return helpers;
    }
}
