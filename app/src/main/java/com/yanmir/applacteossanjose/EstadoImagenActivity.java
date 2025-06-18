package com.yanmir.applacteossanjose;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EstadoImagenActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView imgSeleccionada;
    private TextView txtResultadoImagen;
    private Uri imagenUri;
    private Button btnAnalizar, btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_imagen);

        imgSeleccionada = findViewById(R.id.imgSeleccionada);
        txtResultadoImagen = findViewById(R.id.txtResultadoImagen);
        btnAnalizar = findViewById(R.id.btnAnalizar);
        btnContinuar = findViewById(R.id.btnContinuar);

        findViewById(R.id.btnSeleccionarImagen).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnAnalizar.setOnClickListener(v -> {
            if (imagenUri != null) {
                analizarImagen(imagenUri);
            } else {
                Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });

        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EstimacionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.getData();
            imgSeleccionada.setImageURI(imagenUri);
        }
    }

    private void analizarImagen(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imagenBytes = baos.toByteArray();

            Map<String, MultipartRequest.DataPart> byteData = new HashMap<>();
            byteData.put("imagen", new MultipartRequest.DataPart("foto.jpg", imagenBytes, "image/jpeg"));

            MultipartRequest request = new MultipartRequest(
                    com.android.volley.Request.Method.POST,
                    "http://10.0.2.2:5000/analizar_imagen",
                    response -> runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            String estado = json.getString("estado");
                            double confianza = json.getDouble("confianza");
                            String texto = "Estado: " + estado + "\nConfianza: " + String.format("%.2f", confianza * 100) + "%";
                            txtResultadoImagen.setText(texto);
                        } catch (Exception e) {
                            txtResultadoImagen.setText("Respuesta inesperada");
                        }
                    }),
                    error -> runOnUiThread(() -> txtResultadoImagen.setText("Error al analizar imagen")),
                    null, // headers si no tienes
                    byteData
            );

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (IOException e) {
            txtResultadoImagen.setText("Error al procesar imagen");
        }
    }
}