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
		T__24=25, T__25=26, ENV=27, DD=28, POMDP=29, IPOMDP=30, DBN=31, UNIFORM=32, 
		OP_ADD=33, OP_SUB=34, OP_MUL=35, OP_DIV=36, IDENTIFIER=37, FLOAT_NUM=38, 
		LP=39, RP=40, WS=41, LINE_COMMENT=42;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", 
		"OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defpbvisolv'", "'defpomdp'", "'defipomdp'", "'initmodelvar'", 
		"'frames'", "'S'", "'O'", "'A'", "'Aj'", "'Mj'", "'Thetaj'", "'dynamics'", 
		"'b'", "'R'", "'discount'", "'H'", "'defdd'", "'#'", "'SAME'", "'defdbn'", 
		"'run'", "'defpol'", "'='", "'poltree'", "'solve'", null, null, null, 
		null, null, null, "'+'", "'-'", "'*'", "'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, "ENV", "DD", "POMDP", "IPOMDP", "DBN", "UNIFORM", "OP_ADD", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2,\u0161\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25"+
		"\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\5\34"+
		"\u00f2\n\34\3\35\3\35\3\35\3\35\5\35\u00f8\n\35\3\36\3\36\3\36\3\36\3"+
		"\36\3\36\3\36\3\36\3\36\3\36\5\36\u0104\n\36\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u0112\n\37\3 \3 \3 \3 \3 \3 "+
		"\5 \u011a\n \3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u012a\n!\3\""+
		"\3\"\3#\3#\3$\3$\3%\3%\3&\5&\u0135\n&\3&\3&\7&\u0139\n&\f&\16&\u013c\13"+
		"&\3\'\7\'\u013f\n\'\f\'\16\'\u0142\13\'\3\'\5\'\u0145\n\'\3\'\6\'\u0148"+
		"\n\'\r\'\16\'\u0149\3(\3(\3)\3)\3*\6*\u0151\n*\r*\16*\u0152\3*\3*\3+\3"+
		"+\3+\3+\7+\u015b\n+\f+\16+\u015e\13+\3+\3+\2\2,\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O"+
		")Q*S+U,\3\2\t\3\2aa\4\2C\\c|\7\2))\62;C\\aac|\3\2\62;\4\2))\60\60\5\2"+
		"\13\f\17\17\"\"\4\2\f\f\17\17\u016d\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2"+
		"\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3"+
		"\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2"+
		"\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2"+
		"\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\3W\3\2\2\2\5^\3\2\2\2\7j"+
		"\3\2\2\2\ts\3\2\2\2\13}\3\2\2\2\r\u008a\3\2\2\2\17\u0091\3\2\2\2\21\u0093"+
		"\3\2\2\2\23\u0095\3\2\2\2\25\u0097\3\2\2\2\27\u009a\3\2\2\2\31\u009d\3"+
		"\2\2\2\33\u00a4\3\2\2\2\35\u00ad\3\2\2\2\37\u00af\3\2\2\2!\u00b1\3\2\2"+
		"\2#\u00ba\3\2\2\2%\u00bc\3\2\2\2\'\u00c2\3\2\2\2)\u00c4\3\2\2\2+\u00c9"+
		"\3\2\2\2-\u00d0\3\2\2\2/\u00d4\3\2\2\2\61\u00db\3\2\2\2\63\u00dd\3\2\2"+
		"\2\65\u00e5\3\2\2\2\67\u00f1\3\2\2\29\u00f7\3\2\2\2;\u0103\3\2\2\2=\u0111"+
		"\3\2\2\2?\u0119\3\2\2\2A\u0129\3\2\2\2C\u012b\3\2\2\2E\u012d\3\2\2\2G"+
		"\u012f\3\2\2\2I\u0131\3\2\2\2K\u0134\3\2\2\2M\u0140\3\2\2\2O\u014b\3\2"+
		"\2\2Q\u014d\3\2\2\2S\u0150\3\2\2\2U\u0156\3\2\2\2WX\7f\2\2XY\7g\2\2YZ"+
		"\7h\2\2Z[\7x\2\2[\\\7c\2\2\\]\7t\2\2]\4\3\2\2\2^_\7f\2\2_`\7g\2\2`a\7"+
		"h\2\2ab\7r\2\2bc\7d\2\2cd\7x\2\2de\7k\2\2ef\7u\2\2fg\7q\2\2gh\7n\2\2h"+
		"i\7x\2\2i\6\3\2\2\2jk\7f\2\2kl\7g\2\2lm\7h\2\2mn\7r\2\2no\7q\2\2op\7o"+
		"\2\2pq\7f\2\2qr\7r\2\2r\b\3\2\2\2st\7f\2\2tu\7g\2\2uv\7h\2\2vw\7k\2\2"+
		"wx\7r\2\2xy\7q\2\2yz\7o\2\2z{\7f\2\2{|\7r\2\2|\n\3\2\2\2}~\7k\2\2~\177"+
		"\7p\2\2\177\u0080\7k\2\2\u0080\u0081\7v\2\2\u0081\u0082\7o\2\2\u0082\u0083"+
		"\7q\2\2\u0083\u0084\7f\2\2\u0084\u0085\7g\2\2\u0085\u0086\7n\2\2\u0086"+
		"\u0087\7x\2\2\u0087\u0088\7c\2\2\u0088\u0089\7t\2\2\u0089\f\3\2\2\2\u008a"+
		"\u008b\7h\2\2\u008b\u008c\7t\2\2\u008c\u008d\7c\2\2\u008d\u008e\7o\2\2"+
		"\u008e\u008f\7g\2\2\u008f\u0090\7u\2\2\u0090\16\3\2\2\2\u0091\u0092\7"+
		"U\2\2\u0092\20\3\2\2\2\u0093\u0094\7Q\2\2\u0094\22\3\2\2\2\u0095\u0096"+
		"\7C\2\2\u0096\24\3\2\2\2\u0097\u0098\7C\2\2\u0098\u0099\7l\2\2\u0099\26"+
		"\3\2\2\2\u009a\u009b\7O\2\2\u009b\u009c\7l\2\2\u009c\30\3\2\2\2\u009d"+
		"\u009e\7V\2\2\u009e\u009f\7j\2\2\u009f\u00a0\7g\2\2\u00a0\u00a1\7v\2\2"+
		"\u00a1\u00a2\7c\2\2\u00a2\u00a3\7l\2\2\u00a3\32\3\2\2\2\u00a4\u00a5\7"+
		"f\2\2\u00a5\u00a6\7{\2\2\u00a6\u00a7\7p\2\2\u00a7\u00a8\7c\2\2\u00a8\u00a9"+
		"\7o\2\2\u00a9\u00aa\7k\2\2\u00aa\u00ab\7e\2\2\u00ab\u00ac\7u\2\2\u00ac"+
		"\34\3\2\2\2\u00ad\u00ae\7d\2\2\u00ae\36\3\2\2\2\u00af\u00b0\7T\2\2\u00b0"+
		" \3\2\2\2\u00b1\u00b2\7f\2\2\u00b2\u00b3\7k\2\2\u00b3\u00b4\7u\2\2\u00b4"+
		"\u00b5\7e\2\2\u00b5\u00b6\7q\2\2\u00b6\u00b7\7w\2\2\u00b7\u00b8\7p\2\2"+
		"\u00b8\u00b9\7v\2\2\u00b9\"\3\2\2\2\u00ba\u00bb\7J\2\2\u00bb$\3\2\2\2"+
		"\u00bc\u00bd\7f\2\2\u00bd\u00be\7g\2\2\u00be\u00bf\7h\2\2\u00bf\u00c0"+
		"\7f\2\2\u00c0\u00c1\7f\2\2\u00c1&\3\2\2\2\u00c2\u00c3\7%\2\2\u00c3(\3"+
		"\2\2\2\u00c4\u00c5\7U\2\2\u00c5\u00c6\7C\2\2\u00c6\u00c7\7O\2\2\u00c7"+
		"\u00c8\7G\2\2\u00c8*\3\2\2\2\u00c9\u00ca\7f\2\2\u00ca\u00cb\7g\2\2\u00cb"+
		"\u00cc\7h\2\2\u00cc\u00cd\7f\2\2\u00cd\u00ce\7d\2\2\u00ce\u00cf\7p\2\2"+
		"\u00cf,\3\2\2\2\u00d0\u00d1\7t\2\2\u00d1\u00d2\7w\2\2\u00d2\u00d3\7p\2"+
		"\2\u00d3.\3\2\2\2\u00d4\u00d5\7f\2\2\u00d5\u00d6\7g\2\2\u00d6\u00d7\7"+
		"h\2\2\u00d7\u00d8\7r\2\2\u00d8\u00d9\7q\2\2\u00d9\u00da\7n\2\2\u00da\60"+
		"\3\2\2\2\u00db\u00dc\7?\2\2\u00dc\62\3\2\2\2\u00dd\u00de\7r\2\2\u00de"+
		"\u00df\7q\2\2\u00df\u00e0\7n\2\2\u00e0\u00e1\7v\2\2\u00e1\u00e2\7t\2\2"+
		"\u00e2\u00e3\7g\2\2\u00e3\u00e4\7g\2\2\u00e4\64\3\2\2\2\u00e5\u00e6\7"+
		"u\2\2\u00e6\u00e7\7q\2\2\u00e7\u00e8\7n\2\2\u00e8\u00e9\7x\2\2\u00e9\u00ea"+
		"\7g\2\2\u00ea\66\3\2\2\2\u00eb\u00ec\7G\2\2\u00ec\u00ed\7P\2\2\u00ed\u00f2"+
		"\7X\2\2\u00ee\u00ef\7g\2\2\u00ef\u00f0\7p\2\2\u00f0\u00f2\7x\2\2\u00f1"+
		"\u00eb\3\2\2\2\u00f1\u00ee\3\2\2\2\u00f28\3\2\2\2\u00f3\u00f4\7F\2\2\u00f4"+
		"\u00f8\7F\2\2\u00f5\u00f6\7f\2\2\u00f6\u00f8\7f\2\2\u00f7\u00f3\3\2\2"+
		"\2\u00f7\u00f5\3\2\2\2\u00f8:\3\2\2\2\u00f9\u00fa\7R\2\2\u00fa\u00fb\7"+
		"Q\2\2\u00fb\u00fc\7O\2\2\u00fc\u00fd\7F\2\2\u00fd\u0104\7R\2\2\u00fe\u00ff"+
		"\7r\2\2\u00ff\u0100\7q\2\2\u0100\u0101\7o\2\2\u0101\u0102\7f\2\2\u0102"+
		"\u0104\7r\2\2\u0103\u00f9\3\2\2\2\u0103\u00fe\3\2\2\2\u0104<\3\2\2\2\u0105"+
		"\u0106\7K\2\2\u0106\u0107\7R\2\2\u0107\u0108\7Q\2\2\u0108\u0109\7O\2\2"+
		"\u0109\u010a\7F\2\2\u010a\u0112\7R\2\2\u010b\u010c\7k\2\2\u010c\u010d"+
		"\7r\2\2\u010d\u010e\7q\2\2\u010e\u010f\7o\2\2\u010f\u0110\7f\2\2\u0110"+
		"\u0112\7r\2\2\u0111\u0105\3\2\2\2\u0111\u010b\3\2\2\2\u0112>\3\2\2\2\u0113"+
		"\u0114\7F\2\2\u0114\u0115\7D\2\2\u0115\u011a\7P\2\2\u0116\u0117\7f\2\2"+
		"\u0117\u0118\7d\2\2\u0118\u011a\7p\2\2\u0119\u0113\3\2\2\2\u0119\u0116"+
		"\3\2\2\2\u011a@\3\2\2\2\u011b\u011c\7w\2\2\u011c\u011d\7p\2\2\u011d\u011e"+
		"\7k\2\2\u011e\u011f\7h\2\2\u011f\u0120\7q\2\2\u0120\u0121\7t\2\2\u0121"+
		"\u012a\7o\2\2\u0122\u0123\7W\2\2\u0123\u0124\7P\2\2\u0124\u0125\7K\2\2"+
		"\u0125\u0126\7H\2\2\u0126\u0127\7Q\2\2\u0127\u0128\7T\2\2\u0128\u012a"+
		"\7O\2\2\u0129\u011b\3\2\2\2\u0129\u0122\3\2\2\2\u012aB\3\2\2\2\u012b\u012c"+
		"\7-\2\2\u012cD\3\2\2\2\u012d\u012e\7/\2\2\u012eF\3\2\2\2\u012f\u0130\7"+
		",\2\2\u0130H\3\2\2\2\u0131\u0132\7\61\2\2\u0132J\3\2\2\2\u0133\u0135\t"+
		"\2\2\2\u0134\u0133\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0136\3\2\2\2\u0136"+
		"\u013a\t\3\2\2\u0137\u0139\t\4\2\2\u0138\u0137\3\2\2\2\u0139\u013c\3\2"+
		"\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013bL\3\2\2\2\u013c\u013a"+
		"\3\2\2\2\u013d\u013f\t\5\2\2\u013e\u013d\3\2\2\2\u013f\u0142\3\2\2\2\u0140"+
		"\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0144\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0143\u0145\t\6\2\2\u0144\u0143\3\2\2\2\u0144\u0145\3\2\2\2\u0145"+
		"\u0147\3\2\2\2\u0146\u0148\t\5\2\2\u0147\u0146\3\2\2\2\u0148\u0149\3\2"+
		"\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014aN\3\2\2\2\u014b\u014c"+
		"\7*\2\2\u014cP\3\2\2\2\u014d\u014e\7+\2\2\u014eR\3\2\2\2\u014f\u0151\t"+
		"\7\2\2\u0150\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0150\3\2\2\2\u0152"+
		"\u0153\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0155\b*\2\2\u0155T\3\2\2\2\u0156"+
		"\u0157\7/\2\2\u0157\u0158\7/\2\2\u0158\u015c\3\2\2\2\u0159\u015b\n\b\2"+
		"\2\u015a\u0159\3\2\2\2\u015b\u015e\3\2\2\2\u015c\u015a\3\2\2\2\u015c\u015d"+
		"\3\2\2\2\u015d\u015f\3\2\2\2\u015e\u015c\3\2\2\2\u015f\u0160\b+\2\2\u0160"+
		"V\3\2\2\2\20\2\u00f1\u00f7\u0103\u0111\u0119\u0129\u0134\u013a\u0140\u0144"+
		"\u0149\u0152\u015c\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}