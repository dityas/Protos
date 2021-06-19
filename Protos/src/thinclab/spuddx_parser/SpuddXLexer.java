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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, OP_ADD=8, OP_SUB=9, 
		OP_MUL=10, OP_DIV=11, IDENTIFIER=12, FLOAT_NUM=13, LP=14, RP=15, WS=16;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "OP_ADD", "OP_SUB", 
		"OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'observations'", "'actions'", "'dd'", "'SAME'", 
		"'env'", "'actiondbn'", "'+'", "'-'", "'*'", "'/'", null, null, "'('", 
		"')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "OP_ADD", "OP_SUB", "OP_MUL", 
		"OP_DIV", "IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\22\u0081\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\5\rb\n\r\3\r\3\r\7\rf\n\r\f\r\16\r"+
		"i\13\r\3\16\7\16l\n\16\f\16\16\16o\13\16\3\16\3\16\6\16s\n\16\r\16\16"+
		"\16t\3\17\3\17\3\20\3\20\3\21\6\21|\n\21\r\21\16\21}\3\21\3\21\2\2\22"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22\3\2\7\3\2aa\4\2C\\c|\7\2))\62;C\\aac|\3\2\62;\5\2\13\f\17\17"+
		"\"\"\u0085\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\3#\3\2\2\2\5-\3\2\2\2\7:\3\2\2\2\tB\3\2\2\2\13E\3\2\2\2\rJ\3\2\2\2"+
		"\17N\3\2\2\2\21X\3\2\2\2\23Z\3\2\2\2\25\\\3\2\2\2\27^\3\2\2\2\31a\3\2"+
		"\2\2\33m\3\2\2\2\35v\3\2\2\2\37x\3\2\2\2!{\3\2\2\2#$\7x\2\2$%\7c\2\2%"+
		"&\7t\2\2&\'\7k\2\2\'(\7c\2\2()\7d\2\2)*\7n\2\2*+\7g\2\2+,\7u\2\2,\4\3"+
		"\2\2\2-.\7q\2\2./\7d\2\2/\60\7u\2\2\60\61\7g\2\2\61\62\7t\2\2\62\63\7"+
		"x\2\2\63\64\7c\2\2\64\65\7v\2\2\65\66\7k\2\2\66\67\7q\2\2\678\7p\2\28"+
		"9\7u\2\29\6\3\2\2\2:;\7c\2\2;<\7e\2\2<=\7v\2\2=>\7k\2\2>?\7q\2\2?@\7p"+
		"\2\2@A\7u\2\2A\b\3\2\2\2BC\7f\2\2CD\7f\2\2D\n\3\2\2\2EF\7U\2\2FG\7C\2"+
		"\2GH\7O\2\2HI\7G\2\2I\f\3\2\2\2JK\7g\2\2KL\7p\2\2LM\7x\2\2M\16\3\2\2\2"+
		"NO\7c\2\2OP\7e\2\2PQ\7v\2\2QR\7k\2\2RS\7q\2\2ST\7p\2\2TU\7f\2\2UV\7d\2"+
		"\2VW\7p\2\2W\20\3\2\2\2XY\7-\2\2Y\22\3\2\2\2Z[\7/\2\2[\24\3\2\2\2\\]\7"+
		",\2\2]\26\3\2\2\2^_\7\61\2\2_\30\3\2\2\2`b\t\2\2\2a`\3\2\2\2ab\3\2\2\2"+
		"bc\3\2\2\2cg\t\3\2\2df\t\4\2\2ed\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2"+
		"h\32\3\2\2\2ig\3\2\2\2jl\t\5\2\2kj\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2"+
		"\2np\3\2\2\2om\3\2\2\2pr\7\60\2\2qs\t\5\2\2rq\3\2\2\2st\3\2\2\2tr\3\2"+
		"\2\2tu\3\2\2\2u\34\3\2\2\2vw\7*\2\2w\36\3\2\2\2xy\7+\2\2y \3\2\2\2z|\t"+
		"\6\2\2{z\3\2\2\2|}\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080"+
		"\b\21\2\2\u0080\"\3\2\2\2\b\2agmt}\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}