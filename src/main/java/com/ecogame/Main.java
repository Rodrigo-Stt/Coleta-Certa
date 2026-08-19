package com.ecogame;

import com.ecogame.view.JanelaJogo;

import javax.swing.*;

/**
 * Ponto de entrada do jogo "Coleta Certa".
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaJogo janela = new JanelaJogo();
            janela.setVisible(true);
        });
    }
}
