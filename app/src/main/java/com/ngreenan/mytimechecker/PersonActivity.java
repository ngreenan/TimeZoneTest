package com.ngreenan.mytimechecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.PersonDetail;

public class PersonActivity extends AppCompatActivity {

    PersonDetail personDetail;
    DBDataSource datasource;
    EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        personDetail = (PersonDetail)intent.getParcelableExtra("personDetail");

        datasource = new DBDataSource(this);
        datasource.open();

        //populate the activity
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setText(personDetail.getPersonName());

//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    //we've moved away - record the change
//                    EditText editText = (EditText)v;
//                    String name = editText.getText().toString();
//                    personDetail.setPersonName(name);
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.person_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void saveChanges(View view) {
        save();
    }

    public void save() {
        personDetail.setPersonName(nameEditText.getText().toString());
        datasource.updateName(personDetail.getPersonID(), personDetail.getPersonName());
        Toast.makeText(this,"Changes saved!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        datasource.close();
        super.onBackPressed();
    }
}
