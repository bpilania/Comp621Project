// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package nodecases.natlab;

import ast.*;

/**
 * A collection of cases to match each node type in the AST. This can
 * be used to create visitors.
 */
public interface NatlabNodeCaseHandler
{
    public void caseASTNode(ASTNode node);
    public void caseList(List node);
    public void caseProgram(Program node);
    public void caseBody(Body node);
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node);
    public void caseHelpComment(HelpComment node);
    public void caseExpr(Expr node);
    public void caseCompilationUnits(CompilationUnits node);
    public void caseAttribute(Attribute node);
    public void caseSuperClass(SuperClass node);
    public void caseProperty(Property node);
    public void caseEvent(Event node);
    public void caseName(Name node);
    public void caseSwitchCaseBlock(SwitchCaseBlock node);
    public void caseDefaultCaseBlock(DefaultCaseBlock node);
    public void caseIfBlock(IfBlock node);
    public void caseElseBlock(ElseBlock node);
    public void caseRow(Row node);
    public void caseClassBody(ClassBody node);
    public void caseStmt(Stmt node);
    public void caseLValueExpr(LValueExpr node);
    public void caseLiteralExpr(LiteralExpr node);
    public void caseUnaryExpr(UnaryExpr node);
    public void caseBinaryExpr(BinaryExpr node);
    public void caseScript(Script node);
    public void caseFunctionList(FunctionList node);
    public void caseEmptyProgram(EmptyProgram node);
    public void caseClassDef(ClassDef node);
    public void caseProperties(Properties node);
    public void caseMethods(Methods node);
    public void caseSignature(Signature node);
    public void casePropertyAccess(PropertyAccess node);
    public void caseClassEvents(ClassEvents node);
    public void caseFunction(Function node);
    public void caseOneLineHelpComment(OneLineHelpComment node);
    public void caseMultiLineHelpComment(MultiLineHelpComment node);
    public void caseExprStmt(ExprStmt node);
    public void caseAssignStmt(AssignStmt node);
    public void caseGlobalStmt(GlobalStmt node);
    public void casePersistentStmt(PersistentStmt node);
    public void caseShellCommandStmt(ShellCommandStmt node);
    public void caseBreakStmt(BreakStmt node);
    public void caseContinueStmt(ContinueStmt node);
    public void caseReturnStmt(ReturnStmt node);
    public void caseEmptyStmt(EmptyStmt node);
    public void caseForStmt(ForStmt node);
    /**
     * A case for for loops that are known to loop over simple ranges.
     */
    public void caseRangeForStmt(ForStmt node);
    public void caseWhileStmt(WhileStmt node);
    public void caseTryStmt(TryStmt node);
    public void caseSwitchStmt(SwitchStmt node);
    public void caseIfStmt(IfStmt node);
    public void caseRangeExpr(RangeExpr node);
    public void caseColonExpr(ColonExpr node);
    public void caseEndExpr(EndExpr node);
    public void caseNameExpr(NameExpr node);
    public void caseParameterizedExpr(ParameterizedExpr node);
    public void caseCellIndexExpr(CellIndexExpr node);
    public void caseDotExpr(DotExpr node);
    public void caseMatrixExpr(MatrixExpr node);
    public void caseCellArrayExpr(CellArrayExpr node);
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node);
    public void caseIntLiteralExpr(IntLiteralExpr node);
    public void caseFPLiteralExpr(FPLiteralExpr node);
    public void caseStringLiteralExpr(StringLiteralExpr node);
    public void caseUMinusExpr(UMinusExpr node);
    public void caseUPlusExpr(UPlusExpr node);
    public void caseNotExpr(NotExpr node);
    public void caseMTransposeExpr(MTransposeExpr node);
    public void caseArrayTransposeExpr(ArrayTransposeExpr node);
    public void casePlusExpr(PlusExpr node);
    public void caseMinusExpr(MinusExpr node);
    public void caseMTimesExpr(MTimesExpr node);
    public void caseMDivExpr(MDivExpr node);
    public void caseMLDivExpr(MLDivExpr node);
    public void caseMPowExpr(MPowExpr node);
    public void caseETimesExpr(ETimesExpr node);
    public void caseEDivExpr(EDivExpr node);
    public void caseELDivExpr(ELDivExpr node);
    public void caseEPowExpr(EPowExpr node);
    public void caseAndExpr(AndExpr node);
    public void caseOrExpr(OrExpr node);
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node);
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node);
    public void caseLTExpr(LTExpr node);
    public void caseGTExpr(GTExpr node);
    public void caseLEExpr(LEExpr node);
    public void caseGEExpr(GEExpr node);
    public void caseEQExpr(EQExpr node);
    public void caseNEExpr(NEExpr node);
    public void caseFunctionHandleExpr(FunctionHandleExpr node);
    public void caseLambdaExpr(LambdaExpr node);
    public void caseCSLExpr(CSLExpr node);
    public void caseEndCallExpr(EndCallExpr node);
    public void caseCheckScalarStmt(CheckScalarStmt node);
}
