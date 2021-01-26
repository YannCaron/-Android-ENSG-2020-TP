package eu.ensg.ing19.pointofinterest.dataaccess;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import eu.ensg.ing19.pointofinterest.dataobject.DataBaseObject;

public abstract class DAO<T extends DataBaseObject> {

    protected SQLiteDatabase db;

    public DAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Save given obj in database
     * @param obj
     * @return
     */
    public abstract T create(T obj);

    /**
     * Update given obj in database
     * @param obj
     * @return
     */
    public abstract boolean update(T obj);

    /**
     * Delete given obj of database
     * @param obj
     * @return
     */
    public abstract boolean delete(T obj);
}
