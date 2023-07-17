package com.example.ecamen;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ListaActivity extends AppCompatActivity {
    private Button btnBack, btnDelete, btnUpdate;

    private ListView listContactos;
    private int valSelected=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listContactos=findViewById(R.id.listContactos);

        btnDelete=findViewById(R.id.btnDelete);
        btnUpdate=findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnAtras);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valSelected!=-1){
                    mostrarDialogoSiNo("Eliminar");
                }else{
                    Toast.makeText(getApplicationContext(), "Debe seleccionar un item", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    };



    //Alerta de confirmacion
    private void mostrarDialogoSiNo(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de " + text + "?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (text.equals("Eliminar")) {
                    String selectedText = (String) listContactos.getItemAtPosition(valSelected).toString();
                    String[] vals = selectedText.split("-");
                    String part1 = vals[0];
                    DeleteContacto(part1);
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acciones a realizar si se selecciona "No"
                // Aquí puedes agregar el código que se ejecutará cuando se seleccione "No"
            }
        });

        builder.show();
    }


    private void DeleteContacto(String id){
        String[] argWhere={String.valueOf(id)};

    }

}
