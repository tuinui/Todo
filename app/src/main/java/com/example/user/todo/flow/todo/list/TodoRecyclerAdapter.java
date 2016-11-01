package com.example.user.todo.flow.todo.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.todo.R;

/**
 * Created by Saran on 1/11/2559.
 */

public abstract class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder> {


    public static String KEY_IS_EDIT_STATE = "IS_EDIT_STATE";

    public abstract void setOnLongClickRoot(View v);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_todo_item, parent, false));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final CheckBox checkBox;
        public final TextView tvName;
        public final LinearLayout layoutUndoOverlay;
        public final Button buttonUndo;

        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox_todo_item);
            layoutUndoOverlay = (LinearLayout) v.findViewById(R.id.linearlayout_todo_item_undo_layout);
            buttonUndo = (Button) v.findViewById(R.id.button_todo_item_undo);
            tvName = (TextView) v.findViewById(R.id.textview_todo_item);
            setOnLongClickRoot(v);
        }
    }
}
