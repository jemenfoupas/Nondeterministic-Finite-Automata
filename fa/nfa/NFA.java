package fa.nfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

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
        return sigma;
    }

    @Override
    public DFA getDFA() {
        //create return value
        DFA rtVal = new DFA();
        //create queue
        Queue<Set<NFAState>> queue = new LinkedList<Set<NFAState>>();
        //add set of start state to queue
        NFAState start = (NFAState)this.getStartState();
        Set<NFAState> startSet = new HashSet<NFAState>();
        startSet.add(start);
        queue.add(startSet);
        //create set of visited sets
        Set<Set<NFAState>> visited = new HashSet<Set<NFAState>>();
        visited.add(startSet);
        //variables for BFS
        boolean started = false;
        boolean fin = false;
        Set<NFAState> curr;
        Set<NFAState> queueSet;
        Set<NFAState> next = new HashSet<NFAState>();
        String cString;
        boolean add = true;
        //BFS for states
        while(!queue.isEmpty()){
            //remove first
            curr = queue.remove();
            cString = curr.toString();
            //mark curr as visited
            visited.add(curr);
            //add curr to rtVal if not there already
            for(DFAState s : rtVal.getStates()){
                if(s.toString().equals(cString)){
                    add = false;
                }
            }
            if(add){
                if(started){
                    for(NFAState s : curr){
                        if(s.isFinalState()){
                            fin = true;
                        }
                    }
                    if(fin){
                        rtVal.addFinalState(cString);
                    }else{
                        rtVal.addState(cString);
                    }
                }else{
                    rtVal.addStartState(cString);
                    started = true;
                }
                fin = false;
            }
            add = true;
            //insert e closed neighbors into queue
            for(char c : this.sigma){
                if(c != 'e'){
                    for(NFAState s : curr){
                        next.addAll(this.getToEClosedState(s, c));
                    }
                    if(!visited.contains(next)){
                        queueSet = new HashSet<NFAState>();
                        queueSet.addAll(next);
                        queue.add(queueSet);
                    }
                    next.clear();
                }
            }
        }
        //add transitions
        queue.clear();
        visited.clear();
        queue.add(startSet);
        //BFS again for transitions
        while(!queue.isEmpty()){
            //remove first
            curr = queue.remove();
            cString = curr.toString();
            //mark curr as visited
            visited.add(curr);
            //insert e closed neighbors into queue
            for(char c : this.sigma){
                if(c != 'e'){
                    for(NFAState s : curr){
                        next.addAll(this.getToEClosedState(s, c));
                    }
                    if(!visited.contains(next)){
                        queueSet = new HashSet<NFAState>();
                        queueSet.addAll(next);
                        queue.add(queueSet);
                    }
                    rtVal.addTransition(cString, c, next.toString());
                    next.clear();
                }
            }
        }
        return rtVal;
    }

    private Set<NFAState> getToEClosedState(NFAState from, char onSymb){
        Set<NFAState> rtVal = new HashSet<NFAState>();
        for(NFAState s : this.getToState(from, onSymb)){
            rtVal.add(s);
            rtVal.addAll(eClosure(s));
        }
        return rtVal;
    }

    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        ArrayList<String> nextStatesName = from.getTransitions(onSymb);
        Set<NFAState> nextStates =  new HashSet<>();

        for(String name: nextStatesName){
            for(NFAState state: states){
                if(state.getName().equals(name)){
                     nextStates.add(state);
                }
            }
        }
        return nextStates;
    }

    @Override
    public Set<NFAState> eClosure(NFAState s) {
        /*ArrayList<String> nextStates = s.getTransitions('e');
        Set<NFAState> eTransitions =  new HashSet<>();

        for(String str: nextStates){
            for(NFAState state: states)
                if(state.getName().equals(str)){
                    eTransitions.add(state);
                }
        }

        for(NFAState state: eTransitions){
            eTransitions.addAll(eClosure(state));
        }
        return eTransitions;*/
        HashSet<NFAState> rtVal = new HashSet<NFAState>(); //return value Set of NFAState
        HashSet<NFAState> visited = new HashSet<NFAState>(); //Set of NFAState for DFS search
        rtVal = DFS(s, visited); //start DFS search
        return rtVal;
    }
 
    private HashSet<NFAState> DFS(NFAState s, HashSet<NFAState> v){
        HashSet<NFAState> rtVal = new HashSet<NFAState>(); //return value Set of NFAState
        v.add(s); //add state to visited
        for(NFAState st : this.getToState(s, 'e')){ //for every state that we can reach with e
            rtVal.add(st); //add the state to the return value
            if(!v.contains(st)){ //if we have not visited it already
                rtVal.addAll(DFS(st, v)); //add all states returned from DFS to return value
            }
        }
        return rtVal;
    }
}