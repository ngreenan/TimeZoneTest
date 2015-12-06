package com.ngreenan.mytimechecker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.City;
import com.ngreenan.mytimechecker.model.Country;
import com.ngreenan.mytimechecker.model.Person;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.util.List;

/**
 * Created by Nick on 06/12/2015.
 */
public class PersonArrayAdapter extends ArrayAdapter<PersonDetail> {

    List<PersonDetail> myPersons;
    Context context;
    int layoutID;
    DBDataSource database;

    public PersonArrayAdapter(Context context, int resource, List<PersonDetail> objects) {
        super(context, resource, objects);

        //don't forget to save our passed over variables!
        this.myPersons = objects;
        this.context = context;
        this.layoutID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        //get the person in relevant position
        PersonDetail person = myPersons.get(position);

        //create our LayoutInflator - this will be used to turn a layout into objects
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //create our view from the layout file
        //View view = inflater.inflate(R.layout.person_list_item, null);
        View view = inflater.inflate(layoutID, null);

        //and now we can plug stuff from the Person object we have into our View and its objects

        //name
        TextView personName = (TextView) view.findViewById(R.id.personName);
        personName.setText(person.getPersonName());

        //details
        TextView personDetails = (TextView) view.findViewById(R.id.personDetails);
        String detailsString = String.format("%02d", person.getStartHour());
        detailsString += ":";
        detailsString += String.format("%02d", person.getStartMin());
        detailsString += "-";
        detailsString += String.format("%02d", person.getEndHour());
        detailsString += ":";
        detailsString += String.format("%02d", person.getEndMin());
        detailsString += " - ";
        detailsString += person.getCityName();
        detailsString += ", ";
        if (person.isUsesRegions()) {
            detailsString += person.getRegionName();
            detailsString += ", ";
        }

        detailsString += person.getCountryName();

        personDetails.setText(detailsString);

        //color
        TextView personColor = (TextView) view.findViewById(R.id.personColor);
        int colorID = context.getResources().getIdentifier("color" + person.getColorID(), "color", context.getPackageName());

        if (colorID != 0) {
            personColor.setBackgroundColor(context.getResources().getColor(colorID));
        }

        //flag
        ImageView personFlag = (ImageView) view.findViewById(R.id.personFlag);
        String flagPath = person.getFlagPath();

        if (flagPath.contains(".png")) {
            flagPath = flagPath.substring(0, flagPath.length() - 4);
        }

        int imageID = context.getResources().getIdentifier(flagPath, "drawable", context.getPackageName());
        if (imageID != 0) {
            personFlag.setImageResource(imageID);
        }

        return view;
    }
}
