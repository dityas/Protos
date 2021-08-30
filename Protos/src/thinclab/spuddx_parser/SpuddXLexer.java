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
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, ENV=24, DD=25, 
		POMDP=26, IPOMDP=27, DBN=28, UNIFORM=29, OP_ADD=30, OP_SUB=31, OP_MUL=32, 
		OP_DIV=33, IDENTIFIER=34, FLOAT_NUM=35, LP=36, RP=37, WS=38, LINE_COMMENT=39;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "ENV", "DD", "POMDP", 
		"IPOMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", 
		"FLOAT_NUM", "LP", "RP", "WS", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defmodelvar'", "'defpbvisolv'", "'defpomdp'", "'defipomdp'", 
		"'S'", "'O'", "'A'", "'Aj'", "'Mj'", "'Thetaj'", "'dynamics'", "'b'", 
		"'R'", "'discount'", "'H'", "'defdd'", "'#'", "'SAME'", "'defdbn'", "'run'", 
		"'='", "'solve'", null, null, null, null, null, null, "'+'", "'-'", "'*'", 
		"'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", 
		"OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS", "LINE_COMMENT"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2)\u0144\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3"+
		"\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26"+
		"\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\5\31\u00d5\n\31\3\32\3\32\3\32\3\32\5\32\u00db\n\32\3\33\3\33\3\33\3"+
		"\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u00e7\n\33\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u00f5\n\34\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\5\35\u00fd\n\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\5\36\u010d\n\36\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\3#\5#\u0118\n#\3#\3#\7#\u011c\n#\f#\16#\u011f\13#\3$\7$\u0122\n"+
		"$\f$\16$\u0125\13$\3$\5$\u0128\n$\3$\6$\u012b\n$\r$\16$\u012c\3%\3%\3"+
		"&\3&\3\'\6\'\u0134\n\'\r\'\16\'\u0135\3\'\3\'\3(\3(\3(\3(\7(\u013e\n("+
		"\f(\16(\u0141\13(\3(\3(\2\2)\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61"+
		"\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)\3\2\t\3\2aa\4\2C\\"+
		"c|\7\2))\62;C\\aac|\3\2\62;\4\2))\60\60\5\2\13\f\17\17\"\"\4\2\f\f\17"+
		"\17\u0150\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\3Q\3\2\2\2\5X"+
		"\3\2\2\2\7d\3\2\2\2\tp\3\2\2\2\13y\3\2\2\2\r\u0083\3\2\2\2\17\u0085\3"+
		"\2\2\2\21\u0087\3\2\2\2\23\u0089\3\2\2\2\25\u008c\3\2\2\2\27\u008f\3\2"+
		"\2\2\31\u0096\3\2\2\2\33\u009f\3\2\2\2\35\u00a1\3\2\2\2\37\u00a3\3\2\2"+
		"\2!\u00ac\3\2\2\2#\u00ae\3\2\2\2%\u00b4\3\2\2\2\'\u00b6\3\2\2\2)\u00bb"+
		"\3\2\2\2+\u00c2\3\2\2\2-\u00c6\3\2\2\2/\u00c8\3\2\2\2\61\u00d4\3\2\2\2"+
		"\63\u00da\3\2\2\2\65\u00e6\3\2\2\2\67\u00f4\3\2\2\29\u00fc\3\2\2\2;\u010c"+
		"\3\2\2\2=\u010e\3\2\2\2?\u0110\3\2\2\2A\u0112\3\2\2\2C\u0114\3\2\2\2E"+
		"\u0117\3\2\2\2G\u0123\3\2\2\2I\u012e\3\2\2\2K\u0130\3\2\2\2M\u0133\3\2"+
		"\2\2O\u0139\3\2\2\2QR\7f\2\2RS\7g\2\2ST\7h\2\2TU\7x\2\2UV\7c\2\2VW\7t"+
		"\2\2W\4\3\2\2\2XY\7f\2\2YZ\7g\2\2Z[\7h\2\2[\\\7o\2\2\\]\7q\2\2]^\7f\2"+
		"\2^_\7g\2\2_`\7n\2\2`a\7x\2\2ab\7c\2\2bc\7t\2\2c\6\3\2\2\2de\7f\2\2ef"+
		"\7g\2\2fg\7h\2\2gh\7r\2\2hi\7d\2\2ij\7x\2\2jk\7k\2\2kl\7u\2\2lm\7q\2\2"+
		"mn\7n\2\2no\7x\2\2o\b\3\2\2\2pq\7f\2\2qr\7g\2\2rs\7h\2\2st\7r\2\2tu\7"+
		"q\2\2uv\7o\2\2vw\7f\2\2wx\7r\2\2x\n\3\2\2\2yz\7f\2\2z{\7g\2\2{|\7h\2\2"+
		"|}\7k\2\2}~\7r\2\2~\177\7q\2\2\177\u0080\7o\2\2\u0080\u0081\7f\2\2\u0081"+
		"\u0082\7r\2\2\u0082\f\3\2\2\2\u0083\u0084\7U\2\2\u0084\16\3\2\2\2\u0085"+
		"\u0086\7Q\2\2\u0086\20\3\2\2\2\u0087\u0088\7C\2\2\u0088\22\3\2\2\2\u0089"+
		"\u008a\7C\2\2\u008a\u008b\7l\2\2\u008b\24\3\2\2\2\u008c\u008d\7O\2\2\u008d"+
		"\u008e\7l\2\2\u008e\26\3\2\2\2\u008f\u0090\7V\2\2\u0090\u0091\7j\2\2\u0091"+
		"\u0092\7g\2\2\u0092\u0093\7v\2\2\u0093\u0094\7c\2\2\u0094\u0095\7l\2\2"+
		"\u0095\30\3\2\2\2\u0096\u0097\7f\2\2\u0097\u0098\7{\2\2\u0098\u0099\7"+
		"p\2\2\u0099\u009a\7c\2\2\u009a\u009b\7o\2\2\u009b\u009c\7k\2\2\u009c\u009d"+
		"\7e\2\2\u009d\u009e\7u\2\2\u009e\32\3\2\2\2\u009f\u00a0\7d\2\2\u00a0\34"+
		"\3\2\2\2\u00a1\u00a2\7T\2\2\u00a2\36\3\2\2\2\u00a3\u00a4\7f\2\2\u00a4"+
		"\u00a5\7k\2\2\u00a5\u00a6\7u\2\2\u00a6\u00a7\7e\2\2\u00a7\u00a8\7q\2\2"+
		"\u00a8\u00a9\7w\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7v\2\2\u00ab \3\2\2"+
		"\2\u00ac\u00ad\7J\2\2\u00ad\"\3\2\2\2\u00ae\u00af\7f\2\2\u00af\u00b0\7"+
		"g\2\2\u00b0\u00b1\7h\2\2\u00b1\u00b2\7f\2\2\u00b2\u00b3\7f\2\2\u00b3$"+
		"\3\2\2\2\u00b4\u00b5\7%\2\2\u00b5&\3\2\2\2\u00b6\u00b7\7U\2\2\u00b7\u00b8"+
		"\7C\2\2\u00b8\u00b9\7O\2\2\u00b9\u00ba\7G\2\2\u00ba(\3\2\2\2\u00bb\u00bc"+
		"\7f\2\2\u00bc\u00bd\7g\2\2\u00bd\u00be\7h\2\2\u00be\u00bf\7f\2\2\u00bf"+
		"\u00c0\7d\2\2\u00c0\u00c1\7p\2\2\u00c1*\3\2\2\2\u00c2\u00c3\7t\2\2\u00c3"+
		"\u00c4\7w\2\2\u00c4\u00c5\7p\2\2\u00c5,\3\2\2\2\u00c6\u00c7\7?\2\2\u00c7"+
		".\3\2\2\2\u00c8\u00c9\7u\2\2\u00c9\u00ca\7q\2\2\u00ca\u00cb\7n\2\2\u00cb"+
		"\u00cc\7x\2\2\u00cc\u00cd\7g\2\2\u00cd\60\3\2\2\2\u00ce\u00cf\7G\2\2\u00cf"+
		"\u00d0\7P\2\2\u00d0\u00d5\7X\2\2\u00d1\u00d2\7g\2\2\u00d2\u00d3\7p\2\2"+
		"\u00d3\u00d5\7x\2\2\u00d4\u00ce\3\2\2\2\u00d4\u00d1\3\2\2\2\u00d5\62\3"+
		"\2\2\2\u00d6\u00d7\7F\2\2\u00d7\u00db\7F\2\2\u00d8\u00d9\7f\2\2\u00d9"+
		"\u00db\7f\2\2\u00da\u00d6\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\64\3\2\2\2"+
		"\u00dc\u00dd\7R\2\2\u00dd\u00de\7Q\2\2\u00de\u00df\7O\2\2\u00df\u00e0"+
		"\7F\2\2\u00e0\u00e7\7R\2\2\u00e1\u00e2\7r\2\2\u00e2\u00e3\7q\2\2\u00e3"+
		"\u00e4\7o\2\2\u00e4\u00e5\7f\2\2\u00e5\u00e7\7r\2\2\u00e6\u00dc\3\2\2"+
		"\2\u00e6\u00e1\3\2\2\2\u00e7\66\3\2\2\2\u00e8\u00e9\7K\2\2\u00e9\u00ea"+
		"\7R\2\2\u00ea\u00eb\7Q\2\2\u00eb\u00ec\7O\2\2\u00ec\u00ed\7F\2\2\u00ed"+
		"\u00f5\7R\2\2\u00ee\u00ef\7k\2\2\u00ef\u00f0\7r\2\2\u00f0\u00f1\7q\2\2"+
		"\u00f1\u00f2\7o\2\2\u00f2\u00f3\7f\2\2\u00f3\u00f5\7r\2\2\u00f4\u00e8"+
		"\3\2\2\2\u00f4\u00ee\3\2\2\2\u00f58\3\2\2\2\u00f6\u00f7\7F\2\2\u00f7\u00f8"+
		"\7D\2\2\u00f8\u00fd\7P\2\2\u00f9\u00fa\7f\2\2\u00fa\u00fb\7d\2\2\u00fb"+
		"\u00fd\7p\2\2\u00fc\u00f6\3\2\2\2\u00fc\u00f9\3\2\2\2\u00fd:\3\2\2\2\u00fe"+
		"\u00ff\7w\2\2\u00ff\u0100\7p\2\2\u0100\u0101\7k\2\2\u0101\u0102\7h\2\2"+
		"\u0102\u0103\7q\2\2\u0103\u0104\7t\2\2\u0104\u010d\7o\2\2\u0105\u0106"+
		"\7W\2\2\u0106\u0107\7P\2\2\u0107\u0108\7K\2\2\u0108\u0109\7H\2\2\u0109"+
		"\u010a\7Q\2\2\u010a\u010b\7T\2\2\u010b\u010d\7O\2\2\u010c\u00fe\3\2\2"+
		"\2\u010c\u0105\3\2\2\2\u010d<\3\2\2\2\u010e\u010f\7-\2\2\u010f>\3\2\2"+
		"\2\u0110\u0111\7/\2\2\u0111@\3\2\2\2\u0112\u0113\7,\2\2\u0113B\3\2\2\2"+
		"\u0114\u0115\7\61\2\2\u0115D\3\2\2\2\u0116\u0118\t\2\2\2\u0117\u0116\3"+
		"\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011d\t\3\2\2\u011a"+
		"\u011c\t\4\2\2\u011b\u011a\3\2\2\2\u011c\u011f\3\2\2\2\u011d\u011b\3\2"+
		"\2\2\u011d\u011e\3\2\2\2\u011eF\3\2\2\2\u011f\u011d\3\2\2\2\u0120\u0122"+
		"\t\5\2\2\u0121\u0120\3\2\2\2\u0122\u0125\3\2\2\2\u0123\u0121\3\2\2\2\u0123"+
		"\u0124\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u0128\t\6"+
		"\2\2\u0127\u0126\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u012a\3\2\2\2\u0129"+
		"\u012b\t\5\2\2\u012a\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012a\3\2"+
		"\2\2\u012c\u012d\3\2\2\2\u012dH\3\2\2\2\u012e\u012f\7*\2\2\u012fJ\3\2"+
		"\2\2\u0130\u0131\7+\2\2\u0131L\3\2\2\2\u0132\u0134\t\7\2\2\u0133\u0132"+
		"\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136"+
		"\u0137\3\2\2\2\u0137\u0138\b\'\2\2\u0138N\3\2\2\2\u0139\u013a\7/\2\2\u013a"+
		"\u013b\7/\2\2\u013b\u013f\3\2\2\2\u013c\u013e\n\b\2\2\u013d\u013c\3\2"+
		"\2\2\u013e\u0141\3\2\2\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140"+
		"\u0142\3\2\2\2\u0141\u013f\3\2\2\2\u0142\u0143\b(\2\2\u0143P\3\2\2\2\20"+
		"\2\u00d4\u00da\u00e6\u00f4\u00fc\u010c\u0117\u011d\u0123\u0127\u012c\u0135"+
		"\u013f\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}