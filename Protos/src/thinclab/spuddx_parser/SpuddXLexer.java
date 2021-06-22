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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, POMDP=7, UNIFORM=8, OP_ADD=9, 
		OP_SUB=10, OP_MUL=11, OP_DIV=12, IDENTIFIER=13, FLOAT_NUM=14, LP=15, RP=16, 
		WS=17;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "POMDP", "UNIFORM", "OP_ADD", 
		"OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'model'", "'actions'", "'dd'", "'SAME'", "'dbn'", 
		"'POMDP'", null, "'+'", "'-'", "'*'", "'/'", null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "POMDP", "UNIFORM", "OP_ADD", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\23\u0088\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\5\t^\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\5"+
		"\16i\n\16\3\16\3\16\7\16m\n\16\f\16\16\16p\13\16\3\17\7\17s\n\17\f\17"+
		"\16\17v\13\17\3\17\3\17\6\17z\n\17\r\17\16\17{\3\20\3\20\3\21\3\21\3\22"+
		"\6\22\u0083\n\22\r\22\16\22\u0084\3\22\3\22\2\2\23\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23\3\2\7"+
		"\3\2aa\4\2C\\c|\7\2))\62;C\\aac|\3\2\62;\5\2\13\f\17\17\"\"\u008d\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2"+
		"\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2"+
		"\3%\3\2\2\2\5/\3\2\2\2\7\65\3\2\2\2\t=\3\2\2\2\13@\3\2\2\2\rE\3\2\2\2"+
		"\17I\3\2\2\2\21]\3\2\2\2\23_\3\2\2\2\25a\3\2\2\2\27c\3\2\2\2\31e\3\2\2"+
		"\2\33h\3\2\2\2\35t\3\2\2\2\37}\3\2\2\2!\177\3\2\2\2#\u0082\3\2\2\2%&\7"+
		"x\2\2&\'\7c\2\2\'(\7t\2\2()\7k\2\2)*\7c\2\2*+\7d\2\2+,\7n\2\2,-\7g\2\2"+
		"-.\7u\2\2.\4\3\2\2\2/\60\7o\2\2\60\61\7q\2\2\61\62\7f\2\2\62\63\7g\2\2"+
		"\63\64\7n\2\2\64\6\3\2\2\2\65\66\7c\2\2\66\67\7e\2\2\678\7v\2\289\7k\2"+
		"\29:\7q\2\2:;\7p\2\2;<\7u\2\2<\b\3\2\2\2=>\7f\2\2>?\7f\2\2?\n\3\2\2\2"+
		"@A\7U\2\2AB\7C\2\2BC\7O\2\2CD\7G\2\2D\f\3\2\2\2EF\7f\2\2FG\7d\2\2GH\7"+
		"p\2\2H\16\3\2\2\2IJ\7R\2\2JK\7Q\2\2KL\7O\2\2LM\7F\2\2MN\7R\2\2N\20\3\2"+
		"\2\2OP\7w\2\2PQ\7p\2\2QR\7k\2\2RS\7h\2\2ST\7q\2\2TU\7t\2\2U^\7o\2\2VW"+
		"\7W\2\2WX\7P\2\2XY\7K\2\2YZ\7H\2\2Z[\7Q\2\2[\\\7T\2\2\\^\7O\2\2]O\3\2"+
		"\2\2]V\3\2\2\2^\22\3\2\2\2_`\7-\2\2`\24\3\2\2\2ab\7/\2\2b\26\3\2\2\2c"+
		"d\7,\2\2d\30\3\2\2\2ef\7\61\2\2f\32\3\2\2\2gi\t\2\2\2hg\3\2\2\2hi\3\2"+
		"\2\2ij\3\2\2\2jn\t\3\2\2km\t\4\2\2lk\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2"+
		"\2\2o\34\3\2\2\2pn\3\2\2\2qs\t\5\2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3"+
		"\2\2\2uw\3\2\2\2vt\3\2\2\2wy\7\60\2\2xz\t\5\2\2yx\3\2\2\2z{\3\2\2\2{y"+
		"\3\2\2\2{|\3\2\2\2|\36\3\2\2\2}~\7*\2\2~ \3\2\2\2\177\u0080\7+\2\2\u0080"+
		"\"\3\2\2\2\u0081\u0083\t\6\2\2\u0082\u0081\3\2\2\2\u0083\u0084\3\2\2\2"+
		"\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087"+
		"\b\22\2\2\u0087$\3\2\2\2\t\2]hnt{\u0084\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}