package analysis;

import java.io.Serializable;
import java.util.Map;

import natlab.toolkits.analysis.HashSetFlowSet;

import ast.ASTNode;

public class SerializeFlowVariables implements Serializable{
	protected Map<ASTNode,HashSetFlowSet<String>> outFlowSets, inFlowSets;

	public Map<ASTNode, HashSetFlowSet<String>> getOutFlowSets() {
		return outFlowSets;
	}

	public void setOutFlowSets(Map<ASTNode, HashSetFlowSet<String>> outFlowSets) {
		this.outFlowSets = outFlowSets;
	}

	public Map<ASTNode, HashSetFlowSet<String>> getInFlowSets() {
		return inFlowSets;
	}

	public void setInFlowSets(Map<ASTNode, HashSetFlowSet<String>> inFlowSets) {
		this.inFlowSets = inFlowSets;
	}

}
