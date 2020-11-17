package com.leafcastlelabs.android.roomdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leafcastlelabs.android.roomdemo.model.Person;
import com.leafcastlelabs.android.roomdemo.model.Repository;

public class PersonActivity extends AppCompatActivity {

    //UI widgets
    private EditText edtFirstname, edtLastname, edtEmail;
    private Button btnUpdate, btnCancel;

    //state (should probably be in a ViewModel)
    private Repository repository;
    private Person selectedPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //get intent from calling activity to know the id of chosen person
        Intent data = getIntent();
        int uid = data.getIntExtra("id", -1);

        //create repository and load data for the chosen person based on id
        repository = new Repository(getApplication());
        selectedPerson = repository.getPerson(uid);

        //set up the ui
        setupUI();
    }

    //setting up the UI widgets
    private void setupUI() {
        edtFirstname = findViewById(R.id.edtFirstname);
        edtLastname = findViewById(R.id.edtLastname);
        edtEmail = findViewById(R.id.edtEmail);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdate = findViewById(R.id.btnUpdate);

        //load Person data into EditText widgets
        edtFirstname.setText(selectedPerson.getFirstname());
        edtLastname.setText(selectedPerson.getLastname());
        edtEmail.setText(selectedPerson.getEmail());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate that data in EditTexts are not empty and then update Person object and write(update) the version in the database through repository
                String newFirst = edtFirstname.getText().toString();
                String newLast = edtLastname.getText().toString();
                String newEmail = edtEmail.getText().toString();
                if((!newFirst.equals("")) && (!newLast.equals("")) && (!newEmail.equals(""))) {
                    selectedPerson.setFirstname(newFirst);
                    selectedPerson.setLastname(newLast);
                    selectedPerson.setEmail(newEmail);

                    Toast.makeText(PersonActivity.this, "Person updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //in case input data wwas invalid, notify user and do nothing more
                    Toast.makeText(PersonActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}