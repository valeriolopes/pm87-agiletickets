package br.com.caelum.agiletickets.domain.precos;

import java.math.BigDecimal;

import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.agiletickets.models.TipoDeEspetaculo;

public class CalculadoraDePrecos {

	private static final double PERCENTUAL_MAXIMO_SHOW_CINEMA = 0.05d;
	private static final double PERCENTUAL_MAXIMO_BALLET_ORQUESTRA = 0.50d;
	
	private static final BigDecimal FATOR_MULTIPLICACAO_SHOW_CINEMA = BigDecimal.valueOf(0.10d);
	private static final BigDecimal FATOR_MULTIPLICACAO_BALLET_ORQUESTRA = BigDecimal.valueOf(0.20d);
	private static final BigDecimal FATOR_MULTIPLICACAO_TEMPO_SESSAO_BALLET_ORQUESTRA = BigDecimal.valueOf(0.10d);
	
	private static final int TEMPO_MINIMO_SESSAO_NAO_AUMENTAR_INGRESSO_BALLET_ORQUESTRA = 60;
	
	public static BigDecimal calcula(final Sessao sessao, final Integer quantidade) {
		
		if(sessao == null || quantidade == null || quantidade <= 0) {
			//TODO checked exception?
			throw new RuntimeException("Parametros Invalidos! A sessao deve ser valida e a quantidade de ingressos maior ou igual a 1");
		}
		
		BigDecimal preco = sessao.getPreco();
		final TipoDeEspetaculo tipoEspetaculo = sessao.getEspetaculo().getTipo();
		final double percentualIngressosDisponiveis = sessao.getPercentualIngressosDisponiveis();
		
		double percIngressosDispAumentaPrecos = 0;
		BigDecimal fatorAumentoPrecoPercIngressos = BigDecimal.ZERO, fatorAumentoPrecoTempoSessao = BigDecimal.ZERO;
		
		int tempoMinimoSessaoNaoAumentarPreco = 0;
		
		boolean verificaValores = true, verificaTempoSessao = false;
		
		switch(tipoEspetaculo) {
			case CINEMA:
			case SHOW:
				percIngressosDispAumentaPrecos = PERCENTUAL_MAXIMO_SHOW_CINEMA;
				fatorAumentoPrecoPercIngressos = FATOR_MULTIPLICACAO_SHOW_CINEMA;
				break;
			case BALLET:
			case ORQUESTRA:
				verificaTempoSessao = true;
				fatorAumentoPrecoTempoSessao = FATOR_MULTIPLICACAO_TEMPO_SESSAO_BALLET_ORQUESTRA;
				tempoMinimoSessaoNaoAumentarPreco = TEMPO_MINIMO_SESSAO_NAO_AUMENTAR_INGRESSO_BALLET_ORQUESTRA;
				percIngressosDispAumentaPrecos = PERCENTUAL_MAXIMO_BALLET_ORQUESTRA;
				fatorAumentoPrecoPercIngressos = FATOR_MULTIPLICACAO_BALLET_ORQUESTRA;
				break;
			default:
				verificaValores = false;
				break;
		}
		
		if(verificaValores && percentualIngressosDisponiveis <= percIngressosDispAumentaPrecos) { 
			preco = preco.add(preco.multiply(fatorAumentoPrecoPercIngressos));
		}
		
		if(verificaTempoSessao && sessao.getDuracaoEmMinutos() > tempoMinimoSessaoNaoAumentarPreco) {
			preco = preco.add(sessao.getPreco().multiply(fatorAumentoPrecoTempoSessao));
		}

		return preco.multiply(BigDecimal.valueOf(quantidade));
	}

}