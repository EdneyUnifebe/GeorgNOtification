package edneyimme.net.georgnotification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import edneyimme.net.myapplication.adapter.listUsersAdapter;
import edneyimme.net.myapplication.dao.Users;

public class PaginaInicial extends Activity {

    ListView listViewUsers;
    private static String URL_USER_DATA="http://www.apsesis.com.br/webservices/listausuarios.php";

    private ArrayList<Users> listaDeUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_inicial);
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chamarTelaDetalheUsuario(i);
            }
        });
        carrecarListaServidor();
        listUsersAdapter listAdapter = new listUsersAdapter(PaginaInicial.this, this.listaDeUsuarios);
        listViewUsers.setAdapter(listAdapter);
    }

    /**
     * Chamar tela detalhe de usuario para fazer upload de imagens
     * @param i
     */
    private void chamarTelaDetalheUsuario(int i) {
        Intent intent = new Intent(PaginaInicial.this, UploadAprovarImagem.class);
        startActivity(intent);
    }

    /**
     * Carregar fontes de informacao
     */
    public void carrecarListaServidor(){
        downloadInformationTask d = new downloadInformationTask();
        d.execute("");
    }

    class downloadInformationTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(PaginaInicial.this);
        InputStream inputStream = null;
        String result = "";


        protected void onPreExecute() {
            progressDialog.setMessage("Atualizando informações...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    //TODO DESENVOLVER METODO ONCANCEL
                    //MyAsyncTask.this.cancel(true);
                }
            });
        }


        @Override
        protected Void doInBackground(String... strings) {

            String url_select = URL_USER_DATA;

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();


                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncoding", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }

            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            try {
                JSONArray jArray = new JSONArray(result);
                for(int i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    String nomeUsuario = jObject.getString("pessoa_nome");
                    String idUsuario = jObject.getString("captura_id");

                    Users users = new Users(idUsuario, nomeUsuario);
                    listaDeUsuarios.add(users);

                }
                this.progressDialog.dismiss();
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
        }
    }
}
