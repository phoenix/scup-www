package edu.scup.web.sys.dao;

import edu.scup.web.sys.entity.SType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface STypeDao extends JpaRepository<SType, String> {

    public List<SType> findBySTypeGroupId(String typeGroupId);

}
