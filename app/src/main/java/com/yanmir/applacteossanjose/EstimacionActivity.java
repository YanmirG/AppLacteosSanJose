package com.yanmir.applacteossanjose;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EstimacionActivity extends AppCompatActivity {

    EditText edtTemperatura, edtHumedad, edtPh;
    Button btnSeleccionarFecha, btnEstimar;
    TextView txtFechaProduccion, txtResultado;
    String fechaProduccionSeleccionada = "--";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimacion);

        edtTemperatura = findViewById(R.id.edtTemperatura);
        edtHumedad = findViewById(R.id.edtHumedad);
        edtPh = findViewById(R.id.edtPh);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnEstimar = findViewById(R.id.btnEstimar);
        txtFechaProduccion = findViewById(R.id.txtFechaProduccion);
        txtResultado = findViewById(R.id.txtResultado);

        btnSeleccionarFecha.setOnClickListener(view -> mostrarDatePicker());
        btnEstimar.setOnClickListener(view -> enviarEstimacion());
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fechaProduccionSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
            txtFechaProduccion.setText(getString(R.string.fecha_seleccionada, fechaProduccionSeleccionada));
        }, año, mes, dia);

        datePickerDialog.show();
    }

    private void enviarEstimacion() {
        try {
            double temperatura = Double.parseDouble(edtTemperatura.getText().toString());
            double humedad = Double.parseDouble(edtHumedad.getText().toString());
            double ph = Double.parseDouble(edtPh.getText().toString());

            if (fechaProduccionSeleccionada.equals("--")) {
                Toast.makeText(this, R.string.fecha_no_seleccionada, Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("temperatura", temperatura);
            jsonBody.put("humedad", humedad);
            jsonBody.put("ph", ph);
            jsonBody.put("fecha_produccion", fechaProduccionSeleccionada);

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(crearRequest(jsonBody));

        } catch (Exception e) {
            Log.e("EstimacionActivity", "Error al preparar datos", e);
            Toast.makeText(this, R.string.error_entrada, Toast.LENGTH_SHORT).show();
        }
    }

    private JsonObjectRequest crearRequest(JSONObject jsonBody) {
        String url = "http://10.0.2.2:5000/estimacion";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    int dias = response.optInt("vida_util_dias");
                    txtResultado.setText(getString(R.string.vida_util_resultado, dias));
                },
                error -> {
                    Log.e("EstimacionActivity", "Error en la solicitud", error);
                    Toast.makeText(this, R.string.error_solicitud, Toast.LENGTH_LONG).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        return request;
    }
}
