package analysis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import mclint.util.Parsing;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Function;
import ast.FunctionList;
import ast.Program;
import ast.Script;


public class Main {
  // extract the name from a function or script
  private static String getName(ASTNode<?> node) {
    if (node instanceof Script) {
      return ((Script) node).getName();
    } else if (node instanceof Function) {
      return ((Function) node).getName();
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    // Parse the input files into an AST.
	  args = new String[1];
	  args[0] = "/home/bhaskar/mclab/languages/Natlab/Alphtrmed.m";
	  args[0] = "/home/bhaskar/mclab/languages/Natlab/ImageSharing.m";
	  args[0] = "/home/bhaskar/mclab/languages/Natlab/Direct.m";
	  args[0] = "/home/bhaskar/mclab/languages/Natlab/erdosRenyi.m";

	  args[0] = "/home/bhaskar/mclab/languages/Natlab/big.m";
	  
	  //args[0] = "/home/bhaskar/mclab/languages/Natlab/sujay.m";

	  
    CompilationUnits program = Parsing.files(args);
    

    //final long startTime = System.currentTimeMillis();
    
    
    /*
     * 
     * Lines below are intentionally commented. They were written as a part of Objective 3 of the project. Currently
     * nullpointerexception is observed as ast.AssgnStmt is not serialized.
     */
    
//    SerializeFlowVariables sfvOld;
//    try
//    {
//       FileInputStream fileIn =
//                        new FileInputStream("FlowVariables.ser");
//       ObjectInputStream in = new ObjectInputStream(fileIn);
//       sfvOld = (SerializeFlowVariables) in.readObject();
//       in.close();
//       fileIn.close();
//    }catch(FileNotFoundException c)
//    {
//       
//    }catch(IOException i)
//    {
//       i.printStackTrace();
//       return;
//    }catch(ClassNotFoundException c)
//    {
//       
//    }
//  
    
    
/*
 * 
 * Lines below are intentionally commented. They were written as a part of Objective 3 of the project. Currently
 * nullpointerexception is observed as ast.AssgnStmt is not serialized.
 */
    
//    
//    SerializeFlowVariables sfv = new SerializeFlowVariables();
//    sfv = ltry.getInstance();
//    //sfv.setOutFlowSets(outFlowSets);
//    try
//    {
//       FileOutputStream fileOut =
//       new FileOutputStream("FlowVariables.ser");
//       ObjectOutputStream out =
//                          new ObjectOutputStream(fileOut);
//       out.writeObject(sfv);
//       out.close();
//        fileOut.close();
//    }catch(IOException i)
//    {
//        i.printStackTrace();
//    }
//    
//    
    
//    
    final long startTime1 = System.currentTimeMillis();
    for(int i=0; i<10; i++){
    Map<ASTNode<?>, LiveVariableAnalysis> analyses1 = 
    		new HashMap<ASTNode<?>, LiveVariableAnalysis>();
    for (Program unit : program.getPrograms()) {
      if (unit instanceof Script) {
        analyses1.put(unit, LiveVariableAnalysis.of(unit));
      } else if (unit instanceof FunctionList) {
        for (Function f : ((FunctionList) unit).getFunctions()) {
          analyses1.put(f, LiveVariableAnalysis.of(f));
        }
      }
    }
    }
    
     //Report the analysis results.
//    for (ASTNode<?> node : analyses1.keySet()) {
//      System.out.println("Live Variable using original Datastructure ");
//      analyses1.get(node).prettyPrint();
//    }
    
	//System.out.println("compCount: "+LiveVariableAnalysis.compCount);

    final long endTime1 = System.currentTimeMillis();

    System.out.println("Total execution time: " + (endTime1 - startTime1)/10);
    
    final long startTime = System.currentTimeMillis();

    for(int i = 0; i< 10; i++){
    	ImprovedLiveVariableAnalysis ltry = null;
    Map<ASTNode<?>, ImprovedLiveVariableAnalysis> analyses = new HashMap<ASTNode<?>, ImprovedLiveVariableAnalysis>();
    for (Program unit : program.getPrograms()) {
      if (unit instanceof Script) {
    	  ltry = ImprovedLiveVariableAnalysis.of(unit);
        analyses.put(unit, ltry);
      } else if (unit instanceof FunctionList) {
        for (Function f : ((FunctionList) unit).getFunctions()) {
        	 ltry = ImprovedLiveVariableAnalysis.of(f);
          analyses.put(f, ltry);
        }
      }
    }
    }
// Report the analysis results.
//    for (ASTNode<?> node : analyses.keySet()) {
//      System.out.println("Live Variable using Bit Vector Datastructure ");
//      analyses.get(node).prettyPrint();
//    }
	//System.out.println("compCount: "+ImprovedLiveVariableAnalysis.compCount);
	//System.out.println("compSaveCount: "+ImprovedLiveVariableAnalysis.compSaveCount);
    final long endTime = System.currentTimeMillis();
////
    System.out.println("Total execution time: " + (endTime - startTime)/10 );
  }
}
