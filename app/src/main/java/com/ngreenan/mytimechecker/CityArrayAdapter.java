package com.ngreenan.mytimechecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.City;
import com.ngreenan.mytimechecker.model.Region;

import java.util.List;

/**
 * Created by Nick on 06/12/2015.
 */
public class CityArrayAdapter extends ArrayAdapter<City> {

    List<City> cities;
    Context context;
    int layoutID;
    DBDataSource database;
    int selectedIndex;

    //controls
    TextView cityName;
    ImageView citySelected;

    public CityArrayAdapter(Context context, int resource, List<City> objects) {
        super(context, resource, objects);

        //don't forget to save our passed over variables!
        this.cities = objects;
        this.context = context;
        this.layoutID = resource;
    }

    public void setSelectedIndex(int i) {
        this.selectedIndex = i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @NonNull
    private View getCustomView(int position, View convertView, ViewGroup parent, boolean usesTick) {
        //get the person in relevant position
        City city = cities.get(position);

        //create our LayoutInflator - this will be used to turn a layout into objects
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //create our view from the layout file
        //View view = inflater.inflate(R.layout.person_list_item, null);
        View view = inflater.inflate(layoutID, null);

        //and now we can plug stuff from the Person object we have into our View and its objects

        //name
        cityName = (TextView) view.findViewById(R.id.cityName);
        cityName.setText(city.getCityName());

        //selected
        citySelected = (ImageView) view.findViewById(R.id.citySelected);
        if (usesTick) {
            if (position == selectedIndex) {
                citySelected.setVisibility(View.VISIBLE);
            } else {
                citySelected.setVisibility(View.GONE);
            }
        } else {
            citySelected.setVisibility(View.GONE);
        }

        return view;
    }
}
