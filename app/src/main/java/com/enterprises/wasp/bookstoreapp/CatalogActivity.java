package com.enterprises.wasp.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.enterprises.wasp.bookstoreapp.data.BookContract;
import com.enterprises.wasp.bookstoreapp.data.BookDbHelper;

/**
 * Displays list of books that were entered and stored in the app
 */
public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter cursorAdapter;
    private BookDbHelper bookDbHelper;
    private EditText quantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this,
                        EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = findViewById(R.id.list_view_book);

        // Find and set the empty view on the ListView, so that it can only show when the list has
        // 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        cursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(cursorAdapter);

        quantityEditText = findViewById(R.id.edit_book_quantity);
        bookDbHelper = new BookDbHelper(this);

        // Setup a ClickListener to open the editor activity when list item is clicked
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create a new intent to go to {@Link EditorActivity}
                Intent editorIntent = new Intent(CatalogActivity.this,
                        EditorActivity.class);

                // From the content URI that represents the specific book that was clicked on,
                // by appending the "id" ( passed as input to this method ) onto the
                // {@Link BookEntry#CONTENT_URI}.
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI,
                        id);

                // Set the URI on the data field of the intent
                editorIntent.setData(currentBookUri);

                // Launch the {@Link EditorActivity} to display the data from the current pet
                startActivity(editorIntent);
            }
        });

        // Kick off loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
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

    /**
     * Helper method to delete all books in the database
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete
                (BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_GENRE,
                BookContract.BookEntry.COLUMN_PRODUCT_AUTHOR,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookContract.BookEntry.COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT
        };

        return new CursorLoader(
                this,                       // Parent activity context
                BookContract.BookEntry.CONTENT_URI, // Provider contentURI to query
                projection,                         // Columns to include in the resulting Cursor
                null,                      // No selection clause
                null,                   // No selection arguments
                null                       // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update with this new cursor containing updated book data
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        cursorAdapter.swapCursor(null);
    }
}

