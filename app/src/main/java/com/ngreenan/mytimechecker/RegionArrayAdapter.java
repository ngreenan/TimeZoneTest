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
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Region;

import java.util.List;

/**
 * Created by Nick on 06/12/2015.
 */
public class RegionArrayAdapter extends ArrayAdapter<Region> {

    List<Region> regions;
    Context context;
    int layoutID;
    DBDataSource database;
    int selectedIndex;

    //controls
    TextView regionName;
    ImageView regionSelected;

    public RegionArrayAdapter(Context context, int resource, List<Region> objects) {
        super(context, resource, objects);

        //don't forget to save our passed over variables!
        this.regions = objects;
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
        Region region = regions.get(position);

        //create our LayoutInflator - this will be used to turn a layout into objects
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //create our view from the layout file
        //View view = inflater.inflate(R.layout.person_list_item, null);
        View view = inflater.inflate(layoutID, null);

        //and now we can plug stuff from the Person object we have into our View and its objects

        //name
        regionName = (TextView) view.findViewById(R.id.regionName);
        regionName.setText(region.getRegionName());

        //selected
        regionSelected = (ImageView) view.findViewById(R.id.regionSelected);
        if (usesTick) {
            if (position == selectedIndex) {
                regionSelected.setVisibility(View.VISIBLE);
            } else {
                regionSelected.setVisibility(View.GONE);
            }
        } else {
            regionSelected.setVisibility(View.GONE);
        }

        return view;
    }
}
