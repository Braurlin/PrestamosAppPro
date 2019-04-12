package com.example.prestamosappaltice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Cuotas extends AppCompatActivity {

    String NOMBRE_DIRECTORIO = "CalculadoraDePréstamos";
    String NOMBRE_DOCUMENTO = "MiPréstamo.pdf";

    TableRow row;
    TextView tvSolicitante;
    ArrayList listado;
    int x, aux_dia, cuota, tasa, dia, mes, year;
    Double intereses, amortizacion, pago, capital, total_amortizacion = 0.0, total_intereses = 0.0, total_pago = 0.0;
    StringBuilder fecha_pago;
    String solicitante;
    DecimalFormat formato_decimal = new DecimalFormat("###,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuotas);
        getSupportActionBar().setTitle("Desglose");
        setupActionBar();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);

        }


        TableLayout table = (TableLayout) this.findViewById(R.id.tabla);

        tvSolicitante = (TextView) findViewById(R.id.tvSolicitante);

        Bundle datos = getIntent().getExtras();
        solicitante = datos.getString("solicitante");

        listado = new ArrayList();
        cuota = Integer.parseInt(datos.getString("cuota"));
        dia = Integer.parseInt(datos.getString("dia"));
        mes = Integer.parseInt(datos.getString("mes"));
        year = Integer.parseInt(datos.getString("year"));
        capital = Double.parseDouble(datos.getString("monto"));
        tasa = Integer.parseInt(datos.getString("tasa"));

        aux_dia = dia;
        amortizacion = capital / cuota;


        tvSolicitante.setText("¡" + solicitante + "!" + " Cuotas del prestamo:");


        for(x = 1; x <= cuota; x++) {
            fecha_pago = new StringBuilder();

            if(x == 1) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.filas_pares, null);
                ((TextView)row.findViewById(R.id.tvCuota)).setText("0");
                ((TextView)row.findViewById(R.id.tvCapital)).setText(String.valueOf(formato_decimal.format(capital)));

                table.addView(row);
                table.requestLayout();
            }

            if(x == 1 && dia == 31) {
                dia = 30;
                aux_dia = dia;
            }

            if(dia > 28 && mes == 2) {
                aux_dia = dia;
                dia = 28;
            }

            if(mes > 12) {
                mes = 1;
                year++;
            }

            fecha_pago.append(dia + "/" + mes + "/" + year);
            intereses = (capital * tasa) / 100;
            capital =  capital - amortizacion;
            pago = amortizacion + intereses;

            total_amortizacion = total_amortizacion + amortizacion;
            total_intereses = total_intereses + intereses;
            total_pago = total_pago + pago;

            if(x%2 != 0) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.filas_impares, null);
                ((TextView)row.findViewById(R.id.tvCuota)).setText(String.valueOf(x));
                ((TextView)row.findViewById(R.id.tvCapital)).setText(String.valueOf(formato_decimal.format(capital)));
                ((TextView)row.findViewById(R.id.tvAmortizacion)).setText(String.valueOf(formato_decimal.format(amortizacion)));
                ((TextView)row.findViewById(R.id.tvIntereses)).setText(String.valueOf(formato_decimal.format(intereses)));
                ((TextView)row.findViewById(R.id.tvPago)).setText(String.valueOf(formato_decimal.format(pago)));
                ((TextView)row.findViewById(R.id.tvFecha)).setText(String.valueOf(fecha_pago));
            }

            else {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.filas_pares, null);
                ((TextView)row.findViewById(R.id.tvCuota)).setText(String.valueOf(x));
                ((TextView)row.findViewById(R.id.tvCapital)).setText(String.valueOf(formato_decimal.format(capital)));
                ((TextView)row.findViewById(R.id.tvAmortizacion)).setText(String.valueOf(formato_decimal.format(amortizacion)));
                ((TextView)row.findViewById(R.id.tvIntereses)).setText(String.valueOf(formato_decimal.format(intereses)));
                ((TextView)row.findViewById(R.id.tvPago)).setText(String.valueOf(formato_decimal.format(pago)));
                ((TextView)row.findViewById(R.id.tvFecha)).setText(String.valueOf(fecha_pago));
            }

            table.addView(row);
            table.requestLayout();

            dia = aux_dia;
            mes++;
        }

        row = (TableRow) LayoutInflater.from(this).inflate(R.layout.fila_final, null);
        ((TextView)row.findViewById(R.id.tvCapital)).setText("Total");
        ((TextView)row.findViewById(R.id.tvAmortizacion)).setText(String.valueOf(formato_decimal.format(total_amortizacion)));
        ((TextView)row.findViewById(R.id.tvIntereses)).setText(String.valueOf(formato_decimal.format(total_intereses)));
        ((TextView)row.findViewById(R.id.tvPago)).setText(String.valueOf(formato_decimal.format(total_pago)));

        table.addView(row);
        table.requestLayout();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
           crearPDF();
        // Genera el documento
        switch (item.getItemId()){
               case  R.id.print_doc:
                Toast.makeText(Cuotas.this, "Se creo el documento en la" +
                        "carpeta de descarga(Download) de la memoria interna.", Toast.LENGTH_LONG).show();
                   break;
            }

        return super.onOptionsItemSelected(item);

    }

    public void crearPDF() {
        Document documento = new Document();

        try {
            File file = crearFichero(NOMBRE_DOCUMENTO);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();

            documento.add(new Paragraph("Calculo de préstamos \n\n"));
            documento.add(new Paragraph("Solicitante: " + solicitante + " \n\n"));


            PdfPTable tabla = new PdfPTable(6);

            Paragraph columna1 = new Paragraph("Cuota");
            columna1.getFont().setStyle(Font.BOLD);
            columna1.getFont().setSize(10);
            tabla.addCell(columna1);

            Paragraph columna2 = new Paragraph("Capital");
            columna2.getFont().setStyle(Font.BOLD);
            columna2.getFont().setSize(10);
            tabla.addCell(columna2);

            Paragraph columna3 = new Paragraph("Amortización");
            columna3.getFont().setStyle(Font.BOLD);
            columna3.getFont().setSize(10);
            tabla.addCell(columna3);

            Paragraph columna4 = new Paragraph("Interés");
            columna4.getFont().setStyle(Font.BOLD);
            columna4.getFont().setSize(10);
            tabla.addCell(columna4);

            Paragraph columna5 = new Paragraph("Pago");
            columna5.getFont().setStyle(Font.BOLD);
            columna5.getFont().setSize(10);
            tabla.addCell(columna5);

            Paragraph columna6 = new Paragraph("Fecha");
            columna6.getFont().setStyle(Font.BOLD);
            columna6.getFont().setSize(10);
            tabla.addCell(columna6);

            for (int i = 1; i<= cuota; i++) {

                tabla.addCell(String.valueOf("Cuota " + i));
                capital =  capital - amortizacion;
                tabla.addCell(String.valueOf(formato_decimal.format(capital)));
                tabla.addCell(String.valueOf(formato_decimal.format(amortizacion)));
                intereses = (capital * tasa) / 100;
                tabla.addCell(String.valueOf(formato_decimal.format(intereses)));
                pago = amortizacion + intereses;
                tabla.addCell(String.valueOf(formato_decimal.format(pago)));

                if(mes > 12) {
                    mes = 1;
                    year++;
                }

                mes++;

                tabla.addCell(String.valueOf(dia + "/" + mes + "/" + year));


            }

                tabla.addCell(String.valueOf("Total"));
                tabla.addCell(String.valueOf(""));
                tabla.addCell(String.valueOf(formato_decimal.format(total_amortizacion)));
                tabla.addCell(String.valueOf(formato_decimal.format(total_intereses)));
                tabla.addCell(String.valueOf(formato_decimal.format(total_pago)));
                tabla.addCell(String.valueOf(""));

           documento.add(tabla);


        } catch(DocumentException e) {
        } catch(IOException e) {
        } finally {
            documento.close();
        }
    }

    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();

        File fichero = null;
        if(ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    public File getRuta() {
        File ruta = null;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            ruta = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), NOMBRE_DIRECTORIO);

            if(ruta != null) {
                if(!ruta.mkdirs()) {
                    if(!ruta.exists()) {
                        return null;
                    }
                }
            }

        }
        return ruta;
    }
}




