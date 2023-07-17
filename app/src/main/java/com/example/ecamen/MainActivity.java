package com.example.ecamen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.ecamen.config.Personas;
import com.example.ecamen.config.RestApiMethods;
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
    private Button clearButton;

    private Button Guardar, Contactos;

    private File file;

    private String redirect, pattern = "^[A-Za-z\\s]+$";

    private RequestQueue requestQeue;

    //Mapa
    EditText txtLatitud, txtLongitud, nombres, telefono;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Firma Canvas
        customView = findViewById(R.id.custom_view);
        saveButton = findViewById(R.id.save_button);
        clearButton = findViewById(R.id.clear_button);

        //Mapa
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        //Nombres y Telefono
        nombres = findViewById(R.id.txtNombre);
        telefono = findViewById(R.id.txtTelefono);

        //Buttons
        Guardar =  findViewById(R.id.btnSalvarContacto);
        Contactos =  findViewById(R.id.btnTodosContactos);

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

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    sendData();
                    Toast.makeText(getApplicationContext(), "Registro Exitoso", Toast.LENGTH_LONG).show();
                    clear();
                }
            }
        });

        //Mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fMap);
        mapFragment.getMapAsync(this);


    }


    //Firma Canvas
    private void saveSignature() {
        Bitmap signatureBitmap = Bitmap.createBitmap(customView.getWidth(), customView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(signatureBitmap);
        customView.draw(canvas);

        try {
            file = new File(getFilesDir(), "signature.png");
            FileOutputStream fos = new FileOutputStream(file);
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            redirect = file.getAbsolutePath();
            fos.flush();
            fos.close();

            Toast.makeText(this, "Signature saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData()
    {
        requestQeue = Volley.newRequestQueue(this);

        Pattern regexPattern = Pattern.compile(pattern);
        String userInput = nombres.getText().toString();

        Matcher matcher = regexPattern.matcher(userInput);

        Personas people = new Personas();
        people.setNombres(userInput);
        people.setTelefono(telefono.getText().toString());
        people.setLatitud(txtLatitud.getText().toString());
        people.setLongitud(txtLongitud.getText().toString());
        people.setFirma(ConvertImage64(redirect));

        JSONObject jsonPeople = new JSONObject();

        try
        {
            jsonPeople.put("nombres", people.getNombres());
            jsonPeople.put("telefono", people.getTelefono());
            jsonPeople.put("latitud", people.getLatitud());
            jsonPeople.put("longitud", people.getLongitud());
            jsonPeople.put("firma", people.getFirma());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        JsonObjectRequest resquest = new JsonObjectRequest(Request.Method.POST,
                RestApiMethods.ApiPost,
                jsonPeople, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
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

        requestQeue.add(resquest);
    }


    private String ConvertImage64(String path)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagearray, Base64.DEFAULT);
    }

    //Mapa
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng honduras = new LatLng(14.083912,-87.2461723);
        mMap.addMarker(new MarkerOptions().position(honduras).title("Honduras"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(honduras));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

        mMap.clear();
        LatLng honduras = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(honduras).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(honduras));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

        mMap.clear();
        LatLng honduras = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(honduras).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(honduras));
    }

    private boolean validarCampos() {
        String nombre = nombres.getText().toString().trim();
        String numero = telefono.getText().toString().trim();
        String latitud = txtLatitud.getText().toString().trim();
        String longitud = txtLongitud.getText().toString().trim();



        if (nombre.isEmpty()) {
            nombres.setError("Campo obligatorio");
            return false;
        }

        if (numero.isEmpty()) {
            telefono.setError("Campo obligatorio");
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
        nombres.setText("");
        telefono.setText("");
        txtLongitud.setText("");
        txtLatitud.setText("");
        customView.clear();
    }

}