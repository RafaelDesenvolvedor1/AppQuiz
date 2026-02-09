package com.example.appquiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private TextView txtPergunta, txtProgresso;
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

        // Inicialização dos componentes
        txtPergunta = findViewById(R.id.txtQuest);
        txtProgresso = findViewById(R.id.txtProgress);
        opt1 = findViewById(R.id.rbOptA);
        opt2 = findViewById(R.id.rbOptB);
        opt3 = findViewById(R.id.rbOptC);
        opt4 = findViewById(R.id.rbOptD);
        btnProxima = findViewById(R.id.btnProximo);
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

        String url;
        String p_idioma = getIntent().getStringExtra("p_idioma");
        int p_categoria = getIntent().getIntExtra("p_categoria", 0);

        if(p_idioma == null) p_idioma = "en";

        url = "http://10.0.2.2:3000/quiz/" + p_idioma;

        if (p_categoria > 0) {
            url += "/" + p_categoria;
        }

       final String urlRequest = url;

        new Thread(() -> {
            try {
                HttpRequest request = new HttpRequest(urlRequest);
                String respostaJson = request.executar();

                Log.d("API_DEBUG", "Resposta recebida: " + respostaJson);

                // Converter a resposta diretamente para JSONArray
                JSONArray results = new JSONArray(respostaJson);

                listaDePerguntas.clear();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);

                    // Nomes das chaves batendo com o LogCat
                    String pergunta = item.getString("pergunta");
                    String correta = item.getString("resposta_correta");
                    JSONArray todasRespostasJson = item.getJSONArray("todas_respostas");

                    List<String> alternativas = new ArrayList<>();
                    for (int j = 0; j < todasRespostasJson.length(); j++) {
                        alternativas.add(todasRespostasJson.getString(j));
                    }

                    listaDePerguntas.add(new Pergunta(pergunta, correta, alternativas));
                }

                runOnUiThread(() -> {
                    esconderLoading();
                    if (!listaDePerguntas.isEmpty()) {
                        indicePerguntaAtual = 0;
                        mostrarPergunta();
                        progresso();
                    }
                });

            } catch (Exception e) {
                Log.e("API_ERROR", "Erro: " + e.getMessage());
                runOnUiThread(() -> {
                    esconderLoading();
                    Toast.makeText(this, "Erro ao conectar com a API", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void mostrarPergunta() {
        if (listaDePerguntas.isEmpty()) return;

        Pergunta p = listaDePerguntas.get(indicePerguntaAtual);
        List<String> alts = p.getAlternativas();

        // Limpa o estado anterior
        rgOpcoes.clearCheck();
        respostaMostrada = false;
        resetarCores();

        // Define os textos (Usa Html.fromHtml para caracteres especiais)
        txtPergunta.setText(Html.fromHtml(p.getPergunta()));

        // Segurança: verifica se existem as 4 alternativas antes de setar
        if (alts.size() >= 4) {
            opt1.setText(Html.fromHtml(alts.get(0)));
            opt2.setText(Html.fromHtml(alts.get(1)));
            opt3.setText(Html.fromHtml(alts.get(2)));
            opt4.setText(Html.fromHtml(alts.get(3)));
        }
    }

    private void progresso() {
        // Correção da concatenação para evitar erros de String.valueOf
        String textoProgresso = (indicePerguntaAtual + 1) + "/" + listaDePerguntas.size();
        txtProgresso.setText(textoProgresso);
    }

    public void pressOk(View view) {
        if (rgOpcoes.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecione uma opção!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!respostaMostrada) {
            mostrarFeedback();
            btnProxima.setText("Próxima");
            respostaMostrada = true;
        } else {
            btnProxima.setText("Ok");
            respostaMostrada = false;
            indicePerguntaAtual++;

            if (indicePerguntaAtual < listaDePerguntas.size()) {
                resetarCores();
                mostrarPergunta();
                progresso();
            } else {
                exibirResultado("Quiz finalizado!", "Score: " + score);
                mostrarOcultar(false);
            }
        }
    }

    private void mostrarFeedback() {
        int idSelecionado = rgOpcoes.getCheckedRadioButtonId();
        RadioButton selecionado = findViewById(idSelecionado);
        Pergunta perguntaAtual = listaDePerguntas.get(indicePerguntaAtual);
        String respostaUsuario = selecionado.getText().toString();

        if (respostaUsuario.equals(perguntaAtual.getCorreta())) {
            selecionado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            score++;
        } else {
            selecionado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            destacarRespostaCorreta(perguntaAtual);
        }

        for (int i = 0; i < rgOpcoes.getChildCount(); i++) {
            rgOpcoes.getChildAt(i).setEnabled(false);
        }
    }

    private void destacarRespostaCorreta(Pergunta pergunta) {
        for (int i = 0; i < rgOpcoes.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgOpcoes.getChildAt(i);
            if (rb.getText().toString().equals(pergunta.getCorreta())) {
                rb.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    private void resetarCores() {
        for (int i = 0; i < rgOpcoes.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgOpcoes.getChildAt(i);
            rb.setTextColor(getResources().getColor(android.R.color.white));
            rb.setEnabled(true);
        }
    }

    private void reiniciarQuiz() {
        score = 0;
        indicePerguntaAtual = 0;
        loadQuests(); // Recarrega do servidor
    }

    private void exibirResultado(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Reiniciar", (dialog, which) -> reiniciarQuiz())
                .setNegativeButton("Sair", (dialog, which) -> finish())
                .show();
    }

    private void mostrarOcultar(boolean opcao) {
        int visibilidade = opcao ? View.VISIBLE : View.GONE;
        txtPergunta.setVisibility(visibilidade);
        txtProgresso.setVisibility(visibilidade);
        rgOpcoes.setVisibility(visibilidade);
        btnProxima.setVisibility(visibilidade);
    }

    private void mostrarLoading() {
        progressLoading.setVisibility(View.VISIBLE);
        mostrarOcultar(false);
    }

    private void esconderLoading() {
        progressLoading.setVisibility(View.GONE);
        mostrarOcultar(true);
    }
}