package analysis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import natlab.toolkits.analysis.HashSetFlowSet;
import nodecases.AbstractNodeCaseHandler;
import analysis.AbstractSimpleStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.EmptyStmt;
import ast.ForStmt;
import ast.IfStmt;
import ast.NameExpr;
import ast.Stmt;
import ast.WhileStmt;

/**
 * This is a simple reaching defs analysis. It doesn't handle function
 * parameters, global variables, or persistent variables. (It just maps variable
 * names to assignment statements). It also doesn't handle nested functions.
 */
public class LivenessTry extends
		AbstractSimpleStructuralBackwardAnalysis<BitVectorFlowMap<String>> implements Serializable{
	// Factory method, instantiates and runs the analysis
	public static LivenessTry of(ASTNode<?> tree) {
		LivenessTry analysis = new LivenessTry(tree);
		analysis.analyze();
		return analysis;
	}

	public void prettyPrint() {
		getTree().analyze(this.new Printer());
	}

	private LivenessTry(ASTNode tree) {
		super(tree);
		currentInSet = newInitialFlow();
		currentOutSet = newInitialFlow();
	}

	// The initial flow is an empty map.
	@Override
	public BitVectorFlowMap<String> newInitialFlow() {
		return new BitVectorFlowMap<String>();
	}

	@Override
	public void caseStmt(Stmt node) {
		outFlowSets.put(node, currentOutSet.clone());
		currentInSet = currentOutSet.clone();
		inFlowSets.put(node, currentInSet.clone());
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
		outFlowSets.put(node, currentOutSet.clone());
		Set<String> kill = node.getLValues();


		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.clone();
		if(currentInSet.getHash().keySet().size() != 0)
		currentInSet.clearVariableList(kill);

		for (NameExpr s : node.getRHS().getNameExpressions()) {
			Set<AssignStmt> defs = new HashSet<AssignStmt>();
			defs.add(node);
			int isChanged = currentInSet.setVariable(s.getVarName());
			if(isChanged == 1)
				flag = 1;
		}
		if(flag == 1)
			inFlowSets.put(node, currentInSet.clone());
		else
			inFlowSets.put(node, currentInSet);
	}

//	public SerializeFlowVariables getInstance(){
//		SerializeFlowVariables sfv = new SerializeFlowVariables();
//		sfv.setInFlowSets(inFlowSets);
//		sfv.setOutFlowSets(outFlowSets);
//		return sfv;
//	}
	
	
	@Override
	public void caseIfStmt(IfStmt node) {
		int flag = 0;
		outFlowSets.put(node, currentOutSet.clone());

		
		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.clone();
		Set<String> kill = node.getLValues();
		if(currentInSet.getHash().keySet().size() != 0)
		currentInSet.clearVariableList(kill);
		
		BitVectorFlowMap<String> gen = newInitialFlow();
		for (NameExpr s : node.getNameExpressions()) {
			Set<Stmt> defs = new HashSet<Stmt>();
			defs.add(node);
			int isChanged = currentInSet.setVariable(s.getVarName());
			if(isChanged == 1)
				flag = 1;
		}

		if(flag == 1)
			inFlowSets.put(node, currentInSet.clone());
		else
			inFlowSets.put(node, currentInSet);
	}
	/* Below code is not a dead code. It was written to compare execution times of objective 2 of the project.*/
	/*
	@Override
	public void caseAssignStmt(AssignStmt node) {
		outFlowSets.put(node, currentOutSet.clone());
		Set<String> kill = node.getLValues();


		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.clone();
		if(currentInSet.getHash().keySet().size() != 0)
		currentInSet.clearVariableList(kill);

		for (NameExpr s : node.getRHS().getNameExpressions()) {
			Set<AssignStmt> defs = new HashSet<AssignStmt>();
			defs.add(node);
			currentInSet.setVariable(s.getVarName());

		}
		inFlowSets.put(node, currentInSet.clone());

	}
	
	
	@Override
	public void caseIfStmt(IfStmt node) {
		outFlowSets.put(node, currentOutSet.clone());
		
		currentInSet = newInitialFlow();
		currentInSet = currentOutSet.clone();
		Set<String> kill = node.getLValues();
		if(currentInSet.getHash().keySet().size() != 0)
		currentInSet.clearVariableList(kill);
		
		BitVectorFlowMap<String> gen = newInitialFlow();
		for (NameExpr s : node.getNameExpressions()) {
			Set<Stmt> defs = new HashSet<Stmt>();
			defs.add(node);
			currentInSet.setVariable(s.getVarName());

		}

			inFlowSets.put(node, currentInSet.clone());

	}
	
	*/

//	@Override
//	public void caseWhileStmt(WhileStmt node) {
//		outFlowSets.put(node, currentOutSet.copy());
//
//		BitVectorFlowMap<String> gen = newInitialFlow();
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
//	@Override
//	public void caseForStmt(ForStmt node) {
//		outFlowSets.put(node, currentOutSet.copy());
//
//		BitVectorFlowMap<String> gen = newInitialFlow();
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
//	// Copy is straightforward.
	@Override
	public void copy(BitVectorFlowMap<String> src, BitVectorFlowMap<String> dest) {
		// if (src != null || dest != null) {

		dest = src.clone();
		// }
	}

	// We just want to create this merger once. It's used in merge() below.

	// Here we define the merge operation. There are two "levels" of set union
	// here:
	// union on the maps by key (the union method)
	// if a key is in both maps, union the two sets (the UNION merger passed to
	// the method)
	@Override
	public void merge(BitVectorFlowMap<String> in1, BitVectorFlowMap<String> in2,
			BitVectorFlowMap<String> out) {
		//in1.union(in2, out);
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

		private void printMap(BitVectorFlowMap<String> map) {
			if (map == null) {
				return;
			}
			for (String var : map.getHash().keySet()) {
				System.out.print(var + ": ");
				System.out.println();
			}
		}
	}
}
