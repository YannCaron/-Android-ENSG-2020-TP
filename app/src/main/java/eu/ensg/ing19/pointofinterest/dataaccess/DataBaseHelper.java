package eu.ensg.ing19.pointofinterest.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import eu.ensg.ing19.pointofinterest.Constants;

public class DataBaseHelper extends SQLiteOpenHelper implements Constants {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "points_of_interest.db";

    // Sql commands
    private String SQL_CREATE_USER = "CREATE TABLE user (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \n" +
            "firstname TEXT, \n" +
            "lastname TEXT, \n" +
            "email TEXT, \n" +
            "password TEXT \n"  +
            ")";

    private String SQL_CREATE_POINT_OF_INTEREST = "CREATE TABLE point_of_interest (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \n" +
            "title TEXT, \n" +
            "description TEXT, \n" +
            "lat LONG, \n" +
            "lng LONG, \n" +
            "user_id INTEGER, FOREIGN KEY (user_id) REFERENCES user(id)"  +
            ")";

    private String SQL_DELETE_USER = "DROP TABLE IF EXISTS user;";
    private String SQL_DELETE_POINT_OF_INTEREST = "DROP TABLE IF EXISTS point_of_interest;";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.i(LOG_TAG, "Database loaded successfully.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_POINT_OF_INTEREST);
        // Pour le début, ajoute des poi au premier utilisateur...
        db.execSQL("INSERT INTO point_of_interest(title, description, lat, lng, user_id) VALUES ('ENSG', 'La meilleure école d''ingénieurs en géomatique du monde', 48.8410201, 2.5872416, 1);");
        db.execSQL("INSERT INTO point_of_interest(title, description, lat, lng, user_id) VALUES ('Ecole des ponts', '', 48.8410536, 2.587911, 1);");
        db.execSQL("INSERT INTO point_of_interest(title, description, lat, lng, user_id) VALUES ('IFTAR', '', 48.8423652, 2.5874393, 1);");

        Log.i(LOG_TAG, "Database created successfully.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USER);
        db.execSQL(SQL_DELETE_POINT_OF_INTEREST);
        onCreate(db);

        Log.i(LOG_TAG, String.format("Database upgrated successfully from version: %d to: %d.", oldVersion, newVersion));

    }

}
