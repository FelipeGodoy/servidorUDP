package servidorudp;

/**
 *
 * @author jessica
 */
public class TipoMensagem
{
  static final int ID_MENSAGEM_INICIAL = 0;
  //0#idDoJogador#pecasDoJogador#PecasDisponiveisParaCompra
  static final int ID_MENSAGEM_INFORMAR_JOGADOR_DA_VEZ = 1;
  //1#idDoJogador
  static final int ID_MENSAGEM_INFORMAR_JOGADA = 2;
  //2#posicaoQueAPecaSeraInserida#Peca#QuantidadeDePecasJogadores
  //obs: posiç�o � 1 se for pra inserir na direita e 0 se for pra inserir na esquerda
  static final int ID_MENSAGEM_QTD_PECAS_COMPRADAS = 3;
  //3#1
  static final int ID_MENSAGEM_VENCEDOR_PARTIDA = 4;
  //4#idJogador#pontuacao
  static final int ID_MENSAGEM_VENCEDOR_JOGO = 5;
  static final int ID_MENSAGEM_ACK = 6;
}