package re;

import java.util.Set;

import fa.State;
import fa.nfa.NFA;

public class RE implements REInterface {

	String input = "";
	int count = 0;

	public RE(String regEx) {
		input = regEx;
	}

	@Override
	public NFA getNFA() {
		return regex();
	}

	private NFA term() {
		NFA factor = new NFA();

		while (more() && peek() != ')' && peek() != '|') {
			NFA nextFactor = factor() ;
			if(factor.getStates().isEmpty()){
				factor = nextFactor;
			//If there are multiple terms following each other, perform concatenate operation on the NFAs
			}else{
				factor = sequence(factor, nextFactor);

			}
		}
		}

		return factor ;
		
		
	private NFA sequence(NFA factor, NFA nextFactor) {
		
		Set<State> factorF = factor.getFinalStates();
		String nextFactorI = nextFactor.getStartState().getName();

		//Add all states from second NFA to first NFA
		factor.addNFAStates(nextFactor.getStates());
		
		//Make sure first NFA's final states are not final
		//But add their transitions to begining of second NFA
		for(State state: factorF) {
			((NFAState)state).setNonFinal();
			factor.addTransition(state.getName(), 'e', nextFactorI);
		}
		
		//Make sure both alphabets are included
		factor.addAbc(nextFactor.getABC());
		
		return factor;
	}

	}

	private NFA factor() {

		NFA base = base() ;

		while (more() && peek() == '*') {
			eat('*') ;
			base = star(base);
		}

		return base ;
	}

	private NFA star(NFA base) {
		// TODO Auto-generated method stub
		return null;
	}

	private NFA base() {
		switch (peek()) {
		case '(':
			eat('(') ;
			NFA r = regex() ;  
			eat(')') ;
			return r ;

		default:
			return nfas(next()) ;
		}
	}

	private NFA regex() {
		// TODO Auto-generated method stub
		return null;
	}

	private char peek() {
		return input.charAt(0) ;
	}

	private void eat(char c) {
		if (peek() == c)
			this.input = this.input.substring(1) ;
		else
			throw new 
			RuntimeException("Expected: " + c + "; got: " + peek()) ;
	}

	private char next() {
		char c = peek() ;
		eat(c) ;
		return c ;
	}

	private boolean more() {
		return input.length() > 0 ;
	}

}
