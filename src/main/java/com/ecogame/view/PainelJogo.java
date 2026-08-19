package com.ecogame.view;

import com.ecogame.controller.Pontuacao;
import com.ecogame.model.ItemLixo;
import com.ecogame.model.Lixeira;
import com.ecogame.model.TipoLixo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Painel principal onde o jogo acontece: gera os itens de lixo,
 * atualiza suas posições e verifica colisões com as lixeiras.
 */
public class PainelJogo extends JPanel {

    private static final int LARGURA = 700;
    private static final int ALTURA = 460;
    private static final int Y_LIXEIRAS = 350;

    private final List<ItemLixo> itens = new ArrayList<>();
    private final List<Lixeira> lixeiras = new ArrayList<>();
    private final Pontuacao pontuacao = new Pontuacao();
    private final Random random = new Random();

    private Timer timerJogo;
    private Timer timerGeracao;

    public PainelJogo() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(235, 245, 235));
        setFocusable(true);

        criarLixeiras();
        configurarTeclado();
        iniciarLoops();

        // Garante que um clique no painel devolva o foco do teclado,
        // caso ele tenha se perdido por algum motivo.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private void criarLixeiras() {
        lixeiras.add(new Lixeira(TipoLixo.PAPEL, 20));
        lixeiras.add(new Lixeira(TipoLixo.VIDRO, 185));
        lixeiras.add(new Lixeira(TipoLixo.PLASTICO, 350));
        lixeiras.add(new Lixeira(TipoLixo.ORGANICO, 515));
    }

    private void configurarTeclado() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                TipoLixo tipoEscolhido = teclaParaTipo(e.getKeyCode());
                if (tipoEscolhido != null) {
                    capturarItem(tipoEscolhido);
                }
            }
        });
    }

    private TipoLixo teclaParaTipo(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_A -> TipoLixo.PAPEL;
            case KeyEvent.VK_S -> TipoLixo.VIDRO;
            case KeyEvent.VK_D -> TipoLixo.PLASTICO;
            case KeyEvent.VK_F -> TipoLixo.ORGANICO;
            default -> null;
        };
    }

    /**
     * Captura o item mais próximo da faixa das lixeiras que combine
     * com o tipo escolhido pelo jogador.
     */
    private void capturarItem(TipoLixo tipoEscolhido) {
        ItemLixo alvo = null;
        for (ItemLixo item : itens) {
            if (item.getTipo() == tipoEscolhido && item.getY() > Y_LIXEIRAS - 120) {
                alvo = item;
                break;
            }
        }
        if (alvo != null) {
            itens.remove(alvo);
            pontuacao.registrarAcerto();
        } else {
            pontuacao.registrarErro();
        }
    }

    private void iniciarLoops() {
        // Atualiza a posição dos itens ~60 vezes por segundo
        timerJogo = new Timer(16, e -> atualizarJogo());
        timerJogo.start();

        // Gera um novo item a cada 1.2 segundos
        timerGeracao = new Timer(1200, e -> gerarItem());
        timerGeracao.start();
    }

    private void gerarItem() {
        TipoLixo[] tipos = TipoLixo.values();
        TipoLixo tipoSorteado = tipos[random.nextInt(tipos.length)];
        double xSorteado = 40 + random.nextInt(LARGURA - 120);
        itens.add(new ItemLixo(xSorteado, -30, 3.0, tipoSorteado));
    }

    private void atualizarJogo() {
        for (ItemLixo item : itens) {
            item.cair();
        }

        // Remove itens que passaram da linha das lixeiras sem ser capturados
        itens.removeIf(item -> {
            if (item.getY() > Y_LIXEIRAS) {
                pontuacao.registrarErro();
                return true;
            }
            return false;
        });

        if (pontuacao.jogoAcabou()) {
            timerJogo.stop();
            timerGeracao.stop();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        desenharHud(g2);
        desenharItens(g2);
        desenharLixeiras(g2);

        if (pontuacao.jogoAcabou()) {
            desenharFimDeJogo(g2);
        }
    }

    private void desenharHud(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Pontos: " + pontuacao.getPontos(), 20, 30);
        g2.drawString("Vidas: " + pontuacao.getVidas(), 580, 30);
    }

    private void desenharItens(Graphics2D g2) {
        for (ItemLixo item : itens) {
            g2.setColor(corParaTipo(item.getTipo()));
            g2.fillOval((int) item.getX(), (int) item.getY(), item.getTamanho(), item.getTamanho());
        }
    }

    private void desenharLixeiras(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        for (Lixeira lixeira : lixeiras) {
            g2.setColor(corParaTipo(lixeira.getTipoAceito()));
            g2.fillRoundRect(lixeira.getX(), Y_LIXEIRAS, lixeira.getLargura(), lixeira.getAltura(), 12, 12);
            g2.setColor(Color.WHITE);
            g2.drawString(lixeira.getTipoAceito().name(), lixeira.getX() + 20, Y_LIXEIRAS + 30);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("tecla " + teclaDoTipo(lixeira.getTipoAceito()), lixeira.getX() + 20, Y_LIXEIRAS + 50);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        }

        desenharLegendaTeclas(g2);
    }

    private void desenharLegendaTeclas(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String legenda = "Controles: A = Papel | S = Vidro | D = Plástico | F = Orgânico";
        g2.drawString(legenda, 20, ALTURA - 12);
    }

    private String teclaDoTipo(TipoLixo tipo) {
        return switch (tipo) {
            case PAPEL -> "A";
            case VIDRO -> "S";
            case PLASTICO -> "D";
            case ORGANICO -> "F";
        };
    }

    private void desenharFimDeJogo(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, LARGURA, ALTURA);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString("Fim de jogo! Pontos: " + pontuacao.getPontos(), 150, ALTURA / 2);
    }

    private Color corParaTipo(TipoLixo tipo) {
        return switch (tipo) {
            case PAPEL -> new Color(230, 168, 55);
            case VIDRO -> new Color(70, 130, 200);
            case PLASTICO -> new Color(60, 170, 110);
            case ORGANICO -> new Color(150, 90, 180);
        };
    }
}
