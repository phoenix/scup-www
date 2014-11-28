package edu.scup.web.sys.dao;

import edu.scup.data.jpa.repository.JpaRepositoryExt;
import edu.scup.web.sys.entity.SDict;

import java.util.List;

public interface SDictDao extends JpaRepositoryExt<SDict, String> {

    public List<SDict> findByDictGroupId(String dictGroupId);

}
