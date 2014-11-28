package edu.scup.web.sys.dao;

import edu.scup.data.jpa.repository.JpaRepositoryExt;
import edu.scup.web.sys.entity.SDictGroup;

public interface SDictGroupDao extends JpaRepositoryExt<SDictGroup, String> {

    public SDictGroup findByDictGroupName(String dictGroupName);
}
