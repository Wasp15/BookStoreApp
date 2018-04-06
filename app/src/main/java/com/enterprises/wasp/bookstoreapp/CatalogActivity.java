package com.enterprises.wasp.bookstoreapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.enterprises.wasp.bookstoreapp.data.BookContract;
import com.enterprises.wasp.bookstoreapp.data.BookDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookDbHelper = new BookDbHelper(this);

        insertBook();
        displayDatabaseInformation();
    }

    private void displayDatabaseInformation() {
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookContract.BookEntry.COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT
        };

        Cursor cursor = db.query(BookContract.BookEntry.TABLE_NAME, projection, null,
                null, null, null, null);

        TextView displayTextView = findViewById(R.id.display_view);

        try {
            displayTextView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayTextView.append(
                    BookContract.BookEntry._ID + " - "
                            + BookContract.BookEntry.COLUMN_PRODUCT_NAME + " - "
                            + BookContract.BookEntry.COLUMN_PRODUCT_PRICE + " - "
                            + BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY + " - "
                            + BookContract.BookEntry.COLUMN_SUPPLIER_NAME + " - "
                            + BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT);

            int iDColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry._ID);
            int nameColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
            int contactColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT);

            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(iDColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentContact = cursor.getString(contactColumnIndex);

                displayTextView.append(("\n" + currentId + " - " + currentName + " - " +
                        currentPrice + " - " + currentQuantity + " - " + currentSupplier +
                        " - " + currentContact));
            }
        } finally {
            cursor.close();
        }
    }

    private void insertBook() {
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, "LOTR");
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, 25);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, 5);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, "Amazon");
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT, "021 875 2342");

        long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);
    }
}

