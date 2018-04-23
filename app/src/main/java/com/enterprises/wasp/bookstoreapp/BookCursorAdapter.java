package com.enterprises.wasp.bookstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.enterprises.wasp.bookstoreapp.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the individual views that we want to modify in the list_item layout
        TextView nameView = view.findViewById(R.id.name);
        TextView summaryView = view.findViewById(R.id.summary);
        TextView priceView = view.findViewById(R.id.price);
        TextView quantityView = view.findViewById(R.id.quantity);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int summaryColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_GENRE);
        int priceColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookSummary = cursor.getString(summaryColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameView.setText(bookName);
        summaryView.setText(genre(Integer.valueOf(bookSummary)));
        String price = context.getString(R.string.dollar_sign) + bookPrice;
        priceView.setText(price);
        String book = bookQuantity + " " + context.getString(R.string.available_new);
        if (Integer.valueOf(bookQuantity) == 0)
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
                genreString = "Action";
                break;
            case 2:
                genreString = "Adventure";
                break;
            case 3:
                genreString = "Comedy";
                break;
            case 4:
                genreString = "Drama";
                break;
            case 5:
                genreString = "Fantasy";
                break;
            case 6:
                genreString = "Horror";
                break;
            case 7:
                genreString = "Mythology";
                break;
            case 8:
                genreString = "Romance";
                break;
            case 9:
                genreString = "Satire";
                break;
            case 10:
                genreString = "Tragedy";
                break;
            default:
                genreString = "Unknown";
                break;
        }
        return genreString;
    }
}

