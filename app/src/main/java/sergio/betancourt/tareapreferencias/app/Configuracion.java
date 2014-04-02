package sergio.betancourt.tareapreferencias.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class Configuracion extends ActionBarActivity {

    TextView txtMensaje;
    TextView txtNombre;
    TextView txtTelefono;
    TextView txtHora;
    Switch swtPlataforma;
    Button btnGuardar;
    boolean opcion1;
    String telefono,nombre, hora, activo;
    String NOMBRE = "hola_mundo";

    public static final String  TAG =
            Configuracion.class.getSimpleName();

    JSONArray datosServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        txtMensaje = (TextView)findViewById(R.id.txtMensaje);
        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtTelefono = (TextView)findViewById(R.id.txtTelefono);
        txtHora = (TextView)findViewById(R.id.txtHora);


        btnGuardar = (Button) findViewById(R.id.btnGuardar);

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);



        opcion1 = sharedPreferences.getBoolean("opcion1", false);
        telefono = sharedPreferences.getString("telefono","");
        nombre = sharedPreferences.getString("nombre","");
        hora = sharedPreferences.getString("hora", "");
        activo = sharedPreferences.getString("activo", "");
        //swtPlataforma.setChecked(opcion1);

        GetAPI getAPI = new GetAPI();
        getAPI.execute();

        leerMemoriaInterna ();
    }

        public void leerMemoriaInterna(){
            try{
                String nombre, texto;
                FileInputStream fileInputStream =
                        openFileInput(NOMBRE);

                BufferedReader bReader = new BufferedReader(
                        new InputStreamReader(fileInputStream, "UTF-8"), 8);

                StringBuilder sBuilder = new StringBuilder();

                String line = null;

                while ((line = bReader.readLine()) != null) {

                         sBuilder.append(line);
                        nombre = sBuilder.toString();
                        Log.d(TAG, nombre);

                        txtNombre.setText(nombre);
                    }

                }

            catch (FileNotFoundException e){}
            catch (IOException e){}
        }



    public void guardarMemoriaInterna(View v){
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

       // editor.putBoolean("opcion1", swtPlataforma.isChecked());
        editor.commit();



        try{
            String telefono = txtTelefono.getText().toString() + ",";
            String nombre = txtNombre.getText().toString() + ",";
            String hora = txtHora.getText().toString() + ",";
            String activo = txtMensaje.getText().toString();

            FileOutputStream fileOutputStream =
                    openFileOutput(NOMBRE, Context.MODE_PRIVATE);

            //MODE_APPEND, MODE_WORLD_READABLE, and MODE_WORLD_WRITEABLE

            fileOutputStream.write(telefono.getBytes());
            fileOutputStream.write(nombre.getBytes());
            fileOutputStream.write(hora.getBytes());
            fileOutputStream.write(activo.getBytes());
            fileOutputStream.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
    }



    private class GetAPI extends AsyncTask<Object, Void, JSONArray> {
        @Override
        protected void onPreExecute(){
        }

        @Override
        protected JSONArray doInBackground(Object... objects) {
            Log.d(TAG, "Response OK");

            int responseCode = -1;
            String resultado = "";
            JSONArray jsonResponse = null;

            try{
                URL apiURL =  new URL(
                        "http://continentalrescueafrica.com/2013/test.php");

                HttpURLConnection httpConnection = (HttpURLConnection)
                        apiURL.openConnection();
                httpConnection.connect();
                responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = httpConnection.getInputStream();
                    BufferedReader bReader = new BufferedReader(
                            new InputStreamReader(inputStream, "UTF-8"), 8);

                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                      //  sBuilder.append(line + "\n");
                        sBuilder.append( line );
                    }

                    inputStream.close();
                    resultado = sBuilder.toString();
                    resultado = "[" + resultado + "]";
                    Log.d(TAG, resultado);
                    jsonResponse = new JSONArray(resultado);

                }else{
                    Log.i(TAG, "Error en el HTTP " + responseCode);
                }
            }
            catch (JSONException e){}
            catch (MalformedURLException e){}
            catch (IOException e){}
            catch (Exception e){}

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONArray respuesta) {

            enlistarDatos(respuesta);
        }
    }

    private void enlistarDatos(JSONArray datos) {
        if (datos == null){
            Toast.makeText(this, "Error en el servidor",
                    Toast.LENGTH_LONG).show();
        }
        else{
            datosServidor = datos;
            ArrayList<HashMap<String,String>> valores =
                    new ArrayList<HashMap<String, String>>();

            try{
                for (int i =0; i< datos.length(); i++){
                    JSONObject valor = datos.getJSONObject(i);

                    HashMap<String, String> hashValor =
                            new HashMap<String, String>();

                    //hashValor.put("id", valor.getString("id"));
                    hashValor.put("telefono",valor.getString("telefono") );
                    hashValor.put("nombre",valor.getString("nombre"));
                    hashValor.put("hora",valor.getString("hora") );
                    hashValor.put("activo",valor.getString("activo"));

                    valores.add(hashValor);

                }
                String[] llaves = {"telefono","nombre","hora","activo"};
                int[] ids = {android.R.id.text2,android.R.id.text1};

                SimpleAdapter adaptador = new SimpleAdapter(this, valores,
                        android.R.layout.simple_list_item_2,
                        llaves, ids);

                mostrarAlerta(0);

               /* swtPlataforma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(swtPlataforma.isChecked() ) {
                            mostrarAlerta(0);
                        }
                        else {
                            mostrarAlerta(1);
                        }
                    }
                });

*/

            }
            catch (JSONException e){}
        }

    }
    private void mostrarAlerta(int posicion) {
        try{
            JSONObject jsonObject = datosServidor.getJSONObject(posicion);
            String datoObtenido = jsonObject.getString("nombre");

            txtTelefono.setText(jsonObject.getString("telefono"));
            txtNombre.setText(datoObtenido);
            txtHora.setText(jsonObject.getString("hora"));
            txtMensaje.setText(jsonObject.getString("activo"));

        }
        catch (JSONException e){}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configuracion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
