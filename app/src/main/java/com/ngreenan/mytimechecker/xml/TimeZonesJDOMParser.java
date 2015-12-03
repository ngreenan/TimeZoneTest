package com.ngreenan.mytimechecker.xml;

import android.content.Context;
import android.util.Log;

import com.ngreenan.mytimechecker.R;
import com.ngreenan.mytimechecker.db.DBOpenHelper;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.TimeZone;

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
public class TimeZonesJDOMParser {

    private static final String LOGTAG = "My Time Checker - XML";

    private static final String TIMEZONE_TAG = "timeZone";

    public List<TimeZone> parseXML(Context context) {
        //open the XML file
        InputStream stream = context.getResources().openRawResource(R.raw.timezone);
        SAXBuilder builder = new SAXBuilder();
        List<TimeZone> timeZones = new ArrayList<>();

        try {
            Document document = (Document) builder.build(stream);
            Element rootNode = document.getRootElement();
            List<Element> list = rootNode.getChildren(TIMEZONE_TAG);

            for (Element node : list) {
                TimeZone timeZone = new TimeZone();
                timeZone.setTimeZoneID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_TIMEZONEID)));
                timeZone.setTimeZoneName(node.getChildText(DBOpenHelper.COLUMN_TIMEZONENAME));
                timeZones.add(timeZone);
            }
        } catch (JDOMException e) {
            Log.i(LOGTAG, e.getMessage());
        } catch (IOException e) {
            Log.i(LOGTAG, e.getMessage());
        }

        return timeZones;
    }
}
