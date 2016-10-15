package jonatan.stryktipset.statistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import net.minidev.json.JSONArray;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phasip on 10/9/16.
 */
public class StatisticsUpdater {
    public static String HISTORYFILE = "main/resources/history.json";

    public static String buildJson(HashMap<String, String> map) {
        StringBuilder mainList = new StringBuilder();
        StringBuilder resultList = new StringBuilder();
        mainList.append("\"data\": [");
        resultList.append("\"result\": [");

        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey().contains("result"))
            {
                resultList.append(e.getValue());
                resultList.append(",");
            } else {
                mainList.append(e.getValue());
                mainList.append(",");
            }
        }

        resultList.setLength(resultList.length()-1);
        mainList.setLength(mainList.length()-1);
        String all = "{" + mainList + "]," + resultList + "]}";
        return all;
    }
    public static void main(String[] args) throws IOException {
        // Could use http://www.jsonschema2pojo.org/ to build actual java objects first.

        //https://api.www.svenskaspel.se/draw/stryktipset/draws/4267/result
        //https://api.www.svenskaspel.se/draw/stryktipset/draws/4267
        HashMap<String, String> urlToDatamap = null;
        try {
            urlToDatamap = loadData();
        } catch (FileNotFoundException e) {
            System.out.println("Could not load previous data");
            urlToDatamap = new HashMap<>();
        }

        updateMap(urlToDatamap);
        storeData(urlToDatamap);

        System.out.println("Data loaded, please wait!");


        List<Map<String, Object>>  oddsAndRet = extractOddsAndResults(urlToDatamap);
        for (Map<String, Object> stringObjectMap : oddsAndRet) {
            System.out.println(stringObjectMap);
        }
    }

    private static List<Map<String, Object>> extractOddsAndResults(HashMap<String, String> urlToDatamap) {
        ReadContext ctx = JsonPath.parse(buildJson(urlToDatamap));
        List<Map<String,Object>> oddsAndResult = new ArrayList<>();
        List<Map<String,Object>> matchodds = ctx.read("$.data[*].draw.drawEvents[*].['svenskaFolket', 'tioTidningar', 'odds','match']");
        // The acutal outcome is stored separately, didn't manage to get that at the same time
        for (Map<String,Object> t: matchodds) {
            int mid = (Integer)(((Map<String,Object>)t.get("match")).get("matchId"));
            t.put("matchId",mid);
            t.remove("match");
            JSONArray outcome = ctx.read("$.result[*].result.events[?(@.matchId == " + mid + ")].outcome");
            if (outcome.size() == 0)
                break;
            t.put("outcome",outcome.get(0));
            oddsAndResult.add(t);
        }
        return oddsAndResult;
    }

    private static void updateMap(HashMap<String, String> urlToDatamap) throws IOException {
        for (int i = 4267; i < 10000; i++) {
            System.out.println("Loading " + i);
            String u1 = "https://api.www.svenskaspel.se/draw/stryktipset/draws/" + i + "/";
            String u2 = u1 + "result";

            if (urlToDatamap.containsKey(u2) && urlToDatamap.get(u2) != null)
                continue;

            if ((i+1)%10 == 0)
                storeData(urlToDatamap);

            try {
                sleep(1);
                urlToDatamap.put(u1, getData(u1));
                sleep(1);
                urlToDatamap.put(u2, getData(u2));
            } catch (FileNotFoundException e) {
                System.out.println("Finished loading data up to " + i);
                break;
            }
        }
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void storeData(HashMap<String, String> urlToDatamap) throws IOException {
        System.out.println("Storing data!");
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(urlToDatamap);
        File outfile = new File(HISTORYFILE);
        outfile.createNewFile(); // if file already exists will do nothing
        FileOutputStream out = new FileOutputStream(outfile);
        out.write(json.getBytes("UTF-8"));
        out.close();
    }
    private static HashMap<String, String> loadData() throws FileNotFoundException, UnsupportedEncodingException {
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
        Gson gson = new GsonBuilder().create();

        FileInputStream fi = new FileInputStream(HISTORYFILE);
        InputStreamReader is = new InputStreamReader(fi, "UTF-8");
        return gson.fromJson(is,typeOfHashMap);
    }

    public static String getData(String url) throws IOException {
        URL u = new URL(url);
        return IOUtils.toString(u, (Charset)null);
    }

}
