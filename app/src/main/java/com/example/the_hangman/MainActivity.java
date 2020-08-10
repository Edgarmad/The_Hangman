package com.example.the_hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public ImageView colgado;
    public ImageView colgadoFlag;
    public int error;
    public boolean estado;
    public boolean [] rev;
    public EditText [] editTexts;
    public EditText intentos;
    public TableLayout tableLayout;
    public ArrayList<String> names;
    public Button nuevo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("https://www.practicaespanol.com/50-nombres-de-animales-en-espanol/").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        names = new ArrayList<String>();
        String[] sides = result.split("</div><!-- .entry-content -->");
        Pattern p = Pattern.compile("<span style=\"font-size: 12pt;\"> <strong>(.*?)</strong> &#8211", Pattern.DOTALL);
        Matcher m = p.matcher(sides[0]);

        int n=0;
        while(m.find()){
            names.add(m.group());
            n++;
        }
        for(int i=0; i< names.size(); i++){
            names.set(i,names.get(i).substring(40,names.get(i).length()-16).toUpperCase());
        }

        for(int i=0; i< names.size(); i++){
            Log.i("prueba:"+i,names.get(i));
        }

        tableLayout= findViewById(R.id.tableLayout);


        intentos= findViewById(R.id.txtWord);

        error=0;
        colgado= findViewById(R.id.halfColgado);
        colgado.setAlpha(0f);
        nuevo = findViewById(R.id.btNew);
        inicio();
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inicio();
            }
        });

    }
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.i("URL", urls[0]);
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1) {
                    char currentCharacter = (char) data;
                    result += currentCharacter;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }

    }
    public void revisar(View view){
        if(estado) {
            boolean prueba1 = true; //errores
            boolean prueba2 = false; //longitud

            String letter = String.valueOf(intentos.getText()).toUpperCase();
            if (letter.length() == 1) {
                prueba2 = true;
                for (int i = 0; i < editTexts.length; i++) {
                    String content = String.valueOf(editTexts[i].getText());
                    if (letter.equals(content)) {
                        editTexts[i].setTextColor(Color.GRAY);
                        editTexts[i].setBackgroundResource(R.drawable.letter_bg);
                        rev[i] = true;
                        prueba1 = false;
                    }
                }
            }
            if (prueba1 && prueba2) {
                error++;
                errores();
                if (error == 6) {
                    estado=false;
                }
            }
        }
    }
    public void arregloLetras(int num){
            TableRow tableRow = new TableRow(this);
            tableLayout.addView(tableRow);
            char [] letritas =  names.get(num).toCharArray();
            editTexts= new EditText[letritas.length];
            rev= new boolean[letritas.length];
            for(int i=0;i<letritas.length;i++) {
                EditText editText = new EditText(this);
                editText.setText(String.valueOf(letritas[i]));
                editText.setClickable(false);
                editText.setTextColor(Color.TRANSPARENT);
                editTexts[i]= editText;
                rev[i]= false;
                tableRow.addView(editText);
            }
    }
    public void inicio(){
        estado=true;
        tableLayout.removeAllViews();
        error=0;
        errores();
        int numero = (int)(Math.random()*(48-0+1)+0);
        arregloLetras(numero);
    }
    public void errores(){
        switch(error){
            case 0:
                colgado.setAlpha(0f);
                break;
            case 1:
                colgado.setAlpha(1f);
                colgado.setImageResource(R.drawable.body);
                break;
            case 2:
                colgado.setImageResource(R.drawable.lefthand);
                break;
            case 3:
                colgado.setImageResource(R.drawable.righthand);
                break;
            case 4:
                colgado.setImageResource(R.drawable.leftleg);
                break;
            case 5:
                colgado.setImageResource(R.drawable.rightleg);
                break;
        }
    }
}
