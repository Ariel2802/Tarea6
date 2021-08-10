package com.example.tarea6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    EditText txtCodPais, txtCelular, txtIdentificacion, txtMonto, txtTarifa, txtReferencia, txtJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCodPais = findViewById(R.id.txtCodPais);
        txtCelular = findViewById(R.id.txtCelular);
        txtIdentificacion = findViewById(R.id.txtIdentificacion);
        txtMonto = findViewById(R.id.txtMonto);
        txtTarifa = findViewById(R.id.txtTarifa);
        txtReferencia = findViewById(R.id.txtReferencia);
        txtJson = findViewById(R.id.txtJson);
        requestQueue = Volley.newRequestQueue(this);
    }

    public void eventoPagar(View view) {
        requestPago();

    }

    private void requestPago() {
        String url = "https://pay.payphonetodoesposible.com/api/Sale";
        HashMap<String, String> hash = new HashMap<>();
        /*"phoneNumber": "986616379",
                "countryCode": "593"
        "clientUserId": "1207028109",
                "reference": "none",
                "amount": 100,
                "amountWithTax": 90,
                "amountWithoutTax": 0,
                "tax": 10,
                "clientTransactionId": "12345"*/
        hash.put("phoneNumber", txtCelular.getText().toString());
        hash.put("countryCode", txtCodPais.getText().toString());
        hash.put("clientUserId", txtIdentificacion.getText().toString());
        hash.put("reference", txtReferencia.getText().toString());
        double amount, tax, amountWithTax;
        amount = Double.parseDouble(txtMonto.getText().toString());
        amount = amount * 100;
        int intAmount, intTax, intAamountWithTax;
        intAmount = (int) Math.round(amount);
        hash.put("amount", String.valueOf(intAmount));

        tax = Double.parseDouble(txtTarifa.getText().toString());
        tax = (intAmount * tax / 100);
        intTax = (int) Math.round(tax);
        hash.put("tax", String.valueOf(intTax));

        amountWithTax = amount - tax;
        txtTarifa.setText((amountWithTax/100) + "");
        intAamountWithTax = (int) Math.round(amountWithTax);
        hash.put("amountWithTax", String.valueOf(intAamountWithTax));

        hash.put("amountWithoutTax", "0");

        SimpleDateFormat format = new SimpleDateFormat("yyyyddMMHHmmss");
        String date = format.format(new Date());
        hash.put("clientTransactionId", date);
        Toast.makeText(MainActivity.this, "Se generó la petición: " + date, Toast.LENGTH_LONG).show();

        JSONObject js = new JSONObject(hash);
        txtJson.setText(js.toString());
        System.out.println(js.toString());
        //JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, createMyReqSuccessListener(), createMyReqErrorListener()) { protected Map<String, String> getParams() throws com.android.volley.AuthFailureError { Map<String, String> params = new HashMap<String, String>(); params.put("param1", num1); params.put("param2", num2); return params; }; };
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String transaction = response.getString("transactionId");
                    Toast.makeText(MainActivity.this, "Se completó la transacción: " + transaction, Toast.LENGTH_LONG).show();
                    txtJson.append(",\n{\"transactionId\":\"" + transaction + "\"}");

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Toast.makeText(MainActivity.this, "Incorrecto\n" + ex.getMessage(), Toast.LENGTH_LONG).show();

                System.out.println(ex.toString());
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Accept", "application/json");
                headerMap.put("Authorization", "Bearer j6FVqJrHzzSN6_bw3mWjKH4dE45vBx8GzYF7vgZErfCEwEeVOXWmjdem_1cWD9cxUKOrlwVBc5PFKSnv5XLO1zWppg9cVAlvf9sIKQ5A5PpObPrwVcHV6EoeAlhfcXelEZ5w3OkSQtm_w6C8c8r3YqhiLdl2F2NZb7Gz44EahDbKR9mfjqZQkKV-pWx61B2xAn7BwU0LuJWolWQnDxod22NwaQ8Fdj-JY014GSL89720obAmFZ5BXrvG2ZIrOiKjXhuqqCDDgoFF1N4QrXesJ2868MqKAU0Dk2uKxPeyj4zizPWc7yanlAllkNte_FSGP0rQHFBJS3lnUDxsb0FJyLsAPcY");
                return headerMap;
            }
        };

        requestQueue.add(jsonRequest);

    }
}