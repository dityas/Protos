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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, POMDP=9, 
		UNIFORM=10, OP_ADD=11, OP_SUB=12, OP_MUL=13, OP_DIV=14, IDENTIFIER=15, 
		FLOAT_NUM=16, LP=17, RP=18, WS=19;
	public static final int
		RULE_domain = 0, RULE_var_decls = 1, RULE_model_decl = 2, RULE_pomdp_def = 3, 
		RULE_states_list = 4, RULE_obs_list = 5, RULE_action_var = 6, RULE_var_list = 7, 
		RULE_actions_list = 8, RULE_agent_name = 9, RULE_dd_decls = 10, RULE_dd_expr = 11, 
		RULE_dd_decl = 12, RULE_dd_ref = 13, RULE_dd_leaf = 14, RULE_same_dd_decl = 15, 
		RULE_rv_decl = 16, RULE_dbn_def = 17, RULE_cpd_def = 18, RULE_dbn_name = 19, 
		RULE_dd_name = 20, RULE_variable_name = 21, RULE_var_value = 22;
	public static final String[] ruleNames = {
		"domain", "var_decls", "model_decl", "pomdp_def", "states_list", "obs_list", 
		"action_var", "var_list", "actions_list", "agent_name", "dd_decls", "dd_expr", 
		"dd_decl", "dd_ref", "dd_leaf", "same_dd_decl", "rv_decl", "dbn_def", 
		"cpd_def", "dbn_name", "dd_name", "variable_name", "var_value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'S'", "'O'", "'A'", "'actions'", "'dd'", "'SAME'", 
		"'dbn'", null, null, "'+'", "'-'", "'*'", "'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "POMDP", "UNIFORM", 
		"OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", 
		"RP", "WS"
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
		public List<Dd_declsContext> dd_decls() {
			return getRuleContexts(Dd_declsContext.class);
		}
		public Dd_declsContext dd_decls(int i) {
			return getRuleContext(Dd_declsContext.class,i);
		}
		public List<Model_declContext> model_decl() {
			return getRuleContexts(Model_declContext.class);
		}
		public Model_declContext model_decl(int i) {
			return getRuleContext(Model_declContext.class,i);
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
			setState(46);
			var_decls();
			setState(50);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(47);
					dd_decls();
					}
					} 
				}
				setState(52);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LP) {
				{
				{
				setState(53);
				model_decl();
				}
				}
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59);
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
			setState(61);
			match(LP);
			setState(62);
			match(T__0);
			setState(64); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(63);
				rv_decl();
				}
				}
				setState(66); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(68);
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
		public Model_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_decl; }
	 
		public Model_declContext() { }
		public void copyFrom(Model_declContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DBNDefContext extends Model_declContext {
		public Dbn_defContext dbn_def() {
			return getRuleContext(Dbn_defContext.class,0);
		}
		public DBNDefContext(Model_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDBNDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDBNDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDBNDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class POMDPDefContext extends Model_declContext {
		public Pomdp_defContext pomdp_def() {
			return getRuleContext(Pomdp_defContext.class,0);
		}
		public POMDPDefContext(Model_declContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPOMDPDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPOMDPDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPOMDPDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_declContext model_decl() throws RecognitionException {
		Model_declContext _localctx = new Model_declContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_model_decl);
		try {
			setState(72);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new POMDPDefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				pomdp_def();
				}
				break;
			case 2:
				_localctx = new DBNDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				dbn_def();
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

	public static class Pomdp_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode POMDP() { return getToken(SpuddXParser.POMDP, 0); }
		public Agent_nameContext agent_name() {
			return getRuleContext(Agent_nameContext.class,0);
		}
		public States_listContext states_list() {
			return getRuleContext(States_listContext.class,0);
		}
		public Obs_listContext obs_list() {
			return getRuleContext(Obs_listContext.class,0);
		}
		public Action_varContext action_var() {
			return getRuleContext(Action_varContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Pomdp_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pomdp_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPomdp_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPomdp_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPomdp_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pomdp_defContext pomdp_def() throws RecognitionException {
		Pomdp_defContext _localctx = new Pomdp_defContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_pomdp_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(LP);
			setState(75);
			match(POMDP);
			setState(76);
			agent_name();
			setState(77);
			states_list();
			setState(78);
			obs_list();
			setState(79);
			action_var();
			setState(80);
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

	public static class States_listContext extends ParserRuleContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
		public List<Variable_nameContext> variable_name() {
			return getRuleContexts(Variable_nameContext.class);
		}
		public Variable_nameContext variable_name(int i) {
			return getRuleContext(Variable_nameContext.class,i);
		}
		public States_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_states_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterStates_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitStates_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitStates_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final States_listContext states_list() throws RecognitionException {
		States_listContext _localctx = new States_listContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_states_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(LP);
			setState(83);
			match(T__1);
			setState(84);
			match(LP);
			setState(86); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(85);
				variable_name();
				}
				}
				setState(88); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(90);
			match(RP);
			setState(91);
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

	public static class Obs_listContext extends ParserRuleContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
		public List<Variable_nameContext> variable_name() {
			return getRuleContexts(Variable_nameContext.class);
		}
		public Variable_nameContext variable_name(int i) {
			return getRuleContext(Variable_nameContext.class,i);
		}
		public Obs_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obs_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterObs_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitObs_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitObs_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Obs_listContext obs_list() throws RecognitionException {
		Obs_listContext _localctx = new Obs_listContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_obs_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(LP);
			setState(94);
			match(T__2);
			setState(95);
			match(LP);
			setState(97); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(96);
				variable_name();
				}
				}
				setState(99); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(101);
			match(RP);
			setState(102);
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

	public static class Action_varContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public Action_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAction_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAction_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAction_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_varContext action_var() throws RecognitionException {
		Action_varContext _localctx = new Action_varContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_action_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(LP);
			setState(105);
			match(T__3);
			{
			setState(106);
			variable_name();
			}
			setState(107);
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
		enterRule(_localctx, 14, RULE_var_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(LP);
			setState(110);
			match(T__0);
			setState(112); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(111);
				match(IDENTIFIER);
				}
				}
				setState(114); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(116);
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
		enterRule(_localctx, 16, RULE_actions_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(RP);
			setState(119);
			match(T__4);
			setState(121); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(120);
				match(IDENTIFIER);
				}
				}
				setState(123); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(125);
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
		enterRule(_localctx, 18, RULE_agent_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
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
		enterRule(_localctx, 20, RULE_dd_decls);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(LP);
			setState(130);
			match(T__5);
			setState(131);
			dd_name();
			setState(132);
			dd_expr(0);
			setState(133);
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
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_dd_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				_localctx = new NegExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(136);
				((NegExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==OP_ADD || _la==OP_SUB) ) {
					((NegExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(137);
				((NegExprContext)_localctx).term = dd_expr(4);
				}
				break;
			case 2:
				{
				_localctx = new AtomicExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(138);
				dd_decl();
				}
				break;
			case 3:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(139);
				match(LP);
				setState(140);
				dd_expr(0);
				setState(141);
				match(RP);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(153);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(151);
					switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
					case 1:
						{
						_localctx = new MultDivExprContext(new Dd_exprContext(_parentctx, _parentState));
						((MultDivExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(145);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(146);
						((MultDivExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_MUL || _la==OP_DIV) ) {
							((MultDivExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(147);
						((MultDivExprContext)_localctx).right = dd_expr(3);
						}
						break;
					case 2:
						{
						_localctx = new AddSubExprContext(new Dd_exprContext(_parentctx, _parentState));
						((AddSubExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(148);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(149);
						((AddSubExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_ADD || _la==OP_SUB) ) {
							((AddSubExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(150);
						((AddSubExprContext)_localctx).right = dd_expr(2);
						}
						break;
					}
					} 
				}
				setState(155);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
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
		enterRule(_localctx, 24, RULE_dd_decl);
		int _la;
		try {
			setState(178);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				_localctx = new DDDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				match(LP);
				setState(157);
				variable_name();
				setState(163); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(158);
					match(LP);
					setState(159);
					var_value();
					setState(160);
					dd_expr(0);
					setState(161);
					match(RP);
					}
					}
					setState(165); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LP );
				setState(167);
				match(RP);
				}
				break;
			case 2:
				_localctx = new DDleafContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(169);
				dd_leaf();
				}
				break;
			case 3:
				_localctx = new SameDDContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(170);
				same_dd_decl();
				}
				break;
			case 4:
				_localctx = new DDRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(171);
				dd_ref();
				}
				break;
			case 5:
				_localctx = new DDDeterministicContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(172);
				variable_name();
				setState(173);
				var_value();
				}
				break;
			case 6:
				_localctx = new DDUniformContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(175);
				variable_name();
				setState(176);
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
		enterRule(_localctx, 26, RULE_dd_ref);
		try {
			setState(185);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(180);
				dd_name();
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(181);
				match(LP);
				setState(182);
				dd_name();
				setState(183);
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
		enterRule(_localctx, 28, RULE_dd_leaf);
		try {
			setState(191);
			switch (_input.LA(1)) {
			case FLOAT_NUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(187);
				match(FLOAT_NUM);
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(188);
				match(LP);
				setState(189);
				match(FLOAT_NUM);
				setState(190);
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
		enterRule(_localctx, 30, RULE_same_dd_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(LP);
			setState(194);
			match(T__6);
			setState(195);
			variable_name();
			setState(196);
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
		enterRule(_localctx, 32, RULE_rv_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(LP);
			setState(199);
			variable_name();
			setState(201); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(200);
				var_value();
				}
				}
				setState(203); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
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

	public static class Dbn_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Dbn_nameContext dbn_name() {
			return getRuleContext(Dbn_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Cpd_defContext> cpd_def() {
			return getRuleContexts(Cpd_defContext.class);
		}
		public Cpd_defContext cpd_def(int i) {
			return getRuleContext(Cpd_defContext.class,i);
		}
		public Dbn_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dbn_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDbn_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDbn_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDbn_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dbn_defContext dbn_def() throws RecognitionException {
		Dbn_defContext _localctx = new Dbn_defContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_dbn_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(LP);
			setState(208);
			match(T__7);
			setState(209);
			dbn_name();
			setState(213);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LP) {
				{
				{
				setState(210);
				cpd_def();
				}
				}
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(216);
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

	public static class Cpd_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
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
		enterRule(_localctx, 36, RULE_cpd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			match(LP);
			setState(219);
			variable_name();
			setState(220);
			dd_expr(0);
			setState(221);
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

	public static class Dbn_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Dbn_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dbn_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDbn_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDbn_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDbn_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dbn_nameContext dbn_name() throws RecognitionException {
		Dbn_nameContext _localctx = new Dbn_nameContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_dbn_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
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
		enterRule(_localctx, 40, RULE_dd_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
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
		enterRule(_localctx, 42, RULE_variable_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
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
		enterRule(_localctx, 44, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
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
		case 11:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\25\u00ea\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\3\2\7"+
		"\2\63\n\2\f\2\16\2\66\13\2\3\2\7\29\n\2\f\2\16\2<\13\2\3\2\3\2\3\3\3\3"+
		"\3\3\6\3C\n\3\r\3\16\3D\3\3\3\3\3\4\3\4\5\4K\n\4\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\6\3\6\3\6\3\6\6\6Y\n\6\r\6\16\6Z\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\6\7d\n\7\r\7\16\7e\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\6"+
		"\ts\n\t\r\t\16\tt\3\t\3\t\3\n\3\n\3\n\6\n|\n\n\r\n\16\n}\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u0092"+
		"\n\r\3\r\3\r\3\r\3\r\3\r\3\r\7\r\u009a\n\r\f\r\16\r\u009d\13\r\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\6\16\u00a6\n\16\r\16\16\16\u00a7\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00b5\n\16\3\17\3\17"+
		"\3\17\3\17\3\17\5\17\u00bc\n\17\3\20\3\20\3\20\3\20\5\20\u00c2\n\20\3"+
		"\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\6\22\u00cc\n\22\r\22\16\22\u00cd"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\7\23\u00d6\n\23\f\23\16\23\u00d9\13\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30"+
		"\3\30\3\30\2\3\30\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2"+
		"\4\3\2\r\16\3\2\17\20\u00e8\2\60\3\2\2\2\4?\3\2\2\2\6J\3\2\2\2\bL\3\2"+
		"\2\2\nT\3\2\2\2\f_\3\2\2\2\16j\3\2\2\2\20o\3\2\2\2\22x\3\2\2\2\24\u0081"+
		"\3\2\2\2\26\u0083\3\2\2\2\30\u0091\3\2\2\2\32\u00b4\3\2\2\2\34\u00bb\3"+
		"\2\2\2\36\u00c1\3\2\2\2 \u00c3\3\2\2\2\"\u00c8\3\2\2\2$\u00d1\3\2\2\2"+
		"&\u00dc\3\2\2\2(\u00e1\3\2\2\2*\u00e3\3\2\2\2,\u00e5\3\2\2\2.\u00e7\3"+
		"\2\2\2\60\64\5\4\3\2\61\63\5\26\f\2\62\61\3\2\2\2\63\66\3\2\2\2\64\62"+
		"\3\2\2\2\64\65\3\2\2\2\65:\3\2\2\2\66\64\3\2\2\2\679\5\6\4\28\67\3\2\2"+
		"\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;=\3\2\2\2<:\3\2\2\2=>\7\2\2\3>\3\3\2"+
		"\2\2?@\7\23\2\2@B\7\3\2\2AC\5\"\22\2BA\3\2\2\2CD\3\2\2\2DB\3\2\2\2DE\3"+
		"\2\2\2EF\3\2\2\2FG\7\24\2\2G\5\3\2\2\2HK\5\b\5\2IK\5$\23\2JH\3\2\2\2J"+
		"I\3\2\2\2K\7\3\2\2\2LM\7\23\2\2MN\7\13\2\2NO\5\24\13\2OP\5\n\6\2PQ\5\f"+
		"\7\2QR\5\16\b\2RS\7\24\2\2S\t\3\2\2\2TU\7\23\2\2UV\7\4\2\2VX\7\23\2\2"+
		"WY\5,\27\2XW\3\2\2\2YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\]\7\24"+
		"\2\2]^\7\24\2\2^\13\3\2\2\2_`\7\23\2\2`a\7\5\2\2ac\7\23\2\2bd\5,\27\2"+
		"cb\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fg\3\2\2\2gh\7\24\2\2hi\7\24\2"+
		"\2i\r\3\2\2\2jk\7\23\2\2kl\7\6\2\2lm\5,\27\2mn\7\24\2\2n\17\3\2\2\2op"+
		"\7\23\2\2pr\7\3\2\2qs\7\21\2\2rq\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3\2\2\2"+
		"uv\3\2\2\2vw\7\24\2\2w\21\3\2\2\2xy\7\24\2\2y{\7\7\2\2z|\7\21\2\2{z\3"+
		"\2\2\2|}\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\7\24\2\2\u0080"+
		"\23\3\2\2\2\u0081\u0082\7\21\2\2\u0082\25\3\2\2\2\u0083\u0084\7\23\2\2"+
		"\u0084\u0085\7\b\2\2\u0085\u0086\5*\26\2\u0086\u0087\5\30\r\2\u0087\u0088"+
		"\7\24\2\2\u0088\27\3\2\2\2\u0089\u008a\b\r\1\2\u008a\u008b\t\2\2\2\u008b"+
		"\u0092\5\30\r\6\u008c\u0092\5\32\16\2\u008d\u008e\7\23\2\2\u008e\u008f"+
		"\5\30\r\2\u008f\u0090\7\24\2\2\u0090\u0092\3\2\2\2\u0091\u0089\3\2\2\2"+
		"\u0091\u008c\3\2\2\2\u0091\u008d\3\2\2\2\u0092\u009b\3\2\2\2\u0093\u0094"+
		"\f\4\2\2\u0094\u0095\t\3\2\2\u0095\u009a\5\30\r\5\u0096\u0097\f\3\2\2"+
		"\u0097\u0098\t\2\2\2\u0098\u009a\5\30\r\4\u0099\u0093\3\2\2\2\u0099\u0096"+
		"\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\31\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u009f\7\23\2\2\u009f\u00a5\5,\27"+
		"\2\u00a0\u00a1\7\23\2\2\u00a1\u00a2\5.\30\2\u00a2\u00a3\5\30\r\2\u00a3"+
		"\u00a4\7\24\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a0\3\2\2\2\u00a6\u00a7\3"+
		"\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9"+
		"\u00aa\7\24\2\2\u00aa\u00b5\3\2\2\2\u00ab\u00b5\5\36\20\2\u00ac\u00b5"+
		"\5 \21\2\u00ad\u00b5\5\34\17\2\u00ae\u00af\5,\27\2\u00af\u00b0\5.\30\2"+
		"\u00b0\u00b5\3\2\2\2\u00b1\u00b2\5,\27\2\u00b2\u00b3\7\f\2\2\u00b3\u00b5"+
		"\3\2\2\2\u00b4\u009e\3\2\2\2\u00b4\u00ab\3\2\2\2\u00b4\u00ac\3\2\2\2\u00b4"+
		"\u00ad\3\2\2\2\u00b4\u00ae\3\2\2\2\u00b4\u00b1\3\2\2\2\u00b5\33\3\2\2"+
		"\2\u00b6\u00bc\5*\26\2\u00b7\u00b8\7\23\2\2\u00b8\u00b9\5*\26\2\u00b9"+
		"\u00ba\7\24\2\2\u00ba\u00bc\3\2\2\2\u00bb\u00b6\3\2\2\2\u00bb\u00b7\3"+
		"\2\2\2\u00bc\35\3\2\2\2\u00bd\u00c2\7\22\2\2\u00be\u00bf\7\23\2\2\u00bf"+
		"\u00c0\7\22\2\2\u00c0\u00c2\7\24\2\2\u00c1\u00bd\3\2\2\2\u00c1\u00be\3"+
		"\2\2\2\u00c2\37\3\2\2\2\u00c3\u00c4\7\23\2\2\u00c4\u00c5\7\t\2\2\u00c5"+
		"\u00c6\5,\27\2\u00c6\u00c7\7\24\2\2\u00c7!\3\2\2\2\u00c8\u00c9\7\23\2"+
		"\2\u00c9\u00cb\5,\27\2\u00ca\u00cc\5.\30\2\u00cb\u00ca\3\2\2\2\u00cc\u00cd"+
		"\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf"+
		"\u00d0\7\24\2\2\u00d0#\3\2\2\2\u00d1\u00d2\7\23\2\2\u00d2\u00d3\7\n\2"+
		"\2\u00d3\u00d7\5(\25\2\u00d4\u00d6\5&\24\2\u00d5\u00d4\3\2\2\2\u00d6\u00d9"+
		"\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00da\3\2\2\2\u00d9"+
		"\u00d7\3\2\2\2\u00da\u00db\7\24\2\2\u00db%\3\2\2\2\u00dc\u00dd\7\23\2"+
		"\2\u00dd\u00de\5,\27\2\u00de\u00df\5\30\r\2\u00df\u00e0\7\24\2\2\u00e0"+
		"\'\3\2\2\2\u00e1\u00e2\7\21\2\2\u00e2)\3\2\2\2\u00e3\u00e4\7\21\2\2\u00e4"+
		"+\3\2\2\2\u00e5\u00e6\7\21\2\2\u00e6-\3\2\2\2\u00e7\u00e8\7\21\2\2\u00e8"+
		"/\3\2\2\2\23\64:DJZet}\u0091\u0099\u009b\u00a7\u00b4\u00bb\u00c1\u00cd"+
		"\u00d7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}