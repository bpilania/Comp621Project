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
	  args[0] = "/home/bhaskar/mclab/languages/Natlab/if.m";
    CompilationUnits program = Parsing.files(args);
    
    // Run the analysis here.
    // Here we run the example reaching defs analysis.
    // Note that the analysis is intraprocedural. We're going to run it on each function/script
    // separately. It would also be possible for the analysis itself to take care of this
    // by defining appropriate caseFunction and caseScript methods.
    
    // Map functions and scripts to their analysis results
    final long startTime = System.currentTimeMillis();
    
    
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
  //  for(int i = 0; i< 100; i++){
    LivenessNew ltry = null;
    Map<ASTNode<?>, LivenessNew> analyses = new HashMap<ASTNode<?>, LivenessNew>();
    for (Program unit : program.getPrograms()) {
      if (unit instanceof Script) {
    	  ltry = LivenessNew.of(unit);
        analyses.put(unit, ltry);
      } else if (unit instanceof FunctionList) {
        for (Function f : ((FunctionList) unit).getFunctions()) {
        	 ltry = LivenessNew.of(f);
          analyses.put(f, ltry);
        }
      }
    }
  //  }
    // Report the analysis results.
    for (ASTNode<?> node : analyses.keySet()) {
      System.out.println("Reaching defs for " + getName(node));
      analyses.get(node).prettyPrint();
    }
    final long endTime = System.currentTimeMillis();
//
    System.out.println("Total execution time: " + (endTime - startTime) );
    
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
//    final long startTime1 = System.currentTimeMillis();
//    for(int i=0; i<100; i++){
//    Map<ASTNode<?>, Liveness> analyses1 = new HashMap<ASTNode<?>, Liveness>();
//    for (Program unit : program.getPrograms()) {
//      if (unit instanceof Script) {
//        analyses1.put(unit, Liveness.of(unit));
//      } else if (unit instanceof FunctionList) {
//        for (Function f : ((FunctionList) unit).getFunctions()) {
//          analyses1.put(f, Liveness.of(f));
//        }
//      }
//    }
//    }
//    
//    // Report the analysis results.
////    for (ASTNode<?> node : analyses1.keySet()) {
////      System.out.println("Reaching defs for " + getName(node));
////      analyses1.get(node).prettyPrint();
////    }
//    final long endTime1 = System.currentTimeMillis();
//
//    System.out.println("Total execution time: " + (endTime1 - startTime1)/100);
  }
}
