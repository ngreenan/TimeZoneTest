package com.ngreenan.mytimechecker;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.City;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Country;
import com.ngreenan.mytimechecker.model.PersonDetail;
import com.ngreenan.mytimechecker.model.Region;
import com.ngreenan.mytimechecker.picker.LineColorPicker;
import com.ngreenan.mytimechecker.picker.OnColorChangedListener;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends AppCompatActivity {

    PersonDetail personDetail;
    DBDataSource datasource;
    EditText nameEditText;
    Button startTimeButton;
    Button endTimeButton;
    RadioButton activeRadioYes;
    RadioButton activeRadioNo;
    RadioButton notifyRadioYes;
    RadioButton notifyRadioNo;
    LineColorPicker colorPicker;
    Spinner continentSpinner;
    Spinner countrySpinner;
    Spinner regionSpinner;
    Spinner citySpinner;
    TextView regionTextView;
    TextView activeTextView;
    RadioGroup activeRadioGroup;
    Button deleteButton;

    private List<Continent> continents;
    private List<Country> countries;
    private List<Region> regions;
    private List<City> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        personDetail = (PersonDetail)intent.getParcelableExtra("personDetail");

        datasource = new DBDataSource(this);
        datasource.open();

        //populate the activity
        populateActivity();
    }

    private void setTimeLabels() {
        startTimeButton.setText(String.format("%02d", personDetail.getStartHour()) + ":" + String.format("%02d", personDetail.getStartMin()));
        endTimeButton.setText(String.format("%02d", personDetail.getEndHour()) + ":" + String.format("%02d", personDetail.getEndMin()));
    }

    private void populateActivity() {
        //easy stuff like text boxes etc

        //name
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setText(personDetail.getPersonName());

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new SaveDetailsTask().execute(PersonControl.NAME);
            }
        });


        //start time
        startTimeButton = (Button) findViewById(R.id.startTimeButton);
        //end time
        endTimeButton = (Button) findViewById(R.id.endTimeButton);
        setTimeLabels();

        //active
        activeRadioYes = (RadioButton) findViewById(R.id.activeRadioYes);
        activeRadioNo = (RadioButton) findViewById(R.id.activeRadioNo);
        activeRadioYes.setChecked(personDetail.isActive());
        activeRadioNo.setChecked(!personDetail.isActive());

        //if it's you, you can't use active/deactive, can't delete self
        activeRadioGroup = (RadioGroup) findViewById(R.id.activeRadioGroup);
        activeTextView = (TextView) findViewById(R.id.activeTextView);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        if (personDetail.isMe()) {
            activeRadioGroup.setVisibility(View.GONE);
            activeTextView.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {
            activeRadioGroup.setVisibility(View.VISIBLE);
            activeTextView.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }


        //notify
        notifyRadioYes = (RadioButton) findViewById(R.id.notifyRadioYes);
        notifyRadioNo = (RadioButton) findViewById(R.id.notifyRadioNo);
        notifyRadioYes.setChecked(personDetail.isDisplayNotifications());
        notifyRadioNo.setChecked(!personDetail.isDisplayNotifications());

        //colorpicker
        colorPicker = (LineColorPicker) findViewById(R.id.colorPicker);
        colorPicker.setSelectedColorPosition(personDetail.getColorID() - 1);
        colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                personDetail.setColorID(colorPicker.getSelectedIndex() + 1);
                new SaveDetailsTask().execute(PersonControl.COLOR);
            }
        });

        //spinners
        updateSpinners();
    }

    private void updateSpinners() {

        //continents
        //get data from database
        continents = datasource.getAllContinents();
        Continent selectContinent = new Continent();
        selectContinent.setContinentName("Please select...");
        continents.add(0, selectContinent);
        //continents.set(0,selectContinent);

        //plug continents into spinner
        continentSpinner = (Spinner) findViewById(R.id.continentSpinner);
        ContinentArrayAdapter continentArrayAdapter = new ContinentArrayAdapter(this, R.layout.continent_spinner_item, continents);
        continentArrayAdapter.setDropDownViewResource(R.layout.continent_spinner_item);
        continentSpinner.setAdapter(continentArrayAdapter);

        //set selected continent
        if (personDetail.getContinentID() != 0) {
            //which item is selected?
            int position = 0;
            for(Continent continent : continents) {
                if (continent.getContinentID() == personDetail.getContinentID()) {
                    int i = continentArrayAdapter.getPosition(continent);
                    continentSpinner.setSelection(i);
                    continentArrayAdapter.setSelectedIndex(i);
                }
            }
        } else {
            continentSpinner.setSelection(0);
            continentArrayAdapter.setSelectedIndex(0);
        }

        //countries
        //do we even have a continent yet?
        if (personDetail.getContinentID() != 0) {
            //we have set a continent, so can potentially load a country
            countries = datasource.getCountriesByContinentID(personDetail.getContinentID());

        } else {
            countries = new ArrayList<Country>();
        }

        Country selectCountry = new Country();
        selectCountry.setCountryName("Please select...");
        selectCountry.setUsesRegions(false);
        countries.add(0, selectCountry);
        //countries.set(0,selectCountry);

        countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        CountryArrayAdapter countryArrayAdapter = new CountryArrayAdapter(this, R.layout.country_spinner_item, countries);
        countryArrayAdapter.setDropDownViewResource(R.layout.country_spinner_item);
        countrySpinner.setAdapter(countryArrayAdapter);

        //set selected country
        if (personDetail.getCountryID() != 0) {
            //which item is selected?
            int position = 0;
            for(Country country : countries) {
                if (country.getCountryID() == personDetail.getCountryID()) {
                    int i = countryArrayAdapter.getPosition(country);
                    countrySpinner.setSelection(i);
                    countryArrayAdapter.setSelectedIndex(i);
                }
            }
        } else {
            countrySpinner.setSelection(0);
            countryArrayAdapter.setSelectedIndex(0);
        }

        //regions
        //do we even have a country yet?
        if (personDetail.getCountryID() != 0) {
            //we have set a country, so can potentially load a region
            regions = datasource.getRegionsByCountryID(personDetail.getCountryID());
        } else {
            regions = new ArrayList<Region>();
        }

        Region selectRegion = new Region();
        selectRegion.setRegionName("Please select...");
        regions.add(0, selectRegion);
        //regions.set(0,selectRegion);

        regionSpinner = (Spinner) findViewById(R.id.regionSpinner);
        regionTextView = (TextView) findViewById(R.id.regionTextView);
        RegionArrayAdapter regionArrayAdapter = new RegionArrayAdapter(this, R.layout.region_spinner_item, regions);
        regionArrayAdapter.setDropDownViewResource(R.layout.region_spinner_item);
        regionSpinner.setAdapter(regionArrayAdapter);

        //set selected region
        if (personDetail.getRegionID() != 0) {
            //which item is selected?
            int position = 0;
            for(Region region : regions) {
                if (region.getRegionID() == personDetail.getRegionID()) {
                    int i = regionArrayAdapter.getPosition(region);
                    regionSpinner.setSelection(i);
                    regionArrayAdapter.setSelectedIndex(i);
                }
            }
        } else {
            regionSpinner.setSelection(0);
            regionArrayAdapter.setSelectedIndex(0);
        }

        //we also want to hide the region spinner and its textview if the country doesn't use regions
        Country selectedCountry = (Country)countrySpinner.getSelectedItem();
        if (selectedCountry.isUsesRegions()) {
            regionSpinner.setVisibility(View.VISIBLE);
            regionTextView.setVisibility(View.VISIBLE);
        } else {
            regionSpinner.setVisibility(View.GONE);
            regionTextView.setVisibility(View.GONE);
        }

        //cities
        //this depends on the selected country and whether it uses regions or not

        if (selectedCountry.isUsesRegions()) {
            //do we even have a region yet?
            if (personDetail.getRegionID() != 0) {
                //we have set a region, so can potentially load a city
                cities = datasource.getCitiesByRegionID(personDetail.getRegionID());
            } else {
                cities = new ArrayList<City>();
            }
        } else {
            //do we even have a country yet?
            if (personDetail.getCountryID() != 0) {
                //we have set a country, so can potentially load a region
                cities = datasource.getCitiesByCountryID(personDetail.getCountryID());
            } else {
                cities = new ArrayList<City>();
            }
        }

        City selectCity = new City();
        selectCity.setCityName("Please select...");
        cities.add(0, selectCity);
        //cities.set(0,selectCity);

        citySpinner = (Spinner) findViewById(R.id.citySpinner);
        CityArrayAdapter cityArrayAdapter = new CityArrayAdapter(this, R.layout.city_spinner_item, cities);
        cityArrayAdapter.setDropDownViewResource(R.layout.city_spinner_item);
        citySpinner.setAdapter(cityArrayAdapter);

        //set selected region
        if (personDetail.getCityID() != 0) {
            //which item is selected?
            int position = 0;
            for(City city : cities) {
                if (city.getCityID() == personDetail.getCityID()) {
                    int i = cityArrayAdapter.getPosition(city);
                    citySpinner.setSelection(i);
                    cityArrayAdapter.setSelectedIndex(i);
                }
            }
        } else {
            citySpinner.setSelection(0);
            cityArrayAdapter.setSelectedIndex(0);
        }

        //add event handlers to spinners
        //continents
        continentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //what's the selected continentID
                long continentID = continents.get(position).getContinentID();

                //has it actually changed?
                if (continentID != personDetail.getContinentID()) {
                    //update our record, refresh everything
                    personDetail.setContinentID(continentID);
                    personDetail.setCountryID(0);
                    updateSpinners();
                    new SaveDetailsTask().execute(PersonControl.CONTINENT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //countries
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //what's the selected countryID
                long countryID = countries.get(position).getCountryID();

                //has it actually changed?
                if (countryID != personDetail.getCountryID()) {
                    //update our record, refresh everything
                    personDetail.setCountryID(countryID);
                    //if (countries.get(position).isUsesRegions()) {
                        personDetail.setRegionID(0);
                    //} else {
                    //    personDetail.setCityID(0);
                    //}
                    updateSpinners();
                    new SaveDetailsTask().execute(PersonControl.COUNTRY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //regions
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //what's the selected regionID
                long regionID = regions.get(position).getRegionID();

                //has it actually changed?
                if (regionID != personDetail.getRegionID()) {
                    //update our record, refresh everything
                    personDetail.setRegionID(regionID);
                    personDetail.setCityID(0);
                    updateSpinners();
                    new SaveDetailsTask().execute(PersonControl.REGION);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //cities
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //what's the selected cityID
                long cityID = cities.get(position).getCityID();

                //has it actually changed?
                if (cityID != personDetail.getCityID()) {
                    //update our record, refresh everything
                    personDetail.setCityID(cityID);
                    updateSpinners();
                    new SaveDetailsTask().execute(PersonControl.CITY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getTime(final View view) {

        //load a TimePickerDialog and ask the user for the new time
        int hour = 0;
        int minute = 0;

        //work out which time we're changing from the id of the Button pressed
        switch (view.getId()) {
            case R.id.startTimeButton:
                hour = personDetail.getStartHour();
                minute = personDetail.getStartMin();
                break;
            case R.id.endTimeButton:
                hour = personDetail.getEndHour();
                minute = personDetail.getEndMin();
                break;
        }

        //launch our TimePickerDialog
        TimePickerDialog timePickerDialog;

        //we need to set a context, an OnTimeSetListener, a start hour, a start minute and whether it's 24 hours or not
        timePickerDialog = new TimePickerDialog(PersonActivity.this,
                R.style.AppTheme2,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //this gets called when we've picked a time - which values do we want to set??
                //for each we will set the variables to the new value

                switch (view.getId()) {

                    case R.id.startTimeButton:
                        //variables
                        personDetail.setStartHour(selectedHour);
                        personDetail.setStartMin(selectedMinute);
                        new SaveDetailsTask().execute(PersonControl.START_TIME);
                        break;

                    case R.id.endTimeButton:
                        //variables
                        personDetail.setEndHour(selectedHour);
                        personDetail.setEndMin(selectedMinute);
                        new SaveDetailsTask().execute(PersonControl.END_TIME);
                        break;
                }

                setTimeLabels();
            }
        }, hour, minute, true);

        timePickerDialog.show();
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

    public void save() {
        personDetail.setPersonName(nameEditText.getText().toString());
        datasource.updatePersonDetails(personDetail);
    }

    private enum PersonControl {
        NAME,
        START_TIME,
        END_TIME,
        CONTINENT,
        COUNTRY,
        REGION,
        CITY,
        ACTIVE,
        NOTIFICATIONS,
        COLOR
    }

    private class SaveDetailsTask extends AsyncTask<PersonControl, Integer, Long> {

        @Override
        protected Long doInBackground(PersonControl... control) {
            save();
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            //Toast.makeText(PersonActivity.this, "Saved changes", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        datasource.close();
        super.onBackPressed();
    }

    public void onRadioClicked(View view) {
        switch (view.getId()) {
            case R.id.notifyRadioYes:
                personDetail.setDisplayNotifications(true);
                new SaveDetailsTask().execute(PersonControl.NOTIFICATIONS);
                break;
            case R.id.notifyRadioNo:
                personDetail.setDisplayNotifications(false);
                new SaveDetailsTask().execute(PersonControl.NOTIFICATIONS);
                break;
            case R.id.activeRadioYes:
                personDetail.setActive(true);
                new SaveDetailsTask().execute(PersonControl.ACTIVE);
                break;
            case R.id.activeRadioNo:
                personDetail.setActive(false);
                new SaveDetailsTask().execute(PersonControl.ACTIVE);
                break;
        }
    }

    public void deletePerson(View view) {
        datasource.deletePersonById(personDetail.getPersonID());
        onBackPressed();
    }
}
