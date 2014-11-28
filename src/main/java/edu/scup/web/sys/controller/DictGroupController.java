package edu.scup.web.sys.controller;

import edu.scup.web.servlet.tags.easyui.json.DataGridResponse;
import edu.scup.web.servlet.tags.easyui.vo.ComboboxOption;
import edu.scup.web.sys.dao.SDictGroupDao;
import edu.scup.web.sys.entity.SDictGroup;
import edu.scup.web.sys.util.EasyUIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.persistence.SearchFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gm/dictGroup")
public class DictGroupController {
    @Autowired
    private SDictGroupDao dictGroupDao;

    @RequestMapping({"", "/"})
    public String index() {
        return "gm/dictGroup/index";
    }

    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public DataGridResponse list(HttpServletRequest request) {
        Page page = dictGroupDao.findPage(EasyUIUtils.getPage(request), SearchFilter.getFiltersFromServletRequest(request));
        return new DataGridResponse(page.getTotalElements(), page.getContent());
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public SDictGroup update(HttpServletRequest request, SDictGroup dictGroup) {
        SDictGroup old = dictGroupDao.findOne(dictGroup.getId());
        old.setDictGroupCode(dictGroup.getDictGroupCode());
        old.setDictGroupName(dictGroup.getDictGroupName());
        old = dictGroupDao.save(old);
        return old;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public SDictGroup add(HttpServletRequest request, SDictGroup dictGroup) {
        dictGroup = dictGroupDao.save(dictGroup);
        return dictGroup;
    }

    @RequestMapping(value = "destroy", method = RequestMethod.POST)
    @ResponseBody
    public Object destroy(HttpServletRequest request, SDictGroup dictGroup) {
        dictGroupDao.delete(dictGroup);
        Map<String, Boolean> rt = new HashMap<>();
        rt.put("success", true);
        return rt;
    }

    @RequestMapping(value = "dict", method = RequestMethod.POST)
    @ResponseBody
    public List<ComboboxOption> dict() {
        List<ComboboxOption> list = new ArrayList<>();
        for (SDictGroup sDictGroup : dictGroupDao.findAll()) {
            ComboboxOption option = new ComboboxOption();
            option.setValue(sDictGroup.getId());
            option.setText(sDictGroup.getDictGroupName());
            list.add(option);
        }

        return list;
    }
}
