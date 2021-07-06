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
		T__9=10, ENV=11, DD=12, POMDP=13, DBN=14, UNIFORM=15, OP_ADD=16, OP_SUB=17, 
		OP_MUL=18, OP_DIV=19, IDENTIFIER=20, FLOAT_NUM=21, LP=22, RP=23, WS=24;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "ENV", "DD", "POMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", 
		"OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'S'", "'Agents'", "'O'", "'A'", "'dynamics'", "'b'", 
		"'R'", "'discount'", "'SAME'", null, null, null, null, null, "'+'", "'-'", 
		"'*'", "'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, "ENV", 
		"DD", "POMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", 
		"IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\32\u00c2\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\5\fl\n\f\3\r\3\r\3\r\3\r\5\rr\n\r\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16~\n\16\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\5\17\u0086\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\5\20\u0096\n\20\3\21\3\21\3\22\3\22\3\23\3\23"+
		"\3\24\3\24\3\25\5\25\u00a1\n\25\3\25\3\25\7\25\u00a5\n\25\f\25\16\25\u00a8"+
		"\13\25\3\26\7\26\u00ab\n\26\f\26\16\26\u00ae\13\26\3\26\5\26\u00b1\n\26"+
		"\3\26\6\26\u00b4\n\26\r\26\16\26\u00b5\3\27\3\27\3\30\3\30\3\31\6\31\u00bd"+
		"\n\31\r\31\16\31\u00be\3\31\3\31\2\2\32\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\3\2\b\3\2aa\4\2C\\c|\7\2))\62;C\\aac|\3\2\62;\4\2))\60"+
		"\60\5\2\13\f\17\17\"\"\u00cc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3"+
		"\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\3\63\3\2\2\2\5=\3\2\2\2\7?\3"+
		"\2\2\2\tF\3\2\2\2\13H\3\2\2\2\rJ\3\2\2\2\17S\3\2\2\2\21U\3\2\2\2\23W\3"+
		"\2\2\2\25`\3\2\2\2\27k\3\2\2\2\31q\3\2\2\2\33}\3\2\2\2\35\u0085\3\2\2"+
		"\2\37\u0095\3\2\2\2!\u0097\3\2\2\2#\u0099\3\2\2\2%\u009b\3\2\2\2\'\u009d"+
		"\3\2\2\2)\u00a0\3\2\2\2+\u00ac\3\2\2\2-\u00b7\3\2\2\2/\u00b9\3\2\2\2\61"+
		"\u00bc\3\2\2\2\63\64\7x\2\2\64\65\7c\2\2\65\66\7t\2\2\66\67\7k\2\2\67"+
		"8\7c\2\289\7d\2\29:\7n\2\2:;\7g\2\2;<\7u\2\2<\4\3\2\2\2=>\7U\2\2>\6\3"+
		"\2\2\2?@\7C\2\2@A\7i\2\2AB\7g\2\2BC\7p\2\2CD\7v\2\2DE\7u\2\2E\b\3\2\2"+
		"\2FG\7Q\2\2G\n\3\2\2\2HI\7C\2\2I\f\3\2\2\2JK\7f\2\2KL\7{\2\2LM\7p\2\2"+
		"MN\7c\2\2NO\7o\2\2OP\7k\2\2PQ\7e\2\2QR\7u\2\2R\16\3\2\2\2ST\7d\2\2T\20"+
		"\3\2\2\2UV\7T\2\2V\22\3\2\2\2WX\7f\2\2XY\7k\2\2YZ\7u\2\2Z[\7e\2\2[\\\7"+
		"q\2\2\\]\7w\2\2]^\7p\2\2^_\7v\2\2_\24\3\2\2\2`a\7U\2\2ab\7C\2\2bc\7O\2"+
		"\2cd\7G\2\2d\26\3\2\2\2ef\7G\2\2fg\7P\2\2gl\7X\2\2hi\7g\2\2ij\7p\2\2j"+
		"l\7x\2\2ke\3\2\2\2kh\3\2\2\2l\30\3\2\2\2mn\7F\2\2nr\7F\2\2op\7f\2\2pr"+
		"\7f\2\2qm\3\2\2\2qo\3\2\2\2r\32\3\2\2\2st\7R\2\2tu\7Q\2\2uv\7O\2\2vw\7"+
		"F\2\2w~\7R\2\2xy\7r\2\2yz\7q\2\2z{\7o\2\2{|\7f\2\2|~\7r\2\2}s\3\2\2\2"+
		"}x\3\2\2\2~\34\3\2\2\2\177\u0080\7F\2\2\u0080\u0081\7D\2\2\u0081\u0086"+
		"\7P\2\2\u0082\u0083\7f\2\2\u0083\u0084\7d\2\2\u0084\u0086\7p\2\2\u0085"+
		"\177\3\2\2\2\u0085\u0082\3\2\2\2\u0086\36\3\2\2\2\u0087\u0088\7w\2\2\u0088"+
		"\u0089\7p\2\2\u0089\u008a\7k\2\2\u008a\u008b\7h\2\2\u008b\u008c\7q\2\2"+
		"\u008c\u008d\7t\2\2\u008d\u0096\7o\2\2\u008e\u008f\7W\2\2\u008f\u0090"+
		"\7P\2\2\u0090\u0091\7K\2\2\u0091\u0092\7H\2\2\u0092\u0093\7Q\2\2\u0093"+
		"\u0094\7T\2\2\u0094\u0096\7O\2\2\u0095\u0087\3\2\2\2\u0095\u008e\3\2\2"+
		"\2\u0096 \3\2\2\2\u0097\u0098\7-\2\2\u0098\"\3\2\2\2\u0099\u009a\7/\2"+
		"\2\u009a$\3\2\2\2\u009b\u009c\7,\2\2\u009c&\3\2\2\2\u009d\u009e\7\61\2"+
		"\2\u009e(\3\2\2\2\u009f\u00a1\t\2\2\2\u00a0\u009f\3\2\2\2\u00a0\u00a1"+
		"\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a6\t\3\2\2\u00a3\u00a5\t\4\2\2\u00a4"+
		"\u00a3\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2"+
		"\2\2\u00a7*\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9\u00ab\t\5\2\2\u00aa\u00a9"+
		"\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad"+
		"\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b1\t\6\2\2\u00b0\u00af\3\2"+
		"\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b3\3\2\2\2\u00b2\u00b4\t\5\2\2\u00b3"+
		"\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b5\u00b6\3\2"+
		"\2\2\u00b6,\3\2\2\2\u00b7\u00b8\7*\2\2\u00b8.\3\2\2\2\u00b9\u00ba\7+\2"+
		"\2\u00ba\60\3\2\2\2\u00bb\u00bd\t\7\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00be"+
		"\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0"+
		"\u00c1\b\31\2\2\u00c1\62\3\2\2\2\16\2kq}\u0085\u0095\u00a0\u00a6\u00ac"+
		"\u00b0\u00b5\u00be\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}