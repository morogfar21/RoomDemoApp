package com.leafcastlelabs.android.roomdemo.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.leafcastlelabs.android.roomdemo.database.PersonDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Repository {

    private PersonDatabase db;              //database
    private ExecutorService executor;       //for asynch processing
    private LiveData<List<Person>> persons; //livedata

    //constructor - takes Application object for context
    public Repository(Application app){
        db = PersonDatabase.getDatabase(app.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
        persons = db.personDAO().getAll();
    }

    public LiveData<List<Person>> getPersons(){
        return persons;
    }

    /////// NON-ASYNCH VERSIONS OF METHODS ONLY WORK BECAUSE WE HAVE "allowMainThreadQueries()" on DATABASE  //////////

    //WARNING: for demo only
    public Person getPerson(int uid){
        return db.personDAO().findPerson(uid);
    }

    //WARNING: for demo only
    public void updatePerson(Person person){
        db.personDAO().updatePerson(person);
    }

    //WARNING: for demo only, use addPersonAsynch method
    public void addPerson(String firstname, String lastname, String email){
        db.personDAO().addPerson(new Person(firstname, lastname, email));
    }

    //WARNING: for demo only, use searchForPersonAsynch method
    public Person searchForPerson(String firstname, String lastname){
        return db.personDAO().findPerson(firstname, lastname);
    }

    //WARNING: for demo only, use searchForPersonAsynch method
    public List<Person> searchForPersons(String name){
        return db.personDAO().findPersons(name);
    }

    //WARNING: for demo only, use searchForPersonAsynch method
    public void deleteAll(List<Person> personsToDelete){
        db.personDAO().deleteAll(personsToDelete);
    }

    /////// FIND ASYNCH VERSIONS OF METHODS BELOW  //////////

    //find person by database entry id (primary key)
    public Person getPersonAsycnh(int uid){
        Future<Person> p = executor.submit(new Callable<Person>() {
            @Override
            public Person call() {
                return db.personDAO().findPerson(uid);
            }
        });

        try {
            return p.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    //update Person in database
    public void updatePersonAsynch(Person person){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.personDAO().updatePerson(person);
            }
        });
    }

    //add a new Person to database
    public void addPersonAsynch(String firstname, String lastname, String email){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.personDAO().addPerson(new Person(firstname, lastname, email));
            }
        });
    }

    //search for Person by firstname and lastname
    public Person searchForPersonAsynch(String firstname, String lastname){
        Future<Person> p = executor.submit(new Callable<Person>() {
            @Override
            public Person call() {
                return db.personDAO().findPerson(firstname, lastname);
            }
        });

        try {
            return p.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    //search for all Persons by either firstname or lastname
    public List<Person> searchForPersonsAsynch(String name){
        Future<List<Person>> ps = executor.submit(new Callable<List<Person>>() {
            @Override
            public List<Person> call() {
                return db.personDAO().findPersons(name);
            }
        });

        try {
            return ps.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    //helper method for testing of database only
    private void createTestData(){
        for(int i=0; i<10; i++) {
            this.addPerson("first" + i, "last" + i, "first" + i + "last" + i + "@email.com");
            //this.addPersonAsynch("first" + i, "last" + i, "first" + i + "last" + i + "@email.com");
        }
    }
}
