package com.leafcastlelabs.android.roomdemo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.leafcastlelabs.android.roomdemo.model.Person;

import java.util.List;

@Dao
public interface PersonDAO {

    @Query("SELECT * FROM person")
    LiveData<List<Person>> getAll();

    @Query("SELECT * FROM person WHERE uid LIKE :uid")
    Person findPerson(int uid);

    @Query("SELECT * FROM person WHERE firstname LIKE :firstname AND "
            + "lastname LIKE :lastname LIMIT 1")
    Person findPerson(String firstname, String lastname);

    @Query("SELECT * FROM person WHERE firstname LIKE :name OR "
            + "lastname LIKE :name")
    List<Person> findPersons(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPerson(Person person);

    @Update
    void updatePerson(Person person);

    @Delete
    void delete(Person person);

    @Delete
    void deleteAll(List<Person> persons);

    @Query("DELETE FROM person")
    public void deleteAllPersons();
}