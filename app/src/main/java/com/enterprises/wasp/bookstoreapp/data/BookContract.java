package com.enterprises.wasp.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.enterprises.wasp.bookstoreapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {

    }

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Name of the database table for books
         */
        public static final String TABLE_NAME = "books";

        /**
         * Unique ID number for said product ( only for use the database table ).
         * Type: TEXT
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of said product
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Genre of said product
         */
        public static final String COLUMN_PRODUCT_GENRE = "genre";

        /**
         * Author of said product
         */
        public static final String COLUMN_PRODUCT_AUTHOR = "author";

        /**
         * Price of said product
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Quantity available of said product
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Supplier name of said product
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        /**
         * Phone number of said supplier
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_CONTACT = "contact";

        // Integer constants for all the different genres
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_ACTION = 1;
        public static final int GENRE_ADVENTURE = 2;
        public static final int GENRE_COMEDY = 3;
        public static final int GENRE_DRAMA = 4;
        public static final int GENRE_FANTASY = 5;
        public static final int GENRE_HORROR = 6;
        public static final int GENRE_MYTHOLOGY = 7;
        public static final int GENRE_ROMANCE = 8;
        public static final int GENRE_SATIRE = 9;
        public static final int GENRE_TRAGEDY = 10;
    }
}

