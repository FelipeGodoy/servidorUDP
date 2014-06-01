package servidorudp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jessica
 */
public class Jogador {

    ArrayList<Peca> pecasDoJogador;
    int ordem;
    boolean podeJogar;
    InetAddress ip;
    int porta;
//    public Socket socket;
//    Scanner entrada;
//    PrintWriter saida;
    int pontuacao;
    static int cont = 0;
    char equipe;
    int id;

    Jogador() {
        this.podeJogar = false;
        this.pecasDoJogador = null;
    }

    Jogador(ArrayList<Peca> pecasDoJogador) {
        this.podeJogar = false;
        this.pecasDoJogador = pecasDoJogador;
    }

    Jogador(InetAddress ip, int porta) throws IOException {
        this.ip = ip;
        this.porta = porta;
        System.out.println("Nova conexão com o cliente de IP: " + ip + ", " + porta);
        this.ordem = 1;
        this.id = cont;
        cont ++;
    }

    int getOrdem() {
        return this.ordem;
    }

    String imprimirPecasJogador() {
        String listaPecas = "";
        for (Peca peca : this.pecasDoJogador) {
            listaPecas = listaPecas + peca.toString() + ",";
        }
        return listaPecas;
    }

    void removerPeca(Peca p) {
        for (Peca peca : this.pecasDoJogador) {
            if (peca.equals(p)) {
                this.pecasDoJogador.remove(peca);
                break;
            }
        }
    }
    
    boolean contemPeca(Peca p){
   
        for(Peca peca : this.pecasDoJogador ){


            if((peca.parteDireita==p.parteDireita)&&(peca.parteEsquerda==p.parteEsquerda)){
//                System.out.println("Peca do jogador:"+peca.toString()+" carroça: "+p.toString());
                return true;
                
            }
        }
         return false;
        
    }
}
