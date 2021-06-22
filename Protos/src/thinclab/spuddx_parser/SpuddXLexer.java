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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, POMDP=8, UNIFORM=9, 
		OP_ADD=10, OP_SUB=11, OP_MUL=12, OP_DIV=13, IDENTIFIER=14, FLOAT_NUM=15, 
		LP=16, RP=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "POMDP", "UNIFORM", 
		"OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", 
		"RP", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\24\u0094\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\5\nj\n\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\5\17u\n\17\3\17\3\17"+
		"\7\17y\n\17\f\17\16\17|\13\17\3\20\7\20\177\n\20\f\20\16\20\u0082\13\20"+
		"\3\20\3\20\6\20\u0086\n\20\r\20\16\20\u0087\3\21\3\21\3\22\3\22\3\23\6"+
		"\23\u008f\n\23\r\23\16\23\u0090\3\23\3\23\2\2\24\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\3\2"+
		"\7\3\2aa\4\2C\\c|\7\2))\62;C\\aac|\3\2\62;\5\2\13\f\17\17\"\"\u0099\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\3\'\3\2\2\2\5\61\3\2\2\2\7\67\3\2\2\2\t?\3\2\2\2\13B\3\2"+
		"\2\2\rG\3\2\2\2\17K\3\2\2\2\21U\3\2\2\2\23i\3\2\2\2\25k\3\2\2\2\27m\3"+
		"\2\2\2\31o\3\2\2\2\33q\3\2\2\2\35t\3\2\2\2\37\u0080\3\2\2\2!\u0089\3\2"+
		"\2\2#\u008b\3\2\2\2%\u008e\3\2\2\2\'(\7x\2\2()\7c\2\2)*\7t\2\2*+\7k\2"+
		"\2+,\7c\2\2,-\7d\2\2-.\7n\2\2./\7g\2\2/\60\7u\2\2\60\4\3\2\2\2\61\62\7"+
		"o\2\2\62\63\7q\2\2\63\64\7f\2\2\64\65\7g\2\2\65\66\7n\2\2\66\6\3\2\2\2"+
		"\678\7c\2\289\7e\2\29:\7v\2\2:;\7k\2\2;<\7q\2\2<=\7p\2\2=>\7u\2\2>\b\3"+
		"\2\2\2?@\7f\2\2@A\7f\2\2A\n\3\2\2\2BC\7U\2\2CD\7C\2\2DE\7O\2\2EF\7G\2"+
		"\2F\f\3\2\2\2GH\7g\2\2HI\7p\2\2IJ\7x\2\2J\16\3\2\2\2KL\7c\2\2LM\7e\2\2"+
		"MN\7v\2\2NO\7k\2\2OP\7q\2\2PQ\7p\2\2QR\7f\2\2RS\7d\2\2ST\7p\2\2T\20\3"+
		"\2\2\2UV\7R\2\2VW\7Q\2\2WX\7O\2\2XY\7F\2\2YZ\7R\2\2Z\22\3\2\2\2[\\\7w"+
		"\2\2\\]\7p\2\2]^\7k\2\2^_\7h\2\2_`\7q\2\2`a\7t\2\2aj\7o\2\2bc\7W\2\2c"+
		"d\7P\2\2de\7K\2\2ef\7H\2\2fg\7Q\2\2gh\7T\2\2hj\7O\2\2i[\3\2\2\2ib\3\2"+
		"\2\2j\24\3\2\2\2kl\7-\2\2l\26\3\2\2\2mn\7/\2\2n\30\3\2\2\2op\7,\2\2p\32"+
		"\3\2\2\2qr\7\61\2\2r\34\3\2\2\2su\t\2\2\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2"+
		"\2vz\t\3\2\2wy\t\4\2\2xw\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{\36\3\2"+
		"\2\2|z\3\2\2\2}\177\t\5\2\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2"+
		"\u0080\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0085"+
		"\7\60\2\2\u0084\u0086\t\5\2\2\u0085\u0084\3\2\2\2\u0086\u0087\3\2\2\2"+
		"\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088 \3\2\2\2\u0089\u008a\7"+
		"*\2\2\u008a\"\3\2\2\2\u008b\u008c\7+\2\2\u008c$\3\2\2\2\u008d\u008f\t"+
		"\6\2\2\u008e\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u008e\3\2\2\2\u0090"+
		"\u0091\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0093\b\23\2\2\u0093&\3\2\2\2"+
		"\t\2itz\u0080\u0087\u0090\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}