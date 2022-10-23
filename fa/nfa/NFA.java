package fa.nfa;

import java.util.HashSet;
import java.util.Set;

import fa.State;
import fa.dfa.DFA;

public class NFA implements NFAInterface {

    private Set<NFAState> states; // set of states in dfa
    private Set<Character> sigma; // set of symbols in language of dfa
    private NFAState qerror; //qerror state for if a bad transition is attempted

     /**
     * Constructor
     * instance the instance variables
     */
    public NFA() {
        this.states = new HashSet<>();
        this.sigma = new HashSet<>();

        qerror = new NFAState("qerror", false, false);
    }

    @Override
    public void addStartState(String name) {
        boolean alreadyExist = false;
        for(NFAState state : states) {
            if(state.getName().equals(name)){
                alreadyExist = true;
                state.setStartState(true);
            }
        }
        if(!alreadyExist) this.states.add(new NFAState(name, true, false));
    }

    @Override
    public void addState(String name) {
        boolean alreadyExist = false;
        for(NFAState state : states) 
            if(state.getName().equals(name)){
                alreadyExist = true;
                System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
            }

        if(!alreadyExist) this.states.add(new NFAState(name, false, false));
    }

    @Override
    public void addFinalState(String name) {
        boolean alreadyExist = false;
        for(NFAState state : states) 
            if(state.getName().equals(name)){
                alreadyExist = true;
                System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
            }
            
        if(!alreadyExist) this.states.add(new NFAState(name, false, true));
        
    }

    @Override
    public void addTransition(String fromState, char onSymb, String toState) {
        for(NFAState state: states){
            if(state.getName().equals(fromState)){
                state.addTransition(onSymb, toState);
            }
        }

        if(!sigma.contains(onSymb)){
            sigma.add(onSymb); //adds symbol to sigma if not already added
            qerror.addTransition(onSymb, "qerror");
        }
    }

    @Override
    public Set<? extends State> getStates() {
        return states;
    }

    @Override
    public Set<? extends State> getFinalStates() {
        Set<NFAState> finalStates = new HashSet<>();
        for(NFAState state: states)
            if(state.isFinalState()) finalStates.add(state);
        return finalStates;
    }

    @Override
    public State getStartState() {
        for(NFAState state: states)
            if(state.isStartState()) return state;
        return null;
    }

    @Override
    public Set<Character> getABC() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DFA getDFA() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<NFAState> eClosure(NFAState s) {
        // TODO Auto-generated method stub
        return null;
    }
}