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
    
   
    private void capturarItem(TipoLixo tipoEscolhido) {
    if (itens.isEmpty()) {
        return;
    }
    ItemLixo primeiro = itens.remove(0);
    if (primeiro.getTipo() == tipoEscolhido) {
        pontuacao.registrarAcerto();
    } else {
        pontuacao.registrarErro();
    }
    }
        


    private void iniciarLoops() {
        timerJogo = new Timer(16, e -> atualizarJogo());
        timerJogo.start();

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
        desenharIconeItem(g2, item);
    }
    }

    /*/* */

    private void desenharIconeItem(Graphics2D g2, ItemLixo item) {
        int x = (int) item.getX();
        int y = (int) item.getY();
        int tam = item.getTamanho();

        switch (item.getTipo()) {
            case PAPEL -> desenharPapel(g2, x, y, tam);
            case VIDRO -> desenharVidro(g2, x, y, tam);
            case PLASTICO -> desenharPlastico(g2, x, y, tam);
            case ORGANICO -> desenharOrganico(g2, x, y, tam);
        }
    }

    private void desenharPapel(Graphics2D g2, int x, int y, int tam) {
        int dobra = 10;
        Polygon folha = new Polygon();
        folha.addPoint(x, y);
        folha.addPoint(x + tam - dobra, y);
        folha.addPoint(x + tam, y + dobra);
        folha.addPoint(x + tam, y + tam);
        folha.addPoint(x, y + tam);

        g2.setColor(new Color(250, 244, 224));
        g2.fillPolygon(folha);
        g2.setColor(new Color(230, 168, 55));
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(folha);
        g2.drawLine(x + tam - dobra, y, x + tam - dobra, y + dobra);
        g2.drawLine(x + tam - dobra, y + dobra, x + tam, y + dobra);

        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i <= 3; i++) {
            int ly = y + dobra + i * 6;
            g2.drawLine(x + 5, ly, x + tam - 6, ly);
        }
    }

    private void desenharVidro(Graphics2D g2, int x, int y, int tam) {
        int corpoX = x + tam / 4;
        int corpoLargura = tam / 2 + 2;
        int corpoY = y + tam / 3;
        int corpoAltura = tam - tam / 3;

        g2.setColor(new Color(70, 130, 200, 220));
        g2.fillRoundRect(corpoX, corpoY, corpoLargura, corpoAltura, 8, 8);
        g2.fillRect(x + tam / 2 - 4, y, 8, tam / 3 + 2);

        g2.setColor(new Color(35, 85, 145));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(corpoX, corpoY, corpoLargura, corpoAltura, 8, 8);
        g2.drawRect(x + tam / 2 - 4, y, 8, tam / 3 + 2);
    }

    private void desenharPlastico(Graphics2D g2, int x, int y, int tam) {
        int corpoX = x + tam / 5;
        int corpoLargura = (int) (tam * 0.6);
        int corpoY = y + tam / 4;
        int corpoAltura = tam - tam / 4;

        g2.setColor(new Color(60, 170, 110, 220));
        g2.fillRoundRect(corpoX, corpoY, corpoLargura, corpoAltura, 12, 12);

        g2.setColor(new Color(230, 168, 55));
        g2.fillRect(x + tam / 2 - 5, y, 10, tam / 4 + 2);

        g2.setColor(new Color(25, 115, 75));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(corpoX, corpoY, corpoLargura, corpoAltura, 12, 12);
    }

    private void desenharOrganico(Graphics2D g2, int x, int y, int tam) {
        g2.setColor(new Color(150, 90, 180));
        g2.fillOval(x, y + 6, tam, tam - 6);
        g2.setColor(new Color(95, 55, 120));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y + 6, tam, tam - 6);

        g2.setColor(new Color(110, 75, 40));
        g2.drawLine(x + tam / 2, y + 6, x + tam / 2, y);

        g2.setColor(new Color(90, 150, 70));
        g2.fillOval(x + tam / 2, y - 2, 10, 6);
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
