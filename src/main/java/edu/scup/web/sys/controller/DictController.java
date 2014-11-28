package edu.scup.web.sys.controller;

import edu.scup.web.servlet.tags.easyui.json.DataGridResponse;
import edu.scup.web.sys.dao.SDictDao;
import edu.scup.web.sys.entity.SDict;
import edu.scup.web.sys.service.SystemService;
import edu.scup.web.sys.util.EasyUIUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.persistence.SearchFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gm/dict")
public class DictController {
    @Autowired
    private SDictDao dictDao;
    @Autowired
    private SystemService systemService;

    @RequestMapping({"", "/"})
    public String index() {
        return "gm/dict/index";
    }

    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public DataGridResponse list(HttpServletRequest request) {
        Page page = dictDao.findPage(EasyUIUtils.getPage(request), SearchFilter.getFiltersFromServletRequest(request));
        return new DataGridResponse(page.getTotalElements(), page.getContent());
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public SDict update(HttpServletRequest request, SDict dict) {
        SDict old = dictDao.findOne(dict.getId());
        old.setDictCode(dict.getDictCode());
        old.setDictName(dict.getDictName());
        if (!StringUtils.equals(old.getDictGroupId(), dict.getDictGroupId())) {
            old.setDictGroupId(dict.getDictGroupId());
        }
        old = dictDao.save(old);
        systemService.initAllTypeGroups();
        return old;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public SDict add(HttpServletRequest request, SDict dict) {
        dict = dictDao.save(dict);
        systemService.initAllTypeGroups();
        return dict;
    }

    @RequestMapping(value = "destroy", method = RequestMethod.POST)
    @ResponseBody
    public Object destroy(HttpServletRequest request, SDict dict) {
        dictDao.delete(dict);
        systemService.initAllTypeGroups();
        Map<String, Boolean> rt = new HashMap<>();
        rt.put("success", true);
        return rt;
    }
}
