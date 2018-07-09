package com.example.meet9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private DataBase db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Note> NoteList;
    private Context context;

    private final int CREATING = 1;
    private final int CHANGING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DataBase(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NoteList = db.getNotes();
                adapter = new AdapterForNote(NoteList, context);
            }
        }).start();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNote = new Intent(MainActivity.this, CreatingActivity.class);
                startActivityForResult(createNote, CREATING);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        Intent intent = getIntent();
        long id = NoteList.get(NoteList.size() - 1).getID() + 1;

        if(requestCode == CREATING && resultCode == RESULT_OK)
        {
            Note note = new Note(id, intent.getStringExtra("Name"),
                                intent.getStringExtra("Date"),
                                intent.getStringExtra("Content"));
            NoteList.add(note);
            adapter.notifyDataSetChanged();

        }
        else if(requestCode == CHANGING && resultCode == RESULT_OK)
        {
            int position = intent.getIntExtra("Number", 0);

            Note note = new Note(Long.parseLong(intent.getStringExtra("ID")),
                                                intent.getStringExtra("Name"),
                                                intent.getStringExtra("Date"),
                                                intent.getStringExtra("Content"));
            Toast.makeText(context, intent.getStringExtra("Name"), Toast.LENGTH_LONG).show();
            NoteList.set(position, note);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        NoteList = db.getNotes();
//
//        adapter = new AdapterForNote(NoteList, this);
//
//        recyclerView.setAdapter(adapter);


        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null)
            db.close();
    }

//    @Override
//    public void openEditor(String text, String color, long id, int position) {
//
//    }
//
//    @Override
//    public void delete(final long id) {
//
//    }
}
