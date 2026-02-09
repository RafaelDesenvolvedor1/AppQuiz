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

import java.util.ArrayList;
import java.util.List;

public class ScreenConfig extends AppCompatActivity {

    private Spinner selectIdiomas;
    private Spinner selectCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_config);

        selectIdiomas = findViewById(R.id.spinnerIdiomas);
        selectCategorias = findViewById(R.id.spinnerCategorias);

        configurarSpinnerIdiomas();
        configurarSpinnerCategorias();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarSpinnerIdiomas() {
        List<String> idiomas = new ArrayList<>();
        idiomas.add("Português");
        idiomas.add("Inglês");

        // O ArrayAdapter liga a sua lista ao layout visual do Android
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, idiomas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectIdiomas.setAdapter(adapter);
    }

    private void configurarSpinnerCategorias() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Geral");
        categorias.add("Ciência");
        categorias.add("História");
        categorias.add("Geografia");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorias.setAdapter(adapter);
    }


    public void jogar(View v){
        Intent it_mainActivity = new Intent(this, MainActivity.class );
        startActivity(it_mainActivity);
    }
}