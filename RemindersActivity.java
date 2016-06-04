package com.pasindukollure.productivityappbtns;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class RemindersActivity extends AppCompatActivity {

    ListView remindersListView;
    ArrayList<String> remindersList = new ArrayList<String>();
    ArrayAdapter<String> remindersListAdapter;

    EditText reminderBox = null;
    AlertDialog.Builder remindersPopUp;
    SQLiteDatabase reminderMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reminders");

        remindersListView = (ListView) findViewById(R.id.listViewReminders);
        remindersListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, remindersList);
        remindersListView.setAdapter(remindersListAdapter);

        reminderMemory = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
        reminderMemory.execSQL("CREATE TABLE IF NOT EXISTS remindersTable (reminders VARCHAR, id INTEGER PRIMARY KEY)");
        Cursor u = reminderMemory.rawQuery("SELECT * FROM remindersTable", null);

        int count = 0;

        u.moveToFirst();
        try{
            int reminderIndex = u.getColumnIndex("reminders");
            while(u != null){
                Log.i("Statement", u.getString(reminderIndex));
                count++;
                u.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(count>0){
            try{
                int reminderIndex2 = u.getColumnIndex("reminders");
                u.moveToFirst();
                for(int i=0; i<count; i++){
                    remindersList.add(u.getString(reminderIndex2));
                    u.moveToNext();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Log.i("Amount", Integer.toString(count));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder reminderPopUp = new AlertDialog.Builder(RemindersActivity.this);
                Context context = RemindersActivity.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                reminderBox = new EditText(context);
                reminderBox.setHint("Reminder");
                layout.addView(reminderBox);

                reminderPopUp.setTitle("Create a Reminder");
                reminderPopUp.setView(layout);
                reminderPopUp.setPositiveButton("CREATE REMINDER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reminderNameText = reminderBox.getText().toString();
                        remindersList.add(reminderBox.getText().toString());
                        remindersListAdapter.notifyDataSetChanged();
                        reminderMemory.execSQL("INSERT INTO remindersTable (reminders) VALUES ('"+reminderNameText+"')");
                    }
                });
                reminderPopUp.setNegativeButton("EXIT", null);
                reminderPopUp.show();
            }
        });

        editReminder();
        setNotification();
    }

    private void editReminder(){
        this.remindersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final EditText editReminder = new EditText(RemindersActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                editReminder.setLayoutParams(lp);

                editReminder.setText(remindersList.get(position));
                new AlertDialog.Builder(RemindersActivity.this)
                        .setTitle("Edit Reminder")
                        .setView(editReminder)
                        .setPositiveButton("SUBMIT EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String orgReminder = remindersList.get(position);
                        String newReminder = editReminder.getText().toString();

                        remindersList.set(position,newReminder);
                        remindersListAdapter.notifyDataSetChanged();

                        reminderMemory.execSQL("UPDATE remindersTable SET reminders = '"+newReminder+"' WHERE reminders = '"+orgReminder+"'");
                    }
                })
                        .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reminderToDelete = remindersList.get(position);
                        remindersList.remove(position);
                        remindersListAdapter.notifyDataSetChanged();

                        reminderMemory.execSQL("DELETE FROM remindersTable WHERE reminders = '"+reminderToDelete+"'");
                    }
                })
                .show();
                return true;
            }
        });
    }

    public void setNotification(){
        this.remindersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                EditText notifyDays = new EditText(RemindersActivity.this);
                notifyDays.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText notifyHours = new EditText(RemindersActivity.this);
                notifyHours.setInputType(InputType.TYPE_CLASS_NUMBER);
                EditText notifyMinutes = new EditText(RemindersActivity.this);
                notifyMinutes.setInputType(InputType.TYPE_CLASS_NUMBER);

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                notifyDays.setLayoutParams(lp2);
                notifyHours.setLayoutParams(lp2);
                notifyMinutes.setLayoutParams(lp2);
                notifyDays.setHint("Days");
                notifyHours.setHint("Hours");
                notifyMinutes.setHint("Minutes");

                new AlertDialog.Builder(RemindersActivity.this)
                        .setTitle("Notification Settings")
                        .setView(notifyHours)
                        .setView(notifyMinutes)
                        .setNegativeButton("EXIT", null)
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                remindersNotification(position);
                            }
                        })
                        .show();
            }
        });
    }

    public void remindersNotification(int tappedItemPosition){
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(RemindersActivity.this,0,intent,0); //what is zero
        Notification noti = new Notification.Builder(RemindersActivity.this)
                .setTicker("Reminder Alert")
                .setSmallIcon(R.drawable.ic_add_alert_white_48dp)
                .setContentTitle("Reminder")
                .setContentText(remindersList.get(tappedItemPosition))
                .setContentIntent(pIntent).getNotification();

        noti.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,noti); //what is zero

    }

}

