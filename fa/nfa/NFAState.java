package fa.nfa;

import java.util.Collection;
import java.util.HashMap;

import fa.State;

public class NFAState extends State{

    private HashMap<Character, Collection<String>> transitions; // map of characters to a list ofstrings representing states outgoing transitions
    private boolean startState; // boolean for if state is start state
    private boolean finalState; // boolean for if state is a final state
    
    public NFAState(String name, boolean isStartState, boolean isFinalState) {
        this.name = name;
        this.startState = isStartState;
        this.finalState = isFinalState;
        transitions = new HashMap<>();
    }

    /**
     * Set state to start state or not
     * @param isStartState boolean
     */
    public void setStartState(boolean isStartState){
        this.startState = isStartState;
    }
}
