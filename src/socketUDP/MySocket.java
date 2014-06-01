
package socketUDP;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MySocket {
    private InetAddress ipDest;
    private int portaDest;
    private int portaOrig;
    private Thread threadConexao;
    
    //Cache
    private String stringEntrada;
    private String stringSaida;

    public MySocket(InetAddress ipDest, int portaDest, int portaOrig) {
        this.ipDest = ipDest;
        this.portaDest = portaDest;
        this.portaOrig = proximaPortaAberta(portaOrig);
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
