package com.leafcastlelabs.android.roomdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leafcastlelabs.android.roomdemo.model.Person;
import com.leafcastlelabs.android.roomdemo.model.Repository;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //constants
    public static final String CLICK_COUNT = "CLICK_COUNT";
    public static final String SHARED_PREFS_FOR_CLICKS = "SHARED_PREFS_FOR_CLICKS";
    private static final String TAG = "MainActivity";

    //ui widgets
    private TextView txtMain;
    private Button btnAdd, btnSearch;
    private EditText edtFirstname, edtLastname, edtSearch;

    //state (should probably be in a ViewModel)
    private Repository repository;  //repository
    private int buttonClicks;       //for keeping track of button clicks using SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup UI
        setupUI();

        //load clicks from shared prefs (note that this is kept between app restarts as opposed to savedInstanceState)
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FOR_CLICKS, MODE_PRIVATE);
        buttonClicks = prefs.getInt(CLICK_COUNT, 0); //load from shared preferences, and default to 0 if no item found
        Log.d(TAG, "onCreate: click count loaded from sharedprefs: " + buttonClicks);

        //setup repository and livedata observer for the list of persons
        repository = new Repository(getApplication());
        repository.getPersons().observe(this, new Observer<List<Person>>() {
            @Override
            public void onChanged(List<Person> people) {
                txtMain.setText(printList(people));
            }
        });
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FOR_CLICKS, MODE_PRIVATE).edit();
        editor.putInt(CLICK_COUNT, buttonClicks);
        editor.apply();
        Log.d(TAG, "onStop: Saved click count to sharedprefs at: " + buttonClicks);
        super.onStop();
    }

    //sets up the UI widgets
    private void setupUI() {
        txtMain = findViewById(R.id.txtMain);
        edtSearch = findViewById(R.id.edtSearch);
        edtFirstname = findViewById(R.id.edtFirstname);
        edtLastname = findViewById(R.id.edtLastname);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicks++;
                addPerson();
            }
        });

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicks++;
                search();
            }
        });
    }

    //search database for matches for either firstname or lastname
    private void search() {
        String searchString = edtSearch.getText().toString();
        List<Person> multiPersonResult = repository.searchForPersons(searchString);
        searchResultPopoup(multiPersonResult);
    }

    //read edit text values and create new person in database (checks if empty)
    private void addPerson() {
        String first = edtFirstname.getText().toString();
        String last = edtLastname.getText().toString();
        if((!first.equals("")) && (!last.equals(""))) {
            repository.addPerson(first, last, first + "." + last + "@email.com");
        } else {
            Toast.makeText(MainActivity.this, "empty fields", Toast.LENGTH_SHORT).show();
        }
    }

    //helper method for creating popups showing results from search and offering some options to the user
    private void searchResultPopoup(List<Person> resultSet){
        if(resultSet == null || resultSet.size()==0){
            //if no results, create specific dialogue for this
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("no matches found")
                    .setTitle("Search result");
            builder.create().show();
        } else {
            //if one or more results (matches to search), list them all and show options for deleting them or viewing the top (first) result in the list
            String result = printList(resultSet);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(result)
                    .setTitle("Search result")
                    .setPositiveButton("View Top Result", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            viewPerson(resultSet.get(0));
                        }
                    })
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            repository.deleteAll(resultSet);
                        }
                    });
            builder.create().show();
        }
    }

    //helper method for converting list of people to a string for printing in UI
    private String printList(List<Person> people) {
        String print = "Persons: " + people.size();
        for(Person p : people){
            print += "\n" + p.getFirstname() + " " + p.getLastname() + " : " + p.getEmail();
        }
        return print;
    }

    //method for starting PersonActivity to view a given person object using an intent
    private void viewPerson(Person person){
        Intent intent = new Intent(MainActivity.this, PersonActivity.class);
        intent.putExtra("id", person.getUid());
        startActivity(intent);
    }
}