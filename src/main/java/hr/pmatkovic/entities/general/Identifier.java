package hr.pmatkovic.entities.general;

import java.io.Serial;
import java.io.Serializable;


/**
 * Base class used to assign an identifier to entity
 */
public abstract class Identifier implements Serializable {

    @Serial
    private static final long serialVersionUID = -8066786309647576192L;

    private Long id;

    public Identifier(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
