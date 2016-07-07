package mawashi.alex.htmlposttester;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    EditText IndirizzoEdit;
    EditText MessaggioEdit;
    TextView RispostaText;

    String Indirizzo = "";
    String Messaggio = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        IndirizzoEdit = (EditText) findViewById(R.id.editTextIND);
        MessaggioEdit = (EditText) findViewById(R.id.editTextMESS);
        RispostaText  = (TextView) findViewById(R.id.textViewRisposta);

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        IndirizzoEdit.setText(sharedPreferences.getString("Indirizzo","")); //"TAG", "DEFAULT_VALUE"
        MessaggioEdit.setText(sharedPreferences.getString("Messaggio",""));
    }

    public void Invia(View v){
        //salvo indirizzo e messaggio per eventuali prove successive
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Indirizzo", IndirizzoEdit.getText().toString());
        editor.putString("Messaggio", MessaggioEdit.getText().toString());
        editor.commit();

        Indirizzo = IndirizzoEdit.getText().toString();
        Messaggio = MessaggioEdit.getText().toString();
        try {
            new AsyncHTTPPOST().execute();
        }catch(Exception e){
            Log.e("ASYNC","ERRORE: " + e.toString());
            Toast.makeText(getApplicationContext(),"ERRORE: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private class AsyncHTTPPOST extends AsyncTask<Void,Void,Void> {
        String result = "";

        @Override
        protected void onPreExecute(){}

        @Override
        protected Void doInBackground(Void...params){
            //Operazioni da fare in background
            try{
                HttpPost post = new HttpPost(Indirizzo);
                HttpClient client = new DefaultHttpClient();
                StringEntity se = new StringEntity(Messaggio);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/string"));
                post.setEntity(se);
                HttpResponse response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
            }catch(Exception e){
                Log.e("POST EXC", "POST EXCEPTION: " + e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
                try{
                    if(!result.equals(""))
                        RispostaText.setText("Risposta: " + result.toString());
                    else
                        RispostaText.setText("Risposta: NULL");
                }catch(Exception e){Log.e("RESULTASYNK","ERRORE ASYNC RESULT: " + e.toString());}
            }
        }
    }

