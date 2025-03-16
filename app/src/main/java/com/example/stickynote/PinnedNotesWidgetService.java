package com.example.stickynote;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PinnedNotesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PinnedNotesWidgetFactory(this.getApplicationContext(), intent);
    }
}
class PinnedNotesWidgetFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context context;
    private List<Note> pinnedNotes;
    private int appWidgetId;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "StickyNotePrefs";
    private static final String NOTES_KEY = "notes";
    public PinnedNotesWidgetFactory(Context context, Intent intent) {
        this.context = context;
        this.pinnedNotes = new ArrayList<>();
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        pinnedNotes.clear();
        // Đọc danh sách notes từ SharedPreferences
        String json = sharedPreferences.getString(NOTES_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Note>>() {}.getType();
            List<Note> notes = gson.fromJson(json, type);
            if (notes != null) {
                for (Note note : notes) {
                    if (note != null && note.isPinned()) {
                        pinnedNotes.add(note);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        pinnedNotes.clear();
    }

    @Override
    public int getCount() {
        return pinnedNotes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= pinnedNotes.size()) {
            return null;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note_item);
        Note note = pinnedNotes.get(position);
        views.setTextViewText(R.id.widget_note_title, note.getTitle());
        views.setTextViewText(R.id.widget_note_content, note.getNotes());
        views.setTextColor(R.id.widget_note_title, note.getColor());
        views.setTextColor(R.id.widget_note_content, note.getColor());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
