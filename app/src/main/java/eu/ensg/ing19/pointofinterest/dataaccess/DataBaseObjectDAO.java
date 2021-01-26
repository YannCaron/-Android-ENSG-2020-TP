package eu.ensg.ing19.pointofinterest.dataaccess;

import android.database.sqlite.SQLiteDatabase;

import eu.ensg.ing19.pointofinterest.dataobject.DataBaseObject;

public class DataBaseObjectDAO extends DAO<DataBaseObject> {

    public DataBaseObjectDAO(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public DataBaseObject create(DataBaseObject obj) {
        return null;
    }

    @Override
    public boolean update(DataBaseObject obj) {
        return false;
    }

    @Override
    public boolean delete(DataBaseObject obj) {
        return false;
    }
}
