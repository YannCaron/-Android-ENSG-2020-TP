package eu.ensg.ing19.pointofinterest.dataobject;

import java.io.Serializable;

public abstract class DataBaseObject implements Serializable {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DataBaseObject() {
    }

    public DataBaseObject(long id) {
        this.id = id;
    }
}
