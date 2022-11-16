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
        //create powerset
        Set<HashSet<NFAState>> pSet = new HashSet<HashSet<NFAState>>();
        HashSet<NFAState> tmpSet;
        int pSetSize = (int)Math.pow(2, this.states.size());
        NFAState statesArray[] = new NFAState[this.states.size()];
        int i = 0;
        for(NFAState s : this.states){
            statesArray[i] = s;
            i++;
        }
        for(i=0; i<pSetSize; i++){
            tmpSet = new HashSet<NFAState>();
            for(int k=0; k<this.states.size(); k++){
                if((i & (1<<k))>0){
                    tmpSet.add(statesArray[k]);
                }
            }
            pSet.add(tmpSet);
        }
        //create queue
        Queue<Set<NFAState>> queue = new LinkedList<Set<NFAState>>();
        //add set of start state to queue
        NFAState start = (NFAState)this.getStartState();
        Set<NFAState> startSet = new HashSet<NFAState>();
        startSet.add(start);
        queue.add(startSet);
        //create array of visited sets
        ArrayList<Set<NFAState>> visited = new ArrayList<Set<NFAState>>();
        visited.add(startSet);
        //add states to rtVal
        /* 
        rtVal.addStartState(startSet.toString());
        */
        boolean isStartSet = false;
        boolean foundStart = false;
        boolean fin = false;
        for(HashSet<NFAState> s : pSet){
            if(!rtVal.getStates().contains(new DFAState(s.toString()))){
                for(NFAState f : s){
                    if(f.isFinalState()){
                        fin = true;
                    }
                }
                if(s.size() == 1 && !foundStart){
                    for(NFAState st : s){
                        if(st.isStartState()){
                            isStartSet = true;
                            foundStart = true;
                        }
                    }
                }
                if(!fin){
                    if(isStartSet){
                        rtVal.addStartState(s.toString());
                    }else{
                        rtVal.addState(s.toString());
                    }
                }else{
                    rtVal.addFinalState(s.toString());
                }
            }
            fin = false;
            isStartSet = false;
        }
        //variables for BFS
        Set<NFAState> curr;
        Set<NFAState> next = new HashSet<NFAState>();
        //BFS
        while(!queue.isEmpty()){
            //remove first
            curr = queue.remove();
            //mark curr as visited
            visited.add(curr);
            //insert unvisited neighbors into queue
            for(Character c : this.sigma){
                for(NFAState s : curr){
                    next.addAll(this.getToState(s, c));
                }
                if(!visited.contains(next)){
                    queue.add(next);
                }
                next.clear();
            }
            //insert all transitions into rtVal
            for(Character c : this.sigma){
                for(NFAState s : curr){
                    next.addAll(this.getToState(s, c));
                }
                queue.add(next);
                next.clear();
            }
        }

        return rtVal;
    }

    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        ArrayList<String> nextStatesName = from.getTransitions(onSymb);
        Set<NFAState> nextStates =  new HashSet<>();

        for(String name: nextStatesName){
            for(NFAState state: states)
                if(state.getName() == name) nextStates.add(state);
        }
        return nextStates;
    }

    @Override
    public Set<NFAState> eClosure(NFAState s) {
        ArrayList<String> nextStates = s.getTransitions('e');
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
        return eTransitions;
    }
}