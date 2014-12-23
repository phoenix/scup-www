package edu.scup.web.sys.entity;

import edu.scup.data.jpa.repository.domain.UUIDPersistable;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "s_dict_group")
public class SDictGroup extends UUIDPersistable {
    private static final long serialVersionUID = -787237567076988584L;

    public static Map<String, SDictGroup> allDictGroups = new HashMap<>();
    private static Map<String, List<SDict>> allDicts = new HashMap<>();

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

    public static Map<String, List<SDict>> getAllDicts() {
        return allDicts;
    }

    public static void setAllDicts(Map<String, List<SDict>> dicts) {
        allDicts = dicts;
    }

    public static String getDictCode(String dictGroupCode, String name) {
        for (SDict dict : allDicts.get(dictGroupCode)) {
            if (StringUtils.equals(name, dict.getDictName())) {
                return dict.getDictCode();
            }
        }
        return name;
    }

    public static String getDictValue(String dictGroupCode, String dictCode) {
        for (SDict dict : allDicts.get(dictGroupCode)) {
            if (StringUtils.equals(dictCode, dict.getDictCode())) {
                return dict.getDictName();
            }
        }
        return dictCode;
    }
}