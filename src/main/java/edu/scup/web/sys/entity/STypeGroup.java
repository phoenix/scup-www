package edu.scup.web.sys.entity;

import edu.scup.data.jpa.repository.domain.UUIDPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "s_type_group")
public class STypeGroup extends UUIDPersistable {
    private static final long serialVersionUID = -787237567076988584L;

    public static Map<String, STypeGroup> allTypeGroups = new HashMap<>();
    public static Map<String, List<SType>> allTypes = new HashMap<>();

    private String typeGroupName;
    private String typeGroupCode;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sTypeGroup")
    private List<SType> sTypes = new ArrayList<>();

    public String getTypeGroupName() {
        return this.typeGroupName;
    }

    public void setTypeGroupName(String typeGroupName) {
        this.typeGroupName = typeGroupName;
    }

    public String getTypeGroupCode() {
        return this.typeGroupCode;
    }

    public void setTypeGroupCode(String typeGroupCode) {
        this.typeGroupCode = typeGroupCode;
    }

    public List<SType> getSTypes() {
        return this.sTypes;
    }

    public void setSTypes(List<SType> TSTypes) {
        this.sTypes = TSTypes;
    }

}