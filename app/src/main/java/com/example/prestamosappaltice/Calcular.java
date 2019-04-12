package com.example.prestamosappaltice;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Calcular extends AppCompatActivity implements View.OnClickListener {


    Button verCuotas, limpiar, bFecha;
    EditText nombre, monto, tasa, fecha, cuota;
    int dia, mes, year;
    StringBuilder fechaFormat;
    Calendar dFecha;
    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calcular_prestamo);
        getSupportActionBar().setTitle("Formulario");
        setupActionBar();


        verCuotas = (Button) findViewById(R.id.bCalcular);
        limpiar = (Button) findViewById(R.id.bLimpiar);
        bFecha = (Button) findViewById(R.id.bFecha);
        //seleccionarFecha = (Button) findViewById(R.id.bSeleccionarFecha);

        nombre = (EditText) findViewById(R.id.etNombre);
        monto = (EditText) findViewById(R.id.etMonto);
        tasa = (EditText) findViewById(R.id.etTasa);
        fecha = (EditText) findViewById(R.id.etFecha);
        cuota = (EditText) findViewById(R.id.etCuotas);

        verCuotas.setOnClickListener(this);
        limpiar.setOnClickListener(this);
        bFecha.setOnClickListener(this);

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == verCuotas) {
            if (nombre.getText().toString().isEmpty() || monto.getText().toString().isEmpty()
                    || tasa.getText().toString().isEmpty() || fecha.getText().toString().isEmpty()) {
                Toast.makeText(this, "Por favor, llene todos los campos del formulario para proseguir.",
                        Toast.LENGTH_LONG).show();
            } else if (tasa.getText().toString().equals("0") || cuota.getText().toString().equals("0")) {
                Toast.makeText(this, "Verifica los campos Tasa y Cuota.",
                        Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(Calcular.this, Cuotas.class);
                intent.putExtra("dia", String.valueOf(dia)).toString();
                intent.putExtra("mes", String.valueOf(mes)).toString();
                intent.putExtra("year", String.valueOf(year)).toString();
                intent.putExtra("solicitante", String.valueOf(nombre.getText())).toString();
                intent.putExtra("tasa", String.valueOf(tasa.getText())).toString();
                intent.putExtra("monto", String.valueOf(monto.getText())).toString();
                intent.putExtra("cuota", String.valueOf(cuota.getText())).toString();
                startActivity(intent);
            }
        }

        if (v == limpiar) {
            nombre.setText("");
            monto.setText("");
            tasa.setText("");
            fecha.setText("");
            cuota.setText("");
        }

      if (v == bFecha) {

                dFecha = Calendar.getInstance();

                year = dFecha.get(Calendar.YEAR);
                mes = dFecha.get(Calendar.MONTH)+1;
                dia = dFecha.get(Calendar.DAY_OF_MONTH);

               fechaFormat = new StringBuilder();
               fechaFormat.append(dia + "/" + mes + "/" + year);

               fecha.setText(fechaFormat);

                datePickerDialog = new DatePickerDialog(Calcular.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                fecha.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                            }
                        }, year, mes, dia);

                datePickerDialog.show();
            }
        }

    }