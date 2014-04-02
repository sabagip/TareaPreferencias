package sergio.betancourt.tareapreferencias.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Mensaje extends ActionBarActivity {

    TextView txtMensaje;
    TextView txtNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);


        txtMensaje = (TextView)findViewById(R.id.txtMensaje);
        txtNombre = (TextView)findViewById(R.id.txtNombre);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String nombre = bundle.getString("nombre");
            String mensaje = bundle.getString("mensaje");

            txtNombre.setText(nombre);
            txtMensaje.setText(mensaje);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mensaje, menu);
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
