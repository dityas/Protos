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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, ENV=29, DD=30, POMDP=31, IPOMDP=32, 
		DBN=33, UNIFORM=34, OP_ADD=35, OP_SUB=36, OP_MUL=37, OP_DIV=38, IDENTIFIER=39, 
		FLOAT_NUM=40, LP=41, RP=42, WS=43, LINE_COMMENT=44;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "T__27", "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", 
		"OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", 
		"RP", "WS", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defpbvisolv'", "'defpomdp'", "'defipomdp'", "'defenv'", 
		"'initmodelvar'", "'frames'", "'S'", "'O'", "'A'", "'Aj'", "'Mj'", "'Thetaj'", 
		"'dynamics'", "'b'", "'R'", "'discount'", "'H'", "'defdd'", "'#'", "'SAME'", 
		"'defdbn'", "'run'", "'defpol'", "'='", "'poltree'", "'lisp'", "'solve'", 
		null, null, null, null, null, null, "'+'", "'-'", "'*'", "'/'", null, 
		null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2.\u0171\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3"+
		"\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34"+
		"\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\5\36"+
		"\u0102\n\36\3\37\3\37\3\37\3\37\5\37\u0108\n\37\3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \3 \5 \u0114\n \3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u0122\n!\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\5\"\u012a\n\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#"+
		"\3#\3#\3#\5#\u013a\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\5(\u0145\n(\3(\3(\7"+
		"(\u0149\n(\f(\16(\u014c\13(\3)\7)\u014f\n)\f)\16)\u0152\13)\3)\5)\u0155"+
		"\n)\3)\6)\u0158\n)\r)\16)\u0159\3*\3*\3+\3+\3,\6,\u0161\n,\r,\16,\u0162"+
		"\3,\3,\3-\3-\3-\3-\7-\u016b\n-\f-\16-\u016e\13-\3-\3-\2\2.\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G"+
		"%I&K\'M(O)Q*S+U,W-Y.\3\2\t\3\2aa\4\2C\\c|\b\2))\60\60\62;C\\aac|\3\2\62"+
		";\4\2))\60\60\5\2\13\f\17\17\"\"\4\2\f\f\17\17\u017d\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2"+
		"\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3"+
		"\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'"+
		"\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63"+
		"\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2"+
		"?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3"+
		"\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2"+
		"\2\2Y\3\2\2\2\3[\3\2\2\2\5b\3\2\2\2\7n\3\2\2\2\tw\3\2\2\2\13\u0081\3\2"+
		"\2\2\r\u0088\3\2\2\2\17\u0095\3\2\2\2\21\u009c\3\2\2\2\23\u009e\3\2\2"+
		"\2\25\u00a0\3\2\2\2\27\u00a2\3\2\2\2\31\u00a5\3\2\2\2\33\u00a8\3\2\2\2"+
		"\35\u00af\3\2\2\2\37\u00b8\3\2\2\2!\u00ba\3\2\2\2#\u00bc\3\2\2\2%\u00c5"+
		"\3\2\2\2\'\u00c7\3\2\2\2)\u00cd\3\2\2\2+\u00cf\3\2\2\2-\u00d4\3\2\2\2"+
		"/\u00db\3\2\2\2\61\u00df\3\2\2\2\63\u00e6\3\2\2\2\65\u00e8\3\2\2\2\67"+
		"\u00f0\3\2\2\29\u00f5\3\2\2\2;\u0101\3\2\2\2=\u0107\3\2\2\2?\u0113\3\2"+
		"\2\2A\u0121\3\2\2\2C\u0129\3\2\2\2E\u0139\3\2\2\2G\u013b\3\2\2\2I\u013d"+
		"\3\2\2\2K\u013f\3\2\2\2M\u0141\3\2\2\2O\u0144\3\2\2\2Q\u0150\3\2\2\2S"+
		"\u015b\3\2\2\2U\u015d\3\2\2\2W\u0160\3\2\2\2Y\u0166\3\2\2\2[\\\7f\2\2"+
		"\\]\7g\2\2]^\7h\2\2^_\7x\2\2_`\7c\2\2`a\7t\2\2a\4\3\2\2\2bc\7f\2\2cd\7"+
		"g\2\2de\7h\2\2ef\7r\2\2fg\7d\2\2gh\7x\2\2hi\7k\2\2ij\7u\2\2jk\7q\2\2k"+
		"l\7n\2\2lm\7x\2\2m\6\3\2\2\2no\7f\2\2op\7g\2\2pq\7h\2\2qr\7r\2\2rs\7q"+
		"\2\2st\7o\2\2tu\7f\2\2uv\7r\2\2v\b\3\2\2\2wx\7f\2\2xy\7g\2\2yz\7h\2\2"+
		"z{\7k\2\2{|\7r\2\2|}\7q\2\2}~\7o\2\2~\177\7f\2\2\177\u0080\7r\2\2\u0080"+
		"\n\3\2\2\2\u0081\u0082\7f\2\2\u0082\u0083\7g\2\2\u0083\u0084\7h\2\2\u0084"+
		"\u0085\7g\2\2\u0085\u0086\7p\2\2\u0086\u0087\7x\2\2\u0087\f\3\2\2\2\u0088"+
		"\u0089\7k\2\2\u0089\u008a\7p\2\2\u008a\u008b\7k\2\2\u008b\u008c\7v\2\2"+
		"\u008c\u008d\7o\2\2\u008d\u008e\7q\2\2\u008e\u008f\7f\2\2\u008f\u0090"+
		"\7g\2\2\u0090\u0091\7n\2\2\u0091\u0092\7x\2\2\u0092\u0093\7c\2\2\u0093"+
		"\u0094\7t\2\2\u0094\16\3\2\2\2\u0095\u0096\7h\2\2\u0096\u0097\7t\2\2\u0097"+
		"\u0098\7c\2\2\u0098\u0099\7o\2\2\u0099\u009a\7g\2\2\u009a\u009b\7u\2\2"+
		"\u009b\20\3\2\2\2\u009c\u009d\7U\2\2\u009d\22\3\2\2\2\u009e\u009f\7Q\2"+
		"\2\u009f\24\3\2\2\2\u00a0\u00a1\7C\2\2\u00a1\26\3\2\2\2\u00a2\u00a3\7"+
		"C\2\2\u00a3\u00a4\7l\2\2\u00a4\30\3\2\2\2\u00a5\u00a6\7O\2\2\u00a6\u00a7"+
		"\7l\2\2\u00a7\32\3\2\2\2\u00a8\u00a9\7V\2\2\u00a9\u00aa\7j\2\2\u00aa\u00ab"+
		"\7g\2\2\u00ab\u00ac\7v\2\2\u00ac\u00ad\7c\2\2\u00ad\u00ae\7l\2\2\u00ae"+
		"\34\3\2\2\2\u00af\u00b0\7f\2\2\u00b0\u00b1\7{\2\2\u00b1\u00b2\7p\2\2\u00b2"+
		"\u00b3\7c\2\2\u00b3\u00b4\7o\2\2\u00b4\u00b5\7k\2\2\u00b5\u00b6\7e\2\2"+
		"\u00b6\u00b7\7u\2\2\u00b7\36\3\2\2\2\u00b8\u00b9\7d\2\2\u00b9 \3\2\2\2"+
		"\u00ba\u00bb\7T\2\2\u00bb\"\3\2\2\2\u00bc\u00bd\7f\2\2\u00bd\u00be\7k"+
		"\2\2\u00be\u00bf\7u\2\2\u00bf\u00c0\7e\2\2\u00c0\u00c1\7q\2\2\u00c1\u00c2"+
		"\7w\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7v\2\2\u00c4$\3\2\2\2\u00c5\u00c6"+
		"\7J\2\2\u00c6&\3\2\2\2\u00c7\u00c8\7f\2\2\u00c8\u00c9\7g\2\2\u00c9\u00ca"+
		"\7h\2\2\u00ca\u00cb\7f\2\2\u00cb\u00cc\7f\2\2\u00cc(\3\2\2\2\u00cd\u00ce"+
		"\7%\2\2\u00ce*\3\2\2\2\u00cf\u00d0\7U\2\2\u00d0\u00d1\7C\2\2\u00d1\u00d2"+
		"\7O\2\2\u00d2\u00d3\7G\2\2\u00d3,\3\2\2\2\u00d4\u00d5\7f\2\2\u00d5\u00d6"+
		"\7g\2\2\u00d6\u00d7\7h\2\2\u00d7\u00d8\7f\2\2\u00d8\u00d9\7d\2\2\u00d9"+
		"\u00da\7p\2\2\u00da.\3\2\2\2\u00db\u00dc\7t\2\2\u00dc\u00dd\7w\2\2\u00dd"+
		"\u00de\7p\2\2\u00de\60\3\2\2\2\u00df\u00e0\7f\2\2\u00e0\u00e1\7g\2\2\u00e1"+
		"\u00e2\7h\2\2\u00e2\u00e3\7r\2\2\u00e3\u00e4\7q\2\2\u00e4\u00e5\7n\2\2"+
		"\u00e5\62\3\2\2\2\u00e6\u00e7\7?\2\2\u00e7\64\3\2\2\2\u00e8\u00e9\7r\2"+
		"\2\u00e9\u00ea\7q\2\2\u00ea\u00eb\7n\2\2\u00eb\u00ec\7v\2\2\u00ec\u00ed"+
		"\7t\2\2\u00ed\u00ee\7g\2\2\u00ee\u00ef\7g\2\2\u00ef\66\3\2\2\2\u00f0\u00f1"+
		"\7n\2\2\u00f1\u00f2\7k\2\2\u00f2\u00f3\7u\2\2\u00f3\u00f4\7r\2\2\u00f4"+
		"8\3\2\2\2\u00f5\u00f6\7u\2\2\u00f6\u00f7\7q\2\2\u00f7\u00f8\7n\2\2\u00f8"+
		"\u00f9\7x\2\2\u00f9\u00fa\7g\2\2\u00fa:\3\2\2\2\u00fb\u00fc\7G\2\2\u00fc"+
		"\u00fd\7P\2\2\u00fd\u0102\7X\2\2\u00fe\u00ff\7g\2\2\u00ff\u0100\7p\2\2"+
		"\u0100\u0102\7x\2\2\u0101\u00fb\3\2\2\2\u0101\u00fe\3\2\2\2\u0102<\3\2"+
		"\2\2\u0103\u0104\7F\2\2\u0104\u0108\7F\2\2\u0105\u0106\7f\2\2\u0106\u0108"+
		"\7f\2\2\u0107\u0103\3\2\2\2\u0107\u0105\3\2\2\2\u0108>\3\2\2\2\u0109\u010a"+
		"\7R\2\2\u010a\u010b\7Q\2\2\u010b\u010c\7O\2\2\u010c\u010d\7F\2\2\u010d"+
		"\u0114\7R\2\2\u010e\u010f\7r\2\2\u010f\u0110\7q\2\2\u0110\u0111\7o\2\2"+
		"\u0111\u0112\7f\2\2\u0112\u0114\7r\2\2\u0113\u0109\3\2\2\2\u0113\u010e"+
		"\3\2\2\2\u0114@\3\2\2\2\u0115\u0116\7K\2\2\u0116\u0117\7R\2\2\u0117\u0118"+
		"\7Q\2\2\u0118\u0119\7O\2\2\u0119\u011a\7F\2\2\u011a\u0122\7R\2\2\u011b"+
		"\u011c\7k\2\2\u011c\u011d\7r\2\2\u011d\u011e\7q\2\2\u011e\u011f\7o\2\2"+
		"\u011f\u0120\7f\2\2\u0120\u0122\7r\2\2\u0121\u0115\3\2\2\2\u0121\u011b"+
		"\3\2\2\2\u0122B\3\2\2\2\u0123\u0124\7F\2\2\u0124\u0125\7D\2\2\u0125\u012a"+
		"\7P\2\2\u0126\u0127\7f\2\2\u0127\u0128\7d\2\2\u0128\u012a\7p\2\2\u0129"+
		"\u0123\3\2\2\2\u0129\u0126\3\2\2\2\u012aD\3\2\2\2\u012b\u012c\7w\2\2\u012c"+
		"\u012d\7p\2\2\u012d\u012e\7k\2\2\u012e\u012f\7h\2\2\u012f\u0130\7q\2\2"+
		"\u0130\u0131\7t\2\2\u0131\u013a\7o\2\2\u0132\u0133\7W\2\2\u0133\u0134"+
		"\7P\2\2\u0134\u0135\7K\2\2\u0135\u0136\7H\2\2\u0136\u0137\7Q\2\2\u0137"+
		"\u0138\7T\2\2\u0138\u013a\7O\2\2\u0139\u012b\3\2\2\2\u0139\u0132\3\2\2"+
		"\2\u013aF\3\2\2\2\u013b\u013c\7-\2\2\u013cH\3\2\2\2\u013d\u013e\7/\2\2"+
		"\u013eJ\3\2\2\2\u013f\u0140\7,\2\2\u0140L\3\2\2\2\u0141\u0142\7\61\2\2"+
		"\u0142N\3\2\2\2\u0143\u0145\t\2\2\2\u0144\u0143\3\2\2\2\u0144\u0145\3"+
		"\2\2\2\u0145\u0146\3\2\2\2\u0146\u014a\t\3\2\2\u0147\u0149\t\4\2\2\u0148"+
		"\u0147\3\2\2\2\u0149\u014c\3\2\2\2\u014a\u0148\3\2\2\2\u014a\u014b\3\2"+
		"\2\2\u014bP\3\2\2\2\u014c\u014a\3\2\2\2\u014d\u014f\t\5\2\2\u014e\u014d"+
		"\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151"+
		"\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u0155\t\6\2\2\u0154\u0153\3\2"+
		"\2\2\u0154\u0155\3\2\2\2\u0155\u0157\3\2\2\2\u0156\u0158\t\5\2\2\u0157"+
		"\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u0157\3\2\2\2\u0159\u015a\3\2"+
		"\2\2\u015aR\3\2\2\2\u015b\u015c\7*\2\2\u015cT\3\2\2\2\u015d\u015e\7+\2"+
		"\2\u015eV\3\2\2\2\u015f\u0161\t\7\2\2\u0160\u015f\3\2\2\2\u0161\u0162"+
		"\3\2\2\2\u0162\u0160\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0164\3\2\2\2\u0164"+
		"\u0165\b,\2\2\u0165X\3\2\2\2\u0166\u0167\7/\2\2\u0167\u0168\7/\2\2\u0168"+
		"\u016c\3\2\2\2\u0169\u016b\n\b\2\2\u016a\u0169\3\2\2\2\u016b\u016e\3\2"+
		"\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016f\3\2\2\2\u016e"+
		"\u016c\3\2\2\2\u016f\u0170\b-\2\2\u0170Z\3\2\2\2\20\2\u0101\u0107\u0113"+
		"\u0121\u0129\u0139\u0144\u014a\u0150\u0154\u0159\u0162\u016c\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}