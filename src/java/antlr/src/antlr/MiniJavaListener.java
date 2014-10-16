// Generated from src/antlr/MiniJava.g4 by ANTLR 4.1

    package mini_java.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MiniJavaParser}.
 */
public interface MiniJavaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull MiniJavaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull MiniJavaParser.ExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#arrayAssignStatement}.
	 * @param ctx the parse tree
	 */
	void enterArrayAssignStatement(@NotNull MiniJavaParser.ArrayAssignStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#arrayAssignStatement}.
	 * @param ctx the parse tree
	 */
	void exitArrayAssignStatement(@NotNull MiniJavaParser.ArrayAssignStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#booleanType}.
	 * @param ctx the parse tree
	 */
	void enterBooleanType(@NotNull MiniJavaParser.BooleanTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#booleanType}.
	 * @param ctx the parse tree
	 */
	void exitBooleanType(@NotNull MiniJavaParser.BooleanTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#mainClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMainClassDeclaration(@NotNull MiniJavaParser.MainClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#mainClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMainClassDeclaration(@NotNull MiniJavaParser.MainClassDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#mainMethod}.
	 * @param ctx the parse tree
	 */
	void enterMainMethod(@NotNull MiniJavaParser.MainMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#mainMethod}.
	 * @param ctx the parse tree
	 */
	void exitMainMethod(@NotNull MiniJavaParser.MainMethodContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(@NotNull MiniJavaParser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(@NotNull MiniJavaParser.FormalParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#intArrayType}.
	 * @param ctx the parse tree
	 */
	void enterIntArrayType(@NotNull MiniJavaParser.IntArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#intArrayType}.
	 * @param ctx the parse tree
	 */
	void exitIntArrayType(@NotNull MiniJavaParser.IntArrayTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#mainClassBody}.
	 * @param ctx the parse tree
	 */
	void enterMainClassBody(@NotNull MiniJavaParser.MainClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#mainClassBody}.
	 * @param ctx the parse tree
	 */
	void exitMainClassBody(@NotNull MiniJavaParser.MainClassBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@NotNull MiniJavaParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@NotNull MiniJavaParser.ClassDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#intType}.
	 * @param ctx the parse tree
	 */
	void enterIntType(@NotNull MiniJavaParser.IntTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#intType}.
	 * @param ctx the parse tree
	 */
	void exitIntType(@NotNull MiniJavaParser.IntTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#assignStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssignStatement(@NotNull MiniJavaParser.AssignStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#assignStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssignStatement(@NotNull MiniJavaParser.AssignStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#mainMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMainMethodDeclaration(@NotNull MiniJavaParser.MainMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#mainMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMainMethodDeclaration(@NotNull MiniJavaParser.MainMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull MiniJavaParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull MiniJavaParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#printStatement}.
	 * @param ctx the parse tree
	 */
	void enterPrintStatement(@NotNull MiniJavaParser.PrintStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#printStatement}.
	 * @param ctx the parse tree
	 */
	void exitPrintStatement(@NotNull MiniJavaParser.PrintStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#booleanLit}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLit(@NotNull MiniJavaParser.BooleanLitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#booleanLit}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLit(@NotNull MiniJavaParser.BooleanLitContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#goal}.
	 * @param ctx the parse tree
	 */
	void enterGoal(@NotNull MiniJavaParser.GoalContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#goal}.
	 * @param ctx the parse tree
	 */
	void exitGoal(@NotNull MiniJavaParser.GoalContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull MiniJavaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull MiniJavaParser.StatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(@NotNull MiniJavaParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(@NotNull MiniJavaParser.WhileStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(@NotNull MiniJavaParser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(@NotNull MiniJavaParser.FormalParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(@NotNull MiniJavaParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(@NotNull MiniJavaParser.ClassBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#ifElseStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfElseStatement(@NotNull MiniJavaParser.IfElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#ifElseStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfElseStatement(@NotNull MiniJavaParser.IfElseStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(@NotNull MiniJavaParser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(@NotNull MiniJavaParser.FormalParameterListContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclaration(@NotNull MiniJavaParser.VarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclaration(@NotNull MiniJavaParser.VarDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link MiniJavaParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(@NotNull MiniJavaParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJavaParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(@NotNull MiniJavaParser.MethodDeclarationContext ctx);
}