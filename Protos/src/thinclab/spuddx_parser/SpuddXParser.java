// Generated from SpuddX.g4 by ANTLR 4.5
package thinclab.spuddx_parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SpuddXParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, POMDP=8, UNIFORM=9, 
		OP_ADD=10, OP_SUB=11, OP_MUL=12, OP_DIV=13, IDENTIFIER=14, FLOAT_NUM=15, 
		LP=16, RP=17, WS=18;
	public static final int
		RULE_domain = 0, RULE_var_decls = 1, RULE_model_decl = 2, RULE_pomdp_decl = 3, 
		RULE_var_list = 4, RULE_actions_list = 5, RULE_agent_name = 6, RULE_dd_decls = 7, 
		RULE_dd_expr = 8, RULE_dd_decl = 9, RULE_dd_ref = 10, RULE_dd_leaf = 11, 
		RULE_same_dd_decl = 12, RULE_rv_decl = 13, RULE_env_def = 14, RULE_actiondbn_def = 15, 
		RULE_actions = 16, RULE_cpd_def = 17, RULE_dd_name = 18, RULE_variable_name = 19, 
		RULE_var_value = 20;
	public static final String[] ruleNames = {
		"domain", "var_decls", "model_decl", "pomdp_decl", "var_list", "actions_list", 
		"agent_name", "dd_decls", "dd_expr", "dd_decl", "dd_ref", "dd_leaf", "same_dd_decl", 
		"rv_decl", "env_def", "actiondbn_def", "actions", "cpd_def", "dd_name", 
		"variable_name", "var_value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'model'", "'actions'", "'dd'", "'SAME'", "'env'", 
		"'actiondbn'", "'POMDP'", null, "'+'", "'-'", "'*'", "'/'", null, null, 
		"'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "POMDP", "UNIFORM", "OP_ADD", 
		"OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SpuddX.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SpuddXParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DomainContext extends ParserRuleContext {
		public Var_declsContext var_decls() {
			return getRuleContext(Var_declsContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SpuddXParser.EOF, 0); }
		public List<Model_declContext> model_decl() {
			return getRuleContexts(Model_declContext.class);
		}
		public Model_declContext model_decl(int i) {
			return getRuleContext(Model_declContext.class,i);
		}
		public List<Dd_declsContext> dd_decls() {
			return getRuleContexts(Dd_declsContext.class);
		}
		public Dd_declsContext dd_decls(int i) {
			return getRuleContext(Dd_declsContext.class,i);
		}
		public Env_defContext env_def() {
			return getRuleContext(Env_defContext.class,0);
		}
		public DomainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDomain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDomain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDomain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DomainContext domain() throws RecognitionException {
		DomainContext _localctx = new DomainContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_domain);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			var_decls();
			setState(46);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(43);
					model_decl();
					}
					} 
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(52);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(49);
					dd_decls();
					}
					} 
				}
				setState(54);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			setState(56);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(55);
				env_def();
				}
			}

			setState(58);
			match(EOF);
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

	public static class Var_declsContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Rv_declContext> rv_decl() {
			return getRuleContexts(Rv_declContext.class);
		}
		public Rv_declContext rv_decl(int i) {
			return getRuleContext(Rv_declContext.class,i);
		}
		public Var_declsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_decls; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVar_decls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVar_decls(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVar_decls(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_declsContext var_decls() throws RecognitionException {
		Var_declsContext _localctx = new Var_declsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_var_decls);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(LP);
			setState(61);
			match(T__0);
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				rv_decl();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(67);
			match(RP);
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

	public static class Model_declContext extends ParserRuleContext {
		public Pomdp_declContext pomdp_decl() {
			return getRuleContext(Pomdp_declContext.class,0);
		}
		public Model_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModel_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModel_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModel_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_declContext model_decl() throws RecognitionException {
		Model_declContext _localctx = new Model_declContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_model_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			pomdp_decl();
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

	public static class Pomdp_declContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode POMDP() { return getToken(SpuddXParser.POMDP, 0); }
		public Agent_nameContext agent_name() {
			return getRuleContext(Agent_nameContext.class,0);
		}
		public Var_listContext var_list() {
			return getRuleContext(Var_listContext.class,0);
		}
		public Actions_listContext actions_list() {
			return getRuleContext(Actions_listContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Pomdp_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pomdp_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPomdp_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPomdp_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPomdp_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pomdp_declContext pomdp_decl() throws RecognitionException {
		Pomdp_declContext _localctx = new Pomdp_declContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_pomdp_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(LP);
			setState(72);
			match(T__1);
			setState(73);
			match(POMDP);
			setState(74);
			agent_name();
			setState(75);
			var_list();
			setState(76);
			actions_list();
			setState(77);
			match(RP);
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

	public static class Var_listContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpuddXParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SpuddXParser.IDENTIFIER, i);
		}
		public Var_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVar_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVar_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVar_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_listContext var_list() throws RecognitionException {
		Var_listContext _localctx = new Var_listContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(LP);
			setState(80);
			match(T__0);
			setState(82); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(81);
				match(IDENTIFIER);
				}
				}
				setState(84); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(86);
			match(RP);
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

	public static class Actions_listContext extends ParserRuleContext {
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpuddXParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SpuddXParser.IDENTIFIER, i);
		}
		public Actions_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actions_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterActions_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitActions_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitActions_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Actions_listContext actions_list() throws RecognitionException {
		Actions_listContext _localctx = new Actions_listContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_actions_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(RP);
			setState(89);
			match(T__2);
			setState(91); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(90);
				match(IDENTIFIER);
				}
				}
				setState(93); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(95);
			match(RP);
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

	public static class Agent_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Agent_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agent_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAgent_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAgent_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAgent_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Agent_nameContext agent_name() throws RecognitionException {
		Agent_nameContext _localctx = new Agent_nameContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_agent_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(IDENTIFIER);
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

	public static class Dd_declsContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Dd_nameContext dd_name() {
			return getRuleContext(Dd_nameContext.class,0);
		}
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Dd_declsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_decls; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_decls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_decls(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_decls(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_declsContext dd_decls() throws RecognitionException {
		Dd_declsContext _localctx = new Dd_declsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_dd_decls);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(LP);
			setState(100);
			match(T__3);
			setState(101);
			dd_name();
			setState(102);
			dd_expr(0);
			setState(103);
			match(RP);
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

	public static class Dd_exprContext extends ParserRuleContext {
		public Dd_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_expr; }
	 
		public Dd_exprContext() { }
		public void copyFrom(Dd_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MultDivExprContext extends Dd_exprContext {
		public Dd_exprContext left;
		public Token op;
		public Dd_exprContext right;
		public List<Dd_exprContext> dd_expr() {
			return getRuleContexts(Dd_exprContext.class);
		}
		public Dd_exprContext dd_expr(int i) {
			return getRuleContext(Dd_exprContext.class,i);
		}
		public TerminalNode OP_MUL() { return getToken(SpuddXParser.OP_MUL, 0); }
		public TerminalNode OP_DIV() { return getToken(SpuddXParser.OP_DIV, 0); }
		public MultDivExprContext(Dd_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterMultDivExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitMultDivExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitMultDivExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AtomicExprContext extends Dd_exprContext {
		public Dd_declContext dd_decl() {
			return getRuleContext(Dd_declContext.class,0);
		}
		public AtomicExprContext(Dd_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAtomicExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAtomicExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAtomicExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegExprContext extends Dd_exprContext {
		public Token op;
		public Dd_exprContext term;
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public TerminalNode OP_ADD() { return getToken(SpuddXParser.OP_ADD, 0); }
		public TerminalNode OP_SUB() { return getToken(SpuddXParser.OP_SUB, 0); }
		public NegExprContext(Dd_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterNegExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitNegExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitNegExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenExprContext extends Dd_exprContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public ParenExprContext(Dd_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterParenExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitParenExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitParenExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AddSubExprContext extends Dd_exprContext {
		public Dd_exprContext left;
		public Token op;
		public Dd_exprContext right;
		public List<Dd_exprContext> dd_expr() {
			return getRuleContexts(Dd_exprContext.class);
		}
		public Dd_exprContext dd_expr(int i) {
			return getRuleContext(Dd_exprContext.class,i);
		}
		public TerminalNode OP_ADD() { return getToken(SpuddXParser.OP_ADD, 0); }
		public TerminalNode OP_SUB() { return getToken(SpuddXParser.OP_SUB, 0); }
		public AddSubExprContext(Dd_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAddSubExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAddSubExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAddSubExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_exprContext dd_expr() throws RecognitionException {
		return dd_expr(0);
	}

	private Dd_exprContext dd_expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Dd_exprContext _localctx = new Dd_exprContext(_ctx, _parentState);
		Dd_exprContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_dd_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				_localctx = new NegExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(106);
				((NegExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==OP_ADD || _la==OP_SUB) ) {
					((NegExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(107);
				((NegExprContext)_localctx).term = dd_expr(4);
				}
				break;
			case 2:
				{
				_localctx = new AtomicExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				dd_decl();
				}
				break;
			case 3:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(109);
				match(LP);
				setState(110);
				dd_expr(0);
				setState(111);
				match(RP);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(123);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(121);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						_localctx = new MultDivExprContext(new Dd_exprContext(_parentctx, _parentState));
						((MultDivExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(115);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(116);
						((MultDivExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_MUL || _la==OP_DIV) ) {
							((MultDivExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(117);
						((MultDivExprContext)_localctx).right = dd_expr(3);
						}
						break;
					case 2:
						{
						_localctx = new AddSubExprContext(new Dd_exprContext(_parentctx, _parentState));
						((AddSubExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(118);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(119);
						((AddSubExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_ADD || _la==OP_SUB) ) {
							((AddSubExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(120);
						((AddSubExprContext)_localctx).right = dd_expr(2);
						}
						break;
					}
					} 
				}
				setState(125);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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

	public static class Dd_declContext extends ParserRuleContext {
		public Dd_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_decl; }
	 
		public Dd_declContext() { }
		public void copyFrom(Dd_declContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DDDeclContext extends Dd_declContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
		public List<Var_valueContext> var_value() {
			return getRuleContexts(Var_valueContext.class);
		}
		public Var_valueContext var_value(int i) {
			return getRuleContext(Var_valueContext.class,i);
		}
		public List<Dd_exprContext> dd_expr() {
			return getRuleContexts(Dd_exprContext.class);
		}
		public Dd_exprContext dd_expr(int i) {
			return getRuleContext(Dd_exprContext.class,i);
		}
		public DDDeclContext(Dd_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDDecl(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DDleafContext extends Dd_declContext {
		public Dd_leafContext dd_leaf() {
			return getRuleContext(Dd_leafContext.class,0);
		}
		public DDleafContext(Dd_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDleaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDleaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDleaf(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DDDeterministicContext extends Dd_declContext {
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public Var_valueContext var_value() {
			return getRuleContext(Var_valueContext.class,0);
		}
		public DDDeterministicContext(Dd_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDDeterministic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDDeterministic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDDeterministic(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SameDDContext extends Dd_declContext {
		public Same_dd_declContext same_dd_decl() {
			return getRuleContext(Same_dd_declContext.class,0);
		}
		public SameDDContext(Dd_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterSameDD(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitSameDD(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitSameDD(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DDRefContext extends Dd_declContext {
		public Dd_refContext dd_ref() {
			return getRuleContext(Dd_refContext.class,0);
		}
		public DDRefContext(Dd_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DDUniformContext extends Dd_declContext {
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public TerminalNode UNIFORM() { return getToken(SpuddXParser.UNIFORM, 0); }
		public DDUniformContext(Dd_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDUniform(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDUniform(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDUniform(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_declContext dd_decl() throws RecognitionException {
		Dd_declContext _localctx = new Dd_declContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_dd_decl);
		int _la;
		try {
			setState(148);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				_localctx = new DDDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(126);
				match(LP);
				setState(127);
				variable_name();
				setState(133); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(128);
					match(LP);
					setState(129);
					var_value();
					setState(130);
					dd_expr(0);
					setState(131);
					match(RP);
					}
					}
					setState(135); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LP );
				setState(137);
				match(RP);
				}
				break;
			case 2:
				_localctx = new DDleafContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				dd_leaf();
				}
				break;
			case 3:
				_localctx = new SameDDContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(140);
				same_dd_decl();
				}
				break;
			case 4:
				_localctx = new DDRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(141);
				dd_ref();
				}
				break;
			case 5:
				_localctx = new DDDeterministicContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(142);
				variable_name();
				setState(143);
				var_value();
				}
				break;
			case 6:
				_localctx = new DDUniformContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(145);
				variable_name();
				setState(146);
				match(UNIFORM);
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

	public static class Dd_refContext extends ParserRuleContext {
		public Dd_nameContext dd_name() {
			return getRuleContext(Dd_nameContext.class,0);
		}
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Dd_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_ref(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_ref(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_refContext dd_ref() throws RecognitionException {
		Dd_refContext _localctx = new Dd_refContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_dd_ref);
		try {
			setState(155);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(150);
				dd_name();
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				match(LP);
				setState(152);
				dd_name();
				setState(153);
				match(RP);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class Dd_leafContext extends ParserRuleContext {
		public TerminalNode FLOAT_NUM() { return getToken(SpuddXParser.FLOAT_NUM, 0); }
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Dd_leafContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_leaf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_leaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_leaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_leaf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_leafContext dd_leaf() throws RecognitionException {
		Dd_leafContext _localctx = new Dd_leafContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_dd_leaf);
		try {
			setState(161);
			switch (_input.LA(1)) {
			case FLOAT_NUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				match(FLOAT_NUM);
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				match(LP);
				setState(159);
				match(FLOAT_NUM);
				setState(160);
				match(RP);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class Same_dd_declContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Same_dd_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_same_dd_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterSame_dd_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitSame_dd_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitSame_dd_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Same_dd_declContext same_dd_decl() throws RecognitionException {
		Same_dd_declContext _localctx = new Same_dd_declContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_same_dd_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(LP);
			setState(164);
			match(T__4);
			setState(165);
			variable_name();
			setState(166);
			match(RP);
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

	public static class Rv_declContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Var_valueContext> var_value() {
			return getRuleContexts(Var_valueContext.class);
		}
		public Var_valueContext var_value(int i) {
			return getRuleContext(Var_valueContext.class,i);
		}
		public Rv_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rv_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterRv_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitRv_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitRv_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rv_declContext rv_decl() throws RecognitionException {
		Rv_declContext _localctx = new Rv_declContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_rv_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(LP);
			setState(169);
			variable_name();
			setState(171); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(170);
				var_value();
				}
				}
				setState(173); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(175);
			match(RP);
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

	public static class Env_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Actiondbn_defContext> actiondbn_def() {
			return getRuleContexts(Actiondbn_defContext.class);
		}
		public Actiondbn_defContext actiondbn_def(int i) {
			return getRuleContext(Actiondbn_defContext.class,i);
		}
		public Env_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_env_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterEnv_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitEnv_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitEnv_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Env_defContext env_def() throws RecognitionException {
		Env_defContext _localctx = new Env_defContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_env_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			match(LP);
			setState(178);
			match(T__5);
			setState(180); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(179);
				actiondbn_def();
				}
				}
				setState(182); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(184);
			match(RP);
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

	public static class Actiondbn_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public ActionsContext actions() {
			return getRuleContext(ActionsContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Cpd_defContext> cpd_def() {
			return getRuleContexts(Cpd_defContext.class);
		}
		public Cpd_defContext cpd_def(int i) {
			return getRuleContext(Cpd_defContext.class,i);
		}
		public Actiondbn_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actiondbn_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterActiondbn_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitActiondbn_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitActiondbn_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Actiondbn_defContext actiondbn_def() throws RecognitionException {
		Actiondbn_defContext _localctx = new Actiondbn_defContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_actiondbn_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			match(LP);
			setState(187);
			match(T__6);
			setState(188);
			actions();
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LP) {
				{
				{
				setState(189);
				cpd_def();
				}
				}
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(195);
			match(RP);
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

	public static class ActionsContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpuddXParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SpuddXParser.IDENTIFIER, i);
		}
		public ActionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitActions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitActions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionsContext actions() throws RecognitionException {
		ActionsContext _localctx = new ActionsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_actions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(197);
				match(IDENTIFIER);
				}
				}
				setState(200); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
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

	public static class Cpd_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public Dd_declContext dd_decl() {
			return getRuleContext(Dd_declContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Cpd_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cpd_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterCpd_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitCpd_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitCpd_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Cpd_defContext cpd_def() throws RecognitionException {
		Cpd_defContext _localctx = new Cpd_defContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_cpd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(LP);
			setState(203);
			variable_name();
			setState(204);
			dd_decl();
			setState(205);
			match(RP);
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

	public static class Dd_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Dd_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_nameContext dd_name() throws RecognitionException {
		Dd_nameContext _localctx = new Dd_nameContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_dd_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(IDENTIFIER);
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

	public static class Variable_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Variable_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVariable_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVariable_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVariable_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_nameContext variable_name() throws RecognitionException {
		Variable_nameContext _localctx = new Variable_nameContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_variable_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(IDENTIFIER);
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

	public static class Var_valueContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Var_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVar_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVar_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVar_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_valueContext var_value() throws RecognitionException {
		Var_valueContext _localctx = new Var_valueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(IDENTIFIER);
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
		case 8:
			return dd_expr_sempred((Dd_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean dd_expr_sempred(Dd_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\24\u00d8\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\7\2/\n\2\f\2\16\2\62"+
		"\13\2\3\2\7\2\65\n\2\f\2\16\28\13\2\3\2\5\2;\n\2\3\2\3\2\3\3\3\3\3\3\6"+
		"\3B\n\3\r\3\16\3C\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6"+
		"\3\6\3\6\6\6U\n\6\r\6\16\6V\3\6\3\6\3\7\3\7\3\7\6\7^\n\7\r\7\16\7_\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5"+
		"\nt\n\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n|\n\n\f\n\16\n\177\13\n\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\6\13\u0088\n\13\r\13\16\13\u0089\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u0097\n\13\3\f\3\f\3\f\3"+
		"\f\3\f\5\f\u009e\n\f\3\r\3\r\3\r\3\r\5\r\u00a4\n\r\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\6\17\u00ae\n\17\r\17\16\17\u00af\3\17\3\17\3\20\3"+
		"\20\3\20\6\20\u00b7\n\20\r\20\16\20\u00b8\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\7\21\u00c1\n\21\f\21\16\21\u00c4\13\21\3\21\3\21\3\22\6\22\u00c9\n\22"+
		"\r\22\16\22\u00ca\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3"+
		"\26\3\26\2\3\22\27\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*\2\4\3"+
		"\2\f\r\3\2\16\17\u00d8\2,\3\2\2\2\4>\3\2\2\2\6G\3\2\2\2\bI\3\2\2\2\nQ"+
		"\3\2\2\2\fZ\3\2\2\2\16c\3\2\2\2\20e\3\2\2\2\22s\3\2\2\2\24\u0096\3\2\2"+
		"\2\26\u009d\3\2\2\2\30\u00a3\3\2\2\2\32\u00a5\3\2\2\2\34\u00aa\3\2\2\2"+
		"\36\u00b3\3\2\2\2 \u00bc\3\2\2\2\"\u00c8\3\2\2\2$\u00cc\3\2\2\2&\u00d1"+
		"\3\2\2\2(\u00d3\3\2\2\2*\u00d5\3\2\2\2,\60\5\4\3\2-/\5\6\4\2.-\3\2\2\2"+
		"/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\66\3\2\2\2\62\60\3\2\2\2\63"+
		"\65\5\20\t\2\64\63\3\2\2\2\658\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\67"+
		":\3\2\2\28\66\3\2\2\29;\5\36\20\2:9\3\2\2\2:;\3\2\2\2;<\3\2\2\2<=\7\2"+
		"\2\3=\3\3\2\2\2>?\7\22\2\2?A\7\3\2\2@B\5\34\17\2A@\3\2\2\2BC\3\2\2\2C"+
		"A\3\2\2\2CD\3\2\2\2DE\3\2\2\2EF\7\23\2\2F\5\3\2\2\2GH\5\b\5\2H\7\3\2\2"+
		"\2IJ\7\22\2\2JK\7\4\2\2KL\7\n\2\2LM\5\16\b\2MN\5\n\6\2NO\5\f\7\2OP\7\23"+
		"\2\2P\t\3\2\2\2QR\7\22\2\2RT\7\3\2\2SU\7\20\2\2TS\3\2\2\2UV\3\2\2\2VT"+
		"\3\2\2\2VW\3\2\2\2WX\3\2\2\2XY\7\23\2\2Y\13\3\2\2\2Z[\7\23\2\2[]\7\5\2"+
		"\2\\^\7\20\2\2]\\\3\2\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2\2\2`a\3\2\2\2ab\7"+
		"\23\2\2b\r\3\2\2\2cd\7\20\2\2d\17\3\2\2\2ef\7\22\2\2fg\7\6\2\2gh\5&\24"+
		"\2hi\5\22\n\2ij\7\23\2\2j\21\3\2\2\2kl\b\n\1\2lm\t\2\2\2mt\5\22\n\6nt"+
		"\5\24\13\2op\7\22\2\2pq\5\22\n\2qr\7\23\2\2rt\3\2\2\2sk\3\2\2\2sn\3\2"+
		"\2\2so\3\2\2\2t}\3\2\2\2uv\f\4\2\2vw\t\3\2\2w|\5\22\n\5xy\f\3\2\2yz\t"+
		"\2\2\2z|\5\22\n\4{u\3\2\2\2{x\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2"+
		"~\23\3\2\2\2\177}\3\2\2\2\u0080\u0081\7\22\2\2\u0081\u0087\5(\25\2\u0082"+
		"\u0083\7\22\2\2\u0083\u0084\5*\26\2\u0084\u0085\5\22\n\2\u0085\u0086\7"+
		"\23\2\2\u0086\u0088\3\2\2\2\u0087\u0082\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\7\23"+
		"\2\2\u008c\u0097\3\2\2\2\u008d\u0097\5\30\r\2\u008e\u0097\5\32\16\2\u008f"+
		"\u0097\5\26\f\2\u0090\u0091\5(\25\2\u0091\u0092\5*\26\2\u0092\u0097\3"+
		"\2\2\2\u0093\u0094\5(\25\2\u0094\u0095\7\13\2\2\u0095\u0097\3\2\2\2\u0096"+
		"\u0080\3\2\2\2\u0096\u008d\3\2\2\2\u0096\u008e\3\2\2\2\u0096\u008f\3\2"+
		"\2\2\u0096\u0090\3\2\2\2\u0096\u0093\3\2\2\2\u0097\25\3\2\2\2\u0098\u009e"+
		"\5&\24\2\u0099\u009a\7\22\2\2\u009a\u009b\5&\24\2\u009b\u009c\7\23\2\2"+
		"\u009c\u009e\3\2\2\2\u009d\u0098\3\2\2\2\u009d\u0099\3\2\2\2\u009e\27"+
		"\3\2\2\2\u009f\u00a4\7\21\2\2\u00a0\u00a1\7\22\2\2\u00a1\u00a2\7\21\2"+
		"\2\u00a2\u00a4\7\23\2\2\u00a3\u009f\3\2\2\2\u00a3\u00a0\3\2\2\2\u00a4"+
		"\31\3\2\2\2\u00a5\u00a6\7\22\2\2\u00a6\u00a7\7\7\2\2\u00a7\u00a8\5(\25"+
		"\2\u00a8\u00a9\7\23\2\2\u00a9\33\3\2\2\2\u00aa\u00ab\7\22\2\2\u00ab\u00ad"+
		"\5(\25\2\u00ac\u00ae\5*\26\2\u00ad\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\7\23"+
		"\2\2\u00b2\35\3\2\2\2\u00b3\u00b4\7\22\2\2\u00b4\u00b6\7\b\2\2\u00b5\u00b7"+
		"\5 \21\2\u00b6\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bb\7\23\2\2\u00bb\37\3\2\2"+
		"\2\u00bc\u00bd\7\22\2\2\u00bd\u00be\7\t\2\2\u00be\u00c2\5\"\22\2\u00bf"+
		"\u00c1\5$\23\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2"+
		"\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5"+
		"\u00c6\7\23\2\2\u00c6!\3\2\2\2\u00c7\u00c9\7\20\2\2\u00c8\u00c7\3\2\2"+
		"\2\u00c9\u00ca\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb#"+
		"\3\2\2\2\u00cc\u00cd\7\22\2\2\u00cd\u00ce\5(\25\2\u00ce\u00cf\5\24\13"+
		"\2\u00cf\u00d0\7\23\2\2\u00d0%\3\2\2\2\u00d1\u00d2\7\20\2\2\u00d2\'\3"+
		"\2\2\2\u00d3\u00d4\7\20\2\2\u00d4)\3\2\2\2\u00d5\u00d6\7\20\2\2\u00d6"+
		"+\3\2\2\2\23\60\66:CV_s{}\u0089\u0096\u009d\u00a3\u00af\u00b8\u00c2\u00ca";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}