package natlab.tame.builtin.shapeprop.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class SPQuestion extends SPAbstractMatchExpr{
	SPAbstractMatchExpr spm;
	public SPQuestion (SPAbstractMatchExpr spm){
		this.spm = spm;
		//System.out.println("?");
	}
	
	public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues){
		if(argValues.size()>previousMatchResult.getNumMatched()){
			System.out.println("inside ? expression");
			ShapePropMatch match = spm.match(isPatternSide, previousMatchResult, argValues);
			return match;
		}
		return previousMatchResult;
	}
	
	public String toString(){
		return spm.toString()+"?";
	}
}
