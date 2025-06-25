package com.labs.lab6;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnCompletedTaskClickListener listener;

    @NonNull
    @Override
    public CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_task, parent, false);
        return new CompletedTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.tvCompletedTaskTitle.setText(currentTask.title);
        holder.tvCompletedTaskTitle.setPaintFlags(holder.tvCompletedTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCompletedTaskTitle;
        private final ImageButton btnDelete;

        public CompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompletedTaskTitle = itemView.findViewById(R.id.tv_completed_task_title);
            btnDelete = itemView.findViewById(R.id.btn_delete_task);

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(tasks.get(position));
                }
            });
        }
    }

    public interface OnCompletedTaskClickListener {
        void onDeleteClick(Task task);
    }

    public void setOnCompletedTaskClickListener(OnCompletedTaskClickListener listener) {
        this.listener = listener;
    }
}