package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;


public class ICinputValue extends ICAbstractValue{

	ICAbstractValue iv;
	
	ICinputValue(ICAbstractValue iv)
	{
		this.iv = iv;
	}
	
	public String toString()
	{
		return iv.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult, List<Integer> argValues) {
		return iv.match(isPatternSide, previousMatchResult, argValues);
	}
	
}
