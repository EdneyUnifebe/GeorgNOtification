package edneyimme.net.georgnotification;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edneyimme.net.georgnotification.adapter.listUsersAdapter;
import edneyimme.net.georgnotification.dao.Users;


public class PaginaInicial extends Activity {

    ListView listViewUsers;
    private static String URL_USER_DATA = "http://www.apsesis.com.br/webservices/listausuarios.php";
    listUsersAdapter listAdapter;
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
        listAdapter = new listUsersAdapter(PaginaInicial.this, this.listaDeUsuarios);
        listViewUsers.setAdapter(listAdapter);

    }

    /**
     * Chamar tela detalhe de usuario para fazer upload de imagens
     *
     * @param i
     */
    private void chamarTelaDetalheUsuario(int i) {
        Intent intent = new Intent(PaginaInicial.this, UploadAprovarImagem.class);
        intent.putExtra(UploadAprovarImagem.USER_DAO, listaDeUsuarios.get(i));
        startActivity(intent);
    }

    /**
     * Carregar fontes de informacao
     */
    public void carrecarListaServidor() {
        listaDeUsuarios = new ArrayList<Users>();

        /*
        LoadingInformation l = new LoadingInformation(PaginaInicial.this);
        l.execute();
        */

    }

    @Override
    protected void onStart() {
        super.onStart();
        getServerData();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getServerData();
        listAdapter.notifyDataSetChanged();

    }

    private void getServerData() {
        String urlGetServerData = URL_USER_DATA;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGetServerData, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("JsonResponse", "Array " + response.getJSONArray("capturas"));
                            JSONArray array = response.getJSONArray("capturas");
                            for (int p = 0; p < array.length(); p++) {
                                JSONObject jsonObject = null;

                                jsonObject = array.getJSONObject(p);

                                String nomeUsuario = jsonObject.getString("pessoa_nome");
                                String idUsuario = jsonObject.getString("captura_id");
                                String arquivo_nome = jsonObject.getString("arquivo_nome");
                                int arquivo_tipo = jsonObject.getInt("arquivo_tipo");
                                Users users = new Users(idUsuario, nomeUsuario, arquivo_nome, arquivo_tipo);
                                listaDeUsuarios.add(users);
                            }
                            listAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Exception", "Error=" + error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private class LoadingInformation extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public LoadingInformation(PaginaInicial activity) {
            dialog = new ProgressDialog(PaginaInicial.this);
        }

        @Override
        protected void onPreExecute() {
/*
//            dialog.setMessage("Aguarde.");
//            dialog.show();
*/
        }

        protected Void doInBackground(Void... args) {
            getServerData();
            return null;
        }

        protected void onPostExecute(Void result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
