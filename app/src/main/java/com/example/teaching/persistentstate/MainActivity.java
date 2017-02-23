package com.example.teaching.persistentstate;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "MySenecaPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load preferences from the Shared Preferences file (if it exists)
        loadSharedPreferences();
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

}
