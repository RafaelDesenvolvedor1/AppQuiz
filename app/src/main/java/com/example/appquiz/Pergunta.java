package com.example.appquiz;

import java.util.List;

public class Pergunta {
    private String pergunta;
    private String correta;
    private List<String> alternativas;

    public Pergunta(String pergunta, String correta, List<String> alternativas) {
        this.pergunta = pergunta;
        this.correta = correta;
        this.alternativas = alternativas;
    }

    public String getPergunta() { return pergunta; }
    public String getCorreta() { return correta; }
    public List<String> getAlternativas() { return alternativas; }
}
