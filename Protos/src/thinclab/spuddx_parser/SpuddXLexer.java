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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, ENV=14, DD=15, POMDP=16, IPOMDP=17, 
		DBN=18, UNIFORM=19, OP_ADD=20, OP_SUB=21, OP_MUL=22, OP_DIV=23, IDENTIFIER=24, 
		FLOAT_NUM=25, LP=26, RP=27, WS=28;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "ENV", "DD", "POMDP", "IPOMDP", "DBN", 
		"UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", 
		"LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defpbvisolv'", "'defpomdp'", "'S'", "'O'", "'A'", 
		"'dynamics'", "'b'", "'R'", "'discount'", "'defdd'", "'SAME'", "'defdbn'", 
		null, null, null, null, null, null, "'+'", "'-'", "'*'", "'/'", null, 
		null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", "OP_ADD", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\36\u00f0\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u008c\n\17\3\20\3\20\3\20"+
		"\3\20\5\20\u0092\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\5\21\u009e\n\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\5\22\u00ac\n\22\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00b4\n\23\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5"+
		"\24\u00c4\n\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\5\31\u00cf"+
		"\n\31\3\31\3\31\7\31\u00d3\n\31\f\31\16\31\u00d6\13\31\3\32\7\32\u00d9"+
		"\n\32\f\32\16\32\u00dc\13\32\3\32\5\32\u00df\n\32\3\32\6\32\u00e2\n\32"+
		"\r\32\16\32\u00e3\3\33\3\33\3\34\3\34\3\35\6\35\u00eb\n\35\r\35\16\35"+
		"\u00ec\3\35\3\35\2\2\36\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63"+
		"\33\65\34\67\359\36\3\2\b\3\2aa\4\2C\\c|\7\2))\62;C\\aac|\3\2\62;\4\2"+
		"))\60\60\5\2\13\f\17\17\"\"\u00fb\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2"+
		"\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5B\3\2\2\2\7N\3\2\2\2\tW\3\2\2"+
		"\2\13Y\3\2\2\2\r[\3\2\2\2\17]\3\2\2\2\21f\3\2\2\2\23h\3\2\2\2\25j\3\2"+
		"\2\2\27s\3\2\2\2\31y\3\2\2\2\33~\3\2\2\2\35\u008b\3\2\2\2\37\u0091\3\2"+
		"\2\2!\u009d\3\2\2\2#\u00ab\3\2\2\2%\u00b3\3\2\2\2\'\u00c3\3\2\2\2)\u00c5"+
		"\3\2\2\2+\u00c7\3\2\2\2-\u00c9\3\2\2\2/\u00cb\3\2\2\2\61\u00ce\3\2\2\2"+
		"\63\u00da\3\2\2\2\65\u00e5\3\2\2\2\67\u00e7\3\2\2\29\u00ea\3\2\2\2;<\7"+
		"f\2\2<=\7g\2\2=>\7h\2\2>?\7x\2\2?@\7c\2\2@A\7t\2\2A\4\3\2\2\2BC\7f\2\2"+
		"CD\7g\2\2DE\7h\2\2EF\7r\2\2FG\7d\2\2GH\7x\2\2HI\7k\2\2IJ\7u\2\2JK\7q\2"+
		"\2KL\7n\2\2LM\7x\2\2M\6\3\2\2\2NO\7f\2\2OP\7g\2\2PQ\7h\2\2QR\7r\2\2RS"+
		"\7q\2\2ST\7o\2\2TU\7f\2\2UV\7r\2\2V\b\3\2\2\2WX\7U\2\2X\n\3\2\2\2YZ\7"+
		"Q\2\2Z\f\3\2\2\2[\\\7C\2\2\\\16\3\2\2\2]^\7f\2\2^_\7{\2\2_`\7p\2\2`a\7"+
		"c\2\2ab\7o\2\2bc\7k\2\2cd\7e\2\2de\7u\2\2e\20\3\2\2\2fg\7d\2\2g\22\3\2"+
		"\2\2hi\7T\2\2i\24\3\2\2\2jk\7f\2\2kl\7k\2\2lm\7u\2\2mn\7e\2\2no\7q\2\2"+
		"op\7w\2\2pq\7p\2\2qr\7v\2\2r\26\3\2\2\2st\7f\2\2tu\7g\2\2uv\7h\2\2vw\7"+
		"f\2\2wx\7f\2\2x\30\3\2\2\2yz\7U\2\2z{\7C\2\2{|\7O\2\2|}\7G\2\2}\32\3\2"+
		"\2\2~\177\7f\2\2\177\u0080\7g\2\2\u0080\u0081\7h\2\2\u0081\u0082\7f\2"+
		"\2\u0082\u0083\7d\2\2\u0083\u0084\7p\2\2\u0084\34\3\2\2\2\u0085\u0086"+
		"\7G\2\2\u0086\u0087\7P\2\2\u0087\u008c\7X\2\2\u0088\u0089\7g\2\2\u0089"+
		"\u008a\7p\2\2\u008a\u008c\7x\2\2\u008b\u0085\3\2\2\2\u008b\u0088\3\2\2"+
		"\2\u008c\36\3\2\2\2\u008d\u008e\7F\2\2\u008e\u0092\7F\2\2\u008f\u0090"+
		"\7f\2\2\u0090\u0092\7f\2\2\u0091\u008d\3\2\2\2\u0091\u008f\3\2\2\2\u0092"+
		" \3\2\2\2\u0093\u0094\7R\2\2\u0094\u0095\7Q\2\2\u0095\u0096\7O\2\2\u0096"+
		"\u0097\7F\2\2\u0097\u009e\7R\2\2\u0098\u0099\7r\2\2\u0099\u009a\7q\2\2"+
		"\u009a\u009b\7o\2\2\u009b\u009c\7f\2\2\u009c\u009e\7r\2\2\u009d\u0093"+
		"\3\2\2\2\u009d\u0098\3\2\2\2\u009e\"\3\2\2\2\u009f\u00a0\7K\2\2\u00a0"+
		"\u00a1\7R\2\2\u00a1\u00a2\7Q\2\2\u00a2\u00a3\7O\2\2\u00a3\u00a4\7F\2\2"+
		"\u00a4\u00ac\7R\2\2\u00a5\u00a6\7k\2\2\u00a6\u00a7\7r\2\2\u00a7\u00a8"+
		"\7q\2\2\u00a8\u00a9\7o\2\2\u00a9\u00aa\7f\2\2\u00aa\u00ac\7r\2\2\u00ab"+
		"\u009f\3\2\2\2\u00ab\u00a5\3\2\2\2\u00ac$\3\2\2\2\u00ad\u00ae\7F\2\2\u00ae"+
		"\u00af\7D\2\2\u00af\u00b4\7P\2\2\u00b0\u00b1\7f\2\2\u00b1\u00b2\7d\2\2"+
		"\u00b2\u00b4\7p\2\2\u00b3\u00ad\3\2\2\2\u00b3\u00b0\3\2\2\2\u00b4&\3\2"+
		"\2\2\u00b5\u00b6\7w\2\2\u00b6\u00b7\7p\2\2\u00b7\u00b8\7k\2\2\u00b8\u00b9"+
		"\7h\2\2\u00b9\u00ba\7q\2\2\u00ba\u00bb\7t\2\2\u00bb\u00c4\7o\2\2\u00bc"+
		"\u00bd\7W\2\2\u00bd\u00be\7P\2\2\u00be\u00bf\7K\2\2\u00bf\u00c0\7H\2\2"+
		"\u00c0\u00c1\7Q\2\2\u00c1\u00c2\7T\2\2\u00c2\u00c4\7O\2\2\u00c3\u00b5"+
		"\3\2\2\2\u00c3\u00bc\3\2\2\2\u00c4(\3\2\2\2\u00c5\u00c6\7-\2\2\u00c6*"+
		"\3\2\2\2\u00c7\u00c8\7/\2\2\u00c8,\3\2\2\2\u00c9\u00ca\7,\2\2\u00ca.\3"+
		"\2\2\2\u00cb\u00cc\7\61\2\2\u00cc\60\3\2\2\2\u00cd\u00cf\t\2\2\2\u00ce"+
		"\u00cd\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d4\t\3"+
		"\2\2\u00d1\u00d3\t\4\2\2\u00d2\u00d1\3\2\2\2\u00d3\u00d6\3\2\2\2\u00d4"+
		"\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\62\3\2\2\2\u00d6\u00d4\3\2\2"+
		"\2\u00d7\u00d9\t\5\2\2\u00d8\u00d7\3\2\2\2\u00d9\u00dc\3\2\2\2\u00da\u00d8"+
		"\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dd"+
		"\u00df\t\6\2\2\u00de\u00dd\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00e1\3\2"+
		"\2\2\u00e0\u00e2\t\5\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3"+
		"\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\64\3\2\2\2\u00e5\u00e6\7*\2\2"+
		"\u00e6\66\3\2\2\2\u00e7\u00e8\7+\2\2\u00e88\3\2\2\2\u00e9\u00eb\t\7\2"+
		"\2\u00ea\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed"+
		"\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef\b\35\2\2\u00ef:\3\2\2\2\17\2"+
		"\u008b\u0091\u009d\u00ab\u00b3\u00c3\u00ce\u00d4\u00da\u00de\u00e3\u00ec"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}