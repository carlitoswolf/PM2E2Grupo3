package com.example.ecamen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecamen.Config.Datos;
import com.example.ecamen.Config.RestApiMethods;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private CustomView customView;
    private Button saveButton;
    private Button clearButton, btnList;

    // Envio de datos
    String rutaImg, pattern = "^[A-Za-z\\s]+$";
    private RequestQueue requestQueue;
    private Button btnSalvarContacto;

    //Mapa
    EditText txtLatitud, txtLongitud, txtNombre, txtTelefono;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firma Canvas
        customView = findViewById(R.id.custom_view);

        //Buttons
        saveButton = findViewById(R.id.save_button);
        clearButton = findViewById(R.id.clear_button);
        btnSalvarContacto = findViewById(R.id.btnSalvarContacto);
        btnList = findViewById(R.id.btnTodosContactos);

        //EditText
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        txtNombre =  findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);


        //Firma Canvas
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSignature();
            }
        });
        //Firma Canvas
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.clear();
            }
        });

        //Mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fMap);
        mapFragment.getMapAsync(this);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListaActivity.class);
                startActivity(intent);
            }
        });


        btnSalvarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    enviarDatos();
                    Toast.makeText(getApplicationContext(), "Registro Exitoso", Toast.LENGTH_LONG).show();
                    clear();
                }

            }
        });
    }

    //Firma Canvas
    private void saveSignature() {
        Bitmap signatureBitmap = Bitmap.createBitmap(customView.getWidth(), customView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(signatureBitmap);
        customView.draw(canvas);

        try {
            File file = new File(getFilesDir(), "signature.png");
            FileOutputStream fos = new FileOutputStream(file);
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(this, "Signature saved", Toast.LENGTH_SHORT).show();
            rutaImg = file.getAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show();
        }
    }

    //Mapa
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng honduras = new LatLng(14.083912, -87.2461723);
        mMap.addMarker(new MarkerOptions().position(honduras).title("Honduras"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(honduras));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

        mMap.clear();
        LatLng honduras = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(honduras).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(honduras));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

        mMap.clear();
        LatLng honduras = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(honduras).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(honduras));
    }

    // Envio de datos
    private void enviarDatos() {
        requestQueue = Volley.newRequestQueue(this);
        Pattern regexPattern = Pattern.compile(pattern);
        String userInput = txtNombre.getText().toString();

        Matcher matcher = regexPattern.matcher(userInput);


        Datos datos = new Datos();
        datos.setNombres(userInput);
        datos.setTelefono(txtTelefono.getText().toString());
        datos.setLatitud(txtLatitud.getText().toString());
        datos.setLongitud(txtLongitud.getText().toString());
        datos.setFirma(ConvertImage64(rutaImg));

        JSONObject jsondatos = new JSONObject();

        try {
            jsondatos.put("nombres", datos.getNombres());
            jsondatos.put("telefono", datos.getTelefono());
            jsondatos.put("latitud", datos.getLatitud());
            jsondatos.put("longitud", datos.getLongitud());
            jsondatos.put("firma", datos.getFirma());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RestApiMethods.ApiPost, jsondatos, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String mensaje = response.getString("message");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    private String ConvertImage64(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagearray, Base64.DEFAULT);
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().toString().trim();
        String numero = txtTelefono.getText().toString().trim();
        String latitud = txtLatitud.getText().toString().trim();
        String longitud = txtLongitud.getText().toString().trim();



        if (nombre.isEmpty()) {
            txtNombre.setError("Campo obligatorio");
            return false;
        }

        if (numero.isEmpty()) {
            txtTelefono.setError("Campo obligatorio");
            return false;
        }

        if (latitud.isEmpty()) {
            txtLatitud.setError("Campo obligatorio");
            return false;
        }

        if (longitud.isEmpty()) {
            txtLongitud.setError("Campo obligatorio");
            return false;
        }

        return true;
    }


    private void clear() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtLongitud.setText("");
        txtLatitud.setText("");
        customView.clear();
    }
}