package com.enterprises.wasp.bookstoreapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.enterprises.wasp.bookstoreapp.data.BookContract;

/**
 * Allows user to create a new pet or edit an existing
 */
public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    // Content URI for the existing book ( null if it's a new book )
    private Uri currentBookUri;
    private EditText nameEditText;
    private EditText authorEditText;
    private EditText supplierEditText;
    private EditText contactEditText;
    private Spinner genreSpinner;
    private EditText priceEditText;
    private EditText quantityEditText;
    private Button callSupplierButton;
    private Button incrementQuantityButton;
    private Button decrementQuantityButton;
    private boolean bookHasChanged = false;
    private int quantity;
    private int genre = BookContract.BookEntry.GENRE_UNKNOWN;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            bookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // If the intent does not contain a book content URI, then we know we are
        // creating a new book.
        if (currentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a book"
            setTitle(getString(R.string.add_book_app_bar_label));

            // Invalidate the options menu, so the delete menu option can be hidden
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book so change the app bar to say "Edit book"
            setTitle(getString(R.string.edit_book_app_bar_title));
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views we have to read data from
        nameEditText = findViewById(R.id.edit_book_name);
        authorEditText = findViewById(R.id.edit_book_author);
        supplierEditText = findViewById(R.id.edit_book_supplier);
        contactEditText = findViewById(R.id.edit_book_supplier_contact);
        genreSpinner = findViewById(R.id.spinner_genre);
        priceEditText = findViewById(R.id.edit_book_price);
        quantityEditText = findViewById(R.id.edit_book_quantity);
        quantityEditText.setText("0");

        // Find all the relevant buttons
        callSupplierButton = findViewById(R.id.contact_supplier_button);
        incrementQuantityButton = findViewById(R.id.increment);
        decrementQuantityButton = findViewById(R.id.decrement);

        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactString = contactEditText.getText().toString().trim();
                if (TextUtils.isEmpty(contactString))
                    Toast.makeText(EditorActivity.this,
                            "No number present for supplier", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contactString));
                    startActivity(intent);
                }
            }
        });

        incrementQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.valueOf(quantityEditText.getText().toString().trim()) + 1;
                quantityEditText.setText(String.valueOf(quantity));
            }
        });

        decrementQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0)
                    quantity = Integer.valueOf(quantityEditText.getText().toString().trim()) - 1;
                else
                    Toast.makeText(EditorActivity.this, R.string.negative_stock_toast,
                            Toast.LENGTH_SHORT).show();
                quantityEditText.setText(String.valueOf(quantity));
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine whether or not
        // the user has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving
        nameEditText.setOnTouchListener(touchListener);
        authorEditText.setOnTouchListener(touchListener);
        supplierEditText.setOnTouchListener(touchListener);
        contactEditText.setOnTouchListener(touchListener);
        genreSpinner.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        incrementQuantityButton.setOnTouchListener(touchListener);
        decrementQuantityButton.setOnTouchListener(touchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the genre of the book
     */
    private void setupSpinner() {
        // Create the adapter for the spinner. The list options are from the String array it will
        // use. The spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        genreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer selected to the constant values
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection))
                    if (selection.equals(getString(R.string.action_genre)))
                        genre = 1;
                    else if (selection.equals(getString(R.string.adventure_genre)))
                        genre = 2;
                    else if (selection.equals(getString(R.string.comedy_genre)))
                        genre = 3;
                    else if (selection.equals(getString(R.string.drama_genre)))
                        genre = 4;
                    else if (selection.equals(getString(R.string.fantasy_genre)))
                        genre = 5;
                    else if (selection.equals(getString(R.string.horror_genre)))
                        genre = 6;
                    else if (selection.equals(getString(R.string.mythology_genre)))
                        genre = 7;
                    else if (selection.equals(getString(R.string.romance_genre)))
                        genre = 8;
                    else if (selection.equals(getString(R.string.satire_genre)))
                        genre = 9;
                    else if (selection.equals(getString(R.string.tragedy_genre)))
                        genre = 10;
                    else
                        genre = 0;
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genre = 0;
            }
        });
    }

    private void saveBook() {
        // Read from the input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String authorString = authorEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String contactString = contactEditText.getText().toString().trim();
        int bookPrice = Integer.parseInt(priceEditText.getText().toString().trim());
        int bookQuantity = Integer.parseInt(quantityEditText.getText().toString().trim());

        // Create a content values object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_GENRE, genre);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_AUTHOR, authorString);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, bookPrice);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, bookQuantity);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT, contactString);

        if (currentBookUri == null) {
            // Insert a new book into the provider, returning the content URI for the new book
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            // Show a toast message on whether the insertion was successful or not
            if (newUri == null)
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.error_toast, Toast.LENGTH_SHORT).show();
            else
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise this is an existing book, so update the the book with the content URI:
            // currentBookUri and pass in the new ContentValues. Pass in null for the selection
            // and selectionArgs because currentBookUri will already identify the correct row
            // in the database that we want to modify
            int rowsAffected = getContentResolver().update(currentBookUri, values, null,
                    null);

            // Show a toast message depending on whether or not the update was successful
            if (rowsAffected == 0)
                // If no rows were affected, then there was an error with the update
                Toast.makeText(this, R.string.edit_book_error_toast,
                        Toast.LENGTH_SHORT).show();
            else
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.edit_book_successful_toast,
                        Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/editor_menu.xml file
        // This adds menu items to the app bar
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    /**
     * Private boolean to check for null values in input fields
     */
    private boolean nullValuesExist() {
        boolean nullValues = false;
        // Read from the input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String authorString = authorEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String contactString = contactEditText.getText().toString().trim();
        String bookPrice = priceEditText.getText().toString().trim();
        String bookQuantity = quantityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString))
            nullValues = true;
        if (TextUtils.isEmpty(authorString))
            nullValues = true;
        if (TextUtils.isEmpty(supplierString))
            nullValues = true;
        if (TextUtils.isEmpty(contactString))
            nullValues = true;
        if (TextUtils.isEmpty(bookPrice))
            nullValues = true;
        if (TextUtils.isEmpty(bookQuantity))
            nullValues = true;

        return nullValues;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked a menu item option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if (!nullValuesExist()) {
                    saveBook();
                    finish();
                } else
                    Toast.makeText(this, "Please fill in all required values",
                            Toast.LENGTH_SHORT).show();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Popup confirmation dialog for the deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to the parent activity
                // which is the {@Link CatalogActivity}
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that the
                // changes should be discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, navigate to parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling the back button press
        if (!bookHasChanged)
            super.onBackPressed();

        // Otherwise there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Discard" button
                        finish();
                    }
                };

        // Show a dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be discarded
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the clickListener for what to do when the
     *                                   user confirms they want to discard the changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative button on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_alert_message);
        builder.setPositiveButton(R.string.positive_button_message, discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.negative_button_message),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        // and continue editing the book.
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(getString(R.string.delete_dialog_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_dialog_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the cancel option, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book
        if (currentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given URI.
            // Pass in null for the selection and selection args because the currentBookUri
            // content URI already identifies the pet we want.
            int rowsDeleted =
                    getContentResolver().delete(currentBookUri, null, null);

            // Shows a toast message depending on whether or not the delete was successful
            if (rowsDeleted == 0)
                Toast.makeText(this, getString(R.string.error_deleting_book),
                        Toast.LENGTH_SHORT).show();
            else
                // Otherwise the deletion was successful and we can display a toast
                Toast.makeText(this, getString(R.string.book_success_delete),
                        Toast.LENGTH_SHORT).show();

            // Close the activity
            finish();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all book attributes, define a projection that contains
        // all the columns from the books table
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,     // Parent activity context
                currentBookUri,   // Query the content URI for the current book
                projection,       // Columns to include in the resulting cursor
                null,    // No selection clause
                null, // No selection args
                null     // Default sort order
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Proceed to moving to the first row of the cursor and reading cursor from it
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int authorColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_AUTHOR);
            int supplierColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
            int contactColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_CONTACT);
            int genreColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_GENRE);
            int priceColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String contact = cursor.getString(contactColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity += cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            authorEditText.setText(author);
            supplierEditText.setText(supplier);
            contactEditText.setText(contact);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(quantity));

            // Genre is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options ( 0 - 11 values ) Then call the setSelection()
            // so that option is displayed on the screen as the current selection
            switch (genre) {
                case BookContract.BookEntry.GENRE_ACTION:
                    genreSpinner.setSelection(1);
                    break;
                case BookContract.BookEntry.GENRE_ADVENTURE:
                    genreSpinner.setSelection(2);
                    break;
                case BookContract.BookEntry.GENRE_COMEDY:
                    genreSpinner.setSelection(3);
                    break;
                case BookContract.BookEntry.GENRE_DRAMA:
                    genreSpinner.setSelection(4);
                    break;
                case BookContract.BookEntry.GENRE_FANTASY:
                    genreSpinner.setSelection(5);
                    break;
                case BookContract.BookEntry.GENRE_HORROR:
                    genreSpinner.setSelection(6);
                    break;
                case BookContract.BookEntry.GENRE_MYTHOLOGY:
                    genreSpinner.setSelection(7);
                    break;
                case BookContract.BookEntry.GENRE_ROMANCE:
                    genreSpinner.setSelection(8);
                    break;
                case BookContract.BookEntry.GENRE_SATIRE:
                    genreSpinner.setSelection(9);
                    break;
                case BookContract.BookEntry.GENRE_TRAGEDY:
                    genreSpinner.setSelection(10);
                    break;
                default:
                    genreSpinner.setSelection(0);
                    break;
            }
        }
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated ( some menu items can be hidden or made visible ).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the delete menu item
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}

