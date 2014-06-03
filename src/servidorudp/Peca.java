package servidorudp;

/**
 *
 * @author jessica
 */
public class Peca {

    int parteEsquerda;
    int parteDireita;

    Peca(int parteEsquerda, int parteDireita) {
        this.parteEsquerda = parteEsquerda;
        this.parteDireita = parteDireita;
    }

    Peca(String partesPeca) {
        String[] itensPartesPeca = partesPeca.split("|");
        this.parteEsquerda = Integer.parseInt(itensPartesPeca[1]);
        this.parteDireita = Integer.parseInt(itensPartesPeca[3]);
    }

    public String toString() {
        return this.parteEsquerda + "|" + this.parteDireita;
    }

    public boolean equals(Peca p) {
        if (((this.parteDireita == p.parteDireita) && (this.parteEsquerda == p.parteEsquerda)) || ((this.parteEsquerda == p.parteDireita) && (this.parteDireita == p.parteEsquerda))) {
            return true;
        }
        return false;
    }
    
    public boolean carroca(){
        return this.parteDireita==this.parteEsquerda;
    }
    
}
