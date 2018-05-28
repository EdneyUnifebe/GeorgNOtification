package edneyimme.net.georgnotification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edneyimme.net.georgnotification.controller.UpdateInformation;
import edneyimme.net.georgnotification.controller.UploadArquivo;
import edneyimme.net.georgnotification.dao.Users;

public class UploadAprovarImagem extends Activity {

    public static final String USER_DAO = "USER_DAO";
    ImageButton imagemCamera;
    ImageButton imagemAprovar;
    ImageButton imagemRecusar;
    ImageView imagemFoto;
    Bitmap imageBitmap;
    File file;
    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    Users users;
    private String fileName ="";
    public  ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_imagem);

        this.fileName = String.valueOf(System.currentTimeMillis());
        users = (Users) getIntent().getExtras().get(UploadAprovarImagem.USER_DAO);
        imagemCamera = (ImageButton) findViewById(R.id.imagemCamera);
        imagemCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamera();
            }
        });

        TextView nomeUsuario = findViewById(R.id.txNameUsuario);
        String name=getString(R.string.nameUsuario)+": "+users.getNome();
        nomeUsuario.setText(name);
        imagemAprovar = (ImageButton) findViewById(R.id.imageAprovar);
        imagemAprovar.setVisibility(View.INVISIBLE);
        imagemAprovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aprovarImagem();
            }
        });

        imagemRecusar = (ImageButton) findViewById(R.id.imageReprovar);
        imagemRecusar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageReprovar();
            }
        });
        imagemRecusar.setVisibility(View.INVISIBLE);
        imagemFoto = (ImageView) findViewById(R.id.imagemFoto);

    }

    private void imageReprovar() {

        finish();
    }

    private void aprovarImagem() {

        uploadArquivoTask u = new uploadArquivoTask();
        u.execute("");
        finish();

    }
    int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private void abrirCamera() {
        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(UploadAprovarImagem.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {


                file = new File(Environment.getExternalStorageDirectory() + "/"+this.fileName+".png");
                Uri outputFileUri = Uri.fromFile(file);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        }
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(UploadAprovarImagem.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    //Bitmap imageBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +  "/"+fileName+".png", options);
                    Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    boolean validaCompressao = imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                    byte[] fotoBinario = outputStream.toByteArray();
                    String encodedImage = Base64.encodeToString(fotoBinario, Base64.DEFAULT);
                    imagemFoto.setImageBitmap(imageBitmap);
                    imagemRecusar.setVisibility(View.VISIBLE);
                    imagemAprovar.setVisibility(View.VISIBLE);
                    boolean isImageTaken = true;
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao salvar imagem="+e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operação cancelada ", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Operação cancelada ", Toast.LENGTH_SHORT);
            }
        }
    }


/*
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d("UploadAprovarImage", "Can't create directory to save image.");
            Toast.makeText(UploadAprovarImagem.this, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(UploadAprovarImagem.this, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d("UploadAprovarImagem", "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(UploadAprovarImagem.this, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }

*/

    class uploadArquivoTask extends AsyncTask<String, String, Void> {

        public ProgressDialog progressDialog = new ProgressDialog(UploadAprovarImagem.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Enviando arquivo, aguarde...");
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
            Log.i("UploadAprovarImagem", "Path = "+ file.getAbsolutePath());
            UploadArquivo up = new UploadArquivo();
            up.uploadFile(file.getAbsolutePath());

            UpdateInformation ui = new UpdateInformation();
            ui.setContext(UploadAprovarImagem.this);
            ui.setUsers(users);
            ui.alterarStatus(users);
            return null;
        }

        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
        }
    }

}
