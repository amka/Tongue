package me.meamka.tongue.Storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrey.maksimov on 24.04.17.
 */

public class BookmarkDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "bookmarks";
    private static final String KEY_ID = "id";
    public static final String COLUMN_NAME_ORIGIN = "origin";
    public static final String COLUMN_NAME_TRANSLATED = "translated";
    public static final String COLUMN_NAME_ORIGIN_LANG = "origin_lang";
    public static final String COLUMN_NAME_TARGET_LANG = "target_lang";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_ORIGIN + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_ORIGIN_LANG + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_TRANSLATED + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_TARGET_LANG + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tongue.db";

    public BookmarkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TONGUE", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void validateTable() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL(SQL_CREATE_ENTRIES);
        } catch (Exception e) {
            Log.d("TONGUE", "Table " + TABLE_NAME + " already exists");
        }
    }

    /**
     * Put new Bookmark entry to Bookmark storage
     */
    public void addEntry(BookmarkEntry entry) {
        validateTable();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                        + COLUMN_NAME_ORIGIN + "=? AND "
                        + COLUMN_NAME_ORIGIN_LANG + "=? AND "
                        + COLUMN_NAME_TARGET_LANG + "=?",
                new String[]{entry.getOrigin(), entry.getOriginLang(), entry.getTargetLang()}
        );
        if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ORIGIN, entry.getOrigin());
        values.put(COLUMN_NAME_TRANSLATED, entry.getTranslated());
        values.put(COLUMN_NAME_ORIGIN_LANG, entry.getOriginLang());
        values.put(COLUMN_NAME_TARGET_LANG, entry.getTargetLang());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<BookmarkEntry> getAllEntries(int limit) {
        validateTable();

        String[] projection = {
                KEY_ID,
                COLUMN_NAME_ORIGIN,
                COLUMN_NAME_TRANSLATED,
                COLUMN_NAME_ORIGIN_LANG,
                COLUMN_NAME_TARGET_LANG
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                KEY_ID + " DESC";

        String qLimit = " ";
        if (limit > 0) {
            qLimit += String.valueOf(limit);
        }

        List<BookmarkEntry> entriesList = new ArrayList<BookmarkEntry>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                qLimit
        );

        if (cursor.moveToFirst()) {
            do {
                BookmarkEntry entry = new BookmarkEntry();
                entry.setOrigin(cursor.getString(1));
                entry.setTranslated(cursor.getString(2));
                entry.setOriginLang(cursor.getString(3));
                entry.setTargetLang(cursor.getString(4));
                entriesList.add(entry);
            } while (cursor.moveToNext());
        }

        return entriesList;
    }

    /**
     * Delete all bookmarks from storage
     */
    public void deleteAll() {
        validateTable();

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
