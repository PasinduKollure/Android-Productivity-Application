package com.pasindukollure.productivityappbtns;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class WrittenActivity extends AppCompatActivity {

    String textNote;
    ListView listNotes;
    ArrayList<String> notesList = new ArrayList<String>();
    ArrayAdapter<String> notesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_written);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesList);
        Intent i = getIntent();
        String classTitle = i.getStringExtra("classTitle");
        getSupportActionBar().setTitle(classTitle+" Notes");

        listNotes = (ListView) findViewById(R.id.listWritten);
        listNotes.setAdapter(notesListAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(WrittenActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                input.setLayoutParams(lp);

                new AlertDialog.Builder(WrittenActivity.this)
                        .setTitle("Add a Note")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                textNote = String.valueOf(input.getText());
                                notesList.add(textNote);
                                notesListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("EXIT", null)
                        .show();
            }
        });

        noteOptions();
    }

    public void noteOptions() {
        this.listNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(WrittenActivity.this)
                        .setTitle("Note")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog editNotePopUp = new AlertDialog.Builder(WrittenActivity.this).create();
                                final EditText editText = new EditText(WrittenActivity.this);
                                editText.setText(notesList.get(position));
                                editNotePopUp.setTitle("Edit Note");
                                editNotePopUp.setView(editText);
                                editNotePopUp.setButton("Submit Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String editedNote = String.valueOf(editText.getText());
                                        notesList.set(position, editedNote);
                                        notesListAdapter.notifyDataSetChanged();
                                    }
                                });
                                editNotePopUp.show();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notesList.remove(position);
                                notesListAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
                return true;
            }
        });
    }
}
