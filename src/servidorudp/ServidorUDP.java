package servidorudp;

import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.BindException;
//import java.net.ServerSocket;
//import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.Scanner;
import javax.swing.JOptionPane;
import socketUDP.MyServerSocket;
import socketUDP.MyServerSocketListener;
import socketUDP.MySocket;

/**
 *
 * @author jessica
 */
public class ServidorUDP {

    public class ServerListener implements MyServerSocketListener {

        @Override
        public void onNewConnection(MySocket mySocket) {
            quantidadeJogadores++;
            jogadoresConectados.add(new Jogador(mySocket));
            if (jogadoresConectados.size() == 4) {
                c = new ControladorJogo(jogadoresConectados);
                jogadores = c.getJogadoresOrdenados();
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
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
    }

    private Thread thread;

    public ArrayList<Jogador> jogadores;
    public ArrayList<Jogador> jogadoresConectados;
    ControladorJogo c;
    int pontuacaoPartida = 0, pontuacaoEquipeA = 0, pontuacaoEquipeB = 0;
    int quantidadeJogadores = 0;

    ServidorUDP() {
//        try {
            jogadoresConectados = new ArrayList();
            MyServerSocket socketDeEscuta = new MyServerSocket(40000, new ServerListener());
//
//            while (true) {
//                Socket socketDeConexao = socketDeEscuta.accept();
//                this.quantidadeJogadores++;
//                if (this.quantidadeJogadores < 5) {
//                    Scanner entrada = new Scanner(socketDeConexao.getInputStream());
//                    PrintWriter saida = new PrintWriter(socketDeConexao.getOutputStream());
//                    jogadoresConectados.add(new Jogador(socketDeConexao, entrada, saida));
//                    if (jogadoresConectados.size() == 4) {
//                        this.c = new ControladorJogo(jogadoresConectados);
//                        jogadores = c.getJogadoresOrdenados();
//                        iniciarJogo();
//                    }
//                } else {
//                    socketDeConexao.close();
//                }
//            }
//        } catch (BindException e) {
//            JOptionPane.showMessageDialog(null, "Erro: Esta porta est� ocupada por outro programa. (" + e.getMessage() + ")");
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(null, "Ocorreu um erro: " + ex.getMessage());
//        }
    }

    private void iniciarJogo() throws IOException, InterruptedException {
        enviarMensagemInicial(this.c);
        while (true) {
            if (pontuacaoEquipeA < 7 && pontuacaoEquipeB < 7) {
                iniciarPartida();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ServidorUDP();
    }

    private void enviarMensagemAoJogador(Jogador j, String mensagem) throws IOException {
        j.mySocket.enviarMensagem(mensagem);
    }

    private void informarQueOJogadorComprouPecas(Jogador jogadorQueComprouPeca, int numeroPecasCompradas) throws IOException {
        for (Jogador j : jogadores) {
            if (j != jogadorQueComprouPeca) {
                enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_QTD_PECAS_COMPRADAS + "#" + numeroPecasCompradas + "#" + this.c.quantidadeDePecasOutrosJogadores(j.id));
            }
        }
    }

    private void informarJogadaParaTodosOsJogadores(String[] itensJogada, Jogador jogadorQueJogouPeca) throws IOException {
        String posicao = itensJogada[1];
        Peca p = new Peca(itensJogada[2]);
        //se comprou peças
        if (itensJogada.length > 4) {
            aumentarNumeroDePecasJogador(itensJogada[4], jogadorQueJogouPeca);
            informarQueOJogadorComprouPecas(jogadorQueJogouPeca, itensJogada[4].split(",").length);
        }
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_INFORMAR_JOGADA + "#" + posicao + "#" + p.toString() + "#" + this.c.quantidadeDePecasOutrosJogadores(j.id));
        }
    }

    private void informarVitoriaPartidaParaTodosOsJogadores(int idJogador, String[] itensJogada) throws IOException {
        calcularPontuacaoPartida(itensJogada);
        char equipe = 'A';
        if (idJogador % 2 == 0) {
            pontuacaoEquipeA += pontuacaoPartida;
            equipe = 'A';
        } else {
            pontuacaoEquipeB += pontuacaoPartida;
            equipe = 'B';
        }
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_VENCEDOR_PARTIDA + "#" + idJogador + "#" + this.pontuacaoPartida + "#" + pontuacaoEquipes());
        }
    }

    public String pontuacaoEquipes() {
        return "Equipe A: " + pontuacaoEquipeA + " pontos" + " Equipe B: " + pontuacaoEquipeB + "pontos";
    }

    public void enviarMensagemInicial(ControladorJogo c) throws IOException {
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_INICIAL + "#" + j.id + "#" + j
                    .imprimirPecasJogador() + "#" + c.imprimirPecasDisponiveis() + "#" + j.equipe);
        }
    }

    public void informarJogadorDaVez(int idJogadorDaVez) throws IOException {
        for (Jogador j : jogadores) {
            enviarMensagemAoJogador(j, TipoMensagem.ID_MENSAGEM_INFORMAR_JOGADOR_DA_VEZ + "#" + idJogadorDaVez);
        }
    }

    private void aumentarNumeroDePecasJogador(String pecasCompradas, Jogador jogadorQueComprouPecas) {
        String[] pecas = pecasCompradas.split(",");
        for (int i = 0; i < pecas.length; i++) {
            this.c.comprarPeca(jogadorQueComprouPecas, new Peca(pecas[i]));
        }
    }

    private void iniciarPartida() throws IOException, InterruptedException {

        for (int i = 0; i < 4; i = (i + 1) % 4) {
            informarJogadorDaVez(i);
            String jogada = jogadores.get(i).mySocket.receberMensagem();
            String[] itensJogada = jogada.split("#");
            //Se o Jogador jogou peça na mesa
            if (itensJogada[0].equals("0")) {
                //0,peca, posicao
                this.c.inserirPecaMesa(new Peca(itensJogada[2]), itensJogada[1], jogadores.get(i));
                informarJogadaParaTodosOsJogadores(itensJogada, jogadores.get(i));
                if (jogadores.get(i).pecasDoJogador.isEmpty()) {
                    informarVitoriaPartidaParaTodosOsJogadores(jogadores.get(i).id, itensJogada);
                }
            } //Se o jogador passou a vez
            else if (itensJogada[0].equals("1")) {
                //se ele comprou peças
                if (itensJogada.length == 2 && !itensJogada[1].equals("")) {
                    aumentarNumeroDePecasJogador(itensJogada[1], jogadores.get(i));
                    informarQueOJogadorComprouPecas(jogadores.get(i), (itensJogada[1].split(",")).length);
                }
            } //clicou em nova partida
            else if (itensJogada[0].equals("2")) {
                break;
            }
        }
    }

    private int calcularPontuacaoPartida(String[] itensJogada) {
        Peca pecaExtremidadeEsquerda = new Peca(itensJogada[3].split(",")[0]);
        Peca pecaExtremidadeDireita = new Peca(itensJogada[3].split(",")[1]);
        Peca ultimaPeca = new Peca(itensJogada[2]);
        String posicao = itensJogada[1];
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
        return pontuacaoPartida;
    }

}
