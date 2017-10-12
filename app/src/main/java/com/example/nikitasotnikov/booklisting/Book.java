package com.example.nikitasotnikov.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nikitasotnikov on 03.09.2017.
 */

public class Book implements Parcelable {

    //We use Creator for this Project for easy switch orientation screen
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    private String mAuthor;
    private String mTitle;
    private String mUrl;


    /**
     * Constructs a new {@link Book} object.
     *
     * @param author is the author of the book
     * @param title  is the title of the book
     * @param url    is the url of the book
     */
    public Book(String author, String title, String url) {
        mAuthor = author;
        mTitle = title;
        mUrl = url;
    }

    protected Book(Parcel in) {
        mAuthor = in.readString();
        mTitle = in.readString();
        mUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mTitle);
        parcel.writeString(mUrl);
    }

    /**
     * @return mAuthor of book
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return mTitle of book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return mUrl of book
     */
    public String getUrl() {
        return mUrl;
    }

}

