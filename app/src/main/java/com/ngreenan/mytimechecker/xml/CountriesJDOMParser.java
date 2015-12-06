package com.ngreenan.mytimechecker.xml;

import android.content.Context;
import android.util.Log;

import com.ngreenan.mytimechecker.R;
import com.ngreenan.mytimechecker.db.DBOpenHelper;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Country;

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
public class CountriesJDOMParser {

    private static final String LOGTAG = "My Time Checker - XML";

    private static final String COUNTRY_TAG = "country";

    public List<Country> parseXML(Context context) {
        //open the XML file
        InputStream stream = context.getResources().openRawResource(R.raw.country);
        SAXBuilder builder = new SAXBuilder();
        List<Country> countries = new ArrayList<>();

        try {
            Document document = builder.build(stream);
            Element rootNode = document.getRootElement();
            List<Element> list = rootNode.getChildren(COUNTRY_TAG);

            for (Element node : list) {
                Country country = new Country();
                country.setCountryID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_COUNTRYID)));
                country.setCountryName(node.getChildText(DBOpenHelper.COLUMN_COUNTRYNAME));

                //for reasons i don't quite understand, node.getChildText(DBOpenHelper.COLUMN_USESREGIONS)) will give you "1" or "0"
                //BUT if you test to see if it equals "1" then it always returns false
                //if you convert to an Integer and test if it equals 1, it's fine
                //makes no sense to me, but there you go

                if (Integer.parseInt(node.getChildText(DBOpenHelper.COLUMN_USESREGIONS)) == 1) {
                    country.setUsesRegions(true);
                } else {
                    country.setUsesRegions(false);
                }

                country.setFlagPath(node.getChildText(DBOpenHelper.COLUMN_FLAGPATH));
                countries.add(country);
            }
            
        } catch (JDOMException e) {
            Log.i(LOGTAG, e.getMessage());
        } catch (IOException e) {
            Log.i(LOGTAG, e.getMessage());
        }

        return countries;
    }
}
