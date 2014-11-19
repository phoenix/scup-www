package edu.scup.web.sys.entity;

import java.io.Serializable;
import java.security.Principal;

public interface SysUser<PK extends Serializable> extends Principal {

    public PK getId();
}
