package analysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Set;

public class BitVectorFlowMap<A> implements Cloneable, Serializable{
	BitSet bset = null;
	HashMap<String,Integer> hash = new HashMap<String,Integer>();
	
	BitVectorFlowMap(){
		bset = new BitSet();
	}
	
	public BitVectorFlowMap<A> clone() {
		try
		{
			BitVectorFlowMap<A> temp = new BitVectorFlowMap<A>();
			Set<String> keys = this.getHash().keySet();
			for(String s : keys){
				temp.setVariable(s);
			}
		return temp;
		}
		catch(Exception e){ return null; }
	}
	
	int setVariable(String variable){
		if(hash.containsKey(variable)){
			int position = hash.get(variable);
			bset.set(position);
			return 0;
		}
		else{
			int position = bset.nextClearBit(0);
			bset.set(position);
			hash.put(variable, position);
			return 1;
		}
	}
	
	void clearVariable(String variable){
			if(!hash.containsKey(variable))
				return;
			int position = hash.get(variable);
			bset.clear(position);
			hash.remove(variable);
	}
	
	ArrayList<String> getLive(String variable){
		Set<String> key = hash.keySet();
		ArrayList<String> alist = new ArrayList<String>();
		for(String str : key){
			alist.add(str);
		}
		return alist;
	}
	
	public void clearVariableList(Set<String> s){
		for(String str : s){
			clearVariable(str);
		}
	}
	
	public void setVariableList(Set<String> s){
		for(String str : s){
			setVariable(str);
		}
	}
	
	//////////////
	
	public void union(){
		
	}

	public HashMap<String, Integer> getHash() {
		return hash;
	}

	public void setHash(HashMap<String, Integer> hash) {
		this.hash = hash;
	}
	
	
	
	
	
	
	///////////
	

}
