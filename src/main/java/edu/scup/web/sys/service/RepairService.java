package edu.scup.web.sys.service;

import edu.scup.web.sys.dao.CommonDao;
import edu.scup.web.sys.dao.STypeDao;
import edu.scup.web.sys.dao.STypeGroupDao;
import edu.scup.web.sys.entity.SType;
import edu.scup.web.sys.entity.STypeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RepairService {
    private static final Logger LOG = LoggerFactory.getLogger(RepairService.class);

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private STypeGroupDao typeGroupDao;
    @Autowired
    private STypeDao typeDao;

    @Transactional
    public void deleteAndRepair() {
        // 由于表中有主外键关系，清空数据库需注意
        LOG.info("重新初始化系统数据");
        commonDao.executeJpql("delete SType");
        commonDao.executeJpql("delete STypeGroup");

        repair();
        LOG.info("系统数据初始化成功");
    }

    private synchronized void repair() {
        repairTypeAndGroup();// 修复字典类型
        repairType();// 修复字典值
    }

    /**
     * 修复类型分组表
     */
    private void repairTypeAndGroup() {
        STypeGroup sex = new STypeGroup();
        sex.setTypeGroupName("性别类");
        sex.setTypeGroupCode("sex");
        typeGroupDao.save(sex);

        STypeGroup bool = new STypeGroup();
        bool.setTypeGroupName("布尔值");
        bool.setTypeGroupCode("bool");
        typeGroupDao.save(bool);
    }

    /**
     * 修复类型表
     */
    private void repairType() {

        STypeGroup sex = typeGroupDao.findByTypeGroupName("性别类");
        SType man = new SType();
        man.setTypeName("男性");
        man.setTypeCode("1");
        man.setsTypeGroup(sex);
        typeDao.save(man);

        SType woman = new SType();
        woman.setTypeName("女性");
        woman.setTypeCode("0");
        woman.setsTypeGroup(sex);
        typeDao.save(woman);

        STypeGroup bool = typeGroupDao.findByTypeGroupName("布尔值");
        SType yes = new SType();
        yes.setTypeName("是");
        yes.setTypeCode("1");
        yes.setsTypeGroup(bool);
        typeDao.save(yes);

        SType no = new SType();
        no.setTypeName("否");
        no.setTypeCode("0");
        no.setsTypeGroup(bool);
        typeDao.save(no);
    }
}
