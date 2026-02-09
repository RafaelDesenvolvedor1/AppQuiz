package com.example.appquiz;

public class CategoriaItem {
public int id;
public String nome;

public CategoriaItem(int id, String nome) { this.id = id; this.nome = nome; }

@Override
public String toString() { return nome; }
}
