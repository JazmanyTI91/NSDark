package com.example.nsdark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText email, pass;
    TextView sign;
    Button log;
    String server = "http://192.168.0.11/android_rest/login.php";
    Snackbar snackbarDisable;
    Snackbar snackbarEnable;
    private WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.txtEmail);
        pass = findViewById(R.id.txtPassword);
        sign = findViewById(R.id.sign);
        log = findViewById(R.id.btnLog);
        View view = findViewById(R.id.container);

        snackbarDisable = Snackbar.make(view, "404 connection wifi disable", BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbarDisable.setBackgroundTint(Color.rgb(121,163,193));
        snackbarDisable.setDuration(5000);

        snackbarEnable = Snackbar.make(view, "Network connection enable", BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbarEnable.setBackgroundTint(Color.rgb(85,29,115));
        snackbarEnable.setDuration(5000);
    }

    //Realizamos la petición para el loggeo
    public void Login(View view) {

        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST, server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("ERROR 1")) {
                            Toast.makeText(getApplicationContext(), "Se deben de llenar todos los campos.", Toast.LENGTH_SHORT).show();
                        } else if(response.equals("ERROR 2")) {
                            Toast.makeText(getApplicationContext(), "No existe ese registro.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Inicio de Sesion exitoso.", Toast.LENGTH_LONG).show();
                            //Menu();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // En caso de tener algun error en la obtencion de los datos
                Toast.makeText(getApplicationContext(), "ERROR AL INICIAR SESION "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                // En este metodo se hace el envio de valores de la aplicacion al servidor
                Map<String, String> parametros = new Hashtable<String, String>();
                parametros.put("usuario", email.getText().toString().trim());
                parametros.put("password", pass.getText().toString().trim());

                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReciver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReciver);
    }

    private BroadcastReceiver wifiStateReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    //status enable
                    //snackbarEnable.show();
                    log.setEnabled(true);
                    sign.setEnabled(true);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    // status disable
                    snackbarDisable.show();
                    log.setEnabled(false);
                    sign.setEnabled(false);
                    break;
            }
        }
    };

}