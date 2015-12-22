package com.ngreenan.mytimechecker.xml;

import android.content.Context;
import android.util.Log;

import com.ngreenan.mytimechecker.R;
import com.ngreenan.mytimechecker.db.DBOpenHelper;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Region;

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
public class RegionsJDOMParser {

    private static final String LOGTAG = "My Time Checker - XML";

    private static final String REGION_TAG = "region";

    public List<Region> parseXML(Context context) {
        //open the XML file
        InputStream stream = context.getResources().openRawResource(R.raw.region);
        SAXBuilder builder = new SAXBuilder();
        List<Region> regions = new ArrayList<>();

        try {
            Document document = builder.build(stream);
            Element rootNode = document.getRootElement();
            List<Element> list = rootNode.getChildren(REGION_TAG);

            for (Element node : list) {
                Region region = new Region();
                region.setRegionID(Long.parseLong(node.getChildText(DBOpenHelper.COLUMN_REGIONID)));
                region.setRegionName(node.getChildText(DBOpenHelper.COLUMN_REGIONNAME));
                regions.add(region);
            }
            
            
        } catch (JDOMException e) {
            Log.i(LOGTAG, e.getMessage());
        } catch (IOException e) {
            Log.i(LOGTAG, e.getMessage());
        }

        return regions;
    }
}
