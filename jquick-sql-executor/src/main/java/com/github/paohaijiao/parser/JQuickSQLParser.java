// Generated from D:/idea/jthornruleGrammer/sql/JQuickSQL.g4 by ANTLR 4.13.2
package com.github.paohaijiao.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class JQuickSQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		ON=25, LIKE=26, IS=27, NULL=28, EXISTS=29, DESC=30, ASC=31, SELECT=32, 
		FROM=33, WHERE=34, GROUP=35, BY=36, HAVING=37, ORDER=38, LIMIT=39, OFFSET=40, 
		AND=41, OR=42, XOR=43, NOT=44, IN=45, BETWEEN=46, AS=47, WITH=48, RECURSIVE=49, 
		ROLLUP=50, DRILLDOWN=51, SLICE=52, DICE=53, PIVOT=54, JOIN=55, INNER=56, 
		OUTER=57, LEFT=58, RIGHT=59, FULL=60, CROSS=61, NATURAL=62, USE=63, FORCE=64, 
		IGNORE=65, INDEX=66, KEY=67, FOR=68, UNION=69, ALL=70, DISTINCT=71, DISTINCTROW=72, 
		HIGH_PRIORITY=73, STRAIGHT_JOIN=74, SQL_SMALL_RESULT=75, SQL_BIG_RESULT=76, 
		SQL_BUFFER_RESULT=77, SQL_CACHE=78, SQL_NO_CACHE=79, SQL_CALC_FOUND_ROWS=80, 
		TRUE=81, FALSE=82, AVG=83, MAX=84, MIN=85, SUM=86, COUNT=87, GROUP_CONCAT=88, 
		ABS=89, ACOS=90, ADDDATE=91, ADDTIME=92, AES_DECRYPT=93, AES_ENCRYPT=94, 
		ASCII=95, ASIN=96, ATAN=97, ATAN2=98, BENCHMARK=99, BIN=100, BIT_COUNT=101, 
		BIT_LENGTH=102, CEIL=103, CEILING=104, CHAR=105, CHAR_LENGTH=106, CHARACTER_LENGTH=107, 
		COALESCE=108, COS=109, COT=110, CRC32=111, CURDATE=112, CURRENT_DATE=113, 
		CURRENT_TIME=114, CURRENT_TIMESTAMP=115, CURRENT_USER=116, CURTIME=117, 
		DATABASE=118, DATE=119, DATEDIFF=120, DATE_ADD=121, DATE_FORMAT=122, DATE_SUB=123, 
		DAY=124, DAYNAME=125, DAYOFMONTH=126, DAYOFWEEK=127, DAYOFYEAR=128, DECODE=129, 
		DEFAULT=130, DEGREES=131, DES_DECRYPT=132, DES_ENCRYPT=133, ELT=134, ENCODE=135, 
		ENCRYPT=136, EXP=137, EXTRACT=138, FIELD=139, FIND_IN_SET=140, FLOOR=141, 
		FORMAT=142, FOUND_ROWS=143, FROM_DAYS=144, FROM_UNIXTIME=145, GET_FORMAT=146, 
		GET_LOCK=147, GREATEST=148, HEX=149, HOUR=150, IF=151, IFNULL=152, INET_ATON=153, 
		INET_NTOA=154, INSERT=155, INSTR=156, IS_FREE_LOCK=157, IS_USED_LOCK=158, 
		LAST_INSERT_ID=159, LCASE=160, LEAST=161, LENGTH=162, LN=163, LOAD_FILE=164, 
		LOCALTIME=165, LOCALTIMESTAMP=166, LOCATE=167, LOG=168, LOG10=169, LOG2=170, 
		LOWER=171, LPAD=172, LTRIM=173, MAKEDATE=174, MAKETIME=175, MAKE_SET=176, 
		MASTER_POS_WAIT=177, MATCH=178, MD5=179, MICROSECOND=180, MID=181, MINUTE=182, 
		MOD=183, MONTH=184, MONTHNAME=185, NOW=186, NULLIF=187, OCT=188, OCTET_LENGTH=189, 
		ORD=190, PASSWORD=191, PERIOD_ADD=192, PERIOD_DIFF=193, PI=194, POSITION=195, 
		POW=196, POWER=197, QUARTER=198, QUOTE=199, RADIANS=200, RAND=201, RELEASE_LOCK=202, 
		REPEAT=203, REPLACE=204, REVERSE=205, ROUND=206, ROW_COUNT=207, RPAD=208, 
		RTRIM=209, SCHEMA=210, SEC_TO_TIME=211, SECOND=212, SHA=213, SHA1=214, 
		SHA2=215, SIGN=216, SIN=217, SLEEP=218, SOUNDEX=219, SPACE=220, SQRT=221, 
		STR_TO_DATE=222, STRCMP=223, SUBDATE=224, SUBSTR=225, SUBSTRING=226, SUBSTRING_INDEX=227, 
		SUCSTRING=228, SYSDATE=229, TAN=230, TIME=231, TIMEDIFF=232, TIMESTAMP=233, 
		TIMESTAMPADD=234, TIMESTAMPDIFF=235, TIME_FORMAT=236, TIME_TO_SEC=237, 
		TO_DAYS=238, TRIM=239, TRUNCATE=240, UCASE=241, UNCOMPRESS=242, UNCOMPRESSED_LENGTH=243, 
		UNHEX=244, UNIX_TIMESTAMP=245, UPPER=246, USER=247, UTC_DATE=248, UTC_TIME=249, 
		UTC_TIMESTAMP=250, UUID=251, UUID_SHORT=252, VALIDATE_PASSWORD_STRENGTH=253, 
		VERSION=254, WEEK=255, WEEKDAY=256, WEEKOFYEAR=257, YEAR=258, YEARWEEK=259, 
		ID_LITERAL=260, STRING_LITERAL=261, DECIMAL_LITERAL=262, HEXADECIMAL_LITERAL=263, 
		BIT_STRING=264, WS=265, COMMENT=266, LINE_COMMENT=267, STD=268, STDDEV=269, 
		LOCAL_ID=270, VAR_ASSIGN=271, REGEXP=272, RLIKE=273, ESCAPE=274;
	public static final int
		RULE_query = 0, RULE_selectStatement = 1, RULE_selectExpression = 2, RULE_olapOperation = 3, 
		RULE_rollUp = 4, RULE_drillDown = 5, RULE_drillDownDimensions = 6, RULE_slice = 7, 
		RULE_sliceCondition = 8, RULE_dice = 9, RULE_diceConditions = 10, RULE_diceCondition = 11, 
		RULE_pivot = 12, RULE_pivotAggregate = 13, RULE_pivotColumn = 14, RULE_pivotValues = 15, 
		RULE_selectClause = 16, RULE_selectSpec = 17, RULE_selectElements = 18, 
		RULE_selectElement = 19, RULE_fromClause = 20, RULE_tableSources = 21, 
		RULE_tableSource = 22, RULE_tableSourceItem = 23, RULE_joinPart = 24, 
		RULE_joinType = 25, RULE_uidList = 26, RULE_groupByItem = 27, RULE_havingExpr = 28, 
		RULE_orderByClause = 29, RULE_orderByExpression = 30, RULE_limitClause = 31, 
		RULE_limitOnly = 32, RULE_limitWithOffset = 33, RULE_commonTableExpression = 34, 
		RULE_columnNames = 35, RULE_initialQuery = 36, RULE_recursivePart = 37, 
		RULE_expressions = 38, RULE_functionCall = 39, RULE_compOperator = 40, 
		RULE_aggregateFunction = 41, RULE_functionArgs = 42, RULE_functionArg = 43, 
		RULE_predicate = 44, RULE_expressionAtom = 45, RULE_expression = 46, RULE_mathOperator = 47, 
		RULE_unaryOperator = 48, RULE_logicalOperator = 49, RULE_comparisonOperator = 50, 
		RULE_constant = 51, RULE_decimal_literal = 52, RULE_null_literal = 53, 
		RULE_dateLiteral = 54, RULE_format = 55, RULE_booleanLiteral = 56, RULE_fullColumnName = 57, 
		RULE_tableName = 58, RULE_schemaName = 59, RULE_dottedId = 60, RULE_uid = 61, 
		RULE_stringLiteral = 62, RULE_simpleId = 63, RULE_keyword = 64;
	private static String[] makeRuleNames() {
		return new String[] {
			"query", "selectStatement", "selectExpression", "olapOperation", "rollUp", 
			"drillDown", "drillDownDimensions", "slice", "sliceCondition", "dice", 
			"diceConditions", "diceCondition", "pivot", "pivotAggregate", "pivotColumn", 
			"pivotValues", "selectClause", "selectSpec", "selectElements", "selectElement", 
			"fromClause", "tableSources", "tableSource", "tableSourceItem", "joinPart", 
			"joinType", "uidList", "groupByItem", "havingExpr", "orderByClause", 
			"orderByExpression", "limitClause", "limitOnly", "limitWithOffset", "commonTableExpression", 
			"columnNames", "initialQuery", "recursivePart", "expressions", "functionCall", 
			"compOperator", "aggregateFunction", "functionArgs", "functionArg", "predicate", 
			"expressionAtom", "expression", "mathOperator", "unaryOperator", "logicalOperator", 
			"comparisonOperator", "constant", "decimal_literal", "null_literal", 
			"dateLiteral", "format", "booleanLiteral", "fullColumnName", "tableName", 
			"schemaName", "dottedId", "uid", "stringLiteral", "simpleId", "keyword"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'('", "')'", "'='", "'*'", "'>'", "'<'", "'<='", 
			"'>='", "'<>'", "'!='", "'<=>'", "'!'", "'/'", "'%'", "'+'", "'-'", "'--'", 
			"'~'", "'&'", "'|'", "'::'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "ON", "LIKE", "IS", "NULL", "EXISTS", "DESC", "ASC", "SELECT", 
			"FROM", "WHERE", "GROUP", "BY", "HAVING", "ORDER", "LIMIT", "OFFSET", 
			"AND", "OR", "XOR", "NOT", "IN", "BETWEEN", "AS", "WITH", "RECURSIVE", 
			"ROLLUP", "DRILLDOWN", "SLICE", "DICE", "PIVOT", "JOIN", "INNER", "OUTER", 
			"LEFT", "RIGHT", "FULL", "CROSS", "NATURAL", "USE", "FORCE", "IGNORE", 
			"INDEX", "KEY", "FOR", "UNION", "ALL", "DISTINCT", "DISTINCTROW", "HIGH_PRIORITY", 
			"STRAIGHT_JOIN", "SQL_SMALL_RESULT", "SQL_BIG_RESULT", "SQL_BUFFER_RESULT", 
			"SQL_CACHE", "SQL_NO_CACHE", "SQL_CALC_FOUND_ROWS", "TRUE", "FALSE", 
			"AVG", "MAX", "MIN", "SUM", "COUNT", "GROUP_CONCAT", "ABS", "ACOS", "ADDDATE", 
			"ADDTIME", "AES_DECRYPT", "AES_ENCRYPT", "ASCII", "ASIN", "ATAN", "ATAN2", 
			"BENCHMARK", "BIN", "BIT_COUNT", "BIT_LENGTH", "CEIL", "CEILING", "CHAR", 
			"CHAR_LENGTH", "CHARACTER_LENGTH", "COALESCE", "COS", "COT", "CRC32", 
			"CURDATE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", 
			"CURTIME", "DATABASE", "DATE", "DATEDIFF", "DATE_ADD", "DATE_FORMAT", 
			"DATE_SUB", "DAY", "DAYNAME", "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", 
			"DECODE", "DEFAULT", "DEGREES", "DES_DECRYPT", "DES_ENCRYPT", "ELT", 
			"ENCODE", "ENCRYPT", "EXP", "EXTRACT", "FIELD", "FIND_IN_SET", "FLOOR", 
			"FORMAT", "FOUND_ROWS", "FROM_DAYS", "FROM_UNIXTIME", "GET_FORMAT", "GET_LOCK", 
			"GREATEST", "HEX", "HOUR", "IF", "IFNULL", "INET_ATON", "INET_NTOA", 
			"INSERT", "INSTR", "IS_FREE_LOCK", "IS_USED_LOCK", "LAST_INSERT_ID", 
			"LCASE", "LEAST", "LENGTH", "LN", "LOAD_FILE", "LOCALTIME", "LOCALTIMESTAMP", 
			"LOCATE", "LOG", "LOG10", "LOG2", "LOWER", "LPAD", "LTRIM", "MAKEDATE", 
			"MAKETIME", "MAKE_SET", "MASTER_POS_WAIT", "MATCH", "MD5", "MICROSECOND", 
			"MID", "MINUTE", "MOD", "MONTH", "MONTHNAME", "NOW", "NULLIF", "OCT", 
			"OCTET_LENGTH", "ORD", "PASSWORD", "PERIOD_ADD", "PERIOD_DIFF", "PI", 
			"POSITION", "POW", "POWER", "QUARTER", "QUOTE", "RADIANS", "RAND", "RELEASE_LOCK", 
			"REPEAT", "REPLACE", "REVERSE", "ROUND", "ROW_COUNT", "RPAD", "RTRIM", 
			"SCHEMA", "SEC_TO_TIME", "SECOND", "SHA", "SHA1", "SHA2", "SIGN", "SIN", 
			"SLEEP", "SOUNDEX", "SPACE", "SQRT", "STR_TO_DATE", "STRCMP", "SUBDATE", 
			"SUBSTR", "SUBSTRING", "SUBSTRING_INDEX", "SUCSTRING", "SYSDATE", "TAN", 
			"TIME", "TIMEDIFF", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TIME_FORMAT", 
			"TIME_TO_SEC", "TO_DAYS", "TRIM", "TRUNCATE", "UCASE", "UNCOMPRESS", 
			"UNCOMPRESSED_LENGTH", "UNHEX", "UNIX_TIMESTAMP", "UPPER", "USER", "UTC_DATE", 
			"UTC_TIME", "UTC_TIMESTAMP", "UUID", "UUID_SHORT", "VALIDATE_PASSWORD_STRENGTH", 
			"VERSION", "WEEK", "WEEKDAY", "WEEKOFYEAR", "YEAR", "YEARWEEK", "ID_LITERAL", 
			"STRING_LITERAL", "DECIMAL_LITERAL", "HEXADECIMAL_LITERAL", "BIT_STRING", 
			"WS", "COMMENT", "LINE_COMMENT", "STD", "STDDEV", "LOCAL_ID", "VAR_ASSIGN", 
			"REGEXP", "RLIKE", "ESCAPE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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

	@Override
	public String getGrammarFileName() { return "JQuickSQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JQuickSQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QueryContext extends ParserRuleContext {
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public TerminalNode EOF() { return getToken(JQuickSQLParser.EOF, 0); }
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			selectStatement();
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(131);
				match(T__0);
				}
			}

			setState(134);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectStatementContext extends ParserRuleContext {
		public SelectStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStatement; }
	 
		public SelectStatementContext() { }
		public void copyFrom(SelectStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleQueryContext extends SelectStatementContext {
		public SelectExpressionContext selectExpression() {
			return getRuleContext(SelectExpressionContext.class,0);
		}
		public SingleQueryContext(SelectStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSingleQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSingleQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSingleQuery(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CteQueryContext extends SelectStatementContext {
		public CommonTableExpressionContext cte;
		public TerminalNode WITH() { return getToken(JQuickSQLParser.WITH, 0); }
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public SelectExpressionContext selectExpression() {
			return getRuleContext(SelectExpressionContext.class,0);
		}
		public List<CommonTableExpressionContext> commonTableExpression() {
			return getRuleContexts(CommonTableExpressionContext.class);
		}
		public CommonTableExpressionContext commonTableExpression(int i) {
			return getRuleContext(CommonTableExpressionContext.class,i);
		}
		public TerminalNode RECURSIVE() { return getToken(JQuickSQLParser.RECURSIVE, 0); }
		public CteQueryContext(SelectStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterCteQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitCteQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitCteQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectStatementContext selectStatement() throws RecognitionException {
		SelectStatementContext _localctx = new SelectStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectStatement);
		int _la;
		try {
			setState(152);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				_localctx = new CteQueryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(136);
				match(WITH);
				setState(138);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(137);
					match(RECURSIVE);
					}
					break;
				}
				setState(140);
				uid();
				setState(141);
				((CteQueryContext)_localctx).cte = commonTableExpression();
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(142);
					match(T__1);
					setState(143);
					commonTableExpression();
					}
					}
					setState(148);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(149);
				selectExpression();
				}
				break;
			case SELECT:
				_localctx = new SingleQueryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				selectExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectExpressionContext extends ParserRuleContext {
		public OlapOperationContext olapOperation() {
			return getRuleContext(OlapOperationContext.class,0);
		}
		public SelectClauseContext selectClause() {
			return getRuleContext(SelectClauseContext.class,0);
		}
		public SelectExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectExpressionContext selectExpression() throws RecognitionException {
		SelectExpressionContext _localctx = new SelectExpressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_selectExpression);
		try {
			setState(156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				olapOperation();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
				selectClause();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OlapOperationContext extends ParserRuleContext {
		public RollUpContext rollUp() {
			return getRuleContext(RollUpContext.class,0);
		}
		public DrillDownContext drillDown() {
			return getRuleContext(DrillDownContext.class,0);
		}
		public SliceContext slice() {
			return getRuleContext(SliceContext.class,0);
		}
		public DiceContext dice() {
			return getRuleContext(DiceContext.class,0);
		}
		public PivotContext pivot() {
			return getRuleContext(PivotContext.class,0);
		}
		public OlapOperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_olapOperation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterOlapOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitOlapOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitOlapOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OlapOperationContext olapOperation() throws RecognitionException {
		OlapOperationContext _localctx = new OlapOperationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_olapOperation);
		try {
			setState(163);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(158);
				rollUp();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(159);
				drillDown();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(160);
				slice();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(161);
				dice();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(162);
				pivot();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RollUpContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public TerminalNode WITH() { return getToken(JQuickSQLParser.WITH, 0); }
		public TerminalNode ROLLUP() { return getToken(JQuickSQLParser.ROLLUP, 0); }
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public RollUpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rollUp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterRollUp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitRollUp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitRollUp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RollUpContext rollUp() throws RecognitionException {
		RollUpContext _localctx = new RollUpContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_rollUp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			match(SELECT);
			setState(166);
			selectElements();
			setState(167);
			match(FROM);
			setState(168);
			tableSources();
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(169);
				match(WHERE);
				setState(170);
				expression(0);
				}
			}

			setState(173);
			match(GROUP);
			setState(174);
			match(BY);
			setState(175);
			groupByItem();
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(176);
				match(T__1);
				setState(177);
				groupByItem();
				}
				}
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(183);
			match(WITH);
			setState(184);
			match(ROLLUP);
			setState(187);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(185);
				match(HAVING);
				setState(186);
				expression(0);
				}
				break;
			}
			setState(190);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(189);
				orderByClause();
				}
				break;
			}
			setState(193);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(192);
				limitClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DrillDownContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public TerminalNode WITH() { return getToken(JQuickSQLParser.WITH, 0); }
		public TerminalNode DRILLDOWN() { return getToken(JQuickSQLParser.DRILLDOWN, 0); }
		public DrillDownDimensionsContext drillDownDimensions() {
			return getRuleContext(DrillDownDimensionsContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public DrillDownContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drillDown; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDrillDown(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDrillDown(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDrillDown(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DrillDownContext drillDown() throws RecognitionException {
		DrillDownContext _localctx = new DrillDownContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_drillDown);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(SELECT);
			setState(196);
			selectElements();
			setState(197);
			match(FROM);
			setState(198);
			tableSources();
			setState(201);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(199);
				match(WHERE);
				setState(200);
				expression(0);
				}
			}

			setState(203);
			match(GROUP);
			setState(204);
			match(BY);
			setState(205);
			groupByItem();
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(206);
				match(T__1);
				setState(207);
				groupByItem();
				}
				}
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(213);
			match(WITH);
			setState(214);
			match(DRILLDOWN);
			setState(215);
			match(T__2);
			setState(216);
			drillDownDimensions();
			setState(217);
			match(T__3);
			setState(220);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(218);
				match(HAVING);
				setState(219);
				expression(0);
				}
				break;
			}
			setState(223);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(222);
				orderByClause();
				}
				break;
			}
			setState(226);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(225);
				limitClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DrillDownDimensionsContext extends ParserRuleContext {
		public List<UidContext> uid() {
			return getRuleContexts(UidContext.class);
		}
		public UidContext uid(int i) {
			return getRuleContext(UidContext.class,i);
		}
		public DrillDownDimensionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drillDownDimensions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDrillDownDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDrillDownDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDrillDownDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DrillDownDimensionsContext drillDownDimensions() throws RecognitionException {
		DrillDownDimensionsContext _localctx = new DrillDownDimensionsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_drillDownDimensions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			uid();
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(229);
				match(T__1);
				setState(230);
				uid();
				}
				}
				setState(235);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SliceContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public TerminalNode SLICE() { return getToken(JQuickSQLParser.SLICE, 0); }
		public SliceConditionContext sliceCondition() {
			return getRuleContext(SliceConditionContext.class,0);
		}
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public SliceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSlice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSlice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSlice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SliceContext slice() throws RecognitionException {
		SliceContext _localctx = new SliceContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_slice);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			match(SELECT);
			setState(237);
			selectElements();
			setState(238);
			match(FROM);
			setState(239);
			tableSources();
			setState(240);
			match(WHERE);
			setState(241);
			match(SLICE);
			setState(242);
			match(T__2);
			setState(243);
			sliceCondition();
			setState(244);
			match(T__3);
			setState(255);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(245);
				match(GROUP);
				setState(246);
				match(BY);
				setState(247);
				groupByItem();
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(248);
					match(T__1);
					setState(249);
					groupByItem();
					}
					}
					setState(254);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(259);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(257);
				match(HAVING);
				setState(258);
				expression(0);
				}
				break;
			}
			setState(262);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(261);
				orderByClause();
				}
				break;
			}
			setState(265);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(264);
				limitClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SliceConditionContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public SliceConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sliceCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSliceCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSliceCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSliceCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SliceConditionContext sliceCondition() throws RecognitionException {
		SliceConditionContext _localctx = new SliceConditionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_sliceCondition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			uid();
			setState(268);
			match(T__4);
			setState(269);
			constant();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiceContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public TerminalNode DICE() { return getToken(JQuickSQLParser.DICE, 0); }
		public DiceConditionsContext diceConditions() {
			return getRuleContext(DiceConditionsContext.class,0);
		}
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public DiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_dice);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			match(SELECT);
			setState(272);
			selectElements();
			setState(273);
			match(FROM);
			setState(274);
			tableSources();
			setState(275);
			match(WHERE);
			setState(276);
			match(DICE);
			setState(277);
			match(T__2);
			setState(278);
			diceConditions();
			setState(279);
			match(T__3);
			setState(290);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(280);
				match(GROUP);
				setState(281);
				match(BY);
				setState(282);
				groupByItem();
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(283);
					match(T__1);
					setState(284);
					groupByItem();
					}
					}
					setState(289);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(292);
				match(HAVING);
				setState(293);
				expression(0);
				}
				break;
			}
			setState(297);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(296);
				orderByClause();
				}
				break;
			}
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(299);
				limitClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiceConditionsContext extends ParserRuleContext {
		public List<DiceConditionContext> diceCondition() {
			return getRuleContexts(DiceConditionContext.class);
		}
		public DiceConditionContext diceCondition(int i) {
			return getRuleContext(DiceConditionContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(JQuickSQLParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(JQuickSQLParser.AND, i);
		}
		public DiceConditionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_diceConditions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDiceConditions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDiceConditions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDiceConditions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceConditionsContext diceConditions() throws RecognitionException {
		DiceConditionsContext _localctx = new DiceConditionsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_diceConditions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			diceCondition();
			setState(307);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(303);
				match(AND);
				setState(304);
				diceCondition();
				}
				}
				setState(309);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiceConditionContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public CompOperatorContext compOperator() {
			return getRuleContext(CompOperatorContext.class,0);
		}
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public DiceConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_diceCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDiceCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDiceCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDiceCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceConditionContext diceCondition() throws RecognitionException {
		DiceConditionContext _localctx = new DiceConditionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_diceCondition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			uid();
			setState(311);
			compOperator();
			setState(312);
			constant();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PivotContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public TerminalNode PIVOT() { return getToken(JQuickSQLParser.PIVOT, 0); }
		public PivotAggregateContext pivotAggregate() {
			return getRuleContext(PivotAggregateContext.class,0);
		}
		public TerminalNode FOR() { return getToken(JQuickSQLParser.FOR, 0); }
		public PivotColumnContext pivotColumn() {
			return getRuleContext(PivotColumnContext.class,0);
		}
		public TerminalNode IN() { return getToken(JQuickSQLParser.IN, 0); }
		public PivotValuesContext pivotValues() {
			return getRuleContext(PivotValuesContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public PivotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPivot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPivot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPivot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PivotContext pivot() throws RecognitionException {
		PivotContext _localctx = new PivotContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_pivot);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(SELECT);
			setState(315);
			selectElements();
			setState(316);
			match(FROM);
			setState(317);
			tableSources();
			setState(318);
			match(PIVOT);
			setState(319);
			match(T__2);
			setState(320);
			pivotAggregate();
			setState(321);
			match(FOR);
			setState(322);
			pivotColumn();
			setState(323);
			match(IN);
			setState(324);
			match(T__2);
			setState(325);
			pivotValues();
			setState(326);
			match(T__3);
			setState(327);
			match(T__3);
			setState(330);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(328);
				match(WHERE);
				setState(329);
				expression(0);
				}
				break;
			}
			setState(342);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(332);
				match(GROUP);
				setState(333);
				match(BY);
				setState(334);
				groupByItem();
				setState(339);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(335);
					match(T__1);
					setState(336);
					groupByItem();
					}
					}
					setState(341);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(346);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				setState(344);
				match(HAVING);
				setState(345);
				expression(0);
				}
				break;
			}
			setState(349);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(348);
				orderByClause();
				}
				break;
			}
			setState(352);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(351);
				limitClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PivotAggregateContext extends ParserRuleContext {
		public AggregateFunctionContext aggregateFunction() {
			return getRuleContext(AggregateFunctionContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PivotAggregateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivotAggregate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPivotAggregate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPivotAggregate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPivotAggregate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PivotAggregateContext pivotAggregate() throws RecognitionException {
		PivotAggregateContext _localctx = new PivotAggregateContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_pivotAggregate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			aggregateFunction();
			setState(355);
			match(T__2);
			setState(356);
			expression(0);
			setState(357);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PivotColumnContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public PivotColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivotColumn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPivotColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPivotColumn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPivotColumn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PivotColumnContext pivotColumn() throws RecognitionException {
		PivotColumnContext _localctx = new PivotColumnContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pivotColumn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(359);
			uid();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PivotValuesContext extends ParserRuleContext {
		public List<ConstantContext> constant() {
			return getRuleContexts(ConstantContext.class);
		}
		public ConstantContext constant(int i) {
			return getRuleContext(ConstantContext.class,i);
		}
		public PivotValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivotValues; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPivotValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPivotValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPivotValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PivotValuesContext pivotValues() throws RecognitionException {
		PivotValuesContext _localctx = new PivotValuesContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_pivotValues);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(361);
			constant();
			setState(366);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(362);
				match(T__1);
				setState(363);
				constant();
				}
				}
				setState(368);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectClauseContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public SelectSpecContext selectSpec() {
			return getRuleContext(SelectSpecContext.class,0);
		}
		public FromClauseContext fromClause() {
			return getRuleContext(FromClauseContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public SelectClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectClauseContext selectClause() throws RecognitionException {
		SelectClauseContext _localctx = new SelectClauseContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_selectClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			match(SELECT);
			setState(371);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				{
				setState(370);
				selectSpec();
				}
				break;
			}
			setState(373);
			selectElements();
			setState(375);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(374);
				fromClause();
				}
				break;
			}
			setState(379);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(377);
				match(WHERE);
				setState(378);
				expression(0);
				}
				break;
			}
			setState(391);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(381);
				match(GROUP);
				setState(382);
				match(BY);
				setState(383);
				groupByItem();
				setState(388);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(384);
						match(T__1);
						setState(385);
						groupByItem();
						}
						} 
					}
					setState(390);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
				}
				}
				break;
			}
			setState(395);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(393);
				match(HAVING);
				setState(394);
				expression(0);
				}
				break;
			}
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(397);
				orderByClause();
				}
				break;
			}
			setState(401);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(400);
				limitClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectSpecContext extends ParserRuleContext {
		public TerminalNode ALL() { return getToken(JQuickSQLParser.ALL, 0); }
		public TerminalNode DISTINCT() { return getToken(JQuickSQLParser.DISTINCT, 0); }
		public TerminalNode DISTINCTROW() { return getToken(JQuickSQLParser.DISTINCTROW, 0); }
		public SelectSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectSpecContext selectSpec() throws RecognitionException {
		SelectSpecContext _localctx = new SelectSpecContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_selectSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
			_la = _input.LA(1);
			if ( !(((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & 7L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectElementsContext extends ParserRuleContext {
		public Token star;
		public List<SelectElementContext> selectElement() {
			return getRuleContexts(SelectElementContext.class);
		}
		public SelectElementContext selectElement(int i) {
			return getRuleContext(SelectElementContext.class,i);
		}
		public SelectElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectElements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectElements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementsContext selectElements() throws RecognitionException {
		SelectElementsContext _localctx = new SelectElementsContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_selectElements);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
				{
				setState(405);
				((SelectElementsContext)_localctx).star = match(T__5);
				}
				break;
			case T__2:
			case T__13:
			case T__16:
			case T__17:
			case T__19:
			case NULL:
			case EXISTS:
			case SELECT:
			case FROM:
			case WHERE:
			case GROUP:
			case BY:
			case HAVING:
			case ORDER:
			case LIMIT:
			case OFFSET:
			case AND:
			case OR:
			case XOR:
			case NOT:
			case AS:
			case WITH:
			case RECURSIVE:
			case ROLLUP:
			case DRILLDOWN:
			case SLICE:
			case DICE:
			case PIVOT:
			case JOIN:
			case INNER:
			case OUTER:
			case LEFT:
			case RIGHT:
			case FULL:
			case CROSS:
			case NATURAL:
			case USE:
			case FORCE:
			case IGNORE:
			case INDEX:
			case KEY:
			case FOR:
			case ALL:
			case DISTINCT:
			case DISTINCTROW:
			case HIGH_PRIORITY:
			case STRAIGHT_JOIN:
			case SQL_SMALL_RESULT:
			case SQL_BIG_RESULT:
			case SQL_BUFFER_RESULT:
			case SQL_CACHE:
			case SQL_NO_CACHE:
			case SQL_CALC_FOUND_ROWS:
			case TRUE:
			case FALSE:
			case ID_LITERAL:
			case STRING_LITERAL:
			case DECIMAL_LITERAL:
			case LOCAL_ID:
				{
				setState(406);
				selectElement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(413);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(409);
					match(T__1);
					setState(410);
					selectElement();
					}
					} 
				}
				setState(415);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectElementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public TerminalNode AS() { return getToken(JQuickSQLParser.AS, 0); }
		public SelectElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementContext selectElement() throws RecognitionException {
		SelectElementContext _localctx = new SelectElementContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_selectElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(416);
			expression(0);
			setState(421);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(418);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(417);
					match(AS);
					}
					break;
				}
				setState(420);
				uid();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FromClauseContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public FromClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fromClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFromClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFromClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFromClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FromClauseContext fromClause() throws RecognitionException {
		FromClauseContext _localctx = new FromClauseContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_fromClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			match(FROM);
			setState(424);
			tableSources();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TableSourcesContext extends ParserRuleContext {
		public List<TableSourceContext> tableSource() {
			return getRuleContexts(TableSourceContext.class);
		}
		public TableSourceContext tableSource(int i) {
			return getRuleContext(TableSourceContext.class,i);
		}
		public TableSourcesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableSources; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterTableSources(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitTableSources(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitTableSources(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableSourcesContext tableSources() throws RecognitionException {
		TableSourcesContext _localctx = new TableSourcesContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_tableSources);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(426);
			tableSource();
			setState(431);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(427);
					match(T__1);
					setState(428);
					tableSource();
					}
					} 
				}
				setState(433);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TableSourceContext extends ParserRuleContext {
		public TableSourceItemContext tableSourceItem() {
			return getRuleContext(TableSourceItemContext.class,0);
		}
		public List<JoinPartContext> joinPart() {
			return getRuleContexts(JoinPartContext.class);
		}
		public JoinPartContext joinPart(int i) {
			return getRuleContext(JoinPartContext.class,i);
		}
		public TableSourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableSource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterTableSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitTableSource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitTableSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableSourceContext tableSource() throws RecognitionException {
		TableSourceContext _localctx = new TableSourceContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_tableSource);
		int _la;
		try {
			int _alt;
			setState(451);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(434);
				tableSourceItem();
				setState(438);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(435);
						joinPart();
						}
						} 
					}
					setState(440);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(441);
				match(T__2);
				setState(442);
				tableSourceItem();
				setState(446);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 9007199254740992000L) != 0)) {
					{
					{
					setState(443);
					joinPart();
					}
					}
					setState(448);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(449);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TableSourceItemContext extends ParserRuleContext {
		public TableNameContext tableName() {
			return getRuleContext(TableNameContext.class,0);
		}
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public TerminalNode AS() { return getToken(JQuickSQLParser.AS, 0); }
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public TableSourcesContext tableSources() {
			return getRuleContext(TableSourcesContext.class,0);
		}
		public TableSourceItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableSourceItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterTableSourceItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitTableSourceItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitTableSourceItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableSourceItemContext tableSourceItem() throws RecognitionException {
		TableSourceItemContext _localctx = new TableSourceItemContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_tableSourceItem);
		try {
			setState(476);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(453);
				tableName();
				setState(458);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(455);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
					case 1:
						{
						setState(454);
						match(AS);
						}
						break;
					}
					setState(457);
					uid();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(465);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SELECT:
				case WITH:
					{
					setState(460);
					selectStatement();
					}
					break;
				case T__2:
					{
					setState(461);
					match(T__2);
					setState(462);
					selectStatement();
					setState(463);
					match(T__3);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(468);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
				case 1:
					{
					setState(467);
					match(AS);
					}
					break;
				}
				setState(470);
				uid();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(472);
				match(T__2);
				setState(473);
				tableSources();
				setState(474);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JoinPartContext extends ParserRuleContext {
		public JoinTypeContext joinType() {
			return getRuleContext(JoinTypeContext.class,0);
		}
		public TerminalNode JOIN() { return getToken(JQuickSQLParser.JOIN, 0); }
		public TableSourceItemContext tableSourceItem() {
			return getRuleContext(TableSourceItemContext.class,0);
		}
		public TerminalNode ON() { return getToken(JQuickSQLParser.ON, 0); }
		public List<FullColumnNameContext> fullColumnName() {
			return getRuleContexts(FullColumnNameContext.class);
		}
		public FullColumnNameContext fullColumnName(int i) {
			return getRuleContext(FullColumnNameContext.class,i);
		}
		public JoinPartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinPart; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterJoinPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitJoinPart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitJoinPart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinPartContext joinPart() throws RecognitionException {
		JoinPartContext _localctx = new JoinPartContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_joinPart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			joinType();
			setState(479);
			match(JOIN);
			setState(480);
			tableSourceItem();
			setState(481);
			match(ON);
			setState(482);
			fullColumnName();
			setState(483);
			match(T__4);
			setState(484);
			fullColumnName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JoinTypeContext extends ParserRuleContext {
		public TerminalNode INNER() { return getToken(JQuickSQLParser.INNER, 0); }
		public TerminalNode CROSS() { return getToken(JQuickSQLParser.CROSS, 0); }
		public TerminalNode LEFT() { return getToken(JQuickSQLParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(JQuickSQLParser.RIGHT, 0); }
		public TerminalNode NATURAL() { return getToken(JQuickSQLParser.NATURAL, 0); }
		public TerminalNode FULL() { return getToken(JQuickSQLParser.FULL, 0); }
		public JoinTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterJoinType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitJoinType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitJoinType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinTypeContext joinType() throws RecognitionException {
		JoinTypeContext _localctx = new JoinTypeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_joinType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(486);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 9007199254740992000L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UidListContext extends ParserRuleContext {
		public List<UidContext> uid() {
			return getRuleContexts(UidContext.class);
		}
		public UidContext uid(int i) {
			return getRuleContext(UidContext.class,i);
		}
		public UidListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uidList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterUidList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitUidList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitUidList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UidListContext uidList() throws RecognitionException {
		UidListContext _localctx = new UidListContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_uidList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
			uid();
			setState(493);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(489);
				match(T__1);
				setState(490);
				uid();
				}
				}
				setState(495);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GroupByItemContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ASC() { return getToken(JQuickSQLParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(JQuickSQLParser.DESC, 0); }
		public GroupByItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterGroupByItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitGroupByItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitGroupByItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupByItemContext groupByItem() throws RecognitionException {
		GroupByItemContext _localctx = new GroupByItemContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_groupByItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(496);
			expression(0);
			setState(498);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(497);
				_la = _input.LA(1);
				if ( !(_la==DESC || _la==ASC) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HavingExprContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public HavingExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_havingExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterHavingExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitHavingExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitHavingExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HavingExprContext havingExpr() throws RecognitionException {
		HavingExprContext _localctx = new HavingExprContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_havingExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(500);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OrderByClauseContext extends ParserRuleContext {
		public TerminalNode ORDER() { return getToken(JQuickSQLParser.ORDER, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public List<OrderByExpressionContext> orderByExpression() {
			return getRuleContexts(OrderByExpressionContext.class);
		}
		public OrderByExpressionContext orderByExpression(int i) {
			return getRuleContext(OrderByExpressionContext.class,i);
		}
		public OrderByClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterOrderByClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitOrderByClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitOrderByClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrderByClauseContext orderByClause() throws RecognitionException {
		OrderByClauseContext _localctx = new OrderByClauseContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_orderByClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(502);
			match(ORDER);
			setState(503);
			match(BY);
			setState(504);
			orderByExpression();
			setState(509);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(505);
					match(T__1);
					setState(506);
					orderByExpression();
					}
					} 
				}
				setState(511);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OrderByExpressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ASC() { return getToken(JQuickSQLParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(JQuickSQLParser.DESC, 0); }
		public OrderByExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterOrderByExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitOrderByExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitOrderByExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrderByExpressionContext orderByExpression() throws RecognitionException {
		OrderByExpressionContext _localctx = new OrderByExpressionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_orderByExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			expression(0);
			setState(514);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(513);
				_la = _input.LA(1);
				if ( !(_la==DESC || _la==ASC) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LimitClauseContext extends ParserRuleContext {
		public TerminalNode LIMIT() { return getToken(JQuickSQLParser.LIMIT, 0); }
		public LimitOnlyContext limitOnly() {
			return getRuleContext(LimitOnlyContext.class,0);
		}
		public LimitWithOffsetContext limitWithOffset() {
			return getRuleContext(LimitWithOffsetContext.class,0);
		}
		public LimitClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limitClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterLimitClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitLimitClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitLimitClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitClauseContext limitClause() throws RecognitionException {
		LimitClauseContext _localctx = new LimitClauseContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_limitClause);
		try {
			setState(520);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(516);
				match(LIMIT);
				setState(517);
				limitOnly();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(518);
				match(LIMIT);
				setState(519);
				limitWithOffset();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LimitOnlyContext extends ParserRuleContext {
		public ExpressionContext limit;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LimitOnlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limitOnly; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterLimitOnly(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitLimitOnly(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitLimitOnly(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitOnlyContext limitOnly() throws RecognitionException {
		LimitOnlyContext _localctx = new LimitOnlyContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_limitOnly);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(522);
			((LimitOnlyContext)_localctx).limit = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LimitWithOffsetContext extends ParserRuleContext {
		public ExpressionContext offset;
		public ExpressionContext limit;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public LimitWithOffsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limitWithOffset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterLimitWithOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitLimitWithOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitLimitWithOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitWithOffsetContext limitWithOffset() throws RecognitionException {
		LimitWithOffsetContext _localctx = new LimitWithOffsetContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_limitWithOffset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			((LimitWithOffsetContext)_localctx).offset = expression(0);
			setState(525);
			match(T__1);
			setState(526);
			((LimitWithOffsetContext)_localctx).limit = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommonTableExpressionContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public TerminalNode AS() { return getToken(JQuickSQLParser.AS, 0); }
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public ColumnNamesContext columnNames() {
			return getRuleContext(ColumnNamesContext.class,0);
		}
		public InitialQueryContext initialQuery() {
			return getRuleContext(InitialQueryContext.class,0);
		}
		public TerminalNode UNION() { return getToken(JQuickSQLParser.UNION, 0); }
		public RecursivePartContext recursivePart() {
			return getRuleContext(RecursivePartContext.class,0);
		}
		public TerminalNode ALL() { return getToken(JQuickSQLParser.ALL, 0); }
		public CommonTableExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commonTableExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterCommonTableExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitCommonTableExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitCommonTableExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommonTableExpressionContext commonTableExpression() throws RecognitionException {
		CommonTableExpressionContext _localctx = new CommonTableExpressionContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_commonTableExpression);
		int _la;
		try {
			setState(551);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(528);
				uid();
				setState(530);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(529);
					columnNames();
					}
				}

				setState(532);
				match(AS);
				setState(533);
				match(T__2);
				setState(534);
				selectStatement();
				setState(535);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(537);
				uid();
				setState(539);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(538);
					columnNames();
					}
				}

				setState(541);
				match(AS);
				setState(542);
				match(T__2);
				setState(543);
				initialQuery();
				setState(544);
				match(UNION);
				setState(546);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL) {
					{
					setState(545);
					match(ALL);
					}
				}

				setState(548);
				recursivePart();
				setState(549);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnNamesContext extends ParserRuleContext {
		public List<UidContext> uid() {
			return getRuleContexts(UidContext.class);
		}
		public UidContext uid(int i) {
			return getRuleContext(UidContext.class,i);
		}
		public ColumnNamesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnNames; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterColumnNames(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitColumnNames(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitColumnNames(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnNamesContext columnNames() throws RecognitionException {
		ColumnNamesContext _localctx = new ColumnNamesContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_columnNames);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			match(T__2);
			setState(554);
			uid();
			setState(559);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(555);
				match(T__1);
				setState(556);
				uid();
				}
				}
				setState(561);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(562);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InitialQueryContext extends ParserRuleContext {
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public InitialQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initialQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterInitialQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitInitialQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitInitialQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitialQueryContext initialQuery() throws RecognitionException {
		InitialQueryContext _localctx = new InitialQueryContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_initialQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(564);
			selectStatement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecursivePartContext extends ParserRuleContext {
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public RecursivePartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recursivePart; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterRecursivePart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitRecursivePart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitRecursivePart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecursivePartContext recursivePart() throws RecognitionException {
		RecursivePartContext _localctx = new RecursivePartContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_recursivePart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(566);
			selectStatement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitExpressions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitExpressions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionsContext expressions() throws RecognitionException {
		ExpressionsContext _localctx = new ExpressionsContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_expressions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(568);
			expression(0);
			setState(573);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(569);
				match(T__1);
				setState(570);
				expression(0);
				}
				}
				setState(575);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public FunctionArgsContext functionArgs() {
			return getRuleContext(FunctionArgsContext.class,0);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(576);
			uid();
			setState(577);
			match(T__2);
			setState(579);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -105556604469176L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 524255L) != 0) || ((((_la - 260)) & ~0x3f) == 0 && ((1L << (_la - 260)) & 1031L) != 0)) {
				{
				setState(578);
				functionArgs();
				}
			}

			setState(581);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CompOperatorContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(JQuickSQLParser.IS, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public TerminalNode LIKE() { return getToken(JQuickSQLParser.LIKE, 0); }
		public TerminalNode IN() { return getToken(JQuickSQLParser.IN, 0); }
		public TerminalNode BETWEEN() { return getToken(JQuickSQLParser.BETWEEN, 0); }
		public CompOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterCompOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitCompOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitCompOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompOperatorContext compOperator() throws RecognitionException {
		CompOperatorContext _localctx = new CompOperatorContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_compOperator);
		int _la;
		try {
			setState(607);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(583);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(584);
				match(T__6);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(585);
				match(T__7);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(586);
				match(T__8);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(587);
				match(T__9);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(588);
				match(T__10);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(589);
				match(T__11);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(590);
				match(T__12);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(591);
				match(IS);
				setState(593);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(592);
					match(NOT);
					}
				}

				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(596);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(595);
					match(NOT);
					}
				}

				setState(598);
				match(LIKE);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(600);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(599);
					match(NOT);
					}
				}

				setState(602);
				match(IN);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(604);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(603);
					match(NOT);
					}
				}

				setState(606);
				match(BETWEEN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AggregateFunctionContext extends ParserRuleContext {
		public TerminalNode AVG() { return getToken(JQuickSQLParser.AVG, 0); }
		public TerminalNode MAX() { return getToken(JQuickSQLParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(JQuickSQLParser.MIN, 0); }
		public TerminalNode SUM() { return getToken(JQuickSQLParser.SUM, 0); }
		public TerminalNode COUNT() { return getToken(JQuickSQLParser.COUNT, 0); }
		public TerminalNode GROUP_CONCAT() { return getToken(JQuickSQLParser.GROUP_CONCAT, 0); }
		public TerminalNode STD() { return getToken(JQuickSQLParser.STD, 0); }
		public TerminalNode STDDEV() { return getToken(JQuickSQLParser.STDDEV, 0); }
		public AggregateFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterAggregateFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitAggregateFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitAggregateFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateFunctionContext aggregateFunction() throws RecognitionException {
		AggregateFunctionContext _localctx = new AggregateFunctionContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_aggregateFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(609);
			_la = _input.LA(1);
			if ( !(((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 63L) != 0) || _la==STD || _la==STDDEV) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionArgsContext extends ParserRuleContext {
		public List<ConstantContext> constant() {
			return getRuleContexts(ConstantContext.class);
		}
		public ConstantContext constant(int i) {
			return getRuleContext(ConstantContext.class,i);
		}
		public List<FullColumnNameContext> fullColumnName() {
			return getRuleContexts(FullColumnNameContext.class);
		}
		public FullColumnNameContext fullColumnName(int i) {
			return getRuleContext(FullColumnNameContext.class,i);
		}
		public List<FunctionCallContext> functionCall() {
			return getRuleContexts(FunctionCallContext.class);
		}
		public FunctionCallContext functionCall(int i) {
			return getRuleContext(FunctionCallContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<FunctionArgContext> functionArg() {
			return getRuleContexts(FunctionArgContext.class);
		}
		public FunctionArgContext functionArg(int i) {
			return getRuleContext(FunctionArgContext.class,i);
		}
		public TerminalNode DISTINCT() { return getToken(JQuickSQLParser.DISTINCT, 0); }
		public FunctionArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionArgs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFunctionArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFunctionArgs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFunctionArgs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionArgsContext functionArgs() throws RecognitionException {
		FunctionArgsContext _localctx = new FunctionArgsContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_functionArgs);
		int _la;
		try {
			setState(641);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(615);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
				case 1:
					{
					setState(611);
					constant();
					}
					break;
				case 2:
					{
					setState(612);
					fullColumnName();
					}
					break;
				case 3:
					{
					setState(613);
					functionCall();
					}
					break;
				case 4:
					{
					setState(614);
					expression(0);
					}
					break;
				}
				setState(626);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(617);
					match(T__1);
					setState(622);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
					case 1:
						{
						setState(618);
						constant();
						}
						break;
					case 2:
						{
						setState(619);
						fullColumnName();
						}
						break;
					case 3:
						{
						setState(620);
						functionCall();
						}
						break;
					case 4:
						{
						setState(621);
						expression(0);
						}
						break;
					}
					}
					}
					setState(628);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(629);
				match(T__5);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(631);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
				case 1:
					{
					setState(630);
					match(DISTINCT);
					}
					break;
				}
				setState(633);
				functionArg();
				setState(638);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(634);
					match(T__1);
					setState(635);
					functionArg();
					}
					}
					setState(640);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionArgContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public FunctionArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFunctionArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFunctionArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFunctionArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionArgContext functionArg() throws RecognitionException {
		FunctionArgContext _localctx = new FunctionArgContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_functionArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(643);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PredicateContext extends ParserRuleContext {
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	 
		public PredicateContext() { }
		public void copyFrom(PredicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionAtomPredicateContext extends PredicateContext {
		public ExpressionAtomContext expressionAtom() {
			return getRuleContext(ExpressionAtomContext.class,0);
		}
		public ExpressionAtomPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterExpressionAtomPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitExpressionAtomPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitExpressionAtomPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BinaryComparisonPredicateContext extends PredicateContext {
		public PredicateContext left;
		public PredicateContext right;
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public BinaryComparisonPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterBinaryComparisonPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitBinaryComparisonPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitBinaryComparisonPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InPredicateContext extends PredicateContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode IN() { return getToken(JQuickSQLParser.IN, 0); }
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
		}
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public InPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterInPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitInPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitInPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExisitsExpressionContext extends PredicateContext {
		public TerminalNode EXISTS() { return getToken(JQuickSQLParser.EXISTS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExisitsExpressionContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterExisitsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitExisitsExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitExisitsExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BetweenPredicateContext extends PredicateContext {
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public TerminalNode BETWEEN() { return getToken(JQuickSQLParser.BETWEEN, 0); }
		public TerminalNode AND() { return getToken(JQuickSQLParser.AND, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public BetweenPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterBetweenPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitBetweenPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitBetweenPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IsNullPredicateContext extends PredicateContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode IS() { return getToken(JQuickSQLParser.IS, 0); }
		public TerminalNode NULL() { return getToken(JQuickSQLParser.NULL, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public IsNullPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterIsNullPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitIsNullPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitIsNullPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LikePredicateContext extends PredicateContext {
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public TerminalNode LIKE() { return getToken(JQuickSQLParser.LIKE, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public TerminalNode ESCAPE() { return getToken(JQuickSQLParser.ESCAPE, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(JQuickSQLParser.STRING_LITERAL, 0); }
		public LikePredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterLikePredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitLikePredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitLikePredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionAtomPredicateWithLocalIdContext extends PredicateContext {
		public ExpressionAtomContext expressionAtom() {
			return getRuleContext(ExpressionAtomContext.class,0);
		}
		public TerminalNode LOCAL_ID() { return getToken(JQuickSQLParser.LOCAL_ID, 0); }
		public TerminalNode VAR_ASSIGN() { return getToken(JQuickSQLParser.VAR_ASSIGN, 0); }
		public ExpressionAtomPredicateWithLocalIdContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterExpressionAtomPredicateWithLocalId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitExpressionAtomPredicateWithLocalId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitExpressionAtomPredicateWithLocalId(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RegexpPredicateContext extends PredicateContext {
		public Token regex;
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public TerminalNode REGEXP() { return getToken(JQuickSQLParser.REGEXP, 0); }
		public TerminalNode RLIKE() { return getToken(JQuickSQLParser.RLIKE, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public RegexpPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterRegexpPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitRegexpPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitRegexpPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		return predicate(0);
	}

	private PredicateContext predicate(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PredicateContext _localctx = new PredicateContext(_ctx, _parentState);
		PredicateContext _prevctx = _localctx;
		int _startState = 88;
		enterRecursionRule(_localctx, 88, RULE_predicate, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(654);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				{
				_localctx = new ExpressionAtomPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(646);
				expressionAtom(0);
				}
				break;
			case 2:
				{
				_localctx = new ExpressionAtomPredicateWithLocalIdContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(649);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOCAL_ID) {
					{
					setState(647);
					match(LOCAL_ID);
					setState(648);
					match(VAR_ASSIGN);
					}
				}

				setState(651);
				expressionAtom(0);
				}
				break;
			case 3:
				{
				_localctx = new ExisitsExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(652);
				match(EXISTS);
				setState(653);
				expression(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(705);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(703);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryComparisonPredicateContext(new PredicateContext(_parentctx, _parentState));
						((BinaryComparisonPredicateContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(656);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(657);
						comparisonOperator();
						setState(658);
						((BinaryComparisonPredicateContext)_localctx).right = predicate(8);
						}
						break;
					case 2:
						{
						_localctx = new BetweenPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(660);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(662);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(661);
							match(NOT);
							}
						}

						setState(664);
						match(BETWEEN);
						setState(665);
						predicate(0);
						setState(666);
						match(AND);
						setState(667);
						predicate(7);
						}
						break;
					case 3:
						{
						_localctx = new RegexpPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(669);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(671);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(670);
							match(NOT);
							}
						}

						setState(673);
						((RegexpPredicateContext)_localctx).regex = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==REGEXP || _la==RLIKE) ) {
							((RegexpPredicateContext)_localctx).regex = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(674);
						predicate(4);
						}
						break;
					case 4:
						{
						_localctx = new IsNullPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(675);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(676);
						match(IS);
						setState(678);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(677);
							match(NOT);
							}
						}

						setState(680);
						match(NULL);
						}
						break;
					case 5:
						{
						_localctx = new InPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(681);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(683);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(682);
							match(NOT);
							}
						}

						setState(685);
						match(IN);
						setState(686);
						match(T__2);
						setState(689);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
						case 1:
							{
							setState(687);
							selectStatement();
							}
							break;
						case 2:
							{
							setState(688);
							expressions();
							}
							break;
						}
						setState(691);
						match(T__3);
						}
						break;
					case 6:
						{
						_localctx = new LikePredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(693);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(695);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(694);
							match(NOT);
							}
						}

						setState(697);
						match(LIKE);
						setState(698);
						predicate(0);
						setState(701);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
						case 1:
							{
							setState(699);
							match(ESCAPE);
							setState(700);
							match(STRING_LITERAL);
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(707);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionAtomContext extends ParserRuleContext {
		public ExpressionAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionAtom; }
	 
		public ExpressionAtomContext() { }
		public void copyFrom(ExpressionAtomContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExpressionAtomContext extends ExpressionAtomContext {
		public UnaryOperatorContext unaryOperator() {
			return getRuleContext(UnaryOperatorContext.class,0);
		}
		public ExpressionAtomContext expressionAtom() {
			return getRuleContext(ExpressionAtomContext.class,0);
		}
		public UnaryExpressionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterUnaryExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitUnaryExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitUnaryExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SubqueryExperssionAtomContext extends ExpressionAtomContext {
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public SubqueryExperssionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSubqueryExperssionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSubqueryExperssionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSubqueryExperssionAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConstantExpressionAtomContext extends ExpressionAtomContext {
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public ConstantExpressionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterConstantExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitConstantExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitConstantExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallExpressionAtomContext extends ExpressionAtomContext {
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public FunctionCallExpressionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFunctionCallExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFunctionCallExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFunctionCallExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FullColumnNameExpressionAtomContext extends ExpressionAtomContext {
		public FullColumnNameContext fullColumnName() {
			return getRuleContext(FullColumnNameContext.class,0);
		}
		public FullColumnNameExpressionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFullColumnNameExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFullColumnNameExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFullColumnNameExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NestedExpressionAtomContext extends ExpressionAtomContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public NestedExpressionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterNestedExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitNestedExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitNestedExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MathExpressionAtomContext extends ExpressionAtomContext {
		public ExpressionAtomContext left;
		public ExpressionAtomContext right;
		public MathOperatorContext mathOperator() {
			return getRuleContext(MathOperatorContext.class,0);
		}
		public List<ExpressionAtomContext> expressionAtom() {
			return getRuleContexts(ExpressionAtomContext.class);
		}
		public ExpressionAtomContext expressionAtom(int i) {
			return getRuleContext(ExpressionAtomContext.class,i);
		}
		public MathExpressionAtomContext(ExpressionAtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterMathExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitMathExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitMathExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionAtomContext expressionAtom() throws RecognitionException {
		return expressionAtom(0);
	}

	private ExpressionAtomContext expressionAtom(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionAtomContext _localctx = new ExpressionAtomContext(_ctx, _parentState);
		ExpressionAtomContext _prevctx = _localctx;
		int _startState = 90;
		enterRecursionRule(_localctx, 90, RULE_expressionAtom, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(730);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
			case 1:
				{
				_localctx = new ConstantExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(709);
				constant();
				}
				break;
			case 2:
				{
				_localctx = new FullColumnNameExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(710);
				fullColumnName();
				}
				break;
			case 3:
				{
				_localctx = new FunctionCallExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(711);
				functionCall();
				}
				break;
			case 4:
				{
				_localctx = new NestedExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(712);
				match(T__2);
				setState(713);
				expression(0);
				setState(718);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(714);
					match(T__1);
					setState(715);
					expression(0);
					}
					}
					setState(720);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(721);
				match(T__3);
				}
				break;
			case 5:
				{
				_localctx = new SubqueryExperssionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(723);
				match(T__2);
				setState(724);
				selectStatement();
				setState(725);
				match(T__3);
				}
				break;
			case 6:
				{
				_localctx = new UnaryExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(727);
				unaryOperator();
				setState(728);
				expressionAtom(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(738);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MathExpressionAtomContext(new ExpressionAtomContext(_parentctx, _parentState));
					((MathExpressionAtomContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expressionAtom);
					setState(732);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(733);
					mathOperator();
					setState(734);
					((MathExpressionAtomContext)_localctx).right = expressionAtom(3);
					}
					} 
				}
				setState(740);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectResultContext extends ExpressionContext {
		public SelectClauseContext selectClause() {
			return getRuleContext(SelectClauseContext.class,0);
		}
		public SelectResultContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectResult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectResult(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExpressionContext extends ExpressionContext {
		public Token notOperator;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public NotExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitNotExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitNotExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParenExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterParenExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitParenExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitParenExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LogicalExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public LogicalOperatorContext logicalOperator() {
			return getRuleContext(LogicalOperatorContext.class,0);
		}
		public LogicalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterLogicalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitLogicalExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitLogicalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PredicateExpressionContext extends ExpressionContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public PredicateExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPredicateExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPredicateExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPredicateExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 92;
		enterRecursionRule(_localctx, 92, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(742);
				((NotExpressionContext)_localctx).notOperator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__13 || _la==NOT) ) {
					((NotExpressionContext)_localctx).notOperator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(743);
				expression(5);
				}
				break;
			case 2:
				{
				_localctx = new PredicateExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(744);
				predicate(0);
				}
				break;
			case 3:
				{
				_localctx = new SelectResultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(745);
				selectClause();
				}
				break;
			case 4:
				{
				_localctx = new ParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(746);
				match(T__2);
				setState(747);
				expression(0);
				setState(748);
				match(T__3);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(758);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new LogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expression);
					setState(752);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(753);
					logicalOperator();
					setState(754);
					expression(5);
					}
					} 
				}
				setState(760);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MathOperatorContext extends ParserRuleContext {
		public MathOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterMathOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitMathOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitMathOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathOperatorContext mathOperator() throws RecognitionException {
		MathOperatorContext _localctx = new MathOperatorContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_mathOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(761);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1015872L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnaryOperatorContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public UnaryOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterUnaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitUnaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitUnaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryOperatorContext unaryOperator() throws RecognitionException {
		UnaryOperatorContext _localctx = new UnaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(763);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 17592187502592L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LogicalOperatorContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(JQuickSQLParser.AND, 0); }
		public TerminalNode XOR() { return getToken(JQuickSQLParser.XOR, 0); }
		public TerminalNode OR() { return getToken(JQuickSQLParser.OR, 0); }
		public LogicalOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterLogicalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitLogicalOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitLogicalOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalOperatorContext logicalOperator() throws RecognitionException {
		LogicalOperatorContext _localctx = new LogicalOperatorContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_logicalOperator);
		try {
			setState(772);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(765);
				match(AND);
				}
				break;
			case T__20:
				enterOuterAlt(_localctx, 2);
				{
				setState(766);
				match(T__20);
				setState(767);
				match(T__20);
				}
				break;
			case XOR:
				enterOuterAlt(_localctx, 3);
				{
				setState(768);
				match(XOR);
				}
				break;
			case OR:
				enterOuterAlt(_localctx, 4);
				{
				setState(769);
				match(OR);
				}
				break;
			case T__21:
				enterOuterAlt(_localctx, 5);
				{
				setState(770);
				match(T__21);
				setState(771);
				match(T__21);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComparisonOperatorContext extends ParserRuleContext {
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(774);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8096L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstantContext extends ParserRuleContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public Decimal_literalContext decimal_literal() {
			return getRuleContext(Decimal_literalContext.class,0);
		}
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
		public Null_literalContext null_literal() {
			return getRuleContext(Null_literalContext.class,0);
		}
		public DateLiteralContext dateLiteral() {
			return getRuleContext(DateLiteralContext.class,0);
		}
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_constant);
		try {
			setState(783);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(776);
				stringLiteral();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(777);
				decimal_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(778);
				match(T__17);
				setState(779);
				decimal_literal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(780);
				booleanLiteral();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(781);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(782);
				dateLiteral();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Decimal_literalContext extends ParserRuleContext {
		public TerminalNode DECIMAL_LITERAL() { return getToken(JQuickSQLParser.DECIMAL_LITERAL, 0); }
		public Decimal_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimal_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDecimal_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDecimal_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDecimal_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Decimal_literalContext decimal_literal() throws RecognitionException {
		Decimal_literalContext _localctx = new Decimal_literalContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_decimal_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(785);
			match(DECIMAL_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_literalContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(JQuickSQLParser.NULL, 0); }
		public Null_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterNull_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitNull_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitNull_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Null_literalContext null_literal() throws RecognitionException {
		Null_literalContext _localctx = new Null_literalContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(787);
			match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DateLiteralContext extends ParserRuleContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public FormatContext format() {
			return getRuleContext(FormatContext.class,0);
		}
		public DateLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dateLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDateLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDateLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDateLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DateLiteralContext dateLiteral() throws RecognitionException {
		DateLiteralContext _localctx = new DateLiteralContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_dateLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(789);
			stringLiteral();
			setState(790);
			match(T__22);
			setState(791);
			format();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormatContext extends ParserRuleContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public FormatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_format; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFormat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFormat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFormat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormatContext format() throws RecognitionException {
		FormatContext _localctx = new FormatContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_format);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(793);
			stringLiteral();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BooleanLiteralContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(JQuickSQLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(JQuickSQLParser.FALSE, 0); }
		public BooleanLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanLiteralContext booleanLiteral() throws RecognitionException {
		BooleanLiteralContext _localctx = new BooleanLiteralContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(795);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FullColumnNameContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public List<DottedIdContext> dottedId() {
			return getRuleContexts(DottedIdContext.class);
		}
		public DottedIdContext dottedId(int i) {
			return getRuleContext(DottedIdContext.class,i);
		}
		public FullColumnNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fullColumnName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFullColumnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFullColumnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFullColumnName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FullColumnNameContext fullColumnName() throws RecognitionException {
		FullColumnNameContext _localctx = new FullColumnNameContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_fullColumnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797);
			uid();
			setState(802);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				{
				setState(798);
				dottedId();
				setState(800);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
				case 1:
					{
					setState(799);
					dottedId();
					}
					break;
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TableNameContext extends ParserRuleContext {
		public UidContext schema;
		public UidContext table;
		public List<UidContext> uid() {
			return getRuleContexts(UidContext.class);
		}
		public UidContext uid(int i) {
			return getRuleContext(UidContext.class,i);
		}
		public TableNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterTableName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitTableName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitTableName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableNameContext tableName() throws RecognitionException {
		TableNameContext _localctx = new TableNameContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_tableName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(807);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				{
				setState(804);
				((TableNameContext)_localctx).schema = uid();
				setState(805);
				match(T__23);
				}
				break;
			}
			setState(809);
			((TableNameContext)_localctx).table = uid();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SchemaNameContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public SchemaNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schemaName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSchemaName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSchemaName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSchemaName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchemaNameContext schemaName() throws RecognitionException {
		SchemaNameContext _localctx = new SchemaNameContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_schemaName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(811);
			uid();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DottedIdContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public DottedIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dottedId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDottedId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDottedId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDottedId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DottedIdContext dottedId() throws RecognitionException {
		DottedIdContext _localctx = new DottedIdContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_dottedId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(813);
			match(T__23);
			setState(814);
			uid();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UidContext extends ParserRuleContext {
		public SimpleIdContext simpleId() {
			return getRuleContext(SimpleIdContext.class,0);
		}
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public UidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterUid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitUid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitUid(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UidContext uid() throws RecognitionException {
		UidContext _localctx = new UidContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_uid);
		try {
			setState(818);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
			case FROM:
			case WHERE:
			case GROUP:
			case BY:
			case HAVING:
			case ORDER:
			case LIMIT:
			case OFFSET:
			case AND:
			case OR:
			case XOR:
			case NOT:
			case AS:
			case WITH:
			case RECURSIVE:
			case ROLLUP:
			case DRILLDOWN:
			case SLICE:
			case DICE:
			case PIVOT:
			case JOIN:
			case INNER:
			case OUTER:
			case LEFT:
			case RIGHT:
			case FULL:
			case CROSS:
			case NATURAL:
			case USE:
			case FORCE:
			case IGNORE:
			case INDEX:
			case KEY:
			case FOR:
			case ALL:
			case DISTINCT:
			case DISTINCTROW:
			case HIGH_PRIORITY:
			case STRAIGHT_JOIN:
			case SQL_SMALL_RESULT:
			case SQL_BIG_RESULT:
			case SQL_BUFFER_RESULT:
			case SQL_CACHE:
			case SQL_NO_CACHE:
			case SQL_CALC_FOUND_ROWS:
			case ID_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(816);
				simpleId();
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(817);
				stringLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringLiteralContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(JQuickSQLParser.STRING_LITERAL, 0); }
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_stringLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(820);
			match(STRING_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SimpleIdContext extends ParserRuleContext {
		public TerminalNode ID_LITERAL() { return getToken(JQuickSQLParser.ID_LITERAL, 0); }
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public SimpleIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSimpleId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSimpleId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSimpleId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleIdContext simpleId() throws RecognitionException {
		SimpleIdContext _localctx = new SimpleIdContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_simpleId);
		try {
			setState(824);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(822);
				match(ID_LITERAL);
				}
				break;
			case SELECT:
			case FROM:
			case WHERE:
			case GROUP:
			case BY:
			case HAVING:
			case ORDER:
			case LIMIT:
			case OFFSET:
			case AND:
			case OR:
			case XOR:
			case NOT:
			case AS:
			case WITH:
			case RECURSIVE:
			case ROLLUP:
			case DRILLDOWN:
			case SLICE:
			case DICE:
			case PIVOT:
			case JOIN:
			case INNER:
			case OUTER:
			case LEFT:
			case RIGHT:
			case FULL:
			case CROSS:
			case NATURAL:
			case USE:
			case FORCE:
			case IGNORE:
			case INDEX:
			case KEY:
			case FOR:
			case ALL:
			case DISTINCT:
			case DISTINCTROW:
			case HIGH_PRIORITY:
			case STRAIGHT_JOIN:
			case SQL_SMALL_RESULT:
			case SQL_BIG_RESULT:
			case SQL_BUFFER_RESULT:
			case SQL_CACHE:
			case SQL_NO_CACHE:
			case SQL_CALC_FOUND_ROWS:
				enterOuterAlt(_localctx, 2);
				{
				setState(823);
				keyword();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public TerminalNode FROM() { return getToken(JQuickSQLParser.FROM, 0); }
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public TerminalNode ORDER() { return getToken(JQuickSQLParser.ORDER, 0); }
		public TerminalNode LIMIT() { return getToken(JQuickSQLParser.LIMIT, 0); }
		public TerminalNode OFFSET() { return getToken(JQuickSQLParser.OFFSET, 0); }
		public TerminalNode AND() { return getToken(JQuickSQLParser.AND, 0); }
		public TerminalNode OR() { return getToken(JQuickSQLParser.OR, 0); }
		public TerminalNode XOR() { return getToken(JQuickSQLParser.XOR, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public TerminalNode AS() { return getToken(JQuickSQLParser.AS, 0); }
		public TerminalNode WITH() { return getToken(JQuickSQLParser.WITH, 0); }
		public TerminalNode RECURSIVE() { return getToken(JQuickSQLParser.RECURSIVE, 0); }
		public TerminalNode ROLLUP() { return getToken(JQuickSQLParser.ROLLUP, 0); }
		public TerminalNode DRILLDOWN() { return getToken(JQuickSQLParser.DRILLDOWN, 0); }
		public TerminalNode SLICE() { return getToken(JQuickSQLParser.SLICE, 0); }
		public TerminalNode DICE() { return getToken(JQuickSQLParser.DICE, 0); }
		public TerminalNode PIVOT() { return getToken(JQuickSQLParser.PIVOT, 0); }
		public TerminalNode JOIN() { return getToken(JQuickSQLParser.JOIN, 0); }
		public TerminalNode INNER() { return getToken(JQuickSQLParser.INNER, 0); }
		public TerminalNode OUTER() { return getToken(JQuickSQLParser.OUTER, 0); }
		public TerminalNode LEFT() { return getToken(JQuickSQLParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(JQuickSQLParser.RIGHT, 0); }
		public TerminalNode FULL() { return getToken(JQuickSQLParser.FULL, 0); }
		public TerminalNode CROSS() { return getToken(JQuickSQLParser.CROSS, 0); }
		public TerminalNode NATURAL() { return getToken(JQuickSQLParser.NATURAL, 0); }
		public TerminalNode USE() { return getToken(JQuickSQLParser.USE, 0); }
		public TerminalNode FORCE() { return getToken(JQuickSQLParser.FORCE, 0); }
		public TerminalNode IGNORE() { return getToken(JQuickSQLParser.IGNORE, 0); }
		public TerminalNode INDEX() { return getToken(JQuickSQLParser.INDEX, 0); }
		public TerminalNode KEY() { return getToken(JQuickSQLParser.KEY, 0); }
		public TerminalNode FOR() { return getToken(JQuickSQLParser.FOR, 0); }
		public TerminalNode ALL() { return getToken(JQuickSQLParser.ALL, 0); }
		public TerminalNode DISTINCT() { return getToken(JQuickSQLParser.DISTINCT, 0); }
		public TerminalNode DISTINCTROW() { return getToken(JQuickSQLParser.DISTINCTROW, 0); }
		public TerminalNode HIGH_PRIORITY() { return getToken(JQuickSQLParser.HIGH_PRIORITY, 0); }
		public TerminalNode STRAIGHT_JOIN() { return getToken(JQuickSQLParser.STRAIGHT_JOIN, 0); }
		public TerminalNode SQL_SMALL_RESULT() { return getToken(JQuickSQLParser.SQL_SMALL_RESULT, 0); }
		public TerminalNode SQL_BIG_RESULT() { return getToken(JQuickSQLParser.SQL_BIG_RESULT, 0); }
		public TerminalNode SQL_BUFFER_RESULT() { return getToken(JQuickSQLParser.SQL_BUFFER_RESULT, 0); }
		public TerminalNode SQL_CACHE() { return getToken(JQuickSQLParser.SQL_CACHE, 0); }
		public TerminalNode SQL_NO_CACHE() { return getToken(JQuickSQLParser.SQL_NO_CACHE, 0); }
		public TerminalNode SQL_CALC_FOUND_ROWS() { return getToken(JQuickSQLParser.SQL_CALC_FOUND_ROWS, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitKeyword(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitKeyword(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(826);
			_la = _input.LA(1);
			if ( !(((((_la - 32)) & ~0x3f) == 0 && ((1L << (_la - 32)) & 562812514443263L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 44:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 45:
			return expressionAtom_sempred((ExpressionAtomContext)_localctx, predIndex);
		case 46:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 7);
		case 1:
			return precpred(_ctx, 6);
		case 2:
			return precpred(_ctx, 3);
		case 3:
			return precpred(_ctx, 8);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean expressionAtom_sempred(ExpressionAtomContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0112\u033d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007"+
		",\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u0007"+
		"1\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u0007"+
		"6\u00027\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007"+
		";\u0002<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007"+
		"@\u0001\u0000\u0001\u0000\u0003\u0000\u0085\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u008b\b\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0005\u0001\u0091\b\u0001\n\u0001\f\u0001"+
		"\u0094\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u0099\b"+
		"\u0001\u0001\u0002\u0001\u0002\u0003\u0002\u009d\b\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00a4\b\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003"+
		"\u0004\u00ac\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0005\u0004\u00b3\b\u0004\n\u0004\f\u0004\u00b6\t\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00bc\b\u0004\u0001\u0004"+
		"\u0003\u0004\u00bf\b\u0004\u0001\u0004\u0003\u0004\u00c2\b\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003"+
		"\u0005\u00ca\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0005\u0005\u00d1\b\u0005\n\u0005\f\u0005\u00d4\t\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0003\u0005\u00dd\b\u0005\u0001\u0005\u0003\u0005\u00e0\b\u0005\u0001"+
		"\u0005\u0003\u0005\u00e3\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005"+
		"\u0006\u00e8\b\u0006\n\u0006\f\u0006\u00eb\t\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0005\u0007\u00fb\b\u0007\n\u0007\f\u0007\u00fe\t\u0007\u0003\u0007\u0100"+
		"\b\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0104\b\u0007\u0001\u0007"+
		"\u0003\u0007\u0107\b\u0007\u0001\u0007\u0003\u0007\u010a\b\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005"+
		"\t\u011e\b\t\n\t\f\t\u0121\t\t\u0003\t\u0123\b\t\u0001\t\u0001\t\u0003"+
		"\t\u0127\b\t\u0001\t\u0003\t\u012a\b\t\u0001\t\u0003\t\u012d\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0005\n\u0132\b\n\n\n\f\n\u0135\t\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0003\f\u014b\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0005\f\u0152\b\f\n\f\f\f\u0155\t\f\u0003\f\u0157\b\f\u0001\f\u0001"+
		"\f\u0003\f\u015b\b\f\u0001\f\u0003\f\u015e\b\f\u0001\f\u0003\f\u0161\b"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u016d\b\u000f\n\u000f\f\u000f"+
		"\u0170\t\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u0174\b\u0010\u0001"+
		"\u0010\u0001\u0010\u0003\u0010\u0178\b\u0010\u0001\u0010\u0001\u0010\u0003"+
		"\u0010\u017c\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0005\u0010\u0183\b\u0010\n\u0010\f\u0010\u0186\t\u0010\u0003\u0010"+
		"\u0188\b\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u018c\b\u0010\u0001"+
		"\u0010\u0003\u0010\u018f\b\u0010\u0001\u0010\u0003\u0010\u0192\b\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u0198\b\u0012"+
		"\u0001\u0012\u0001\u0012\u0005\u0012\u019c\b\u0012\n\u0012\f\u0012\u019f"+
		"\t\u0012\u0001\u0013\u0001\u0013\u0003\u0013\u01a3\b\u0013\u0001\u0013"+
		"\u0003\u0013\u01a6\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0005\u0015\u01ae\b\u0015\n\u0015\f\u0015\u01b1"+
		"\t\u0015\u0001\u0016\u0001\u0016\u0005\u0016\u01b5\b\u0016\n\u0016\f\u0016"+
		"\u01b8\t\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u01bd\b"+
		"\u0016\n\u0016\f\u0016\u01c0\t\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u01c4\b\u0016\u0001\u0017\u0001\u0017\u0003\u0017\u01c8\b\u0017\u0001"+
		"\u0017\u0003\u0017\u01cb\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0003\u0017\u01d2\b\u0017\u0001\u0017\u0003\u0017\u01d5"+
		"\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0003\u0017\u01dd\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001"+
		"\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u01ec\b\u001a\n"+
		"\u001a\f\u001a\u01ef\t\u001a\u0001\u001b\u0001\u001b\u0003\u001b\u01f3"+
		"\b\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0005\u001d\u01fc\b\u001d\n\u001d\f\u001d\u01ff\t\u001d"+
		"\u0001\u001e\u0001\u001e\u0003\u001e\u0203\b\u001e\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0003\u001f\u0209\b\u001f\u0001 \u0001 \u0001"+
		"!\u0001!\u0001!\u0001!\u0001\"\u0001\"\u0003\"\u0213\b\"\u0001\"\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0003\"\u021c\b\"\u0001\"\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0003\"\u0223\b\"\u0001\"\u0001\"\u0001\"\u0003"+
		"\"\u0228\b\"\u0001#\u0001#\u0001#\u0001#\u0005#\u022e\b#\n#\f#\u0231\t"+
		"#\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001&\u0005"+
		"&\u023c\b&\n&\f&\u023f\t&\u0001\'\u0001\'\u0001\'\u0003\'\u0244\b\'\u0001"+
		"\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001"+
		"(\u0001(\u0003(\u0252\b(\u0001(\u0003(\u0255\b(\u0001(\u0001(\u0003(\u0259"+
		"\b(\u0001(\u0001(\u0003(\u025d\b(\u0001(\u0003(\u0260\b(\u0001)\u0001"+
		")\u0001*\u0001*\u0001*\u0001*\u0003*\u0268\b*\u0001*\u0001*\u0001*\u0001"+
		"*\u0001*\u0003*\u026f\b*\u0005*\u0271\b*\n*\f*\u0274\t*\u0001*\u0001*"+
		"\u0003*\u0278\b*\u0001*\u0001*\u0001*\u0005*\u027d\b*\n*\f*\u0280\t*\u0003"+
		"*\u0282\b*\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0003,\u028a\b,\u0001"+
		",\u0001,\u0001,\u0003,\u028f\b,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0003,\u0297\b,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0003"+
		",\u02a0\b,\u0001,\u0001,\u0001,\u0001,\u0001,\u0003,\u02a7\b,\u0001,\u0001"+
		",\u0001,\u0003,\u02ac\b,\u0001,\u0001,\u0001,\u0001,\u0003,\u02b2\b,\u0001"+
		",\u0001,\u0001,\u0001,\u0003,\u02b8\b,\u0001,\u0001,\u0001,\u0001,\u0003"+
		",\u02be\b,\u0005,\u02c0\b,\n,\f,\u02c3\t,\u0001-\u0001-\u0001-\u0001-"+
		"\u0001-\u0001-\u0001-\u0001-\u0005-\u02cd\b-\n-\f-\u02d0\t-\u0001-\u0001"+
		"-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0003-\u02db\b-\u0001"+
		"-\u0001-\u0001-\u0001-\u0005-\u02e1\b-\n-\f-\u02e4\t-\u0001.\u0001.\u0001"+
		".\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0003.\u02ef\b.\u0001.\u0001"+
		".\u0001.\u0001.\u0005.\u02f5\b.\n.\f.\u02f8\t.\u0001/\u0001/\u00010\u0001"+
		"0\u00011\u00011\u00011\u00011\u00011\u00011\u00011\u00031\u0305\b1\u0001"+
		"2\u00012\u00013\u00013\u00013\u00013\u00013\u00013\u00013\u00033\u0310"+
		"\b3\u00014\u00014\u00015\u00015\u00016\u00016\u00016\u00016\u00017\u0001"+
		"7\u00018\u00018\u00019\u00019\u00019\u00039\u0321\b9\u00039\u0323\b9\u0001"+
		":\u0001:\u0001:\u0003:\u0328\b:\u0001:\u0001:\u0001;\u0001;\u0001<\u0001"+
		"<\u0001<\u0001=\u0001=\u0003=\u0333\b=\u0001>\u0001>\u0001?\u0001?\u0003"+
		"?\u0339\b?\u0001@\u0001@\u0001@\u0000\u0003XZ\\A\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,."+
		"02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0000\u000b\u0001\u0000"+
		"FH\u0002\u000088:>\u0001\u0000\u001e\u001f\u0002\u0000SX\u010c\u010d\u0001"+
		"\u0000\u0110\u0111\u0002\u0000\u000e\u000e,,\u0002\u0000\u0006\u0006\u000f"+
		"\u0013\u0004\u0000\u000e\u000e\u0011\u0012\u0014\u0014,,\u0002\u0000\u0005"+
		"\u0005\u0007\f\u0001\u0000QR\u0003\u0000 ,/DFP\u0386\u0000\u0082\u0001"+
		"\u0000\u0000\u0000\u0002\u0098\u0001\u0000\u0000\u0000\u0004\u009c\u0001"+
		"\u0000\u0000\u0000\u0006\u00a3\u0001\u0000\u0000\u0000\b\u00a5\u0001\u0000"+
		"\u0000\u0000\n\u00c3\u0001\u0000\u0000\u0000\f\u00e4\u0001\u0000\u0000"+
		"\u0000\u000e\u00ec\u0001\u0000\u0000\u0000\u0010\u010b\u0001\u0000\u0000"+
		"\u0000\u0012\u010f\u0001\u0000\u0000\u0000\u0014\u012e\u0001\u0000\u0000"+
		"\u0000\u0016\u0136\u0001\u0000\u0000\u0000\u0018\u013a\u0001\u0000\u0000"+
		"\u0000\u001a\u0162\u0001\u0000\u0000\u0000\u001c\u0167\u0001\u0000\u0000"+
		"\u0000\u001e\u0169\u0001\u0000\u0000\u0000 \u0171\u0001\u0000\u0000\u0000"+
		"\"\u0193\u0001\u0000\u0000\u0000$\u0197\u0001\u0000\u0000\u0000&\u01a0"+
		"\u0001\u0000\u0000\u0000(\u01a7\u0001\u0000\u0000\u0000*\u01aa\u0001\u0000"+
		"\u0000\u0000,\u01c3\u0001\u0000\u0000\u0000.\u01dc\u0001\u0000\u0000\u0000"+
		"0\u01de\u0001\u0000\u0000\u00002\u01e6\u0001\u0000\u0000\u00004\u01e8"+
		"\u0001\u0000\u0000\u00006\u01f0\u0001\u0000\u0000\u00008\u01f4\u0001\u0000"+
		"\u0000\u0000:\u01f6\u0001\u0000\u0000\u0000<\u0200\u0001\u0000\u0000\u0000"+
		">\u0208\u0001\u0000\u0000\u0000@\u020a\u0001\u0000\u0000\u0000B\u020c"+
		"\u0001\u0000\u0000\u0000D\u0227\u0001\u0000\u0000\u0000F\u0229\u0001\u0000"+
		"\u0000\u0000H\u0234\u0001\u0000\u0000\u0000J\u0236\u0001\u0000\u0000\u0000"+
		"L\u0238\u0001\u0000\u0000\u0000N\u0240\u0001\u0000\u0000\u0000P\u025f"+
		"\u0001\u0000\u0000\u0000R\u0261\u0001\u0000\u0000\u0000T\u0281\u0001\u0000"+
		"\u0000\u0000V\u0283\u0001\u0000\u0000\u0000X\u028e\u0001\u0000\u0000\u0000"+
		"Z\u02da\u0001\u0000\u0000\u0000\\\u02ee\u0001\u0000\u0000\u0000^\u02f9"+
		"\u0001\u0000\u0000\u0000`\u02fb\u0001\u0000\u0000\u0000b\u0304\u0001\u0000"+
		"\u0000\u0000d\u0306\u0001\u0000\u0000\u0000f\u030f\u0001\u0000\u0000\u0000"+
		"h\u0311\u0001\u0000\u0000\u0000j\u0313\u0001\u0000\u0000\u0000l\u0315"+
		"\u0001\u0000\u0000\u0000n\u0319\u0001\u0000\u0000\u0000p\u031b\u0001\u0000"+
		"\u0000\u0000r\u031d\u0001\u0000\u0000\u0000t\u0327\u0001\u0000\u0000\u0000"+
		"v\u032b\u0001\u0000\u0000\u0000x\u032d\u0001\u0000\u0000\u0000z\u0332"+
		"\u0001\u0000\u0000\u0000|\u0334\u0001\u0000\u0000\u0000~\u0338\u0001\u0000"+
		"\u0000\u0000\u0080\u033a\u0001\u0000\u0000\u0000\u0082\u0084\u0003\u0002"+
		"\u0001\u0000\u0083\u0085\u0005\u0001\u0000\u0000\u0084\u0083\u0001\u0000"+
		"\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000"+
		"\u0000\u0000\u0086\u0087\u0005\u0000\u0000\u0001\u0087\u0001\u0001\u0000"+
		"\u0000\u0000\u0088\u008a\u00050\u0000\u0000\u0089\u008b\u00051\u0000\u0000"+
		"\u008a\u0089\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008d\u0003z=\u0000\u008d\u0092"+
		"\u0003D\"\u0000\u008e\u008f\u0005\u0002\u0000\u0000\u008f\u0091\u0003"+
		"D\"\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0091\u0094\u0001\u0000"+
		"\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000"+
		"\u0000\u0000\u0093\u0095\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000"+
		"\u0000\u0000\u0095\u0096\u0003\u0004\u0002\u0000\u0096\u0099\u0001\u0000"+
		"\u0000\u0000\u0097\u0099\u0003\u0004\u0002\u0000\u0098\u0088\u0001\u0000"+
		"\u0000\u0000\u0098\u0097\u0001\u0000\u0000\u0000\u0099\u0003\u0001\u0000"+
		"\u0000\u0000\u009a\u009d\u0003\u0006\u0003\u0000\u009b\u009d\u0003 \u0010"+
		"\u0000\u009c\u009a\u0001\u0000\u0000\u0000\u009c\u009b\u0001\u0000\u0000"+
		"\u0000\u009d\u0005\u0001\u0000\u0000\u0000\u009e\u00a4\u0003\b\u0004\u0000"+
		"\u009f\u00a4\u0003\n\u0005\u0000\u00a0\u00a4\u0003\u000e\u0007\u0000\u00a1"+
		"\u00a4\u0003\u0012\t\u0000\u00a2\u00a4\u0003\u0018\f\u0000\u00a3\u009e"+
		"\u0001\u0000\u0000\u0000\u00a3\u009f\u0001\u0000\u0000\u0000\u00a3\u00a0"+
		"\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a2"+
		"\u0001\u0000\u0000\u0000\u00a4\u0007\u0001\u0000\u0000\u0000\u00a5\u00a6"+
		"\u0005 \u0000\u0000\u00a6\u00a7\u0003$\u0012\u0000\u00a7\u00a8\u0005!"+
		"\u0000\u0000\u00a8\u00ab\u0003*\u0015\u0000\u00a9\u00aa\u0005\"\u0000"+
		"\u0000\u00aa\u00ac\u0003\\.\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000"+
		"\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000"+
		"\u00ad\u00ae\u0005#\u0000\u0000\u00ae\u00af\u0005$\u0000\u0000\u00af\u00b4"+
		"\u00036\u001b\u0000\u00b0\u00b1\u0005\u0002\u0000\u0000\u00b1\u00b3\u0003"+
		"6\u001b\u0000\u00b2\u00b0\u0001\u0000\u0000\u0000\u00b3\u00b6\u0001\u0000"+
		"\u0000\u0000\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000"+
		"\u0000\u0000\u00b5\u00b7\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000"+
		"\u0000\u0000\u00b7\u00b8\u00050\u0000\u0000\u00b8\u00bb\u00052\u0000\u0000"+
		"\u00b9\u00ba\u0005%\u0000\u0000\u00ba\u00bc\u0003\\.\u0000\u00bb\u00b9"+
		"\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00be"+
		"\u0001\u0000\u0000\u0000\u00bd\u00bf\u0003:\u001d\u0000\u00be\u00bd\u0001"+
		"\u0000\u0000\u0000\u00be\u00bf\u0001\u0000\u0000\u0000\u00bf\u00c1\u0001"+
		"\u0000\u0000\u0000\u00c0\u00c2\u0003>\u001f\u0000\u00c1\u00c0\u0001\u0000"+
		"\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\t\u0001\u0000\u0000"+
		"\u0000\u00c3\u00c4\u0005 \u0000\u0000\u00c4\u00c5\u0003$\u0012\u0000\u00c5"+
		"\u00c6\u0005!\u0000\u0000\u00c6\u00c9\u0003*\u0015\u0000\u00c7\u00c8\u0005"+
		"\"\u0000\u0000\u00c8\u00ca\u0003\\.\u0000\u00c9\u00c7\u0001\u0000\u0000"+
		"\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000"+
		"\u0000\u00cb\u00cc\u0005#\u0000\u0000\u00cc\u00cd\u0005$\u0000\u0000\u00cd"+
		"\u00d2\u00036\u001b\u0000\u00ce\u00cf\u0005\u0002\u0000\u0000\u00cf\u00d1"+
		"\u00036\u001b\u0000\u00d0\u00ce\u0001\u0000\u0000\u0000\u00d1\u00d4\u0001"+
		"\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001"+
		"\u0000\u0000\u0000\u00d3\u00d5\u0001\u0000\u0000\u0000\u00d4\u00d2\u0001"+
		"\u0000\u0000\u0000\u00d5\u00d6\u00050\u0000\u0000\u00d6\u00d7\u00053\u0000"+
		"\u0000\u00d7\u00d8\u0005\u0003\u0000\u0000\u00d8\u00d9\u0003\f\u0006\u0000"+
		"\u00d9\u00dc\u0005\u0004\u0000\u0000\u00da\u00db\u0005%\u0000\u0000\u00db"+
		"\u00dd\u0003\\.\u0000\u00dc\u00da\u0001\u0000\u0000\u0000\u00dc\u00dd"+
		"\u0001\u0000\u0000\u0000\u00dd\u00df\u0001\u0000\u0000\u0000\u00de\u00e0"+
		"\u0003:\u001d\u0000\u00df\u00de\u0001\u0000\u0000\u0000\u00df\u00e0\u0001"+
		"\u0000\u0000\u0000\u00e0\u00e2\u0001\u0000\u0000\u0000\u00e1\u00e3\u0003"+
		">\u001f\u0000\u00e2\u00e1\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000"+
		"\u0000\u0000\u00e3\u000b\u0001\u0000\u0000\u0000\u00e4\u00e9\u0003z=\u0000"+
		"\u00e5\u00e6\u0005\u0002\u0000\u0000\u00e6\u00e8\u0003z=\u0000\u00e7\u00e5"+
		"\u0001\u0000\u0000\u0000\u00e8\u00eb\u0001\u0000\u0000\u0000\u00e9\u00e7"+
		"\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea\r\u0001"+
		"\u0000\u0000\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005"+
		" \u0000\u0000\u00ed\u00ee\u0003$\u0012\u0000\u00ee\u00ef\u0005!\u0000"+
		"\u0000\u00ef\u00f0\u0003*\u0015\u0000\u00f0\u00f1\u0005\"\u0000\u0000"+
		"\u00f1\u00f2\u00054\u0000\u0000\u00f2\u00f3\u0005\u0003\u0000\u0000\u00f3"+
		"\u00f4\u0003\u0010\b\u0000\u00f4\u00ff\u0005\u0004\u0000\u0000\u00f5\u00f6"+
		"\u0005#\u0000\u0000\u00f6\u00f7\u0005$\u0000\u0000\u00f7\u00fc\u00036"+
		"\u001b\u0000\u00f8\u00f9\u0005\u0002\u0000\u0000\u00f9\u00fb\u00036\u001b"+
		"\u0000\u00fa\u00f8\u0001\u0000\u0000\u0000\u00fb\u00fe\u0001\u0000\u0000"+
		"\u0000\u00fc\u00fa\u0001\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000"+
		"\u0000\u00fd\u0100\u0001\u0000\u0000\u0000\u00fe\u00fc\u0001\u0000\u0000"+
		"\u0000\u00ff\u00f5\u0001\u0000\u0000\u0000\u00ff\u0100\u0001\u0000\u0000"+
		"\u0000\u0100\u0103\u0001\u0000\u0000\u0000\u0101\u0102\u0005%\u0000\u0000"+
		"\u0102\u0104\u0003\\.\u0000\u0103\u0101\u0001\u0000\u0000\u0000\u0103"+
		"\u0104\u0001\u0000\u0000\u0000\u0104\u0106\u0001\u0000\u0000\u0000\u0105"+
		"\u0107\u0003:\u001d\u0000\u0106\u0105\u0001\u0000\u0000\u0000\u0106\u0107"+
		"\u0001\u0000\u0000\u0000\u0107\u0109\u0001\u0000\u0000\u0000\u0108\u010a"+
		"\u0003>\u001f\u0000\u0109\u0108\u0001\u0000\u0000\u0000\u0109\u010a\u0001"+
		"\u0000\u0000\u0000\u010a\u000f\u0001\u0000\u0000\u0000\u010b\u010c\u0003"+
		"z=\u0000\u010c\u010d\u0005\u0005\u0000\u0000\u010d\u010e\u0003f3\u0000"+
		"\u010e\u0011\u0001\u0000\u0000\u0000\u010f\u0110\u0005 \u0000\u0000\u0110"+
		"\u0111\u0003$\u0012\u0000\u0111\u0112\u0005!\u0000\u0000\u0112\u0113\u0003"+
		"*\u0015\u0000\u0113\u0114\u0005\"\u0000\u0000\u0114\u0115\u00055\u0000"+
		"\u0000\u0115\u0116\u0005\u0003\u0000\u0000\u0116\u0117\u0003\u0014\n\u0000"+
		"\u0117\u0122\u0005\u0004\u0000\u0000\u0118\u0119\u0005#\u0000\u0000\u0119"+
		"\u011a\u0005$\u0000\u0000\u011a\u011f\u00036\u001b\u0000\u011b\u011c\u0005"+
		"\u0002\u0000\u0000\u011c\u011e\u00036\u001b\u0000\u011d\u011b\u0001\u0000"+
		"\u0000\u0000\u011e\u0121\u0001\u0000\u0000\u0000\u011f\u011d\u0001\u0000"+
		"\u0000\u0000\u011f\u0120\u0001\u0000\u0000\u0000\u0120\u0123\u0001\u0000"+
		"\u0000\u0000\u0121\u011f\u0001\u0000\u0000\u0000\u0122\u0118\u0001\u0000"+
		"\u0000\u0000\u0122\u0123\u0001\u0000\u0000\u0000\u0123\u0126\u0001\u0000"+
		"\u0000\u0000\u0124\u0125\u0005%\u0000\u0000\u0125\u0127\u0003\\.\u0000"+
		"\u0126\u0124\u0001\u0000\u0000\u0000\u0126\u0127\u0001\u0000\u0000\u0000"+
		"\u0127\u0129\u0001\u0000\u0000\u0000\u0128\u012a\u0003:\u001d\u0000\u0129"+
		"\u0128\u0001\u0000\u0000\u0000\u0129\u012a\u0001\u0000\u0000\u0000\u012a"+
		"\u012c\u0001\u0000\u0000\u0000\u012b\u012d\u0003>\u001f\u0000\u012c\u012b"+
		"\u0001\u0000\u0000\u0000\u012c\u012d\u0001\u0000\u0000\u0000\u012d\u0013"+
		"\u0001\u0000\u0000\u0000\u012e\u0133\u0003\u0016\u000b\u0000\u012f\u0130"+
		"\u0005)\u0000\u0000\u0130\u0132\u0003\u0016\u000b\u0000\u0131\u012f\u0001"+
		"\u0000\u0000\u0000\u0132\u0135\u0001\u0000\u0000\u0000\u0133\u0131\u0001"+
		"\u0000\u0000\u0000\u0133\u0134\u0001\u0000\u0000\u0000\u0134\u0015\u0001"+
		"\u0000\u0000\u0000\u0135\u0133\u0001\u0000\u0000\u0000\u0136\u0137\u0003"+
		"z=\u0000\u0137\u0138\u0003P(\u0000\u0138\u0139\u0003f3\u0000\u0139\u0017"+
		"\u0001\u0000\u0000\u0000\u013a\u013b\u0005 \u0000\u0000\u013b\u013c\u0003"+
		"$\u0012\u0000\u013c\u013d\u0005!\u0000\u0000\u013d\u013e\u0003*\u0015"+
		"\u0000\u013e\u013f\u00056\u0000\u0000\u013f\u0140\u0005\u0003\u0000\u0000"+
		"\u0140\u0141\u0003\u001a\r\u0000\u0141\u0142\u0005D\u0000\u0000\u0142"+
		"\u0143\u0003\u001c\u000e\u0000\u0143\u0144\u0005-\u0000\u0000\u0144\u0145"+
		"\u0005\u0003\u0000\u0000\u0145\u0146\u0003\u001e\u000f\u0000\u0146\u0147"+
		"\u0005\u0004\u0000\u0000\u0147\u014a\u0005\u0004\u0000\u0000\u0148\u0149"+
		"\u0005\"\u0000\u0000\u0149\u014b\u0003\\.\u0000\u014a\u0148\u0001\u0000"+
		"\u0000\u0000\u014a\u014b\u0001\u0000\u0000\u0000\u014b\u0156\u0001\u0000"+
		"\u0000\u0000\u014c\u014d\u0005#\u0000\u0000\u014d\u014e\u0005$\u0000\u0000"+
		"\u014e\u0153\u00036\u001b\u0000\u014f\u0150\u0005\u0002\u0000\u0000\u0150"+
		"\u0152\u00036\u001b\u0000\u0151\u014f\u0001\u0000\u0000\u0000\u0152\u0155"+
		"\u0001\u0000\u0000\u0000\u0153\u0151\u0001\u0000\u0000\u0000\u0153\u0154"+
		"\u0001\u0000\u0000\u0000\u0154\u0157\u0001\u0000\u0000\u0000\u0155\u0153"+
		"\u0001\u0000\u0000\u0000\u0156\u014c\u0001\u0000\u0000\u0000\u0156\u0157"+
		"\u0001\u0000\u0000\u0000\u0157\u015a\u0001\u0000\u0000\u0000\u0158\u0159"+
		"\u0005%\u0000\u0000\u0159\u015b\u0003\\.\u0000\u015a\u0158\u0001\u0000"+
		"\u0000\u0000\u015a\u015b\u0001\u0000\u0000\u0000\u015b\u015d\u0001\u0000"+
		"\u0000\u0000\u015c\u015e\u0003:\u001d\u0000\u015d\u015c\u0001\u0000\u0000"+
		"\u0000\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u0160\u0001\u0000\u0000"+
		"\u0000\u015f\u0161\u0003>\u001f\u0000\u0160\u015f\u0001\u0000\u0000\u0000"+
		"\u0160\u0161\u0001\u0000\u0000\u0000\u0161\u0019\u0001\u0000\u0000\u0000"+
		"\u0162\u0163\u0003R)\u0000\u0163\u0164\u0005\u0003\u0000\u0000\u0164\u0165"+
		"\u0003\\.\u0000\u0165\u0166\u0005\u0004\u0000\u0000\u0166\u001b\u0001"+
		"\u0000\u0000\u0000\u0167\u0168\u0003z=\u0000\u0168\u001d\u0001\u0000\u0000"+
		"\u0000\u0169\u016e\u0003f3\u0000\u016a\u016b\u0005\u0002\u0000\u0000\u016b"+
		"\u016d\u0003f3\u0000\u016c\u016a\u0001\u0000\u0000\u0000\u016d\u0170\u0001"+
		"\u0000\u0000\u0000\u016e\u016c\u0001\u0000\u0000\u0000\u016e\u016f\u0001"+
		"\u0000\u0000\u0000\u016f\u001f\u0001\u0000\u0000\u0000\u0170\u016e\u0001"+
		"\u0000\u0000\u0000\u0171\u0173\u0005 \u0000\u0000\u0172\u0174\u0003\""+
		"\u0011\u0000\u0173\u0172\u0001\u0000\u0000\u0000\u0173\u0174\u0001\u0000"+
		"\u0000\u0000\u0174\u0175\u0001\u0000\u0000\u0000\u0175\u0177\u0003$\u0012"+
		"\u0000\u0176\u0178\u0003(\u0014\u0000\u0177\u0176\u0001\u0000\u0000\u0000"+
		"\u0177\u0178\u0001\u0000\u0000\u0000\u0178\u017b\u0001\u0000\u0000\u0000"+
		"\u0179\u017a\u0005\"\u0000\u0000\u017a\u017c\u0003\\.\u0000\u017b\u0179"+
		"\u0001\u0000\u0000\u0000\u017b\u017c\u0001\u0000\u0000\u0000\u017c\u0187"+
		"\u0001\u0000\u0000\u0000\u017d\u017e\u0005#\u0000\u0000\u017e\u017f\u0005"+
		"$\u0000\u0000\u017f\u0184\u00036\u001b\u0000\u0180\u0181\u0005\u0002\u0000"+
		"\u0000\u0181\u0183\u00036\u001b\u0000\u0182\u0180\u0001\u0000\u0000\u0000"+
		"\u0183\u0186\u0001\u0000\u0000\u0000\u0184\u0182\u0001\u0000\u0000\u0000"+
		"\u0184\u0185\u0001\u0000\u0000\u0000\u0185\u0188\u0001\u0000\u0000\u0000"+
		"\u0186\u0184\u0001\u0000\u0000\u0000\u0187\u017d\u0001\u0000\u0000\u0000"+
		"\u0187\u0188\u0001\u0000\u0000\u0000\u0188\u018b\u0001\u0000\u0000\u0000"+
		"\u0189\u018a\u0005%\u0000\u0000\u018a\u018c\u0003\\.\u0000\u018b\u0189"+
		"\u0001\u0000\u0000\u0000\u018b\u018c\u0001\u0000\u0000\u0000\u018c\u018e"+
		"\u0001\u0000\u0000\u0000\u018d\u018f\u0003:\u001d\u0000\u018e\u018d\u0001"+
		"\u0000\u0000\u0000\u018e\u018f\u0001\u0000\u0000\u0000\u018f\u0191\u0001"+
		"\u0000\u0000\u0000\u0190\u0192\u0003>\u001f\u0000\u0191\u0190\u0001\u0000"+
		"\u0000\u0000\u0191\u0192\u0001\u0000\u0000\u0000\u0192!\u0001\u0000\u0000"+
		"\u0000\u0193\u0194\u0007\u0000\u0000\u0000\u0194#\u0001\u0000\u0000\u0000"+
		"\u0195\u0198\u0005\u0006\u0000\u0000\u0196\u0198\u0003&\u0013\u0000\u0197"+
		"\u0195\u0001\u0000\u0000\u0000\u0197\u0196\u0001\u0000\u0000\u0000\u0198"+
		"\u019d\u0001\u0000\u0000\u0000\u0199\u019a\u0005\u0002\u0000\u0000\u019a"+
		"\u019c\u0003&\u0013\u0000\u019b\u0199\u0001\u0000\u0000\u0000\u019c\u019f"+
		"\u0001\u0000\u0000\u0000\u019d\u019b\u0001\u0000\u0000\u0000\u019d\u019e"+
		"\u0001\u0000\u0000\u0000\u019e%\u0001\u0000\u0000\u0000\u019f\u019d\u0001"+
		"\u0000\u0000\u0000\u01a0\u01a5\u0003\\.\u0000\u01a1\u01a3\u0005/\u0000"+
		"\u0000\u01a2\u01a1\u0001\u0000\u0000\u0000\u01a2\u01a3\u0001\u0000\u0000"+
		"\u0000\u01a3\u01a4\u0001\u0000\u0000\u0000\u01a4\u01a6\u0003z=\u0000\u01a5"+
		"\u01a2\u0001\u0000\u0000\u0000\u01a5\u01a6\u0001\u0000\u0000\u0000\u01a6"+
		"\'\u0001\u0000\u0000\u0000\u01a7\u01a8\u0005!\u0000\u0000\u01a8\u01a9"+
		"\u0003*\u0015\u0000\u01a9)\u0001\u0000\u0000\u0000\u01aa\u01af\u0003,"+
		"\u0016\u0000\u01ab\u01ac\u0005\u0002\u0000\u0000\u01ac\u01ae\u0003,\u0016"+
		"\u0000\u01ad\u01ab\u0001\u0000\u0000\u0000\u01ae\u01b1\u0001\u0000\u0000"+
		"\u0000\u01af\u01ad\u0001\u0000\u0000\u0000\u01af\u01b0\u0001\u0000\u0000"+
		"\u0000\u01b0+\u0001\u0000\u0000\u0000\u01b1\u01af\u0001\u0000\u0000\u0000"+
		"\u01b2\u01b6\u0003.\u0017\u0000\u01b3\u01b5\u00030\u0018\u0000\u01b4\u01b3"+
		"\u0001\u0000\u0000\u0000\u01b5\u01b8\u0001\u0000\u0000\u0000\u01b6\u01b4"+
		"\u0001\u0000\u0000\u0000\u01b6\u01b7\u0001\u0000\u0000\u0000\u01b7\u01c4"+
		"\u0001\u0000\u0000\u0000\u01b8\u01b6\u0001\u0000\u0000\u0000\u01b9\u01ba"+
		"\u0005\u0003\u0000\u0000\u01ba\u01be\u0003.\u0017\u0000\u01bb\u01bd\u0003"+
		"0\u0018\u0000\u01bc\u01bb\u0001\u0000\u0000\u0000\u01bd\u01c0\u0001\u0000"+
		"\u0000\u0000\u01be\u01bc\u0001\u0000\u0000\u0000\u01be\u01bf\u0001\u0000"+
		"\u0000\u0000\u01bf\u01c1\u0001\u0000\u0000\u0000\u01c0\u01be\u0001\u0000"+
		"\u0000\u0000\u01c1\u01c2\u0005\u0004\u0000\u0000\u01c2\u01c4\u0001\u0000"+
		"\u0000\u0000\u01c3\u01b2\u0001\u0000\u0000\u0000\u01c3\u01b9\u0001\u0000"+
		"\u0000\u0000\u01c4-\u0001\u0000\u0000\u0000\u01c5\u01ca\u0003t:\u0000"+
		"\u01c6\u01c8\u0005/\u0000\u0000\u01c7\u01c6\u0001\u0000\u0000\u0000\u01c7"+
		"\u01c8\u0001\u0000\u0000\u0000\u01c8\u01c9\u0001\u0000\u0000\u0000\u01c9"+
		"\u01cb\u0003z=\u0000\u01ca\u01c7\u0001\u0000\u0000\u0000\u01ca\u01cb\u0001"+
		"\u0000\u0000\u0000\u01cb\u01dd\u0001\u0000\u0000\u0000\u01cc\u01d2\u0003"+
		"\u0002\u0001\u0000\u01cd\u01ce\u0005\u0003\u0000\u0000\u01ce\u01cf\u0003"+
		"\u0002\u0001\u0000\u01cf\u01d0\u0005\u0004\u0000\u0000\u01d0\u01d2\u0001"+
		"\u0000\u0000\u0000\u01d1\u01cc\u0001\u0000\u0000\u0000\u01d1\u01cd\u0001"+
		"\u0000\u0000\u0000\u01d2\u01d4\u0001\u0000\u0000\u0000\u01d3\u01d5\u0005"+
		"/\u0000\u0000\u01d4\u01d3\u0001\u0000\u0000\u0000\u01d4\u01d5\u0001\u0000"+
		"\u0000\u0000\u01d5\u01d6\u0001\u0000\u0000\u0000\u01d6\u01d7\u0003z=\u0000"+
		"\u01d7\u01dd\u0001\u0000\u0000\u0000\u01d8\u01d9\u0005\u0003\u0000\u0000"+
		"\u01d9\u01da\u0003*\u0015\u0000\u01da\u01db\u0005\u0004\u0000\u0000\u01db"+
		"\u01dd\u0001\u0000\u0000\u0000\u01dc\u01c5\u0001\u0000\u0000\u0000\u01dc"+
		"\u01d1\u0001\u0000\u0000\u0000\u01dc\u01d8\u0001\u0000\u0000\u0000\u01dd"+
		"/\u0001\u0000\u0000\u0000\u01de\u01df\u00032\u0019\u0000\u01df\u01e0\u0005"+
		"7\u0000\u0000\u01e0\u01e1\u0003.\u0017\u0000\u01e1\u01e2\u0005\u0019\u0000"+
		"\u0000\u01e2\u01e3\u0003r9\u0000\u01e3\u01e4\u0005\u0005\u0000\u0000\u01e4"+
		"\u01e5\u0003r9\u0000\u01e51\u0001\u0000\u0000\u0000\u01e6\u01e7\u0007"+
		"\u0001\u0000\u0000\u01e73\u0001\u0000\u0000\u0000\u01e8\u01ed\u0003z="+
		"\u0000\u01e9\u01ea\u0005\u0002\u0000\u0000\u01ea\u01ec\u0003z=\u0000\u01eb"+
		"\u01e9\u0001\u0000\u0000\u0000\u01ec\u01ef\u0001\u0000\u0000\u0000\u01ed"+
		"\u01eb\u0001\u0000\u0000\u0000\u01ed\u01ee\u0001\u0000\u0000\u0000\u01ee"+
		"5\u0001\u0000\u0000\u0000\u01ef\u01ed\u0001\u0000\u0000\u0000\u01f0\u01f2"+
		"\u0003\\.\u0000\u01f1\u01f3\u0007\u0002\u0000\u0000\u01f2\u01f1\u0001"+
		"\u0000\u0000\u0000\u01f2\u01f3\u0001\u0000\u0000\u0000\u01f37\u0001\u0000"+
		"\u0000\u0000\u01f4\u01f5\u0003\\.\u0000\u01f59\u0001\u0000\u0000\u0000"+
		"\u01f6\u01f7\u0005&\u0000\u0000\u01f7\u01f8\u0005$\u0000\u0000\u01f8\u01fd"+
		"\u0003<\u001e\u0000\u01f9\u01fa\u0005\u0002\u0000\u0000\u01fa\u01fc\u0003"+
		"<\u001e\u0000\u01fb\u01f9\u0001\u0000\u0000\u0000\u01fc\u01ff\u0001\u0000"+
		"\u0000\u0000\u01fd\u01fb\u0001\u0000\u0000\u0000\u01fd\u01fe\u0001\u0000"+
		"\u0000\u0000\u01fe;\u0001\u0000\u0000\u0000\u01ff\u01fd\u0001\u0000\u0000"+
		"\u0000\u0200\u0202\u0003\\.\u0000\u0201\u0203\u0007\u0002\u0000\u0000"+
		"\u0202\u0201\u0001\u0000\u0000\u0000\u0202\u0203\u0001\u0000\u0000\u0000"+
		"\u0203=\u0001\u0000\u0000\u0000\u0204\u0205\u0005\'\u0000\u0000\u0205"+
		"\u0209\u0003@ \u0000\u0206\u0207\u0005\'\u0000\u0000\u0207\u0209\u0003"+
		"B!\u0000\u0208\u0204\u0001\u0000\u0000\u0000\u0208\u0206\u0001\u0000\u0000"+
		"\u0000\u0209?\u0001\u0000\u0000\u0000\u020a\u020b\u0003\\.\u0000\u020b"+
		"A\u0001\u0000\u0000\u0000\u020c\u020d\u0003\\.\u0000\u020d\u020e\u0005"+
		"\u0002\u0000\u0000\u020e\u020f\u0003\\.\u0000\u020fC\u0001\u0000\u0000"+
		"\u0000\u0210\u0212\u0003z=\u0000\u0211\u0213\u0003F#\u0000\u0212\u0211"+
		"\u0001\u0000\u0000\u0000\u0212\u0213\u0001\u0000\u0000\u0000\u0213\u0214"+
		"\u0001\u0000\u0000\u0000\u0214\u0215\u0005/\u0000\u0000\u0215\u0216\u0005"+
		"\u0003\u0000\u0000\u0216\u0217\u0003\u0002\u0001\u0000\u0217\u0218\u0005"+
		"\u0004\u0000\u0000\u0218\u0228\u0001\u0000\u0000\u0000\u0219\u021b\u0003"+
		"z=\u0000\u021a\u021c\u0003F#\u0000\u021b\u021a\u0001\u0000\u0000\u0000"+
		"\u021b\u021c\u0001\u0000\u0000\u0000\u021c\u021d\u0001\u0000\u0000\u0000"+
		"\u021d\u021e\u0005/\u0000\u0000\u021e\u021f\u0005\u0003\u0000\u0000\u021f"+
		"\u0220\u0003H$\u0000\u0220\u0222\u0005E\u0000\u0000\u0221\u0223\u0005"+
		"F\u0000\u0000\u0222\u0221\u0001\u0000\u0000\u0000\u0222\u0223\u0001\u0000"+
		"\u0000\u0000\u0223\u0224\u0001\u0000\u0000\u0000\u0224\u0225\u0003J%\u0000"+
		"\u0225\u0226\u0005\u0004\u0000\u0000\u0226\u0228\u0001\u0000\u0000\u0000"+
		"\u0227\u0210\u0001\u0000\u0000\u0000\u0227\u0219\u0001\u0000\u0000\u0000"+
		"\u0228E\u0001\u0000\u0000\u0000\u0229\u022a\u0005\u0003\u0000\u0000\u022a"+
		"\u022f\u0003z=\u0000\u022b\u022c\u0005\u0002\u0000\u0000\u022c\u022e\u0003"+
		"z=\u0000\u022d\u022b\u0001\u0000\u0000\u0000\u022e\u0231\u0001\u0000\u0000"+
		"\u0000\u022f\u022d\u0001\u0000\u0000\u0000\u022f\u0230\u0001\u0000\u0000"+
		"\u0000\u0230\u0232\u0001\u0000\u0000\u0000\u0231\u022f\u0001\u0000\u0000"+
		"\u0000\u0232\u0233\u0005\u0004\u0000\u0000\u0233G\u0001\u0000\u0000\u0000"+
		"\u0234\u0235\u0003\u0002\u0001\u0000\u0235I\u0001\u0000\u0000\u0000\u0236"+
		"\u0237\u0003\u0002\u0001\u0000\u0237K\u0001\u0000\u0000\u0000\u0238\u023d"+
		"\u0003\\.\u0000\u0239\u023a\u0005\u0002\u0000\u0000\u023a\u023c\u0003"+
		"\\.\u0000\u023b\u0239\u0001\u0000\u0000\u0000\u023c\u023f\u0001\u0000"+
		"\u0000\u0000\u023d\u023b\u0001\u0000\u0000\u0000\u023d\u023e\u0001\u0000"+
		"\u0000\u0000\u023eM\u0001\u0000\u0000\u0000\u023f\u023d\u0001\u0000\u0000"+
		"\u0000\u0240\u0241\u0003z=\u0000\u0241\u0243\u0005\u0003\u0000\u0000\u0242"+
		"\u0244\u0003T*\u0000\u0243\u0242\u0001\u0000\u0000\u0000\u0243\u0244\u0001"+
		"\u0000\u0000\u0000\u0244\u0245\u0001\u0000\u0000\u0000\u0245\u0246\u0005"+
		"\u0004\u0000\u0000\u0246O\u0001\u0000\u0000\u0000\u0247\u0260\u0005\u0005"+
		"\u0000\u0000\u0248\u0260\u0005\u0007\u0000\u0000\u0249\u0260\u0005\b\u0000"+
		"\u0000\u024a\u0260\u0005\t\u0000\u0000\u024b\u0260\u0005\n\u0000\u0000"+
		"\u024c\u0260\u0005\u000b\u0000\u0000\u024d\u0260\u0005\f\u0000\u0000\u024e"+
		"\u0260\u0005\r\u0000\u0000\u024f\u0251\u0005\u001b\u0000\u0000\u0250\u0252"+
		"\u0005,\u0000\u0000\u0251\u0250\u0001\u0000\u0000\u0000\u0251\u0252\u0001"+
		"\u0000\u0000\u0000\u0252\u0260\u0001\u0000\u0000\u0000\u0253\u0255\u0005"+
		",\u0000\u0000\u0254\u0253\u0001\u0000\u0000\u0000\u0254\u0255\u0001\u0000"+
		"\u0000\u0000\u0255\u0256\u0001\u0000\u0000\u0000\u0256\u0260\u0005\u001a"+
		"\u0000\u0000\u0257\u0259\u0005,\u0000\u0000\u0258\u0257\u0001\u0000\u0000"+
		"\u0000\u0258\u0259\u0001\u0000\u0000\u0000\u0259\u025a\u0001\u0000\u0000"+
		"\u0000\u025a\u0260\u0005-\u0000\u0000\u025b\u025d\u0005,\u0000\u0000\u025c"+
		"\u025b\u0001\u0000\u0000\u0000\u025c\u025d\u0001\u0000\u0000\u0000\u025d"+
		"\u025e\u0001\u0000\u0000\u0000\u025e\u0260\u0005.\u0000\u0000\u025f\u0247"+
		"\u0001\u0000\u0000\u0000\u025f\u0248\u0001\u0000\u0000\u0000\u025f\u0249"+
		"\u0001\u0000\u0000\u0000\u025f\u024a\u0001\u0000\u0000\u0000\u025f\u024b"+
		"\u0001\u0000\u0000\u0000\u025f\u024c\u0001\u0000\u0000\u0000\u025f\u024d"+
		"\u0001\u0000\u0000\u0000\u025f\u024e\u0001\u0000\u0000\u0000\u025f\u024f"+
		"\u0001\u0000\u0000\u0000\u025f\u0254\u0001\u0000\u0000\u0000\u025f\u0258"+
		"\u0001\u0000\u0000\u0000\u025f\u025c\u0001\u0000\u0000\u0000\u0260Q\u0001"+
		"\u0000\u0000\u0000\u0261\u0262\u0007\u0003\u0000\u0000\u0262S\u0001\u0000"+
		"\u0000\u0000\u0263\u0268\u0003f3\u0000\u0264\u0268\u0003r9\u0000\u0265"+
		"\u0268\u0003N\'\u0000\u0266\u0268\u0003\\.\u0000\u0267\u0263\u0001\u0000"+
		"\u0000\u0000\u0267\u0264\u0001\u0000\u0000\u0000\u0267\u0265\u0001\u0000"+
		"\u0000\u0000\u0267\u0266\u0001\u0000\u0000\u0000\u0268\u0272\u0001\u0000"+
		"\u0000\u0000\u0269\u026e\u0005\u0002\u0000\u0000\u026a\u026f\u0003f3\u0000"+
		"\u026b\u026f\u0003r9\u0000\u026c\u026f\u0003N\'\u0000\u026d\u026f\u0003"+
		"\\.\u0000\u026e\u026a\u0001\u0000\u0000\u0000\u026e\u026b\u0001\u0000"+
		"\u0000\u0000\u026e\u026c\u0001\u0000\u0000\u0000\u026e\u026d\u0001\u0000"+
		"\u0000\u0000\u026f\u0271\u0001\u0000\u0000\u0000\u0270\u0269\u0001\u0000"+
		"\u0000\u0000\u0271\u0274\u0001\u0000\u0000\u0000\u0272\u0270\u0001\u0000"+
		"\u0000\u0000\u0272\u0273\u0001\u0000\u0000\u0000\u0273\u0282\u0001\u0000"+
		"\u0000\u0000\u0274\u0272\u0001\u0000\u0000\u0000\u0275\u0282\u0005\u0006"+
		"\u0000\u0000\u0276\u0278\u0005G\u0000\u0000\u0277\u0276\u0001\u0000\u0000"+
		"\u0000\u0277\u0278\u0001\u0000\u0000\u0000\u0278\u0279\u0001\u0000\u0000"+
		"\u0000\u0279\u027e\u0003V+\u0000\u027a\u027b\u0005\u0002\u0000\u0000\u027b"+
		"\u027d\u0003V+\u0000\u027c\u027a\u0001\u0000\u0000\u0000\u027d\u0280\u0001"+
		"\u0000\u0000\u0000\u027e\u027c\u0001\u0000\u0000\u0000\u027e\u027f\u0001"+
		"\u0000\u0000\u0000\u027f\u0282\u0001\u0000\u0000\u0000\u0280\u027e\u0001"+
		"\u0000\u0000\u0000\u0281\u0267\u0001\u0000\u0000\u0000\u0281\u0275\u0001"+
		"\u0000\u0000\u0000\u0281\u0277\u0001\u0000\u0000\u0000\u0282U\u0001\u0000"+
		"\u0000\u0000\u0283\u0284\u0003\\.\u0000\u0284W\u0001\u0000\u0000\u0000"+
		"\u0285\u0286\u0006,\uffff\uffff\u0000\u0286\u028f\u0003Z-\u0000\u0287"+
		"\u0288\u0005\u010e\u0000\u0000\u0288\u028a\u0005\u010f\u0000\u0000\u0289"+
		"\u0287\u0001\u0000\u0000\u0000\u0289\u028a\u0001\u0000\u0000\u0000\u028a"+
		"\u028b\u0001\u0000\u0000\u0000\u028b\u028f\u0003Z-\u0000\u028c\u028d\u0005"+
		"\u001d\u0000\u0000\u028d\u028f\u0003\\.\u0000\u028e\u0285\u0001\u0000"+
		"\u0000\u0000\u028e\u0289\u0001\u0000\u0000\u0000\u028e\u028c\u0001\u0000"+
		"\u0000\u0000\u028f\u02c1\u0001\u0000\u0000\u0000\u0290\u0291\n\u0007\u0000"+
		"\u0000\u0291\u0292\u0003d2\u0000\u0292\u0293\u0003X,\b\u0293\u02c0\u0001"+
		"\u0000\u0000\u0000\u0294\u0296\n\u0006\u0000\u0000\u0295\u0297\u0005,"+
		"\u0000\u0000\u0296\u0295\u0001\u0000\u0000\u0000\u0296\u0297\u0001\u0000"+
		"\u0000\u0000\u0297\u0298\u0001\u0000\u0000\u0000\u0298\u0299\u0005.\u0000"+
		"\u0000\u0299\u029a\u0003X,\u0000\u029a\u029b\u0005)\u0000\u0000\u029b"+
		"\u029c\u0003X,\u0007\u029c\u02c0\u0001\u0000\u0000\u0000\u029d\u029f\n"+
		"\u0003\u0000\u0000\u029e\u02a0\u0005,\u0000\u0000\u029f\u029e\u0001\u0000"+
		"\u0000\u0000\u029f\u02a0\u0001\u0000\u0000\u0000\u02a0\u02a1\u0001\u0000"+
		"\u0000\u0000\u02a1\u02a2\u0007\u0004\u0000\u0000\u02a2\u02c0\u0003X,\u0004"+
		"\u02a3\u02a4\n\b\u0000\u0000\u02a4\u02a6\u0005\u001b\u0000\u0000\u02a5"+
		"\u02a7\u0005,\u0000\u0000\u02a6\u02a5\u0001\u0000\u0000\u0000\u02a6\u02a7"+
		"\u0001\u0000\u0000\u0000\u02a7\u02a8\u0001\u0000\u0000\u0000\u02a8\u02c0"+
		"\u0005\u001c\u0000\u0000\u02a9\u02ab\n\u0005\u0000\u0000\u02aa\u02ac\u0005"+
		",\u0000\u0000\u02ab\u02aa\u0001\u0000\u0000\u0000\u02ab\u02ac\u0001\u0000"+
		"\u0000\u0000\u02ac\u02ad\u0001\u0000\u0000\u0000\u02ad\u02ae\u0005-\u0000"+
		"\u0000\u02ae\u02b1\u0005\u0003\u0000\u0000\u02af\u02b2\u0003\u0002\u0001"+
		"\u0000\u02b0\u02b2\u0003L&\u0000\u02b1\u02af\u0001\u0000\u0000\u0000\u02b1"+
		"\u02b0\u0001\u0000\u0000\u0000\u02b2\u02b3\u0001\u0000\u0000\u0000\u02b3"+
		"\u02b4\u0005\u0004\u0000\u0000\u02b4\u02c0\u0001\u0000\u0000\u0000\u02b5"+
		"\u02b7\n\u0004\u0000\u0000\u02b6\u02b8\u0005,\u0000\u0000\u02b7\u02b6"+
		"\u0001\u0000\u0000\u0000\u02b7\u02b8\u0001\u0000\u0000\u0000\u02b8\u02b9"+
		"\u0001\u0000\u0000\u0000\u02b9\u02ba\u0005\u001a\u0000\u0000\u02ba\u02bd"+
		"\u0003X,\u0000\u02bb\u02bc\u0005\u0112\u0000\u0000\u02bc\u02be\u0005\u0105"+
		"\u0000\u0000\u02bd\u02bb\u0001\u0000\u0000\u0000\u02bd\u02be\u0001\u0000"+
		"\u0000\u0000\u02be\u02c0\u0001\u0000\u0000\u0000\u02bf\u0290\u0001\u0000"+
		"\u0000\u0000\u02bf\u0294\u0001\u0000\u0000\u0000\u02bf\u029d\u0001\u0000"+
		"\u0000\u0000\u02bf\u02a3\u0001\u0000\u0000\u0000\u02bf\u02a9\u0001\u0000"+
		"\u0000\u0000\u02bf\u02b5\u0001\u0000\u0000\u0000\u02c0\u02c3\u0001\u0000"+
		"\u0000\u0000\u02c1\u02bf\u0001\u0000\u0000\u0000\u02c1\u02c2\u0001\u0000"+
		"\u0000\u0000\u02c2Y\u0001\u0000\u0000\u0000\u02c3\u02c1\u0001\u0000\u0000"+
		"\u0000\u02c4\u02c5\u0006-\uffff\uffff\u0000\u02c5\u02db\u0003f3\u0000"+
		"\u02c6\u02db\u0003r9\u0000\u02c7\u02db\u0003N\'\u0000\u02c8\u02c9\u0005"+
		"\u0003\u0000\u0000\u02c9\u02ce\u0003\\.\u0000\u02ca\u02cb\u0005\u0002"+
		"\u0000\u0000\u02cb\u02cd\u0003\\.\u0000\u02cc\u02ca\u0001\u0000\u0000"+
		"\u0000\u02cd\u02d0\u0001\u0000\u0000\u0000\u02ce\u02cc\u0001\u0000\u0000"+
		"\u0000\u02ce\u02cf\u0001\u0000\u0000\u0000\u02cf\u02d1\u0001\u0000\u0000"+
		"\u0000\u02d0\u02ce\u0001\u0000\u0000\u0000\u02d1\u02d2\u0005\u0004\u0000"+
		"\u0000\u02d2\u02db\u0001\u0000\u0000\u0000\u02d3\u02d4\u0005\u0003\u0000"+
		"\u0000\u02d4\u02d5\u0003\u0002\u0001\u0000\u02d5\u02d6\u0005\u0004\u0000"+
		"\u0000\u02d6\u02db\u0001\u0000\u0000\u0000\u02d7\u02d8\u0003`0\u0000\u02d8"+
		"\u02d9\u0003Z-\u0001\u02d9\u02db\u0001\u0000\u0000\u0000\u02da\u02c4\u0001"+
		"\u0000\u0000\u0000\u02da\u02c6\u0001\u0000\u0000\u0000\u02da\u02c7\u0001"+
		"\u0000\u0000\u0000\u02da\u02c8\u0001\u0000\u0000\u0000\u02da\u02d3\u0001"+
		"\u0000\u0000\u0000\u02da\u02d7\u0001\u0000\u0000\u0000\u02db\u02e2\u0001"+
		"\u0000\u0000\u0000\u02dc\u02dd\n\u0002\u0000\u0000\u02dd\u02de\u0003^"+
		"/\u0000\u02de\u02df\u0003Z-\u0003\u02df\u02e1\u0001\u0000\u0000\u0000"+
		"\u02e0\u02dc\u0001\u0000\u0000\u0000\u02e1\u02e4\u0001\u0000\u0000\u0000"+
		"\u02e2\u02e0\u0001\u0000\u0000\u0000\u02e2\u02e3\u0001\u0000\u0000\u0000"+
		"\u02e3[\u0001\u0000\u0000\u0000\u02e4\u02e2\u0001\u0000\u0000\u0000\u02e5"+
		"\u02e6\u0006.\uffff\uffff\u0000\u02e6\u02e7\u0007\u0005\u0000\u0000\u02e7"+
		"\u02ef\u0003\\.\u0005\u02e8\u02ef\u0003X,\u0000\u02e9\u02ef\u0003 \u0010"+
		"\u0000\u02ea\u02eb\u0005\u0003\u0000\u0000\u02eb\u02ec\u0003\\.\u0000"+
		"\u02ec\u02ed\u0005\u0004\u0000\u0000\u02ed\u02ef\u0001\u0000\u0000\u0000"+
		"\u02ee\u02e5\u0001\u0000\u0000\u0000\u02ee\u02e8\u0001\u0000\u0000\u0000"+
		"\u02ee\u02e9\u0001\u0000\u0000\u0000\u02ee\u02ea\u0001\u0000\u0000\u0000"+
		"\u02ef\u02f6\u0001\u0000\u0000\u0000\u02f0\u02f1\n\u0004\u0000\u0000\u02f1"+
		"\u02f2\u0003b1\u0000\u02f2\u02f3\u0003\\.\u0005\u02f3\u02f5\u0001\u0000"+
		"\u0000\u0000\u02f4\u02f0\u0001\u0000\u0000\u0000\u02f5\u02f8\u0001\u0000"+
		"\u0000\u0000\u02f6\u02f4\u0001\u0000\u0000\u0000\u02f6\u02f7\u0001\u0000"+
		"\u0000\u0000\u02f7]\u0001\u0000\u0000\u0000\u02f8\u02f6\u0001\u0000\u0000"+
		"\u0000\u02f9\u02fa\u0007\u0006\u0000\u0000\u02fa_\u0001\u0000\u0000\u0000"+
		"\u02fb\u02fc\u0007\u0007\u0000\u0000\u02fca\u0001\u0000\u0000\u0000\u02fd"+
		"\u0305\u0005)\u0000\u0000\u02fe\u02ff\u0005\u0015\u0000\u0000\u02ff\u0305"+
		"\u0005\u0015\u0000\u0000\u0300\u0305\u0005+\u0000\u0000\u0301\u0305\u0005"+
		"*\u0000\u0000\u0302\u0303\u0005\u0016\u0000\u0000\u0303\u0305\u0005\u0016"+
		"\u0000\u0000\u0304\u02fd\u0001\u0000\u0000\u0000\u0304\u02fe\u0001\u0000"+
		"\u0000\u0000\u0304\u0300\u0001\u0000\u0000\u0000\u0304\u0301\u0001\u0000"+
		"\u0000\u0000\u0304\u0302\u0001\u0000\u0000\u0000\u0305c\u0001\u0000\u0000"+
		"\u0000\u0306\u0307\u0007\b\u0000\u0000\u0307e\u0001\u0000\u0000\u0000"+
		"\u0308\u0310\u0003|>\u0000\u0309\u0310\u0003h4\u0000\u030a\u030b\u0005"+
		"\u0012\u0000\u0000\u030b\u0310\u0003h4\u0000\u030c\u0310\u0003p8\u0000"+
		"\u030d\u0310\u0003j5\u0000\u030e\u0310\u0003l6\u0000\u030f\u0308\u0001"+
		"\u0000\u0000\u0000\u030f\u0309\u0001\u0000\u0000\u0000\u030f\u030a\u0001"+
		"\u0000\u0000\u0000\u030f\u030c\u0001\u0000\u0000\u0000\u030f\u030d\u0001"+
		"\u0000\u0000\u0000\u030f\u030e\u0001\u0000\u0000\u0000\u0310g\u0001\u0000"+
		"\u0000\u0000\u0311\u0312\u0005\u0106\u0000\u0000\u0312i\u0001\u0000\u0000"+
		"\u0000\u0313\u0314\u0005\u001c\u0000\u0000\u0314k\u0001\u0000\u0000\u0000"+
		"\u0315\u0316\u0003|>\u0000\u0316\u0317\u0005\u0017\u0000\u0000\u0317\u0318"+
		"\u0003n7\u0000\u0318m\u0001\u0000\u0000\u0000\u0319\u031a\u0003|>\u0000"+
		"\u031ao\u0001\u0000\u0000\u0000\u031b\u031c\u0007\t\u0000\u0000\u031c"+
		"q\u0001\u0000\u0000\u0000\u031d\u0322\u0003z=\u0000\u031e\u0320\u0003"+
		"x<\u0000\u031f\u0321\u0003x<\u0000\u0320\u031f\u0001\u0000\u0000\u0000"+
		"\u0320\u0321\u0001\u0000\u0000\u0000\u0321\u0323\u0001\u0000\u0000\u0000"+
		"\u0322\u031e\u0001\u0000\u0000\u0000\u0322\u0323\u0001\u0000\u0000\u0000"+
		"\u0323s\u0001\u0000\u0000\u0000\u0324\u0325\u0003z=\u0000\u0325\u0326"+
		"\u0005\u0018\u0000\u0000\u0326\u0328\u0001\u0000\u0000\u0000\u0327\u0324"+
		"\u0001\u0000\u0000\u0000\u0327\u0328\u0001\u0000\u0000\u0000\u0328\u0329"+
		"\u0001\u0000\u0000\u0000\u0329\u032a\u0003z=\u0000\u032au\u0001\u0000"+
		"\u0000\u0000\u032b\u032c\u0003z=\u0000\u032cw\u0001\u0000\u0000\u0000"+
		"\u032d\u032e\u0005\u0018\u0000\u0000\u032e\u032f\u0003z=\u0000\u032fy"+
		"\u0001\u0000\u0000\u0000\u0330\u0333\u0003~?\u0000\u0331\u0333\u0003|"+
		">\u0000\u0332\u0330\u0001\u0000\u0000\u0000\u0332\u0331\u0001\u0000\u0000"+
		"\u0000\u0333{\u0001\u0000\u0000\u0000\u0334\u0335\u0005\u0105\u0000\u0000"+
		"\u0335}\u0001\u0000\u0000\u0000\u0336\u0339\u0005\u0104\u0000\u0000\u0337"+
		"\u0339\u0003\u0080@\u0000\u0338\u0336\u0001\u0000\u0000\u0000\u0338\u0337"+
		"\u0001\u0000\u0000\u0000\u0339\u007f\u0001\u0000\u0000\u0000\u033a\u033b"+
		"\u0007\n\u0000\u0000\u033b\u0081\u0001\u0000\u0000\u0000f\u0084\u008a"+
		"\u0092\u0098\u009c\u00a3\u00ab\u00b4\u00bb\u00be\u00c1\u00c9\u00d2\u00dc"+
		"\u00df\u00e2\u00e9\u00fc\u00ff\u0103\u0106\u0109\u011f\u0122\u0126\u0129"+
		"\u012c\u0133\u014a\u0153\u0156\u015a\u015d\u0160\u016e\u0173\u0177\u017b"+
		"\u0184\u0187\u018b\u018e\u0191\u0197\u019d\u01a2\u01a5\u01af\u01b6\u01be"+
		"\u01c3\u01c7\u01ca\u01d1\u01d4\u01dc\u01ed\u01f2\u01fd\u0202\u0208\u0212"+
		"\u021b\u0222\u0227\u022f\u023d\u0243\u0251\u0254\u0258\u025c\u025f\u0267"+
		"\u026e\u0272\u0277\u027e\u0281\u0289\u028e\u0296\u029f\u02a6\u02ab\u02b1"+
		"\u02b7\u02bd\u02bf\u02c1\u02ce\u02da\u02e2\u02ee\u02f6\u0304\u030f\u0320"+
		"\u0322\u0327\u0332\u0338";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}