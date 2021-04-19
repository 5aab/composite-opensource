package com.example.spring.boot.composite;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestClassMapper {

    public static void main(String[] args)
    {
        //String json = "{\"id\":1,\"name\":\"Lokesh Gupta\",\"age\":34,\"location\":\"India\"}";

        HashMap<String, Object> map = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            //Convert Map to JSON
            Set ma = mapper.readValue(json, new TypeReference<Set<Map<String, Object>>>(){});

            //Print JSON output
            System.out.println(ma);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String json = "[{\n" +
            "        \"id\": 1,\n" +
            "        \"type\": \"Car Insurance\",\n" +
            "        \"insuredBy\": \"LIC\",\n" +
            "        \"insuredOn\": [2020, 1, 1],\n" +
            "        \"formattedDate\": null,\n" +
            "        \"startsOn\": \"20200101\",\n" +
            "        \"coverPlasticItems\": \"yes\",\n" +
            "        \"coverGlassItems\": \"no\",\n" +
            "        \"coverInteriorItems\": \"yes\"\n" +
            "    }, {\n" +
            "        \"id\": 2,\n" +
            "        \"type\": \"Car Insurance\",\n" +
            "        \"insuredBy\": \"BAJAJ ALLIANZ\",\n" +
            "        \"insuredOn\": [2020, 1, 1],\n" +
            "        \"formattedDate\": null,\n" +
            "        \"startsOn\": \"20200101\",\n" +
            "        \"coverPlasticItems\": \"yes\",\n" +
            "        \"coverGlassItems\": \"no\",\n" +
            "        \"coverInteriorItems\": \"yes\"\n" +
            "    }, {\n" +
            "        \"id\": 3,\n" +
            "        \"type\": \"Car Insurance\",\n" +
            "        \"insuredBy\": \"RELIANCE\",\n" +
            "        \"insuredOn\": [2020, 1, 1],\n" +
            "        \"formattedDate\": null,\n" +
            "        \"startsOn\": \"20200101\",\n" +
            "        \"coverPlasticItems\": \"yes\",\n" +
            "        \"coverGlassItems\": \"no\",\n" +
            "        \"coverInteriorItems\": \"yes\"\n" +
            "    }\n" +
            "]";
}
