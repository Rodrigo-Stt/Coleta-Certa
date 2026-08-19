package com.ecogame.view;

import javax.swing.*;

/**
 * Janela principal da aplicação, responsável por hospedar o painel do jogo.
 */
public class JanelaJogo extends JFrame {

    private final PainelJogo painelJogo;

    public JanelaJogo() {
        setTitle("Coleta Certa - Educação Ambiental");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        painelJogo = new PainelJogo();
        add(painelJogo);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * O foco só pode ser pedido depois que a janela está visível,
     * senão o KeyListener do painel não recebe os eventos de teclado.
     */
    @Override
    public void setVisible(boolean visivel) {
        super.setVisible(visivel);
        if (visivel) {
            painelJogo.requestFocusInWindow();
        }
    }
}
