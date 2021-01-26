package eu.ensg.ing19.pointofinterest.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import eu.ensg.ing19.pointofinterest.dataobject.User;

public class UserDAO extends DAO<User> {

    public UserDAO(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public User create(User obj) {
        ContentValues values = new ContentValues();
        values.put("email", obj.getEmail());
        values.put("password", obj.getPassword());
        values.put("firstname", obj.getFirstName());
        values.put("lastname", obj.getLastName());

        long id = db.insert("user", null, values);
        obj.setId(id);

        return obj;
    }

    @Override
    public boolean update(User obj) {
        ContentValues values = new ContentValues();
        values.put("email", obj.getEmail());
        values.put("password", obj.getPassword());
        values.put("firstname", obj.getFirstName());
        values.put("lastname", obj.getLastName());

        // Which row to update, based on the title
        String selection = " id = ? "; // TODO: Is LIKE necessary ?
        String[] selectionArgs = { String.valueOf(obj.getId()) };

        int count = db.update("user", values, selection, selectionArgs);
        // UPDATE [user] set ( email.... ) VALUES ( "cyann74@gmail.com".... ) WHERE id = ?;

        return count == 1;

    }

    @Override
    public boolean delete(User obj) {
        // Define 'where' part of query.
        String selection = "id = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(obj.getId()) };
        // Issue SQL statement.
        int count = db.delete("user", selection, selectionArgs);

        return count == 1;
    }

    public List<User> findAll() {
        Cursor cursor = db.rawQuery("SELECT * FROM user;", null);

        List<User> users = new LinkedList<>();

        cursor.moveToFirst();
        do {
            Long id = cursor.getLong(cursor.getColumnIndex("id"));
            String first_name = cursor.getString(cursor.getColumnIndex("firstname"));
            String last_name = cursor.getString(cursor.getColumnIndex("lastname"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String password = cursor.getString(cursor.getColumnIndex("password"));

            users.add(new User(id, first_name, last_name, email, password));
        } while(cursor.moveToNext());

        return users;
    }

    public User findByEmailAndEncodedPassword(String email, String encodedPassword) {
        String[] columns = { "id", "email", "password", "firstname", "lastname" };

        String selection = "email = ? AND password = ?";
        String[] selectionArgs = { email, encodedPassword };

        Cursor cursor = db.query(
                "user",                     // The table to query
                columns,                    // The columns to return
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                null                        // don't sort order
        );

        if (cursor.moveToFirst()) {
            Long id = cursor.getLong(cursor.getColumnIndex("id"));
            String first_name = cursor.getString(cursor.getColumnIndex("firstname"));
            String last_name = cursor.getString(cursor.getColumnIndex("lastname"));
            return new User(id, email, encodedPassword, first_name, last_name);
        }

        return null;
    }

}
