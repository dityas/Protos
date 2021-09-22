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
		T__24=25, ENV=26, DD=27, POMDP=28, IPOMDP=29, DBN=30, UNIFORM=31, OP_ADD=32, 
		OP_SUB=33, OP_MUL=34, OP_DIV=35, IDENTIFIER=36, FLOAT_NUM=37, LP=38, RP=39, 
		WS=40, LINE_COMMENT=41;
	public static final int
		RULE_domain = 0, RULE_var_defs = 1, RULE_var_def = 2, RULE_all_def = 3, 
		RULE_pbvi_solv_def = 4, RULE_pomdp_def = 5, RULE_ipomdp_def = 6, RULE_states_list = 7, 
		RULE_obs_list = 8, RULE_action_var = 9, RULE_action_j_var = 10, RULE_model_j_var = 11, 
		RULE_actions_list = 12, RULE_frame_def = 13, RULE_frame_tuple = 14, RULE_dynamics = 15, 
		RULE_action_model = 16, RULE_initial_belief = 17, RULE_reward = 18, RULE_action_reward = 19, 
		RULE_discount = 20, RULE_reachability = 21, RULE_dd_def = 22, RULE_dd_expr = 23, 
		RULE_dd_decl = 24, RULE_dd_ref = 25, RULE_dd_leaf = 26, RULE_same_dd_decl = 27, 
		RULE_dbn_def = 28, RULE_cpd_def = 29, RULE_exec_block = 30, RULE_exec_expr = 31, 
		RULE_solv_cmd = 32, RULE_dd_list = 33, RULE_backups = 34, RULE_exp_horizon = 35, 
		RULE_env_name = 36, RULE_policy_name = 37, RULE_action_name = 38, RULE_model_name = 39, 
		RULE_dd_name = 40, RULE_var_name = 41, RULE_var_value = 42, RULE_solv_name = 43, 
		RULE_pol_name = 44;
	public static final String[] ruleNames = {
		"domain", "var_defs", "var_def", "all_def", "pbvi_solv_def", "pomdp_def", 
		"ipomdp_def", "states_list", "obs_list", "action_var", "action_j_var", 
		"model_j_var", "actions_list", "frame_def", "frame_tuple", "dynamics", 
		"action_model", "initial_belief", "reward", "action_reward", "discount", 
		"reachability", "dd_def", "dd_expr", "dd_decl", "dd_ref", "dd_leaf", "same_dd_decl", 
		"dbn_def", "cpd_def", "exec_block", "exec_expr", "solv_cmd", "dd_list", 
		"backups", "exp_horizon", "env_name", "policy_name", "action_name", "model_name", 
		"dd_name", "var_name", "var_value", "solv_name", "pol_name"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defmodelvar'", "'defpbvisolv'", "'defpomdp'", "'defipomdp'", 
		"'S'", "'O'", "'A'", "'Aj'", "'Mj'", "'Thetaj'", "'dynamics'", "'b'", 
		"'R'", "'discount'", "'H'", "'defdd'", "'#'", "'SAME'", "'defdbn'", "'run'", 
		"'defpol'", "'='", "'poltree'", "'solve'", null, null, null, null, null, 
		null, "'+'", "'-'", "'*'", "'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", "OP_ADD", 
		"OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS", 
		"LINE_COMMENT"
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
			setState(90);
			var_defs();
			setState(94);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(91);
					all_def();
					}
					} 
				}
				setState(96);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(98);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(97);
				exec_block();
				}
			}

			setState(100);
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
			setState(103); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(102);
					var_def();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(105); 
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
		public Var_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_def; }
	 
		public Var_defContext() { }
		public void copyFrom(Var_defContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ModelVarDefContext extends Var_defContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Var_nameContext var_name() {
			return getRuleContext(Var_nameContext.class,0);
		}
		public TerminalNode RP() { return getToken(SpuddXParser.RP, 0); }
		public ModelVarDefContext(Var_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterModelVarDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitModelVarDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitModelVarDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RVarDefContext extends Var_defContext {
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
		public RVarDefContext(Var_defContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterRVarDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitRVarDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitRVarDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_defContext var_def() throws RecognitionException {
		Var_defContext _localctx = new Var_defContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_var_def);
		int _la;
		try {
			setState(124);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new RVarDefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				match(LP);
				setState(108);
				match(T__0);
				setState(109);
				var_name();
				setState(110);
				match(LP);
				setState(112); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(111);
					var_value();
					}
					}
					setState(114); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				setState(116);
				match(RP);
				setState(117);
				match(RP);
				}
				break;
			case 2:
				_localctx = new ModelVarDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(119);
				match(LP);
				setState(120);
				match(T__1);
				setState(121);
				var_name();
				setState(122);
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

	public final All_defContext all_def() throws RecognitionException {
		All_defContext _localctx = new All_defContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_all_def);
		try {
			setState(136);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new PreDefModelContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(126);
				model_name();
				}
				break;
			case 2:
				_localctx = new DDDefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(127);
				dd_def();
				}
				break;
			case 3:
				_localctx = new POMDPDefContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(128);
				pomdp_def();
				}
				break;
			case 4:
				_localctx = new IPOMDPDefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(129);
				ipomdp_def();
				}
				break;
			case 5:
				_localctx = new DBNDefContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(130);
				dbn_def();
				}
				break;
			case 6:
				_localctx = new PBVISolverDefContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(131);
				pbvi_solv_def();
				}
				break;
			case 7:
				_localctx = new OtherDefParenContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(132);
				match(LP);
				setState(133);
				all_def();
				setState(134);
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
			setState(138);
			match(LP);
			setState(139);
			match(T__2);
			setState(140);
			((Pbvi_solv_defContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==POMDP || _la==IPOMDP) ) {
				((Pbvi_solv_defContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(141);
			solv_name();
			setState(142);
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
			setState(144);
			match(LP);
			setState(145);
			match(T__3);
			setState(146);
			model_name();
			setState(147);
			states_list();
			setState(148);
			obs_list();
			setState(149);
			action_var();
			setState(150);
			dynamics();
			setState(151);
			reward();
			setState(152);
			discount();
			setState(153);
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
			setState(155);
			match(LP);
			setState(156);
			match(T__4);
			setState(157);
			model_name();
			setState(158);
			states_list();
			setState(159);
			obs_list();
			setState(160);
			action_var();
			setState(161);
			action_j_var();
			setState(162);
			model_j_var();
			setState(163);
			frame_def();
			setState(164);
			dynamics();
			setState(165);
			reward();
			setState(166);
			discount();
			setState(167);
			reachability();
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
		enterRule(_localctx, 14, RULE_states_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(LP);
			setState(171);
			match(T__5);
			setState(172);
			match(LP);
			setState(174); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(173);
				var_name();
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
		enterRule(_localctx, 16, RULE_obs_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(LP);
			setState(182);
			match(T__6);
			setState(183);
			match(LP);
			setState(185); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(184);
				var_name();
				}
				}
				setState(187); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(189);
			match(RP);
			setState(190);
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
		enterRule(_localctx, 18, RULE_action_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(LP);
			setState(193);
			match(T__7);
			{
			setState(194);
			var_name();
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
		enterRule(_localctx, 20, RULE_action_j_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(LP);
			setState(198);
			match(T__8);
			{
			setState(199);
			var_name();
			}
			setState(200);
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
		enterRule(_localctx, 22, RULE_model_j_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(LP);
			setState(203);
			match(T__9);
			{
			setState(204);
			var_name();
			}
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
		enterRule(_localctx, 24, RULE_actions_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(LP);
			setState(208);
			match(T__7);
			setState(209);
			match(LP);
			setState(211); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(210);
				var_name();
				}
				}
				setState(213); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(215);
			match(RP);
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
		enterRule(_localctx, 26, RULE_frame_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			match(LP);
			setState(219);
			match(T__10);
			setState(220);
			var_name();
			setState(222); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(221);
				frame_tuple();
				}
				}
				setState(224); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
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

	public static class Frame_tupleContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(SpuddXParser.LP, 0); }
		public Var_valueContext var_value() {
			return getRuleContext(Var_valueContext.class,0);
		}
		public Model_nameContext model_name() {
			return getRuleContext(Model_nameContext.class,0);
		}
		public Dd_listContext dd_list() {
			return getRuleContext(Dd_listContext.class,0);
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
		enterRule(_localctx, 28, RULE_frame_tuple);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(LP);
			setState(229);
			var_value();
			setState(230);
			model_name();
			setState(231);
			dd_list();
			setState(232);
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
		enterRule(_localctx, 30, RULE_dynamics);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			match(LP);
			setState(235);
			match(T__11);
			setState(237); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(236);
				action_model();
				}
				}
				setState(239); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
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
		enterRule(_localctx, 32, RULE_action_model);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(LP);
			setState(244);
			action_name();
			setState(245);
			all_def();
			setState(246);
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
		enterRule(_localctx, 34, RULE_initial_belief);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			match(LP);
			setState(249);
			match(T__12);
			setState(250);
			dd_expr(0);
			setState(251);
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
		enterRule(_localctx, 36, RULE_reward);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			match(LP);
			setState(254);
			match(T__13);
			setState(256); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(255);
				action_reward();
				}
				}
				setState(258); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LP );
			setState(260);
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
		enterRule(_localctx, 38, RULE_action_reward);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			match(LP);
			setState(263);
			action_name();
			setState(264);
			dd_expr(0);
			setState(265);
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
		enterRule(_localctx, 40, RULE_discount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			match(LP);
			setState(268);
			match(T__14);
			setState(269);
			match(FLOAT_NUM);
			setState(270);
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
		enterRule(_localctx, 42, RULE_reachability);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			match(LP);
			setState(273);
			match(T__15);
			setState(274);
			match(FLOAT_NUM);
			setState(275);
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
		enterRule(_localctx, 44, RULE_dd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(LP);
			setState(278);
			match(T__16);
			setState(279);
			dd_name();
			setState(280);
			dd_expr(0);
			setState(281);
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
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_dd_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				_localctx = new NegExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(284);
				((NegExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==OP_ADD || _la==OP_SUB) ) {
					((NegExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(285);
				((NegExprContext)_localctx).term = dd_expr(5);
				}
				break;
			case 2:
				{
				_localctx = new SumoutExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(286);
				match(T__17);
				setState(287);
				match(LP);
				setState(289); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(288);
					var_name();
					}
					}
					setState(291); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				setState(293);
				match(RP);
				setState(294);
				dd_expr(4);
				}
				break;
			case 3:
				{
				_localctx = new AtomicExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(296);
				dd_decl();
				}
				break;
			case 4:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(297);
				match(LP);
				setState(298);
				dd_expr(0);
				setState(299);
				match(RP);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(311);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(309);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new MultDivExprContext(new Dd_exprContext(_parentctx, _parentState));
						((MultDivExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(303);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(304);
						((MultDivExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_MUL || _la==OP_DIV) ) {
							((MultDivExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(305);
						((MultDivExprContext)_localctx).right = dd_expr(3);
						}
						break;
					case 2:
						{
						_localctx = new AddSubExprContext(new Dd_exprContext(_parentctx, _parentState));
						((AddSubExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dd_expr);
						setState(306);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(307);
						((AddSubExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OP_ADD || _la==OP_SUB) ) {
							((AddSubExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(308);
						((AddSubExprContext)_localctx).right = dd_expr(2);
						}
						break;
					}
					} 
				}
				setState(313);
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
		enterRule(_localctx, 48, RULE_dd_decl);
		int _la;
		try {
			setState(336);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				_localctx = new DDDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(314);
				match(LP);
				setState(315);
				var_name();
				setState(321); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(316);
					match(LP);
					setState(317);
					var_value();
					setState(318);
					dd_expr(0);
					setState(319);
					match(RP);
					}
					}
					setState(323); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LP );
				setState(325);
				match(RP);
				}
				break;
			case 2:
				_localctx = new DDleafContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(327);
				dd_leaf();
				}
				break;
			case 3:
				_localctx = new SameDDContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(328);
				same_dd_decl();
				}
				break;
			case 4:
				_localctx = new DDRefContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(329);
				dd_ref();
				}
				break;
			case 5:
				_localctx = new DDDeterministicContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(330);
				var_name();
				setState(331);
				var_value();
				}
				break;
			case 6:
				_localctx = new DDUniformContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(333);
				var_name();
				setState(334);
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
		enterRule(_localctx, 50, RULE_dd_ref);
		try {
			setState(343);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(338);
				dd_name();
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(339);
				match(LP);
				setState(340);
				dd_name();
				setState(341);
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
		enterRule(_localctx, 52, RULE_dd_leaf);
		try {
			setState(349);
			switch (_input.LA(1)) {
			case FLOAT_NUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				match(FLOAT_NUM);
				}
				break;
			case LP:
				enterOuterAlt(_localctx, 2);
				{
				setState(346);
				match(LP);
				setState(347);
				match(FLOAT_NUM);
				setState(348);
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
		enterRule(_localctx, 54, RULE_same_dd_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			match(LP);
			setState(352);
			match(T__18);
			setState(353);
			var_name();
			setState(354);
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
		enterRule(_localctx, 56, RULE_dbn_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
			match(LP);
			setState(357);
			match(T__19);
			setState(358);
			model_name();
			setState(362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LP) {
				{
				{
				setState(359);
				cpd_def();
				}
				}
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(365);
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
		enterRule(_localctx, 58, RULE_cpd_def);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(367);
			match(LP);
			setState(368);
			var_name();
			setState(369);
			dd_expr(0);
			setState(370);
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
		enterRule(_localctx, 60, RULE_exec_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			match(LP);
			setState(373);
			match(T__20);
			setState(377);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__21) | (1L << T__23) | (1L << LP))) != 0)) {
				{
				{
				setState(374);
				exec_expr();
				}
				}
				setState(379);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(380);
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
		enterRule(_localctx, 62, RULE_exec_expr);
		try {
			setState(398);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new DDExecDefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(382);
				dd_def();
				}
				break;
			case 2:
				_localctx = new SolvExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(383);
				match(T__21);
				setState(384);
				policy_name();
				setState(385);
				match(T__22);
				setState(386);
				solv_cmd();
				}
				break;
			case 3:
				_localctx = new PolTreeExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(388);
				match(T__23);
				setState(389);
				dd_list();
				setState(390);
				model_name();
				setState(391);
				policy_name();
				setState(392);
				exp_horizon();
				}
				break;
			case 4:
				_localctx = new ParenExecExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(394);
				match(LP);
				setState(395);
				exec_expr();
				setState(396);
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
		enterRule(_localctx, 64, RULE_solv_cmd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
			match(T__24);
			setState(401);
			solv_name();
			setState(402);
			dd_list();
			setState(403);
			model_name();
			setState(404);
			backups();
			setState(405);
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
		enterRule(_localctx, 66, RULE_dd_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			match(LP);
			setState(409); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(408);
				dd_expr(0);
				}
				}
				setState(411); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << OP_ADD) | (1L << OP_SUB) | (1L << IDENTIFIER) | (1L << FLOAT_NUM) | (1L << LP))) != 0) );
			setState(413);
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
		enterRule(_localctx, 68, RULE_backups);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
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
		enterRule(_localctx, 70, RULE_exp_horizon);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417);
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
		enterRule(_localctx, 72, RULE_env_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(419);
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
		enterRule(_localctx, 74, RULE_policy_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
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
		enterRule(_localctx, 76, RULE_action_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
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
		enterRule(_localctx, 78, RULE_model_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(425);
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
		enterRule(_localctx, 80, RULE_dd_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
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
		enterRule(_localctx, 82, RULE_var_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(429);
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
		enterRule(_localctx, 84, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
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
		enterRule(_localctx, 86, RULE_solv_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
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
		enterRule(_localctx, 88, RULE_pol_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(435);
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
		case 23:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3+\u01b8\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\3\2\3\2\7\2_\n\2\f\2\16\2b\13\2\3\2\5\2e\n\2\3\2\3\2"+
		"\3\3\6\3j\n\3\r\3\16\3k\3\4\3\4\3\4\3\4\3\4\6\4s\n\4\r\4\16\4t\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\5\4\177\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\5\5\u008b\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\t\3\t\3\t\3\t\6\t\u00b1\n\t\r\t\16\t\u00b2\3\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\6\n\u00bc\n\n\r\n\16\n\u00bd\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\6"+
		"\16\u00d6\n\16\r\16\16\16\u00d7\3\16\3\16\3\16\3\17\3\17\3\17\3\17\6\17"+
		"\u00e1\n\17\r\17\16\17\u00e2\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\21\3\21\3\21\6\21\u00f0\n\21\r\21\16\21\u00f1\3\21\3\21\3\22\3\22\3\22"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\6\24\u0103\n\24\r\24"+
		"\16\24\u0104\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3"+
		"\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\6\31\u0124\n\31\r\31\16\31\u0125\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\5\31\u0130\n\31\3\31\3\31\3\31\3\31\3\31\3\31\7\31"+
		"\u0138\n\31\f\31\16\31\u013b\13\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\6\32\u0144\n\32\r\32\16\32\u0145\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3"+
		"\32\3\32\3\32\3\32\5\32\u0153\n\32\3\33\3\33\3\33\3\33\3\33\5\33\u015a"+
		"\n\33\3\34\3\34\3\34\3\34\5\34\u0160\n\34\3\35\3\35\3\35\3\35\3\35\3\36"+
		"\3\36\3\36\3\36\7\36\u016b\n\36\f\36\16\36\u016e\13\36\3\36\3\36\3\37"+
		"\3\37\3\37\3\37\3\37\3 \3 \3 \7 \u017a\n \f \16 \u017d\13 \3 \3 \3!\3"+
		"!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u0191\n!\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3#\3#\6#\u019c\n#\r#\16#\u019d\3#\3#\3$\3$\3%\3%\3&\3&"+
		"\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3.\2\3\60/\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVX"+
		"Z\2\5\3\2\36\37\3\2\"#\3\2$%\u01af\2\\\3\2\2\2\4i\3\2\2\2\6~\3\2\2\2\b"+
		"\u008a\3\2\2\2\n\u008c\3\2\2\2\f\u0092\3\2\2\2\16\u009d\3\2\2\2\20\u00ac"+
		"\3\2\2\2\22\u00b7\3\2\2\2\24\u00c2\3\2\2\2\26\u00c7\3\2\2\2\30\u00cc\3"+
		"\2\2\2\32\u00d1\3\2\2\2\34\u00dc\3\2\2\2\36\u00e6\3\2\2\2 \u00ec\3\2\2"+
		"\2\"\u00f5\3\2\2\2$\u00fa\3\2\2\2&\u00ff\3\2\2\2(\u0108\3\2\2\2*\u010d"+
		"\3\2\2\2,\u0112\3\2\2\2.\u0117\3\2\2\2\60\u012f\3\2\2\2\62\u0152\3\2\2"+
		"\2\64\u0159\3\2\2\2\66\u015f\3\2\2\28\u0161\3\2\2\2:\u0166\3\2\2\2<\u0171"+
		"\3\2\2\2>\u0176\3\2\2\2@\u0190\3\2\2\2B\u0192\3\2\2\2D\u0199\3\2\2\2F"+
		"\u01a1\3\2\2\2H\u01a3\3\2\2\2J\u01a5\3\2\2\2L\u01a7\3\2\2\2N\u01a9\3\2"+
		"\2\2P\u01ab\3\2\2\2R\u01ad\3\2\2\2T\u01af\3\2\2\2V\u01b1\3\2\2\2X\u01b3"+
		"\3\2\2\2Z\u01b5\3\2\2\2\\`\5\4\3\2]_\5\b\5\2^]\3\2\2\2_b\3\2\2\2`^\3\2"+
		"\2\2`a\3\2\2\2ad\3\2\2\2b`\3\2\2\2ce\5> \2dc\3\2\2\2de\3\2\2\2ef\3\2\2"+
		"\2fg\7\2\2\3g\3\3\2\2\2hj\5\6\4\2ih\3\2\2\2jk\3\2\2\2ki\3\2\2\2kl\3\2"+
		"\2\2l\5\3\2\2\2mn\7(\2\2no\7\3\2\2op\5T+\2pr\7(\2\2qs\5V,\2rq\3\2\2\2"+
		"st\3\2\2\2tr\3\2\2\2tu\3\2\2\2uv\3\2\2\2vw\7)\2\2wx\7)\2\2x\177\3\2\2"+
		"\2yz\7(\2\2z{\7\4\2\2{|\5T+\2|}\7)\2\2}\177\3\2\2\2~m\3\2\2\2~y\3\2\2"+
		"\2\177\7\3\2\2\2\u0080\u008b\5P)\2\u0081\u008b\5.\30\2\u0082\u008b\5\f"+
		"\7\2\u0083\u008b\5\16\b\2\u0084\u008b\5:\36\2\u0085\u008b\5\n\6\2\u0086"+
		"\u0087\7(\2\2\u0087\u0088\5\b\5\2\u0088\u0089\7)\2\2\u0089\u008b\3\2\2"+
		"\2\u008a\u0080\3\2\2\2\u008a\u0081\3\2\2\2\u008a\u0082\3\2\2\2\u008a\u0083"+
		"\3\2\2\2\u008a\u0084\3\2\2\2\u008a\u0085\3\2\2\2\u008a\u0086\3\2\2\2\u008b"+
		"\t\3\2\2\2\u008c\u008d\7(\2\2\u008d\u008e\7\5\2\2\u008e\u008f\t\2\2\2"+
		"\u008f\u0090\5X-\2\u0090\u0091\7)\2\2\u0091\13\3\2\2\2\u0092\u0093\7("+
		"\2\2\u0093\u0094\7\6\2\2\u0094\u0095\5P)\2\u0095\u0096\5\20\t\2\u0096"+
		"\u0097\5\22\n\2\u0097\u0098\5\24\13\2\u0098\u0099\5 \21\2\u0099\u009a"+
		"\5&\24\2\u009a\u009b\5*\26\2\u009b\u009c\7)\2\2\u009c\r\3\2\2\2\u009d"+
		"\u009e\7(\2\2\u009e\u009f\7\7\2\2\u009f\u00a0\5P)\2\u00a0\u00a1\5\20\t"+
		"\2\u00a1\u00a2\5\22\n\2\u00a2\u00a3\5\24\13\2\u00a3\u00a4\5\26\f\2\u00a4"+
		"\u00a5\5\30\r\2\u00a5\u00a6\5\34\17\2\u00a6\u00a7\5 \21\2\u00a7\u00a8"+
		"\5&\24\2\u00a8\u00a9\5*\26\2\u00a9\u00aa\5,\27\2\u00aa\u00ab\7)\2\2\u00ab"+
		"\17\3\2\2\2\u00ac\u00ad\7(\2\2\u00ad\u00ae\7\b\2\2\u00ae\u00b0\7(\2\2"+
		"\u00af\u00b1\5T+\2\u00b0\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b0"+
		"\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b5\7)\2\2\u00b5"+
		"\u00b6\7)\2\2\u00b6\21\3\2\2\2\u00b7\u00b8\7(\2\2\u00b8\u00b9\7\t\2\2"+
		"\u00b9\u00bb\7(\2\2\u00ba\u00bc\5T+\2\u00bb\u00ba\3\2\2\2\u00bc\u00bd"+
		"\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf"+
		"\u00c0\7)\2\2\u00c0\u00c1\7)\2\2\u00c1\23\3\2\2\2\u00c2\u00c3\7(\2\2\u00c3"+
		"\u00c4\7\n\2\2\u00c4\u00c5\5T+\2\u00c5\u00c6\7)\2\2\u00c6\25\3\2\2\2\u00c7"+
		"\u00c8\7(\2\2\u00c8\u00c9\7\13\2\2\u00c9\u00ca\5T+\2\u00ca\u00cb\7)\2"+
		"\2\u00cb\27\3\2\2\2\u00cc\u00cd\7(\2\2\u00cd\u00ce\7\f\2\2\u00ce\u00cf"+
		"\5T+\2\u00cf\u00d0\7)\2\2\u00d0\31\3\2\2\2\u00d1\u00d2\7(\2\2\u00d2\u00d3"+
		"\7\n\2\2\u00d3\u00d5\7(\2\2\u00d4\u00d6\5T+\2\u00d5\u00d4\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\3\2"+
		"\2\2\u00d9\u00da\7)\2\2\u00da\u00db\7)\2\2\u00db\33\3\2\2\2\u00dc\u00dd"+
		"\7(\2\2\u00dd\u00de\7\r\2\2\u00de\u00e0\5T+\2\u00df\u00e1\5\36\20\2\u00e0"+
		"\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2"+
		"\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e5\7)\2\2\u00e5\35\3\2\2\2\u00e6\u00e7"+
		"\7(\2\2\u00e7\u00e8\5V,\2\u00e8\u00e9\5P)\2\u00e9\u00ea\5D#\2\u00ea\u00eb"+
		"\7)\2\2\u00eb\37\3\2\2\2\u00ec\u00ed\7(\2\2\u00ed\u00ef\7\16\2\2\u00ee"+
		"\u00f0\5\"\22\2\u00ef\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00ef\3"+
		"\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\7)\2\2\u00f4"+
		"!\3\2\2\2\u00f5\u00f6\7(\2\2\u00f6\u00f7\5N(\2\u00f7\u00f8\5\b\5\2\u00f8"+
		"\u00f9\7)\2\2\u00f9#\3\2\2\2\u00fa\u00fb\7(\2\2\u00fb\u00fc\7\17\2\2\u00fc"+
		"\u00fd\5\60\31\2\u00fd\u00fe\7)\2\2\u00fe%\3\2\2\2\u00ff\u0100\7(\2\2"+
		"\u0100\u0102\7\20\2\2\u0101\u0103\5(\25\2\u0102\u0101\3\2\2\2\u0103\u0104"+
		"\3\2\2\2\u0104\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0106\3\2\2\2\u0106"+
		"\u0107\7)\2\2\u0107\'\3\2\2\2\u0108\u0109\7(\2\2\u0109\u010a\5N(\2\u010a"+
		"\u010b\5\60\31\2\u010b\u010c\7)\2\2\u010c)\3\2\2\2\u010d\u010e\7(\2\2"+
		"\u010e\u010f\7\21\2\2\u010f\u0110\7\'\2\2\u0110\u0111\7)\2\2\u0111+\3"+
		"\2\2\2\u0112\u0113\7(\2\2\u0113\u0114\7\22\2\2\u0114\u0115\7\'\2\2\u0115"+
		"\u0116\7)\2\2\u0116-\3\2\2\2\u0117\u0118\7(\2\2\u0118\u0119\7\23\2\2\u0119"+
		"\u011a\5R*\2\u011a\u011b\5\60\31\2\u011b\u011c\7)\2\2\u011c/\3\2\2\2\u011d"+
		"\u011e\b\31\1\2\u011e\u011f\t\3\2\2\u011f\u0130\5\60\31\7\u0120\u0121"+
		"\7\24\2\2\u0121\u0123\7(\2\2\u0122\u0124\5T+\2\u0123\u0122\3\2\2\2\u0124"+
		"\u0125\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0127\3\2"+
		"\2\2\u0127\u0128\7)\2\2\u0128\u0129\5\60\31\6\u0129\u0130\3\2\2\2\u012a"+
		"\u0130\5\62\32\2\u012b\u012c\7(\2\2\u012c\u012d\5\60\31\2\u012d\u012e"+
		"\7)\2\2\u012e\u0130\3\2\2\2\u012f\u011d\3\2\2\2\u012f\u0120\3\2\2\2\u012f"+
		"\u012a\3\2\2\2\u012f\u012b\3\2\2\2\u0130\u0139\3\2\2\2\u0131\u0132\f\4"+
		"\2\2\u0132\u0133\t\4\2\2\u0133\u0138\5\60\31\5\u0134\u0135\f\3\2\2\u0135"+
		"\u0136\t\3\2\2\u0136\u0138\5\60\31\4\u0137\u0131\3\2\2\2\u0137\u0134\3"+
		"\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\61\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u013d\7(\2\2\u013d\u0143\5T+\2\u013e"+
		"\u013f\7(\2\2\u013f\u0140\5V,\2\u0140\u0141\5\60\31\2\u0141\u0142\7)\2"+
		"\2\u0142\u0144\3\2\2\2\u0143\u013e\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0143"+
		"\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\7)\2\2\u0148"+
		"\u0153\3\2\2\2\u0149\u0153\5\66\34\2\u014a\u0153\58\35\2\u014b\u0153\5"+
		"\64\33\2\u014c\u014d\5T+\2\u014d\u014e\5V,\2\u014e\u0153\3\2\2\2\u014f"+
		"\u0150\5T+\2\u0150\u0151\7!\2\2\u0151\u0153\3\2\2\2\u0152\u013c\3\2\2"+
		"\2\u0152\u0149\3\2\2\2\u0152\u014a\3\2\2\2\u0152\u014b\3\2\2\2\u0152\u014c"+
		"\3\2\2\2\u0152\u014f\3\2\2\2\u0153\63\3\2\2\2\u0154\u015a\5R*\2\u0155"+
		"\u0156\7(\2\2\u0156\u0157\5R*\2\u0157\u0158\7)\2\2\u0158\u015a\3\2\2\2"+
		"\u0159\u0154\3\2\2\2\u0159\u0155\3\2\2\2\u015a\65\3\2\2\2\u015b\u0160"+
		"\7\'\2\2\u015c\u015d\7(\2\2\u015d\u015e\7\'\2\2\u015e\u0160\7)\2\2\u015f"+
		"\u015b\3\2\2\2\u015f\u015c\3\2\2\2\u0160\67\3\2\2\2\u0161\u0162\7(\2\2"+
		"\u0162\u0163\7\25\2\2\u0163\u0164\5T+\2\u0164\u0165\7)\2\2\u01659\3\2"+
		"\2\2\u0166\u0167\7(\2\2\u0167\u0168\7\26\2\2\u0168\u016c\5P)\2\u0169\u016b"+
		"\5<\37\2\u016a\u0169\3\2\2\2\u016b\u016e\3\2\2\2\u016c\u016a\3\2\2\2\u016c"+
		"\u016d\3\2\2\2\u016d\u016f\3\2\2\2\u016e\u016c\3\2\2\2\u016f\u0170\7)"+
		"\2\2\u0170;\3\2\2\2\u0171\u0172\7(\2\2\u0172\u0173\5T+\2\u0173\u0174\5"+
		"\60\31\2\u0174\u0175\7)\2\2\u0175=\3\2\2\2\u0176\u0177\7(\2\2\u0177\u017b"+
		"\7\27\2\2\u0178\u017a\5@!\2\u0179\u0178\3\2\2\2\u017a\u017d\3\2\2\2\u017b"+
		"\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017c\u017e\3\2\2\2\u017d\u017b\3\2"+
		"\2\2\u017e\u017f\7)\2\2\u017f?\3\2\2\2\u0180\u0191\5.\30\2\u0181\u0182"+
		"\7\30\2\2\u0182\u0183\5L\'\2\u0183\u0184\7\31\2\2\u0184\u0185\5B\"\2\u0185"+
		"\u0191\3\2\2\2\u0186\u0187\7\32\2\2\u0187\u0188\5D#\2\u0188\u0189\5P)"+
		"\2\u0189\u018a\5L\'\2\u018a\u018b\5H%\2\u018b\u0191\3\2\2\2\u018c\u018d"+
		"\7(\2\2\u018d\u018e\5@!\2\u018e\u018f\7)\2\2\u018f\u0191\3\2\2\2\u0190"+
		"\u0180\3\2\2\2\u0190\u0181\3\2\2\2\u0190\u0186\3\2\2\2\u0190\u018c\3\2"+
		"\2\2\u0191A\3\2\2\2\u0192\u0193\7\33\2\2\u0193\u0194\5X-\2\u0194\u0195"+
		"\5D#\2\u0195\u0196\5P)\2\u0196\u0197\5F$\2\u0197\u0198\5H%\2\u0198C\3"+
		"\2\2\2\u0199\u019b\7(\2\2\u019a\u019c\5\60\31\2\u019b\u019a\3\2\2\2\u019c"+
		"\u019d\3\2\2\2\u019d\u019b\3\2\2\2\u019d\u019e\3\2\2\2\u019e\u019f\3\2"+
		"\2\2\u019f\u01a0\7)\2\2\u01a0E\3\2\2\2\u01a1\u01a2\7\'\2\2\u01a2G\3\2"+
		"\2\2\u01a3\u01a4\7\'\2\2\u01a4I\3\2\2\2\u01a5\u01a6\7&\2\2\u01a6K\3\2"+
		"\2\2\u01a7\u01a8\7&\2\2\u01a8M\3\2\2\2\u01a9\u01aa\7&\2\2\u01aaO\3\2\2"+
		"\2\u01ab\u01ac\7&\2\2\u01acQ\3\2\2\2\u01ad\u01ae\7&\2\2\u01aeS\3\2\2\2"+
		"\u01af\u01b0\7&\2\2\u01b0U\3\2\2\2\u01b1\u01b2\7&\2\2\u01b2W\3\2\2\2\u01b3"+
		"\u01b4\7&\2\2\u01b4Y\3\2\2\2\u01b5\u01b6\7&\2\2\u01b6[\3\2\2\2\32`dkt"+
		"~\u008a\u00b2\u00bd\u00d7\u00e2\u00f1\u0104\u0125\u012f\u0137\u0139\u0145"+
		"\u0152\u0159\u015f\u016c\u017b\u0190\u019d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}