package edu.scup.web.sys.service;

import edu.scup.web.sys.dao.SDictDao;
import edu.scup.web.sys.dao.SDictGroupDao;
import edu.scup.web.sys.entity.SDict;
import edu.scup.web.sys.entity.SDictGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SystemService {
    @Autowired
    private SDictGroupDao typeGroupDao;
    @Autowired
    private SDictDao typeDao;

    public void initAllTypeGroups() {
        List<SDictGroup> typeGroups = typeGroupDao.findAll();
        Map<String, List<SDict>> allDicts = new HashMap<>();
        for (SDictGroup sTypeGroup : typeGroups) {
            SDictGroup.allDictGroups.put(sTypeGroup.getDictGroupCode().toLowerCase(), sTypeGroup);
            List<SDict> types = typeDao.findByDictGroupId(sTypeGroup.getId());
            allDicts.put(sTypeGroup.getDictGroupCode().toLowerCase(), types);
        }
        SDictGroup.setAllDicts(allDicts);
    }
}
