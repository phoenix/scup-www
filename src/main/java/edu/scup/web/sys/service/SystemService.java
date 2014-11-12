package edu.scup.web.sys.service;

import edu.scup.web.sys.dao.STypeDao;
import edu.scup.web.sys.dao.STypeGroupDao;
import edu.scup.web.sys.entity.SType;
import edu.scup.web.sys.entity.STypeGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SystemService {
    @Autowired
    private STypeGroupDao typeGroupDao;
    @Autowired
    private STypeDao typeDao;

    public void initAllTypeGroups() {
        List<STypeGroup> typeGroups = typeGroupDao.findAll();
        for (STypeGroup sTypeGroup : typeGroups) {
            STypeGroup.allTypeGroups.put(sTypeGroup.getTypeGroupCode().toLowerCase(), sTypeGroup);
            List<SType> types = typeDao.findBySTypeGroupId(sTypeGroup.getId());
            STypeGroup.allTypes.put(sTypeGroup.getTypeGroupCode().toLowerCase(), types);
        }
    }
}
