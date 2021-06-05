// Generated from Spudd.g4 by ANTLR 4.5
package thinclab.spuddx_parser;

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
public class SpuddParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, VARNAME=3, VARVAL=4, LPAREN=5, RPAREN=6;
	public static final int
		RULE_domain = 0, RULE_state_var_decl = 1, RULE_obs_var_decl = 2, RULE_rv_decl = 3, 
		RULE_var_values = 4, RULE_var_value = 5;
	public static final String[] ruleNames = {
		"domain", "state_var_decl", "obs_var_decl", "rv_decl", "var_values", "var_value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'observations'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "VARNAME", "VARVAL", "LPAREN", "RPAREN"
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
	public String getGrammarFileName() { return "Spudd.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SpuddParser(TokenStream input) {
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
		public TerminalNode EOF() { return getToken(SpuddParser.EOF, 0); }
		public DomainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).enterDomain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).exitDomain(this);
		}
	}

	public final DomainContext domain() throws RecognitionException {
		DomainContext _localctx = new DomainContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_domain);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			state_var_decl();
			setState(13);
			obs_var_decl();
			setState(14);
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
		public TerminalNode LPAREN() { return getToken(SpuddParser.LPAREN, 0); }
		public Rv_declContext rv_decl() {
			return getRuleContext(Rv_declContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SpuddParser.RPAREN, 0); }
		public State_var_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_state_var_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).enterState_var_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).exitState_var_decl(this);
		}
	}

	public final State_var_declContext state_var_decl() throws RecognitionException {
		State_var_declContext _localctx = new State_var_declContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_state_var_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			match(LPAREN);
			setState(17);
			match(T__0);
			setState(18);
			rv_decl();
			setState(19);
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
		public TerminalNode LPAREN() { return getToken(SpuddParser.LPAREN, 0); }
		public Rv_declContext rv_decl() {
			return getRuleContext(Rv_declContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SpuddParser.RPAREN, 0); }
		public Obs_var_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obs_var_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).enterObs_var_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).exitObs_var_decl(this);
		}
	}

	public final Obs_var_declContext obs_var_decl() throws RecognitionException {
		Obs_var_declContext _localctx = new Obs_var_declContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_obs_var_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			match(LPAREN);
			setState(22);
			match(T__1);
			setState(23);
			rv_decl();
			setState(24);
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

	public static class Rv_declContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SpuddParser.LPAREN, 0); }
		public TerminalNode VARNAME() { return getToken(SpuddParser.VARNAME, 0); }
		public Var_valuesContext var_values() {
			return getRuleContext(Var_valuesContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SpuddParser.RPAREN, 0); }
		public Rv_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rv_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).enterRv_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).exitRv_decl(this);
		}
	}

	public final Rv_declContext rv_decl() throws RecognitionException {
		Rv_declContext _localctx = new Rv_declContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rv_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			match(LPAREN);
			setState(27);
			match(VARNAME);
			setState(28);
			var_values();
			setState(29);
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

	public static class Var_valuesContext extends ParserRuleContext {
		public Var_valueContext var_value() {
			return getRuleContext(Var_valueContext.class,0);
		}
		public Var_valuesContext var_values() {
			return getRuleContext(Var_valuesContext.class,0);
		}
		public Var_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).enterVar_values(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).exitVar_values(this);
		}
	}

	public final Var_valuesContext var_values() throws RecognitionException {
		Var_valuesContext _localctx = new Var_valuesContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_var_values);
		try {
			setState(35);
			switch (_input.LA(1)) {
			case VARVAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(31);
				var_value();
				setState(32);
				var_values();
				}
				break;
			case RPAREN:
				enterOuterAlt(_localctx, 2);
				{
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

	public static class Var_valueContext extends ParserRuleContext {
		public TerminalNode VARVAL() { return getToken(SpuddParser.VARVAL, 0); }
		public Var_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).enterVar_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpuddListener ) ((SpuddListener)listener).exitVar_value(this);
		}
	}

	public final Var_valueContext var_value() throws RecognitionException {
		Var_valueContext _localctx = new Var_valueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_var_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\b*\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3"+
		"\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\5\6&\n\6\3\7"+
		"\3\7\3\7\2\2\b\2\4\6\b\n\f\2\2$\2\16\3\2\2\2\4\22\3\2\2\2\6\27\3\2\2\2"+
		"\b\34\3\2\2\2\n%\3\2\2\2\f\'\3\2\2\2\16\17\5\4\3\2\17\20\5\6\4\2\20\21"+
		"\7\2\2\3\21\3\3\2\2\2\22\23\7\7\2\2\23\24\7\3\2\2\24\25\5\b\5\2\25\26"+
		"\7\b\2\2\26\5\3\2\2\2\27\30\7\7\2\2\30\31\7\4\2\2\31\32\5\b\5\2\32\33"+
		"\7\b\2\2\33\7\3\2\2\2\34\35\7\7\2\2\35\36\7\5\2\2\36\37\5\n\6\2\37 \7"+
		"\b\2\2 \t\3\2\2\2!\"\5\f\7\2\"#\5\n\6\2#&\3\2\2\2$&\3\2\2\2%!\3\2\2\2"+
		"%$\3\2\2\2&\13\3\2\2\2\'(\7\6\2\2(\r\3\2\2\2\3%";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}