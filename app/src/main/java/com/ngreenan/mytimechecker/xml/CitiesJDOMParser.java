package com.ngreenan.mytimechecker.xml;

import android.content.Context;
import android.util.Log;

import com.ngreenan.mytimechecker.R;
import com.ngreenan.mytimechecker.db.DBOpenHelper;
import com.ngreenan.mytimechecker.model.City;
import com.ngreenan.mytimechecker.model.Continent;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 03/12/2015.
 */
public class CitiesJDOMParser {

    private static final String LOGTAG = "My Time Checker - CitiesParser";

    private static final String CITY_TAG = "city";

    public List<City> parseXML(Context context) {
        //open the XML file
        InputStream stream = context.getResources().openRawResource(R.raw.city);
        SAXBuilder builder = new SAXBuilder();
        List<City> cities = new ArrayList<>();

        try {
            Document document = (Document) builder.build(stream);
            Element rootNode = document.getRootElement();
            List<Element> list = rootNode.getChildren(CITY_TAG);

            for (Element node : list) {
                City city = new City();
                city.setCityID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_CITYID)));
                city.setCityName(node.getChildText(DBOpenHelper.COLUMN_CITYNAME));
                city.setContinentID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_CONTINENTID)));
                city.setCountryID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_COUNTRYID)));
                city.setRegionID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_REGIONID)));
                city.setTimeZoneID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_TIMEZONEID)));
                cities.add(city);
            }
            
        } catch (JDOMException e) {
            Log.i(LOGTAG, e.getMessage());
        } catch (IOException e) {
            Log.i(LOGTAG, e.getMessage());
        }

        return cities;
    }
}
