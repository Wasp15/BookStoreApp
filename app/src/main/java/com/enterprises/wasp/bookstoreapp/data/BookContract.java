package com.enterprises.wasp.bookstoreapp.data;

import android.provider.BaseColumns;

public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {

    }

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

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
        public final static String COLUMN_PRODUCT_NAME = "Product Name";

        /**
         * Price of said product
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_PRICE = "Price";

        /**
         * Quantity available of said product
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";

        /**
         * Supplier name of said product
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "Supplier Name";

        /**
         * Phone number of said supplier
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_CONTACT = "Supplier Phone Number";
    }
}

