package webfirmam.app.osmanozetwebapp;


import android.os.AsyncTask;

import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class DownloadData extends AsyncTask<String, Void, String> {
    String finalDate;

    @Override
    protected String doInBackground(String... strings) {

        String result = "";
        URL url;
        HttpURLConnection httpURLConnection;

        try {

            url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while (data > 0) {

                char character = (char) data;
                result += character;

                data = inputStreamReader.read();

            }


            return result;

        } catch (Exception e) {
            //hata varsa null yap
            return null;
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        System.out.println("alınan data:" + s);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String CurrentDate = df.format(Calendar.getInstance().getTime());
        try {

            JSONObject parentObject  = new JSONObject(s);
            JSONArray parentArray = parentObject.getJSONArray("postdates");
            JSONObject finalObject = parentArray.getJSONObject(0);
            finalDate = finalObject.getString("post_date");
            System.out.println("dates :"+finalDate);

        } catch (Exception e) {
            System.out.println("Something Wrong Post Execute");
        }

        Boolean compareDates = CompareString(CurrentDate,finalDate);
        //System.out.println("compare :"+CompareString(CurrentDate,finalDate));
        if (compareDates){
            System.out.println("compare"+compareDates);
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    try {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'İçerik Güncellendi'}, 'include_player_ids': ['" + userId + "']}"), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }





    }




    public Boolean CompareString(String currentDate , String databaseDate){
        String[] splitDatabaseArray =databaseDate.split(" ");
        //we dont use it tarihler aynı saatin saniye kısmını atcam
        String dateDatabaseIndex = splitDatabaseArray[0];
        String clockDatabaseIndex = splitDatabaseArray[1];
        String[] clockDatabaseHourIndex = clockDatabaseIndex.split(":");
        String clockDatabaseHour = clockDatabaseHourIndex[0];
        String clockDatabaseMin = clockDatabaseHourIndex[1];
        String clockCombineTime = clockDatabaseHour+":"+clockDatabaseMin;
        String dateClockCombineDate = dateDatabaseIndex+" "+clockCombineTime;
        Boolean check = dateClockCombineDate.equals(currentDate);
        //System.out.println("eşit mi :"+check);
        System.out.println("senin saatin :"+currentDate);
        return check;
    }
}