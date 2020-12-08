package com.tech.mynewsapp.utils;

import android.util.Log;

import com.tech.mynewsapp.model.NewsData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class XMLParser {

    public static final String URL = "https://news.yahoo.com/rss";

    // XML node keys
    static final String KEY_ITEM = "item"; // parent node
    static final String KEY_title = "title";
    static final String KEY_link = "link";
    static final String KEY_pubDate = "pubDate";
    static final String KEY_source = "source";
    static final String KEY_mediacontent = "media:content";
    static final String KEY_mediacredit = "media:credit";

    public String getXmlFromUrl(String url) {
        String xml = null;

        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }

    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public List<NewsData> ResponceParse(String xml) {

        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();

        JSONObject jsonObj = xmlToJson.toJson();;

        Log.d("XML", xml);
        assert jsonObj != null;
        Log.d("JSON", jsonObj.toString());

        List<NewsData> NewsDataList = new ArrayList<>();

        try {
            JSONObject rssobj = jsonObj.getJSONObject("rss");
            JSONObject channelobj = rssobj.getJSONObject("channel");
            JSONArray itemarray = channelobj.getJSONArray("item");

            for (int i = 0; i < itemarray.length(); i++) {

                JSONObject obj = itemarray.getJSONObject(i);

                NewsData newsData = new NewsData();
                newsData.setTitle(obj.has("title") ? obj.getString("title") : "");
                newsData.setLink(obj.has("link") ? obj.getString("link") : "");
                newsData.setPubDate(obj.has("pubDate") ? obj.getString("pubDate") : "");

                if (obj.has("source")) {
                    JSONObject sourceobj = obj.getJSONObject("source");
                    newsData.setSource(sourceobj.has("content") ? sourceobj.getString("content") : "");
                }

                if (obj.has("media:content")) {
                    JSONObject mediacontentobj = obj.getJSONObject("media:content");
                    newsData.setMediacontent(mediacontentobj.has("url") ? mediacontentobj.getString("url") : "");
                }

                newsData.setTimestemp(!newsData.getPubDate().equals("") ? Tools.convertDateToTimestap(newsData.getPubDate()) : 0);
                NewsDataList.add(newsData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(NewsDataList, new Comparator<NewsData>() {
            public int compare(NewsData obj1, NewsData obj2) {
                // ## Ascending order
                if (obj1.getTimestemp() < obj2.getTimestemp()) {
                    return 1;
                }
                if (obj1.getTimestemp() > obj2.getTimestemp()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        Log.d("News Data Size", "" + NewsDataList.size());
        return NewsDataList;
    }

    public List<NewsData> XmlResponceParse(String xml) {

        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();

        JSONObject jsonObj = xmlToJson.toJson();
        ;

        Log.d("XML", xml);
        Log.d("JSON", jsonObj.toString());


        XMLParser parser = new XMLParser();
        //String xml = parser.getXmlFromUrl(URL); //

        List<NewsData> NewsDataList = new ArrayList<>();

        // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_ITEM);
        // looping through all item nodes <item>
        for (int i = 0; i < nl.getLength(); i++) {

            //Element e = (Element) nl.item(i);
            NewsData newsData = new NewsData();

            Node item = nl.item(i);
            NodeList properties = item.getChildNodes();
            for (int j = 0; j < properties.getLength(); j++) {
                Node property = properties.item(j);
                String name = property.getNodeName();
                if (name.equalsIgnoreCase(KEY_title)) {
                    newsData.setTitle(property.getFirstChild().getNodeValue());
                } else if (name.equalsIgnoreCase(KEY_link)) {
                    newsData.setLink(property.getFirstChild().getNodeValue());
                } else if (name.equalsIgnoreCase(KEY_pubDate)) {
                    newsData.setPubDate(property.getFirstChild().getNodeValue());
                } else if (name.equalsIgnoreCase(KEY_source)) {
                    newsData.setPubDate(property.getFirstChild().getNodeValue());
                }
            }

            NewsDataList.add(newsData);
        }

        Log.d("News Data Size", "" + NewsDataList.size());
        return NewsDataList;
    }
}
