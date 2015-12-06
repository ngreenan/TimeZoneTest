package com.ngreenan.mytimechecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.Person;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    //database
    DBDataSource datasource;
    List<PersonDetail> myDetails;
    List<PersonDetail> myFriendDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //the top half of this activity will list our details
        //the bottom half will list all our colleagues' details
        //essentially two lists of Person objects then!
        loadData();
    }

    private void loadData() {
        //open up the database
        datasource = new DBDataSource(this);
        datasource.open();

        //get my details and put into ListView
        myDetails = datasource.getMyPersonDetails();
        ListView listView = (ListView) findViewById(R.id.myDetailsListView);
        ArrayAdapter<PersonDetail> myArrayAdapter = new PersonArrayAdapter(this, R.layout.person_list_item, myDetails);
        listView.setAdapter(myArrayAdapter);
        
        //get my friend persons and put into ListView
        myFriendDetails = datasource.getMyFriendPersonDetails();
        listView = (ListView) findViewById(R.id.theirDetailsListView);
        ArrayAdapter<PersonDetail> myFriendArrayAdapter = new PersonArrayAdapter(this, R.layout.person_list_item, myFriendDetails);
        listView.setAdapter(myFriendArrayAdapter);
    }

    public void viewMain(View view) {
        datasource.close();
        finish();
    }

    @Override
    public void onBackPressed() {
        datasource.close();
        super.onBackPressed();
    }
}
