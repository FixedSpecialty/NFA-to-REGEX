package re;

import java.util.LinkedHashSet;
import java.util.Set;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

/**
 * Recursive Descent Parser for a Regex to an NFA
 * @author Andre Murphy and Josh Dixon
 *
 */
public class RE implements REInterface {

	String input = "";	
	Integer stateNum = 0; //to iterate through regex and assign # value

	/**
	 * Constructor for regular expression
	 * @param regEx
	 */
	public RE(String regEx) {
		input = regEx; //save input globally
	}
	
	/**
	 * returns the next item of input without consuming it
	 * @return
	 */
	private char peek(){
		return input.charAt(0);
	}
	
	/**
	 * consumes the next item of input, failing if not equal to item
	 * @param c
	 */
	private void eat(char c){
		if(peek() == c){
			this.input = this.input.substring(1);
		}	
		else
			throw new RuntimeException("Expected: " + c + "; got: " + peek());
	}
	
	/**
	 * returns the next item of input and consumes it
	 * @return
	 */
	private char next(){
		char c = peek();
		eat(c);
		return c;
	}		
	
	/**
	 * checks if there is more input available
	 * @return
	 */
	private boolean more(){
		return input.length()>0;
	}
	
	/**
	 * Constructs and NFA from given start states, final states and creates transitions
	 * @return new NFA if more, old NFA if not
	 */
	private NFA regex(){
		NFA term = term();
		if(more() && peek() == '|'){
			eat('|');
			NFA regex = regex(); //recursive call
			NFA addition = new NFA(); //create new NFA
			String start = stateNum.toString(); 
			addition.addStartState(start); //add start state
			stateNum++;
			addition.addNFAStates(term.getStates()); //add states from term
			addition.addNFAStates(regex.getStates()); //add states from recursive call
			addition.addTransition(start, 'e', regex.getStartState().getName()); //add transition from other NFAs
			addition.addTransition(start, 'e', term.getStartState().getName()); //add transition from other NFAs
			addition.addAbc(term.getABC()); //add OLD alphabet
			addition.addAbc(regex.getABC());
			return addition;
		}
		else{
			return term;
		}
	}
	
	/**
	 * has to check that it has not reached the boundary of a term or the end of the input
	 * @return NFA
	 */
	private NFA term(){
		NFA factor = new NFA();
		while (more() && peek() != ')' && peek() != '|'){
			NFA nextFactor = factor();
			if(factor.getStates().size() == 0){
				factor = nextFactor;
			}
			else{
				factor = join(factor, nextFactor); //concatenate both NFAs
			}
		}
		return factor;
	}
	
	/**
	 * this is a regex in ( )
	 * @default 
	 * @return
	 */
	private NFA base(){
		switch(peek()){
			case '(':
				eat('(');
				NFA nfa = regex();
				eat(')');
				return nfa;
			default: //constructs an NFA from next char with two states
				NFA nfa2 = new NFA();
				stateNum++;
				String start = stateNum.toString(); //locate start state from counter
				stateNum++;
				String finals = stateNum.toString(); //locate final state from counter
				nfa2.addStartState(start);
				stateNum++;
				nfa2.addFinalState(finals);
				char in = next();
				nfa2.addTransition(start, in, finals);
				Set<Character> abc = new LinkedHashSet<Character>(); //create alphabet for NFA
				abc.add(in);
				nfa2.addAbc(abc);
				return nfa2;
		}
	}
	
	/**
	 * parse a base and then any number of Kleene stars
	 * @return
	 */
	private NFA factor(){
		NFA base = base();
		while(more() && peek() == '*'){
			eat('*');
			base = handleAsterisk(base);
		}
		return base;
	}
	
	/**
	 * concatenate two NFAs
	 * @param first NFA: one
	 * @param second NFA: two
	 * @return combined NFA
	 */
	private NFA join(NFA one, NFA two){
		Set<State> finals = one.getFinalStates();
		one.addNFAStates(two.getStates());		
		for(State s: finals){
			((NFAState)s).setNonFinal();
			one.addTransition(s.getName(), 'e', two.getStartState().getName());			
		}
		one.addAbc(two.getABC());
		return one;
	}
	
	/**
	 * Handles the case of (A*)= e or A,AA,AAA,etc.
	 * @param base
	 * @return
	 */
	private NFA handleAsterisk(NFA base){
		NFA nfa = new NFA();
		String s = stateNum.toString(); //locate start state with counter
		stateNum++;
		String f = stateNum.toString(); //locate final state with counter
		stateNum++;
		nfa.addStartState(s); //add to NFA
		nfa.addFinalState(f); //add to NFA
		nfa.addNFAStates(base.getStates());
		nfa.addTransition(s,'e',f); //add empty transition
		nfa.addTransition(f,'e',base.getStartState().getName()); //add empty transition
		nfa.addTransition(s,'e',base.getStartState().getName()); //link initial state to base NFA
		nfa.addAbc(base.getABC()); //add old alphabet
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

	/**
	 * Constructs NFA from regex using recursive descent
	 */
	public NFA getNFA() {
		return regex();
	}
}
