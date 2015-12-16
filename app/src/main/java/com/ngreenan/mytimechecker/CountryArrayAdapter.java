package com.ngreenan.mytimechecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.Country;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.util.List;

/**
 * Created by Nick on 06/12/2015.
 */
public class CountryArrayAdapter extends ArrayAdapter<Country> {

    List<Country> countries;
    Context context;
    int layoutID;
    DBDataSource database;
    int selectedIndex;

    //controls
    TextView countryName;
    ImageView countryFlag;
    ImageView countrySelected;

    public CountryArrayAdapter(Context context, int resource, List<Country> objects) {
        super(context, resource, objects);

        //don't forget to save our passed over variables!
        this.countries = objects;
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
        Country country = countries.get(position);

        //create our LayoutInflator - this will be used to turn a layout into objects
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //create our view from the layout file
        //View view = inflater.inflate(R.layout.person_list_item, null);
        View view = inflater.inflate(layoutID, null);

        //and now we can plug stuff from the Person object we have into our View and its objects

        //name
        countryName = (TextView) view.findViewById(R.id.countryName);
        countryName.setText(country.getCountryName());

        //flag
        countryFlag = (ImageView) view.findViewById(R.id.countryFlag);
        String flagPath = country.getFlagPath();

        if (flagPath != null && flagPath.contains(".png")) {
            flagPath = flagPath.substring(0, flagPath.length() - 4);
        } else {
            flagPath = "unknown";
        }

        int imageID = context.getResources().getIdentifier(flagPath, "drawable", context.getPackageName());
        if (imageID != 0) {
            countryFlag.setImageResource(imageID);
        }

        //selected
        countrySelected = (ImageView) view.findViewById(R.id.countrySelected);
        if (usesTick) {
            if (position == selectedIndex) {
                countrySelected.setVisibility(View.VISIBLE);
            } else {
                countrySelected.setVisibility(View.GONE);
            }
        } else {
            countrySelected.setVisibility(View.GONE);
        }

        return view;
    }
}
