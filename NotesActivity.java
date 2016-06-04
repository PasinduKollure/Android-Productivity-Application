package com.pasindukollure.productivityappbtns;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> classList = new ArrayList<String>();
    ArrayAdapter<String> classListAdapter;

    static ArrayList<String> notesList = new ArrayList<String>();
    ArrayAdapter<String> notesListAdapter;

    static SQLiteDatabase noteMemory;
    static Cursor c;
    int classesIndex;
    String orgText;
    String listOfClasses;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Classes");

        classListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classList);
        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(classListAdapter);

        //creates database, tests, appTable, myTable
        noteMemory = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
        noteMemory = this.openOrCreateDatabase("Written", MODE_PRIVATE, null);
        //noteMemory.execSQL("CREATE TABLE IF NOT EXISTS noteTable (placeHolder VARCHAR)");
        noteMemory.execSQL("CREATE TABLE IF NOT EXISTS myTable (classes VARCHAR, notes VARCHAR, id INTEGER PRIMARY KEY)");
        c = noteMemory.rawQuery("SELECT * FROM myTable", null);
        //Cursor y = noteMemory.rawQuery("SELECT * FROM noteTable", null);
        int counter = 0;

        //counts the number of classes (rows) in database
        try{
            classesIndex = c.getColumnIndex("classes");

            c.moveToFirst();
            while(c != null){
                Log.i("Statement", c.getString(classesIndex));
                counter++;
                c.moveToNext();
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        //if there is data in the db. If not, classList (arrayList) doesn't need to be inputted with classes the user made
        if(counter > 0) {
            try {
                c.moveToFirst();
                for (int i = 0; i < counter; i++){
                    classList.add(c.getString(classesIndex)); //adds classname to classList
                    c.moveToNext();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i("Amount", Integer.toString(counter));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(NotesActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                input.setLayoutParams(lp);

                new AlertDialog.Builder(NotesActivity.this)
                        .setTitle("Add a Class")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String classNameText = String.valueOf(input.getText());
                                classList.add(classNameText);
                                classListAdapter.notifyDataSetChanged();
                                //adds new class to memory
                                noteMemory.execSQL("INSERT INTO myTable (classes) VALUES ('"+classNameText+"')");
                                //creates a column in noteTable named after the class name
                                //noteMemory.execSQL("ALTER TABLE noteTable ADD COLUMN "+classNameText+" VARCHAR");
                            }
                        })
                        .setNegativeButton("EXIT", null)
                        .show();
            }
        });

        longPressDelete();
        itemPress();
    }

    public void longPressDelete() {
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(NotesActivity.this)
                        .setTitle("Class Options")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog editNotePopUp = new AlertDialog.Builder(NotesActivity.this).create();
                                final EditText editClassText = new EditText(NotesActivity.this);

                                editNotePopUp.setTitle("Edit Class Name");
                                editNotePopUp.setView(editClassText);
                                editClassText.setText(classList.get(position));
                                editNotePopUp.setButton("Submit Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        orgText = classList.get(position);
                                        String newText = editClassText.getText().toString();
                                        //recreates noteTable without the edited class and then
                                        //adds in a column with the edited class name and imports the column values
                                        classList.set(position, editClassText.getText().toString());
                                        classListAdapter.notifyDataSetChanged();
                                        noteMemory.execSQL("UPDATE myTable SET classes = '"+newText+"' WHERE classes = '"+orgText+"'");
                                    }
                                });
                                editNotePopUp.show();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String deletingText = classList.get(position);

                                //recreates noteTable without the class name which is deleted
                               /* noteMemory.execSQL("CREATE TABLE IF NOT EXISTS notesTableBackup (backups VARCHAR)");
                                noteMemory.execSQL("INSERT INTO notesTableBackup ("+copyingColumns(deletingText)+")");
                                noteMemory.execSQL("DROP TABLE notesTable");
                                noteMemory.execSQL("CREATE TABLE IF NOT EXISTS notesTable ("+columns()+")");
                                noteMemory.execSQL("INSERT INTO notesTable SELECT * FROM notesTableBackup");
                                noteMemory.execSQL("DROP TABLE notesTableBackup");*/

                                classList.remove(position);
                                classListAdapter.notifyDataSetChanged();

                                noteMemory.execSQL("DELETE FROM myTable WHERE classes = '"+deletingText+"'");
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public void itemPress() {
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent openWritten = new Intent(NotesActivity.this, WrittenActivity.class);

                String classTitleString = classList.get(position);
                openWritten.putExtra("classTitle", classTitleString );
                startActivity(openWritten);
            }
        });
    }

    public String columns() {
        listOfClasses = classList.get(0)+"VARCHAR,";
        for(int i=1; i<classList.size(); i++){

            if (i == classList.size()-1) {
                name = " "+classList.get(i)+" VARCHAR";
            } else if ( (i != classList.size()-1) && (i != 0) && (i != classList.size()-1)) {
                name = " "+classList.get(i)+" VARCHAR,";
            } else if(i == classList.size()-1){
                name = classList.get(i)+" VARCHAR";
            }

            listOfClasses += name;
        }
        return listOfClasses;
    }

    public String copyingColumns(String classToIgnore) {
        listOfClasses = classList.get(0)+"VARCHAR,";
        for(int i=1; i<classList.size(); i++){
            if (classList.get(i) != classToIgnore) {
                if (i == classList.size()-1) {
                    name = " "+classList.get(i)+" VARCHAR";
                } else if ( (i != classList.size()-1) && (i != 0) && (i != classList.size()-1)) {
                    name = " "+classList.get(i)+" VARCHAR,";
                } else if(i == classList.size()-1){
                    name = classList.get(i)+" VARCHAR";
                }
                listOfClasses += name;
            }
        }
        return listOfClasses;
    }

}

