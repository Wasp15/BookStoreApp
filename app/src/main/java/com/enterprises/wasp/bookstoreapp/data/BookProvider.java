package com.enterprises.wasp.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class BookProvider extends ContentProvider {

    // Tag for log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    // URI matcher code for the content URI for the books table
    private static final int BOOKS = 100;
    // URI matcher for the content URI for a single book in the books table
    private static final int BOOK_ID = 101;
    /**
     * UriMatcher object to match the content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addUri() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY,
                BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY,
                BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    BookDbHelper bookDbHelper;

    /**
     * Initialize the provider and the database helper object
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        bookDbHelper = new BookDbHelper(getContext());
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get a readable database
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, null,
                        null, null, null, null, null);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI
                // For an example URI such as
                // "content://com.enterprises.wasp.bookstoreapp.books/books/3", the selection ID
                // will be "_id=?" and the selection argument will be a String array containing
                // the actual ID of 3 in this case
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection,we have 1 String in the selection arguments' String array.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content
        // URI the cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException
                        ("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        if (name == null)
            throw new IllegalArgumentException("Book requires a name");

        // Check that the price value is not null
        Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        if (price == null)
            throw new IllegalArgumentException("Book requires a price");

        // Check that supplier contact is not null
        Integer contact = values.getAsInteger(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT);
        if (contact == null)
            throw new IllegalArgumentException("Supplier requires a contact");

        // Get writable database
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        // If the id is -1, then the insertion failed. Log an error and return null
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table.
        // return the new URI with the ID appended to the end of it

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        // Track the number of rows deleted
        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row in the database given the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data
        // given uri changed
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of rows that were deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments ( which could be 0 or 1 or more pets ).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@Link BookEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null)
                throw new IllegalArgumentException("Book requires a name");
        }

        // If the {@Link BookEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the gender is valid.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null)
                throw new IllegalArgumentException("Book requires a price");
        }

        // If the {@Link BookEntry#COLUMN_SUPPLIER_CONTACT} key is present,
        // check that the contact is present.
        if (values.containsKey(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT)) {
            String contact = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT);
            if (contact == null)
                throw new IllegalArgumentException("Supplier requires a contact");
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0)
            return 0;

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection,
                selectionArgs);

        // If 1 or more were updated, then notify all listeners that the data at the
        // given URI changed
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of rows updated
        return rowsUpdated;
    }
}

