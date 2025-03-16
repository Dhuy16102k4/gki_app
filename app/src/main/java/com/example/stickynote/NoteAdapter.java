package com.example.stickynote;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stickynote.R;
import com.example.stickynote.Note;
import java.util.List;
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes ;
    private OnNoteListener onNoteListener;

    public NoteAdapter(List<Note> notes, OnNoteListener onNoteListener) {
        this.notes = notes;
        this.onNoteListener = onNoteListener;
    }


    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);

        return new NoteViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.textViewTitle.setText(note.getTitle());
        holder.textViewNotes.setText(note.getNotes());
        holder.textViewDate.setText(note.getDate());
        holder.textViewTitle.setTextColor(note.getColor());
        holder.textViewNotes.setTextColor(note.getColor());
        Typeface typeface;
        switch (note.getFont()) {
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
        holder.textViewTitle.setTypeface(typeface);
        holder.textViewNotes.setTypeface(typeface);
        if (note.isPinned()) {
            holder.buttonPin.setImageResource(R.drawable.baseline_check_24); // Icon khi pinned
            holder.textViewTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_check_24, 0);
        } else {
            holder.buttonPin.setImageResource(R.drawable.ic_pin); // Icon khi không pinned
            holder.textViewTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0); // Xóa drawable khi không pin
        }

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewNotes, textViewDate;
        ImageButton buttonPin, buttonDelete;
        public NoteViewHolder(@NonNull View itemView , OnNoteListener onNoteListener) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewNotes = itemView.findViewById(R.id.text_view_notes);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            buttonPin = itemView.findViewById(R.id.button_pin);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            buttonPin.setOnClickListener(v -> {
                if (onNoteListener != null) {
                    onNoteListener.onPinClick(getAdapterPosition());
                }
            });
            buttonDelete.setOnClickListener(v -> {
                if (onNoteListener != null) {
                    onNoteListener.onDeleteClick(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(v -> {
                if(onNoteListener!=null){
                    onNoteListener.onItemCLick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnNoteListener{
        void onPinClick(int position);
        void onDeleteClick(int position);
        void onItemCLick(int position);
    }

}
