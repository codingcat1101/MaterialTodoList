package com.edmingle.materialtodo.Adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edmingle.materialtodo.Pojo.TaskItem;
import com.edmingle.materialtodo.R;

import java.util.List;

public class TaskRowAdapter extends RecyclerView.Adapter<TaskRowAdapter.ViewHolder>{
    List<TaskItem> mItems;

    public TaskRowAdapter(List<TaskItem> items){
        mItems = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.row_list, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskItem item = mItems.get(position);
        holder.name.setText(item.getPseudo());
        holder.text.setText(item.getText());
        if(item.getImportant().equals("y")) {
            holder.text.setTextColor(Color.parseColor("#FF0000"));
        }
        holder.avatar.setImageDrawable(new ColorDrawable(item.getColor()));

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView name,text;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = (ImageView) itemView.findViewById(R.id.avatar);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.text = (TextView) itemView.findViewById(R.id.text);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.rl_row_list);
        }
    }
}
