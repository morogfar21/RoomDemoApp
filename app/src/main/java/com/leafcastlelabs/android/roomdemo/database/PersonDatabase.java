package com.leafcastlelabs.android.roomdemo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.leafcastlelabs.android.roomdemo.model.Person;

@Database(entities = {Person.class}, version = 3)
public abstract class PersonDatabase  extends RoomDatabase {

    public abstract PersonDAO personDAO();  //mandatory DAO getter
    private static PersonDatabase instance; //database instance for singleton

    //singleton pattern used, for lazy loading + single instance since db object is expensive
    public static PersonDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (PersonDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            PersonDatabase.class, "person_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()   //WARNING: For demo only, database should always be accessed asynchronolously in your apps! (see Repository class)
                            .build();

                }
            }
        }
        return instance;
    }
}
