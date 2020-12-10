package com.edmingle.materialtodo.Fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edmingle.materialtodo.Adapter.TaskRowAdapter;
import com.edmingle.materialtodo.Pojo.TaskItem;
import com.edmingle.materialtodo.R;
import com.edmingle.materialtodo.RecyclerItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskFragment extends Fragment {

    public RecyclerView rv_tasks;
    public List<TaskItem> mTasks;
    public TaskItem taskItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        rv_tasks = rootView.findViewById(R.id.rv_tasks);
        LinearLayout ll_cancel_button = rootView.findViewById(R.id.ll_cancel_button);
        FloatingActionButton fab_addButton = rootView.findViewById(R.id.fab_addButton);
        mTasks = generateData();
        refreshList();

        rv_tasks.setLongClickable(true);
        fab_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog();
            }
        });

        ll_cancel_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDialog();
            }
        });
        rv_tasks.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv_tasks ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                modifyOne(position);
            }

            @Override public void onLongItemClick(View view, int position) {
//               deleteOne(position);
            }
        }));
//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                int position = viewHolder.getAdapterPosition();
//                deleteOne(position);
//                refreshList();
//
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(rv_tasks);
        return rootView;
    }

    private void addDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Add");
        alert.setMessage("Do you want to add ?");

        // Create TextView
        final EditText name = new EditText (getContext());
        name.setHint("Enter Title");
        final EditText text = new EditText(getContext());
        text.setHint("Enter Description");

        // Checkbox
        final CheckBox importantCheck = new CheckBox(getContext());
        importantCheck.setText("Important");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 70, 0);

        layout.addView(name, layoutParams);
        layout.addView(text, layoutParams);
        layout.addView(importantCheck, layoutParams);

        alert.setView(layout);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Random color & add to list
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                String important;
                if(importantCheck.isChecked()) {
                    important = "y";
                }
                else {
                    important = "n";
                }

                if(name.length() > 0 || text.length() > 0) {
                    taskItem = new TaskItem(color, name.getText().toString(), text.getText().toString(), important);
                    addItem(taskItem);
                    refreshList();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void deleteOne(int pos) {
        final int position = pos;
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete");
        alert.setMessage("Do you want to delete ?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteOnePos(position);
                refreshList();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void clearDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Clear");
        alert.setMessage("Do you want to clear ?");

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteAll();
                refreshList();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void refreshList() {
        TaskRowAdapter adapter = new TaskRowAdapter(mTasks);
        rv_tasks.setHasFixedSize(true);
        rv_tasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_tasks.setAdapter(adapter);
    }

    // GENERATE INITIAL DATA
    private List<TaskItem> generateData() {
        mTasks = new ArrayList<>();
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        if(myData != null)
        {
            try {
                JSONArray jsonArray = new JSONArray(myData);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String data  = jsonArray.getString(i);
                    String[] splitData = data.split("\\.");

                    mTasks.add(new TaskItem(Integer.parseInt(splitData[0]), splitData[1], splitData[2], splitData[3]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            taskItem = new TaskItem(Color.BLACK, "Florent", "Test", "y");
            addItem(taskItem);
        }

        return mTasks;
    }

    private void modifyOne(final int position) {

        taskItem = mTasks.get(position);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Modify");
        alert.setMessage("Do you want to modify");

        // Create TextView
        final EditText name = new EditText (getContext());
        name.setText(taskItem.getPseudo());

        final EditText text = new EditText(getContext());
        text.setText(taskItem.getText());

        // Checkbox
        final CheckBox importantCheck = new CheckBox(getContext());
        importantCheck.setText("Important");

        if(taskItem.getImportant().equals("y")) {
            importantCheck.setChecked(true);
        }

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 70, 0);

        layout.addView(name, layoutParams);
        layout.addView(text, layoutParams);
        layout.addView(importantCheck, layoutParams);

        alert.setView(layout);


        alert.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String important;
                if(importantCheck.isChecked()) {
                    important = "y";
                }
                else {
                    important = "n";
                }

                if(name.length() > 0 || text.length() > 0) {
                    taskItem = new TaskItem(taskItem.getColor(), name.getText().toString(), text.getText().toString(), important);
                    modifyItem(position, taskItem);
                    refreshList();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();


    }

    private void addItem(TaskItem item) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;
        if(myData == null) {
            jsonArray = new JSONArray();
            jsonArray.put(item.getColor() + "." + item.getPseudo() + "." + item.getText() + "." + item.getImportant());
            mTasks.add(item);
        }
        else {
            try {
                jsonArray = new JSONArray(myData);
                jsonArray.put(item.getColor() + "." + item.getPseudo() + "." + item.getText() + "." + item.getImportant());
                mTasks.add(item);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    private void deleteOnePos(int pos) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(myData);

            jsonArray.remove(pos);
            mTasks.remove(pos);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    private void deleteAll() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        JSONArray jsonArray = new JSONArray();
        mTasks = new ArrayList<>();

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray.toString());
        editor.apply();
    }
    private void modifyItem(int position, TaskItem e) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(myData);
            jsonArray.remove(position);
            jsonArray.put(e.getColor() + "." + e.getPseudo() + "." + e.getText() + "." + e.getImportant());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        mTasks.remove(position);
        mTasks.add(e);

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }
}