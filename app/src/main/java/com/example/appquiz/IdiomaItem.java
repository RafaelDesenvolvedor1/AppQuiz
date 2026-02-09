package com.example.appquiz;

public class IdiomaItem {
    public String codigo;
    public String nome;

    public IdiomaItem(String codigo, String nome) { this.codigo = codigo; this.nome = nome; }

    @Override
    public String toString() { return nome; }
}
