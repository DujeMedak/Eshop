package cmov.feup.eshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "eshop.db";
    private static final int SCHEMA_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, privatekey TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String username, String privateKey) {
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("privatekey", privateKey);
        getWritableDatabase().insert("user", username, cv);
    }

    public String getUsername() {
        Cursor c = getReadableDatabase().rawQuery("SELECT username FROM user", null);//.getString(0);
        c.moveToFirst();
        return c.getString(0);
    }

    public String getPrivateKey() {
        Cursor c = getReadableDatabase().rawQuery("SELECT privatekey FROM user", null);
        c.moveToFirst();
        return c.getString(0);
    }

}
