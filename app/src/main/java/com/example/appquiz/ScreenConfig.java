package com.example.appquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScreenConfig extends AppCompatActivity {

    private Spinner selectIdiomas, selectCategorias;
    private List<IdiomaItem> listaIdiomas = new ArrayList<>();
    private List<CategoriaItem> listaCategorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_config);

        selectIdiomas = findViewById(R.id.spinnerIdiomas);
        selectCategorias = findViewById(R.id.spinnerCategorias);

        // Busca os dados da API Node
        carregarDadosDaAPI();
    }

    private void carregarDadosDaAPI() {
        new Thread(() -> {
            try {
                // 1. Carregar Idiomas (Rota: /quiz/idiomas)
                String jsonIdiomas = new HttpRequest("http://10.0.2.2:3000/quiz/idiomas").executar();
                JSONObject objIdiomas = new JSONObject(jsonIdiomas);

                listaIdiomas.clear();
                // A lib google-translate-api-x retorna um objeto onde a chave é o código
                Iterator<String> keys = objIdiomas.keys();
                while(keys.hasNext()) {
                    String cod = keys.next();
                    String nome = objIdiomas.getString(cod);
                    listaIdiomas.add(new IdiomaItem(cod, nome));
                }

                // 2. Carregar Categorias (Rota: /quiz/categorias)
                String jsonCats = new HttpRequest("http://10.0.2.2:3000/quiz/categorias").executar();
                JSONArray arrayCats = new JSONArray(jsonCats);

                listaCategorias.clear();
                listaCategorias.add(new CategoriaItem(0, "Todas as Categorias")); // Opção padrão
                for (int i = 0; i < arrayCats.length(); i++) {
                    JSONObject c = arrayCats.getJSONObject(i);
                    listaCategorias.add(new CategoriaItem(c.getInt("id"), c.getString("name")));
                }

                // Atualiza a UI
                runOnUiThread(this::configurarSpinners);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void configurarSpinners() {
        ArrayAdapter<IdiomaItem> adapterIdio = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaIdiomas);
        selectIdiomas.setAdapter(adapterIdio);

        ArrayAdapter<CategoriaItem> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        selectCategorias.setAdapter(adapterCat);
    }

    public void jogar(View v){
        // Pega os objetos selecionados
        IdiomaItem idiomaSel = (IdiomaItem) selectIdiomas.getSelectedItem();
        CategoriaItem categoriaSel = (CategoriaItem) selectCategorias.getSelectedItem();

        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("p_idioma", idiomaSel.codigo);

        // Se for 0 (Todas), passamos null ou tratamos na MainActivity
        it.putExtra("p_categoria", categoriaSel.id);

        startActivity(it);
    }
}
