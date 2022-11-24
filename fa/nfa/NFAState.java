package fa.nfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import fa.State;

public class NFAState extends State{

    private HashMap<Character, ArrayList<String>> transitions; // map of characters to a list ofstrings representing states outgoing transitions
    private boolean startState; // boolean for if state is start state
    private boolean finalState; // boolean for if state is a final state
    
    public NFAState(String name, boolean isStartState, boolean isFinalState) {
        this.name = name;
        this.startState = isStartState;
        this.finalState = isFinalState;
        transitions = new HashMap<>();
    }

    /**
     * Adds a transition to the state list
     * @param onSymb char symbol for transition to be added
     * @param toState String name of state that transition ends on
     */
    public void addTransition(char onSymb, String toState){
        ArrayList<String> stateList = transitions.get(onSymb);
        if(stateList == null) {
            stateList = new ArrayList<>();
            transitions.put(onSymb, stateList);
        }
        
        stateList.add(toState);
    }

    /**
     * returns name of state resulting from transition on a symbol
     * @param c char transition symbol
     * @return String name of resulting state
     */
    public ArrayList<String> getTransitions(char c) {
        ArrayList<String> nextStates = this.transitions.get(c);
        if(nextStates == null){
            nextStates = new ArrayList<String>();
            nextStates.add("");
        }
        return nextStates;
    }

    
    /**
     * returns name of state 
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * returns true if state is a start state
     * @return boolean
     */
    public boolean isStartState() {
        return startState;
    }

    /**
     * returns true if state is a final state
     * @return boolean
     */
    public boolean isFinalState() {
        return finalState;
    }

    /**
     * Set state to start state or not
     * @param isStartState boolean
     */
    public void setStartState(boolean isStartState){
        this.startState = isStartState;
    }
}
