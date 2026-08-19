package com.ecogame.controller;

public class Pontuacao {

    private int pontos = 0;
    private int vidas = 3;

    private static final int PONTOS_POR_ACERTO = 10;

    public void registrarAcerto() {
        pontos += PONTOS_POR_ACERTO;
    }

    public void reiniciar() {
        pontos = 0;
        vidas = 3;
    }

    public void registrarErro() {
        vidas--;
    }

    public boolean jogoAcabou() {
        return vidas <= 0;
    }

    public int getPontos() {
        return pontos;
    }

    public int getVidas() {
        return vidas;
    }
}
