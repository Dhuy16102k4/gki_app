package com.example.stickynote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextNotes;
    private Button buttonFont, buttonColor, buttonSave;
    private String selectedFont = "Arial";
    private int selectedColor = Color.BLACK;
    private Note noteToEdit;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note1);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextNotes = findViewById(R.id.edit_text_notes);
        buttonFont = findViewById(R.id.button_font);
        buttonColor = findViewById(R.id.button_color);
        buttonSave = findViewById(R.id.button_save);

        // Kiểm tra xem có note cần chỉnh sửa không
        Intent intent = getIntent();
        if (intent.hasExtra("edit_note")) {
            noteToEdit = intent.getParcelableExtra("edit_note");
            position = intent.getIntExtra("position", -1);
            if (noteToEdit != null) {
                editTextTitle.setText(noteToEdit.getTitle());
                editTextNotes.setText(noteToEdit.getNotes());
                selectedFont = noteToEdit.getFont();
                selectedColor = noteToEdit.getColor();
                editTextTitle.setTextColor(selectedColor);
                editTextNotes.setTextColor(selectedColor);
                Typeface typeface;
                switch (selectedFont) {
                    case "Arial":
                        typeface = Typeface.SANS_SERIF;
                        break;
                    case "Times New Roman":
                        typeface = Typeface.SERIF;
                        break;
                    case "Courier New":
                        typeface = Typeface.MONOSPACE;
                        break;
                    case "Verdana":
                        typeface = Typeface.SANS_SERIF;
                        break;
                    default:
                        typeface = Typeface.DEFAULT;
                        break;
                }
                editTextTitle.setTypeface(typeface);
                editTextNotes.setTypeface(typeface);
            }
        }

        buttonFont.setOnClickListener(v -> showFontDialog());
        buttonColor.setOnClickListener(v -> showColorDialog());
        buttonSave.setOnClickListener(v -> saveNote());
    }

    private void showFontDialog() {
        String[] fonts = {"Arial", "Times New Roman", "Courier New", "Verdana"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn font chữ");
        builder.setItems(fonts, (dialog, which) -> {
            selectedFont = fonts[which];
            Typeface typeface;
            switch (selectedFont) {
                case "Arial":
                    typeface = Typeface.SANS_SERIF;
                    break;
                case "Times New Roman":
                    typeface = Typeface.SERIF;
                    break;
                case "Courier New":
                    typeface = Typeface.MONOSPACE;
                    break;
                case "Verdana":
                    typeface = Typeface.SANS_SERIF;
                    break;
                default:
                    typeface = Typeface.DEFAULT;
                    break;
            }
            editTextTitle.setTypeface(typeface);
            editTextNotes.setTypeface(typeface);
            Toast.makeText(this, "Font chữ đã chọn: " + selectedFont, Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showColorDialog() {
        int[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN};
        String[] colorNames = {"Đen", "Đỏ", "Xanh dương", "Xanh lá"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn màu chữ");
        builder.setItems(colorNames, (dialog, which) -> {
            selectedColor = colors[which];
            editTextTitle.setTextColor(selectedColor);
            editTextNotes.setTextColor(selectedColor);
            Toast.makeText(this, "Màu chữ đã chọn: " + colorNames[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (title.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
            return;
        }
        // vấn đề gặp phải là các note bị sai logic
        // khi add nhiều note thì note nó bị tự pin
        // khi nhấn pin nhiều note và out ra thì nó ko cho gỡ pin?? (lỗi display khi nhấn pin 1 lần nữa để gỡ thì nó update widget nhưng ko update trong app và khi thêm thêm note mới chưa pin cũng hiện check đã pin.)
        // => lỗi dsplay
        String currentDate = noteToEdit != null ? noteToEdit.getDate() : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean isPinned = noteToEdit != null ? noteToEdit.isPinned() : false;
        Note note = new Note(currentDate, title, notes, isPinned, selectedFont, selectedColor);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_note", note);
        if (position != -1) {
            resultIntent.putExtra("position", position); // Trả về vị trí nếu là chỉnh sửa
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}