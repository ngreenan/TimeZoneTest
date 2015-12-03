package com.ngreenan.mytimechecker.xml;

import android.content.Context;
import android.util.Log;

import com.ngreenan.mytimechecker.R;
import com.ngreenan.mytimechecker.db.DBOpenHelper;
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
public class ContinentsJDOMParser {

    private static final String LOGTAG = "My Time Checker - XML";
    
    private static final String CONTINENT_TAG = "continent";
    
    public List<Continent> parseXML(Context context) {
        //open the XML file
        InputStream stream = context.getResources().openRawResource(R.raw.continent);
        SAXBuilder builder = new SAXBuilder();
        List<Continent> continents = new ArrayList<>();
        
        try {
            Document document = (Document) builder.build(stream); 
            org.jdom2.Element rootNode = document.getRootElement();
            List<org.jdom2.Element> list = rootNode.getChildren(CONTINENT_TAG);

            for (Element node : list) {
                Continent continent = new Continent();
                continent.setContinentID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_CONTINENTID)));
                continent.setContinentName(node.getChildText(DBOpenHelper.COLUMN_CONTINENTNAME));
                continents.add(continent);
            }
            
            
        } catch (JDOMException e) {
            Log.i(LOGTAG, e.getMessage());
        } catch (IOException e) {
            Log.i(LOGTAG, e.getMessage());
        }

        return continents;
    }
}
