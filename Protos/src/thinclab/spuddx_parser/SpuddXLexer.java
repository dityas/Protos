// Generated from SpuddX.g4 by ANTLR 4.5
package thinclab.spuddx_parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SpuddXLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, VARNAME=5, VARVAL=6, FLOAT_NUM=7, LPAREN=8, 
		RPAREN=9, WS=10;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "VARNAME", "VARVAL", "FLOAT_NUM", "LPAREN", 
		"RPAREN", "WS"
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


	public SpuddXLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SpuddX.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\f]\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\6\3\6\7\6<\n\6\f\6\16\6?\13\6\3\7\3\7\6\7C\n\7\r\7\16\7D\3\b\7\bH\n"+
		"\b\f\b\16\bK\13\b\3\b\3\b\6\bO\n\b\r\b\16\bP\3\t\3\t\3\n\3\n\3\13\6\13"+
		"X\n\13\r\13\16\13Y\3\13\3\13\2\2\f\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\f\3\2\b\3\2C\\\6\2\62;C\\aac|\3\2c|\5\2\62;aac|\3\2\62;\5\2"+
		"\13\f\17\17\"\"a\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\3\27\3\2\2\2\5!\3\2\2\2\7.\3\2\2\2\t\66\3\2\2\2\139\3\2\2\2\r@\3\2"+
		"\2\2\17I\3\2\2\2\21R\3\2\2\2\23T\3\2\2\2\25W\3\2\2\2\27\30\7x\2\2\30\31"+
		"\7c\2\2\31\32\7t\2\2\32\33\7k\2\2\33\34\7c\2\2\34\35\7d\2\2\35\36\7n\2"+
		"\2\36\37\7g\2\2\37 \7u\2\2 \4\3\2\2\2!\"\7q\2\2\"#\7d\2\2#$\7u\2\2$%\7"+
		"g\2\2%&\7t\2\2&\'\7x\2\2\'(\7c\2\2()\7v\2\2)*\7k\2\2*+\7q\2\2+,\7p\2\2"+
		",-\7u\2\2-\6\3\2\2\2./\7c\2\2/\60\7e\2\2\60\61\7v\2\2\61\62\7k\2\2\62"+
		"\63\7q\2\2\63\64\7p\2\2\64\65\7u\2\2\65\b\3\2\2\2\66\67\7f\2\2\678\7f"+
		"\2\28\n\3\2\2\29=\t\2\2\2:<\t\3\2\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3"+
		"\2\2\2>\f\3\2\2\2?=\3\2\2\2@B\t\4\2\2AC\t\5\2\2BA\3\2\2\2CD\3\2\2\2DB"+
		"\3\2\2\2DE\3\2\2\2E\16\3\2\2\2FH\t\6\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2"+
		"IJ\3\2\2\2JL\3\2\2\2KI\3\2\2\2LN\7\60\2\2MO\t\6\2\2NM\3\2\2\2OP\3\2\2"+
		"\2PN\3\2\2\2PQ\3\2\2\2Q\20\3\2\2\2RS\7*\2\2S\22\3\2\2\2TU\7+\2\2U\24\3"+
		"\2\2\2VX\t\7\2\2WV\3\2\2\2XY\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z[\3\2\2\2[\\"+
		"\b\13\2\2\\\26\3\2\2\2\b\2=DIPY\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}