package re;

import java.util.LinkedHashSet;
import java.util.Set;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

public class RE implements REInterface {

	String input = "";
	int count = 1;

	public RE(String regEx) {
		input = regEx;
	}
	
	private char peek(){
		return input.charAt(0);
	}
	private void eat(char c){
		if(peek() == c){
			this.input = this.input.substring(1);
		}	
	}
	private char next(){
		char c = peek();
		eat(c);
		return c;
	}		
	private boolean more(){
		return input.length()>0;
	}
	private NFA regex(){
		NFA term = term();
		if(more() && peek() == '|'){
			eat('|');
			NFA regex = regex();
			NFA addition = new NFA();
			addition.addStartState("q"+ count);
			String name = "q"+count;
			count++;
			addition.addNFAStates(term.getStates());
			addition.addNFAStates(regex.getStates());
			addition.addTransition(name, 'e', term.getStartState().getName());
			addition.addTransition(name, 'e', regex.getStartState().getName());
			addition.addAbc(term.getABC());
			addition.addAbc(regex.getABC());
			return addition;
		}
		else{
			return term;
		}
	}	
	private NFA term(){
		NFA factor = new NFA();
		while (more() && peek() != ')' && peek() != '|'){
			NFA nextFactor = factor();
			if(factor.getStates().size() == 0){
				factor = nextFactor;
			}
			else{
				factor = join(factor, nextFactor);
			}
		}
		return factor;
	}
	private NFA base(){
		switch(peek()){
			case '(':
				eat('(');
				NFA nfa = regex();
				eat(')');
				return nfa;
			default:
				NFA nfa2 = new NFA();
				String start ="q"+ count;
				count++;
				String finals ="q"+ count;
				count++;
				nfa2.addStartState(start);
				nfa2.addFinalState(finals);
				char in = next();
				nfa2.addTransition(start, in, finals);
				Set<Character> abc = new LinkedHashSet<Character>();
				abc.add(in);
				nfa2.addAbc(abc);
				return nfa2;
		}
	}
	private NFA factor(){
		NFA base = base();
		while(more() && peek() == '*'){
			eat('*');
			base = handleAsterisk(base);
		}
		return base;
	}
	private NFA join(NFA one, NFA two){
		one.addNFAStates(two.getStates());
		for(State s: one.getFinalStates()){
			((NFAState)s).setNonFinal();
			one.addTransition(s.getName(), 'e', two.getStartState().getName());			
		}
		one.addAbc(two.getABC());
		return one;
	}
	private NFA handleAsterisk(NFA base){
		NFA nfa = new NFA();
		String s = "q"+count;
		count++;
		String f = "q"+count;
		count++;
		nfa.addStartState(s);
		nfa.addFinalState(f);
		nfa.addNFAStates(base.getStates());
		nfa.addTransition(s,'e',f);
		nfa.addTransition(f,'e',base.getStartState().getName());
		nfa.addTransition(s,'e',base.getStartState().getName());
		nfa.addAbc(base.getABC());
		for(State s1: base.getFinalStates()){
			nfa.addTransition(s1.getName(), 'e', f);
			for(State s2: nfa.getFinalStates()){
				if(s2.getName().equals(s1.getName())){
					((NFAState)s2).setNonFinal();
				}
			}
		}
		return nfa;
	}

	@Override
	public NFA getNFA() {
		// TODO Auto-generated method stub
		return regex();
	}
}
