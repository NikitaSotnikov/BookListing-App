package com.example.nikitasotnikov.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nikitasotnikov on 03.09.2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, List<Book> books) {
        super(context, 0, books);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_example, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Find the TextView with view ID location
        TextView author = (TextView) listItemView.findViewById(R.id.author);
        // Display the location of the current book in that TextView
        author.setText(currentBook.getAuthor());

        // Find the TextView with view ID location
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        // Display the location of the current book in that TextView
        title.setText(currentBook.getTitle());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
