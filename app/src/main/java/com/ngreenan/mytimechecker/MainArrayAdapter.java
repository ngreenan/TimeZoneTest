package com.ngreenan.mytimechecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Nick on 06/12/2015.
 */
public class MainArrayAdapter extends ArrayAdapter<PersonDetail> {

    List<PersonDetail> myPersons;
    Context context;
    int layoutID;
    DBDataSource database;

    //controls
    TextView personName;
    TextView personDetails;
    TextView personColor;
    ImageView personFlag;

    public MainArrayAdapter(Context context, int resource, List<PersonDetail> objects) {
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
        personName = (TextView) view.findViewById(R.id.personName);
        personName.setText(person.getPersonName());

        //details
        personDetails = (TextView) view.findViewById(R.id.personDetails);

        //check if we have a CityID - if not then details unknown
        if (person.getCityID() == 0) {
            personDetails.setText("N/A");
        } else {
            TimeZone timeZone = TimeZone.getTimeZone(person.getTimeZoneName());
            Calendar calendar = Calendar.getInstance(timeZone);

            String detailsString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            detailsString += " ";
            detailsString += String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
            detailsString += ":";
            detailsString += String.format("%02d", calendar.get(Calendar.MINUTE));

            personDetails.setText(detailsString);
        }

        //color
        personColor = (TextView) view.findViewById(R.id.personColor);
        int colorID = context.getResources().getIdentifier("color" + person.getColorID(), "color", context.getPackageName());

        if (colorID != 0) {
            personColor.setBackgroundColor(context.getResources().getColor(colorID));
        }

        //flag
        personFlag = (ImageView) view.findViewById(R.id.personFlag);
        String flagPath = person.getFlagPath();

        if (flagPath != null && flagPath.contains(".png")) {
            flagPath = flagPath.substring(0, flagPath.length() - 4);
        } else {
            flagPath = "unknown";
        }

        int imageID = context.getResources().getIdentifier(flagPath, "drawable", context.getPackageName());
        if (imageID != 0) {
            personFlag.setImageResource(imageID);
        }

        //active overlay
        //TextView personActive = (TextView) view.findViewById(R.id.personActive);
        if (person.isActive()) {
            setAlphas(1F);
        } else {
            setAlphas(0.3F);
        }

        return view;
    }

    private void setAlphas(float v) {
        personName.setAlpha(v);
        personDetails.setAlpha(v);
        personColor.setAlpha(v);
        personFlag.setAlpha(v);
    }
}
