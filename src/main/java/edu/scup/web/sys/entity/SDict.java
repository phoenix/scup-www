package edu.scup.web.sys.entity;

import edu.scup.data.jpa.repository.domain.UUIDPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用类型字典表
 */
@Entity
@Table(name = "s_dict")
public class SDict extends UUIDPersistable {
    private static final long serialVersionUID = -850998721100866317L;

    private String dictGroupId;//字典分组
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dict_p_id")
    private SDict parent;//父字典
    private String dictName;//字典名称
    private String dictCode;//字典编码
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
    private List<SDict> sDicts = new ArrayList<>();

    public SDict getParent() {
        return parent;
    }

    public void setParent(SDict parent) {
        this.parent = parent;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public List<SDict> getsSDicts() {
        return sDicts;
    }

    public void setsSDicts(List<SDict> sDicts) {
        this.sDicts = sDicts;
    }

    public String getDictGroupId() {
        return dictGroupId;
    }

    public void setDictGroupId(String dictGroupId) {
        this.dictGroupId = dictGroupId;
    }
}