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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, IDENTIFIER=7, FLOAT_NUM=8, 
		LP=9, RP=10, WS=11;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "IDENTIFIER", "FLOAT_NUM", 
		"LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'observations'", "'actions'", "'dd'", "'SAME'", 
		"'BN'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "IDENTIFIER", "FLOAT_NUM", "LP", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\rd\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\5\bE\n\b\3\b\3\b\7\bI\n\b"+
		"\f\b\16\bL\13\b\3\t\7\tO\n\t\f\t\16\tR\13\t\3\t\3\t\6\tV\n\t\r\t\16\t"+
		"W\3\n\3\n\3\13\3\13\3\f\6\f_\n\f\r\f\16\f`\3\f\3\f\2\2\r\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\3\2\7\3\2aa\4\2C\\c|\7\2))\62;C"+
		"\\aac|\3\2\62;\5\2\13\f\17\17\"\"h\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\3\31\3\2\2\2\5#\3\2\2\2\7\60\3\2\2\2"+
		"\t8\3\2\2\2\13;\3\2\2\2\r@\3\2\2\2\17D\3\2\2\2\21P\3\2\2\2\23Y\3\2\2\2"+
		"\25[\3\2\2\2\27^\3\2\2\2\31\32\7x\2\2\32\33\7c\2\2\33\34\7t\2\2\34\35"+
		"\7k\2\2\35\36\7c\2\2\36\37\7d\2\2\37 \7n\2\2 !\7g\2\2!\"\7u\2\2\"\4\3"+
		"\2\2\2#$\7q\2\2$%\7d\2\2%&\7u\2\2&\'\7g\2\2\'(\7t\2\2()\7x\2\2)*\7c\2"+
		"\2*+\7v\2\2+,\7k\2\2,-\7q\2\2-.\7p\2\2./\7u\2\2/\6\3\2\2\2\60\61\7c\2"+
		"\2\61\62\7e\2\2\62\63\7v\2\2\63\64\7k\2\2\64\65\7q\2\2\65\66\7p\2\2\66"+
		"\67\7u\2\2\67\b\3\2\2\289\7f\2\29:\7f\2\2:\n\3\2\2\2;<\7U\2\2<=\7C\2\2"+
		"=>\7O\2\2>?\7G\2\2?\f\3\2\2\2@A\7D\2\2AB\7P\2\2B\16\3\2\2\2CE\t\2\2\2"+
		"DC\3\2\2\2DE\3\2\2\2EF\3\2\2\2FJ\t\3\2\2GI\t\4\2\2HG\3\2\2\2IL\3\2\2\2"+
		"JH\3\2\2\2JK\3\2\2\2K\20\3\2\2\2LJ\3\2\2\2MO\t\5\2\2NM\3\2\2\2OR\3\2\2"+
		"\2PN\3\2\2\2PQ\3\2\2\2QS\3\2\2\2RP\3\2\2\2SU\7\60\2\2TV\t\5\2\2UT\3\2"+
		"\2\2VW\3\2\2\2WU\3\2\2\2WX\3\2\2\2X\22\3\2\2\2YZ\7*\2\2Z\24\3\2\2\2[\\"+
		"\7+\2\2\\\26\3\2\2\2]_\t\6\2\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2"+
		"ab\3\2\2\2bc\b\f\2\2c\30\3\2\2\2\b\2DJPW`\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}