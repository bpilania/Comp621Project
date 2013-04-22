package analysis;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import analysis.SerializeFlowVariables;
import analysis.BitVectorFlowVector;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import nodecases.AbstractNodeCaseHandler;
import analysis.AbstractSimpleStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.EmptyStmt;
import ast.ForStmt;
import ast.IfStmt;
import ast.NameExpr;
import ast.Script;
import ast.Stmt;
import ast.WhileStmt;

/**
 * This is a simple reaching defs analysis. It doesn't handle function
 * parameters, global variables, or persistent variables. (It just maps variable
 * names to assignment statements). It also doesn't handle nested functions.
 */
public class LiveneNew extends
		AbstractSimpleStructuralBackwardAnalysis<BitVectorFlowVector> implements Serializable{
	// Factory method, instantiates and runs the analysis
	private static VFPreorderAnalysis reorderAnalysis;

	public static LiveneNew of(ASTNode<?> tree) {
		reorderAnalysis = new VFPreorderAnalysis(tree);
		reorderAnalysis.analyze();
		LiveneNew analysis = new LiveneNew(tree);
		analysis.analyze();
		return analysis;
	}
	public LiveneNew(){
		
	}

	

	
	public void prettyPrint() {
		getTree().analyze(this.new Printer());
	}

	private LiveneNew(ASTNode tree) {
		super(tree);
		currentInSet = newInitialFlow();
		currentOutSet = newInitialFlow();
	}

	// The initial flow is an empty map.
	@Override
	public BitVectorFlowVector newInitialFlow() {
		return new BitVectorFlowVector();
	}

	@Override
	public void caseStmt(Stmt node) {

		outFlowSets.put(node, currentOutSet.copy());
		currentInSet = currentOutSet.copy();
		inFlowSets.put(node, currentInSet.copy());
	}

	/*
	 * @Override public void caseBreakStmt(BreakStmt node) {
	 * inFlowSets.put(node, currentInSet.copy()); super.caseBreakStmt(node);
	 * outFlowSets.put(node, currentOutSet.copy()); }
	 * 
	 * @Override public void caseContinueStmt(ContinueStmt node) {
	 * inFlowSets.put(node, currentInSet.copy()); super.caseContinueStmt(node);
	 * outFlowSets.put(node, currentOutSet.copy()); }
	 */

	@Override
	public void caseAssignStmt(AssignStmt node) {
		int flag = 0;

		Set<String> killSet = node.getLValues();
		Set<String> genSet = new LinkedHashSet<String>();
		Set<NameExpr> setname = node.getRHS().getAllNameExpressions();
		for(NameExpr name : setname){
			name.getVarName();
			if (reorderAnalysis.getResult(name.getName()).isVariable() == true){
				genSet.add(name.getVarName());
			}
			
		}
		if(killSet.containsAll(genSet) && genSet.containsAll(killSet))
			flag = 1;
		
		if(flag == 0){
		outFlowSets.put(node, currentOutSet.copy());
		
		BitSet genBitSet = new BitSet();
		for (String s : genSet) {
			if(s.equalsIgnoreCase("a"))
				genBitSet.set(0);
			else if(s.equalsIgnoreCase("b"))
				genBitSet.set(1);
			else if(s.equalsIgnoreCase("c"))
				genBitSet.set(2);
			else if(s.equalsIgnoreCase("d"))
				genBitSet.set(3);
			else if(s.equalsIgnoreCase("e"))
				genBitSet.set(4);
			else if(s.equalsIgnoreCase("fun"))
				genBitSet.set(5);
			else if(s.equalsIgnoreCase("z"))
				genBitSet.set(6);
			else if(s.equalsIgnoreCase("i"))
				genBitSet.set(7);
			 
		}
		node.setGenBitSet(genBitSet);
		
		
		BitSet killBitSet = new BitSet();
		for (String s : killSet) {
			if(s.equalsIgnoreCase("a")){
				killBitSet.set(0);
			}
			else if(s.equalsIgnoreCase("b")){
				killBitSet.set(1);
			}
			else if(s.equalsIgnoreCase("c")){
				killBitSet.set(2);
			}
			else if(s.equalsIgnoreCase("d")){
				killBitSet.set(3);
			}
			else if(s.equalsIgnoreCase("e")){
				killBitSet.set(4);
			}
			else if(s.equalsIgnoreCase("fun")){
				killBitSet.set(5);
			}
			else if(s.equalsIgnoreCase("z")){
				killBitSet.set(6);
			}
			else if(s.equalsIgnoreCase("i")){
				killBitSet.set(7);
			}
			 
		}
		int lastIndex = killBitSet.length();
		int outLength = currentOutSet.length();
		killBitSet.flip(0,outLength);
		node.setKillBitSet(killBitSet);

		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		
		currentInSet.and(killBitSet);
		currentInSet.or(genBitSet);
		
		
		inFlowSets.put(node, currentInSet.copy());
		}
		else{
			outFlowSets.put(node, currentOutSet.copy());
			inFlowSets.put(node, currentInSet);
		}
	}
	
	@Override
	public void caseScript(Script node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		outFlowSets.put(node, currentOutSet);

		node.getStmts().analyze(this);

		inFlowSets.put(node, currentInSet);
	}


	@Override
	public void caseIfStmt(IfStmt node) {
		int flag = 0;

		Set<String> killSet = node.getLValues();
		Set<String> genSet = new LinkedHashSet<String>();
		Set<NameExpr> setname = node.getAllNameExpressions();
		for(NameExpr name : setname){
			name.getVarName();
			if (reorderAnalysis.getResult(name.getName()).isVariable() == true){
				genSet.add(name.getVarName());
			}
			
		}
		if(killSet.containsAll(genSet) && genSet.containsAll(killSet))
			flag = 1;
		
		if(flag == 0){
		outFlowSets.put(node, currentOutSet.copy());
		
		BitSet genBitSet = new BitSet();
		for (String s : genSet) {
			if(s.equalsIgnoreCase("a"))
				genBitSet.set(0);
			else if(s.equalsIgnoreCase("b"))
				genBitSet.set(1);
			else if(s.equalsIgnoreCase("c"))
				genBitSet.set(2);
			else if(s.equalsIgnoreCase("d"))
				genBitSet.set(3);
			else if(s.equalsIgnoreCase("e"))
				genBitSet.set(4);
			else if(s.equalsIgnoreCase("fun"))
				genBitSet.set(5);
			else if(s.equalsIgnoreCase("z"))
				genBitSet.set(6);
			else if(s.equalsIgnoreCase("i"))
				genBitSet.set(7);
			 
		}
		node.setGenBitSet(genBitSet);
		
		
		BitSet killBitSet = new BitSet();
		for (String s : killSet) {
			if(s.equalsIgnoreCase("a")){
				killBitSet.set(0);
			}
			else if(s.equalsIgnoreCase("b")){
				killBitSet.set(1);
			}
			else if(s.equalsIgnoreCase("c")){
				killBitSet.set(2);
			}
			else if(s.equalsIgnoreCase("d")){
				killBitSet.set(3);
			}
			else if(s.equalsIgnoreCase("e")){
				killBitSet.set(4);
			}
			else if(s.equalsIgnoreCase("fun")){
				killBitSet.set(5);
			}
			else if(s.equalsIgnoreCase("z")){
				killBitSet.set(6);
			}
			else if(s.equalsIgnoreCase("i")){
				killBitSet.set(7);
			}
			 
		}
		int lastIndex = killBitSet.length();
		int outLength = currentOutSet.length();
		killBitSet.flip(0,outLength);
		node.setKillBitSet(killBitSet);

		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		
		currentInSet.and(killBitSet);
		currentInSet.or(genBitSet);
		
		
		inFlowSets.put(node, currentInSet.copy());
		}
		else{
			outFlowSets.put(node, currentOutSet.copy());
			inFlowSets.put(node, currentInSet);
		}
	}
	
	

//	@Override
//	public void caseWhileStmt(WhileStmt node) {
//		outFlowSets.put(node, currentOutSet.copy());
//
//		BitVectorFlowVector<String> gen = newInitialFlow();
//		for (NameExpr s : node.getNameExpressions()) {
//			Set<Stmt> defs = new HashSet<Stmt>();
//			defs.add(node);
//			gen.add(s.getVarName());
//		}
//		currentInSet = newInitialFlow();
//		currentOutSet.copy(currentInSet);
//		Set<String> kill = node.getLValues();
//		Set<String> tempSet = currentInSet.getSet();
//		tempSet.remove(kill.iterator().next());
//		currentInSet.clear();
//		currentInSet.addAll(tempSet);
//
//		currentInSet.union(gen);
//		inFlowSets.put(node, currentInSet.copy());
//	}
//
	@Override
	public void caseForStmt(ForStmt node) {

		Set<String> killSet = node.getLValues();
		Set<String> genSet = new LinkedHashSet<String>();
		Set<NameExpr> setname = node.getAllNameExpressions();
		for(NameExpr name : setname){
			name.getVarName();
			if (reorderAnalysis.getResult(name.getName()).isVariable() == true){
				genSet.add(name.getVarName());
			}
			
		}

		outFlowSets.put(node, currentOutSet.copy());
		
		BitSet genBitSet = new BitSet();
		for (String s : genSet) {
			if(s.equalsIgnoreCase("a"))
				genBitSet.set(0);
			else if(s.equalsIgnoreCase("b"))
				genBitSet.set(1);
			else if(s.equalsIgnoreCase("c"))
				genBitSet.set(2);
			else if(s.equalsIgnoreCase("d"))
				genBitSet.set(3);
			else if(s.equalsIgnoreCase("e"))
				genBitSet.set(4);
			else if(s.equalsIgnoreCase("fun"))
				genBitSet.set(5);
			else if(s.equalsIgnoreCase("z"))
				genBitSet.set(6);
			else if(s.equalsIgnoreCase("i"))
				genBitSet.set(7);
			 
		}
		node.setGenBitSet(genBitSet);
		
		
		BitSet killBitSet = new BitSet();
		for (String s : killSet) {
			if(s.equalsIgnoreCase("a")){
				killBitSet.set(0);
			}
			else if(s.equalsIgnoreCase("b")){
				killBitSet.set(1);
			}
			else if(s.equalsIgnoreCase("c")){
				killBitSet.set(2);
			}
			else if(s.equalsIgnoreCase("d")){
				killBitSet.set(3);
			}
			else if(s.equalsIgnoreCase("e")){
				killBitSet.set(4);
			}
			else if(s.equalsIgnoreCase("fun")){
				killBitSet.set(5);
			}
			else if(s.equalsIgnoreCase("z")){
				killBitSet.set(6);
			}
			else if(s.equalsIgnoreCase("i")){
				killBitSet.set(7);
			}
			 
		}
		int lastIndex = killBitSet.length();
		int outLength = currentOutSet.length();
		killBitSet.flip(0,outLength);
		node.setKillBitSet(killBitSet);

		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		
		currentInSet.and(killBitSet);
		currentInSet.or(genBitSet);
		
		
		inFlowSets.put(node, currentInSet.copy());
		
	}

	// Copy is straightforward.
	@Override
	public void copy(BitVectorFlowVector src, BitVectorFlowVector dest) {
		// if (src != null || dest != null) {

//		src.copy(dest);
		// }
	}

	// We just want to create this merger once. It's used in merge() below.

	// Here we define the merge operation. There are two "levels" of set union
	// here:
	// union on the maps by key (the union method)
	// if a key is in both maps, union the two sets (the UNION merger passed to
	// the method)
	@Override
	public void merge(BitVectorFlowVector in1, BitVectorFlowVector in2,
			BitVectorFlowVector out) {
//		in1.union(in2, out);
	}

	// This class pretty prints the program annotated with analysis results.
	class Printer extends AbstractNodeCaseHandler {

		private int getLine(ASTNode<?> node) {
			return beaver.Symbol.getLine(node.getStart());
		}

		private int getColumn(ASTNode<?> node) {
			return beaver.Symbol.getColumn(node.getStart());
		}

		@Override
		public void caseASTNode(ASTNode node) {
			for (int i = 0; i < node.getNumChild(); i++) {
				node.getChild(i).analyze(this);
			}
		}

		@Override
		public void caseStmt(Stmt node) {
			if (inFlowSets.get(node) == null) {
				return;
			}
			System.out.println("in {");
			printMap(inFlowSets.get(node));
			System.out.println("}");
			System.out.println(node.getPrettyPrinted());
			System.out.println("out {");
			printMap(outFlowSets.get(node));
			System.out.println("}");
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
				String var=null;
				
				if(index == 0)
					var = "a";
				else if(index == 1)
					var = "b";
				else if(index == 2)
					var = "c";
				else if(index == 3)
					var = "d";
				else if(index == 4)
					var = "e";
				else if(index == 5)
					var = "fun";
				else if(index == 6)
					var = "z";
				else if(index == 7)
					var = "i";
				
				System.out.print(var+",");
				index = bvfv.nextSetBit(index+1);
			}

		}
	}
}
