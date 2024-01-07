package ch.heig.dai.lab.http;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;


import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Unit test for when the server is on :-)
 */
public class APITest
{

    private String url = "http://localhost/api";

    private String endpoint = "memes";


    /**
     * Rigorous Test :-)
     */
    @Test
    public void getAllMemesTest() {
        HttpResponse<JsonNode> httpResponse = null;

        try {
            httpResponse = Unirest.get(url + '/' + endpoint).asJson();
        } catch (UnirestException e) {
            System.out.println("Exception: " + e);
            assert(false);
        }

        System.out.println("status: " + httpResponse.getStatus());

        // Prettifying
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(httpResponse.getBody().toString());
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);

        assertNotNull(httpResponse.getBody());
        assertEquals(200, httpResponse.getStatus());
    }

    @Test
    public void getOneMemeTest() {
        HttpResponse<JsonNode> httpResponse = null;
        try {
            httpResponse = Unirest.get(url + '/' + endpoint + "/2").asJson();
        } catch (UnirestException e) {
            System.out.println("Exception: " + e);
            assert(false);
        }

        System.out.println("status: " + httpResponse.getStatus());

        // Prettifying
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(httpResponse.getBody().toString());
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);

        assertNotNull(httpResponse.getBody());
        assertEquals(200, httpResponse.getStatus());
    }



    @Test
    public void createMemeTest() {

        Unirest.setObjectMapper(new ObjectMapper() {
            com.fasterxml.jackson.databind.ObjectMapper mapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return mapper.readValue(value, valueType);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        HttpResponse<String> httpResponse = null;

        Meme meme =
                new Meme("Exit vim", "wait you guys actually can't exit vim? I thought it was a joke",
                        "https://www.reddit.com/media?url=https%3A%2F%2Fpreview.redd.it%2Fithoughtitwasajoke-v0-e0jit5zpuu9c1.jpeg%3Fauto%3Dwebp%26s%3Dfcdc4ffb7caacf65c3fb237610a3e2161ea4e2cb");

        try {
            httpResponse = Unirest.post(url + '/' + endpoint)
                    .body(meme).asString();
        } catch (UnirestException e) {
            System.out.println("Exception: " + e);
            assert(false);
        }

        assertEquals(201, httpResponse.getStatus());
    }


    @Test
    public void updateMemeTest() {

        Unirest.setObjectMapper(new ObjectMapper() {
            com.fasterxml.jackson.databind.ObjectMapper mapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return mapper.readValue(value, valueType);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        HttpResponse<String> httpResponse = null;

        Meme meme =
                new Meme("Another title", "wait you guys actually can't exit vim? I thought it was a joke",
                        "https://www.reddit.com/media?url=https%3A%2F%2Fpreview.redd.it%2Fithoughtitwasajoke-v0-e0jit5zpuu9c1.jpeg%3Fauto%3Dwebp%26s%3Dfcdc4ffb7caacf65c3fb237610a3e2161ea4e2cb");

        try {
            httpResponse = Unirest.put(url + '/' + endpoint + "/2")
                    .body(meme).asString();
        } catch (UnirestException e) {
            System.out.println("Exception: " + e);
            assert(false);
        }

        assertEquals(202, httpResponse.getStatus());
    }

    @Test
    public void deleteMemeTest() {
        HttpResponse<String> httpResponse = null;

        try {
            httpResponse = Unirest.delete(url + '/' + endpoint + "/2").asString();
        } catch (UnirestException e) {
            System.out.println("Exception: " + e);
            assert(false);
        }

        assertEquals(204, httpResponse.getStatus());
    }



}
