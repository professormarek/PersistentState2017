package com.example.teaching.persistentstate;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //to perform database interactions we need several "helper classes" to work with SQLite

    /*
    HELPER CLASS #1 for defining SQL table layouts in Java
    we also need this because the Android database API expects an "_ID" field
    (remember the fish!)
     */
    public static class MyDataEntry implements BaseColumns{
        //here we'll define table and column names as static String constants
        //notice that the _ID field is inherted from BaseColumns
        //table name
        public static final String TABLE_NAME = "studentGrades";
        //column names
        public static final String STUDENT_ID_COLUMN = "studentID";
        public static final String STUDENT_GRADE_COLUMN = "studentGrade";
    }
    /*
    HELPER CLASS #2 for database creation and version management
    stores the datbase name, and version, and queries that we will use
     */
    public class MyDbHelper extends SQLiteOpenHelper{
        //we're going to have some static constants to easily remember information about our database
        //database name
        public static final String DB_NAME = "MyCoolDatabase.db";
        //every time the database schema changes we have to update the format
        //by incrementing the database version (for the framework to do its job)
        public static final int DB_VERSION = 1;

        //queries - note: they're just strings
        private static final String SQL_CREATE_TABLE_QUERY = "CREATE TABLE " + MyDataEntry.TABLE_NAME + " ("
                +MyDataEntry._ID + " INTEGER PRIMARY KEY," + MyDataEntry.STUDENT_ID_COLUMN + " TEXT,"
                + MyDataEntry.STUDENT_GRADE_COLUMN + " TEXT )";
        private static final String SQL_DELETE_QUERY = "DROP TABLE IF EXISTS " + MyDataEntry.TABLE_NAME;

        //constructor
        public MyDbHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);//call the super (base) class's constructor
        }

        /**
         * This framework method is called whenever the database is opened but doesn't yet exist
         * @param db - the database we're working with
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            System.out.println("Executing Query: SQL_CREATE_TABLE " + SQL_CREATE_TABLE_QUERY);
            //execute the query on the databse
            db.execSQL(SQL_CREATE_TABLE_QUERY);
        }

        /**
         * this framework method is called whenever DB_VERSION is incremented
         * that means that the database schema has changed!
         * normally we would write some migration code here! but we're going to be lazy
         * @param db - a reference to the database we're working with
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //shortcut - discard the table and create a new table
            db.execSQL(SQL_DELETE_QUERY);
            onCreate(db);
        }
    }

    public static final String PREF_FILE_NAME = "MySenecaPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load preferences from the Shared Preferences file (if it exists)
        loadSharedPreferences();

        //create a handler for the button
        Button saveGradeButton = (Button)findViewById(R.id.gradeButton);
        saveGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delegate the work to a method
                saveGrade();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveSharedPreferences();
    }

    /*
        reads the data we saved in SharedPreferences and use the data (if applicable)
         */
    protected void loadSharedPreferences(){
        Log.i("DEBUG", "Called loadSharedPreferences()");
        //step 0: open the shared preferences file. if it doesn't exist, it will be created now.
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, 0);
        //get data out of the shared preferences file
        long studentID = sharedPreferences.getLong("studentID", -1);
        if( studentID > 0){
            //if there is a valid saved value for this, insert it into the text box
            EditText idEditText = (EditText)findViewById(R.id.studentId);
            idEditText.setText(""+studentID);
        }
        String studentGrade = sharedPreferences.getString("studentGrade", null);
        if(studentGrade != null){
            EditText gradeEditText = (EditText)findViewById(R.id.studentGrade);
            gradeEditText.setText(studentGrade);
        }

    }

    /*
    save data in the Shared Preferences file specifically, the data the user
    has entered into the text boxes but has not yet stored using the button
     */
    protected void saveSharedPreferences(){
        Log.i("DEBUG", "Called saveSharedPreferences()");
        //step 0: get a SharedPreferences instance
        //getSharedPreferences expects a filename and mode arguments
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, 0);

        //step 1: get an instance of SharedPreferences.Editor object
        //Editor is needed to write/save preferences in the file
        //calling .edit() on our SharedPreferences instance will return an Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //step 2: get the values you want to store..
        EditText idEditText = (EditText)findViewById(R.id.studentId);
        long studentId = Long.parseLong(idEditText.getText().toString());
        EditText gradeEditText = (EditText)findViewById(R.id.studentGrade);
        String studentGrade = gradeEditText.getText().toString();
        //step3 use the Editor to put info into SharedPreferences
        editor.putLong("studentID", studentId);
        editor.putString("studentGrade", studentGrade);
        //step 4: call commit to save the changes!
        //or else!
        editor.commit();

    }

    /**
     * this method demonstrates how to save data in the database
     */
    private void saveGrade(){
        /*
        here we write the student id and grade to the dtabase
        ideally, any work we do with the database could be time-consuming (processing time)
        so it should be done in an AsyncTask - I leave this to you as an exercise!
        */

        //get an instance of MyDbHelper - notice it requires a Context object
        MyDbHelper dbHelper = new MyDbHelper(this);
        //get a writeable reference to the database using dbHelper
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //1) create a new map of values representing the new row in the table
        //where the column names are the table keys
        ContentValues newRow = new ContentValues();
        //2) add rows to the map
        EditText studentIDbox = (EditText)findViewById(R.id.studentId);
        String studentID = studentIDbox.getText().toString();
        EditText studentGradeBox = (EditText)findViewById(R.id.studentGrade);
        String studentGrade = studentGradeBox.getText().toString();
        newRow.put(MyDataEntry.STUDENT_ID_COLUMN, studentID);
        newRow.put(MyDataEntry.STUDENT_GRADE_COLUMN, studentGrade);
        System.out.println("Writing a row to the database: " + studentID + " " + studentGrade);
        //insert the new row into the table
        //middle argument is what to insert in case newRow is a null object (null)
        //insert returns the primary key value for the new row in case we need it
        long newRowID = db.insert(MyDataEntry.TABLE_NAME, null, newRow);
        System.out.println("Result of database insertion " + newRowID);
    }

}
