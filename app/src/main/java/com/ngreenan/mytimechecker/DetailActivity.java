package com.ngreenan.mytimechecker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        //open up the database
        datasource = new DBDataSource(this);
        datasource.open();
        loadData();

        setHandlers();
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    private void loadData() {
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
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                    //startActivityForResult(intent, 0);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                refreshListViews();

                //mark the click as handled
                return true;
            }
        });
    }

    private void refreshListViews() {
        ((BaseAdapter) myDetailsListView.getAdapter()).notifyDataSetChanged();
        ((BaseAdapter) theirDetailsListView.getAdapter()).notifyDataSetChanged();
    }

    public void viewMain(View view) {
        datasource.close();
        finish();
    }

    @Override
    public void onBackPressed() {
        datasource.close();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addNew();
                return true;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNew() {
        //create a new default Person
        Person person = new Person();
        person.setPersonName("New person");
        person.setColorID(1);
        person.setActive(true);
        person.setDisplayNotifications(true);
        person.setStartHour(8);
        person.setStartMin(0);
        person.setEndHour(22);
        person.setEndMin(0);
        person.setMe(false);
        person = datasource.create(person);

        //get PersonDetails for new Person
        PersonDetail personDetail = datasource.getPersonDetailsById(person.getPersonID());

        //start the activity
        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("personDetail", personDetail);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private class SetActiveTask extends AsyncTask<PersonDetail, Integer, Long> {

        @Override
        protected Long doInBackground(PersonDetail... detail) {
            datasource.setActive(detail[0].getPersonID(), detail[0].isActive());
            return null;
        }
    }
}
