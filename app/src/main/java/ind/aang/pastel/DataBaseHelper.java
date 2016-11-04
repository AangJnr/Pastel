package ind.aang.pastel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AangJnr on 10/5/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "colorsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COLORS_TABLE_NAME = "color";
    public static final String COLOR_COLUMN_ID = "_id";
    public static final String COLOR_CODE = "color_code";

    public static final String COLORS_HISTORY_TABLE_NAME = "color_history";
    public static final String COLOR_HISTORY_COLUMN_ID = "_id";
    public static final String COLOR_HISTORY_CODE = "color_code_history";




    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + COLORS_TABLE_NAME + "(" +
                COLOR_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLOR_CODE + " TEXT " + ")");

        db.execSQL("CREATE TABLE " + COLORS_HISTORY_TABLE_NAME + "(" +
                COLOR_HISTORY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLOR_HISTORY_CODE + " TEXT " + ")");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COLORS_TABLE_NAME);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + COLORS_HISTORY_TABLE_NAME);
        onCreate(db);
    }






    public List<ColorItem> getAllColors() {
        List<ColorItem> colors = new ArrayList<ColorItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + COLORS_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                ColorItem color = new ColorItem(cursor.getString(1));

                // Adding contact to list
                colors.add(color);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return colors;
    }








    public boolean addColor(ColorItem color_code) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLOR_CODE, color_code.getColorCode());
        db.insert(COLORS_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }



    public Integer deleteColor(String color_code) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(COLORS_TABLE_NAME,
                COLOR_CODE + " = ? ",
                new String[]{color_code});}


    public Boolean colorExists(String color_code) {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db,COLORS_TABLE_NAME, COLOR_CODE + " = ? ",
                new String[]{color_code}) > 0;

    }

    public boolean addColorToHistory(ColorItem color_code) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLOR_HISTORY_CODE, color_code.getColorCode());
        db.insert(COLORS_HISTORY_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public Boolean colorExisted(String color_code) {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, COLORS_HISTORY_TABLE_NAME,
                COLOR_HISTORY_CODE + " = ? ",
                new String[]{color_code}) > 0;

    }

}
