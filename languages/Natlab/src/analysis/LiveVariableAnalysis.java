package analysis;

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

public class LiveVariableAnalysis extends
		AbstractSimpleStructuralBackwardAnalysis<HashSetFlowSet<String>> {
	private static VFPreorderAnalysis reorderAnalysis;

	public static LiveVariableAnalysis of(ASTNode<?> tree) {
		reorderAnalysis = new VFPreorderAnalysis(tree);
		reorderAnalysis.analyze();
		LiveVariableAnalysis analysis = new LiveVariableAnalysis(tree);
		analysis.analyze();
		return analysis;
	}
	static int compCount = 0;

	public void prettyPrint() {
		getTree().analyze(this.new Printer());
	}

	private LiveVariableAnalysis(ASTNode tree) {
		super(tree);
	}

	@Override
	public HashSetFlowSet<String> newInitialFlow() {
		return new HashSetFlowSet<String>();
	}

	// Copy is straightforward.
	@Override
	public void copy(HashSetFlowSet<String> src, HashSetFlowSet<String> dest) {
		src.copy(dest);
	}

	// We just want to create this merger once. It's used in merge() below.
	// private static final Merger<String> UNION = Mergers.union();

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

	@Override
	public void caseStmt(Stmt node) {
		outFlowSets.put(node, currentOutSet.copy());
		caseASTNode(node);
		inFlowSets.put(node, currentInSet.copy());
	}

	@Override
	public void caseNameExpr(NameExpr nameExp) {
		if (reorderAnalysis.getResult(nameExp.getName()).isVariable() == true)
			currentInSet.add(nameExp.getVarName());
	}

	@Override
	public void caseAssignStmt(AssignStmt node) {
		int flag = 0;
		Set<String> killSet = node.getLValues();
		Set<String> gen = node.getRHS().getSymbols();

		outFlowSets.put(node, currentOutSet.copy());


		// compute (out - kill) + gen
		currentInSet = newInitialFlow();

		Set<String> outSet = currentOutSet.getSet();
		outSet.removeAll(killSet);

		currentOutSet.clear();
		currentOutSet.addAll(outSet);

		currentOutSet.copy(currentInSet);

		caseASTNode(node.getRHS());
		// currentInSet.union(gen);
		compCount++;
		inFlowSets.put(node, currentInSet.copy());
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

		private void printMap(HashSetFlowSet<String> map) {
			boolean first = true;
			if (map != null) {
				for (String var : map.getSet()) {
					if (!first) {
						System.out.print(", ");
					}
					System.out.print(var);
					first = false;
				}
			}

		}
	}
}