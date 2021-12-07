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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, ENV=28, DD=29, POMDP=30, IPOMDP=31, DBN=32, 
		UNIFORM=33, OP_ADD=34, OP_SUB=35, OP_MUL=36, OP_DIV=37, IDENTIFIER=38, 
		FLOAT_NUM=39, LP=40, RP=41, WS=42, LINE_COMMENT=43;
	public static final int
		RULE_domain = 0, RULE_var_defs = 1, RULE_var_def = 2, RULE_all_def = 3, 
		RULE_pbvi_solv_def = 4, RULE_pomdp_def = 5, RULE_ipomdp_def = 6, RULE_env_def = 7, 
		RULE_modelvar_init_def = 8, RULE_model_init = 9, RULE_states_list = 10, 
		RULE_obs_list = 11, RULE_action_var = 12, RULE_action_j_var = 13, RULE_model_j_var = 14, 
		RULE_actions_list = 15, RULE_frame_def = 16, RULE_frame_tuple = 17, RULE_frame_name = 18, 
		RULE_dynamics = 19, RULE_action_model = 20, RULE_initial_belief = 21, 
		RULE_reward = 22, RULE_action_reward = 23, RULE_discount = 24, RULE_reachability = 25, 
		RULE_dd_def = 26, RULE_dd_expr = 27, RULE_dd_decl = 28, RULE_dd_ref = 29, 
		RULE_dd_leaf = 30, RULE_same_dd_decl = 31, RULE_dbn_def = 32, RULE_cpd_def = 33, 
		RULE_exec_block = 34, RULE_exec_expr = 35, RULE_solv_cmd = 36, RULE_dd_list = 37, 
		RULE_backups = 38, RULE_exp_horizon = 39, RULE_env_name = 40, RULE_policy_name = 41, 
		RULE_action_name = 42, RULE_model_name = 43, RULE_dd_name = 44, RULE_var_name = 45, 
		RULE_var_value = 46, RULE_solv_name = 47, RULE_pol_name = 48;
	public static final String[] ruleNames = {
		"domain", "var_defs", "var_def", "all_def", "pbvi_solv_def", "pomdp_def", 
		"ipomdp_def", "env_def", "modelvar_init_def", "model_init", "states_list", 
		"obs_list", "action_var", "action_j_var", "model_j_var", "actions_list", 
		"frame_def", "frame_tuple", "frame_name", "dynamics", "action_model", 
		"initial_belief", "reward", "action_reward", "discount", "reachability", 
		"dd_def", "dd_expr", "dd_decl", "dd_ref", "dd_leaf", "same_dd_decl", "dbn_def", 
		"cpd_def", "exec_block", "exec_expr", "solv_cmd", "dd_list", "backups", 
		"exp_horizon", "env_name", "policy_name", "action_name", "model_name", 
		"dd_name", "var_name", "var_value", "solv_name", "pol_name"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defpbvisolv'", "'defpomdp'", "'defipomdp'", "'defenv'", 
		"'initmodelvar'", "'frames'", "'S'", "'O'", "'A'", "'Aj'", "'Mj'", "'Thetaj'", 
		"'dynamics'", "'b'", "'R'", "'discount'", "'H'", "'defdd'", "'#'", "'SAME'", 
		"'defdbn'", "'run'", "'defpol'", "'='", "'poltree'", "'solve'", null, 
		null, null, null, null, null, "'+'", "'-'", "'*'", "'/'", null, null, 
		"'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", 
		"OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", 
		"RP", "WS", "LINE_COMMENT"
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
		public Var_defsContext var_defs() {
			return getRuleContext(Var_defsContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SpuddXParser.EOF, 0); }
		public List<All_defContext> all_def() {
			return getRuleContexts(All_defContext.class);
		}
		public All_defContext all_def(int i) {
			return getRuleContext(All_defContext.class,i);
		}
		public Exec_blockContext exec_block() {
			return getRuleContext(Exec_blockContext.class,0);
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
			setState(98);
			var_defs();
			setState(102);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(99);
					all_def();
					}
					} 
				}
				setState(104);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(106);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(105);
				exec_block();
				}
			}

			setState(108);
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

	public static class Var_defsContext extends ParserRuleContext {
		public List<Var_defContext> var_def() {
			return getRuleContexts(Var_defContext.class);
		}
		public Var_defContext var_def(int i) {
			return getRuleContext(Var_defContext.class,i);
		}
		public Var_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_defs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVar_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVar_defs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVar_defs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_defsContext var_defs() throws RecognitionException {
		Var_defsContext _localctx = new Var_defsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_var_defs);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(111); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(110);
					var_def();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(113); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class Var_defContext extends ParserRuleContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		public Var_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVar_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVar_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVar_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_defContext var_def() throws RecognitionException {
		Var_defContext _localctx = new Var_defContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_var_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(LP);
			setState(116);
			match(T__0);
			setState(117);
			var_name();
			setState(118);
			match(LP);
			setState(120); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(119);
				var_value();
				}
				}
				setState(122); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(124);
			match(RP);
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

	public static class All_defContext extends ParserRuleContext {
		public All_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_all_def; }
	 
		public All_defContext() { }
		public void copyFrom(All_defContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IPOMDPDefContext extends All_defContext {
		public Ipomdp_defContext ipomdp_def() {
			return getRuleContext(Ipomdp_defContext.class,0);
		}
		public IPOMDPDefContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterIPOMDPDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitIPOMDPDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitIPOMDPDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ModelVarInitDefContext extends All_defContext {
		public Modelvar_init_defContext modelvar_init_def() {
			return getRuleContext(Modelvar_init_defContext.class,0);
		}
		public ModelVarInitDefContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModelVarInitDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModelVarInitDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModelVarInitDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DBNDefContext extends All_defContext {
		public Dbn_defContext dbn_def() {
			return getRuleContext(Dbn_defContext.class,0);
		}
		public DBNDefContext(All_defContext ctx) { copyFrom(ctx); }
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
	public static class PreDefModelContext extends All_defContext {
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
		}
		public PreDefModelContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPreDefModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPreDefModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPreDefModel(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OtherDefParenContext extends All_defContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public All_defContext all_def() {
			return getRuleContext(All_defContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public OtherDefParenContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterOtherDefParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitOtherDefParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitOtherDefParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class POMDPDefContext extends All_defContext {
		public Pomdp_defContext pomdp_def() {
			return getRuleContext(Pomdp_defContext.class,0);
		}
		public POMDPDefContext(All_defContext ctx) { copyFrom(ctx); }
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
	public static class PBVISolverDefContext extends All_defContext {
		public Pbvi_solv_defContext pbvi_solv_def() {
			return getRuleContext(Pbvi_solv_defContext.class,0);
		}
		public PBVISolverDefContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPBVISolverDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPBVISolverDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPBVISolverDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DDDefContext extends All_defContext {
		public Dd_defContext dd_def() {
			return getRuleContext(Dd_defContext.class,0);
		}
		public DDDefContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EnvDefContext extends All_defContext {
		public Env_defContext env_def() {
			return getRuleContext(Env_defContext.class,0);
		}
		public EnvDefContext(All_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterEnvDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitEnvDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitEnvDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final All_defContext all_def() throws RecognitionException {
		All_defContext _localctx = new All_defContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_all_def);
		try {
			setState(139);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new PreDefModelContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(127);
				model_name();
				}
				break;
			case 2:
				_localctx = new DDDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(128);
				dd_def();
				}
				break;
			case 3:
				_localctx = new POMDPDefContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(129);
				pomdp_def();
				}
				break;
			case 4:
				_localctx = new IPOMDPDefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(130);
				ipomdp_def();
				}
				break;
			case 5:
				_localctx = new EnvDefContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(131);
				env_def();
				}
				break;
			case 6:
				_localctx = new DBNDefContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(132);
				dbn_def();
				}
				break;
			case 7:
				_localctx = new PBVISolverDefContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(133);
				pbvi_solv_def();
				}
				break;
			case 8:
				_localctx = new ModelVarInitDefContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(134);
				modelvar_init_def();
				}
				break;
			case 9:
				_localctx = new OtherDefParenContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(135);
				match(LP);
				setState(136);
				all_def();
				setState(137);
				match(RP);
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

	public static class Pbvi_solv_defContext extends ParserRuleContext {
		public Token type;
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Solv_nameContext solv_name() {
			return getRuleContext(Solv_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public TerminalNode POMDP() { return getToken(SpuddXParser.POMDP, 0); }
		public TerminalNode IPOMDP() { return getToken(SpuddXParser.IPOMDP, 0); }
		public Pbvi_solv_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pbvi_solv_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPbvi_solv_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPbvi_solv_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPbvi_solv_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pbvi_solv_defContext pbvi_solv_def() throws RecognitionException {
		Pbvi_solv_defContext _localctx = new Pbvi_solv_defContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_pbvi_solv_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(LP);
			setState(142);
			match(T__1);
			setState(143);
			((Pbvi_solv_defContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==POMDP || _la==IPOMDP) ) {
				((Pbvi_solv_defContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(144);
			solv_name();
			setState(145);
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

	public static class Pomdp_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
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
		public DynamicsContext dynamics() {
			return getRuleContext(DynamicsContext.class,0);
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
		enterRule(_localctx, 10, RULE_pomdp_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(LP);
			setState(148);
			match(T__2);
			setState(149);
			model_name();
			setState(150);
			states_list();
			setState(151);
			obs_list();
			setState(152);
			action_var();
			setState(153);
			dynamics();
			setState(154);
			reward();
			setState(155);
			discount();
			setState(156);
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

	public static class Ipomdp_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
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
		public Action_j_varContext action_j_var() {
			return getRuleContext(Action_j_varContext.class,0);
		}
		public Model_j_varContext model_j_var() {
			return getRuleContext(Model_j_varContext.class,0);
		}
		public Frame_defContext frame_def() {
			return getRuleContext(Frame_defContext.class,0);
		}
		public DynamicsContext dynamics() {
			return getRuleContext(DynamicsContext.class,0);
		}
		public RewardContext reward() {
			return getRuleContext(RewardContext.class,0);
		}
		public DiscountContext discount() {
			return getRuleContext(DiscountContext.class,0);
		}
		public ReachabilityContext reachability() {
			return getRuleContext(ReachabilityContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Ipomdp_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ipomdp_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterIpomdp_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitIpomdp_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitIpomdp_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ipomdp_defContext ipomdp_def() throws RecognitionException {
		Ipomdp_defContext _localctx = new Ipomdp_defContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_ipomdp_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(LP);
			setState(159);
			match(T__3);
			setState(160);
			model_name();
			setState(161);
			states_list();
			setState(162);
			obs_list();
			setState(163);
			action_var();
			setState(164);
			action_j_var();
			setState(165);
			model_j_var();
			setState(166);
			frame_def();
			setState(167);
			dynamics();
			setState(168);
			reward();
			setState(169);
			discount();
			setState(170);
			reachability();
			setState(171);
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
		public Env_nameContext env_name() {
			return getRuleContext(Env_nameContext.class,0);
		}
		public States_listContext states_list() {
			return getRuleContext(States_listContext.class,0);
		}
		public Obs_listContext obs_list() {
			return getRuleContext(Obs_listContext.class,0);
		}
		public Dbn_defContext dbn_def() {
			return getRuleContext(Dbn_defContext.class,0);
		}
		public RewardContext reward() {
			return getRuleContext(RewardContext.class,0);
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
		enterRule(_localctx, 14, RULE_env_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			match(LP);
			setState(174);
			match(T__4);
			setState(175);
			env_name();
			setState(176);
			states_list();
			setState(177);
			obs_list();
			setState(178);
			dbn_def();
			setState(179);
			reward();
			setState(180);
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

	public static class Modelvar_init_defContext extends ParserRuleContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public List<Var_nameContext> var_name() {
			return getRuleContexts(Var_nameContext.class);
		}
		public Var_nameContext var_name(int i) {
			return getRuleContext(Var_nameContext.class,i);
		}
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
		public List<Model_initContext> model_init() {
			return getRuleContexts(Model_initContext.class);
		}
		public Model_initContext model_init(int i) {
			return getRuleContext(Model_initContext.class,i);
		}
		public Modelvar_init_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelvar_init_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModelvar_init_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModelvar_init_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModelvar_init_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Modelvar_init_defContext modelvar_init_def() throws RecognitionException {
		Modelvar_init_defContext _localctx = new Modelvar_init_defContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_modelvar_init_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(LP);
			setState(183);
			match(T__5);
			setState(184);
			var_name();
			setState(185);
			match(LP);
			setState(186);
			match(T__6);
			setState(187);
			var_name();
			setState(188);
			match(RP);
			setState(189);
			match(LP);
			setState(191); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(190);
				model_init();
				}
				}
				setState(193); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(195);
			match(RP);
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

	public static class Model_initContext extends ParserRuleContext {
		public List<TerminalNode> LP() { return getTokens(SpuddXParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(SpuddXParser.LP, i);
		}
		public Frame_nameContext frame_name() {
			return getRuleContext(Frame_nameContext.class,0);
		}
		public Var_valueContext var_value() {
			return getRuleContext(Var_valueContext.class,0);
		}
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public List<TerminalNode> RP() { return getTokens(SpuddXParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(SpuddXParser.RP, i);
		}
		public Model_initContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_init; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModel_init(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModel_init(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModel_init(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_initContext model_init() throws RecognitionException {
		Model_initContext _localctx = new Model_initContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_model_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(LP);
			setState(199);
			frame_name();
			setState(200);
			match(LP);
			setState(201);
			var_value();
			setState(202);
			dd_expr(0);
			setState(203);
			match(RP);
			setState(204);
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
		public List<Var_nameContext> var_name() {
			return getRuleContexts(Var_nameContext.class);
		}
		public Var_nameContext var_name(int i) {
			return getRuleContext(Var_nameContext.class,i);
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
		enterRule(_localctx, 20, RULE_states_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(LP);
			setState(207);
			match(T__7);
			setState(208);
			match(LP);
			setState(210); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(209);
				var_name();
				}
				}
				setState(212); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(214);
			match(RP);
			setState(215);
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
		public List<Var_nameContext> var_name() {
			return getRuleContexts(Var_nameContext.class);
		}
		public Var_nameContext var_name(int i) {
			return getRuleContext(Var_nameContext.class,i);
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
		enterRule(_localctx, 22, RULE_obs_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(LP);
			setState(218);
			match(T__8);
			setState(219);
			match(LP);
			setState(221); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(220);
				var_name();
				}
				}
				setState(223); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(225);
			match(RP);
			setState(226);
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
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		enterRule(_localctx, 24, RULE_action_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(LP);
			setState(229);
			match(T__9);
			{
			setState(230);
			var_name();
			}
			setState(231);
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

	public static class Action_j_varContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
		}
		public Action_j_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action_j_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterAction_j_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitAction_j_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitAction_j_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Action_j_varContext action_j_var() throws RecognitionException {
		Action_j_varContext _localctx = new Action_j_varContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_action_j_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			match(LP);
			setState(234);
			match(T__10);
			{
			setState(235);
			var_name();
			}
			setState(236);
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

	public static class Model_j_varContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
		}
		public Model_j_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_j_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModel_j_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModel_j_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModel_j_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_j_varContext model_j_var() throws RecognitionException {
		Model_j_varContext _localctx = new Model_j_varContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_model_j_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			match(LP);
			setState(239);
			match(T__11);
			{
			setState(240);
			var_name();
			}
			setState(241);
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
		public List<Var_nameContext> var_name() {
			return getRuleContexts(Var_nameContext.class);
		}
		public Var_nameContext var_name(int i) {
			return getRuleContext(Var_nameContext.class,i);
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
		enterRule(_localctx, 30, RULE_actions_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(LP);
			setState(244);
			match(T__9);
			setState(245);
			match(LP);
			setState(247); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(246);
				var_name();
				}
				}
				setState(249); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(251);
			match(RP);
			setState(252);
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

	public static class Frame_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Frame_tupleContext> frame_tuple() {
			return getRuleContexts(Frame_tupleContext.class);
		}
		public Frame_tupleContext frame_tuple(int i) {
			return getRuleContext(Frame_tupleContext.class,i);
		}
		public Frame_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_frame_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterFrame_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitFrame_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitFrame_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Frame_defContext frame_def() throws RecognitionException {
		Frame_defContext _localctx = new Frame_defContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_frame_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			match(LP);
			setState(255);
			match(T__12);
			setState(256);
			var_name();
			setState(258); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(257);
				frame_tuple();
				}
				}
				setState(260); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(262);
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

	public static class Frame_tupleContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Var_valueContext var_value() {
			return getRuleContext(Var_valueContext.class,0);
		}
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Frame_tupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_frame_tuple; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterFrame_tuple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitFrame_tuple(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitFrame_tuple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Frame_tupleContext frame_tuple() throws RecognitionException {
		Frame_tupleContext _localctx = new Frame_tupleContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_frame_tuple);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			match(LP);
			setState(265);
			var_value();
			setState(266);
			model_name();
			setState(267);
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

	public static class Frame_nameContext extends ParserRuleContext {
		public Var_valueContext var_value() {
			return getRuleContext(Var_valueContext.class,0);
		}
		public Frame_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_frame_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterFrame_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitFrame_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitFrame_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Frame_nameContext frame_name() throws RecognitionException {
		Frame_nameContext _localctx = new Frame_nameContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_frame_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(269);
			var_value();
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
		enterRule(_localctx, 38, RULE_dynamics);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			match(LP);
			setState(272);
			match(T__13);
			setState(274); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(273);
				action_model();
				}
				}
				setState(276); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(278);
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
		public All_defContext all_def() {
			return getRuleContext(All_defContext.class,0);
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
		enterRule(_localctx, 40, RULE_action_model);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(280);
			match(LP);
			setState(281);
			action_name();
			setState(282);
			all_def();
			setState(283);
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
		enterRule(_localctx, 42, RULE_initial_belief);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(LP);
			setState(286);
			match(T__14);
			setState(287);
			dd_expr(0);
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
		enterRule(_localctx, 44, RULE_reward);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			match(LP);
			setState(291);
			match(T__15);
			setState(293); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(292);
				action_reward();
				}
				}
				setState(295); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
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
		enterRule(_localctx, 46, RULE_action_reward);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			match(LP);
			setState(300);
			action_name();
			setState(301);
			dd_expr(0);
			setState(302);
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
		enterRule(_localctx, 48, RULE_discount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(LP);
			setState(305);
			match(T__16);
			setState(306);
			match(FLOAT_NUM);
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

	public static class ReachabilityContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode FLOAT_NUM() { return getToken(SpuddXParser.FLOAT_NUM, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public ReachabilityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reachability; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterReachability(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitReachability(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitReachability(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReachabilityContext reachability() throws RecognitionException {
		ReachabilityContext _localctx = new ReachabilityContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_reachability);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
			match(LP);
			setState(310);
			match(T__17);
			setState(311);
			match(FLOAT_NUM);
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

	public static class Dd_defContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Dd_nameContext dd_name() {
			return getRuleContext(Dd_nameContext.class,0);
		}
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Dd_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_def(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_defContext dd_def() throws RecognitionException {
		Dd_defContext _localctx = new Dd_defContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_dd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(LP);
			setState(315);
			match(T__18);
			setState(316);
			dd_name();
			setState(317);
			dd_expr(0);
			setState(318);
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
	public static class SumoutExprContext extends Dd_exprContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public Dd_exprContext dd_expr() {
			return getRuleContext(Dd_exprContext.class,0);
		}
		public List<Var_nameContext> var_name() {
			return getRuleContexts(Var_nameContext.class);
		}
		public Var_nameContext var_name(int i) {
			return getRuleContext(Var_nameContext.class,i);
		}
		public SumoutExprContext(Dd_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterSumoutExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitSumoutExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitSumoutExpr(this);
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
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_dd_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(338);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				_localctx = new NegExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(321);
				((NegExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==OP_ADD || _la==OP_SUB) ) {
					((NegExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(322);
				((NegExprContext)_localctx).term = dd_expr(5);
				}
				break;
			case 2:
				{
				_localctx = new SumoutExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(323);
				match(T__19);
				setState(324);
				match(LP);
				setState(326); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(325);
					var_name();
					}
					}
					setState(328); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				setState(330);
				match(RP);
				setState(331);
				dd_expr(1);
				}
				break;
			case 3:
				{
				_localctx = new AtomicExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(333);
				dd_decl();
				}
				break;
			case 4:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(334);
				match(LP);
				setState(335);
				dd_expr(0);
				setState(336);
				match(RP);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(348);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(346);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new MultDivExprContext(new Dd_exprContext(_parentctx, _parentState));
						((MultDivExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(340);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(341);
						((MultDivExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_MUL || _la==OP_DIV) ) {
							((MultDivExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(342);
						((MultDivExprContext)_localctx).right = dd_expr(4);
						}
						break;
					case 2:
						{
						_localctx = new AddSubExprContext(new Dd_exprContext(_parentctx, _parentState));
						((AddSubExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(343);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(344);
						((AddSubExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_ADD || _la==OP_SUB) ) {
							((AddSubExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(345);
						((AddSubExprContext)_localctx).right = dd_expr(3);
						}
						break;
					}
					} 
				}
				setState(350);
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
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		enterRule(_localctx, 56, RULE_dd_decl);
		int _la;
		try {
			setState(373);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				_localctx = new DDDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				match(LP);
				setState(352);
				var_name();
				setState(358); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(353);
					match(LP);
					setState(354);
					var_value();
					setState(355);
					dd_expr(0);
					setState(356);
					match(RP);
					}
					}
					setState(360); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LP );
				setState(362);
				match(RP);
				}
				break;
			case 2:
				_localctx = new DDleafContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(364);
				dd_leaf();
				}
				break;
			case 3:
				_localctx = new SameDDContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(365);
				same_dd_decl();
				}
				break;
			case 4:
				_localctx = new DDRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(366);
				dd_ref();
				}
				break;
			case 5:
				_localctx = new DDDeterministicContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(367);
				var_name();
				setState(368);
				var_value();
				}
				break;
			case 6:
				_localctx = new DDUniformContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(370);
				var_name();
				setState(371);
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
		enterRule(_localctx, 58, RULE_dd_ref);
		try {
			setState(380);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(375);
				dd_name();
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(376);
				match(LP);
				setState(377);
				dd_name();
				setState(378);
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
		enterRule(_localctx, 60, RULE_dd_leaf);
		try {
			setState(386);
			switch (_input.LA(1)) {
			case FLOAT_NUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(382);
				match(FLOAT_NUM);
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(383);
				match(LP);
				setState(384);
				match(FLOAT_NUM);
				setState(385);
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
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		enterRule(_localctx, 62, RULE_same_dd_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			match(LP);
			setState(389);
			match(T__20);
			setState(390);
			var_name();
			setState(391);
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
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
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
		enterRule(_localctx, 64, RULE_dbn_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393);
			match(LP);
			setState(394);
			match(T__21);
			setState(395);
			model_name();
			setState(399);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LP) {
				{
				{
				setState(396);
				cpd_def();
				}
				}
				setState(401);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(402);
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
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
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
		enterRule(_localctx, 66, RULE_cpd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
			match(LP);
			setState(405);
			var_name();
			setState(406);
			dd_expr(0);
			setState(407);
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

	public static class Exec_blockContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Exec_exprContext> exec_expr() {
			return getRuleContexts(Exec_exprContext.class);
		}
		public Exec_exprContext exec_expr(int i) {
			return getRuleContext(Exec_exprContext.class,i);
		}
		public Exec_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exec_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterExec_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitExec_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitExec_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Exec_blockContext exec_block() throws RecognitionException {
		Exec_blockContext _localctx = new Exec_blockContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_exec_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			match(LP);
			setState(410);
			match(T__22);
			setState(414);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__23) | (1L << T__25) | (1L << LP))) != 0)) {
				{
				{
				setState(411);
				exec_expr();
				}
				}
				setState(416);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(417);
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

	public static class Exec_exprContext extends ParserRuleContext {
		public Exec_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exec_expr; }
	 
		public Exec_exprContext() { }
		public void copyFrom(Exec_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PolTreeExprContext extends Exec_exprContext {
		public Dd_listContext dd_list() {
			return getRuleContext(Dd_listContext.class,0);
		}
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
		}
		public Policy_nameContext policy_name() {
			return getRuleContext(Policy_nameContext.class,0);
		}
		public Exp_horizonContext exp_horizon() {
			return getRuleContext(Exp_horizonContext.class,0);
		}
		public PolTreeExprContext(Exec_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPolTreeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPolTreeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPolTreeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenExecExprContext extends Exec_exprContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Exec_exprContext exec_expr() {
			return getRuleContext(Exec_exprContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public ParenExecExprContext(Exec_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterParenExecExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitParenExecExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitParenExecExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DDExecDefContext extends Exec_exprContext {
		public Dd_defContext dd_def() {
			return getRuleContext(Dd_defContext.class,0);
		}
		public DDExecDefContext(Exec_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDDExecDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDDExecDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDDExecDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SolvExprContext extends Exec_exprContext {
		public Policy_nameContext policy_name() {
			return getRuleContext(Policy_nameContext.class,0);
		}
		public Solv_cmdContext solv_cmd() {
			return getRuleContext(Solv_cmdContext.class,0);
		}
		public SolvExprContext(Exec_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterSolvExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitSolvExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitSolvExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Exec_exprContext exec_expr() throws RecognitionException {
		Exec_exprContext _localctx = new Exec_exprContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_exec_expr);
		try {
			setState(435);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new DDExecDefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(419);
				dd_def();
				}
				break;
			case 2:
				_localctx = new SolvExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(420);
				match(T__23);
				setState(421);
				policy_name();
				setState(422);
				match(T__24);
				setState(423);
				solv_cmd();
				}
				break;
			case 3:
				_localctx = new PolTreeExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(425);
				match(T__25);
				setState(426);
				dd_list();
				setState(427);
				model_name();
				setState(428);
				policy_name();
				setState(429);
				exp_horizon();
				}
				break;
			case 4:
				_localctx = new ParenExecExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(431);
				match(LP);
				setState(432);
				exec_expr();
				setState(433);
				match(RP);
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

	public static class Solv_cmdContext extends ParserRuleContext {
		public Solv_nameContext solv_name() {
			return getRuleContext(Solv_nameContext.class,0);
		}
		public Dd_listContext dd_list() {
			return getRuleContext(Dd_listContext.class,0);
		}
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
		}
		public BackupsContext backups() {
			return getRuleContext(BackupsContext.class,0);
		}
		public Exp_horizonContext exp_horizon() {
			return getRuleContext(Exp_horizonContext.class,0);
		}
		public Solv_cmdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_solv_cmd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterSolv_cmd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitSolv_cmd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitSolv_cmd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Solv_cmdContext solv_cmd() throws RecognitionException {
		Solv_cmdContext _localctx = new Solv_cmdContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_solv_cmd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			match(T__26);
			setState(438);
			solv_name();
			setState(439);
			dd_list();
			setState(440);
			model_name();
			setState(441);
			backups();
			setState(442);
			exp_horizon();
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

	public static class Dd_listContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public List<Dd_exprContext> dd_expr() {
			return getRuleContexts(Dd_exprContext.class);
		}
		public Dd_exprContext dd_expr(int i) {
			return getRuleContext(Dd_exprContext.class,i);
		}
		public Dd_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_listContext dd_list() throws RecognitionException {
		Dd_listContext _localctx = new Dd_listContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_dd_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(444);
			match(LP);
			setState(446); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(445);
				dd_expr(0);
				}
				}
				setState(448); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__19) | (1L << OP_ADD) | (1L << OP_SUB) | (1L << IDENTIFIER) | (1L << FLOAT_NUM) | (1L << LP))) != 0) );
			setState(450);
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

	public static class BackupsContext extends ParserRuleContext {
		public TerminalNode FLOAT_NUM() { return getToken(SpuddXParser.FLOAT_NUM, 0); }
		public BackupsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_backups; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterBackups(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitBackups(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitBackups(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BackupsContext backups() throws RecognitionException {
		BackupsContext _localctx = new BackupsContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_backups);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
			match(FLOAT_NUM);
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

	public static class Exp_horizonContext extends ParserRuleContext {
		public TerminalNode FLOAT_NUM() { return getToken(SpuddXParser.FLOAT_NUM, 0); }
		public Exp_horizonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp_horizon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterExp_horizon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitExp_horizon(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitExp_horizon(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Exp_horizonContext exp_horizon() throws RecognitionException {
		Exp_horizonContext _localctx = new Exp_horizonContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_exp_horizon);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			match(FLOAT_NUM);
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
		enterRule(_localctx, 80, RULE_env_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456);
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

	public static class Policy_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Policy_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_policy_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPolicy_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPolicy_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPolicy_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Policy_nameContext policy_name() throws RecognitionException {
		Policy_nameContext _localctx = new Policy_nameContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_policy_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(458);
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
		enterRule(_localctx, 84, RULE_action_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460);
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
		enterRule(_localctx, 86, RULE_model_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462);
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
		enterRule(_localctx, 88, RULE_dd_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(464);
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

	public static class Var_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Var_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterVar_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitVar_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitVar_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_nameContext var_name() throws RecognitionException {
		Var_nameContext _localctx = new Var_nameContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_var_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
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
		enterRule(_localctx, 92, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
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

	public static class Solv_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Solv_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_solv_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterSolv_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitSolv_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitSolv_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Solv_nameContext solv_name() throws RecognitionException {
		Solv_nameContext _localctx = new Solv_nameContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_solv_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
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

	public static class Pol_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpuddXParser.IDENTIFIER, 0); }
		public Pol_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pol_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterPol_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitPol_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitPol_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pol_nameContext pol_name() throws RecognitionException {
		Pol_nameContext _localctx = new Pol_nameContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_pol_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
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
		case 27:
			return dd_expr_sempred((Dd_exprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean dd_expr_sempred(Dd_exprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3-\u01dd\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\3\2\3\2\7\2g\n\2"+
		"\f\2\16\2j\13\2\3\2\5\2m\n\2\3\2\3\2\3\3\6\3r\n\3\r\3\16\3s\3\4\3\4\3"+
		"\4\3\4\3\4\6\4{\n\4\r\4\16\4|\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\5\5\u008e\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\6\n\u00c2\n\n\r\n\16\n\u00c3\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\6\f\u00d5\n\f\r\f"+
		"\16\f\u00d6\3\f\3\f\3\f\3\r\3\r\3\r\3\r\6\r\u00e0\n\r\r\r\16\r\u00e1\3"+
		"\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\6\21\u00fa\n\21\r\21\16\21\u00fb\3"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\6\22\u0105\n\22\r\22\16\22\u0106\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25\6\25\u0115\n\25"+
		"\r\25\16\25\u0116\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3"+
		"\27\3\27\3\30\3\30\3\30\6\30\u0128\n\30\r\30\16\30\u0129\3\30\3\30\3\31"+
		"\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\6\35\u0149"+
		"\n\35\r\35\16\35\u014a\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u0155"+
		"\n\35\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u015d\n\35\f\35\16\35\u0160\13"+
		"\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\6\36\u0169\n\36\r\36\16\36\u016a"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\5\36\u0178\n\36"+
		"\3\37\3\37\3\37\3\37\3\37\5\37\u017f\n\37\3 \3 \3 \3 \5 \u0185\n \3!\3"+
		"!\3!\3!\3!\3\"\3\"\3\"\3\"\7\"\u0190\n\"\f\"\16\"\u0193\13\"\3\"\3\"\3"+
		"#\3#\3#\3#\3#\3$\3$\3$\7$\u019f\n$\f$\16$\u01a2\13$\3$\3$\3%\3%\3%\3%"+
		"\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\5%\u01b6\n%\3&\3&\3&\3&\3&\3&\3&"+
		"\3\'\3\'\6\'\u01c1\n\'\r\'\16\'\u01c2\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+"+
		"\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\62\2\38\63\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJL"+
		"NPRTVXZ\\^`b\2\5\3\2 !\3\2$%\3\2&\'\u01d2\2d\3\2\2\2\4q\3\2\2\2\6u\3\2"+
		"\2\2\b\u008d\3\2\2\2\n\u008f\3\2\2\2\f\u0095\3\2\2\2\16\u00a0\3\2\2\2"+
		"\20\u00af\3\2\2\2\22\u00b8\3\2\2\2\24\u00c8\3\2\2\2\26\u00d0\3\2\2\2\30"+
		"\u00db\3\2\2\2\32\u00e6\3\2\2\2\34\u00eb\3\2\2\2\36\u00f0\3\2\2\2 \u00f5"+
		"\3\2\2\2\"\u0100\3\2\2\2$\u010a\3\2\2\2&\u010f\3\2\2\2(\u0111\3\2\2\2"+
		"*\u011a\3\2\2\2,\u011f\3\2\2\2.\u0124\3\2\2\2\60\u012d\3\2\2\2\62\u0132"+
		"\3\2\2\2\64\u0137\3\2\2\2\66\u013c\3\2\2\28\u0154\3\2\2\2:\u0177\3\2\2"+
		"\2<\u017e\3\2\2\2>\u0184\3\2\2\2@\u0186\3\2\2\2B\u018b\3\2\2\2D\u0196"+
		"\3\2\2\2F\u019b\3\2\2\2H\u01b5\3\2\2\2J\u01b7\3\2\2\2L\u01be\3\2\2\2N"+
		"\u01c6\3\2\2\2P\u01c8\3\2\2\2R\u01ca\3\2\2\2T\u01cc\3\2\2\2V\u01ce\3\2"+
		"\2\2X\u01d0\3\2\2\2Z\u01d2\3\2\2\2\\\u01d4\3\2\2\2^\u01d6\3\2\2\2`\u01d8"+
		"\3\2\2\2b\u01da\3\2\2\2dh\5\4\3\2eg\5\b\5\2fe\3\2\2\2gj\3\2\2\2hf\3\2"+
		"\2\2hi\3\2\2\2il\3\2\2\2jh\3\2\2\2km\5F$\2lk\3\2\2\2lm\3\2\2\2mn\3\2\2"+
		"\2no\7\2\2\3o\3\3\2\2\2pr\5\6\4\2qp\3\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2"+
		"\2\2t\5\3\2\2\2uv\7*\2\2vw\7\3\2\2wx\5\\/\2xz\7*\2\2y{\5^\60\2zy\3\2\2"+
		"\2{|\3\2\2\2|z\3\2\2\2|}\3\2\2\2}~\3\2\2\2~\177\7+\2\2\177\u0080\7+\2"+
		"\2\u0080\7\3\2\2\2\u0081\u008e\5X-\2\u0082\u008e\5\66\34\2\u0083\u008e"+
		"\5\f\7\2\u0084\u008e\5\16\b\2\u0085\u008e\5\20\t\2\u0086\u008e\5B\"\2"+
		"\u0087\u008e\5\n\6\2\u0088\u008e\5\22\n\2\u0089\u008a\7*\2\2\u008a\u008b"+
		"\5\b\5\2\u008b\u008c\7+\2\2\u008c\u008e\3\2\2\2\u008d\u0081\3\2\2\2\u008d"+
		"\u0082\3\2\2\2\u008d\u0083\3\2\2\2\u008d\u0084\3\2\2\2\u008d\u0085\3\2"+
		"\2\2\u008d\u0086\3\2\2\2\u008d\u0087\3\2\2\2\u008d\u0088\3\2\2\2\u008d"+
		"\u0089\3\2\2\2\u008e\t\3\2\2\2\u008f\u0090\7*\2\2\u0090\u0091\7\4\2\2"+
		"\u0091\u0092\t\2\2\2\u0092\u0093\5`\61\2\u0093\u0094\7+\2\2\u0094\13\3"+
		"\2\2\2\u0095\u0096\7*\2\2\u0096\u0097\7\5\2\2\u0097\u0098\5X-\2\u0098"+
		"\u0099\5\26\f\2\u0099\u009a\5\30\r\2\u009a\u009b\5\32\16\2\u009b\u009c"+
		"\5(\25\2\u009c\u009d\5.\30\2\u009d\u009e\5\62\32\2\u009e\u009f\7+\2\2"+
		"\u009f\r\3\2\2\2\u00a0\u00a1\7*\2\2\u00a1\u00a2\7\6\2\2\u00a2\u00a3\5"+
		"X-\2\u00a3\u00a4\5\26\f\2\u00a4\u00a5\5\30\r\2\u00a5\u00a6\5\32\16\2\u00a6"+
		"\u00a7\5\34\17\2\u00a7\u00a8\5\36\20\2\u00a8\u00a9\5\"\22\2\u00a9\u00aa"+
		"\5(\25\2\u00aa\u00ab\5.\30\2\u00ab\u00ac\5\62\32\2\u00ac\u00ad\5\64\33"+
		"\2\u00ad\u00ae\7+\2\2\u00ae\17\3\2\2\2\u00af\u00b0\7*\2\2\u00b0\u00b1"+
		"\7\7\2\2\u00b1\u00b2\5R*\2\u00b2\u00b3\5\26\f\2\u00b3\u00b4\5\30\r\2\u00b4"+
		"\u00b5\5B\"\2\u00b5\u00b6\5.\30\2\u00b6\u00b7\7+\2\2\u00b7\21\3\2\2\2"+
		"\u00b8\u00b9\7*\2\2\u00b9\u00ba\7\b\2\2\u00ba\u00bb\5\\/\2\u00bb\u00bc"+
		"\7*\2\2\u00bc\u00bd\7\t\2\2\u00bd\u00be\5\\/\2\u00be\u00bf\7+\2\2\u00bf"+
		"\u00c1\7*\2\2\u00c0\u00c2\5\24\13\2\u00c1\u00c0\3\2\2\2\u00c2\u00c3\3"+
		"\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5"+
		"\u00c6\7+\2\2\u00c6\u00c7\7+\2\2\u00c7\23\3\2\2\2\u00c8\u00c9\7*\2\2\u00c9"+
		"\u00ca\5&\24\2\u00ca\u00cb\7*\2\2\u00cb\u00cc\5^\60\2\u00cc\u00cd\58\35"+
		"\2\u00cd\u00ce\7+\2\2\u00ce\u00cf\7+\2\2\u00cf\25\3\2\2\2\u00d0\u00d1"+
		"\7*\2\2\u00d1\u00d2\7\n\2\2\u00d2\u00d4\7*\2\2\u00d3\u00d5\5\\/\2\u00d4"+
		"\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2"+
		"\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\7+\2\2\u00d9\u00da\7+\2\2\u00da\27"+
		"\3\2\2\2\u00db\u00dc\7*\2\2\u00dc\u00dd\7\13\2\2\u00dd\u00df\7*\2\2\u00de"+
		"\u00e0\5\\/\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00df\3\2"+
		"\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e4\7+\2\2\u00e4"+
		"\u00e5\7+\2\2\u00e5\31\3\2\2\2\u00e6\u00e7\7*\2\2\u00e7\u00e8\7\f\2\2"+
		"\u00e8\u00e9\5\\/\2\u00e9\u00ea\7+\2\2\u00ea\33\3\2\2\2\u00eb\u00ec\7"+
		"*\2\2\u00ec\u00ed\7\r\2\2\u00ed\u00ee\5\\/\2\u00ee\u00ef\7+\2\2\u00ef"+
		"\35\3\2\2\2\u00f0\u00f1\7*\2\2\u00f1\u00f2\7\16\2\2\u00f2\u00f3\5\\/\2"+
		"\u00f3\u00f4\7+\2\2\u00f4\37\3\2\2\2\u00f5\u00f6\7*\2\2\u00f6\u00f7\7"+
		"\f\2\2\u00f7\u00f9\7*\2\2\u00f8\u00fa\5\\/\2\u00f9\u00f8\3\2\2\2\u00fa"+
		"\u00fb\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fd\3\2"+
		"\2\2\u00fd\u00fe\7+\2\2\u00fe\u00ff\7+\2\2\u00ff!\3\2\2\2\u0100\u0101"+
		"\7*\2\2\u0101\u0102\7\17\2\2\u0102\u0104\5\\/\2\u0103\u0105\5$\23\2\u0104"+
		"\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0104\3\2\2\2\u0106\u0107\3\2"+
		"\2\2\u0107\u0108\3\2\2\2\u0108\u0109\7+\2\2\u0109#\3\2\2\2\u010a\u010b"+
		"\7*\2\2\u010b\u010c\5^\60\2\u010c\u010d\5X-\2\u010d\u010e\7+\2\2\u010e"+
		"%\3\2\2\2\u010f\u0110\5^\60\2\u0110\'\3\2\2\2\u0111\u0112\7*\2\2\u0112"+
		"\u0114\7\20\2\2\u0113\u0115\5*\26\2\u0114\u0113\3\2\2\2\u0115\u0116\3"+
		"\2\2\2\u0116\u0114\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\3\2\2\2\u0118"+
		"\u0119\7+\2\2\u0119)\3\2\2\2\u011a\u011b\7*\2\2\u011b\u011c\5V,\2\u011c"+
		"\u011d\5\b\5\2\u011d\u011e\7+\2\2\u011e+\3\2\2\2\u011f\u0120\7*\2\2\u0120"+
		"\u0121\7\21\2\2\u0121\u0122\58\35\2\u0122\u0123\7+\2\2\u0123-\3\2\2\2"+
		"\u0124\u0125\7*\2\2\u0125\u0127\7\22\2\2\u0126\u0128\5\60\31\2\u0127\u0126"+
		"\3\2\2\2\u0128\u0129\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\u012c\7+\2\2\u012c/\3\2\2\2\u012d\u012e\7*\2\2\u012e"+
		"\u012f\5V,\2\u012f\u0130\58\35\2\u0130\u0131\7+\2\2\u0131\61\3\2\2\2\u0132"+
		"\u0133\7*\2\2\u0133\u0134\7\23\2\2\u0134\u0135\7)\2\2\u0135\u0136\7+\2"+
		"\2\u0136\63\3\2\2\2\u0137\u0138\7*\2\2\u0138\u0139\7\24\2\2\u0139\u013a"+
		"\7)\2\2\u013a\u013b\7+\2\2\u013b\65\3\2\2\2\u013c\u013d\7*\2\2\u013d\u013e"+
		"\7\25\2\2\u013e\u013f\5Z.\2\u013f\u0140\58\35\2\u0140\u0141\7+\2\2\u0141"+
		"\67\3\2\2\2\u0142\u0143\b\35\1\2\u0143\u0144\t\3\2\2\u0144\u0155\58\35"+
		"\7\u0145\u0146\7\26\2\2\u0146\u0148\7*\2\2\u0147\u0149\5\\/\2\u0148\u0147"+
		"\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u014d\7+\2\2\u014d\u014e\58\35\3\u014e\u0155\3\2"+
		"\2\2\u014f\u0155\5:\36\2\u0150\u0151\7*\2\2\u0151\u0152\58\35\2\u0152"+
		"\u0153\7+\2\2\u0153\u0155\3\2\2\2\u0154\u0142\3\2\2\2\u0154\u0145\3\2"+
		"\2\2\u0154\u014f\3\2\2\2\u0154\u0150\3\2\2\2\u0155\u015e\3\2\2\2\u0156"+
		"\u0157\f\5\2\2\u0157\u0158\t\4\2\2\u0158\u015d\58\35\6\u0159\u015a\f\4"+
		"\2\2\u015a\u015b\t\3\2\2\u015b\u015d\58\35\5\u015c\u0156\3\2\2\2\u015c"+
		"\u0159\3\2\2\2\u015d\u0160\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2"+
		"\2\2\u015f9\3\2\2\2\u0160\u015e\3\2\2\2\u0161\u0162\7*\2\2\u0162\u0168"+
		"\5\\/\2\u0163\u0164\7*\2\2\u0164\u0165\5^\60\2\u0165\u0166\58\35\2\u0166"+
		"\u0167\7+\2\2\u0167\u0169\3\2\2\2\u0168\u0163\3\2\2\2\u0169\u016a\3\2"+
		"\2\2\u016a\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u016c\3\2\2\2\u016c"+
		"\u016d\7+\2\2\u016d\u0178\3\2\2\2\u016e\u0178\5> \2\u016f\u0178\5@!\2"+
		"\u0170\u0178\5<\37\2\u0171\u0172\5\\/\2\u0172\u0173\5^\60\2\u0173\u0178"+
		"\3\2\2\2\u0174\u0175\5\\/\2\u0175\u0176\7#\2\2\u0176\u0178\3\2\2\2\u0177"+
		"\u0161\3\2\2\2\u0177\u016e\3\2\2\2\u0177\u016f\3\2\2\2\u0177\u0170\3\2"+
		"\2\2\u0177\u0171\3\2\2\2\u0177\u0174\3\2\2\2\u0178;\3\2\2\2\u0179\u017f"+
		"\5Z.\2\u017a\u017b\7*\2\2\u017b\u017c\5Z.\2\u017c\u017d\7+\2\2\u017d\u017f"+
		"\3\2\2\2\u017e\u0179\3\2\2\2\u017e\u017a\3\2\2\2\u017f=\3\2\2\2\u0180"+
		"\u0185\7)\2\2\u0181\u0182\7*\2\2\u0182\u0183\7)\2\2\u0183\u0185\7+\2\2"+
		"\u0184\u0180\3\2\2\2\u0184\u0181\3\2\2\2\u0185?\3\2\2\2\u0186\u0187\7"+
		"*\2\2\u0187\u0188\7\27\2\2\u0188\u0189\5\\/\2\u0189\u018a\7+\2\2\u018a"+
		"A\3\2\2\2\u018b\u018c\7*\2\2\u018c\u018d\7\30\2\2\u018d\u0191\5X-\2\u018e"+
		"\u0190\5D#\2\u018f\u018e\3\2\2\2\u0190\u0193\3\2\2\2\u0191\u018f\3\2\2"+
		"\2\u0191\u0192\3\2\2\2\u0192\u0194\3\2\2\2\u0193\u0191\3\2\2\2\u0194\u0195"+
		"\7+\2\2\u0195C\3\2\2\2\u0196\u0197\7*\2\2\u0197\u0198\5\\/\2\u0198\u0199"+
		"\58\35\2\u0199\u019a\7+\2\2\u019aE\3\2\2\2\u019b\u019c\7*\2\2\u019c\u01a0"+
		"\7\31\2\2\u019d\u019f\5H%\2\u019e\u019d\3\2\2\2\u019f\u01a2\3\2\2\2\u01a0"+
		"\u019e\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a3\3\2\2\2\u01a2\u01a0\3\2"+
		"\2\2\u01a3\u01a4\7+\2\2\u01a4G\3\2\2\2\u01a5\u01b6\5\66\34\2\u01a6\u01a7"+
		"\7\32\2\2\u01a7\u01a8\5T+\2\u01a8\u01a9\7\33\2\2\u01a9\u01aa\5J&\2\u01aa"+
		"\u01b6\3\2\2\2\u01ab\u01ac\7\34\2\2\u01ac\u01ad\5L\'\2\u01ad\u01ae\5X"+
		"-\2\u01ae\u01af\5T+\2\u01af\u01b0\5P)\2\u01b0\u01b6\3\2\2\2\u01b1\u01b2"+
		"\7*\2\2\u01b2\u01b3\5H%\2\u01b3\u01b4\7+\2\2\u01b4\u01b6\3\2\2\2\u01b5"+
		"\u01a5\3\2\2\2\u01b5\u01a6\3\2\2\2\u01b5\u01ab\3\2\2\2\u01b5\u01b1\3\2"+
		"\2\2\u01b6I\3\2\2\2\u01b7\u01b8\7\35\2\2\u01b8\u01b9\5`\61\2\u01b9\u01ba"+
		"\5L\'\2\u01ba\u01bb\5X-\2\u01bb\u01bc\5N(\2\u01bc\u01bd\5P)\2\u01bdK\3"+
		"\2\2\2\u01be\u01c0\7*\2\2\u01bf\u01c1\58\35\2\u01c0\u01bf\3\2\2\2\u01c1"+
		"\u01c2\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3\u01c4\3\2"+
		"\2\2\u01c4\u01c5\7+\2\2\u01c5M\3\2\2\2\u01c6\u01c7\7)\2\2\u01c7O\3\2\2"+
		"\2\u01c8\u01c9\7)\2\2\u01c9Q\3\2\2\2\u01ca\u01cb\7(\2\2\u01cbS\3\2\2\2"+
		"\u01cc\u01cd\7(\2\2\u01cdU\3\2\2\2\u01ce\u01cf\7(\2\2\u01cfW\3\2\2\2\u01d0"+
		"\u01d1\7(\2\2\u01d1Y\3\2\2\2\u01d2\u01d3\7(\2\2\u01d3[\3\2\2\2\u01d4\u01d5"+
		"\7(\2\2\u01d5]\3\2\2\2\u01d6\u01d7\7(\2\2\u01d7_\3\2\2\2\u01d8\u01d9\7"+
		"(\2\2\u01d9a\3\2\2\2\u01da\u01db\7(\2\2\u01dbc\3\2\2\2\32hls|\u008d\u00c3"+
		"\u00d6\u00e1\u00fb\u0106\u0116\u0129\u014a\u0154\u015c\u015e\u016a\u0177"+
		"\u017e\u0184\u0191\u01a0\u01b5\u01c2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}