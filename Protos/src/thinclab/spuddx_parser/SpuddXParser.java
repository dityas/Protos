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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, ENV=11, DD=12, POMDP=13, DBN=14, UNIFORM=15, OP_ADD=16, OP_SUB=17, 
		OP_MUL=18, OP_DIV=19, IDENTIFIER=20, FLOAT_NUM=21, LP=22, RP=23, WS=24;
	public static final int
		RULE_domain = 0, RULE_var_decls = 1, RULE_model_decl = 2, RULE_model_def = 3, 
		RULE_pomdp_def = 4, RULE_env_decl = 5, RULE_env_def = 6, RULE_states_list = 7, 
		RULE_agents_list = 8, RULE_obs_list = 9, RULE_action_var = 10, RULE_actions_list = 11, 
		RULE_dynamics = 12, RULE_action_model = 13, RULE_initial_belief = 14, 
		RULE_reward = 15, RULE_action_reward = 16, RULE_discount = 17, RULE_dd_decls = 18, 
		RULE_dd_expr = 19, RULE_dd_decl = 20, RULE_dd_ref = 21, RULE_dd_leaf = 22, 
		RULE_same_dd_decl = 23, RULE_rv_decl = 24, RULE_dbn_def = 25, RULE_cpd_def = 26, 
		RULE_env_name = 27, RULE_action_name = 28, RULE_model_name = 29, RULE_dd_name = 30, 
		RULE_variable_name = 31, RULE_var_value = 32;
	public static final String[] ruleNames = {
		"domain", "var_decls", "model_decl", "model_def", "pomdp_def", "env_decl", 
		"env_def", "states_list", "agents_list", "obs_list", "action_var", "actions_list", 
		"dynamics", "action_model", "initial_belief", "reward", "action_reward", 
		"discount", "dd_decls", "dd_expr", "dd_decl", "dd_ref", "dd_leaf", "same_dd_decl", 
		"rv_decl", "dbn_def", "cpd_def", "env_name", "action_name", "model_name", 
		"dd_name", "variable_name", "var_value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'S'", "'Agents'", "'O'", "'A'", "'dynamics'", "'b'", 
		"'R'", "'discount'", "'SAME'", null, null, null, null, null, "'+'", "'-'", 
		"'*'", "'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, "ENV", 
		"DD", "POMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", 
		"IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
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
		public Env_declContext env_decl() {
			return getRuleContext(Env_declContext.class,0);
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
			setState(66);
			var_decls();
			setState(70);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(67);
					dd_decls();
					}
					} 
				}
				setState(72);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(76);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(73);
					model_decl();
					}
					} 
				}
				setState(78);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			setState(80);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(79);
				env_decl();
				}
			}

			setState(82);
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
			setState(84);
			match(LP);
			setState(85);
			match(T__0);
			setState(87); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(86);
				rv_decl();
				}
				}
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
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

	public static class Model_declContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
		}
		public Model_defContext model_def() {
			return getRuleContext(Model_defContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
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
			setState(93);
			match(LP);
			setState(94);
			model_name();
			setState(95);
			model_def();
			setState(96);
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

	public static class Model_defContext extends ParserRuleContext {
		public Model_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_def; }
	 
		public Model_defContext() { }
		public void copyFrom(Model_defContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DBNDefContext extends Model_defContext {
		public Dbn_defContext dbn_def() {
			return getRuleContext(Dbn_defContext.class,0);
		}
		public DBNDefContext(Model_defContext ctx) { copyFrom(ctx); }
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
	public static class ModelDefParenContext extends Model_defContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Model_defContext model_def() {
			return getRuleContext(Model_defContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public ModelDefParenContext(Model_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModelDefParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModelDefParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModelDefParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class POMDPDefContext extends Model_defContext {
		public Pomdp_defContext pomdp_def() {
			return getRuleContext(Pomdp_defContext.class,0);
		}
		public POMDPDefContext(Model_defContext ctx) { copyFrom(ctx); }
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
	public static class ModelRefContext extends Model_defContext {
		public Variable_nameContext variable_name() {
			return getRuleContext(Variable_nameContext.class,0);
		}
		public ModelRefContext(Model_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModelRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModelRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModelRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_defContext model_def() throws RecognitionException {
		Model_defContext _localctx = new Model_defContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_model_def);
		try {
			setState(105);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new ModelDefParenContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				match(LP);
				setState(99);
				model_def();
				setState(100);
				match(RP);
				}
				break;
			case 2:
				_localctx = new POMDPDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
				pomdp_def();
				}
				break;
			case 3:
				_localctx = new DBNDefContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(103);
				dbn_def();
				}
				break;
			case 4:
				_localctx = new ModelRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(104);
				variable_name();
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
		public States_listContext states_list() {
			return getRuleContext(States_listContext.class,0);
		}
		public Obs_listContext obs_list() {
			return getRuleContext(Obs_listContext.class,0);
		}
		public Action_varContext action_var() {
			return getRuleContext(Action_varContext.class,0);
		}
		public DynamicsContext dynamics() {
			return getRuleContext(DynamicsContext.class,0);
		}
		public Initial_beliefContext initial_belief() {
			return getRuleContext(Initial_beliefContext.class,0);
		}
		public RewardContext reward() {
			return getRuleContext(RewardContext.class,0);
		}
		public DiscountContext discount() {
			return getRuleContext(DiscountContext.class,0);
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
		enterRule(_localctx, 8, RULE_pomdp_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(LP);
			setState(108);
			match(POMDP);
			setState(109);
			states_list();
			setState(110);
			obs_list();
			setState(111);
			action_var();
			setState(112);
			dynamics();
			setState(113);
			initial_belief();
			setState(114);
			reward();
			setState(115);
			discount();
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

	public static class Env_declContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Env_nameContext env_name() {
			return getRuleContext(Env_nameContext.class,0);
		}
		public Env_defContext env_def() {
			return getRuleContext(Env_defContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Env_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_env_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterEnv_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitEnv_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitEnv_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Env_declContext env_decl() throws RecognitionException {
		Env_declContext _localctx = new Env_declContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_env_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(LP);
			setState(119);
			env_name();
			setState(120);
			env_def();
			setState(121);
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
		public TerminalNode ENV() { return getToken(SpuddXParser.ENV, 0); }
		public States_listContext states_list() {
			return getRuleContext(States_listContext.class,0);
		}
		public Obs_listContext obs_list() {
			return getRuleContext(Obs_listContext.class,0);
		}
		public Actions_listContext actions_list() {
			return getRuleContext(Actions_listContext.class,0);
		}
		public Agents_listContext agents_list() {
			return getRuleContext(Agents_listContext.class,0);
		}
		public DynamicsContext dynamics() {
			return getRuleContext(DynamicsContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
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
		enterRule(_localctx, 12, RULE_env_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(LP);
			setState(124);
			match(ENV);
			setState(125);
			states_list();
			setState(126);
			obs_list();
			setState(127);
			actions_list();
			setState(128);
			agents_list();
			setState(129);
			dynamics();
			setState(130);
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
		enterRule(_localctx, 14, RULE_states_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(LP);
			setState(133);
			match(T__1);
			setState(134);
			match(LP);
			setState(136); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(135);
				variable_name();
				}
				}
				setState(138); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(140);
			match(RP);
			setState(141);
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

	public static class Agents_listContext extends ParserRuleContext {
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
		public Agents_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agents_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAgents_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAgents_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAgents_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Agents_listContext agents_list() throws RecognitionException {
		Agents_listContext _localctx = new Agents_listContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_agents_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(LP);
			setState(144);
			match(T__2);
			setState(145);
			match(LP);
			setState(147); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(146);
				variable_name();
				}
				}
				setState(149); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(151);
			match(RP);
			setState(152);
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
		enterRule(_localctx, 18, RULE_obs_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(LP);
			setState(155);
			match(T__3);
			setState(156);
			match(LP);
			setState(158); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(157);
				variable_name();
				}
				}
				setState(160); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(162);
			match(RP);
			setState(163);
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
		enterRule(_localctx, 20, RULE_action_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			match(LP);
			setState(166);
			match(T__4);
			{
			setState(167);
			variable_name();
			}
			setState(168);
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
		enterRule(_localctx, 22, RULE_actions_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(LP);
			setState(171);
			match(T__4);
			setState(172);
			match(LP);
			setState(174); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(173);
				variable_name();
				}
				}
				setState(176); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(178);
			match(RP);
			setState(179);
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

	public static class DynamicsContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Action_modelContext> action_model() {
			return getRuleContexts(Action_modelContext.class);
		}
		public Action_modelContext action_model(int i) {
			return getRuleContext(Action_modelContext.class,i);
		}
		public DynamicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dynamics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDynamics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDynamics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDynamics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DynamicsContext dynamics() throws RecognitionException {
		DynamicsContext _localctx = new DynamicsContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_dynamics);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(LP);
			setState(182);
			match(T__5);
			setState(184); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(183);
				action_model();
				}
				}
				setState(186); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(188);
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

	public static class Action_modelContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Action_nameContext action_name() {
			return getRuleContext(Action_nameContext.class,0);
		}
		public Model_defContext model_def() {
			return getRuleContext(Model_defContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Action_modelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAction_model(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAction_model(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAction_model(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_modelContext action_model() throws RecognitionException {
		Action_modelContext _localctx = new Action_modelContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_action_model);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(LP);
			setState(191);
			action_name();
			setState(192);
			model_def();
			setState(193);
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

	public static class Initial_beliefContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Initial_beliefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initial_belief; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterInitial_belief(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitInitial_belief(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitInitial_belief(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Initial_beliefContext initial_belief() throws RecognitionException {
		Initial_beliefContext _localctx = new Initial_beliefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_initial_belief);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(LP);
			setState(196);
			match(T__6);
			setState(197);
			dd_expr(0);
			setState(198);
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

	public static class RewardContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Action_rewardContext> action_reward() {
			return getRuleContexts(Action_rewardContext.class);
		}
		public Action_rewardContext action_reward(int i) {
			return getRuleContext(Action_rewardContext.class,i);
		}
		public RewardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reward; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterReward(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitReward(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitReward(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RewardContext reward() throws RecognitionException {
		RewardContext _localctx = new RewardContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_reward);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(LP);
			setState(201);
			match(T__7);
			setState(203); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(202);
				action_reward();
				}
				}
				setState(205); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(207);
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

	public static class Action_rewardContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Action_nameContext action_name() {
			return getRuleContext(Action_nameContext.class,0);
		}
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Action_rewardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_reward; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAction_reward(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAction_reward(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAction_reward(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_rewardContext action_reward() throws RecognitionException {
		Action_rewardContext _localctx = new Action_rewardContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_action_reward);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(LP);
			setState(210);
			action_name();
			setState(211);
			dd_expr(0);
			setState(212);
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

	public static class DiscountContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode FLOAT_NUM() { return getToken(SpuddXParser.FLOAT_NUM, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public DiscountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_discount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDiscount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDiscount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDiscount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiscountContext discount() throws RecognitionException {
		DiscountContext _localctx = new DiscountContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_discount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(LP);
			setState(215);
			match(T__8);
			setState(216);
			match(FLOAT_NUM);
			setState(217);
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

	public static class Dd_declsContext extends ParserRuleContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public Dd_nameContext dd_name() {
			return getRuleContext(Dd_nameContext.class,0);
		}
		public TerminalNode DD() { return getToken(SpuddXParser.DD, 0); }
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
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
		enterRule(_localctx, 36, RULE_dd_decls);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(LP);
			setState(220);
			dd_name();
			setState(221);
			match(LP);
			setState(222);
			match(DD);
			setState(223);
			dd_expr(0);
			setState(224);
			match(RP);
			setState(225);
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
		int _startState = 38;
		enterRecursionRule(_localctx, 38, RULE_dd_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				_localctx = new NegExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(228);
				((NegExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==OP_ADD || _la==OP_SUB) ) {
					((NegExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(229);
				((NegExprContext)_localctx).term = dd_expr(4);
				}
				break;
			case 2:
				{
				_localctx = new AtomicExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(230);
				dd_decl();
				}
				break;
			case 3:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(231);
				match(LP);
				setState(232);
				dd_expr(0);
				setState(233);
				match(RP);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(245);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(243);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						_localctx = new MultDivExprContext(new Dd_exprContext(_parentctx, _parentState));
						((MultDivExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(237);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(238);
						((MultDivExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_MUL || _la==OP_DIV) ) {
							((MultDivExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(239);
						((MultDivExprContext)_localctx).right = dd_expr(3);
						}
						break;
					case 2:
						{
						_localctx = new AddSubExprContext(new Dd_exprContext(_parentctx, _parentState));
						((AddSubExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(240);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(241);
						((AddSubExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_ADD || _la==OP_SUB) ) {
							((AddSubExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(242);
						((AddSubExprContext)_localctx).right = dd_expr(2);
						}
						break;
					}
					} 
				}
				setState(247);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
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
		enterRule(_localctx, 40, RULE_dd_decl);
		int _la;
		try {
			setState(270);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new DDDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(248);
				match(LP);
				setState(249);
				variable_name();
				setState(255); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(250);
					match(LP);
					setState(251);
					var_value();
					setState(252);
					dd_expr(0);
					setState(253);
					match(RP);
					}
					}
					setState(257); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LP );
				setState(259);
				match(RP);
				}
				break;
			case 2:
				_localctx = new DDleafContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(261);
				dd_leaf();
				}
				break;
			case 3:
				_localctx = new SameDDContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(262);
				same_dd_decl();
				}
				break;
			case 4:
				_localctx = new DDRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(263);
				dd_ref();
				}
				break;
			case 5:
				_localctx = new DDDeterministicContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(264);
				variable_name();
				setState(265);
				var_value();
				}
				break;
			case 6:
				_localctx = new DDUniformContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(267);
				variable_name();
				setState(268);
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
		enterRule(_localctx, 42, RULE_dd_ref);
		try {
			setState(277);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(272);
				dd_name();
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(273);
				match(LP);
				setState(274);
				dd_name();
				setState(275);
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
		enterRule(_localctx, 44, RULE_dd_leaf);
		try {
			setState(283);
			switch (_input.LA(1)) {
			case FLOAT_NUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(279);
				match(FLOAT_NUM);
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(280);
				match(LP);
				setState(281);
				match(FLOAT_NUM);
				setState(282);
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
		enterRule(_localctx, 46, RULE_same_dd_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(LP);
			setState(286);
			match(T__9);
			setState(287);
			variable_name();
			setState(288);
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
		enterRule(_localctx, 48, RULE_rv_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			match(LP);
			setState(291);
			variable_name();
			setState(293); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(292);
				var_value();
				}
				}
				setState(295); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(297);
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
		public TerminalNode DBN() { return getToken(SpuddXParser.DBN, 0); }
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
		enterRule(_localctx, 50, RULE_dbn_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			match(LP);
			setState(300);
			match(DBN);
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LP) {
				{
				{
				setState(301);
				cpd_def();
				}
				}
				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(307);
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
		enterRule(_localctx, 52, RULE_cpd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
			match(LP);
			setState(310);
			variable_name();
			setState(311);
			dd_expr(0);
			setState(312);
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

	public static class Env_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Env_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_env_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterEnv_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitEnv_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitEnv_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Env_nameContext env_name() throws RecognitionException {
		Env_nameContext _localctx = new Env_nameContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_env_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
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

	public static class Action_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Action_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAction_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAction_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAction_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_nameContext action_name() throws RecognitionException {
		Action_nameContext _localctx = new Action_nameContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_action_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
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

	public static class Model_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Model_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModel_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModel_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModel_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_nameContext model_name() throws RecognitionException {
		Model_nameContext _localctx = new Model_nameContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_model_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
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
		enterRule(_localctx, 60, RULE_dd_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
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
		enterRule(_localctx, 62, RULE_variable_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
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
		enterRule(_localctx, 64, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
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
		case 19:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\32\u0149\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\3\2\3\2\7\2G\n\2\f\2\16\2J\13\2\3\2\7\2M\n\2\f\2\16\2P\13"+
		"\2\3\2\5\2S\n\2\3\2\3\2\3\3\3\3\3\3\6\3Z\n\3\r\3\16\3[\3\3\3\3\3\4\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5l\n\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\t\3\t\6\t\u008b\n\t\r\t\16\t\u008c\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\6\n\u0096\n\n\r\n\16\n\u0097\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\6\13\u00a1\n\13\r\13\16\13\u00a2\3\13\3\13\3\13\3\f\3\f\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\r\6\r\u00b1\n\r\r\r\16\r\u00b2\3\r\3\r\3\r\3\16\3\16"+
		"\3\16\6\16\u00bb\n\16\r\16\16\16\u00bc\3\16\3\16\3\17\3\17\3\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\6\21\u00ce\n\21\r\21\16\21"+
		"\u00cf\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\5\25\u00ee\n\25\3\25\3\25\3\25\3\25\3\25\3\25\7\25\u00f6\n\25\f"+
		"\25\16\25\u00f9\13\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\6\26\u0102\n"+
		"\26\r\26\16\26\u0103\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\5\26\u0111\n\26\3\27\3\27\3\27\3\27\3\27\5\27\u0118\n\27\3\30\3"+
		"\30\3\30\3\30\5\30\u011e\n\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\6\32\u0128\n\32\r\32\16\32\u0129\3\32\3\32\3\33\3\33\3\33\7\33\u0131"+
		"\n\33\f\33\16\33\u0134\13\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\35\3"+
		"\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\2\3(#\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@B\2\4\3\2\22\23\3\2\24"+
		"\25\u0142\2D\3\2\2\2\4V\3\2\2\2\6_\3\2\2\2\bk\3\2\2\2\nm\3\2\2\2\fx\3"+
		"\2\2\2\16}\3\2\2\2\20\u0086\3\2\2\2\22\u0091\3\2\2\2\24\u009c\3\2\2\2"+
		"\26\u00a7\3\2\2\2\30\u00ac\3\2\2\2\32\u00b7\3\2\2\2\34\u00c0\3\2\2\2\36"+
		"\u00c5\3\2\2\2 \u00ca\3\2\2\2\"\u00d3\3\2\2\2$\u00d8\3\2\2\2&\u00dd\3"+
		"\2\2\2(\u00ed\3\2\2\2*\u0110\3\2\2\2,\u0117\3\2\2\2.\u011d\3\2\2\2\60"+
		"\u011f\3\2\2\2\62\u0124\3\2\2\2\64\u012d\3\2\2\2\66\u0137\3\2\2\28\u013c"+
		"\3\2\2\2:\u013e\3\2\2\2<\u0140\3\2\2\2>\u0142\3\2\2\2@\u0144\3\2\2\2B"+
		"\u0146\3\2\2\2DH\5\4\3\2EG\5&\24\2FE\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2"+
		"\2\2IN\3\2\2\2JH\3\2\2\2KM\5\6\4\2LK\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2"+
		"\2\2OR\3\2\2\2PN\3\2\2\2QS\5\f\7\2RQ\3\2\2\2RS\3\2\2\2ST\3\2\2\2TU\7\2"+
		"\2\3U\3\3\2\2\2VW\7\30\2\2WY\7\3\2\2XZ\5\62\32\2YX\3\2\2\2Z[\3\2\2\2["+
		"Y\3\2\2\2[\\\3\2\2\2\\]\3\2\2\2]^\7\31\2\2^\5\3\2\2\2_`\7\30\2\2`a\5<"+
		"\37\2ab\5\b\5\2bc\7\31\2\2c\7\3\2\2\2de\7\30\2\2ef\5\b\5\2fg\7\31\2\2"+
		"gl\3\2\2\2hl\5\n\6\2il\5\64\33\2jl\5@!\2kd\3\2\2\2kh\3\2\2\2ki\3\2\2\2"+
		"kj\3\2\2\2l\t\3\2\2\2mn\7\30\2\2no\7\17\2\2op\5\20\t\2pq\5\24\13\2qr\5"+
		"\26\f\2rs\5\32\16\2st\5\36\20\2tu\5 \21\2uv\5$\23\2vw\7\31\2\2w\13\3\2"+
		"\2\2xy\7\30\2\2yz\58\35\2z{\5\16\b\2{|\7\31\2\2|\r\3\2\2\2}~\7\30\2\2"+
		"~\177\7\r\2\2\177\u0080\5\20\t\2\u0080\u0081\5\24\13\2\u0081\u0082\5\30"+
		"\r\2\u0082\u0083\5\22\n\2\u0083\u0084\5\32\16\2\u0084\u0085\7\31\2\2\u0085"+
		"\17\3\2\2\2\u0086\u0087\7\30\2\2\u0087\u0088\7\4\2\2\u0088\u008a\7\30"+
		"\2\2\u0089\u008b\5@!\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a"+
		"\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\7\31\2\2"+
		"\u008f\u0090\7\31\2\2\u0090\21\3\2\2\2\u0091\u0092\7\30\2\2\u0092\u0093"+
		"\7\5\2\2\u0093\u0095\7\30\2\2\u0094\u0096\5@!\2\u0095\u0094\3\2\2\2\u0096"+
		"\u0097\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0099\3\2"+
		"\2\2\u0099\u009a\7\31\2\2\u009a\u009b\7\31\2\2\u009b\23\3\2\2\2\u009c"+
		"\u009d\7\30\2\2\u009d\u009e\7\6\2\2\u009e\u00a0\7\30\2\2\u009f\u00a1\5"+
		"@!\2\u00a0\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2"+
		"\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a5\7\31\2\2\u00a5\u00a6\7"+
		"\31\2\2\u00a6\25\3\2\2\2\u00a7\u00a8\7\30\2\2\u00a8\u00a9\7\7\2\2\u00a9"+
		"\u00aa\5@!\2\u00aa\u00ab\7\31\2\2\u00ab\27\3\2\2\2\u00ac\u00ad\7\30\2"+
		"\2\u00ad\u00ae\7\7\2\2\u00ae\u00b0\7\30\2\2\u00af\u00b1\5@!\2\u00b0\u00af"+
		"\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3"+
		"\u00b4\3\2\2\2\u00b4\u00b5\7\31\2\2\u00b5\u00b6\7\31\2\2\u00b6\31\3\2"+
		"\2\2\u00b7\u00b8\7\30\2\2\u00b8\u00ba\7\b\2\2\u00b9\u00bb\5\34\17\2\u00ba"+
		"\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2"+
		"\2\2\u00bd\u00be\3\2\2\2\u00be\u00bf\7\31\2\2\u00bf\33\3\2\2\2\u00c0\u00c1"+
		"\7\30\2\2\u00c1\u00c2\5:\36\2\u00c2\u00c3\5\b\5\2\u00c3\u00c4\7\31\2\2"+
		"\u00c4\35\3\2\2\2\u00c5\u00c6\7\30\2\2\u00c6\u00c7\7\t\2\2\u00c7\u00c8"+
		"\5(\25\2\u00c8\u00c9\7\31\2\2\u00c9\37\3\2\2\2\u00ca\u00cb\7\30\2\2\u00cb"+
		"\u00cd\7\n\2\2\u00cc\u00ce\5\"\22\2\u00cd\u00cc\3\2\2\2\u00ce\u00cf\3"+
		"\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1"+
		"\u00d2\7\31\2\2\u00d2!\3\2\2\2\u00d3\u00d4\7\30\2\2\u00d4\u00d5\5:\36"+
		"\2\u00d5\u00d6\5(\25\2\u00d6\u00d7\7\31\2\2\u00d7#\3\2\2\2\u00d8\u00d9"+
		"\7\30\2\2\u00d9\u00da\7\13\2\2\u00da\u00db\7\27\2\2\u00db\u00dc\7\31\2"+
		"\2\u00dc%\3\2\2\2\u00dd\u00de\7\30\2\2\u00de\u00df\5> \2\u00df\u00e0\7"+
		"\30\2\2\u00e0\u00e1\7\16\2\2\u00e1\u00e2\5(\25\2\u00e2\u00e3\7\31\2\2"+
		"\u00e3\u00e4\7\31\2\2\u00e4\'\3\2\2\2\u00e5\u00e6\b\25\1\2\u00e6\u00e7"+
		"\t\2\2\2\u00e7\u00ee\5(\25\6\u00e8\u00ee\5*\26\2\u00e9\u00ea\7\30\2\2"+
		"\u00ea\u00eb\5(\25\2\u00eb\u00ec\7\31\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00e5"+
		"\3\2\2\2\u00ed\u00e8\3\2\2\2\u00ed\u00e9\3\2\2\2\u00ee\u00f7\3\2\2\2\u00ef"+
		"\u00f0\f\4\2\2\u00f0\u00f1\t\3\2\2\u00f1\u00f6\5(\25\5\u00f2\u00f3\f\3"+
		"\2\2\u00f3\u00f4\t\2\2\2\u00f4\u00f6\5(\25\4\u00f5\u00ef\3\2\2\2\u00f5"+
		"\u00f2\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2"+
		"\2\2\u00f8)\3\2\2\2\u00f9\u00f7\3\2\2\2\u00fa\u00fb\7\30\2\2\u00fb\u0101"+
		"\5@!\2\u00fc\u00fd\7\30\2\2\u00fd\u00fe\5B\"\2\u00fe\u00ff\5(\25\2\u00ff"+
		"\u0100\7\31\2\2\u0100\u0102\3\2\2\2\u0101\u00fc\3\2\2\2\u0102\u0103\3"+
		"\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\3\2\2\2\u0105"+
		"\u0106\7\31\2\2\u0106\u0111\3\2\2\2\u0107\u0111\5.\30\2\u0108\u0111\5"+
		"\60\31\2\u0109\u0111\5,\27\2\u010a\u010b\5@!\2\u010b\u010c\5B\"\2\u010c"+
		"\u0111\3\2\2\2\u010d\u010e\5@!\2\u010e\u010f\7\21\2\2\u010f\u0111\3\2"+
		"\2\2\u0110\u00fa\3\2\2\2\u0110\u0107\3\2\2\2\u0110\u0108\3\2\2\2\u0110"+
		"\u0109\3\2\2\2\u0110\u010a\3\2\2\2\u0110\u010d\3\2\2\2\u0111+\3\2\2\2"+
		"\u0112\u0118\5> \2\u0113\u0114\7\30\2\2\u0114\u0115\5> \2\u0115\u0116"+
		"\7\31\2\2\u0116\u0118\3\2\2\2\u0117\u0112\3\2\2\2\u0117\u0113\3\2\2\2"+
		"\u0118-\3\2\2\2\u0119\u011e\7\27\2\2\u011a\u011b\7\30\2\2\u011b\u011c"+
		"\7\27\2\2\u011c\u011e\7\31\2\2\u011d\u0119\3\2\2\2\u011d\u011a\3\2\2\2"+
		"\u011e/\3\2\2\2\u011f\u0120\7\30\2\2\u0120\u0121\7\f\2\2\u0121\u0122\5"+
		"@!\2\u0122\u0123\7\31\2\2\u0123\61\3\2\2\2\u0124\u0125\7\30\2\2\u0125"+
		"\u0127\5@!\2\u0126\u0128\5B\"\2\u0127\u0126\3\2\2\2\u0128\u0129\3\2\2"+
		"\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u012c"+
		"\7\31\2\2\u012c\63\3\2\2\2\u012d\u012e\7\30\2\2\u012e\u0132\7\20\2\2\u012f"+
		"\u0131\5\66\34\2\u0130\u012f\3\2\2\2\u0131\u0134\3\2\2\2\u0132\u0130\3"+
		"\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134\u0132\3\2\2\2\u0135"+
		"\u0136\7\31\2\2\u0136\65\3\2\2\2\u0137\u0138\7\30\2\2\u0138\u0139\5@!"+
		"\2\u0139\u013a\5(\25\2\u013a\u013b\7\31\2\2\u013b\67\3\2\2\2\u013c\u013d"+
		"\7\26\2\2\u013d9\3\2\2\2\u013e\u013f\7\26\2\2\u013f;\3\2\2\2\u0140\u0141"+
		"\7\26\2\2\u0141=\3\2\2\2\u0142\u0143\7\26\2\2\u0143?\3\2\2\2\u0144\u0145"+
		"\7\26\2\2\u0145A\3\2\2\2\u0146\u0147\7\26\2\2\u0147C\3\2\2\2\26HNR[k\u008c"+
		"\u0097\u00a2\u00b2\u00bc\u00cf\u00ed\u00f5\u00f7\u0103\u0110\u0117\u011d"+
		"\u0129\u0132";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}