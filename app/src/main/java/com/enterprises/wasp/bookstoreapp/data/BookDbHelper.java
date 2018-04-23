package com.enterprises.wasp.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for the book store inventory app
 */
public class BookDbHelper extends SQLiteOpenHelper {

    // Log constant to use in debug logs
    public final static String LOG_TAG = BookDbHelper.class.getSimpleName();

    // Name of the database file
    public static final String DATABASE_NAME = "inventory.db";

    // Database version. If the schema changes this needs to be incremented by one
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@Link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a string that contains the SQL statements to create the inventory database.
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME
                + " (" + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_PRODUCT_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_PRODUCT_GENRE + " TEXT, "
                + BookContract.BookEntry.COLUMN_PRODUCT_AUTHOR + " TEXT, "
                + BookContract.BookEntry.COLUMN_PRODUCT_PRICE + " INTEGER, "
                + BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER, "
                + BookContract.BookEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to be done here as Database is still at version 1.
    }
}

