package analysis;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import nodecases.AbstractNodeCaseHandler;
import analysis.AbstractSimpleStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.EmptyStmt;
import ast.Name;
import ast.NameExpr;
import ast.Script;
import ast.Stmt;

/**
 * This is a simple Live variable analysis. Live variable Analysis is a Backward
 * Union Analysis.
 */

public class ImprovedLiveVariableAnalysis extends
		AbstractSimpleStructuralBackwardAnalysis<BitVectorFlowVector> {
	private static VFPreorderAnalysis reorderAnalysis;

	public int count = -1;
	public static ImprovedLiveVariableAnalysis of(ASTNode<?> tree) {
		reorderAnalysis = new VFPreorderAnalysis(tree);
		reorderAnalysis.analyze();
		ImprovedLiveVariableAnalysis analysis = new ImprovedLiveVariableAnalysis(tree);
		analysis.analyze();
		return analysis;
	}
	public static int compCount = 0;
	public static int compSaveCount = 0;

	public void prettyPrint() {
		getTree().analyze(this.new Printer());
	}

	private ImprovedLiveVariableAnalysis(ASTNode tree) {
		super(tree);
	}

	@Override
	public BitVectorFlowVector newInitialFlow() {
		return new BitVectorFlowVector();
	}

	// Copy is straightforward.
	@Override
	public void copy(BitVectorFlowVector src, BitVectorFlowVector dest) {
		
		
		
		int index = src.nextSetBit(0);
		
		while(index != -1){
			dest.set(index);
			index = src.nextSetBit(index+1);
		}
		
	}


	@Override
	public void merge(BitVectorFlowVector in1, BitVectorFlowVector in2,
			BitVectorFlowVector out) {
		out.or(in1);
		out.or(in2);
	}

	@Override
	public void caseStmt(Stmt node) {
		outFlowSets.put(node, currentOutSet.copy());
		caseASTNode(node);
		inFlowSets.put(node, currentInSet.copy());
	}
	int flagCall = 0;

	@Override
	public void caseNameExpr(NameExpr nameExp) {
		if (reorderAnalysis.getResult(nameExp.getName()).isVariable() == true )
			if(flagCall==1)
			genSet.add(nameExp.getVarName());
		else
		{
			String var = nameExp.getVarName();
			if(!map.containsKey(var)){
				count++;
				map.put(var, count);
				invMap.put(count, var);
				currentInSet.set(count);
			}else{
				int index = map.get(var);
				currentInSet.set(index);
			}
			

		}
	}

	@Override
	public void caseAssignStmt(AssignStmt node) {
		int flag = 0;

		Set<String> killSet = node.getLValues();
		node.getRHS().getNodeString();
		genSet.clear();
		flagCall = 1;
		caseASTNode(node.getRHS());
		flagCall = 0;
		
		outFlowSets.put(node, currentOutSet.copy());
		
		BitSet genBitSet = new BitSet();
		for (String s : genSet) {
			if(!map.containsKey(s)){
				count++;
				map.put(s, count);
				invMap.put(count, s);
				genBitSet.set(count);
			}
			else{
				int index = map.get(s);
				genBitSet.set(index);
			}
			 
		}
		node.setGenBitSet(genBitSet);
		
		
		BitSet killBitSet = new BitSet();
		for (String s : killSet) {
			if(!map.containsKey(s)){
				count++;
				map.put(s, count);
				invMap.put(count, s);
				killBitSet.set(count);
			}
			else{
				int index = map.get(s);
				killBitSet.set(index);
			}
			 
		}
		int lastIndex = killBitSet.length();
		int outLength = currentOutSet.length();
		BitSet temp = new BitSet(); 
		temp.or(killBitSet);
		temp.xor(genBitSet);
		if(temp.nextSetBit(0) == -1)
			flag = 1;
		
		killBitSet.flip(0,outLength);
		node.setKillBitSet(killBitSet);
		
		
		if(flag == 0){
			compCount++;
		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		
		currentInSet.and(killBitSet);
		currentInSet.or(genBitSet);
		
		
		inFlowSets.put(node, currentInSet.copy());
		}
		else{
			compSaveCount++;
			outFlowSets.put(node, currentOutSet);
			inFlowSets.put(node, currentInSet);
		}
		

	}

	@Override
	public void caseFunction(ast.Function node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		outFlowSets.put(node, currentOutSet);

		node.getStmts().analyze(this);

		inFlowSets.put(node, currentInSet);
	}

	@Override
	public void caseScript(Script node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		outFlowSets.put(node, currentOutSet);

		node.getStmts().analyze(this);

		inFlowSets.put(node, currentInSet);
	}

	// This class pretty prints the program annotated with analysis results.
	class Printer extends AbstractNodeCaseHandler {
		@Override
		public void caseASTNode(ASTNode node) {
			for (int i = 0; i < node.getNumChild(); i++) {
				node.getChild(i).analyze(this);
			}
		}

		@Override
		public void caseStmt(Stmt node) {
			System.out.print("in: { ");
			printMap(inFlowSets.get(node));
			System.out.println(" }");
			System.out.println(node.getPrettyPrinted());
			System.out.print("out: { ");
			printMap(outFlowSets.get(node));
			System.out.println(" }");
			System.out.println();
			caseASTNode(node);
		}

		@Override
		public void caseEmptyStmt(EmptyStmt node) {
			return;
		}

		private void printMap(BitVectorFlowVector bvfv) {
			if (bvfv.cardinality() == 0) {
				return;
			}
			int index = bvfv.nextSetBit(0);
			while(index != -1){
				
				
				String var = invMap.get(index);
				
				System.out.print(var+",");
				index = bvfv.nextSetBit(index+1);
			}


		}
	}
}