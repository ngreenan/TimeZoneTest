package com.ngreenan.mytimechecker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Person;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.lang.reflect.Array;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    //database
    DBDataSource datasource;
    List<PersonDetail> myDetails;
    List<PersonDetail> myFriendDetails;
    ListView myDetailsListView;
    ListView theirDetailsListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //the top half of this activity will list our details
        //the bottom half will list all our colleagues' details
        //essentially two lists of Person objects then!
        loadData();

        setHandlers();
    }

    private void loadData() {
        //open up the database
        datasource = new DBDataSource(this);
        datasource.open();

        //get my details and put into ListView
        myDetails = datasource.getMyPersonDetails();
        myDetailsListView = (ListView) findViewById(R.id.myDetailsListView);
        ArrayAdapter<PersonDetail> myArrayAdapter = new PersonArrayAdapter(this, R.layout.person_list_item, myDetails);
        myDetailsListView.setAdapter(myArrayAdapter);
        
        //get my friend persons and put into ListView
        myFriendDetails = datasource.getMyFriendPersonDetails();
        theirDetailsListView = (ListView) findViewById(R.id.theirDetailsListView);
        ArrayAdapter<PersonDetail> myFriendArrayAdapter = new PersonArrayAdapter(this, R.layout.person_list_item, myFriendDetails);
        theirDetailsListView.setAdapter(myFriendArrayAdapter);
    }

    private void setHandlers() {

        //click
        myDetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), PersonActivity.class);
                intent.putExtra("personDetail", myDetails.get(position));
                startActivityForResult(intent,0);
            }
        });

        //long click
        myDetailsListView.setLongClickable(true);

        myDetailsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DetailActivity.this, "You cannot disable yourself!", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        //click
        theirDetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(DetailActivity.this, PersonActivity.class);
                    intent.putExtra("personDetail", myFriendDetails.get(position));
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "whoa", Toast.LENGTH_LONG).show();
                }
            }
        });

        //long click
        theirDetailsListView.setLongClickable(true);

        theirDetailsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //get the PersonDetail we clicked on
                PersonDetail personDetail = myFriendDetails.get(position);

                //toggle Active
                personDetail.toggleActive();

                //save change to database - async so there's no lag on the front end
                new SetActiveTask().execute(personDetail);

                //reload the ListView
                ((BaseAdapter) theirDetailsListView.getAdapter()).notifyDataSetChanged();

                //mark the click as handled
                return true;
            }
        });
    }

    private class SetActiveTask extends AsyncTask<PersonDetail, Integer, Long> {

        @Override
        protected Long doInBackground(PersonDetail... detail) {
            datasource.setActive(detail[0].getPersonID(), detail[0].isActive());
            return null;
        }
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
