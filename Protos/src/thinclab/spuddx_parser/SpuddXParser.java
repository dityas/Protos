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
		T__0=1, T__1=2, T__2=3, T__3=4, VARNAME=5, VARVAL=6, FLOAT_NUM=7, LPAREN=8, 
		RPAREN=9, WS=10;
	public static final int
		RULE_domain = 0, RULE_state_var_decl = 1, RULE_obs_var_decl = 2, RULE_actions_decl = 3, 
		RULE_dd_decl_block = 4, RULE_dd_decls = 5, RULE_dd_decl = 6, RULE_dd_child = 7, 
		RULE_dd_leaf = 8, RULE_rv_decl = 9, RULE_var_value = 10;
	public static final String[] ruleNames = {
		"domain", "state_var_decl", "obs_var_decl", "actions_decl", "dd_decl_block", 
		"dd_decls", "dd_decl", "dd_child", "dd_leaf", "rv_decl", "var_value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'observations'", "'actions'", "'dd'", null, null, 
		null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "VARNAME", "VARVAL", "FLOAT_NUM", "LPAREN", 
		"RPAREN", "WS"
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
		public State_var_declContext state_var_decl() {
			return getRuleContext(State_var_declContext.class,0);
		}
		public Obs_var_declContext obs_var_decl() {
			return getRuleContext(Obs_var_declContext.class,0);
		}
		public Actions_declContext actions_decl() {
			return getRuleContext(Actions_declContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SpuddXParser.EOF, 0); }
		public Dd_decl_blockContext dd_decl_block() {
			return getRuleContext(Dd_decl_blockContext.class,0);
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
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			state_var_decl();
			setState(23);
			obs_var_decl();
			setState(24);
			actions_decl();
			setState(26);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(25);
				dd_decl_block();
				}
			}

			setState(28);
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

	public static class State_var_declContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
		public List<Rv_declContext> rv_decl() {
			return getRuleContexts(Rv_declContext.class);
		}
		public Rv_declContext rv_decl(int i) {
			return getRuleContext(Rv_declContext.class,i);
		}
		public State_var_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_state_var_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterState_var_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitState_var_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitState_var_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final State_var_declContext state_var_decl() throws RecognitionException {
		State_var_declContext _localctx = new State_var_declContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_state_var_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(LPAREN);
			setState(31);
			match(T__0);
			setState(33); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(32);
				rv_decl();
				}
				}
				setState(35); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LPAREN );
			setState(37);
			match(RPAREN);
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

	public static class Obs_var_declContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
		public List<Rv_declContext> rv_decl() {
			return getRuleContexts(Rv_declContext.class);
		}
		public Rv_declContext rv_decl(int i) {
			return getRuleContext(Rv_declContext.class,i);
		}
		public Obs_var_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obs_var_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterObs_var_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitObs_var_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitObs_var_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Obs_var_declContext obs_var_decl() throws RecognitionException {
		Obs_var_declContext _localctx = new Obs_var_declContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_obs_var_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			match(LPAREN);
			setState(40);
			match(T__1);
			setState(42); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(41);
				rv_decl();
				}
				}
				setState(44); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LPAREN );
			setState(46);
			match(RPAREN);
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

	public static class Actions_declContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
		public List<Rv_declContext> rv_decl() {
			return getRuleContexts(Rv_declContext.class);
		}
		public Rv_declContext rv_decl(int i) {
			return getRuleContext(Rv_declContext.class,i);
		}
		public Actions_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actions_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterActions_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitActions_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitActions_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Actions_declContext actions_decl() throws RecognitionException {
		Actions_declContext _localctx = new Actions_declContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_actions_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(LPAREN);
			setState(49);
			match(T__2);
			setState(51); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(50);
				rv_decl();
				}
				}
				setState(53); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LPAREN );
			setState(55);
			match(RPAREN);
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

	public static class Dd_decl_blockContext extends ParserRuleContext {
		public List<Dd_declsContext> dd_decls() {
			return getRuleContexts(Dd_declsContext.class);
		}
		public Dd_declsContext dd_decls(int i) {
			return getRuleContext(Dd_declsContext.class,i);
		}
		public Dd_decl_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_decl_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_decl_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_decl_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_decl_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_decl_blockContext dd_decl_block() throws RecognitionException {
		Dd_decl_blockContext _localctx = new Dd_decl_blockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_dd_decl_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(57);
				dd_decls();
				}
				}
				setState(60); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LPAREN );
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
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode VARNAME() { return getToken(SpuddXParser.VARNAME, 0); }
		public Dd_declContext dd_decl() {
			return getRuleContext(Dd_declContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
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
		enterRule(_localctx, 10, RULE_dd_decls);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(LPAREN);
			setState(63);
			match(T__3);
			setState(64);
			match(VARNAME);
			setState(65);
			dd_decl();
			setState(66);
			match(RPAREN);
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

	public static class Dd_declContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode VARNAME() { return getToken(SpuddXParser.VARNAME, 0); }
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
		public List<Dd_childContext> dd_child() {
			return getRuleContexts(Dd_childContext.class);
		}
		public Dd_childContext dd_child(int i) {
			return getRuleContext(Dd_childContext.class,i);
		}
		public Dd_leafContext dd_leaf() {
			return getRuleContext(Dd_leafContext.class,0);
		}
		public Dd_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_declContext dd_decl() throws RecognitionException {
		Dd_declContext _localctx = new Dd_declContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_dd_decl);
		int _la;
		try {
			setState(78);
			switch (_input.LA(1)) {
			case LPAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(68);
				match(LPAREN);
				setState(69);
				match(VARNAME);
				setState(71); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(70);
					dd_child();
					}
					}
					setState(73); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LPAREN );
				setState(75);
				match(RPAREN);
				}
				break;
			case FLOAT_NUM:
				enterOuterAlt(_localctx, 2);
				{
				setState(77);
				dd_leaf();
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

	public static class Dd_childContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode VARVAL() { return getToken(SpuddXParser.VARVAL, 0); }
		public Dd_declContext dd_decl() {
			return getRuleContext(Dd_declContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
		public Dd_childContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dd_child; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).enterDd_child(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddXListener ) ((SpuddXListener)listener).exitDd_child(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpuddXVisitor ) return ((SpuddXVisitor<? extends T>)visitor).visitDd_child(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dd_childContext dd_child() throws RecognitionException {
		Dd_childContext _localctx = new Dd_childContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_dd_child);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(LPAREN);
			setState(81);
			match(VARVAL);
			setState(82);
			dd_decl();
			setState(83);
			match(RPAREN);
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
		enterRule(_localctx, 16, RULE_dd_leaf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
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

	public static class Rv_declContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddXParser.LPAREN, 0); }
		public TerminalNode VARNAME() { return getToken(SpuddXParser.VARNAME, 0); }
		public TerminalNode RPAREN() { return getToken(SpuddXParser.RPAREN, 0); }
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
		enterRule(_localctx, 18, RULE_rv_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(LPAREN);
			setState(88);
			match(VARNAME);
			setState(90); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(89);
				var_value();
				}
				}
				setState(92); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==VARVAL );
			setState(94);
			match(RPAREN);
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
		public TerminalNode VARVAL() { return getToken(SpuddXParser.VARVAL, 0); }
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
		enterRule(_localctx, 20, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(VARVAL);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\fe\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\3\2\3\2\3\2\3\2\5\2\35\n\2\3\2\3\2\3\3\3\3\3\3\6\3$\n\3\r\3\16"+
		"\3%\3\3\3\3\3\4\3\4\3\4\6\4-\n\4\r\4\16\4.\3\4\3\4\3\5\3\5\3\5\6\5\66"+
		"\n\5\r\5\16\5\67\3\5\3\5\3\6\6\6=\n\6\r\6\16\6>\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\b\6\bJ\n\b\r\b\16\bK\3\b\3\b\3\b\5\bQ\n\b\3\t\3\t\3\t\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\13\6\13]\n\13\r\13\16\13^\3\13\3\13\3\f\3\f\3"+
		"\f\2\2\r\2\4\6\b\n\f\16\20\22\24\26\2\2a\2\30\3\2\2\2\4 \3\2\2\2\6)\3"+
		"\2\2\2\b\62\3\2\2\2\n<\3\2\2\2\f@\3\2\2\2\16P\3\2\2\2\20R\3\2\2\2\22W"+
		"\3\2\2\2\24Y\3\2\2\2\26b\3\2\2\2\30\31\5\4\3\2\31\32\5\6\4\2\32\34\5\b"+
		"\5\2\33\35\5\n\6\2\34\33\3\2\2\2\34\35\3\2\2\2\35\36\3\2\2\2\36\37\7\2"+
		"\2\3\37\3\3\2\2\2 !\7\n\2\2!#\7\3\2\2\"$\5\24\13\2#\"\3\2\2\2$%\3\2\2"+
		"\2%#\3\2\2\2%&\3\2\2\2&\'\3\2\2\2\'(\7\13\2\2(\5\3\2\2\2)*\7\n\2\2*,\7"+
		"\4\2\2+-\5\24\13\2,+\3\2\2\2-.\3\2\2\2.,\3\2\2\2./\3\2\2\2/\60\3\2\2\2"+
		"\60\61\7\13\2\2\61\7\3\2\2\2\62\63\7\n\2\2\63\65\7\5\2\2\64\66\5\24\13"+
		"\2\65\64\3\2\2\2\66\67\3\2\2\2\67\65\3\2\2\2\678\3\2\2\289\3\2\2\29:\7"+
		"\13\2\2:\t\3\2\2\2;=\5\f\7\2<;\3\2\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?"+
		"\13\3\2\2\2@A\7\n\2\2AB\7\6\2\2BC\7\7\2\2CD\5\16\b\2DE\7\13\2\2E\r\3\2"+
		"\2\2FG\7\n\2\2GI\7\7\2\2HJ\5\20\t\2IH\3\2\2\2JK\3\2\2\2KI\3\2\2\2KL\3"+
		"\2\2\2LM\3\2\2\2MN\7\13\2\2NQ\3\2\2\2OQ\5\22\n\2PF\3\2\2\2PO\3\2\2\2Q"+
		"\17\3\2\2\2RS\7\n\2\2ST\7\b\2\2TU\5\16\b\2UV\7\13\2\2V\21\3\2\2\2WX\7"+
		"\t\2\2X\23\3\2\2\2YZ\7\n\2\2Z\\\7\7\2\2[]\5\26\f\2\\[\3\2\2\2]^\3\2\2"+
		"\2^\\\3\2\2\2^_\3\2\2\2_`\3\2\2\2`a\7\13\2\2a\25\3\2\2\2bc\7\b\2\2c\27"+
		"\3\2\2\2\n\34%.\67>KP^";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}