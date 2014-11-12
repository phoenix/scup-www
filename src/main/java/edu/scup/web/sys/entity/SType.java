package edu.scup.web.sys.entity;

import edu.scup.data.jpa.repository.domain.UUIDPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用类型字典表
 */
@Entity
@Table(name = "s_type")
public class SType extends UUIDPersistable {
    private static final long serialVersionUID = -850998721100866317L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_group_id")
    private STypeGroup sTypeGroup;//类型分组
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_p_id")
    private SType sType;//父类型
    private String typeName;//类型名称
    private String typeCode;//类型编码
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sType")
    private List<SType> sTypes = new ArrayList<>();

    public STypeGroup getsTypeGroup() {
        return sTypeGroup;
    }

    public void setsTypeGroup(STypeGroup sTypeGroup) {
        this.sTypeGroup = sTypeGroup;
    }

    public SType getsType() {
        return sType;
    }

    public void setsType(SType sType) {
        this.sType = sType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public List<SType> getsTypes() {
        return sTypes;
    }

    public void setsTypes(List<SType> sTypes) {
        this.sTypes = sTypes;
    }
}