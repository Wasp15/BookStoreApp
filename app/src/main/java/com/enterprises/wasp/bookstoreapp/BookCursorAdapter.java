package com.enterprises.wasp.bookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.enterprises.wasp.bookstoreapp.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    int bookQuantity;
    Context context1;

    /**
     * Constructs a new {@Link BookCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set ( or bound ) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing pet view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        context1 = context;
        // Find the individual views that we want to modify in the list_item layout
        final TextView nameView = view.findViewById(R.id.name);
        TextView summaryView = view.findViewById(R.id.summary);
        final TextView priceView = view.findViewById(R.id.price);
        final TextView quantityView = view.findViewById(R.id.quantity);
        Button salesButton = view.findViewById(R.id.sale_button);
        TextView authorEditText = view.findViewById(R.id.edit_book_author);
        TextView supplierEditText = view.findViewById(R.id.edit_book_supplier);
        TextView contactEditText = view.findViewById(R.id.edit_book_supplier_contact);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int summaryColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_GENRE);
        int priceColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
        int authorColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_AUTHOR);
        int supplierColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
        int contactColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT);
        int idColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry._ID);

        // Read the book attributes from the Cursor for the current book
        final String bookName = cursor.getString(nameColumnIndex);
        final String bookSummary = cursor.getString(summaryColumnIndex);
        final Double bookPrice = cursor.getDouble(priceColumnIndex);
        final String authorString = cursor.getString(authorColumnIndex);
        final String supplierString = cursor.getString(supplierColumnIndex);
        final String contactString = cursor.getString(contactColumnIndex);
        final String idString = cursor.getString(idColumnIndex);
        bookQuantity = cursor.getInt(quantityColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameView.setText(bookName);
        summaryView.setText(genre(Integer.valueOf(bookSummary)));
        String price = context.getString(R.string.dollar_sign) + bookPrice;
        priceView.setText(price);
        String book = bookQuantity + " " + context.getString(R.string.available_new);

        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookQuantity > 0)
                    bookQuantity--;
                else
                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();
                if (bookQuantity == 0)
                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();

                // Create a content values object where column names are the keys,
                // and book attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, bookName);
                values.put(BookContract.BookEntry.COLUMN_PRODUCT_GENRE, bookSummary);
                values.put(BookContract.BookEntry.COLUMN_PRODUCT_AUTHOR, authorString);
                values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, bookPrice);
                values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, bookQuantity);
                values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
                values.put(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT, contactString);

                Uri currentBookUri = Uri.parse(BookContract.BookEntry.CONTENT_URI + "/" + idString);

                int rowsAffected = context.getContentResolver().update(currentBookUri, values,
                        null, null);
            }
        });
        if (bookQuantity == 0)
            quantityView.setText(R.string.out_of_stock);
        else
            quantityView.setText(book);
    }

    /**
     * Private method to determine the genre of the book
     */
    private String genre(int n) {
        String genreString;
        switch (n) {
            case 1:
                genreString = context1.getString(R.string.action_genre);
                break;
            case 2:
                genreString = context1.getString(R.string.adventure_genre);
                break;
            case 3:
                genreString = context1.getString(R.string.comedy_genre);
                break;
            case 4:
                genreString = context1.getString(R.string.drama_genre);
                break;
            case 5:
                genreString = context1.getString(R.string.fantasy_genre);
                break;
            case 6:
                genreString = context1.getString(R.string.horror_genre);
                break;
            case 7:
                genreString = context1.getString(R.string.mythology_genre);
                break;
            case 8:
                genreString = context1.getString(R.string.romance_genre);
                break;
            case 9:
                genreString = context1.getString(R.string.satire_genre);
                break;
            case 10:
                genreString = context1.getString(R.string.tragedy_genre);
                break;
            default:
                genreString = context1.getString(R.string.unknown_genre);
                break;
        }
        return genreString;
    }
}

