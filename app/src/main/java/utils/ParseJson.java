package utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 德帅 on 2016/8/5.
 * action:
 */
public class ParseJson {
    public static String parseWeatherWithJSON(String response) {

        String results ="";
        try {
            JSONObject jsonObject = new JSONObject(response);
            String resultcode = jsonObject.getString("resultcode");
            if (resultcode.equals("200")) {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                JSONObject todayObject = resultObject.getJSONObject("today");
                String temperature = todayObject.getString("temperature");//温度
                String weather = todayObject.getString("weather");//天气
                JSONObject weatherObject=todayObject.getJSONObject("weather_id");
                String fa = weatherObject.getString("fa");//天气标示a
                String fb = weatherObject.getString("fb");//天气标示b

                results+=weather;
                results+="!"+temperature;
                results+="!"+fa;
                results+="!"+fb;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }
}
