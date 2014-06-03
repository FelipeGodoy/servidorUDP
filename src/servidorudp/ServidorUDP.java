package servidorudp;

import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.Scanner;
import javax.swing.JOptionPane;
import socketUDP.*;

/**
 *
 * @author jessica
 */
public class ServidorUDP {

    public ArrayList<Jogador> jogadores;
    public ArrayList<Jogador> jogadoresConectados;
    ControladorJogo c;
    int pontuacaoPartida = 0;
    int pontuacaoJogo = 0;
    int quantidadeJogadores = 0;
    MyServerSocket socketDeEscuta;
    Thread thread;

    ServidorUDP() throws IOException {
        jogadoresConectados = new ArrayList();
        socketDeEscuta = new MyServerSocket(40000,new MyServerSocketListener() {
            @Override
            public void onNewConnection(MySocket mySocket) {
                quantidadeJogadores++;
                jogadoresConectados.add(new Jogador(mySocket));
                System.out.println("Jogador Conectado");
                if(quantidadeJogadores == 4){
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("Iniciando o Jogo");
                                iniciarJogo();
                            } catch (IOException ex) {
                                Logger.getLogger(ServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
//        try {
//            boolean loop = true;
//            while (loop) {
//                jogadoresConectados.size();
////                Socket socketDeConexao = socketDeEscuta.accept();
////                this.quantidadeJogadores++;
//                if (this.quantidadeJogadores < 5) {
//                    if (jogadoresConectados.size() == 4) {
//                        this.c = new ControladorJogo(jogadoresConectados);
//                        jogadores = c.getJogadoresOrdenados();
//                        iniciarJogo();
//                    }
//                } else {
////                    socketDeConexao.close();
//                }
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(null, "Erro:" + ex.getMessage());
//        }
    }

    private void iniciarJogo() throws IOException, InterruptedException {
        this.c = new ControladorJogo(jogadoresConectados);
        jogadores = c.getJogadoresOrdenados();
        enviarMensagemInicial(this.c);
        
        //TO DO: 
        // 1) reiniciar partida ap�s vitoria de um participante
        while (true) {
            for (int i = 0; i < 4; i = (i + 1) % 4) {
                informarJogadorDaVez(i); 
                String jogada = jogadores.get(i).socket.receberMensagem(); 
                String[] itensJogada = jogada.split("#");
//                Se o Jogador jogou peça na mesa
                if (itensJogada.length > 2) {
                    this.c.inserirPecaMesa(new Peca(itensJogada[1]), itensJogada[0], jogadores.get(i));
                    informarJogadaParaTodosOsJogadores(itensJogada, jogadores.get(i));
                    if (jogadores.get(i).pecasDoJogador.isEmpty()) {
                        informarVitoriaPartidaParaTodosOsJogadores(jogadores.get(i).id, itensJogada);
                    }       
                }
                //Se o jogador comprou peças e passou a vez
                 else if (itensJogada.length == 2) {
                    aumentarNumeroDePecasJogador(itensJogada[1], jogadores.get(i));
                    informarQueOJogadorComprouPecas(jogadores.get(i), (itensJogada[1].split(",")).length);
                 }
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        new ServidorUDP();
    }

    private void enviarMensagemAoJogador(Jogador j, String mensagem) throws IOException {
        j.socket.enviarMensagem(mensagem);
    }

    private void informarQueOJogadorComprouPecas(Jogador jogadorQueComprouPeca, int numeroPecasCompradas) throws IOException {
        for (Jogador j : jogadores) {
            if (j != jogadorQueComprouPeca) {
                enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_QTD_PECAS_COMPRADAS + "#" + numeroPecasCompradas);
                
            }
        }
    }

    private void informarJogadaParaTodosOsJogadores(String[] itensJogada, Jogador jogadorQueJogouPeca) throws IOException {
        String posicao = itensJogada[0];
        Peca p = new Peca(itensJogada[1]);
        
        //se comprou peças
        if (itensJogada.length > 3) {
            aumentarNumeroDePecasJogador(itensJogada[3], jogadorQueJogouPeca);
            informarQueOJogadorComprouPecas(jogadorQueJogouPeca, itensJogada[3].split(",").length);
        }
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_INFORMAR_JOGADA + "#" + posicao + "#" + p.toString() + "#" + this.c.quantidadeDePecasJogadores());
        }
    }

    private void informarVitoriaPartidaParaTodosOsJogadores(int idJogador, String[] itensJogada)throws IOException {
        calcularPontuacaoPartida(itensJogada);
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_VENCEDOR_PARTIDA + "#" + idJogador + "#" + this.pontuacaoPartida);
        }
    }

    public void enviarMensagemInicial(ControladorJogo c)
            throws IOException {
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_INICIAL + "#" + j.id + "#" + j
                    .imprimirPecasJogador() + "#" + c.imprimirPecasDisponiveis());
        }
    }

    public void informarJogadorDaVez(int idJogadorDaVez)
            throws IOException {
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_INFORMAR_JOGADOR_DA_VEZ + "#" + idJogadorDaVez);
        }
    }

    private void calcularPontuacaoPartida(String[] itensJogada) {
        Peca pecaExtremidadeEsquerda = new Peca(itensJogada[2].split(",")[0]);
        Peca pecaExtremidadeDireita = new Peca(itensJogada[2].split(",")[1]);
        Peca ultimaPeca = new Peca(itensJogada[1]);
        String posicao = itensJogada[0];
        boolean carroca = false;
        boolean serviuParaOsDoisLados = false;
        if (ultimaPeca.parteDireita == ultimaPeca.parteEsquerda) {
            carroca = true;
        }
        if (((posicao.equals("1")) && (ultimaPeca.parteDireita == pecaExtremidadeEsquerda.parteEsquerda)) || ((posicao.equals("0")) && (ultimaPeca.parteEsquerda == pecaExtremidadeDireita.parteDireita))) {
            serviuParaOsDoisLados = true;
        }
        if ((carroca) && (serviuParaOsDoisLados)) {
            this.pontuacaoPartida = 4;
        } else if (serviuParaOsDoisLados) {
            this.pontuacaoPartida = 3;
        } else if (carroca) {
            this.pontuacaoPartida = 2;
        } else {
            this.pontuacaoPartida = 1;
        }
    }

    private void aumentarNumeroDePecasJogador(String pecasCompradas, Jogador jogadorQueComprouPecas) {
                    String[] pecas = pecasCompradas.split(",");
                    for (int i = 0; i < pecas.length; i++) {
                        this.c.comprarPeca(jogadorQueComprouPecas, new Peca(pecas[i]));
                    }            
    }

}
