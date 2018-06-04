package edneyimme.net.georgnotification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
    private ImageButton imagemCamera;
    private ImageButton imagemAprovar;
    private ImageButton imagemRecusar;
    private ImageView imagemFoto;
    private Bitmap imageBitmap;
    private File file;
    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private Users users;
    private String fileName = "";
    int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

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
        String name = getString(R.string.nameUsuario) + users.getNome();
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
        Intent paginaInicial = new Intent(UploadAprovarImagem.this, PaginaInicial.class);
        startActivity(paginaInicial);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void aprovarImagem() {
        uploadArquivoTask u = new uploadArquivoTask(UploadAprovarImagem.this);
        u.execute("");
        chamarPaginaInicial();
    }

    private void chamarPaginaInicial() {
        Intent paginaInicial = new Intent(UploadAprovarImagem.this, PaginaInicial.class);
        startActivity(paginaInicial);
        finish();

    }

    private void abrirCamera() {
        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(UploadAprovarImagem.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                file = new File(Environment.getExternalStorageDirectory() + "/" + this.fileName + ".png");
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
                    Toast.makeText(this, "Erro ao salvar imagem=" + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operação cancelada ", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Operação cancelada ", Toast.LENGTH_SHORT);
            }
        }
    }


    class uploadArquivoTask extends AsyncTask<String, String, Void> {
        private Context context;
        ProgressDialog progressDialog;
        InputStream inputStream = null;
        String result = "";

        public uploadArquivoTask(UploadAprovarImagem uploadAprovarImagem) {
            this.context = uploadAprovarImagem;
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UploadAprovarImagem.this);
            progressDialog.setMessage("Enviando arquivo.");
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

            if (users.getFileType() ==2 ){
                converterImagemPDF();
                Log.i("UploadAprovarImagem", "image="+fileName);
                users.setFileName(fileName + ".pdf");
            }else {
                UploadArquivo up = new UploadArquivo();
                up.uploadFile(file.getAbsolutePath());
                users.setFileName(fileName + ".png");
            }

            UpdateInformation ui = new UpdateInformation();
            ui.setContext(UploadAprovarImagem.this);
            ui.setUsers(users);
            ui.alterarStatus(users, UploadAprovarImagem.this);

            return null;
        }

        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
        }
    }

    private void converterImagemPDF() {
        createPdf();
    }

    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);



        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = Environment.getExternalStorageDirectory()+ "/"+fileName+".pdf";
        Log.i("UploadAprovarImagem", "Endereco="+targetPdf);

        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("UploadAprovarImagem", "Endereço "+ filePath.getAbsolutePath());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }


}
