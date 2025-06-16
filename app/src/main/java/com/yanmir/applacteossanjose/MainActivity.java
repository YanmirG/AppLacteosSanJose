package com.yanmir.applacteossanjose;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProductoAdapter adapter;

    // Lista pública para acceder desde otras actividades si en caso es necesario
    public static ArrayList<Producto> listaProductos = new ArrayList<>();

    // URL del endpoint Flask local
    private static final String URL_API = "http://10.0.2.2:5000/productos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listaProductos);
        adapter = new ProductoAdapter(this, listaProductos);
        listView.setAdapter(adapter);

        // Al hacer clic en un producto, abre la actividad de estimación
        ListView listView = findViewById(R.id.listaProductos);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, EstadoImagenActivity.class);
            // Puedes pasar datos con putExtra si lo necesitas
            startActivity(intent);
        });

        // Cargar productos desde la API
        cargarDatos();
    }

    private void cargarDatos() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        listaProductos.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Producto producto = new Producto(
                                    obj.getInt("id_producto"),
                                    obj.getString("tipo_producto"),
                                    obj.getString("fecha_produccion"),
                                    obj.getString("lote")
                            );
                            listaProductos.add(producto);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Puedes mostrar un mensaje al usuario si deseas
                }
        );

        queue.add(request);
    }
}
