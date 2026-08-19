package com.ecogame.model;

/**
 * Representa um item de lixo que cai do topo da tela.
 * Guarda posição, tipo e velocidade de queda.
 */
public class ItemLixo {

    private double x;
    private double y;
    private final double velocidade;
    private final TipoLixo tipo;
    private final int tamanho = 34;

    public ItemLixo(double x, double y, double velocidade, TipoLixo tipo) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.tipo = tipo;
    }

    /** Atualiza a posição vertical do item, simulando a queda. */
    public void cair() {
        this.y += velocidade;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public TipoLixo getTipo() {
        return tipo;
    }

    public int getTamanho() {
        return tamanho;
    }
}
