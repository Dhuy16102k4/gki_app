package com.example.stickynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.appwidget.AppWidgetManager;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    public static List<Note> notes = new ArrayList<>();
    private FloatingActionButton buttonAdd;
    ActivityResultLauncher<Intent> launcher;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "StickyNotePrefs";
    private static final String NOTES_KEY = "notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        buttonAdd = findViewById(R.id.button_add);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Đọc notes từ SharedPreferences
        loadNotes();

        // Nếu danh sách rỗng, thêm dữ liệu mẫu
        if (notes.isEmpty()) {
            notes.add(new Note("2023-10-01", "Title 1", "Notes 1", false, "Arial", Color.BLACK));
            notes.add(new Note("2023-10-02", "Title 2", "Notes 2", true, "Times New Roman", Color.RED));
            saveNotes(); // Lưu dữ liệu mẫu vào SharedPreferences

        }

        // Log để kiểm tra danh sách notes sau khi load


        noteAdapter = new NoteAdapter(notes, new NoteAdapter.OnNoteListener() {
            @Override
            public void onPinClick(int position) {
                Note note = notes.get(position);
                note.setPinned(!note.isPinned());
                noteAdapter.notifyItemChanged(position);
                saveNotes();
                updateWidget();
            }

            @Override
            public void onDeleteClick(int position) {
                notes.remove(position);
                noteAdapter.notifyItemRemoved(position);
                saveNotes();
                updateWidget();
            }

            @Override
            public void onItemCLick(int position) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("edit_note", notes.get(position));
                intent.putExtra("position", position);
                launcher.launch(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("new_note")) {
                            Note newNote = data.getParcelableExtra("new_note");
                            if (newNote != null) {
                                if (data.hasExtra("position")) {
                                    int position = data.getIntExtra("position", -1);
                                    if (position != -1) {
                                        notes.set(position, newNote);
                                        noteAdapter.notifyItemChanged(position);
                                    }
                                } else {
                                    notes.add(newNote);
                                    noteAdapter.notifyItemInserted(notes.size() - 1);
                                }
                                saveNotes();
                                updateWidget();
                            }
                        }
                    }
                }
        );

        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            launcher.launch(intent);
        });
    }

    private void saveNotes() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        editor.putString(NOTES_KEY, json);
        editor.apply();
        android.util.Log.d("MainActivity", "Saved notes to SharedPreferences: " + json);
    }

    private void loadNotes() {
        String json = sharedPreferences.getString(NOTES_KEY, null);
        android.util.Log.d("MainActivity", "Loaded JSON from SharedPreferences: " + json);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Note>>() {}.getType();
            notes = gson.fromJson(json, type);
        }
        if (notes == null) {
            notes = new ArrayList<>();
        }
    }

    private void updateWidget() {
        Intent intent = new Intent(this, PinnedNotesWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                .getAppWidgetIds(new android.content.ComponentName(getApplicationContext(), PinnedNotesWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
        android.util.Log.d("MainActivity", "Sent update broadcast to widget, IDs: " + (ids != null ? ids.length : 0));
    }
}