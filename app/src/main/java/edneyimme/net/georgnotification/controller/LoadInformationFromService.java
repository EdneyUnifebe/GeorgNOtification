package edneyimme.net.georgnotification.controller;

import java.util.ArrayList;

import edneyimme.net.myapplication.dao.Users;

public class LoadInformationFromService {
    ArrayList<Users> usersList = new ArrayList<Users>();

    public ArrayList<Users> getUserInformationFromService() {

        for (int indice = 0; indice < 10; indice++) {
            Users u = new Users("" + indice, "Nome " + indice);
            usersList.add(u);
        }
        return usersList;
    }

/*

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";


    public JSONObject getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }


        try {
            jObj = new JSONObject(json);
            Log.i("Json", "Linha 76" + jObj.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
*/

}
