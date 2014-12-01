package edu.scup.web.sys.service;

import edu.scup.web.sys.dao.CommonDao;
import edu.scup.web.sys.dao.SDictDao;
import edu.scup.web.sys.dao.SDictGroupDao;
import edu.scup.web.sys.entity.SDict;
import edu.scup.web.sys.entity.SDictGroup;
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
    private SDictGroupDao dictGroupDao;
    @Autowired
    private SDictDao dictDao;

    @Transactional
    public void deleteAndRepair() {
        // 由于表中有主外键关系，清空数据库需注意
        LOG.info("重新初始化系统数据");
        commonDao.executeJpql("delete SDict");
        commonDao.executeJpql("delete SDictGroup");

        repair();
        LOG.info("系统数据初始化成功");
    }

    private synchronized void repair() {
        repairDictAndGroup();// 修复字典类型
        repairDict();// 修复字典值
    }

    /**
     * 修复类型分组表
     */
    private void repairDictAndGroup() {
        SDictGroup sex = new SDictGroup();
        sex.setDictGroupName("性别类");
        sex.setDictGroupCode("sex");
        dictGroupDao.save(sex);

        SDictGroup bool = new SDictGroup();
        bool.setDictGroupName("布尔值");
        bool.setDictGroupCode("bool");
        dictGroupDao.save(bool);
    }

    /**
     * 修复类型表
     */
    private void repairDict() {

        SDictGroup sex = dictGroupDao.findByDictGroupName("性别类");
        SDict man = new SDict();
        man.setDictName("男性");
        man.setDictCode("1");
        man.setDictGroupId(sex.getId());
        dictDao.save(man);

        SDict woman = new SDict();
        woman.setDictName("女性");
        woman.setDictCode("0");
        woman.setDictGroupId(sex.getId());
        dictDao.save(woman);

        SDictGroup bool = dictGroupDao.findByDictGroupName("布尔值");
        SDict yes = new SDict();
        yes.setDictName("是");
        yes.setDictCode("1");
        yes.setDictGroupId(bool.getId());
        dictDao.save(yes);

        SDict no = new SDict();
        no.setDictName("否");
        no.setDictCode("0");
        no.setDictGroupId(bool.getId());
        dictDao.save(no);
    }
}
