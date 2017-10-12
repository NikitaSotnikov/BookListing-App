package com.example.nikitasotnikov.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikitasotnikov on 03.09.2017.
 */

public class BookActivity extends AppCompatActivity {

    public static final String LOG_TAG = BookActivity.class.getName();

    static final String SEARCH_RESULTS = "booksSearchResults";

    /**
     * First part URL for book data from the Google Book API dataset
     */
    private static String googleBookRequestFirstPart = "https://www.googleapis.com/books/v1/volumes?q=";

    /**
     * Second part URL for book data from the Google Book API dataset
     */
    private static String googleBookRequestSecondPart;

    /**
     * Final URL for book data from the Google Book API dataset
     */
    private static String googleBookRequestFinal;

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    private View loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        final EditText editTextView = (EditText) findViewById(R.id.edit_book_text_view);
        final Button searchButton = (Button) findViewById(R.id.search_button_book);


        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);


        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v = getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                //Check internet connection
                if (isInternetConnectionAvailable()) {
                    googleBookRequestSecondPart = editTextView.getText().toString();
                    googleBookRequestFinal = googleBookRequestFirstPart + googleBookRequestSecondPart;
                    // Start the AsyncTask to fetch the book data
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(googleBookRequestFinal);

                } else {
                    // Otherwise, display error
                    // First, hide loading indicator so error message will be visible
                    loadingIndicator.setVisibility(View.GONE);
                    TextView mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
                    mEmptyStateTextView.setText(R.string.no_connection);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                }
            }


        });

        if (savedInstanceState != null) {
            Book[] books = (Book[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            mAdapter.addAll(books);
        }


    }

    //Check internet connection is available
    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Book[] books = new Book[mAdapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = mAdapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) books);
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Book}s as the result.
         */
        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            // Clear the adapter of previous book data
            List<Book> result = null;
            result = QueryUtils.fetchBookData(urls[0]);
            return result;
        }

        @Override
        protected void onPreExecute() {
            mAdapter.clear();
            TextView mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of book data from a previous
         * query to Google API. Then we update the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Book> data) {

            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            // Clear the adapter of previous book data
            mAdapter.clear();

            // If there is a valid list of {@link Book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            } else {
                TextView mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
                mEmptyStateTextView.setText(R.string.no_books);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
            }
        }
    }


}
