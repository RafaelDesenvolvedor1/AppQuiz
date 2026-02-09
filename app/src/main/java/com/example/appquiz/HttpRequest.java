package com.example.appquiz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    private String urlDestino;

    // Construtor que recebe a URL
    public HttpRequest(String url) {
        this.urlDestino = url;
    }

    // Método que faz o processo e retorna a String JSON
    public String executar() throws Exception {
        URL url = new URL(urlDestino);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.connect();

        if (conexao.getResponseCode() != 200) {
            throw new Exception("Erro na conexão: " + conexao.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conexao.getInputStream())
        );

        StringBuilder json = new StringBuilder();
        String linha;

        while ((linha = reader.readLine()) != null) {
            json.append(linha);
        }

        reader.close();
        conexao.disconnect();

        return json.toString();
    }
}