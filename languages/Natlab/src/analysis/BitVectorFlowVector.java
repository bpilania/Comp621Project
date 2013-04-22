package analysis;

import java.util.BitSet;

public class BitVectorFlowVector extends BitSet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7450558137279598705L;
	
	
	public BitVectorFlowVector copy(){
		
		BitVectorFlowVector bSetNew = new BitVectorFlowVector();
		int index = this.nextSetBit(0);
		
		while(index != -1){
			bSetNew.set(index);
			index = this.nextSetBit(index+1);
		}
				
		return bSetNew;			
		
	}
	
	
	
	
	
	
	 
	

}
