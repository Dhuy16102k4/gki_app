package com.example.stickynote;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private String title;
    private String date;
    private boolean pinned;
    private String notes;
    private String font;
    private int color;

    public Note(String date, String title, String notes, boolean pinned, String font, int color) {
        this.date = date;
        this.title = title;
        this.notes = notes;
        this.pinned = pinned;
        this.font = font;
        this.color = color;
    }

    protected Note(Parcel in) {
        date = in.readString();
        title = in.readString();
        notes = in.readString();
        pinned = in.readByte() != 0;
        font = in.readString();
        color = in.readInt();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(notes);
        dest.writeByte((byte) (pinned ? 1 : 0));
        dest.writeString(font);
        dest.writeInt(color);
    }

    // Getter và Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getFont() { return font; }
    public void setFont(String font) { this.font = font; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
}