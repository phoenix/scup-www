package edu.scup.data.jpa.repository.domain;

import com.fasterxml.jackson.annotation.JsonView;
import edu.scup.web.servlet.view.json.BaseView;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UUIDPersistable implements Persistable<String> {

    private static final long serialVersionUID = 6478053662771705554L;
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "ID", nullable = false, length = 32)
    @JsonView(BaseView.Id.class)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(StringUtils.isNotBlank(id)){
            this.id = id;
        }
    }

    @Override
    public boolean isNew() {
        return null == getId();
    }
}
