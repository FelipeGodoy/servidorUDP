
package socketUDP;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySocket {
    private InetAddress ipDest;
    private int portaDest;
    private int portaOrig;
    private Thread threadConexao;
    private boolean conectado;
    
    //buffer
    private final int BUFFER_SIZE = 2;
    private LinkedList<String> bufferEntrada;
    private LinkedList<String> bufferSaida;
    private int ackEntrada;
    private int ackSaida;
    private boolean aguardandoAck;
    

    public MySocket(InetAddress ipDest, int portaDest, int portaOrig) {
        this.aguardandoAck = false;
        this.conectado = true;
        this.ipDest = ipDest;
        this.portaDest = portaDest;
        this.portaOrig = proximaPortaAberta(portaOrig);
        this.bufferEntrada = new LinkedList();
        this.bufferSaida = new LinkedList();
        this.ackEntrada = 0;
        this.ackSaida = 0;
    }
    
    private void update(){
        while(this.conectado){
            if(!this.aguardandoAck){
                
            }
        }
    }

    public InetAddress getIpDest() {
        return ipDest;
    }

    public void setIpDest(InetAddress ipDest) {
        this.ipDest = ipDest;
    }

    public int getPortaDest() {
        return portaDest;
    }

    public void setPortaDest(int portaDest) {
        this.portaDest = portaDest;
    }

    public int getPortaOrig() {
        return portaOrig;
    }

    public void setPortaOrig(int portaOrig) {
        this.portaOrig = portaOrig;
    }
    
    public String receberMensagem(){
        while(bufferEntrada.isEmpty()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MySocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return bufferEntrada.removeLast();
    }
    
    public void enviarMensagem(String mensagem){
        bufferSaida.addFirst(mensagem);
    }
    
    public static int proximaPortaAberta(int porta) {
        for (int i = porta; i < 65535; i++) {
            try {
                DatagramSocket ds = new DatagramSocket(i);
                ds.close();
                return i;
            } 
            catch (IOException ex) {}
        }
        return -1;
    }
    
}
