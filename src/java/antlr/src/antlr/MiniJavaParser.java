// Generated from src/antlr/MiniJava.g4 by ANTLR 4.1

    package mini_java.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MiniJavaParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__34=1, T__33=2, T__32=3, T__31=4, T__30=5, T__29=6, T__28=7, T__27=8, 
		T__26=9, T__25=10, T__24=11, T__23=12, T__22=13, T__21=14, T__20=15, T__19=16, 
		T__18=17, T__17=18, T__16=19, T__15=20, T__14=21, T__13=22, T__12=23, 
		T__11=24, T__10=25, T__9=26, T__8=27, T__7=28, T__6=29, T__5=30, T__4=31, 
		T__3=32, T__2=33, T__1=34, T__0=35, ID=36, INT=37, WS=38;
	public static final String[] tokenNames = {
		"<INVALID>", "']'", "'public'", "','", "'while'", "'['", "'-'", "'*'", 
		"'('", "'if'", "'int'", "'<'", "'main'", "'false'", "'void'", "'{'", "'extends'", 
		"'else'", "'boolean'", "'}'", "'true'", "'static'", "'length'", "'System.out.println'", 
		"')'", "'.'", "'+'", "'='", "'return'", "'String'", "';'", "'&&'", "'this'", 
		"'new'", "'class'", "'!'", "ID", "INT", "WS"
	};
	public static final int
		RULE_goal = 0, RULE_mainClassDeclaration = 1, RULE_classDeclaration = 2, 
		RULE_mainClassBody = 3, RULE_mainMethod = 4, RULE_mainMethodDeclaration = 5, 
		RULE_classBody = 6, RULE_varDeclaration = 7, RULE_methodDeclaration = 8, 
		RULE_formalParameters = 9, RULE_formalParameterList = 10, RULE_formalParameter = 11, 
		RULE_type = 12, RULE_intArrayType = 13, RULE_booleanType = 14, RULE_intType = 15, 
		RULE_statement = 16, RULE_ifElseStatement = 17, RULE_whileStatement = 18, 
		RULE_printStatement = 19, RULE_assignStatement = 20, RULE_arrayAssignStatement = 21, 
		RULE_expression = 22, RULE_booleanLit = 23;
	public static final String[] ruleNames = {
		"goal", "mainClassDeclaration", "classDeclaration", "mainClassBody", "mainMethod", 
		"mainMethodDeclaration", "classBody", "varDeclaration", "methodDeclaration", 
		"formalParameters", "formalParameterList", "formalParameter", "type", 
		"intArrayType", "booleanType", "intType", "statement", "ifElseStatement", 
		"whileStatement", "printStatement", "assignStatement", "arrayAssignStatement", 
		"expression", "booleanLit"
	};

	@Override
	public String getGrammarFileName() { return "MiniJava.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public MiniJavaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class GoalContext extends ParserRuleContext {
		public ClassDeclarationContext classDeclaration(int i) {
			return getRuleContext(ClassDeclarationContext.class,i);
		}
		public TerminalNode EOF() { return getToken(MiniJavaParser.EOF, 0); }
		public MainClassDeclarationContext mainClassDeclaration() {
			return getRuleContext(MainClassDeclarationContext.class,0);
		}
		public List<ClassDeclarationContext> classDeclaration() {
			return getRuleContexts(ClassDeclarationContext.class);
		}
		public GoalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterGoal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitGoal(this);
		}
	}

	public final GoalContext goal() throws RecognitionException {
		GoalContext _localctx = new GoalContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_goal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48); mainClassDeclaration();
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==34) {
				{
				{
				setState(49); classDeclaration();
				}
				}
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(55); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MainClassDeclarationContext extends ParserRuleContext {
		public MainClassBodyContext mainClassBody() {
			return getRuleContext(MainClassBodyContext.class,0);
		}
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public MainClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainClassDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterMainClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitMainClassDeclaration(this);
		}
	}

	public final MainClassDeclarationContext mainClassDeclaration() throws RecognitionException {
		MainClassDeclarationContext _localctx = new MainClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_mainClassDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57); match(34);
			setState(58); match(ID);
			setState(59); mainClassBody();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDeclarationContext extends ParserRuleContext {
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitClassDeclaration(this);
		}
	}

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_classDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61); match(34);
			setState(62); match(ID);
			setState(65);
			_la = _input.LA(1);
			if (_la==16) {
				{
				setState(63); match(16);
				setState(64); type();
				}
			}

			setState(67); classBody();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MainClassBodyContext extends ParserRuleContext {
		public MainMethodContext mainMethod() {
			return getRuleContext(MainMethodContext.class,0);
		}
		public MainClassBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainClassBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterMainClassBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitMainClassBody(this);
		}
	}

	public final MainClassBodyContext mainClassBody() throws RecognitionException {
		MainClassBodyContext _localctx = new MainClassBodyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_mainClassBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69); match(15);
			setState(70); mainMethod();
			setState(71); match(19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MainMethodContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public MainMethodDeclarationContext mainMethodDeclaration() {
			return getRuleContext(MainMethodDeclarationContext.class,0);
		}
		public MainMethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainMethod; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterMainMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitMainMethod(this);
		}
	}

	public final MainMethodContext mainMethod() throws RecognitionException {
		MainMethodContext _localctx = new MainMethodContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_mainMethod);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73); mainMethodDeclaration();
			setState(74); match(15);
			setState(75); statement();
			setState(76); match(19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MainMethodDeclarationContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public MainMethodDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainMethodDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterMainMethodDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitMainMethodDeclaration(this);
		}
	}

	public final MainMethodDeclarationContext mainMethodDeclaration() throws RecognitionException {
		MainMethodDeclarationContext _localctx = new MainMethodDeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_mainMethodDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78); match(2);
			setState(79); match(21);
			setState(80); match(14);
			setState(81); match(12);
			setState(82); match(8);
			setState(83); match(29);
			setState(84); match(5);
			setState(85); match(1);
			setState(86); match(ID);
			setState(87); match(24);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassBodyContext extends ParserRuleContext {
		public MethodDeclarationContext methodDeclaration(int i) {
			return getRuleContext(MethodDeclarationContext.class,i);
		}
		public List<VarDeclarationContext> varDeclaration() {
			return getRuleContexts(VarDeclarationContext.class);
		}
		public VarDeclarationContext varDeclaration(int i) {
			return getRuleContext(VarDeclarationContext.class,i);
		}
		public List<MethodDeclarationContext> methodDeclaration() {
			return getRuleContexts(MethodDeclarationContext.class);
		}
		public ClassBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterClassBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitClassBody(this);
		}
	}

	public final ClassBodyContext classBody() throws RecognitionException {
		ClassBodyContext _localctx = new ClassBodyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_classBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89); match(15);
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 10) | (1L << 18) | (1L << ID))) != 0)) {
				{
				{
				setState(90); varDeclaration();
				}
				}
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==2) {
				{
				{
				setState(96); methodDeclaration();
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(102); match(19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclarationContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VarDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitVarDeclaration(this);
		}
	}

	public final VarDeclarationContext varDeclaration() throws RecognitionException {
		VarDeclarationContext _localctx = new VarDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_varDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104); type();
			setState(105); match(ID);
			setState(106); match(30);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodDeclarationContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public List<VarDeclarationContext> varDeclaration() {
			return getRuleContexts(VarDeclarationContext.class);
		}
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VarDeclarationContext varDeclaration(int i) {
			return getRuleContext(VarDeclarationContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public MethodDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterMethodDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitMethodDeclaration(this);
		}
	}

	public final MethodDeclarationContext methodDeclaration() throws RecognitionException {
		MethodDeclarationContext _localctx = new MethodDeclarationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_methodDeclaration);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(108); match(2);
			setState(109); type();
			setState(110); match(ID);
			setState(111); formalParameters();
			setState(112); match(15);
			setState(116);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(113); varDeclaration();
					}
					} 
				}
				setState(118);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 4) | (1L << 9) | (1L << 15) | (1L << 23) | (1L << ID))) != 0)) {
				{
				{
				setState(119); statement();
				}
				}
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(125); match(28);
			setState(126); expression(0);
			setState(127); match(30);
			setState(128); match(19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParametersContext extends ParserRuleContext {
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public FormalParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterFormalParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitFormalParameters(this);
		}
	}

	public final FormalParametersContext formalParameters() throws RecognitionException {
		FormalParametersContext _localctx = new FormalParametersContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_formalParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130); match(8);
			setState(132);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 10) | (1L << 18) | (1L << ID))) != 0)) {
				{
				setState(131); formalParameterList();
				}
			}

			setState(134); match(24);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParameterListContext extends ParserRuleContext {
		public List<FormalParameterContext> formalParameter() {
			return getRuleContexts(FormalParameterContext.class);
		}
		public FormalParameterContext formalParameter(int i) {
			return getRuleContext(FormalParameterContext.class,i);
		}
		public FormalParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterFormalParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitFormalParameterList(this);
		}
	}

	public final FormalParameterListContext formalParameterList() throws RecognitionException {
		FormalParameterListContext _localctx = new FormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_formalParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136); formalParameter();
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==3) {
				{
				{
				setState(137); match(3);
				setState(138); formalParameter();
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParameterContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FormalParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterFormalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitFormalParameter(this);
		}
	}

	public final FormalParameterContext formalParameter() throws RecognitionException {
		FormalParameterContext _localctx = new FormalParameterContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_formalParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144); type();
			setState(145); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public BooleanTypeContext booleanType() {
			return getRuleContext(BooleanTypeContext.class,0);
		}
		public IntTypeContext intType() {
			return getRuleContext(IntTypeContext.class,0);
		}
		public IntArrayTypeContext intArrayType() {
			return getRuleContext(IntArrayTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_type);
		try {
			setState(151);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(147); intArrayType();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(148); booleanType();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(149); intType();
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(150); match(ID);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntArrayTypeContext extends ParserRuleContext {
		public IntArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intArrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterIntArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitIntArrayType(this);
		}
	}

	public final IntArrayTypeContext intArrayType() throws RecognitionException {
		IntArrayTypeContext _localctx = new IntArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_intArrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153); match(10);
			setState(154); match(5);
			setState(155); match(1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanTypeContext extends ParserRuleContext {
		public BooleanTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterBooleanType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitBooleanType(this);
		}
	}

	public final BooleanTypeContext booleanType() throws RecognitionException {
		BooleanTypeContext _localctx = new BooleanTypeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_booleanType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157); match(18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntTypeContext extends ParserRuleContext {
		public IntTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterIntType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitIntType(this);
		}
	}

	public final IntTypeContext intType() throws RecognitionException {
		IntTypeContext _localctx = new IntTypeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_intType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159); match(10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public IfElseStatementContext ifElseStatement() {
			return getRuleContext(IfElseStatementContext.class,0);
		}
		public ArrayAssignStatementContext arrayAssignStatement() {
			return getRuleContext(ArrayAssignStatementContext.class,0);
		}
		public AssignStatementContext assignStatement() {
			return getRuleContext(AssignStatementContext.class,0);
		}
		public WhileStatementContext whileStatement() {
			return getRuleContext(WhileStatementContext.class,0);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public PrintStatementContext printStatement() {
			return getRuleContext(PrintStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_statement);
		int _la;
		try {
			setState(174);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(161); match(15);
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 4) | (1L << 9) | (1L << 15) | (1L << 23) | (1L << ID))) != 0)) {
					{
					{
					setState(162); statement();
					}
					}
					setState(167);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(168); match(19);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(169); ifElseStatement();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(170); whileStatement();
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(171); printStatement();
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(172); assignStatement();
				}
				break;

			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(173); arrayAssignStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfElseStatementContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public IfElseStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifElseStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterIfElseStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitIfElseStatement(this);
		}
	}

	public final IfElseStatementContext ifElseStatement() throws RecognitionException {
		IfElseStatementContext _localctx = new IfElseStatementContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_ifElseStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176); match(9);
			setState(177); match(8);
			setState(178); expression(0);
			setState(179); match(24);
			setState(180); statement();
			setState(181); match(17);
			setState(182); statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhileStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public WhileStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitWhileStatement(this);
		}
	}

	public final WhileStatementContext whileStatement() throws RecognitionException {
		WhileStatementContext _localctx = new WhileStatementContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_whileStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184); match(4);
			setState(185); match(8);
			setState(186); expression(0);
			setState(187); match(24);
			setState(188); statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrintStatementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PrintStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_printStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterPrintStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitPrintStatement(this);
		}
	}

	public final PrintStatementContext printStatement() throws RecognitionException {
		PrintStatementContext _localctx = new PrintStatementContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_printStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190); match(23);
			setState(191); match(8);
			setState(192); expression(0);
			setState(193); match(24);
			setState(194); match(30);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignStatementContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterAssignStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitAssignStatement(this);
		}
	}

	public final AssignStatementContext assignStatement() throws RecognitionException {
		AssignStatementContext _localctx = new AssignStatementContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_assignStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196); match(ID);
			setState(197); match(27);
			setState(198); expression(0);
			setState(199); match(30);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayAssignStatementContext extends ParserRuleContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ArrayAssignStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayAssignStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterArrayAssignStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitArrayAssignStatement(this);
		}
	}

	public final ArrayAssignStatementContext arrayAssignStatement() throws RecognitionException {
		ArrayAssignStatementContext _localctx = new ArrayAssignStatementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_arrayAssignStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201); match(ID);
			setState(202); match(5);
			setState(203); expression(0);
			setState(204); match(1);
			setState(205); match(27);
			setState(206); expression(0);
			setState(207); match(30);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public int _p;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode INT() { return getToken(MiniJavaParser.INT, 0); }
		public BooleanLitContext booleanLit() {
			return getRuleContext(BooleanLitContext.class,0);
		}
		public TerminalNode ID() { return getToken(MiniJavaParser.ID, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ExpressionContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState, _p);
		ExpressionContext _prevctx = _localctx;
		int _startState = 44;
		enterRecursionRule(_localctx, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(210); match(35);
				setState(211); expression(2);
				}
				break;

			case 2:
				{
				setState(212); match(INT);
				}
				break;

			case 3:
				{
				setState(213); booleanLit();
				}
				break;

			case 4:
				{
				setState(214); match(ID);
				}
				break;

			case 5:
				{
				setState(215); match(32);
				}
				break;

			case 6:
				{
				setState(216); match(33);
				setState(217); match(10);
				setState(218); match(5);
				setState(219); expression(0);
				setState(220); match(1);
				}
				break;

			case 7:
				{
				setState(222); match(33);
				setState(223); match(ID);
				setState(224); match(8);
				setState(225); match(24);
				}
				break;

			case 8:
				{
				setState(226); match(8);
				setState(227); expression(0);
				setState(228); match(24);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(272);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(270);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(232);
						if (!(16 >= _localctx._p)) throw new FailedPredicateException(this, "16 >= $_p");
						setState(233); match(31);
						setState(234); expression(17);
						}
						break;

					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(235);
						if (!(15 >= _localctx._p)) throw new FailedPredicateException(this, "15 >= $_p");
						setState(236); match(11);
						setState(237); expression(16);
						}
						break;

					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(238);
						if (!(14 >= _localctx._p)) throw new FailedPredicateException(this, "14 >= $_p");
						setState(239); match(26);
						setState(240); expression(15);
						}
						break;

					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(241);
						if (!(13 >= _localctx._p)) throw new FailedPredicateException(this, "13 >= $_p");
						setState(242); match(6);
						setState(243); expression(14);
						}
						break;

					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(244);
						if (!(12 >= _localctx._p)) throw new FailedPredicateException(this, "12 >= $_p");
						setState(245); match(7);
						setState(246); expression(13);
						}
						break;

					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(247);
						if (!(11 >= _localctx._p)) throw new FailedPredicateException(this, "11 >= $_p");
						setState(248); match(5);
						setState(249); expression(0);
						setState(250); match(1);
						}
						break;

					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(252);
						if (!(10 >= _localctx._p)) throw new FailedPredicateException(this, "10 >= $_p");
						setState(253); match(25);
						setState(254); match(22);
						}
						break;

					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(255);
						if (!(9 >= _localctx._p)) throw new FailedPredicateException(this, "9 >= $_p");
						setState(256); match(25);
						setState(257); match(ID);
						setState(258); match(8);
						setState(267);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 8) | (1L << 13) | (1L << 20) | (1L << 32) | (1L << 33) | (1L << 35) | (1L << ID) | (1L << INT))) != 0)) {
							{
							setState(259); expression(0);
							setState(264);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==3) {
								{
								{
								setState(260); match(3);
								setState(261); expression(0);
								}
								}
								setState(266);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
						}

						setState(269); match(24);
						}
						break;
					}
					} 
				}
				setState(274);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class BooleanLitContext extends ParserRuleContext {
		public BooleanLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).enterBooleanLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MiniJavaListener ) ((MiniJavaListener)listener).exitBooleanLit(this);
		}
	}

	public final BooleanLitContext booleanLit() throws RecognitionException {
		BooleanLitContext _localctx = new BooleanLitContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_booleanLit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			_la = _input.LA(1);
			if ( !(_la==13 || _la==20) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 22: return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 16 >= _localctx._p;

		case 1: return 15 >= _localctx._p;

		case 2: return 14 >= _localctx._p;

		case 3: return 13 >= _localctx._p;

		case 4: return 12 >= _localctx._p;

		case 5: return 11 >= _localctx._p;

		case 6: return 10 >= _localctx._p;

		case 7: return 9 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3(\u0118\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\7\2\65\n\2\f\2\16\28\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4"+
		"\3\4\5\4D\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\7\b^\n\b\f\b\16\ba\13\b\3\b\7"+
		"\bd\n\b\f\b\16\bg\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\7\nu\n\n\f\n\16\nx\13\n\3\n\7\n{\n\n\f\n\16\n~\13\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\5\13\u0087\n\13\3\13\3\13\3\f\3\f\3\f\7\f\u008e\n\f\f\f\16"+
		"\f\u0091\13\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\5\16\u009a\n\16\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\7\22\u00a6\n\22\f\22\16\22\u00a9"+
		"\13\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u00b1\n\22\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00e9\n\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\7\30\u0109\n\30\f\30\16\30\u010c\13\30\5\30\u010e\n\30\3\30\7\30\u0111"+
		"\n\30\f\30\16\30\u0114\13\30\3\31\3\31\3\31\2\32\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&(*,.\60\2\3\4\2\17\17\26\26\u0121\2\62\3\2\2\2"+
		"\4;\3\2\2\2\6?\3\2\2\2\bG\3\2\2\2\nK\3\2\2\2\fP\3\2\2\2\16[\3\2\2\2\20"+
		"j\3\2\2\2\22n\3\2\2\2\24\u0084\3\2\2\2\26\u008a\3\2\2\2\30\u0092\3\2\2"+
		"\2\32\u0099\3\2\2\2\34\u009b\3\2\2\2\36\u009f\3\2\2\2 \u00a1\3\2\2\2\""+
		"\u00b0\3\2\2\2$\u00b2\3\2\2\2&\u00ba\3\2\2\2(\u00c0\3\2\2\2*\u00c6\3\2"+
		"\2\2,\u00cb\3\2\2\2.\u00e8\3\2\2\2\60\u0115\3\2\2\2\62\66\5\4\3\2\63\65"+
		"\5\6\4\2\64\63\3\2\2\2\658\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\679\3\2"+
		"\2\28\66\3\2\2\29:\7\2\2\3:\3\3\2\2\2;<\7$\2\2<=\7&\2\2=>\5\b\5\2>\5\3"+
		"\2\2\2?@\7$\2\2@C\7&\2\2AB\7\22\2\2BD\5\32\16\2CA\3\2\2\2CD\3\2\2\2DE"+
		"\3\2\2\2EF\5\16\b\2F\7\3\2\2\2GH\7\21\2\2HI\5\n\6\2IJ\7\25\2\2J\t\3\2"+
		"\2\2KL\5\f\7\2LM\7\21\2\2MN\5\"\22\2NO\7\25\2\2O\13\3\2\2\2PQ\7\4\2\2"+
		"QR\7\27\2\2RS\7\20\2\2ST\7\16\2\2TU\7\n\2\2UV\7\37\2\2VW\7\7\2\2WX\7\3"+
		"\2\2XY\7&\2\2YZ\7\32\2\2Z\r\3\2\2\2[_\7\21\2\2\\^\5\20\t\2]\\\3\2\2\2"+
		"^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`e\3\2\2\2a_\3\2\2\2bd\5\22\n\2cb\3\2\2"+
		"\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7\25\2\2i\17\3"+
		"\2\2\2jk\5\32\16\2kl\7&\2\2lm\7 \2\2m\21\3\2\2\2no\7\4\2\2op\5\32\16\2"+
		"pq\7&\2\2qr\5\24\13\2rv\7\21\2\2su\5\20\t\2ts\3\2\2\2ux\3\2\2\2vt\3\2"+
		"\2\2vw\3\2\2\2w|\3\2\2\2xv\3\2\2\2y{\5\"\22\2zy\3\2\2\2{~\3\2\2\2|z\3"+
		"\2\2\2|}\3\2\2\2}\177\3\2\2\2~|\3\2\2\2\177\u0080\7\36\2\2\u0080\u0081"+
		"\5.\30\2\u0081\u0082\7 \2\2\u0082\u0083\7\25\2\2\u0083\23\3\2\2\2\u0084"+
		"\u0086\7\n\2\2\u0085\u0087\5\26\f\2\u0086\u0085\3\2\2\2\u0086\u0087\3"+
		"\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089\7\32\2\2\u0089\25\3\2\2\2\u008a"+
		"\u008f\5\30\r\2\u008b\u008c\7\5\2\2\u008c\u008e\5\30\r\2\u008d\u008b\3"+
		"\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090"+
		"\27\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u0093\5\32\16\2\u0093\u0094\7&\2"+
		"\2\u0094\31\3\2\2\2\u0095\u009a\5\34\17\2\u0096\u009a\5\36\20\2\u0097"+
		"\u009a\5 \21\2\u0098\u009a\7&\2\2\u0099\u0095\3\2\2\2\u0099\u0096\3\2"+
		"\2\2\u0099\u0097\3\2\2\2\u0099\u0098\3\2\2\2\u009a\33\3\2\2\2\u009b\u009c"+
		"\7\f\2\2\u009c\u009d\7\7\2\2\u009d\u009e\7\3\2\2\u009e\35\3\2\2\2\u009f"+
		"\u00a0\7\24\2\2\u00a0\37\3\2\2\2\u00a1\u00a2\7\f\2\2\u00a2!\3\2\2\2\u00a3"+
		"\u00a7\7\21\2\2\u00a4\u00a6\5\"\22\2\u00a5\u00a4\3\2\2\2\u00a6\u00a9\3"+
		"\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9"+
		"\u00a7\3\2\2\2\u00aa\u00b1\7\25\2\2\u00ab\u00b1\5$\23\2\u00ac\u00b1\5"+
		"&\24\2\u00ad\u00b1\5(\25\2\u00ae\u00b1\5*\26\2\u00af\u00b1\5,\27\2\u00b0"+
		"\u00a3\3\2\2\2\u00b0\u00ab\3\2\2\2\u00b0\u00ac\3\2\2\2\u00b0\u00ad\3\2"+
		"\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00af\3\2\2\2\u00b1#\3\2\2\2\u00b2\u00b3"+
		"\7\13\2\2\u00b3\u00b4\7\n\2\2\u00b4\u00b5\5.\30\2\u00b5\u00b6\7\32\2\2"+
		"\u00b6\u00b7\5\"\22\2\u00b7\u00b8\7\23\2\2\u00b8\u00b9\5\"\22\2\u00b9"+
		"%\3\2\2\2\u00ba\u00bb\7\6\2\2\u00bb\u00bc\7\n\2\2\u00bc\u00bd\5.\30\2"+
		"\u00bd\u00be\7\32\2\2\u00be\u00bf\5\"\22\2\u00bf\'\3\2\2\2\u00c0\u00c1"+
		"\7\31\2\2\u00c1\u00c2\7\n\2\2\u00c2\u00c3\5.\30\2\u00c3\u00c4\7\32\2\2"+
		"\u00c4\u00c5\7 \2\2\u00c5)\3\2\2\2\u00c6\u00c7\7&\2\2\u00c7\u00c8\7\35"+
		"\2\2\u00c8\u00c9\5.\30\2\u00c9\u00ca\7 \2\2\u00ca+\3\2\2\2\u00cb\u00cc"+
		"\7&\2\2\u00cc\u00cd\7\7\2\2\u00cd\u00ce\5.\30\2\u00ce\u00cf\7\3\2\2\u00cf"+
		"\u00d0\7\35\2\2\u00d0\u00d1\5.\30\2\u00d1\u00d2\7 \2\2\u00d2-\3\2\2\2"+
		"\u00d3\u00d4\b\30\1\2\u00d4\u00d5\7%\2\2\u00d5\u00e9\5.\30\2\u00d6\u00e9"+
		"\7\'\2\2\u00d7\u00e9\5\60\31\2\u00d8\u00e9\7&\2\2\u00d9\u00e9\7\"\2\2"+
		"\u00da\u00db\7#\2\2\u00db\u00dc\7\f\2\2\u00dc\u00dd\7\7\2\2\u00dd\u00de"+
		"\5.\30\2\u00de\u00df\7\3\2\2\u00df\u00e9\3\2\2\2\u00e0\u00e1\7#\2\2\u00e1"+
		"\u00e2\7&\2\2\u00e2\u00e3\7\n\2\2\u00e3\u00e9\7\32\2\2\u00e4\u00e5\7\n"+
		"\2\2\u00e5\u00e6\5.\30\2\u00e6\u00e7\7\32\2\2\u00e7\u00e9\3\2\2\2\u00e8"+
		"\u00d3\3\2\2\2\u00e8\u00d6\3\2\2\2\u00e8\u00d7\3\2\2\2\u00e8\u00d8\3\2"+
		"\2\2\u00e8\u00d9\3\2\2\2\u00e8\u00da\3\2\2\2\u00e8\u00e0\3\2\2\2\u00e8"+
		"\u00e4\3\2\2\2\u00e9\u0112\3\2\2\2\u00ea\u00eb\6\30\2\3\u00eb\u00ec\7"+
		"!\2\2\u00ec\u0111\5.\30\2\u00ed\u00ee\6\30\3\3\u00ee\u00ef\7\r\2\2\u00ef"+
		"\u0111\5.\30\2\u00f0\u00f1\6\30\4\3\u00f1\u00f2\7\34\2\2\u00f2\u0111\5"+
		".\30\2\u00f3\u00f4\6\30\5\3\u00f4\u00f5\7\b\2\2\u00f5\u0111\5.\30\2\u00f6"+
		"\u00f7\6\30\6\3\u00f7\u00f8\7\t\2\2\u00f8\u0111\5.\30\2\u00f9\u00fa\6"+
		"\30\7\3\u00fa\u00fb\7\7\2\2\u00fb\u00fc\5.\30\2\u00fc\u00fd\7\3\2\2\u00fd"+
		"\u0111\3\2\2\2\u00fe\u00ff\6\30\b\3\u00ff\u0100\7\33\2\2\u0100\u0111\7"+
		"\30\2\2\u0101\u0102\6\30\t\3\u0102\u0103\7\33\2\2\u0103\u0104\7&\2\2\u0104"+
		"\u010d\7\n\2\2\u0105\u010a\5.\30\2\u0106\u0107\7\5\2\2\u0107\u0109\5."+
		"\30\2\u0108\u0106\3\2\2\2\u0109\u010c\3\2\2\2\u010a\u0108\3\2\2\2\u010a"+
		"\u010b\3\2\2\2\u010b\u010e\3\2\2\2\u010c\u010a\3\2\2\2\u010d\u0105\3\2"+
		"\2\2\u010d\u010e\3\2\2\2\u010e\u010f\3\2\2\2\u010f\u0111\7\32\2\2\u0110"+
		"\u00ea\3\2\2\2\u0110\u00ed\3\2\2\2\u0110\u00f0\3\2\2\2\u0110\u00f3\3\2"+
		"\2\2\u0110\u00f6\3\2\2\2\u0110\u00f9\3\2\2\2\u0110\u00fe\3\2\2\2\u0110"+
		"\u0101\3\2\2\2\u0111\u0114\3\2\2\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2"+
		"\2\2\u0113/\3\2\2\2\u0114\u0112\3\2\2\2\u0115\u0116\t\2\2\2\u0116\61\3"+
		"\2\2\2\22\66C_ev|\u0086\u008f\u0099\u00a7\u00b0\u00e8\u010a\u010d\u0110"+
		"\u0112";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}