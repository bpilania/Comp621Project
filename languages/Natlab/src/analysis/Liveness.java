package analysis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import analysis.SerializeFlowVariables;
import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
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
public class Liveness extends
		AbstractSimpleStructuralBackwardAnalysis<HashSetFlowSet<String>> implements Serializable{
	// Factory method, instantiates and runs the analysis
	private static VFPreorderAnalysis reorderAnalysis;

	public static Liveness of(ASTNode<?> tree) {
		reorderAnalysis = new VFPreorderAnalysis(tree);
		reorderAnalysis.analyze();
		Liveness analysis = new Liveness(tree);
		analysis.analyze();
		return analysis;
	}
	public Liveness(){
		
	}
	public SerializeFlowVariables getInstance(){
		SerializeFlowVariables sfv = new SerializeFlowVariables();
		sfv.setInFlowSets(inFlowSets);
		sfv.setOutFlowSets(outFlowSets);
		return sfv;
	}
	
	public void setInstance(SerializeFlowVariables sfv){
		inFlowSets = sfv.getInFlowSets();
		outFlowSets = sfv.getOutFlowSets();
	}
	
	static int count1 = 0;
	static int count2 = 0;
	public void prettyPrint() {
		getTree().analyze(this.new Printer());
	}

	private Liveness(ASTNode tree) {
		super(tree);
		currentInSet = newInitialFlow();
		currentOutSet = newInitialFlow();
	}

	// The initial flow is an empty map.
	@Override
	public HashSetFlowSet<String> newInitialFlow() {
		return new HashSetFlowSet<String>();
	}

	@Override
	public void caseStmt(Stmt node) {
		outFlowSets.put(node, currentOutSet.copy());
		currentOutSet.copy(currentInSet);
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

		Set<String> kill = node.getLValues();
		Set<String> genSet = new LinkedHashSet<String>();
		Set<NameExpr> setname = node.getRHS().getAllNameExpressions();
		for(NameExpr name : setname){
			name.getVarName();
			if (reorderAnalysis.getResult(name.getName()).isVariable() == true){
				genSet.add(name.getVarName());
			}
			
		}
		if(kill.containsAll(genSet) && genSet.containsAll(kill))
			flag = 1;
		
		if(flag == 0){
		outFlowSets.put(node, currentOutSet.copy());

		HashSetFlowSet<String> gen = newInitialFlow();
		for (NameExpr s : node.getRHS().getNameExpressions()) {
			Set<AssignStmt> defs = new HashSet<AssignStmt>();
			defs.add(node);
			gen.add(s.getVarName());
		}
		currentInSet = newInitialFlow();
		currentOutSet.copy(currentInSet);

		Set<String> tempSet = currentInSet.getSet();
		tempSet.remove(kill.iterator().next());
		currentInSet.clear();
		currentInSet.addAll(tempSet);

		currentInSet.union(gen);
		inFlowSets.put(node, currentInSet.copy());
		count1++;
		}
		else{
			outFlowSets.put(node, currentOutSet.copy());
			inFlowSets.put(node, currentInSet);
			count2++;
		}
	}

	@Override
	public void caseIfStmt(IfStmt node) {
		outFlowSets.put(node, currentOutSet.copy());

		HashSetFlowSet<String> gen = newInitialFlow();
		for (NameExpr s : node.getNameExpressions()) {
			Set<Stmt> defs = new HashSet<Stmt>();
			defs.add(node);
			gen.add(s.getVarName());
		}
		currentInSet = newInitialFlow();
		currentOutSet.copy(currentInSet);
		Set<String> kill = node.getLValues();
		Set<String> tempSet = currentInSet.getSet();
		tempSet.remove(kill.iterator().next());
		currentInSet.clear();
		currentInSet.addAll(tempSet);

		currentInSet.union(gen);
		inFlowSets.put(node, currentInSet.copy());
	}

	@Override
	public void caseWhileStmt(WhileStmt node) {
		outFlowSets.put(node, currentOutSet.copy());

		HashSetFlowSet<String> gen = newInitialFlow();
		for (NameExpr s : node.getNameExpressions()) {
			Set<Stmt> defs = new HashSet<Stmt>();
			defs.add(node);
			gen.add(s.getVarName());
		}
		currentInSet = newInitialFlow();
		currentOutSet.copy(currentInSet);
		Set<String> kill = node.getLValues();
		Set<String> tempSet = currentInSet.getSet();
		tempSet.remove(kill.iterator().next());
		currentInSet.clear();
		currentInSet.addAll(tempSet);

		currentInSet.union(gen);
		inFlowSets.put(node, currentInSet.copy());
	}

	@Override
	public void caseForStmt(ForStmt node) {
		outFlowSets.put(node, currentOutSet.copy());

		HashSetFlowSet<String> gen = newInitialFlow();
		for (NameExpr s : node.getNameExpressions()) {
			Set<Stmt> defs = new HashSet<Stmt>();
			defs.add(node);
			gen.add(s.getVarName());
		}
		currentInSet = newInitialFlow();
		currentOutSet.copy(currentInSet);
		Set<String> kill = node.getLValues();
		Set<String> tempSet = currentInSet.getSet();
		tempSet.remove(kill.iterator().next());
		currentInSet.clear();
		currentInSet.addAll(tempSet);

		currentInSet.union(gen);
		inFlowSets.put(node, currentInSet.copy());
	}

	// Copy is straightforward.
	@Override
	public void copy(HashSetFlowSet<String> src, HashSetFlowSet<String> dest) {
		// if (src != null || dest != null) {

		src.copy(dest);
		// }
	}

	// We just want to create this merger once. It's used in merge() below.

	// Here we define the merge operation. There are two "levels" of set union
	// here:
	// union on the maps by key (the union method)
	// if a key is in both maps, union the two sets (the UNION merger passed to
	// the method)
	@Override
	public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2,
			HashSetFlowSet<String> out) {
		in1.union(in2, out);
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

		private void printMap(HashSetFlowSet<String> map) {
			if (map == null) {
				return;
			}
			for (String var : map.toList()) {
				System.out.print(var + ": ");
				System.out.println();
			}
			System.out.println("Count1 is: "+count1);
			System.out.println("Count2 is: "+count2);
		}
	}
}
