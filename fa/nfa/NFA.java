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
        //create set of visited sets
        Set<Set<NFAState>> visited = new HashSet<Set<NFAState>>();
        visited.add(startSet);
        //add states to rtVal
        boolean started = false;
        boolean fin = false;
        /*for(HashSet<NFAState> s : pSet){
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
                if(!s.isEmpty()){
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
            }
            fin = false;
            isStartSet = false;
        }
        //variables for BFS
        Set<NFAState> curr;
        Set<NFAState> next = new HashSet<NFAState>();
        String cString;
        String nString;
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
                if(!visited.contains(next) && !next.isEmpty()){
                    Set<NFAState> queueSet = new HashSet<NFAState>();
                    queueSet.addAll(next);
                    queue.add(queueSet);
                }
                next.clear();
            }
            //insert all transitions into rtVal
            cString = curr.toString();
            if(!cString.equals("[]")){
                for(Character c : this.sigma){
                    if(!c.equals('e')){
                        for(NFAState s : curr){
                            next.addAll(this.getToEClosedState(s,c));
                        }
                        nString = next.toString();
                        if(!nString.equals("[]")){
                            rtVal.addTransition(cString, c, nString);
                        }else{
                            rtVal.addTransition(cString, c, "qerror");
                        }
                        next.clear();
                    }
                }
            }
        }*/
        //variables for BFS
        Set<NFAState> curr;
        Set<NFAState> queueSet;
        Set<NFAState> next = new HashSet<NFAState>();
        String cString;
        //BFS for states
        while(!queue.isEmpty()){
            //remove first
            curr = queue.remove();
            cString = curr.toString();
            //mark curr as visited
            visited.add(curr);
            //add curr to rtVal
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
            //insert e closed neighbors into queue
            for(char c : this.sigma){
                if(c != 'e'){
                    for(NFAState s : curr){
                        next.addAll(this.getToEClosedState(s, c));
                    }
                    if(!visited.contains(next) && !next.isEmpty()){
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
                    if(!visited.contains(next) && !next.isEmpty()){
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
        HashSet<NFAState> rtVal = new HashSet<NFAState>();
        HashSet<NFAState> visited = new HashSet<NFAState>();
        rtVal = DFS(s, visited);
        return rtVal;
    }

    private HashSet<NFAState> DFS(NFAState s, HashSet<NFAState> v){
        HashSet<NFAState> rtVal = new HashSet<NFAState>();
        v.add(s);
        for(NFAState st : this.getToState(s, 'e')){
            rtVal.add(st);
            if(!v.contains(st)){
                rtVal.addAll(DFS(st, v));
            }
        }
        return rtVal;
    }
}