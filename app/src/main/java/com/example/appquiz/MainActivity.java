package com.example.appquiz;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressLoading;

    private TextView txtPergunta;

    private TextView txtProgresso;
    private RadioGroup rgOpcoes;
    private RadioButton opt1, opt2, opt3, opt4;
    private Button btnProxima;

    private List<Pergunta> listaDePerguntas = new ArrayList<>();
    private int indicePerguntaAtual = 0;

    private int score = 0;

    private boolean respostaMostrada = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializando os componentes
        txtPergunta = findViewById(R.id.txtQuest);
        txtProgresso = findViewById(R.id.txtProgress);
        opt1 = findViewById(R.id.rbOptA);
        opt2 = findViewById(R.id.rbOptB);
        opt3 = findViewById(R.id.rbOptC);
        opt4 = findViewById(R.id.rbOptD);
        btnProxima = findViewById(R.id.btnProximo);
        // Não esqueça de vincular o RadioGroup se ele existir no XML
        rgOpcoes = findViewById(R.id.radioGroup);
        progressLoading = findViewById(R.id.progressBar);

        loadQuests();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadQuests() {
        runOnUiThread(this::mostrarLoading);

        new Thread(() -> {
            try {
                // Instancia a classe passando a URL
                HttpRequest request = new HttpRequest("https://opentdb.com/api.php?amount=10&type=multiple");

                // Chama o método que faz todo o processo de conexão e retorna a String
                String respostaJson = request.executar();

                // Agora você faz o "Trabalho de Inteligência" (Parse do JSON)
                JSONObject jsonObject = new JSONObject(respostaJson);
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);

                    String pergunta = item.getString("question");
                    String correta = item.getString("correct_answer");

                    JSONArray incorretasJson = item.getJSONArray("incorrect_answers");
                    List<String> alternativas = new ArrayList<>();

                    alternativas.add(correta);

                    for (int j = 0; j < incorretasJson.length(); j++) {
                        alternativas.add(incorretasJson.getString(j));
                    }

                    embaralharLista(alternativas);

                    listaDePerguntas.add(
                            new Pergunta(pergunta, correta, alternativas)
                    );
                }

                runOnUiThread(() -> {
                    esconderLoading();
                    indicePerguntaAtual = 0;
                    mostrarPergunta();
                    progresso();
                });

            } catch (Exception e) {
                // Se der erro na conexão lá na classe HttpRequest, ele cai aqui
                Log.e("API_ERROR", "Erro na requisição: " + e.getMessage());
            }
        }).start();
    }

    public void pressOk(View view){

        if(rgOpcoes.getCheckedRadioButtonId() == -1){
            return;
        }

        if(!respostaMostrada){
            mostrarFeedback();
            btnProxima.setText("Próxima");
            respostaMostrada = true;
        }else{
            btnProxima.setText("Ok");
            respostaMostrada = false;
            resetarCores();

            indicePerguntaAtual++;

            if(indicePerguntaAtual < listaDePerguntas.size()){
                mostrarPergunta();
            }else{
                exibirResultado("Quiz finalizado!", "Score: "+String.valueOf(score));
                mostrarOcultar(false);
            }

            progresso();
        }

    }

    private void mostrarFeedback(){
        int idSelecionado = rgOpcoes.getCheckedRadioButtonId();
        RadioButton selecionado = findViewById(idSelecionado);

        Pergunta perguntaAtual = listaDePerguntas.get(indicePerguntaAtual);

        String respostaUsuario = selecionado.getText().toString();

        if(respostaUsuario.equals(perguntaAtual.getCorreta())){
            selecionado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            score++;
        }else{
            selecionado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            destacarRespostaCorreta(perguntaAtual);
        }

        for(int i = 0; i < rgOpcoes.getChildCount(); i++){
            rgOpcoes.getChildAt(i).setEnabled(false);
        }
    }

    private void destacarRespostaCorreta(Pergunta pergunta){
        for(int i = 0; i < rgOpcoes.getChildCount(); i++){
            RadioButton rb = (RadioButton) rgOpcoes.getChildAt(i);
            if(rb.getText().toString().equals(pergunta.getCorreta())){
                rb.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    private void resetarCores(){
        for(int i = 0; i < rgOpcoes.getChildCount(); i++){
            RadioButton rb = (RadioButton) rgOpcoes.getChildAt(i);
            rb.setTextColor(getResources().getColor(android.R.color.white));
            rb.setEnabled(true);
        }
    }



    private void calcularScore(){
        int idSelecionado = rgOpcoes.getCheckedRadioButtonId();

        RadioButton selecionado = findViewById(idSelecionado);
        String respostaUsuario = selecionado.getText().toString();

        Pergunta perguntaAtual = listaDePerguntas.get(indicePerguntaAtual);

        if(respostaUsuario.equals(perguntaAtual.getCorreta())){
            score++;
        }
    }

    private void mostrarPergunta(){

        if (listaDePerguntas.isEmpty()) return;

        Pergunta p= listaDePerguntas.get(indicePerguntaAtual);

        txtPergunta.setText(Html.fromHtml(p.getPergunta()));

        opt1.setText(Html.fromHtml(p.getAlternativas().get(0)));
        opt2.setText(Html.fromHtml(p.getAlternativas().get(1)));
        opt3.setText(Html.fromHtml(p.getAlternativas().get(2)));
        opt4.setText(Html.fromHtml(p.getAlternativas().get(3)));

        rgOpcoes.clearCheck();
        respostaMostrada = false;
        resetarCores();
    }

    private void progresso(){
        txtProgresso.setText(String.valueOf(indicePerguntaAtual+1 + "/" + listaDePerguntas.size()));
    }

    private void exibirResultado(String title, String message){
       new AlertDialog.Builder(this)
               .setTitle(title)
               .setMessage(message)
               .setCancelable(false)
               .setPositiveButton("Reiniciar", (dialog, which)->{
                   reiniciarQuiz();
               })
               .setNegativeButton("Sair", (dialog, which) -> {
                   finish();
               })
               .show();
    }

    private void reiniciarQuiz(){
        score = 0;
        indicePerguntaAtual = 0;

        progresso();

        embaralharLista(listaDePerguntas);

        mostrarPergunta();

        mostrarOcultar(true);
    }

    private void mostrarOcultar(boolean opcao){
        if(opcao){
            txtPergunta.setVisibility(View.VISIBLE);
            txtProgresso.setVisibility(View.VISIBLE);
            rgOpcoes.setVisibility(View.VISIBLE);
            btnProxima.setEnabled(true);
        }else{
            txtPergunta.setVisibility(View.GONE);
            txtProgresso.setVisibility(View.GONE);
            rgOpcoes.setVisibility(View.GONE);
            btnProxima.setEnabled(false);
        }
    }

    private <T> void embaralharLista(List<T> lista ){
        Collections.shuffle(lista);
    }

    private void mostrarLoading(){
        progressLoading.setVisibility(View.VISIBLE);
        mostrarOcultar(false);
    }

    private void esconderLoading(){
        progressLoading.setVisibility(View.GONE);
        mostrarOcultar(true);
    }


}

