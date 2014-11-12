package edu.scup.web.sys.dao;

import edu.scup.web.sys.entity.STypeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STypeGroupDao extends JpaRepository<STypeGroup, String> {

    public STypeGroup findByTypeGroupName(String typeGroupName);
}
