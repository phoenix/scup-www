package edu.scup.web.sys.entity;

import edu.scup.data.jpa.repository.domain.UUIDPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "s_dict_group")
public class SDictGroup extends UUIDPersistable {
    private static final long serialVersionUID = -787237567076988584L;

    public static Map<String, SDictGroup> allDictGroups = new HashMap<>();
    public static Map<String, List<SDict>> allTypes = new HashMap<>();

    private String dictGroupName;
    private String dictGroupCode;

    public String getDictGroupName() {
        return this.dictGroupName;
    }

    public void setDictGroupName(String dictGroupName) {
        this.dictGroupName = dictGroupName;
    }

    public String getDictGroupCode() {
        return this.dictGroupCode;
    }

    public void setDictGroupCode(String dictGroupCode) {
        this.dictGroupCode = dictGroupCode;
    }
}