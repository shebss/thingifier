package uk.co.compendiumdev.casestudy.todomanager.http_api;

import java.util.HashMap;
import java.util.Map;

public class HeadersSupport {

    static Map<String, String> acceptXml(){

        Map<String, String> acceptXml;
        acceptXml = new HashMap<String, String>();
        acceptXml.put("Accept", "application/xml");
        return acceptXml;

    }

    static Map<String, String> containsXml(){

        Map<String, String> containsXml;
        containsXml = new HashMap<String, String>();
        containsXml.put("Content-Type", "application/xml");
        return containsXml;

    }


    static Map<String, String> acceptJson(){

        Map<String, String> acceptJson;
        acceptJson = new HashMap<String, String>();
        acceptJson.put("Accept", "application/json");
        return acceptJson;

    }

    static Map<String, String> containsJson(){

        Map<String, String> containsJson;
        containsJson = new HashMap<String, String>();
        containsJson.put("Content-Type", "application/json");
        return containsJson;

    }


}
