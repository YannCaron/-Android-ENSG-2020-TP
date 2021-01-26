package eu.ensg.ing19.pointofinterest.dataaccess;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import eu.ensg.ing19.pointofinterest.dataobject.PointOfInterest;

public class PointOfInterestDAO extends DAO<PointOfInterest> {

    public PointOfInterestDAO(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public PointOfInterest create(PointOfInterest obj) {
        ContentValues values = new ContentValues();
        values.put("title", obj.getTitle());
        values.put("description", obj.getDescription());
        values.put("lat", obj.getLat());
        values.put("lng", obj.getLng());
        values.put("user_id", obj.getUserId());

        long id = db.insert("point_of_interest", null, values);
        obj.setId(id);

        return obj;
    }

    @Override
    public boolean update(PointOfInterest obj) {
        return false;
    }

    @Override
    public boolean delete(PointOfInterest obj) {
        return false;
    }
}
