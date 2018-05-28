package edneyimme.net.georgnotification;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadAprovarImagem extends Activity {

    ImageButton imagemCamera;
    ImageButton imagemAprovar;
    ImageButton imagemRecusar;
    ImageView imagemFoto;

    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_imagem);
        imagemCamera = (ImageButton) findViewById(R.id.imagemCamera);
        imagemCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamera();
            }
        });

        imagemAprovar = (ImageButton) findViewById(R.id.imageAprovar);
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

        imagemFoto = (ImageView) findViewById(R.id.imagemFoto);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                imagemFoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("FileNoteFoundException", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            } finally {
                if (stream != null)
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
    }

    private void imageReprovar() {
        Toast.makeText(this, "Reprovar Camera", Toast.LENGTH_SHORT).show();
    }

    private void aprovarImagem() {
        Toast.makeText(this, "Aprovar Camera", Toast.LENGTH_SHORT).show();
    }

    private void abrirCamera() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
        Toast.makeText(this, "Abrir Camera", Toast.LENGTH_SHORT).show();
    }


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

}
