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
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, ON=24, IS=25, 
		NULL=26, EXISTS=27, DESC=28, ASC=29, SELECT=30, FROM=31, WHERE=32, GROUP=33, 
		BY=34, HAVING=35, ORDER=36, LIMIT=37, OFFSET=38, AND=39, OR=40, XOR=41, 
		NOT=42, IN=43, BETWEEN=44, AS=45, WITH=46, RECURSIVE=47, ROLLUP=48, DRILLDOWN=49, 
		SLICE=50, DICE=51, PIVOT=52, JOIN=53, INNER=54, OUTER=55, LEFT=56, RIGHT=57, 
		FULL=58, CROSS=59, NATURAL=60, USE=61, FORCE=62, IGNORE=63, INDEX=64, 
		KEY=65, FOR=66, ALL=67, DISTINCT=68, DISTINCTROW=69, HIGH_PRIORITY=70, 
		STRAIGHT_JOIN=71, SQL_SMALL_RESULT=72, SQL_BIG_RESULT=73, SQL_BUFFER_RESULT=74, 
		SQL_CACHE=75, SQL_NO_CACHE=76, SQL_CALC_FOUND_ROWS=77, TRUE=78, FALSE=79, 
		NULL_LITERAL=80, AVG=81, MAX=82, MIN=83, SUM=84, COUNT=85, GROUP_CONCAT=86, 
		ABS=87, ACOS=88, ADDDATE=89, ADDTIME=90, AES_DECRYPT=91, AES_ENCRYPT=92, 
		ASCII=93, ASIN=94, ATAN=95, ATAN2=96, BENCHMARK=97, BIN=98, BIT_COUNT=99, 
		BIT_LENGTH=100, CEIL=101, CEILING=102, CHAR=103, CHAR_LENGTH=104, CHARACTER_LENGTH=105, 
		COALESCE=106, COS=107, COT=108, CRC32=109, CURDATE=110, CURRENT_DATE=111, 
		CURRENT_TIME=112, CURRENT_TIMESTAMP=113, CURRENT_USER=114, CURTIME=115, 
		DATABASE=116, DATE=117, DATEDIFF=118, DATE_ADD=119, DATE_FORMAT=120, DATE_SUB=121, 
		DAY=122, DAYNAME=123, DAYOFMONTH=124, DAYOFWEEK=125, DAYOFYEAR=126, DECODE=127, 
		DEFAULT=128, DEGREES=129, DES_DECRYPT=130, DES_ENCRYPT=131, ELT=132, ENCODE=133, 
		ENCRYPT=134, EXP=135, EXTRACT=136, FIELD=137, FIND_IN_SET=138, FLOOR=139, 
		FORMAT=140, FOUND_ROWS=141, FROM_DAYS=142, FROM_UNIXTIME=143, GET_FORMAT=144, 
		GET_LOCK=145, GREATEST=146, HEX=147, HOUR=148, IF=149, IFNULL=150, INET_ATON=151, 
		INET_NTOA=152, INSERT=153, INSTR=154, IS_FREE_LOCK=155, IS_USED_LOCK=156, 
		LAST_INSERT_ID=157, LCASE=158, LEAST=159, LENGTH=160, LN=161, LOAD_FILE=162, 
		LOCALTIME=163, LOCALTIMESTAMP=164, LOCATE=165, LOG=166, LOG10=167, LOG2=168, 
		LOWER=169, LPAD=170, LTRIM=171, MAKEDATE=172, MAKETIME=173, MAKE_SET=174, 
		MASTER_POS_WAIT=175, MATCH=176, MD5=177, MICROSECOND=178, MID=179, MINUTE=180, 
		MOD=181, MONTH=182, MONTHNAME=183, NOW=184, NULLIF=185, OCT=186, OCTET_LENGTH=187, 
		ORD=188, PASSWORD=189, PERIOD_ADD=190, PERIOD_DIFF=191, PI=192, POSITION=193, 
		POW=194, POWER=195, QUARTER=196, QUOTE=197, RADIANS=198, RAND=199, RELEASE_LOCK=200, 
		REPEAT=201, REPLACE=202, REVERSE=203, ROUND=204, ROW_COUNT=205, RPAD=206, 
		RTRIM=207, SCHEMA=208, SEC_TO_TIME=209, SECOND=210, SHA=211, SHA1=212, 
		SHA2=213, SIGN=214, SIN=215, SLEEP=216, SOUNDEX=217, SPACE=218, SQRT=219, 
		STR_TO_DATE=220, STRCMP=221, SUBDATE=222, SUBSTR=223, SUBSTRING=224, SUBSTRING_INDEX=225, 
		SUCSTRING=226, SYSDATE=227, TAN=228, TIME=229, TIMEDIFF=230, TIMESTAMP=231, 
		TIMESTAMPADD=232, TIMESTAMPDIFF=233, TIME_FORMAT=234, TIME_TO_SEC=235, 
		TO_DAYS=236, TRIM=237, TRUNCATE=238, UCASE=239, UNCOMPRESS=240, UNCOMPRESSED_LENGTH=241, 
		UNHEX=242, UNIX_TIMESTAMP=243, UPPER=244, USER=245, UTC_DATE=246, UTC_TIME=247, 
		UTC_TIMESTAMP=248, UUID=249, UUID_SHORT=250, VALIDATE_PASSWORD_STRENGTH=251, 
		VERSION=252, WEEK=253, WEEKDAY=254, WEEKOFYEAR=255, YEAR=256, YEARWEEK=257, 
		ID_LITERAL=258, STRING_LITERAL=259, DECIMAL_LITERAL=260, HEXADECIMAL_LITERAL=261, 
		BIT_STRING=262, WS=263, COMMENT=264, LINE_COMMENT=265, LOCAL_ID=266, VAR_ASSIGN=267, 
		REGEXP=268, RLIKE=269, LIKE=270, ESCAPE=271, SEPARATOR=272, DIV=273, SOUNDS=274, 
		STD=275, STDDEV=276, STDDEV_POP=277, STDDEV_SAMP=278, VAR_POP=279, VAR_SAMP=280, 
		VARIANCE=281, BIT_AND=282, BIT_OR=283, BIT_XOR=284, JSON_ARRAYAGG=285, 
		JSON_OBJECTAGG=286;
	public static final int
		RULE_query = 0, RULE_selectStatement = 1, RULE_selectExpression = 2, RULE_olapOperation = 3, 
		RULE_rollUp = 4, RULE_drillDown = 5, RULE_drillDownDimensions = 6, RULE_slice = 7, 
		RULE_sliceCondition = 8, RULE_dice = 9, RULE_diceConditions = 10, RULE_diceCondition = 11, 
		RULE_pivot = 12, RULE_pivotAggregate = 13, RULE_pivotColumn = 14, RULE_pivotValues = 15, 
		RULE_selectClause = 16, RULE_selectSpec = 17, RULE_selectElements = 18, 
		RULE_selectElement = 19, RULE_fromClause = 20, RULE_tableSources = 21, 
		RULE_tableSource = 22, RULE_tableSourceItem = 23, RULE_joinPart = 24, 
		RULE_indexHintList = 25, RULE_indexHint = 26, RULE_uidList = 27, RULE_groupByItem = 28, 
		RULE_havingExpr = 29, RULE_orderByClause = 30, RULE_orderByExpression = 31, 
		RULE_limitClause = 32, RULE_limitOnly = 33, RULE_limitWithOffset = 34, 
		RULE_commonTableExpression = 35, RULE_expression = 36, RULE_predicate = 37, 
		RULE_expressionAtom = 38, RULE_expressions = 39, RULE_functionCall = 40, 
		RULE_aggregateWindowedFunction = 41, RULE_scalarFunctionName = 42, RULE_logicalOperator = 43, 
		RULE_comparisonOperator = 44, RULE_mathOperator = 45, RULE_unaryOperator = 46, 
		RULE_constant = 47, RULE_booleanLiteral = 48, RULE_fullColumnName = 49, 
		RULE_uid = 50, RULE_simpleId = 51, RULE_dottedId = 52, RULE_keyword = 53, 
		RULE_compOperator = 54, RULE_aggregateFunction = 55, RULE_tableName = 56, 
		RULE_schemaName = 57, RULE_functionArgs = 58, RULE_functionArg = 59;
	private static String[] makeRuleNames() {
		return new String[] {
			"query", "selectStatement", "selectExpression", "olapOperation", "rollUp", 
			"drillDown", "drillDownDimensions", "slice", "sliceCondition", "dice", 
			"diceConditions", "diceCondition", "pivot", "pivotAggregate", "pivotColumn", 
			"pivotValues", "selectClause", "selectSpec", "selectElements", "selectElement", 
			"fromClause", "tableSources", "tableSource", "tableSourceItem", "joinPart", 
			"indexHintList", "indexHint", "uidList", "groupByItem", "havingExpr", 
			"orderByClause", "orderByExpression", "limitClause", "limitOnly", "limitWithOffset", 
			"commonTableExpression", "expression", "predicate", "expressionAtom", 
			"expressions", "functionCall", "aggregateWindowedFunction", "scalarFunctionName", 
			"logicalOperator", "comparisonOperator", "mathOperator", "unaryOperator", 
			"constant", "booleanLiteral", "fullColumnName", "uid", "simpleId", "dottedId", 
			"keyword", "compOperator", "aggregateFunction", "tableName", "schemaName", 
			"functionArgs", "functionArg"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'('", "')'", "'='", "'*'", "'!'", "'&'", "'|'", 
			"'>'", "'<'", "'/'", "'%'", "'+'", "'-'", "'--'", "'~'", "'.'", "'<='", 
			"'>='", "'<>'", "'!='", "'<=>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"ON", "IS", "NULL", "EXISTS", "DESC", "ASC", "SELECT", "FROM", "WHERE", 
			"GROUP", "BY", "HAVING", "ORDER", "LIMIT", "OFFSET", "AND", "OR", "XOR", 
			"NOT", "IN", "BETWEEN", "AS", "WITH", "RECURSIVE", "ROLLUP", "DRILLDOWN", 
			"SLICE", "DICE", "PIVOT", "JOIN", "INNER", "OUTER", "LEFT", "RIGHT", 
			"FULL", "CROSS", "NATURAL", "USE", "FORCE", "IGNORE", "INDEX", "KEY", 
			"FOR", "ALL", "DISTINCT", "DISTINCTROW", "HIGH_PRIORITY", "STRAIGHT_JOIN", 
			"SQL_SMALL_RESULT", "SQL_BIG_RESULT", "SQL_BUFFER_RESULT", "SQL_CACHE", 
			"SQL_NO_CACHE", "SQL_CALC_FOUND_ROWS", "TRUE", "FALSE", "NULL_LITERAL", 
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
			"WS", "COMMENT", "LINE_COMMENT", "LOCAL_ID", "VAR_ASSIGN", "REGEXP", 
			"RLIKE", "LIKE", "ESCAPE", "SEPARATOR", "DIV", "SOUNDS", "STD", "STDDEV", 
			"STDDEV_POP", "STDDEV_SAMP", "VAR_POP", "VAR_SAMP", "VARIANCE", "BIT_AND", 
			"BIT_OR", "BIT_XOR", "JSON_ARRAYAGG", "JSON_OBJECTAGG"
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
			setState(120);
			selectStatement();
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(121);
				match(T__0);
				}
			}

			setState(124);
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
		public CommonTableExpressionContext cte;
		public TerminalNode WITH() { return getToken(JQuickSQLParser.WITH, 0); }
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
		public SelectStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSelectStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSelectStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSelectStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectStatementContext selectStatement() throws RecognitionException {
		SelectStatementContext _localctx = new SelectStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectStatement);
		int _la;
		try {
			setState(141);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				enterOuterAlt(_localctx, 1);
				{
				setState(126);
				match(WITH);
				setState(128);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(127);
					match(RECURSIVE);
					}
					break;
				}
				setState(130);
				((SelectStatementContext)_localctx).cte = commonTableExpression();
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(131);
					match(T__1);
					setState(132);
					commonTableExpression();
					}
					}
					setState(137);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(138);
				selectExpression();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
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
			setState(145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(143);
				olapOperation();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
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
			setState(152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(147);
				rollUp();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(148);
				drillDown();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(149);
				slice();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(150);
				dice();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(151);
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
			setState(154);
			match(SELECT);
			setState(155);
			selectElements();
			setState(156);
			match(FROM);
			setState(157);
			tableSources();
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(158);
				match(WHERE);
				setState(159);
				expression(0);
				}
			}

			setState(162);
			match(GROUP);
			setState(163);
			match(BY);
			setState(164);
			groupByItem();
			setState(169);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(165);
				match(T__1);
				setState(166);
				groupByItem();
				}
				}
				setState(171);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(172);
			match(WITH);
			setState(173);
			match(ROLLUP);
			setState(176);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(174);
				match(HAVING);
				setState(175);
				expression(0);
				}
				break;
			}
			setState(179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(178);
				orderByClause();
				}
				break;
			}
			setState(182);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(181);
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
			setState(184);
			match(SELECT);
			setState(185);
			selectElements();
			setState(186);
			match(FROM);
			setState(187);
			tableSources();
			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(188);
				match(WHERE);
				setState(189);
				expression(0);
				}
			}

			setState(192);
			match(GROUP);
			setState(193);
			match(BY);
			setState(194);
			groupByItem();
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(195);
				match(T__1);
				setState(196);
				groupByItem();
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
			match(WITH);
			setState(203);
			match(DRILLDOWN);
			setState(204);
			match(T__2);
			setState(205);
			drillDownDimensions();
			setState(206);
			match(T__3);
			setState(209);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(207);
				match(HAVING);
				setState(208);
				expression(0);
				}
				break;
			}
			setState(212);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(211);
				orderByClause();
				}
				break;
			}
			setState(215);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(214);
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
			setState(217);
			uid();
			setState(222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(218);
				match(T__1);
				setState(219);
				uid();
				}
				}
				setState(224);
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
			setState(225);
			match(SELECT);
			setState(226);
			selectElements();
			setState(227);
			match(FROM);
			setState(228);
			tableSources();
			setState(229);
			match(WHERE);
			setState(230);
			match(SLICE);
			setState(231);
			match(T__2);
			setState(232);
			sliceCondition();
			setState(233);
			match(T__3);
			setState(244);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(234);
				match(GROUP);
				setState(235);
				match(BY);
				setState(236);
				groupByItem();
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(237);
					match(T__1);
					setState(238);
					groupByItem();
					}
					}
					setState(243);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(248);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(246);
				match(HAVING);
				setState(247);
				expression(0);
				}
				break;
			}
			setState(251);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(250);
				orderByClause();
				}
				break;
			}
			setState(254);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(253);
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
			setState(256);
			uid();
			setState(257);
			match(T__4);
			setState(258);
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
			setState(260);
			match(SELECT);
			setState(261);
			selectElements();
			setState(262);
			match(FROM);
			setState(263);
			tableSources();
			setState(264);
			match(WHERE);
			setState(265);
			match(DICE);
			setState(266);
			match(T__2);
			setState(267);
			diceConditions();
			setState(268);
			match(T__3);
			setState(279);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(269);
				match(GROUP);
				setState(270);
				match(BY);
				setState(271);
				groupByItem();
				setState(276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(272);
					match(T__1);
					setState(273);
					groupByItem();
					}
					}
					setState(278);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(283);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(281);
				match(HAVING);
				setState(282);
				expression(0);
				}
				break;
			}
			setState(286);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(285);
				orderByClause();
				}
				break;
			}
			setState(289);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(288);
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
			setState(291);
			diceCondition();
			setState(296);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(292);
				match(AND);
				setState(293);
				diceCondition();
				}
				}
				setState(298);
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
			setState(299);
			uid();
			setState(300);
			compOperator();
			setState(301);
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
			setState(303);
			match(SELECT);
			setState(304);
			selectElements();
			setState(305);
			match(FROM);
			setState(306);
			tableSources();
			setState(307);
			match(PIVOT);
			setState(308);
			match(T__2);
			setState(309);
			pivotAggregate();
			setState(310);
			match(FOR);
			setState(311);
			pivotColumn();
			setState(312);
			match(IN);
			setState(313);
			match(T__2);
			setState(314);
			pivotValues();
			setState(315);
			match(T__3);
			setState(316);
			match(T__3);
			setState(319);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(317);
				match(WHERE);
				setState(318);
				expression(0);
				}
				break;
			}
			setState(331);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(321);
				match(GROUP);
				setState(322);
				match(BY);
				setState(323);
				groupByItem();
				setState(328);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(324);
					match(T__1);
					setState(325);
					groupByItem();
					}
					}
					setState(330);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(335);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				setState(333);
				match(HAVING);
				setState(334);
				expression(0);
				}
				break;
			}
			setState(338);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(337);
				orderByClause();
				}
				break;
			}
			setState(341);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(340);
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
			setState(343);
			aggregateFunction();
			setState(344);
			match(T__2);
			setState(345);
			expression(0);
			setState(346);
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
			setState(348);
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
			setState(350);
			constant();
			setState(355);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(351);
				match(T__1);
				setState(352);
				constant();
				}
				}
				setState(357);
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
			setState(358);
			match(SELECT);
			setState(360);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				{
				setState(359);
				selectSpec();
				}
				break;
			}
			setState(362);
			selectElements();
			setState(364);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(363);
				fromClause();
				}
				break;
			}
			setState(368);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(366);
				match(WHERE);
				setState(367);
				expression(0);
				}
				break;
			}
			setState(380);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(370);
				match(GROUP);
				setState(371);
				match(BY);
				setState(372);
				groupByItem();
				setState(377);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(373);
						match(T__1);
						setState(374);
						groupByItem();
						}
						} 
					}
					setState(379);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
				}
				}
				break;
			}
			setState(384);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(382);
				match(HAVING);
				setState(383);
				expression(0);
				}
				break;
			}
			setState(387);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(386);
				orderByClause();
				}
				break;
			}
			setState(390);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(389);
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
			setState(392);
			_la = _input.LA(1);
			if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & 7L) != 0)) ) {
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
			setState(396);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
				{
				setState(394);
				((SelectElementsContext)_localctx).star = match(T__5);
				}
				break;
			case T__2:
			case T__6:
			case T__13:
			case T__14:
			case T__16:
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
			case IN:
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
			case NULL_LITERAL:
			case AVG:
			case MAX:
			case MIN:
			case SUM:
			case COUNT:
			case GROUP_CONCAT:
			case ABS:
			case ACOS:
			case ADDDATE:
			case ADDTIME:
			case AES_DECRYPT:
			case AES_ENCRYPT:
			case ASCII:
			case ASIN:
			case ATAN:
			case ATAN2:
			case BENCHMARK:
			case BIN:
			case BIT_COUNT:
			case BIT_LENGTH:
			case CEIL:
			case CEILING:
			case CHAR:
			case CHAR_LENGTH:
			case CHARACTER_LENGTH:
			case COALESCE:
			case COS:
			case COT:
			case CRC32:
			case CURDATE:
			case CURRENT_DATE:
			case CURRENT_TIME:
			case CURRENT_TIMESTAMP:
			case CURRENT_USER:
			case CURTIME:
			case DATABASE:
			case DATE:
			case DATEDIFF:
			case DATE_ADD:
			case DATE_FORMAT:
			case DATE_SUB:
			case DAY:
			case DAYNAME:
			case DAYOFMONTH:
			case DAYOFWEEK:
			case DAYOFYEAR:
			case DECODE:
			case DEFAULT:
			case DEGREES:
			case DES_DECRYPT:
			case DES_ENCRYPT:
			case ELT:
			case ENCODE:
			case ENCRYPT:
			case EXP:
			case EXTRACT:
			case FIELD:
			case FIND_IN_SET:
			case FLOOR:
			case FORMAT:
			case FOUND_ROWS:
			case FROM_DAYS:
			case FROM_UNIXTIME:
			case GET_FORMAT:
			case GET_LOCK:
			case GREATEST:
			case HEX:
			case HOUR:
			case IF:
			case IFNULL:
			case INET_ATON:
			case INET_NTOA:
			case INSERT:
			case INSTR:
			case IS_FREE_LOCK:
			case IS_USED_LOCK:
			case LAST_INSERT_ID:
			case LCASE:
			case LEAST:
			case LENGTH:
			case LN:
			case LOAD_FILE:
			case LOCALTIME:
			case LOCALTIMESTAMP:
			case LOCATE:
			case LOG:
			case LOG10:
			case LOG2:
			case LOWER:
			case LPAD:
			case LTRIM:
			case MAKEDATE:
			case MAKETIME:
			case MAKE_SET:
			case MASTER_POS_WAIT:
			case MATCH:
			case MD5:
			case MICROSECOND:
			case MID:
			case MINUTE:
			case MOD:
			case MONTH:
			case MONTHNAME:
			case NOW:
			case NULLIF:
			case OCT:
			case OCTET_LENGTH:
			case ORD:
			case PASSWORD:
			case PERIOD_ADD:
			case PERIOD_DIFF:
			case PI:
			case POSITION:
			case POW:
			case POWER:
			case QUARTER:
			case QUOTE:
			case RADIANS:
			case RAND:
			case RELEASE_LOCK:
			case REPEAT:
			case REPLACE:
			case REVERSE:
			case ROUND:
			case ROW_COUNT:
			case RPAD:
			case RTRIM:
			case SCHEMA:
			case SEC_TO_TIME:
			case SECOND:
			case SHA:
			case SHA1:
			case SHA2:
			case SIGN:
			case SIN:
			case SLEEP:
			case SOUNDEX:
			case SPACE:
			case SQRT:
			case STR_TO_DATE:
			case STRCMP:
			case SUBDATE:
			case SUBSTR:
			case SUBSTRING:
			case SUBSTRING_INDEX:
			case SUCSTRING:
			case SYSDATE:
			case TAN:
			case TIME:
			case TIMEDIFF:
			case TIMESTAMP:
			case TIMESTAMPADD:
			case TIMESTAMPDIFF:
			case TIME_FORMAT:
			case TIME_TO_SEC:
			case TO_DAYS:
			case TRIM:
			case TRUNCATE:
			case UCASE:
			case UNCOMPRESS:
			case UNCOMPRESSED_LENGTH:
			case UNHEX:
			case UNIX_TIMESTAMP:
			case UPPER:
			case USER:
			case UTC_DATE:
			case UTC_TIME:
			case UTC_TIMESTAMP:
			case UUID:
			case UUID_SHORT:
			case VALIDATE_PASSWORD_STRENGTH:
			case VERSION:
			case WEEK:
			case WEEKDAY:
			case WEEKOFYEAR:
			case YEAR:
			case YEARWEEK:
			case ID_LITERAL:
			case STRING_LITERAL:
			case DECIMAL_LITERAL:
			case HEXADECIMAL_LITERAL:
			case BIT_STRING:
			case LOCAL_ID:
				{
				setState(395);
				selectElement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(402);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(398);
					match(T__1);
					setState(399);
					selectElement();
					}
					} 
				}
				setState(404);
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
			setState(405);
			expression(0);
			setState(410);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(407);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(406);
					match(AS);
					}
					break;
				}
				setState(409);
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
			setState(412);
			match(FROM);
			setState(413);
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
			setState(415);
			tableSource();
			setState(420);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(416);
					match(T__1);
					setState(417);
					tableSource();
					}
					} 
				}
				setState(422);
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
			setState(440);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(423);
				tableSourceItem();
				setState(427);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(424);
						joinPart();
						}
						} 
					}
					setState(429);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(430);
				match(T__2);
				setState(431);
				tableSourceItem();
				setState(435);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 53)) & ~0x3f) == 0 && ((1L << (_la - 53)) & 262363L) != 0)) {
					{
					{
					setState(432);
					joinPart();
					}
					}
					setState(437);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(438);
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
		public IndexHintListContext indexHintList() {
			return getRuleContext(IndexHintListContext.class,0);
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
			setState(468);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(442);
				tableName();
				setState(447);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(444);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
					case 1:
						{
						setState(443);
						match(AS);
						}
						break;
					}
					setState(446);
					uid();
					}
					break;
				}
				setState(450);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(449);
					indexHintList();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(457);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SELECT:
				case WITH:
					{
					setState(452);
					selectStatement();
					}
					break;
				case T__2:
					{
					setState(453);
					match(T__2);
					setState(454);
					selectStatement();
					setState(455);
					match(T__3);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(460);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
				case 1:
					{
					setState(459);
					match(AS);
					}
					break;
				}
				setState(462);
				uid();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(464);
				match(T__2);
				setState(465);
				tableSources();
				setState(466);
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
		public TerminalNode JOIN() { return getToken(JQuickSQLParser.JOIN, 0); }
		public TableSourceItemContext tableSourceItem() {
			return getRuleContext(TableSourceItemContext.class,0);
		}
		public TerminalNode ON() { return getToken(JQuickSQLParser.ON, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode INNER() { return getToken(JQuickSQLParser.INNER, 0); }
		public TerminalNode CROSS() { return getToken(JQuickSQLParser.CROSS, 0); }
		public TerminalNode STRAIGHT_JOIN() { return getToken(JQuickSQLParser.STRAIGHT_JOIN, 0); }
		public TerminalNode LEFT() { return getToken(JQuickSQLParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(JQuickSQLParser.RIGHT, 0); }
		public TerminalNode OUTER() { return getToken(JQuickSQLParser.OUTER, 0); }
		public TerminalNode NATURAL() { return getToken(JQuickSQLParser.NATURAL, 0); }
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
		int _la;
		try {
			setState(503);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JOIN:
			case INNER:
			case CROSS:
				enterOuterAlt(_localctx, 1);
				{
				setState(471);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INNER || _la==CROSS) {
					{
					setState(470);
					_la = _input.LA(1);
					if ( !(_la==INNER || _la==CROSS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(473);
				match(JOIN);
				setState(474);
				tableSourceItem();
				setState(477);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(475);
					match(ON);
					setState(476);
					expression(0);
					}
					break;
				}
				}
				break;
			case STRAIGHT_JOIN:
				enterOuterAlt(_localctx, 2);
				{
				setState(479);
				match(STRAIGHT_JOIN);
				setState(480);
				tableSourceItem();
				setState(483);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(481);
					match(ON);
					setState(482);
					expression(0);
					}
					break;
				}
				}
				break;
			case LEFT:
			case RIGHT:
				enterOuterAlt(_localctx, 3);
				{
				setState(485);
				_la = _input.LA(1);
				if ( !(_la==LEFT || _la==RIGHT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(487);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUTER) {
					{
					setState(486);
					match(OUTER);
					}
				}

				setState(489);
				match(JOIN);
				setState(490);
				tableSourceItem();
				{
				setState(491);
				match(ON);
				setState(492);
				expression(0);
				}
				}
				break;
			case NATURAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(494);
				match(NATURAL);
				setState(499);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LEFT || _la==RIGHT) {
					{
					setState(495);
					_la = _input.LA(1);
					if ( !(_la==LEFT || _la==RIGHT) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(497);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==OUTER) {
						{
						setState(496);
						match(OUTER);
						}
					}

					}
				}

				setState(501);
				match(JOIN);
				setState(502);
				tableSourceItem();
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
	public static class IndexHintListContext extends ParserRuleContext {
		public List<IndexHintContext> indexHint() {
			return getRuleContexts(IndexHintContext.class);
		}
		public IndexHintContext indexHint(int i) {
			return getRuleContext(IndexHintContext.class,i);
		}
		public IndexHintListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexHintList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterIndexHintList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitIndexHintList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitIndexHintList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexHintListContext indexHintList() throws RecognitionException {
		IndexHintListContext _localctx = new IndexHintListContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_indexHintList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(505);
			indexHint();
			setState(510);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(506);
					match(T__1);
					setState(507);
					indexHint();
					}
					} 
				}
				setState(512);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
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
	public static class IndexHintContext extends ParserRuleContext {
		public UidListContext uidList() {
			return getRuleContext(UidListContext.class,0);
		}
		public TerminalNode USE() { return getToken(JQuickSQLParser.USE, 0); }
		public TerminalNode IGNORE() { return getToken(JQuickSQLParser.IGNORE, 0); }
		public TerminalNode FORCE() { return getToken(JQuickSQLParser.FORCE, 0); }
		public TerminalNode INDEX() { return getToken(JQuickSQLParser.INDEX, 0); }
		public TerminalNode KEY() { return getToken(JQuickSQLParser.KEY, 0); }
		public TerminalNode FOR() { return getToken(JQuickSQLParser.FOR, 0); }
		public TerminalNode JOIN() { return getToken(JQuickSQLParser.JOIN, 0); }
		public TerminalNode ORDER() { return getToken(JQuickSQLParser.ORDER, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public IndexHintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexHint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterIndexHint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitIndexHint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitIndexHint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexHintContext indexHint() throws RecognitionException {
		IndexHintContext _localctx = new IndexHintContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_indexHint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(513);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & -2305843009213693952L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(514);
			_la = _input.LA(1);
			if ( !(_la==INDEX || _la==KEY) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(523);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FOR) {
				{
				setState(515);
				match(FOR);
				setState(521);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case JOIN:
					{
					setState(516);
					match(JOIN);
					}
					break;
				case ORDER:
					{
					setState(517);
					match(ORDER);
					setState(518);
					match(BY);
					}
					break;
				case GROUP:
					{
					setState(519);
					match(GROUP);
					setState(520);
					match(BY);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
			}

			setState(525);
			match(T__2);
			setState(526);
			uidList();
			setState(527);
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
		enterRule(_localctx, 54, RULE_uidList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(529);
			uid();
			setState(534);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(530);
				match(T__1);
				setState(531);
				uid();
				}
				}
				setState(536);
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
		enterRule(_localctx, 56, RULE_groupByItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(537);
			expression(0);
			setState(539);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				{
				setState(538);
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
		enterRule(_localctx, 58, RULE_havingExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
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
		enterRule(_localctx, 60, RULE_orderByClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(543);
			match(ORDER);
			setState(544);
			match(BY);
			setState(545);
			orderByExpression();
			setState(550);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(546);
					match(T__1);
					setState(547);
					orderByExpression();
					}
					} 
				}
				setState(552);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
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
		enterRule(_localctx, 62, RULE_orderByExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			expression(0);
			setState(555);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(554);
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
		enterRule(_localctx, 64, RULE_limitClause);
		try {
			setState(561);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(557);
				match(LIMIT);
				setState(558);
				limitOnly();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(559);
				match(LIMIT);
				setState(560);
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
		enterRule(_localctx, 66, RULE_limitOnly);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(563);
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
		enterRule(_localctx, 68, RULE_limitWithOffset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(565);
			((LimitWithOffsetContext)_localctx).offset = expression(0);
			setState(566);
			match(T__1);
			setState(567);
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
		enterRule(_localctx, 70, RULE_commonTableExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			uid();
			setState(570);
			match(AS);
			setState(571);
			match(T__2);
			setState(572);
			selectStatement();
			setState(573);
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
	public static class InExpressionContext extends ExpressionContext {
		public TerminalNode IN() { return getToken(JQuickSQLParser.IN, 0); }
		public LogicalOperatorContext logicalOperator() {
			return getRuleContext(LogicalOperatorContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXISTS() { return getToken(JQuickSQLParser.EXISTS, 0); }
		public InExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterInExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitInExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitInExpression(this);
			else return visitor.visitChildren(this);
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
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(590);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(576);
				((NotExpressionContext)_localctx).notOperator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__6 || _la==NOT) ) {
					((NotExpressionContext)_localctx).notOperator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(577);
				expression(7);
				}
				break;
			case 2:
				{
				_localctx = new InExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(578);
				match(IN);
				setState(579);
				logicalOperator();
				setState(580);
				expression(5);
				}
				break;
			case 3:
				{
				_localctx = new InExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(582);
				match(EXISTS);
				setState(583);
				expression(4);
				}
				break;
			case 4:
				{
				_localctx = new PredicateExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(584);
				predicate(0);
				}
				break;
			case 5:
				{
				_localctx = new SelectResultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(585);
				selectClause();
				}
				break;
			case 6:
				{
				_localctx = new ParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(586);
				match(T__2);
				setState(587);
				expression(0);
				setState(588);
				match(T__3);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(598);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new LogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expression);
					setState(592);
					if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
					setState(593);
					logicalOperator();
					setState(594);
					expression(7);
					}
					} 
				}
				setState(600);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
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
		public TerminalNode LOCAL_ID() { return getToken(JQuickSQLParser.LOCAL_ID, 0); }
		public TerminalNode VAR_ASSIGN() { return getToken(JQuickSQLParser.VAR_ASSIGN, 0); }
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
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_predicate, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(608);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				{
				_localctx = new ExpressionAtomPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(602);
				expressionAtom(0);
				}
				break;
			case 2:
				{
				_localctx = new ExpressionAtomPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(605);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOCAL_ID) {
					{
					setState(603);
					match(LOCAL_ID);
					setState(604);
					match(VAR_ASSIGN);
					}
				}

				setState(607);
				expressionAtom(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(659);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,84,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(657);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryComparisonPredicateContext(new PredicateContext(_parentctx, _parentState));
						((BinaryComparisonPredicateContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(610);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(611);
						comparisonOperator();
						setState(612);
						((BinaryComparisonPredicateContext)_localctx).right = predicate(7);
						}
						break;
					case 2:
						{
						_localctx = new BetweenPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(614);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(616);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(615);
							match(NOT);
							}
						}

						setState(618);
						match(BETWEEN);
						setState(619);
						predicate(0);
						setState(620);
						match(AND);
						setState(621);
						predicate(6);
						}
						break;
					case 3:
						{
						_localctx = new RegexpPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(623);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(625);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(624);
							match(NOT);
							}
						}

						setState(627);
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
						setState(628);
						predicate(3);
						}
						break;
					case 4:
						{
						_localctx = new IsNullPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(629);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(630);
						match(IS);
						setState(632);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(631);
							match(NOT);
							}
						}

						setState(634);
						match(NULL);
						}
						break;
					case 5:
						{
						_localctx = new InPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(635);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(637);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(636);
							match(NOT);
							}
						}

						setState(639);
						match(IN);
						setState(640);
						match(T__2);
						setState(643);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
						case 1:
							{
							setState(641);
							selectStatement();
							}
							break;
						case 2:
							{
							setState(642);
							expressions();
							}
							break;
						}
						setState(645);
						match(T__3);
						}
						break;
					case 6:
						{
						_localctx = new LikePredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(647);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(649);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(648);
							match(NOT);
							}
						}

						setState(651);
						match(LIKE);
						setState(652);
						predicate(0);
						setState(655);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
						case 1:
							{
							setState(653);
							match(ESCAPE);
							setState(654);
							match(STRING_LITERAL);
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(661);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,84,_ctx);
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
		int _startState = 76;
		enterRecursionRule(_localctx, 76, RULE_expressionAtom, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				{
				_localctx = new ConstantExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(663);
				constant();
				}
				break;
			case 2:
				{
				_localctx = new FullColumnNameExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(664);
				fullColumnName();
				}
				break;
			case 3:
				{
				_localctx = new FunctionCallExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(665);
				functionCall();
				}
				break;
			case 4:
				{
				_localctx = new NestedExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(666);
				match(T__2);
				setState(667);
				expression(0);
				setState(672);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(668);
					match(T__1);
					setState(669);
					expression(0);
					}
					}
					setState(674);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(675);
				match(T__3);
				}
				break;
			case 5:
				{
				_localctx = new SubqueryExperssionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(677);
				match(T__2);
				setState(678);
				selectStatement();
				setState(679);
				match(T__3);
				}
				break;
			case 6:
				{
				_localctx = new UnaryExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(681);
				unaryOperator();
				setState(682);
				expressionAtom(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(692);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,87,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MathExpressionAtomContext(new ExpressionAtomContext(_parentctx, _parentState));
					((MathExpressionAtomContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expressionAtom);
					setState(686);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(687);
					mathOperator();
					setState(688);
					((MathExpressionAtomContext)_localctx).right = expressionAtom(3);
					}
					} 
				}
				setState(694);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,87,_ctx);
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
		enterRule(_localctx, 78, RULE_expressions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(695);
			expression(0);
			setState(700);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(696);
				match(T__1);
				setState(697);
				expression(0);
				}
				}
				setState(702);
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
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
	 
		public FunctionCallContext() { }
		public void copyFrom(FunctionCallContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UdfFunctionCallContext extends FunctionCallContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public FunctionArgsContext functionArgs() {
			return getRuleContext(FunctionArgsContext.class,0);
		}
		public UdfFunctionCallContext(FunctionCallContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterUdfFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitUdfFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitUdfFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AggregateFunctionCallContext extends FunctionCallContext {
		public AggregateWindowedFunctionContext aggregateWindowedFunction() {
			return getRuleContext(AggregateWindowedFunctionContext.class,0);
		}
		public AggregateFunctionCallContext(FunctionCallContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterAggregateFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitAggregateFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitAggregateFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ScalarFunctionCallContext extends FunctionCallContext {
		public ScalarFunctionNameContext scalarFunctionName() {
			return getRuleContext(ScalarFunctionNameContext.class,0);
		}
		public FunctionArgsContext functionArgs() {
			return getRuleContext(FunctionArgsContext.class,0);
		}
		public ScalarFunctionCallContext(FunctionCallContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterScalarFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitScalarFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitScalarFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_functionCall);
		int _la;
		try {
			setState(718);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
			case 1:
				_localctx = new AggregateFunctionCallContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(703);
				aggregateWindowedFunction();
				}
				break;
			case 2:
				_localctx = new ScalarFunctionCallContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(704);
				scalarFunctionName();
				setState(705);
				match(T__2);
				setState(707);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -17593125388088L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 1151L) != 0)) {
					{
					setState(706);
					functionArgs();
					}
				}

				setState(709);
				match(T__3);
				}
				break;
			case 3:
				_localctx = new UdfFunctionCallContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(711);
				uid();
				setState(712);
				match(T__2);
				setState(714);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -17593125388088L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & 1151L) != 0)) {
					{
					setState(713);
					functionArgs();
					}
				}

				setState(716);
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
	public static class AggregateWindowedFunctionContext extends ParserRuleContext {
		public Token aggregator;
		public Token starArg;
		public Token separator;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AVG() { return getToken(JQuickSQLParser.AVG, 0); }
		public TerminalNode MAX() { return getToken(JQuickSQLParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(JQuickSQLParser.MIN, 0); }
		public TerminalNode SUM() { return getToken(JQuickSQLParser.SUM, 0); }
		public TerminalNode ALL() { return getToken(JQuickSQLParser.ALL, 0); }
		public TerminalNode COUNT() { return getToken(JQuickSQLParser.COUNT, 0); }
		public TerminalNode DISTINCT() { return getToken(JQuickSQLParser.DISTINCT, 0); }
		public TerminalNode GROUP_CONCAT() { return getToken(JQuickSQLParser.GROUP_CONCAT, 0); }
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public TerminalNode SEPARATOR() { return getToken(JQuickSQLParser.SEPARATOR, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(JQuickSQLParser.STRING_LITERAL, 0); }
		public AggregateWindowedFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateWindowedFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterAggregateWindowedFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitAggregateWindowedFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitAggregateWindowedFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateWindowedFunctionContext aggregateWindowedFunction() throws RecognitionException {
		AggregateWindowedFunctionContext _localctx = new AggregateWindowedFunctionContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_aggregateWindowedFunction);
		int _la;
		try {
			setState(766);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(720);
				_la = _input.LA(1);
				if ( !(((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 15L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(721);
				match(T__2);
				setState(723);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
				case 1:
					{
					setState(722);
					((AggregateWindowedFunctionContext)_localctx).aggregator = match(ALL);
					}
					break;
				}
				setState(725);
				expression(0);
				setState(726);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(728);
				match(COUNT);
				setState(729);
				match(T__2);
				setState(735);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__5:
					{
					setState(730);
					((AggregateWindowedFunctionContext)_localctx).starArg = match(T__5);
					}
					break;
				case T__2:
				case T__6:
				case T__13:
				case T__14:
				case T__16:
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
				case IN:
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
				case NULL_LITERAL:
				case AVG:
				case MAX:
				case MIN:
				case SUM:
				case COUNT:
				case GROUP_CONCAT:
				case ABS:
				case ACOS:
				case ADDDATE:
				case ADDTIME:
				case AES_DECRYPT:
				case AES_ENCRYPT:
				case ASCII:
				case ASIN:
				case ATAN:
				case ATAN2:
				case BENCHMARK:
				case BIN:
				case BIT_COUNT:
				case BIT_LENGTH:
				case CEIL:
				case CEILING:
				case CHAR:
				case CHAR_LENGTH:
				case CHARACTER_LENGTH:
				case COALESCE:
				case COS:
				case COT:
				case CRC32:
				case CURDATE:
				case CURRENT_DATE:
				case CURRENT_TIME:
				case CURRENT_TIMESTAMP:
				case CURRENT_USER:
				case CURTIME:
				case DATABASE:
				case DATE:
				case DATEDIFF:
				case DATE_ADD:
				case DATE_FORMAT:
				case DATE_SUB:
				case DAY:
				case DAYNAME:
				case DAYOFMONTH:
				case DAYOFWEEK:
				case DAYOFYEAR:
				case DECODE:
				case DEFAULT:
				case DEGREES:
				case DES_DECRYPT:
				case DES_ENCRYPT:
				case ELT:
				case ENCODE:
				case ENCRYPT:
				case EXP:
				case EXTRACT:
				case FIELD:
				case FIND_IN_SET:
				case FLOOR:
				case FORMAT:
				case FOUND_ROWS:
				case FROM_DAYS:
				case FROM_UNIXTIME:
				case GET_FORMAT:
				case GET_LOCK:
				case GREATEST:
				case HEX:
				case HOUR:
				case IF:
				case IFNULL:
				case INET_ATON:
				case INET_NTOA:
				case INSERT:
				case INSTR:
				case IS_FREE_LOCK:
				case IS_USED_LOCK:
				case LAST_INSERT_ID:
				case LCASE:
				case LEAST:
				case LENGTH:
				case LN:
				case LOAD_FILE:
				case LOCALTIME:
				case LOCALTIMESTAMP:
				case LOCATE:
				case LOG:
				case LOG10:
				case LOG2:
				case LOWER:
				case LPAD:
				case LTRIM:
				case MAKEDATE:
				case MAKETIME:
				case MAKE_SET:
				case MASTER_POS_WAIT:
				case MATCH:
				case MD5:
				case MICROSECOND:
				case MID:
				case MINUTE:
				case MOD:
				case MONTH:
				case MONTHNAME:
				case NOW:
				case NULLIF:
				case OCT:
				case OCTET_LENGTH:
				case ORD:
				case PASSWORD:
				case PERIOD_ADD:
				case PERIOD_DIFF:
				case PI:
				case POSITION:
				case POW:
				case POWER:
				case QUARTER:
				case QUOTE:
				case RADIANS:
				case RAND:
				case RELEASE_LOCK:
				case REPEAT:
				case REPLACE:
				case REVERSE:
				case ROUND:
				case ROW_COUNT:
				case RPAD:
				case RTRIM:
				case SCHEMA:
				case SEC_TO_TIME:
				case SECOND:
				case SHA:
				case SHA1:
				case SHA2:
				case SIGN:
				case SIN:
				case SLEEP:
				case SOUNDEX:
				case SPACE:
				case SQRT:
				case STR_TO_DATE:
				case STRCMP:
				case SUBDATE:
				case SUBSTR:
				case SUBSTRING:
				case SUBSTRING_INDEX:
				case SUCSTRING:
				case SYSDATE:
				case TAN:
				case TIME:
				case TIMEDIFF:
				case TIMESTAMP:
				case TIMESTAMPADD:
				case TIMESTAMPDIFF:
				case TIME_FORMAT:
				case TIME_TO_SEC:
				case TO_DAYS:
				case TRIM:
				case TRUNCATE:
				case UCASE:
				case UNCOMPRESS:
				case UNCOMPRESSED_LENGTH:
				case UNHEX:
				case UNIX_TIMESTAMP:
				case UPPER:
				case USER:
				case UTC_DATE:
				case UTC_TIME:
				case UTC_TIMESTAMP:
				case UUID:
				case UUID_SHORT:
				case VALIDATE_PASSWORD_STRENGTH:
				case VERSION:
				case WEEK:
				case WEEKDAY:
				case WEEKOFYEAR:
				case YEAR:
				case YEARWEEK:
				case ID_LITERAL:
				case STRING_LITERAL:
				case DECIMAL_LITERAL:
				case HEXADECIMAL_LITERAL:
				case BIT_STRING:
				case LOCAL_ID:
					{
					setState(732);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
					case 1:
						{
						setState(731);
						((AggregateWindowedFunctionContext)_localctx).aggregator = match(ALL);
						}
						break;
					}
					setState(734);
					expression(0);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(737);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(738);
				_la = _input.LA(1);
				if ( !(((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 31L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(739);
				match(T__2);
				setState(740);
				((AggregateWindowedFunctionContext)_localctx).aggregator = match(DISTINCT);
				setState(741);
				expression(0);
				setState(742);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(744);
				match(GROUP_CONCAT);
				setState(745);
				match(T__2);
				setState(747);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
				case 1:
					{
					setState(746);
					((AggregateWindowedFunctionContext)_localctx).aggregator = match(DISTINCT);
					}
					break;
				}
				setState(749);
				expression(0);
				setState(754);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(750);
					match(T__1);
					setState(751);
					expression(0);
					}
					}
					setState(756);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(758);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ORDER) {
					{
					setState(757);
					orderByClause();
					}
				}

				setState(762);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEPARATOR) {
					{
					setState(760);
					match(SEPARATOR);
					setState(761);
					((AggregateWindowedFunctionContext)_localctx).separator = match(STRING_LITERAL);
					}
				}

				setState(764);
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
	public static class ScalarFunctionNameContext extends ParserRuleContext {
		public TerminalNode ABS() { return getToken(JQuickSQLParser.ABS, 0); }
		public TerminalNode ACOS() { return getToken(JQuickSQLParser.ACOS, 0); }
		public TerminalNode ADDDATE() { return getToken(JQuickSQLParser.ADDDATE, 0); }
		public TerminalNode ADDTIME() { return getToken(JQuickSQLParser.ADDTIME, 0); }
		public TerminalNode AES_DECRYPT() { return getToken(JQuickSQLParser.AES_DECRYPT, 0); }
		public TerminalNode AES_ENCRYPT() { return getToken(JQuickSQLParser.AES_ENCRYPT, 0); }
		public TerminalNode ASCII() { return getToken(JQuickSQLParser.ASCII, 0); }
		public TerminalNode ASIN() { return getToken(JQuickSQLParser.ASIN, 0); }
		public TerminalNode ATAN() { return getToken(JQuickSQLParser.ATAN, 0); }
		public TerminalNode ATAN2() { return getToken(JQuickSQLParser.ATAN2, 0); }
		public TerminalNode BENCHMARK() { return getToken(JQuickSQLParser.BENCHMARK, 0); }
		public TerminalNode BIN() { return getToken(JQuickSQLParser.BIN, 0); }
		public TerminalNode BIT_COUNT() { return getToken(JQuickSQLParser.BIT_COUNT, 0); }
		public TerminalNode BIT_LENGTH() { return getToken(JQuickSQLParser.BIT_LENGTH, 0); }
		public TerminalNode CEIL() { return getToken(JQuickSQLParser.CEIL, 0); }
		public TerminalNode CEILING() { return getToken(JQuickSQLParser.CEILING, 0); }
		public TerminalNode CHAR() { return getToken(JQuickSQLParser.CHAR, 0); }
		public TerminalNode CHAR_LENGTH() { return getToken(JQuickSQLParser.CHAR_LENGTH, 0); }
		public TerminalNode CHARACTER_LENGTH() { return getToken(JQuickSQLParser.CHARACTER_LENGTH, 0); }
		public TerminalNode COALESCE() { return getToken(JQuickSQLParser.COALESCE, 0); }
		public TerminalNode COS() { return getToken(JQuickSQLParser.COS, 0); }
		public TerminalNode COT() { return getToken(JQuickSQLParser.COT, 0); }
		public TerminalNode COUNT() { return getToken(JQuickSQLParser.COUNT, 0); }
		public TerminalNode CRC32() { return getToken(JQuickSQLParser.CRC32, 0); }
		public TerminalNode CURDATE() { return getToken(JQuickSQLParser.CURDATE, 0); }
		public TerminalNode CURRENT_DATE() { return getToken(JQuickSQLParser.CURRENT_DATE, 0); }
		public TerminalNode CURRENT_TIME() { return getToken(JQuickSQLParser.CURRENT_TIME, 0); }
		public TerminalNode CURRENT_TIMESTAMP() { return getToken(JQuickSQLParser.CURRENT_TIMESTAMP, 0); }
		public TerminalNode CURRENT_USER() { return getToken(JQuickSQLParser.CURRENT_USER, 0); }
		public TerminalNode CURTIME() { return getToken(JQuickSQLParser.CURTIME, 0); }
		public TerminalNode DATABASE() { return getToken(JQuickSQLParser.DATABASE, 0); }
		public TerminalNode DATE() { return getToken(JQuickSQLParser.DATE, 0); }
		public TerminalNode DATEDIFF() { return getToken(JQuickSQLParser.DATEDIFF, 0); }
		public TerminalNode DATE_ADD() { return getToken(JQuickSQLParser.DATE_ADD, 0); }
		public TerminalNode DATE_FORMAT() { return getToken(JQuickSQLParser.DATE_FORMAT, 0); }
		public TerminalNode DATE_SUB() { return getToken(JQuickSQLParser.DATE_SUB, 0); }
		public TerminalNode DAY() { return getToken(JQuickSQLParser.DAY, 0); }
		public TerminalNode DAYNAME() { return getToken(JQuickSQLParser.DAYNAME, 0); }
		public TerminalNode DAYOFMONTH() { return getToken(JQuickSQLParser.DAYOFMONTH, 0); }
		public TerminalNode DAYOFWEEK() { return getToken(JQuickSQLParser.DAYOFWEEK, 0); }
		public TerminalNode DAYOFYEAR() { return getToken(JQuickSQLParser.DAYOFYEAR, 0); }
		public TerminalNode DECODE() { return getToken(JQuickSQLParser.DECODE, 0); }
		public TerminalNode DEFAULT() { return getToken(JQuickSQLParser.DEFAULT, 0); }
		public TerminalNode DEGREES() { return getToken(JQuickSQLParser.DEGREES, 0); }
		public TerminalNode DES_DECRYPT() { return getToken(JQuickSQLParser.DES_DECRYPT, 0); }
		public TerminalNode DES_ENCRYPT() { return getToken(JQuickSQLParser.DES_ENCRYPT, 0); }
		public TerminalNode ELT() { return getToken(JQuickSQLParser.ELT, 0); }
		public TerminalNode ENCODE() { return getToken(JQuickSQLParser.ENCODE, 0); }
		public TerminalNode ENCRYPT() { return getToken(JQuickSQLParser.ENCRYPT, 0); }
		public TerminalNode EXP() { return getToken(JQuickSQLParser.EXP, 0); }
		public TerminalNode EXTRACT() { return getToken(JQuickSQLParser.EXTRACT, 0); }
		public TerminalNode FIELD() { return getToken(JQuickSQLParser.FIELD, 0); }
		public TerminalNode FIND_IN_SET() { return getToken(JQuickSQLParser.FIND_IN_SET, 0); }
		public TerminalNode FLOOR() { return getToken(JQuickSQLParser.FLOOR, 0); }
		public TerminalNode FORMAT() { return getToken(JQuickSQLParser.FORMAT, 0); }
		public TerminalNode FOUND_ROWS() { return getToken(JQuickSQLParser.FOUND_ROWS, 0); }
		public TerminalNode FROM_DAYS() { return getToken(JQuickSQLParser.FROM_DAYS, 0); }
		public TerminalNode FROM_UNIXTIME() { return getToken(JQuickSQLParser.FROM_UNIXTIME, 0); }
		public TerminalNode GET_FORMAT() { return getToken(JQuickSQLParser.GET_FORMAT, 0); }
		public TerminalNode GET_LOCK() { return getToken(JQuickSQLParser.GET_LOCK, 0); }
		public TerminalNode GREATEST() { return getToken(JQuickSQLParser.GREATEST, 0); }
		public TerminalNode HEX() { return getToken(JQuickSQLParser.HEX, 0); }
		public TerminalNode HOUR() { return getToken(JQuickSQLParser.HOUR, 0); }
		public TerminalNode IF() { return getToken(JQuickSQLParser.IF, 0); }
		public TerminalNode IFNULL() { return getToken(JQuickSQLParser.IFNULL, 0); }
		public TerminalNode INET_ATON() { return getToken(JQuickSQLParser.INET_ATON, 0); }
		public TerminalNode INET_NTOA() { return getToken(JQuickSQLParser.INET_NTOA, 0); }
		public TerminalNode INSERT() { return getToken(JQuickSQLParser.INSERT, 0); }
		public TerminalNode INSTR() { return getToken(JQuickSQLParser.INSTR, 0); }
		public TerminalNode IS_FREE_LOCK() { return getToken(JQuickSQLParser.IS_FREE_LOCK, 0); }
		public TerminalNode IS_USED_LOCK() { return getToken(JQuickSQLParser.IS_USED_LOCK, 0); }
		public TerminalNode LAST_INSERT_ID() { return getToken(JQuickSQLParser.LAST_INSERT_ID, 0); }
		public TerminalNode LCASE() { return getToken(JQuickSQLParser.LCASE, 0); }
		public TerminalNode LEAST() { return getToken(JQuickSQLParser.LEAST, 0); }
		public TerminalNode LEFT() { return getToken(JQuickSQLParser.LEFT, 0); }
		public TerminalNode LENGTH() { return getToken(JQuickSQLParser.LENGTH, 0); }
		public TerminalNode LN() { return getToken(JQuickSQLParser.LN, 0); }
		public TerminalNode LOAD_FILE() { return getToken(JQuickSQLParser.LOAD_FILE, 0); }
		public TerminalNode LOCALTIME() { return getToken(JQuickSQLParser.LOCALTIME, 0); }
		public TerminalNode LOCALTIMESTAMP() { return getToken(JQuickSQLParser.LOCALTIMESTAMP, 0); }
		public TerminalNode LOCATE() { return getToken(JQuickSQLParser.LOCATE, 0); }
		public TerminalNode LOG() { return getToken(JQuickSQLParser.LOG, 0); }
		public TerminalNode LOG10() { return getToken(JQuickSQLParser.LOG10, 0); }
		public TerminalNode LOG2() { return getToken(JQuickSQLParser.LOG2, 0); }
		public TerminalNode LOWER() { return getToken(JQuickSQLParser.LOWER, 0); }
		public TerminalNode LPAD() { return getToken(JQuickSQLParser.LPAD, 0); }
		public TerminalNode LTRIM() { return getToken(JQuickSQLParser.LTRIM, 0); }
		public TerminalNode MAKEDATE() { return getToken(JQuickSQLParser.MAKEDATE, 0); }
		public TerminalNode MAKETIME() { return getToken(JQuickSQLParser.MAKETIME, 0); }
		public TerminalNode MAKE_SET() { return getToken(JQuickSQLParser.MAKE_SET, 0); }
		public TerminalNode MASTER_POS_WAIT() { return getToken(JQuickSQLParser.MASTER_POS_WAIT, 0); }
		public TerminalNode MATCH() { return getToken(JQuickSQLParser.MATCH, 0); }
		public TerminalNode MAX() { return getToken(JQuickSQLParser.MAX, 0); }
		public TerminalNode MD5() { return getToken(JQuickSQLParser.MD5, 0); }
		public TerminalNode MICROSECOND() { return getToken(JQuickSQLParser.MICROSECOND, 0); }
		public TerminalNode MID() { return getToken(JQuickSQLParser.MID, 0); }
		public TerminalNode MIN() { return getToken(JQuickSQLParser.MIN, 0); }
		public TerminalNode MINUTE() { return getToken(JQuickSQLParser.MINUTE, 0); }
		public TerminalNode MOD() { return getToken(JQuickSQLParser.MOD, 0); }
		public TerminalNode MONTH() { return getToken(JQuickSQLParser.MONTH, 0); }
		public TerminalNode MONTHNAME() { return getToken(JQuickSQLParser.MONTHNAME, 0); }
		public TerminalNode NOW() { return getToken(JQuickSQLParser.NOW, 0); }
		public TerminalNode NULLIF() { return getToken(JQuickSQLParser.NULLIF, 0); }
		public TerminalNode OCT() { return getToken(JQuickSQLParser.OCT, 0); }
		public TerminalNode OCTET_LENGTH() { return getToken(JQuickSQLParser.OCTET_LENGTH, 0); }
		public TerminalNode ORD() { return getToken(JQuickSQLParser.ORD, 0); }
		public TerminalNode PASSWORD() { return getToken(JQuickSQLParser.PASSWORD, 0); }
		public TerminalNode PERIOD_ADD() { return getToken(JQuickSQLParser.PERIOD_ADD, 0); }
		public TerminalNode PERIOD_DIFF() { return getToken(JQuickSQLParser.PERIOD_DIFF, 0); }
		public TerminalNode PI() { return getToken(JQuickSQLParser.PI, 0); }
		public TerminalNode POSITION() { return getToken(JQuickSQLParser.POSITION, 0); }
		public TerminalNode POW() { return getToken(JQuickSQLParser.POW, 0); }
		public TerminalNode POWER() { return getToken(JQuickSQLParser.POWER, 0); }
		public TerminalNode QUARTER() { return getToken(JQuickSQLParser.QUARTER, 0); }
		public TerminalNode QUOTE() { return getToken(JQuickSQLParser.QUOTE, 0); }
		public TerminalNode RADIANS() { return getToken(JQuickSQLParser.RADIANS, 0); }
		public TerminalNode RAND() { return getToken(JQuickSQLParser.RAND, 0); }
		public TerminalNode RELEASE_LOCK() { return getToken(JQuickSQLParser.RELEASE_LOCK, 0); }
		public TerminalNode REPEAT() { return getToken(JQuickSQLParser.REPEAT, 0); }
		public TerminalNode REPLACE() { return getToken(JQuickSQLParser.REPLACE, 0); }
		public TerminalNode REVERSE() { return getToken(JQuickSQLParser.REVERSE, 0); }
		public TerminalNode RIGHT() { return getToken(JQuickSQLParser.RIGHT, 0); }
		public TerminalNode ROUND() { return getToken(JQuickSQLParser.ROUND, 0); }
		public TerminalNode ROW_COUNT() { return getToken(JQuickSQLParser.ROW_COUNT, 0); }
		public TerminalNode RPAD() { return getToken(JQuickSQLParser.RPAD, 0); }
		public TerminalNode RTRIM() { return getToken(JQuickSQLParser.RTRIM, 0); }
		public TerminalNode SCHEMA() { return getToken(JQuickSQLParser.SCHEMA, 0); }
		public TerminalNode SEC_TO_TIME() { return getToken(JQuickSQLParser.SEC_TO_TIME, 0); }
		public TerminalNode SECOND() { return getToken(JQuickSQLParser.SECOND, 0); }
		public TerminalNode SHA() { return getToken(JQuickSQLParser.SHA, 0); }
		public TerminalNode SHA1() { return getToken(JQuickSQLParser.SHA1, 0); }
		public TerminalNode SHA2() { return getToken(JQuickSQLParser.SHA2, 0); }
		public TerminalNode SIGN() { return getToken(JQuickSQLParser.SIGN, 0); }
		public TerminalNode SIN() { return getToken(JQuickSQLParser.SIN, 0); }
		public TerminalNode SLEEP() { return getToken(JQuickSQLParser.SLEEP, 0); }
		public TerminalNode SOUNDEX() { return getToken(JQuickSQLParser.SOUNDEX, 0); }
		public TerminalNode SPACE() { return getToken(JQuickSQLParser.SPACE, 0); }
		public TerminalNode SQRT() { return getToken(JQuickSQLParser.SQRT, 0); }
		public TerminalNode STR_TO_DATE() { return getToken(JQuickSQLParser.STR_TO_DATE, 0); }
		public TerminalNode STRCMP() { return getToken(JQuickSQLParser.STRCMP, 0); }
		public TerminalNode SUBDATE() { return getToken(JQuickSQLParser.SUBDATE, 0); }
		public TerminalNode SUBSTR() { return getToken(JQuickSQLParser.SUBSTR, 0); }
		public TerminalNode SUBSTRING() { return getToken(JQuickSQLParser.SUBSTRING, 0); }
		public TerminalNode SUBSTRING_INDEX() { return getToken(JQuickSQLParser.SUBSTRING_INDEX, 0); }
		public TerminalNode SUCSTRING() { return getToken(JQuickSQLParser.SUCSTRING, 0); }
		public TerminalNode SUM() { return getToken(JQuickSQLParser.SUM, 0); }
		public TerminalNode SYSDATE() { return getToken(JQuickSQLParser.SYSDATE, 0); }
		public TerminalNode TAN() { return getToken(JQuickSQLParser.TAN, 0); }
		public TerminalNode TIME() { return getToken(JQuickSQLParser.TIME, 0); }
		public TerminalNode TIMEDIFF() { return getToken(JQuickSQLParser.TIMEDIFF, 0); }
		public TerminalNode TIMESTAMP() { return getToken(JQuickSQLParser.TIMESTAMP, 0); }
		public TerminalNode TIMESTAMPADD() { return getToken(JQuickSQLParser.TIMESTAMPADD, 0); }
		public TerminalNode TIMESTAMPDIFF() { return getToken(JQuickSQLParser.TIMESTAMPDIFF, 0); }
		public TerminalNode TIME_FORMAT() { return getToken(JQuickSQLParser.TIME_FORMAT, 0); }
		public TerminalNode TIME_TO_SEC() { return getToken(JQuickSQLParser.TIME_TO_SEC, 0); }
		public TerminalNode TO_DAYS() { return getToken(JQuickSQLParser.TO_DAYS, 0); }
		public TerminalNode TRIM() { return getToken(JQuickSQLParser.TRIM, 0); }
		public TerminalNode TRUNCATE() { return getToken(JQuickSQLParser.TRUNCATE, 0); }
		public TerminalNode UCASE() { return getToken(JQuickSQLParser.UCASE, 0); }
		public TerminalNode UNCOMPRESS() { return getToken(JQuickSQLParser.UNCOMPRESS, 0); }
		public TerminalNode UNCOMPRESSED_LENGTH() { return getToken(JQuickSQLParser.UNCOMPRESSED_LENGTH, 0); }
		public TerminalNode UNHEX() { return getToken(JQuickSQLParser.UNHEX, 0); }
		public TerminalNode UNIX_TIMESTAMP() { return getToken(JQuickSQLParser.UNIX_TIMESTAMP, 0); }
		public TerminalNode UPPER() { return getToken(JQuickSQLParser.UPPER, 0); }
		public TerminalNode USER() { return getToken(JQuickSQLParser.USER, 0); }
		public TerminalNode UTC_DATE() { return getToken(JQuickSQLParser.UTC_DATE, 0); }
		public TerminalNode UTC_TIME() { return getToken(JQuickSQLParser.UTC_TIME, 0); }
		public TerminalNode UTC_TIMESTAMP() { return getToken(JQuickSQLParser.UTC_TIMESTAMP, 0); }
		public TerminalNode UUID() { return getToken(JQuickSQLParser.UUID, 0); }
		public TerminalNode UUID_SHORT() { return getToken(JQuickSQLParser.UUID_SHORT, 0); }
		public TerminalNode VALIDATE_PASSWORD_STRENGTH() { return getToken(JQuickSQLParser.VALIDATE_PASSWORD_STRENGTH, 0); }
		public TerminalNode VERSION() { return getToken(JQuickSQLParser.VERSION, 0); }
		public TerminalNode WEEK() { return getToken(JQuickSQLParser.WEEK, 0); }
		public TerminalNode WEEKDAY() { return getToken(JQuickSQLParser.WEEKDAY, 0); }
		public TerminalNode WEEKOFYEAR() { return getToken(JQuickSQLParser.WEEKOFYEAR, 0); }
		public TerminalNode YEAR() { return getToken(JQuickSQLParser.YEAR, 0); }
		public TerminalNode YEARWEEK() { return getToken(JQuickSQLParser.YEARWEEK, 0); }
		public ScalarFunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scalarFunctionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterScalarFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitScalarFunctionName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitScalarFunctionName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScalarFunctionNameContext scalarFunctionName() throws RecognitionException {
		ScalarFunctionNameContext _localctx = new ScalarFunctionNameContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_scalarFunctionName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			_la = _input.LA(1);
			if ( !(_la==LEFT || _la==RIGHT || ((((_la - 82)) & ~0x3f) == 0 && ((1L << (_la - 82)) & -17L) != 0) || ((((_la - 146)) & ~0x3f) == 0 && ((1L << (_la - 146)) & -1L) != 0) || ((((_la - 210)) & ~0x3f) == 0 && ((1L << (_la - 210)) & 281474976710655L) != 0)) ) {
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
		enterRule(_localctx, 86, RULE_logicalOperator);
		try {
			setState(777);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(770);
				match(AND);
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(771);
				match(T__7);
				setState(772);
				match(T__7);
				}
				break;
			case XOR:
				enterOuterAlt(_localctx, 3);
				{
				setState(773);
				match(XOR);
				}
				break;
			case OR:
				enterOuterAlt(_localctx, 4);
				{
				setState(774);
				match(OR);
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 5);
				{
				setState(775);
				match(T__8);
				setState(776);
				match(T__8);
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
		enterRule(_localctx, 88, RULE_comparisonOperator);
		try {
			setState(793);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(779);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(780);
				match(T__9);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(781);
				match(T__10);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(782);
				match(T__10);
				setState(783);
				match(T__4);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(784);
				match(T__9);
				setState(785);
				match(T__4);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(786);
				match(T__10);
				setState(787);
				match(T__9);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(788);
				match(T__6);
				setState(789);
				match(T__4);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(790);
				match(T__10);
				setState(791);
				match(T__4);
				setState(792);
				match(T__9);
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
	public static class MathOperatorContext extends ParserRuleContext {
		public TerminalNode DIV() { return getToken(JQuickSQLParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(JQuickSQLParser.MOD, 0); }
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
		enterRule(_localctx, 90, RULE_mathOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(795);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 127040L) != 0) || _la==MOD || _la==DIV) ) {
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
		enterRule(_localctx, 92, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398046691456L) != 0)) ) {
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
		public TerminalNode STRING_LITERAL() { return getToken(JQuickSQLParser.STRING_LITERAL, 0); }
		public TerminalNode DECIMAL_LITERAL() { return getToken(JQuickSQLParser.DECIMAL_LITERAL, 0); }
		public TerminalNode HEXADECIMAL_LITERAL() { return getToken(JQuickSQLParser.HEXADECIMAL_LITERAL, 0); }
		public TerminalNode BIT_STRING() { return getToken(JQuickSQLParser.BIT_STRING, 0); }
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
		public TerminalNode NULL_LITERAL() { return getToken(JQuickSQLParser.NULL_LITERAL, 0); }
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
		enterRule(_localctx, 94, RULE_constant);
		try {
			setState(807);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(799);
				match(STRING_LITERAL);
				}
				break;
			case DECIMAL_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(800);
				match(DECIMAL_LITERAL);
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 3);
				{
				setState(801);
				match(T__14);
				setState(802);
				match(DECIMAL_LITERAL);
				}
				break;
			case HEXADECIMAL_LITERAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(803);
				match(HEXADECIMAL_LITERAL);
				}
				break;
			case BIT_STRING:
				enterOuterAlt(_localctx, 5);
				{
				setState(804);
				match(BIT_STRING);
				}
				break;
			case TRUE:
			case FALSE:
				enterOuterAlt(_localctx, 6);
				{
				setState(805);
				booleanLiteral();
				}
				break;
			case NULL_LITERAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(806);
				match(NULL_LITERAL);
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
		enterRule(_localctx, 96, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(809);
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
		enterRule(_localctx, 98, RULE_fullColumnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(811);
			uid();
			setState(816);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(812);
				dottedId();
				setState(814);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
				case 1:
					{
					setState(813);
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
	public static class UidContext extends ParserRuleContext {
		public SimpleIdContext simpleId() {
			return getRuleContext(SimpleIdContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(JQuickSQLParser.STRING_LITERAL, 0); }
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
		enterRule(_localctx, 100, RULE_uid);
		try {
			setState(820);
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
			case MAX:
			case MIN:
			case SUM:
			case COUNT:
			case ABS:
			case ACOS:
			case ADDDATE:
			case ADDTIME:
			case AES_DECRYPT:
			case AES_ENCRYPT:
			case ASCII:
			case ASIN:
			case ATAN:
			case ATAN2:
			case BENCHMARK:
			case BIN:
			case BIT_COUNT:
			case BIT_LENGTH:
			case CEIL:
			case CEILING:
			case CHAR:
			case CHAR_LENGTH:
			case CHARACTER_LENGTH:
			case COALESCE:
			case COS:
			case COT:
			case CRC32:
			case CURDATE:
			case CURRENT_DATE:
			case CURRENT_TIME:
			case CURRENT_TIMESTAMP:
			case CURRENT_USER:
			case CURTIME:
			case DATABASE:
			case DATE:
			case DATEDIFF:
			case DATE_ADD:
			case DATE_FORMAT:
			case DATE_SUB:
			case DAY:
			case DAYNAME:
			case DAYOFMONTH:
			case DAYOFWEEK:
			case DAYOFYEAR:
			case DECODE:
			case DEFAULT:
			case DEGREES:
			case DES_DECRYPT:
			case DES_ENCRYPT:
			case ELT:
			case ENCODE:
			case ENCRYPT:
			case EXP:
			case EXTRACT:
			case FIELD:
			case FIND_IN_SET:
			case FLOOR:
			case FORMAT:
			case FOUND_ROWS:
			case FROM_DAYS:
			case FROM_UNIXTIME:
			case GET_FORMAT:
			case GET_LOCK:
			case GREATEST:
			case HEX:
			case HOUR:
			case IF:
			case IFNULL:
			case INET_ATON:
			case INET_NTOA:
			case INSERT:
			case INSTR:
			case IS_FREE_LOCK:
			case IS_USED_LOCK:
			case LAST_INSERT_ID:
			case LCASE:
			case LEAST:
			case LENGTH:
			case LN:
			case LOAD_FILE:
			case LOCALTIME:
			case LOCALTIMESTAMP:
			case LOCATE:
			case LOG:
			case LOG10:
			case LOG2:
			case LOWER:
			case LPAD:
			case LTRIM:
			case MAKEDATE:
			case MAKETIME:
			case MAKE_SET:
			case MASTER_POS_WAIT:
			case MATCH:
			case MD5:
			case MICROSECOND:
			case MID:
			case MINUTE:
			case MOD:
			case MONTH:
			case MONTHNAME:
			case NOW:
			case NULLIF:
			case OCT:
			case OCTET_LENGTH:
			case ORD:
			case PASSWORD:
			case PERIOD_ADD:
			case PERIOD_DIFF:
			case PI:
			case POSITION:
			case POW:
			case POWER:
			case QUARTER:
			case QUOTE:
			case RADIANS:
			case RAND:
			case RELEASE_LOCK:
			case REPEAT:
			case REPLACE:
			case REVERSE:
			case ROUND:
			case ROW_COUNT:
			case RPAD:
			case RTRIM:
			case SCHEMA:
			case SEC_TO_TIME:
			case SECOND:
			case SHA:
			case SHA1:
			case SHA2:
			case SIGN:
			case SIN:
			case SLEEP:
			case SOUNDEX:
			case SPACE:
			case SQRT:
			case STR_TO_DATE:
			case STRCMP:
			case SUBDATE:
			case SUBSTR:
			case SUBSTRING:
			case SUBSTRING_INDEX:
			case SUCSTRING:
			case SYSDATE:
			case TAN:
			case TIME:
			case TIMEDIFF:
			case TIMESTAMP:
			case TIMESTAMPADD:
			case TIMESTAMPDIFF:
			case TIME_FORMAT:
			case TIME_TO_SEC:
			case TO_DAYS:
			case TRIM:
			case TRUNCATE:
			case UCASE:
			case UNCOMPRESS:
			case UNCOMPRESSED_LENGTH:
			case UNHEX:
			case UNIX_TIMESTAMP:
			case UPPER:
			case USER:
			case UTC_DATE:
			case UTC_TIME:
			case UTC_TIMESTAMP:
			case UUID:
			case UUID_SHORT:
			case VALIDATE_PASSWORD_STRENGTH:
			case VERSION:
			case WEEK:
			case WEEKDAY:
			case WEEKOFYEAR:
			case YEAR:
			case YEARWEEK:
			case ID_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(818);
				simpleId();
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(819);
				match(STRING_LITERAL);
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
	public static class SimpleIdContext extends ParserRuleContext {
		public TerminalNode ID_LITERAL() { return getToken(JQuickSQLParser.ID_LITERAL, 0); }
		public ScalarFunctionNameContext scalarFunctionName() {
			return getRuleContext(ScalarFunctionNameContext.class,0);
		}
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
		enterRule(_localctx, 102, RULE_simpleId);
		try {
			setState(825);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,106,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(822);
				match(ID_LITERAL);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(823);
				scalarFunctionName();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(824);
				keyword();
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
		enterRule(_localctx, 104, RULE_dottedId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(827);
			match(T__17);
			setState(828);
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
		enterRule(_localctx, 106, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(830);
			_la = _input.LA(1);
			if ( !(((((_la - 30)) & ~0x3f) == 0 && ((1L << (_la - 30)) & 281474976686079L) != 0)) ) {
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
	public static class CompOperatorContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(JQuickSQLParser.IS, 0); }
		public TerminalNode NOT() { return getToken(JQuickSQLParser.NOT, 0); }
		public TerminalNode LIKE() { return getToken(JQuickSQLParser.LIKE, 0); }
		public TerminalNode IN() { return getToken(JQuickSQLParser.IN, 0); }
		public TerminalNode BETWEEN() { return getToken(JQuickSQLParser.BETWEEN, 0); }
		public TerminalNode REGEXP() { return getToken(JQuickSQLParser.REGEXP, 0); }
		public TerminalNode RLIKE() { return getToken(JQuickSQLParser.RLIKE, 0); }
		public TerminalNode SOUNDS() { return getToken(JQuickSQLParser.SOUNDS, 0); }
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
		enterRule(_localctx, 108, RULE_compOperator);
		int _la;
		try {
			setState(866);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(832);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(833);
				match(T__9);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(834);
				match(T__10);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(835);
				match(T__18);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(836);
				match(T__19);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(837);
				match(T__20);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(838);
				match(T__21);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(839);
				match(T__22);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(840);
				match(IS);
				setState(842);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(841);
					match(NOT);
					}
				}

				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(845);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(844);
					match(NOT);
					}
				}

				setState(847);
				match(LIKE);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(849);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(848);
					match(NOT);
					}
				}

				setState(851);
				match(IN);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(853);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(852);
					match(NOT);
					}
				}

				setState(855);
				match(BETWEEN);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(857);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(856);
					match(NOT);
					}
				}

				setState(859);
				match(REGEXP);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(861);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(860);
					match(NOT);
					}
				}

				setState(863);
				match(RLIKE);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(864);
				match(SOUNDS);
				setState(865);
				match(LIKE);
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
		public TerminalNode STDDEV_POP() { return getToken(JQuickSQLParser.STDDEV_POP, 0); }
		public TerminalNode STDDEV_SAMP() { return getToken(JQuickSQLParser.STDDEV_SAMP, 0); }
		public TerminalNode VAR_POP() { return getToken(JQuickSQLParser.VAR_POP, 0); }
		public TerminalNode VAR_SAMP() { return getToken(JQuickSQLParser.VAR_SAMP, 0); }
		public TerminalNode VARIANCE() { return getToken(JQuickSQLParser.VARIANCE, 0); }
		public TerminalNode BIT_AND() { return getToken(JQuickSQLParser.BIT_AND, 0); }
		public TerminalNode BIT_OR() { return getToken(JQuickSQLParser.BIT_OR, 0); }
		public TerminalNode BIT_XOR() { return getToken(JQuickSQLParser.BIT_XOR, 0); }
		public TerminalNode JSON_ARRAYAGG() { return getToken(JQuickSQLParser.JSON_ARRAYAGG, 0); }
		public TerminalNode JSON_OBJECTAGG() { return getToken(JQuickSQLParser.JSON_OBJECTAGG, 0); }
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
		enterRule(_localctx, 110, RULE_aggregateFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(868);
			_la = _input.LA(1);
			if ( !(((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 63L) != 0) || ((((_la - 275)) & ~0x3f) == 0 && ((1L << (_la - 275)) & 4095L) != 0)) ) {
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
		enterRule(_localctx, 112, RULE_tableName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(873);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				{
				setState(870);
				((TableNameContext)_localctx).schema = uid();
				setState(871);
				match(T__17);
				}
				break;
			}
			setState(875);
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
		enterRule(_localctx, 114, RULE_schemaName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(877);
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
		enterRule(_localctx, 116, RULE_functionArgs);
		int _la;
		try {
			setState(909);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(883);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
				case 1:
					{
					setState(879);
					constant();
					}
					break;
				case 2:
					{
					setState(880);
					fullColumnName();
					}
					break;
				case 3:
					{
					setState(881);
					functionCall();
					}
					break;
				case 4:
					{
					setState(882);
					expression(0);
					}
					break;
				}
				setState(894);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(885);
					match(T__1);
					setState(890);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
					case 1:
						{
						setState(886);
						constant();
						}
						break;
					case 2:
						{
						setState(887);
						fullColumnName();
						}
						break;
					case 3:
						{
						setState(888);
						functionCall();
						}
						break;
					case 4:
						{
						setState(889);
						expression(0);
						}
						break;
					}
					}
					}
					setState(896);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(897);
				match(T__5);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(899);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
				case 1:
					{
					setState(898);
					match(DISTINCT);
					}
					break;
				}
				setState(901);
				functionArg();
				setState(906);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(902);
					match(T__1);
					setState(903);
					functionArg();
					}
					}
					setState(908);
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
		enterRule(_localctx, 118, RULE_functionArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(911);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 36:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 37:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 38:
			return expressionAtom_sempred((ExpressionAtomContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 6);
		case 2:
			return precpred(_ctx, 5);
		case 3:
			return precpred(_ctx, 2);
		case 4:
			return precpred(_ctx, 7);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean expressionAtom_sempred(ExpressionAtomContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u011e\u0392\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		";\u0001\u0000\u0001\u0000\u0003\u0000{\b\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0003\u0001\u0081\b\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0005\u0001\u0086\b\u0001\n\u0001\f\u0001\u0089\t\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u008e\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0003\u0002\u0092\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u0099\b\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00a1\b\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u00a8"+
		"\b\u0004\n\u0004\f\u0004\u00ab\t\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004\u00b1\b\u0004\u0001\u0004\u0003\u0004\u00b4\b"+
		"\u0004\u0001\u0004\u0003\u0004\u00b7\b\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u00bf\b\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005\u00c6"+
		"\b\u0005\n\u0005\f\u0005\u00c9\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u00d2\b\u0005"+
		"\u0001\u0005\u0003\u0005\u00d5\b\u0005\u0001\u0005\u0003\u0005\u00d8\b"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00dd\b\u0006\n"+
		"\u0006\f\u0006\u00e0\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007\u00f0"+
		"\b\u0007\n\u0007\f\u0007\u00f3\t\u0007\u0003\u0007\u00f5\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0003\u0007\u00f9\b\u0007\u0001\u0007\u0003\u0007\u00fc"+
		"\b\u0007\u0001\u0007\u0003\u0007\u00ff\b\u0007\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t\u0113\b\t\n"+
		"\t\f\t\u0116\t\t\u0003\t\u0118\b\t\u0001\t\u0001\t\u0003\t\u011c\b\t\u0001"+
		"\t\u0003\t\u011f\b\t\u0001\t\u0003\t\u0122\b\t\u0001\n\u0001\n\u0001\n"+
		"\u0005\n\u0127\b\n\n\n\f\n\u012a\t\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003"+
		"\f\u0140\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u0147\b\f"+
		"\n\f\f\f\u014a\t\f\u0003\f\u014c\b\f\u0001\f\u0001\f\u0003\f\u0150\b\f"+
		"\u0001\f\u0003\f\u0153\b\f\u0001\f\u0003\f\u0156\b\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0005\u000f\u0162\b\u000f\n\u000f\f\u000f\u0165\t\u000f\u0001\u0010"+
		"\u0001\u0010\u0003\u0010\u0169\b\u0010\u0001\u0010\u0001\u0010\u0003\u0010"+
		"\u016d\b\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u0171\b\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u0178"+
		"\b\u0010\n\u0010\f\u0010\u017b\t\u0010\u0003\u0010\u017d\b\u0010\u0001"+
		"\u0010\u0001\u0010\u0003\u0010\u0181\b\u0010\u0001\u0010\u0003\u0010\u0184"+
		"\b\u0010\u0001\u0010\u0003\u0010\u0187\b\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u018d\b\u0012\u0001\u0012\u0001\u0012"+
		"\u0005\u0012\u0191\b\u0012\n\u0012\f\u0012\u0194\t\u0012\u0001\u0013\u0001"+
		"\u0013\u0003\u0013\u0198\b\u0013\u0001\u0013\u0003\u0013\u019b\b\u0013"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0005\u0015\u01a3\b\u0015\n\u0015\f\u0015\u01a6\t\u0015\u0001\u0016\u0001"+
		"\u0016\u0005\u0016\u01aa\b\u0016\n\u0016\f\u0016\u01ad\t\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0005\u0016\u01b2\b\u0016\n\u0016\f\u0016\u01b5"+
		"\t\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01b9\b\u0016\u0001\u0017"+
		"\u0001\u0017\u0003\u0017\u01bd\b\u0017\u0001\u0017\u0003\u0017\u01c0\b"+
		"\u0017\u0001\u0017\u0003\u0017\u01c3\b\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u01ca\b\u0017\u0001\u0017\u0003"+
		"\u0017\u01cd\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0003\u0017\u01d5\b\u0017\u0001\u0018\u0003\u0018\u01d8"+
		"\b\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u01de"+
		"\b\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u01e4"+
		"\b\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u01e8\b\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0003\u0018\u01f2\b\u0018\u0003\u0018\u01f4\b\u0018\u0001"+
		"\u0018\u0001\u0018\u0003\u0018\u01f8\b\u0018\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0005\u0019\u01fd\b\u0019\n\u0019\f\u0019\u0200\t\u0019\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0003\u001a\u020a\b\u001a\u0003\u001a\u020c\b\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0005\u001b\u0215\b\u001b\n\u001b\f\u001b\u0218\t\u001b\u0001\u001c"+
		"\u0001\u001c\u0003\u001c\u021c\b\u001c\u0001\u001d\u0001\u001d\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u0225\b\u001e"+
		"\n\u001e\f\u001e\u0228\t\u001e\u0001\u001f\u0001\u001f\u0003\u001f\u022c"+
		"\b\u001f\u0001 \u0001 \u0001 \u0001 \u0003 \u0232\b \u0001!\u0001!\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001"+
		"$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"+
		"$\u0001$\u0001$\u0001$\u0001$\u0003$\u024f\b$\u0001$\u0001$\u0001$\u0001"+
		"$\u0005$\u0255\b$\n$\f$\u0258\t$\u0001%\u0001%\u0001%\u0001%\u0003%\u025e"+
		"\b%\u0001%\u0003%\u0261\b%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003"+
		"%\u0269\b%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003%\u0272"+
		"\b%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003%\u0279\b%\u0001%\u0001%\u0001"+
		"%\u0003%\u027e\b%\u0001%\u0001%\u0001%\u0001%\u0003%\u0284\b%\u0001%\u0001"+
		"%\u0001%\u0001%\u0003%\u028a\b%\u0001%\u0001%\u0001%\u0001%\u0003%\u0290"+
		"\b%\u0005%\u0292\b%\n%\f%\u0295\t%\u0001&\u0001&\u0001&\u0001&\u0001&"+
		"\u0001&\u0001&\u0001&\u0005&\u029f\b&\n&\f&\u02a2\t&\u0001&\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u02ad\b&\u0001&\u0001"+
		"&\u0001&\u0001&\u0005&\u02b3\b&\n&\f&\u02b6\t&\u0001\'\u0001\'\u0001\'"+
		"\u0005\'\u02bb\b\'\n\'\f\'\u02be\t\'\u0001(\u0001(\u0001(\u0001(\u0003"+
		"(\u02c4\b(\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(\u02cb\b(\u0001(\u0001"+
		"(\u0003(\u02cf\b(\u0001)\u0001)\u0001)\u0003)\u02d4\b)\u0001)\u0001)\u0001"+
		")\u0001)\u0001)\u0001)\u0001)\u0003)\u02dd\b)\u0001)\u0003)\u02e0\b)\u0001"+
		")\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0003"+
		")\u02ec\b)\u0001)\u0001)\u0001)\u0005)\u02f1\b)\n)\f)\u02f4\t)\u0001)"+
		"\u0003)\u02f7\b)\u0001)\u0001)\u0003)\u02fb\b)\u0001)\u0001)\u0003)\u02ff"+
		"\b)\u0001*\u0001*\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0003"+
		"+\u030a\b+\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0003,\u031a\b,\u0001-\u0001-\u0001"+
		".\u0001.\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0003"+
		"/\u0328\b/\u00010\u00010\u00011\u00011\u00011\u00031\u032f\b1\u00031\u0331"+
		"\b1\u00012\u00012\u00032\u0335\b2\u00013\u00013\u00013\u00033\u033a\b"+
		"3\u00014\u00014\u00014\u00015\u00015\u00016\u00016\u00016\u00016\u0001"+
		"6\u00016\u00016\u00016\u00016\u00016\u00036\u034b\b6\u00016\u00036\u034e"+
		"\b6\u00016\u00016\u00036\u0352\b6\u00016\u00016\u00036\u0356\b6\u0001"+
		"6\u00016\u00036\u035a\b6\u00016\u00016\u00036\u035e\b6\u00016\u00016\u0001"+
		"6\u00036\u0363\b6\u00017\u00017\u00018\u00018\u00018\u00038\u036a\b8\u0001"+
		"8\u00018\u00019\u00019\u0001:\u0001:\u0001:\u0001:\u0003:\u0374\b:\u0001"+
		":\u0001:\u0001:\u0001:\u0001:\u0003:\u037b\b:\u0005:\u037d\b:\n:\f:\u0380"+
		"\t:\u0001:\u0001:\u0003:\u0384\b:\u0001:\u0001:\u0001:\u0005:\u0389\b"+
		":\n:\f:\u038c\t:\u0003:\u038e\b:\u0001;\u0001;\u0001;\u0000\u0003HJL<"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtv\u0000\u0010"+
		"\u0001\u0000CE\u0002\u000066;;\u0001\u000089\u0001\u0000=?\u0001\u0000"+
		"@A\u0001\u0000\u001c\u001d\u0002\u0000\u0007\u0007**\u0001\u0000\u010c"+
		"\u010d\u0001\u0000QT\u0001\u0000QU\u0003\u000089RUW\u0101\u0004\u0000"+
		"\u0006\u0006\f\u0010\u00b5\u00b5\u0111\u0111\u0004\u0000\u0007\u0007\u000e"+
		"\u000f\u0011\u0011**\u0001\u0000NO\u0002\u0000\u001e*-M\u0002\u0000QV"+
		"\u0113\u011e\u0405\u0000x\u0001\u0000\u0000\u0000\u0002\u008d\u0001\u0000"+
		"\u0000\u0000\u0004\u0091\u0001\u0000\u0000\u0000\u0006\u0098\u0001\u0000"+
		"\u0000\u0000\b\u009a\u0001\u0000\u0000\u0000\n\u00b8\u0001\u0000\u0000"+
		"\u0000\f\u00d9\u0001\u0000\u0000\u0000\u000e\u00e1\u0001\u0000\u0000\u0000"+
		"\u0010\u0100\u0001\u0000\u0000\u0000\u0012\u0104\u0001\u0000\u0000\u0000"+
		"\u0014\u0123\u0001\u0000\u0000\u0000\u0016\u012b\u0001\u0000\u0000\u0000"+
		"\u0018\u012f\u0001\u0000\u0000\u0000\u001a\u0157\u0001\u0000\u0000\u0000"+
		"\u001c\u015c\u0001\u0000\u0000\u0000\u001e\u015e\u0001\u0000\u0000\u0000"+
		" \u0166\u0001\u0000\u0000\u0000\"\u0188\u0001\u0000\u0000\u0000$\u018c"+
		"\u0001\u0000\u0000\u0000&\u0195\u0001\u0000\u0000\u0000(\u019c\u0001\u0000"+
		"\u0000\u0000*\u019f\u0001\u0000\u0000\u0000,\u01b8\u0001\u0000\u0000\u0000"+
		".\u01d4\u0001\u0000\u0000\u00000\u01f7\u0001\u0000\u0000\u00002\u01f9"+
		"\u0001\u0000\u0000\u00004\u0201\u0001\u0000\u0000\u00006\u0211\u0001\u0000"+
		"\u0000\u00008\u0219\u0001\u0000\u0000\u0000:\u021d\u0001\u0000\u0000\u0000"+
		"<\u021f\u0001\u0000\u0000\u0000>\u0229\u0001\u0000\u0000\u0000@\u0231"+
		"\u0001\u0000\u0000\u0000B\u0233\u0001\u0000\u0000\u0000D\u0235\u0001\u0000"+
		"\u0000\u0000F\u0239\u0001\u0000\u0000\u0000H\u024e\u0001\u0000\u0000\u0000"+
		"J\u0260\u0001\u0000\u0000\u0000L\u02ac\u0001\u0000\u0000\u0000N\u02b7"+
		"\u0001\u0000\u0000\u0000P\u02ce\u0001\u0000\u0000\u0000R\u02fe\u0001\u0000"+
		"\u0000\u0000T\u0300\u0001\u0000\u0000\u0000V\u0309\u0001\u0000\u0000\u0000"+
		"X\u0319\u0001\u0000\u0000\u0000Z\u031b\u0001\u0000\u0000\u0000\\\u031d"+
		"\u0001\u0000\u0000\u0000^\u0327\u0001\u0000\u0000\u0000`\u0329\u0001\u0000"+
		"\u0000\u0000b\u032b\u0001\u0000\u0000\u0000d\u0334\u0001\u0000\u0000\u0000"+
		"f\u0339\u0001\u0000\u0000\u0000h\u033b\u0001\u0000\u0000\u0000j\u033e"+
		"\u0001\u0000\u0000\u0000l\u0362\u0001\u0000\u0000\u0000n\u0364\u0001\u0000"+
		"\u0000\u0000p\u0369\u0001\u0000\u0000\u0000r\u036d\u0001\u0000\u0000\u0000"+
		"t\u038d\u0001\u0000\u0000\u0000v\u038f\u0001\u0000\u0000\u0000xz\u0003"+
		"\u0002\u0001\u0000y{\u0005\u0001\u0000\u0000zy\u0001\u0000\u0000\u0000"+
		"z{\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|}\u0005\u0000\u0000"+
		"\u0001}\u0001\u0001\u0000\u0000\u0000~\u0080\u0005.\u0000\u0000\u007f"+
		"\u0081\u0005/\u0000\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0080\u0081"+
		"\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u0087"+
		"\u0003F#\u0000\u0083\u0084\u0005\u0002\u0000\u0000\u0084\u0086\u0003F"+
		"#\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0089\u0001\u0000\u0000"+
		"\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000"+
		"\u0000\u0088\u008a\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000"+
		"\u0000\u008a\u008b\u0003\u0004\u0002\u0000\u008b\u008e\u0001\u0000\u0000"+
		"\u0000\u008c\u008e\u0003\u0004\u0002\u0000\u008d~\u0001\u0000\u0000\u0000"+
		"\u008d\u008c\u0001\u0000\u0000\u0000\u008e\u0003\u0001\u0000\u0000\u0000"+
		"\u008f\u0092\u0003\u0006\u0003\u0000\u0090\u0092\u0003 \u0010\u0000\u0091"+
		"\u008f\u0001\u0000\u0000\u0000\u0091\u0090\u0001\u0000\u0000\u0000\u0092"+
		"\u0005\u0001\u0000\u0000\u0000\u0093\u0099\u0003\b\u0004\u0000\u0094\u0099"+
		"\u0003\n\u0005\u0000\u0095\u0099\u0003\u000e\u0007\u0000\u0096\u0099\u0003"+
		"\u0012\t\u0000\u0097\u0099\u0003\u0018\f\u0000\u0098\u0093\u0001\u0000"+
		"\u0000\u0000\u0098\u0094\u0001\u0000\u0000\u0000\u0098\u0095\u0001\u0000"+
		"\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0098\u0097\u0001\u0000"+
		"\u0000\u0000\u0099\u0007\u0001\u0000\u0000\u0000\u009a\u009b\u0005\u001e"+
		"\u0000\u0000\u009b\u009c\u0003$\u0012\u0000\u009c\u009d\u0005\u001f\u0000"+
		"\u0000\u009d\u00a0\u0003*\u0015\u0000\u009e\u009f\u0005 \u0000\u0000\u009f"+
		"\u00a1\u0003H$\u0000\u00a0\u009e\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005"+
		"!\u0000\u0000\u00a3\u00a4\u0005\"\u0000\u0000\u00a4\u00a9\u00038\u001c"+
		"\u0000\u00a5\u00a6\u0005\u0002\u0000\u0000\u00a6\u00a8\u00038\u001c\u0000"+
		"\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8\u00ab\u0001\u0000\u0000\u0000"+
		"\u00a9\u00a7\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000"+
		"\u00aa\u00ac\u0001\u0000\u0000\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000"+
		"\u00ac\u00ad\u0005.\u0000\u0000\u00ad\u00b0\u00050\u0000\u0000\u00ae\u00af"+
		"\u0005#\u0000\u0000\u00af\u00b1\u0003H$\u0000\u00b0\u00ae\u0001\u0000"+
		"\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b3\u0001\u0000"+
		"\u0000\u0000\u00b2\u00b4\u0003<\u001e\u0000\u00b3\u00b2\u0001\u0000\u0000"+
		"\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b6\u0001\u0000\u0000"+
		"\u0000\u00b5\u00b7\u0003@ \u0000\u00b6\u00b5\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b7\u0001\u0000\u0000\u0000\u00b7\t\u0001\u0000\u0000\u0000\u00b8\u00b9"+
		"\u0005\u001e\u0000\u0000\u00b9\u00ba\u0003$\u0012\u0000\u00ba\u00bb\u0005"+
		"\u001f\u0000\u0000\u00bb\u00be\u0003*\u0015\u0000\u00bc\u00bd\u0005 \u0000"+
		"\u0000\u00bd\u00bf\u0003H$\u0000\u00be\u00bc\u0001\u0000\u0000\u0000\u00be"+
		"\u00bf\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0"+
		"\u00c1\u0005!\u0000\u0000\u00c1\u00c2\u0005\"\u0000\u0000\u00c2\u00c7"+
		"\u00038\u001c\u0000\u00c3\u00c4\u0005\u0002\u0000\u0000\u00c4\u00c6\u0003"+
		"8\u001c\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c6\u00c9\u0001\u0000"+
		"\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000"+
		"\u0000\u0000\u00c8\u00ca\u0001\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000"+
		"\u0000\u0000\u00ca\u00cb\u0005.\u0000\u0000\u00cb\u00cc\u00051\u0000\u0000"+
		"\u00cc\u00cd\u0005\u0003\u0000\u0000\u00cd\u00ce\u0003\f\u0006\u0000\u00ce"+
		"\u00d1\u0005\u0004\u0000\u0000\u00cf\u00d0\u0005#\u0000\u0000\u00d0\u00d2"+
		"\u0003H$\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000"+
		"\u0000\u0000\u00d2\u00d4\u0001\u0000\u0000\u0000\u00d3\u00d5\u0003<\u001e"+
		"\u0000\u00d4\u00d3\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000"+
		"\u0000\u00d5\u00d7\u0001\u0000\u0000\u0000\u00d6\u00d8\u0003@ \u0000\u00d7"+
		"\u00d6\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8"+
		"\u000b\u0001\u0000\u0000\u0000\u00d9\u00de\u0003d2\u0000\u00da\u00db\u0005"+
		"\u0002\u0000\u0000\u00db\u00dd\u0003d2\u0000\u00dc\u00da\u0001\u0000\u0000"+
		"\u0000\u00dd\u00e0\u0001\u0000\u0000\u0000\u00de\u00dc\u0001\u0000\u0000"+
		"\u0000\u00de\u00df\u0001\u0000\u0000\u0000\u00df\r\u0001\u0000\u0000\u0000"+
		"\u00e0\u00de\u0001\u0000\u0000\u0000\u00e1\u00e2\u0005\u001e\u0000\u0000"+
		"\u00e2\u00e3\u0003$\u0012\u0000\u00e3\u00e4\u0005\u001f\u0000\u0000\u00e4"+
		"\u00e5\u0003*\u0015\u0000\u00e5\u00e6\u0005 \u0000\u0000\u00e6\u00e7\u0005"+
		"2\u0000\u0000\u00e7\u00e8\u0005\u0003\u0000\u0000\u00e8\u00e9\u0003\u0010"+
		"\b\u0000\u00e9\u00f4\u0005\u0004\u0000\u0000\u00ea\u00eb\u0005!\u0000"+
		"\u0000\u00eb\u00ec\u0005\"\u0000\u0000\u00ec\u00f1\u00038\u001c\u0000"+
		"\u00ed\u00ee\u0005\u0002\u0000\u0000\u00ee\u00f0\u00038\u001c\u0000\u00ef"+
		"\u00ed\u0001\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000\u0000\u00f1"+
		"\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2"+
		"\u00f5\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000\u00f4"+
		"\u00ea\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5"+
		"\u00f8\u0001\u0000\u0000\u0000\u00f6\u00f7\u0005#\u0000\u0000\u00f7\u00f9"+
		"\u0003H$\u0000\u00f8\u00f6\u0001\u0000\u0000\u0000\u00f8\u00f9\u0001\u0000"+
		"\u0000\u0000\u00f9\u00fb\u0001\u0000\u0000\u0000\u00fa\u00fc\u0003<\u001e"+
		"\u0000\u00fb\u00fa\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000"+
		"\u0000\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd\u00ff\u0003@ \u0000\u00fe"+
		"\u00fd\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff"+
		"\u000f\u0001\u0000\u0000\u0000\u0100\u0101\u0003d2\u0000\u0101\u0102\u0005"+
		"\u0005\u0000\u0000\u0102\u0103\u0003^/\u0000\u0103\u0011\u0001\u0000\u0000"+
		"\u0000\u0104\u0105\u0005\u001e\u0000\u0000\u0105\u0106\u0003$\u0012\u0000"+
		"\u0106\u0107\u0005\u001f\u0000\u0000\u0107\u0108\u0003*\u0015\u0000\u0108"+
		"\u0109\u0005 \u0000\u0000\u0109\u010a\u00053\u0000\u0000\u010a\u010b\u0005"+
		"\u0003\u0000\u0000\u010b\u010c\u0003\u0014\n\u0000\u010c\u0117\u0005\u0004"+
		"\u0000\u0000\u010d\u010e\u0005!\u0000\u0000\u010e\u010f\u0005\"\u0000"+
		"\u0000\u010f\u0114\u00038\u001c\u0000\u0110\u0111\u0005\u0002\u0000\u0000"+
		"\u0111\u0113\u00038\u001c\u0000\u0112\u0110\u0001\u0000\u0000\u0000\u0113"+
		"\u0116\u0001\u0000\u0000\u0000\u0114\u0112\u0001\u0000\u0000\u0000\u0114"+
		"\u0115\u0001\u0000\u0000\u0000\u0115\u0118\u0001\u0000\u0000\u0000\u0116"+
		"\u0114\u0001\u0000\u0000\u0000\u0117\u010d\u0001\u0000\u0000\u0000\u0117"+
		"\u0118\u0001\u0000\u0000\u0000\u0118\u011b\u0001\u0000\u0000\u0000\u0119"+
		"\u011a\u0005#\u0000\u0000\u011a\u011c\u0003H$\u0000\u011b\u0119\u0001"+
		"\u0000\u0000\u0000\u011b\u011c\u0001\u0000\u0000\u0000\u011c\u011e\u0001"+
		"\u0000\u0000\u0000\u011d\u011f\u0003<\u001e\u0000\u011e\u011d\u0001\u0000"+
		"\u0000\u0000\u011e\u011f\u0001\u0000\u0000\u0000\u011f\u0121\u0001\u0000"+
		"\u0000\u0000\u0120\u0122\u0003@ \u0000\u0121\u0120\u0001\u0000\u0000\u0000"+
		"\u0121\u0122\u0001\u0000\u0000\u0000\u0122\u0013\u0001\u0000\u0000\u0000"+
		"\u0123\u0128\u0003\u0016\u000b\u0000\u0124\u0125\u0005\'\u0000\u0000\u0125"+
		"\u0127\u0003\u0016\u000b\u0000\u0126\u0124\u0001\u0000\u0000\u0000\u0127"+
		"\u012a\u0001\u0000\u0000\u0000\u0128\u0126\u0001\u0000\u0000\u0000\u0128"+
		"\u0129\u0001\u0000\u0000\u0000\u0129\u0015\u0001\u0000\u0000\u0000\u012a"+
		"\u0128\u0001\u0000\u0000\u0000\u012b\u012c\u0003d2\u0000\u012c\u012d\u0003"+
		"l6\u0000\u012d\u012e\u0003^/\u0000\u012e\u0017\u0001\u0000\u0000\u0000"+
		"\u012f\u0130\u0005\u001e\u0000\u0000\u0130\u0131\u0003$\u0012\u0000\u0131"+
		"\u0132\u0005\u001f\u0000\u0000\u0132\u0133\u0003*\u0015\u0000\u0133\u0134"+
		"\u00054\u0000\u0000\u0134\u0135\u0005\u0003\u0000\u0000\u0135\u0136\u0003"+
		"\u001a\r\u0000\u0136\u0137\u0005B\u0000\u0000\u0137\u0138\u0003\u001c"+
		"\u000e\u0000\u0138\u0139\u0005+\u0000\u0000\u0139\u013a\u0005\u0003\u0000"+
		"\u0000\u013a\u013b\u0003\u001e\u000f\u0000\u013b\u013c\u0005\u0004\u0000"+
		"\u0000\u013c\u013f\u0005\u0004\u0000\u0000\u013d\u013e\u0005 \u0000\u0000"+
		"\u013e\u0140\u0003H$\u0000\u013f\u013d\u0001\u0000\u0000\u0000\u013f\u0140"+
		"\u0001\u0000\u0000\u0000\u0140\u014b\u0001\u0000\u0000\u0000\u0141\u0142"+
		"\u0005!\u0000\u0000\u0142\u0143\u0005\"\u0000\u0000\u0143\u0148\u0003"+
		"8\u001c\u0000\u0144\u0145\u0005\u0002\u0000\u0000\u0145\u0147\u00038\u001c"+
		"\u0000\u0146\u0144\u0001\u0000\u0000\u0000\u0147\u014a\u0001\u0000\u0000"+
		"\u0000\u0148\u0146\u0001\u0000\u0000\u0000\u0148\u0149\u0001\u0000\u0000"+
		"\u0000\u0149\u014c\u0001\u0000\u0000\u0000\u014a\u0148\u0001\u0000\u0000"+
		"\u0000\u014b\u0141\u0001\u0000\u0000\u0000\u014b\u014c\u0001\u0000\u0000"+
		"\u0000\u014c\u014f\u0001\u0000\u0000\u0000\u014d\u014e\u0005#\u0000\u0000"+
		"\u014e\u0150\u0003H$\u0000\u014f\u014d\u0001\u0000\u0000\u0000\u014f\u0150"+
		"\u0001\u0000\u0000\u0000\u0150\u0152\u0001\u0000\u0000\u0000\u0151\u0153"+
		"\u0003<\u001e\u0000\u0152\u0151\u0001\u0000\u0000\u0000\u0152\u0153\u0001"+
		"\u0000\u0000\u0000\u0153\u0155\u0001\u0000\u0000\u0000\u0154\u0156\u0003"+
		"@ \u0000\u0155\u0154\u0001\u0000\u0000\u0000\u0155\u0156\u0001\u0000\u0000"+
		"\u0000\u0156\u0019\u0001\u0000\u0000\u0000\u0157\u0158\u0003n7\u0000\u0158"+
		"\u0159\u0005\u0003\u0000\u0000\u0159\u015a\u0003H$\u0000\u015a\u015b\u0005"+
		"\u0004\u0000\u0000\u015b\u001b\u0001\u0000\u0000\u0000\u015c\u015d\u0003"+
		"d2\u0000\u015d\u001d\u0001\u0000\u0000\u0000\u015e\u0163\u0003^/\u0000"+
		"\u015f\u0160\u0005\u0002\u0000\u0000\u0160\u0162\u0003^/\u0000\u0161\u015f"+
		"\u0001\u0000\u0000\u0000\u0162\u0165\u0001\u0000\u0000\u0000\u0163\u0161"+
		"\u0001\u0000\u0000\u0000\u0163\u0164\u0001\u0000\u0000\u0000\u0164\u001f"+
		"\u0001\u0000\u0000\u0000\u0165\u0163\u0001\u0000\u0000\u0000\u0166\u0168"+
		"\u0005\u001e\u0000\u0000\u0167\u0169\u0003\"\u0011\u0000\u0168\u0167\u0001"+
		"\u0000\u0000\u0000\u0168\u0169\u0001\u0000\u0000\u0000\u0169\u016a\u0001"+
		"\u0000\u0000\u0000\u016a\u016c\u0003$\u0012\u0000\u016b\u016d\u0003(\u0014"+
		"\u0000\u016c\u016b\u0001\u0000\u0000\u0000\u016c\u016d\u0001\u0000\u0000"+
		"\u0000\u016d\u0170\u0001\u0000\u0000\u0000\u016e\u016f\u0005 \u0000\u0000"+
		"\u016f\u0171\u0003H$\u0000\u0170\u016e\u0001\u0000\u0000\u0000\u0170\u0171"+
		"\u0001\u0000\u0000\u0000\u0171\u017c\u0001\u0000\u0000\u0000\u0172\u0173"+
		"\u0005!\u0000\u0000\u0173\u0174\u0005\"\u0000\u0000\u0174\u0179\u0003"+
		"8\u001c\u0000\u0175\u0176\u0005\u0002\u0000\u0000\u0176\u0178\u00038\u001c"+
		"\u0000\u0177\u0175\u0001\u0000\u0000\u0000\u0178\u017b\u0001\u0000\u0000"+
		"\u0000\u0179\u0177\u0001\u0000\u0000\u0000\u0179\u017a\u0001\u0000\u0000"+
		"\u0000\u017a\u017d\u0001\u0000\u0000\u0000\u017b\u0179\u0001\u0000\u0000"+
		"\u0000\u017c\u0172\u0001\u0000\u0000\u0000\u017c\u017d\u0001\u0000\u0000"+
		"\u0000\u017d\u0180\u0001\u0000\u0000\u0000\u017e\u017f\u0005#\u0000\u0000"+
		"\u017f\u0181\u0003H$\u0000\u0180\u017e\u0001\u0000\u0000\u0000\u0180\u0181"+
		"\u0001\u0000\u0000\u0000\u0181\u0183\u0001\u0000\u0000\u0000\u0182\u0184"+
		"\u0003<\u001e\u0000\u0183\u0182\u0001\u0000\u0000\u0000\u0183\u0184\u0001"+
		"\u0000\u0000\u0000\u0184\u0186\u0001\u0000\u0000\u0000\u0185\u0187\u0003"+
		"@ \u0000\u0186\u0185\u0001\u0000\u0000\u0000\u0186\u0187\u0001\u0000\u0000"+
		"\u0000\u0187!\u0001\u0000\u0000\u0000\u0188\u0189\u0007\u0000\u0000\u0000"+
		"\u0189#\u0001\u0000\u0000\u0000\u018a\u018d\u0005\u0006\u0000\u0000\u018b"+
		"\u018d\u0003&\u0013\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018c\u018b"+
		"\u0001\u0000\u0000\u0000\u018d\u0192\u0001\u0000\u0000\u0000\u018e\u018f"+
		"\u0005\u0002\u0000\u0000\u018f\u0191\u0003&\u0013\u0000\u0190\u018e\u0001"+
		"\u0000\u0000\u0000\u0191\u0194\u0001\u0000\u0000\u0000\u0192\u0190\u0001"+
		"\u0000\u0000\u0000\u0192\u0193\u0001\u0000\u0000\u0000\u0193%\u0001\u0000"+
		"\u0000\u0000\u0194\u0192\u0001\u0000\u0000\u0000\u0195\u019a\u0003H$\u0000"+
		"\u0196\u0198\u0005-\u0000\u0000\u0197\u0196\u0001\u0000\u0000\u0000\u0197"+
		"\u0198\u0001\u0000\u0000\u0000\u0198\u0199\u0001\u0000\u0000\u0000\u0199"+
		"\u019b\u0003d2\u0000\u019a\u0197\u0001\u0000\u0000\u0000\u019a\u019b\u0001"+
		"\u0000\u0000\u0000\u019b\'\u0001\u0000\u0000\u0000\u019c\u019d\u0005\u001f"+
		"\u0000\u0000\u019d\u019e\u0003*\u0015\u0000\u019e)\u0001\u0000\u0000\u0000"+
		"\u019f\u01a4\u0003,\u0016\u0000\u01a0\u01a1\u0005\u0002\u0000\u0000\u01a1"+
		"\u01a3\u0003,\u0016\u0000\u01a2\u01a0\u0001\u0000\u0000\u0000\u01a3\u01a6"+
		"\u0001\u0000\u0000\u0000\u01a4\u01a2\u0001\u0000\u0000\u0000\u01a4\u01a5"+
		"\u0001\u0000\u0000\u0000\u01a5+\u0001\u0000\u0000\u0000\u01a6\u01a4\u0001"+
		"\u0000\u0000\u0000\u01a7\u01ab\u0003.\u0017\u0000\u01a8\u01aa\u00030\u0018"+
		"\u0000\u01a9\u01a8\u0001\u0000\u0000\u0000\u01aa\u01ad\u0001\u0000\u0000"+
		"\u0000\u01ab\u01a9\u0001\u0000\u0000\u0000\u01ab\u01ac\u0001\u0000\u0000"+
		"\u0000\u01ac\u01b9\u0001\u0000\u0000\u0000\u01ad\u01ab\u0001\u0000\u0000"+
		"\u0000\u01ae\u01af\u0005\u0003\u0000\u0000\u01af\u01b3\u0003.\u0017\u0000"+
		"\u01b0\u01b2\u00030\u0018\u0000\u01b1\u01b0\u0001\u0000\u0000\u0000\u01b2"+
		"\u01b5\u0001\u0000\u0000\u0000\u01b3\u01b1\u0001\u0000\u0000\u0000\u01b3"+
		"\u01b4\u0001\u0000\u0000\u0000\u01b4\u01b6\u0001\u0000\u0000\u0000\u01b5"+
		"\u01b3\u0001\u0000\u0000\u0000\u01b6\u01b7\u0005\u0004\u0000\u0000\u01b7"+
		"\u01b9\u0001\u0000\u0000\u0000\u01b8\u01a7\u0001\u0000\u0000\u0000\u01b8"+
		"\u01ae\u0001\u0000\u0000\u0000\u01b9-\u0001\u0000\u0000\u0000\u01ba\u01bf"+
		"\u0003p8\u0000\u01bb\u01bd\u0005-\u0000\u0000\u01bc\u01bb\u0001\u0000"+
		"\u0000\u0000\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01be\u0001\u0000"+
		"\u0000\u0000\u01be\u01c0\u0003d2\u0000\u01bf\u01bc\u0001\u0000\u0000\u0000"+
		"\u01bf\u01c0\u0001\u0000\u0000\u0000\u01c0\u01c2\u0001\u0000\u0000\u0000"+
		"\u01c1\u01c3\u00032\u0019\u0000\u01c2\u01c1\u0001\u0000\u0000\u0000\u01c2"+
		"\u01c3\u0001\u0000\u0000\u0000\u01c3\u01d5\u0001\u0000\u0000\u0000\u01c4"+
		"\u01ca\u0003\u0002\u0001\u0000\u01c5\u01c6\u0005\u0003\u0000\u0000\u01c6"+
		"\u01c7\u0003\u0002\u0001\u0000\u01c7\u01c8\u0005\u0004\u0000\u0000\u01c8"+
		"\u01ca\u0001\u0000\u0000\u0000\u01c9\u01c4\u0001\u0000\u0000\u0000\u01c9"+
		"\u01c5\u0001\u0000\u0000\u0000\u01ca\u01cc\u0001\u0000\u0000\u0000\u01cb"+
		"\u01cd\u0005-\u0000\u0000\u01cc\u01cb\u0001\u0000\u0000\u0000\u01cc\u01cd"+
		"\u0001\u0000\u0000\u0000\u01cd\u01ce\u0001\u0000\u0000\u0000\u01ce\u01cf"+
		"\u0003d2\u0000\u01cf\u01d5\u0001\u0000\u0000\u0000\u01d0\u01d1\u0005\u0003"+
		"\u0000\u0000\u01d1\u01d2\u0003*\u0015\u0000\u01d2\u01d3\u0005\u0004\u0000"+
		"\u0000\u01d3\u01d5\u0001\u0000\u0000\u0000\u01d4\u01ba\u0001\u0000\u0000"+
		"\u0000\u01d4\u01c9\u0001\u0000\u0000\u0000\u01d4\u01d0\u0001\u0000\u0000"+
		"\u0000\u01d5/\u0001\u0000\u0000\u0000\u01d6\u01d8\u0007\u0001\u0000\u0000"+
		"\u01d7\u01d6\u0001\u0000\u0000\u0000\u01d7\u01d8\u0001\u0000\u0000\u0000"+
		"\u01d8\u01d9\u0001\u0000\u0000\u0000\u01d9\u01da\u00055\u0000\u0000\u01da"+
		"\u01dd\u0003.\u0017\u0000\u01db\u01dc\u0005\u0018\u0000\u0000\u01dc\u01de"+
		"\u0003H$\u0000\u01dd\u01db\u0001\u0000\u0000\u0000\u01dd\u01de\u0001\u0000"+
		"\u0000\u0000\u01de\u01f8\u0001\u0000\u0000\u0000\u01df\u01e0\u0005G\u0000"+
		"\u0000\u01e0\u01e3\u0003.\u0017\u0000\u01e1\u01e2\u0005\u0018\u0000\u0000"+
		"\u01e2\u01e4\u0003H$\u0000\u01e3\u01e1\u0001\u0000\u0000\u0000\u01e3\u01e4"+
		"\u0001\u0000\u0000\u0000\u01e4\u01f8\u0001\u0000\u0000\u0000\u01e5\u01e7"+
		"\u0007\u0002\u0000\u0000\u01e6\u01e8\u00057\u0000\u0000\u01e7\u01e6\u0001"+
		"\u0000\u0000\u0000\u01e7\u01e8\u0001\u0000\u0000\u0000\u01e8\u01e9\u0001"+
		"\u0000\u0000\u0000\u01e9\u01ea\u00055\u0000\u0000\u01ea\u01eb\u0003.\u0017"+
		"\u0000\u01eb\u01ec\u0005\u0018\u0000\u0000\u01ec\u01ed\u0003H$\u0000\u01ed"+
		"\u01f8\u0001\u0000\u0000\u0000\u01ee\u01f3\u0005<\u0000\u0000\u01ef\u01f1"+
		"\u0007\u0002\u0000\u0000\u01f0\u01f2\u00057\u0000\u0000\u01f1\u01f0\u0001"+
		"\u0000\u0000\u0000\u01f1\u01f2\u0001\u0000\u0000\u0000\u01f2\u01f4\u0001"+
		"\u0000\u0000\u0000\u01f3\u01ef\u0001\u0000\u0000\u0000\u01f3\u01f4\u0001"+
		"\u0000\u0000\u0000\u01f4\u01f5\u0001\u0000\u0000\u0000\u01f5\u01f6\u0005"+
		"5\u0000\u0000\u01f6\u01f8\u0003.\u0017\u0000\u01f7\u01d7\u0001\u0000\u0000"+
		"\u0000\u01f7\u01df\u0001\u0000\u0000\u0000\u01f7\u01e5\u0001\u0000\u0000"+
		"\u0000\u01f7\u01ee\u0001\u0000\u0000\u0000\u01f81\u0001\u0000\u0000\u0000"+
		"\u01f9\u01fe\u00034\u001a\u0000\u01fa\u01fb\u0005\u0002\u0000\u0000\u01fb"+
		"\u01fd\u00034\u001a\u0000\u01fc\u01fa\u0001\u0000\u0000\u0000\u01fd\u0200"+
		"\u0001\u0000\u0000\u0000\u01fe\u01fc\u0001\u0000\u0000\u0000\u01fe\u01ff"+
		"\u0001\u0000\u0000\u0000\u01ff3\u0001\u0000\u0000\u0000\u0200\u01fe\u0001"+
		"\u0000\u0000\u0000\u0201\u0202\u0007\u0003\u0000\u0000\u0202\u020b\u0007"+
		"\u0004\u0000\u0000\u0203\u0209\u0005B\u0000\u0000\u0204\u020a\u00055\u0000"+
		"\u0000\u0205\u0206\u0005$\u0000\u0000\u0206\u020a\u0005\"\u0000\u0000"+
		"\u0207\u0208\u0005!\u0000\u0000\u0208\u020a\u0005\"\u0000\u0000\u0209"+
		"\u0204\u0001\u0000\u0000\u0000\u0209\u0205\u0001\u0000\u0000\u0000\u0209"+
		"\u0207\u0001\u0000\u0000\u0000\u020a\u020c\u0001\u0000\u0000\u0000\u020b"+
		"\u0203\u0001\u0000\u0000\u0000\u020b\u020c\u0001\u0000\u0000\u0000\u020c"+
		"\u020d\u0001\u0000\u0000\u0000\u020d\u020e\u0005\u0003\u0000\u0000\u020e"+
		"\u020f\u00036\u001b\u0000\u020f\u0210\u0005\u0004\u0000\u0000\u02105\u0001"+
		"\u0000\u0000\u0000\u0211\u0216\u0003d2\u0000\u0212\u0213\u0005\u0002\u0000"+
		"\u0000\u0213\u0215\u0003d2\u0000\u0214\u0212\u0001\u0000\u0000\u0000\u0215"+
		"\u0218\u0001\u0000\u0000\u0000\u0216\u0214\u0001\u0000\u0000\u0000\u0216"+
		"\u0217\u0001\u0000\u0000\u0000\u02177\u0001\u0000\u0000\u0000\u0218\u0216"+
		"\u0001\u0000\u0000\u0000\u0219\u021b\u0003H$\u0000\u021a\u021c\u0007\u0005"+
		"\u0000\u0000\u021b\u021a\u0001\u0000\u0000\u0000\u021b\u021c\u0001\u0000"+
		"\u0000\u0000\u021c9\u0001\u0000\u0000\u0000\u021d\u021e\u0003H$\u0000"+
		"\u021e;\u0001\u0000\u0000\u0000\u021f\u0220\u0005$\u0000\u0000\u0220\u0221"+
		"\u0005\"\u0000\u0000\u0221\u0226\u0003>\u001f\u0000\u0222\u0223\u0005"+
		"\u0002\u0000\u0000\u0223\u0225\u0003>\u001f\u0000\u0224\u0222\u0001\u0000"+
		"\u0000\u0000\u0225\u0228\u0001\u0000\u0000\u0000\u0226\u0224\u0001\u0000"+
		"\u0000\u0000\u0226\u0227\u0001\u0000\u0000\u0000\u0227=\u0001\u0000\u0000"+
		"\u0000\u0228\u0226\u0001\u0000\u0000\u0000\u0229\u022b\u0003H$\u0000\u022a"+
		"\u022c\u0007\u0005\u0000\u0000\u022b\u022a\u0001\u0000\u0000\u0000\u022b"+
		"\u022c\u0001\u0000\u0000\u0000\u022c?\u0001\u0000\u0000\u0000\u022d\u022e"+
		"\u0005%\u0000\u0000\u022e\u0232\u0003B!\u0000\u022f\u0230\u0005%\u0000"+
		"\u0000\u0230\u0232\u0003D\"\u0000\u0231\u022d\u0001\u0000\u0000\u0000"+
		"\u0231\u022f\u0001\u0000\u0000\u0000\u0232A\u0001\u0000\u0000\u0000\u0233"+
		"\u0234\u0003H$\u0000\u0234C\u0001\u0000\u0000\u0000\u0235\u0236\u0003"+
		"H$\u0000\u0236\u0237\u0005\u0002\u0000\u0000\u0237\u0238\u0003H$\u0000"+
		"\u0238E\u0001\u0000\u0000\u0000\u0239\u023a\u0003d2\u0000\u023a\u023b"+
		"\u0005-\u0000\u0000\u023b\u023c\u0005\u0003\u0000\u0000\u023c\u023d\u0003"+
		"\u0002\u0001\u0000\u023d\u023e\u0005\u0004\u0000\u0000\u023eG\u0001\u0000"+
		"\u0000\u0000\u023f\u0240\u0006$\uffff\uffff\u0000\u0240\u0241\u0007\u0006"+
		"\u0000\u0000\u0241\u024f\u0003H$\u0007\u0242\u0243\u0005+\u0000\u0000"+
		"\u0243\u0244\u0003V+\u0000\u0244\u0245\u0003H$\u0005\u0245\u024f\u0001"+
		"\u0000\u0000\u0000\u0246\u0247\u0005\u001b\u0000\u0000\u0247\u024f\u0003"+
		"H$\u0004\u0248\u024f\u0003J%\u0000\u0249\u024f\u0003 \u0010\u0000\u024a"+
		"\u024b\u0005\u0003\u0000\u0000\u024b\u024c\u0003H$\u0000\u024c\u024d\u0005"+
		"\u0004\u0000\u0000\u024d\u024f\u0001\u0000\u0000\u0000\u024e\u023f\u0001"+
		"\u0000\u0000\u0000\u024e\u0242\u0001\u0000\u0000\u0000\u024e\u0246\u0001"+
		"\u0000\u0000\u0000\u024e\u0248\u0001\u0000\u0000\u0000\u024e\u0249\u0001"+
		"\u0000\u0000\u0000\u024e\u024a\u0001\u0000\u0000\u0000\u024f\u0256\u0001"+
		"\u0000\u0000\u0000\u0250\u0251\n\u0006\u0000\u0000\u0251\u0252\u0003V"+
		"+\u0000\u0252\u0253\u0003H$\u0007\u0253\u0255\u0001\u0000\u0000\u0000"+
		"\u0254\u0250\u0001\u0000\u0000\u0000\u0255\u0258\u0001\u0000\u0000\u0000"+
		"\u0256\u0254\u0001\u0000\u0000\u0000\u0256\u0257\u0001\u0000\u0000\u0000"+
		"\u0257I\u0001\u0000\u0000\u0000\u0258\u0256\u0001\u0000\u0000\u0000\u0259"+
		"\u025a\u0006%\uffff\uffff\u0000\u025a\u0261\u0003L&\u0000\u025b\u025c"+
		"\u0005\u010a\u0000\u0000\u025c\u025e\u0005\u010b\u0000\u0000\u025d\u025b"+
		"\u0001\u0000\u0000\u0000\u025d\u025e\u0001\u0000\u0000\u0000\u025e\u025f"+
		"\u0001\u0000\u0000\u0000\u025f\u0261\u0003L&\u0000\u0260\u0259\u0001\u0000"+
		"\u0000\u0000\u0260\u025d\u0001\u0000\u0000\u0000\u0261\u0293\u0001\u0000"+
		"\u0000\u0000\u0262\u0263\n\u0006\u0000\u0000\u0263\u0264\u0003X,\u0000"+
		"\u0264\u0265\u0003J%\u0007\u0265\u0292\u0001\u0000\u0000\u0000\u0266\u0268"+
		"\n\u0005\u0000\u0000\u0267\u0269\u0005*\u0000\u0000\u0268\u0267\u0001"+
		"\u0000\u0000\u0000\u0268\u0269\u0001\u0000\u0000\u0000\u0269\u026a\u0001"+
		"\u0000\u0000\u0000\u026a\u026b\u0005,\u0000\u0000\u026b\u026c\u0003J%"+
		"\u0000\u026c\u026d\u0005\'\u0000\u0000\u026d\u026e\u0003J%\u0006\u026e"+
		"\u0292\u0001\u0000\u0000\u0000\u026f\u0271\n\u0002\u0000\u0000\u0270\u0272"+
		"\u0005*\u0000\u0000\u0271\u0270\u0001\u0000\u0000\u0000\u0271\u0272\u0001"+
		"\u0000\u0000\u0000\u0272\u0273\u0001\u0000\u0000\u0000\u0273\u0274\u0007"+
		"\u0007\u0000\u0000\u0274\u0292\u0003J%\u0003\u0275\u0276\n\u0007\u0000"+
		"\u0000\u0276\u0278\u0005\u0019\u0000\u0000\u0277\u0279\u0005*\u0000\u0000"+
		"\u0278\u0277\u0001\u0000\u0000\u0000\u0278\u0279\u0001\u0000\u0000\u0000"+
		"\u0279\u027a\u0001\u0000\u0000\u0000\u027a\u0292\u0005\u001a\u0000\u0000"+
		"\u027b\u027d\n\u0004\u0000\u0000\u027c\u027e\u0005*\u0000\u0000\u027d"+
		"\u027c\u0001\u0000\u0000\u0000\u027d\u027e\u0001\u0000\u0000\u0000\u027e"+
		"\u027f\u0001\u0000\u0000\u0000\u027f\u0280\u0005+\u0000\u0000\u0280\u0283"+
		"\u0005\u0003\u0000\u0000\u0281\u0284\u0003\u0002\u0001\u0000\u0282\u0284"+
		"\u0003N\'\u0000\u0283\u0281\u0001\u0000\u0000\u0000\u0283\u0282\u0001"+
		"\u0000\u0000\u0000\u0284\u0285\u0001\u0000\u0000\u0000\u0285\u0286\u0005"+
		"\u0004\u0000\u0000\u0286\u0292\u0001\u0000\u0000\u0000\u0287\u0289\n\u0003"+
		"\u0000\u0000\u0288\u028a\u0005*\u0000\u0000\u0289\u0288\u0001\u0000\u0000"+
		"\u0000\u0289\u028a\u0001\u0000\u0000\u0000\u028a\u028b\u0001\u0000\u0000"+
		"\u0000\u028b\u028c\u0005\u010e\u0000\u0000\u028c\u028f\u0003J%\u0000\u028d"+
		"\u028e\u0005\u010f\u0000\u0000\u028e\u0290\u0005\u0103\u0000\u0000\u028f"+
		"\u028d\u0001\u0000\u0000\u0000\u028f\u0290\u0001\u0000\u0000\u0000\u0290"+
		"\u0292\u0001\u0000\u0000\u0000\u0291\u0262\u0001\u0000\u0000\u0000\u0291"+
		"\u0266\u0001\u0000\u0000\u0000\u0291\u026f\u0001\u0000\u0000\u0000\u0291"+
		"\u0275\u0001\u0000\u0000\u0000\u0291\u027b\u0001\u0000\u0000\u0000\u0291"+
		"\u0287\u0001\u0000\u0000\u0000\u0292\u0295\u0001\u0000\u0000\u0000\u0293"+
		"\u0291\u0001\u0000\u0000\u0000\u0293\u0294\u0001\u0000\u0000\u0000\u0294"+
		"K\u0001\u0000\u0000\u0000\u0295\u0293\u0001\u0000\u0000\u0000\u0296\u0297"+
		"\u0006&\uffff\uffff\u0000\u0297\u02ad\u0003^/\u0000\u0298\u02ad\u0003"+
		"b1\u0000\u0299\u02ad\u0003P(\u0000\u029a\u029b\u0005\u0003\u0000\u0000"+
		"\u029b\u02a0\u0003H$\u0000\u029c\u029d\u0005\u0002\u0000\u0000\u029d\u029f"+
		"\u0003H$\u0000\u029e\u029c\u0001\u0000\u0000\u0000\u029f\u02a2\u0001\u0000"+
		"\u0000\u0000\u02a0\u029e\u0001\u0000\u0000\u0000\u02a0\u02a1\u0001\u0000"+
		"\u0000\u0000\u02a1\u02a3\u0001\u0000\u0000\u0000\u02a2\u02a0\u0001\u0000"+
		"\u0000\u0000\u02a3\u02a4\u0005\u0004\u0000\u0000\u02a4\u02ad\u0001\u0000"+
		"\u0000\u0000\u02a5\u02a6\u0005\u0003\u0000\u0000\u02a6\u02a7\u0003\u0002"+
		"\u0001\u0000\u02a7\u02a8\u0005\u0004\u0000\u0000\u02a8\u02ad\u0001\u0000"+
		"\u0000\u0000\u02a9\u02aa\u0003\\.\u0000\u02aa\u02ab\u0003L&\u0001\u02ab"+
		"\u02ad\u0001\u0000\u0000\u0000\u02ac\u0296\u0001\u0000\u0000\u0000\u02ac"+
		"\u0298\u0001\u0000\u0000\u0000\u02ac\u0299\u0001\u0000\u0000\u0000\u02ac"+
		"\u029a\u0001\u0000\u0000\u0000\u02ac\u02a5\u0001\u0000\u0000\u0000\u02ac"+
		"\u02a9\u0001\u0000\u0000\u0000\u02ad\u02b4\u0001\u0000\u0000\u0000\u02ae"+
		"\u02af\n\u0002\u0000\u0000\u02af\u02b0\u0003Z-\u0000\u02b0\u02b1\u0003"+
		"L&\u0003\u02b1\u02b3\u0001\u0000\u0000\u0000\u02b2\u02ae\u0001\u0000\u0000"+
		"\u0000\u02b3\u02b6\u0001\u0000\u0000\u0000\u02b4\u02b2\u0001\u0000\u0000"+
		"\u0000\u02b4\u02b5\u0001\u0000\u0000\u0000\u02b5M\u0001\u0000\u0000\u0000"+
		"\u02b6\u02b4\u0001\u0000\u0000\u0000\u02b7\u02bc\u0003H$\u0000\u02b8\u02b9"+
		"\u0005\u0002\u0000\u0000\u02b9\u02bb\u0003H$\u0000\u02ba\u02b8\u0001\u0000"+
		"\u0000\u0000\u02bb\u02be\u0001\u0000\u0000\u0000\u02bc\u02ba\u0001\u0000"+
		"\u0000\u0000\u02bc\u02bd\u0001\u0000\u0000\u0000\u02bdO\u0001\u0000\u0000"+
		"\u0000\u02be\u02bc\u0001\u0000\u0000\u0000\u02bf\u02cf\u0003R)\u0000\u02c0"+
		"\u02c1\u0003T*\u0000\u02c1\u02c3\u0005\u0003\u0000\u0000\u02c2\u02c4\u0003"+
		"t:\u0000\u02c3\u02c2\u0001\u0000\u0000\u0000\u02c3\u02c4\u0001\u0000\u0000"+
		"\u0000\u02c4\u02c5\u0001\u0000\u0000\u0000\u02c5\u02c6\u0005\u0004\u0000"+
		"\u0000\u02c6\u02cf\u0001\u0000\u0000\u0000\u02c7\u02c8\u0003d2\u0000\u02c8"+
		"\u02ca\u0005\u0003\u0000\u0000\u02c9\u02cb\u0003t:\u0000\u02ca\u02c9\u0001"+
		"\u0000\u0000\u0000\u02ca\u02cb\u0001\u0000\u0000\u0000\u02cb\u02cc\u0001"+
		"\u0000\u0000\u0000\u02cc\u02cd\u0005\u0004\u0000\u0000\u02cd\u02cf\u0001"+
		"\u0000\u0000\u0000\u02ce\u02bf\u0001\u0000\u0000\u0000\u02ce\u02c0\u0001"+
		"\u0000\u0000\u0000\u02ce\u02c7\u0001\u0000\u0000\u0000\u02cfQ\u0001\u0000"+
		"\u0000\u0000\u02d0\u02d1\u0007\b\u0000\u0000\u02d1\u02d3\u0005\u0003\u0000"+
		"\u0000\u02d2\u02d4\u0005C\u0000\u0000\u02d3\u02d2\u0001\u0000\u0000\u0000"+
		"\u02d3\u02d4\u0001\u0000\u0000\u0000\u02d4\u02d5\u0001\u0000\u0000\u0000"+
		"\u02d5\u02d6\u0003H$\u0000\u02d6\u02d7\u0005\u0004\u0000\u0000\u02d7\u02ff"+
		"\u0001\u0000\u0000\u0000\u02d8\u02d9\u0005U\u0000\u0000\u02d9\u02df\u0005"+
		"\u0003\u0000\u0000\u02da\u02e0\u0005\u0006\u0000\u0000\u02db\u02dd\u0005"+
		"C\u0000\u0000\u02dc\u02db\u0001\u0000\u0000\u0000\u02dc\u02dd\u0001\u0000"+
		"\u0000\u0000\u02dd\u02de\u0001\u0000\u0000\u0000\u02de\u02e0\u0003H$\u0000"+
		"\u02df\u02da\u0001\u0000\u0000\u0000\u02df\u02dc\u0001\u0000\u0000\u0000"+
		"\u02e0\u02e1\u0001\u0000\u0000\u0000\u02e1\u02ff\u0005\u0004\u0000\u0000"+
		"\u02e2\u02e3\u0007\t\u0000\u0000\u02e3\u02e4\u0005\u0003\u0000\u0000\u02e4"+
		"\u02e5\u0005D\u0000\u0000\u02e5\u02e6\u0003H$\u0000\u02e6\u02e7\u0005"+
		"\u0004\u0000\u0000\u02e7\u02ff\u0001\u0000\u0000\u0000\u02e8\u02e9\u0005"+
		"V\u0000\u0000\u02e9\u02eb\u0005\u0003\u0000\u0000\u02ea\u02ec\u0005D\u0000"+
		"\u0000\u02eb\u02ea\u0001\u0000\u0000\u0000\u02eb\u02ec\u0001\u0000\u0000"+
		"\u0000\u02ec\u02ed\u0001\u0000\u0000\u0000\u02ed\u02f2\u0003H$\u0000\u02ee"+
		"\u02ef\u0005\u0002\u0000\u0000\u02ef\u02f1\u0003H$\u0000\u02f0\u02ee\u0001"+
		"\u0000\u0000\u0000\u02f1\u02f4\u0001\u0000\u0000\u0000\u02f2\u02f0\u0001"+
		"\u0000\u0000\u0000\u02f2\u02f3\u0001\u0000\u0000\u0000\u02f3\u02f6\u0001"+
		"\u0000\u0000\u0000\u02f4\u02f2\u0001\u0000\u0000\u0000\u02f5\u02f7\u0003"+
		"<\u001e\u0000\u02f6\u02f5\u0001\u0000\u0000\u0000\u02f6\u02f7\u0001\u0000"+
		"\u0000\u0000\u02f7\u02fa\u0001\u0000\u0000\u0000\u02f8\u02f9\u0005\u0110"+
		"\u0000\u0000\u02f9\u02fb\u0005\u0103\u0000\u0000\u02fa\u02f8\u0001\u0000"+
		"\u0000\u0000\u02fa\u02fb\u0001\u0000\u0000\u0000\u02fb\u02fc\u0001\u0000"+
		"\u0000\u0000\u02fc\u02fd\u0005\u0004\u0000\u0000\u02fd\u02ff\u0001\u0000"+
		"\u0000\u0000\u02fe\u02d0\u0001\u0000\u0000\u0000\u02fe\u02d8\u0001\u0000"+
		"\u0000\u0000\u02fe\u02e2\u0001\u0000\u0000\u0000\u02fe\u02e8\u0001\u0000"+
		"\u0000\u0000\u02ffS\u0001\u0000\u0000\u0000\u0300\u0301\u0007\n\u0000"+
		"\u0000\u0301U\u0001\u0000\u0000\u0000\u0302\u030a\u0005\'\u0000\u0000"+
		"\u0303\u0304\u0005\b\u0000\u0000\u0304\u030a\u0005\b\u0000\u0000\u0305"+
		"\u030a\u0005)\u0000\u0000\u0306\u030a\u0005(\u0000\u0000\u0307\u0308\u0005"+
		"\t\u0000\u0000\u0308\u030a\u0005\t\u0000\u0000\u0309\u0302\u0001\u0000"+
		"\u0000\u0000\u0309\u0303\u0001\u0000\u0000\u0000\u0309\u0305\u0001\u0000"+
		"\u0000\u0000\u0309\u0306\u0001\u0000\u0000\u0000\u0309\u0307\u0001\u0000"+
		"\u0000\u0000\u030aW\u0001\u0000\u0000\u0000\u030b\u031a\u0005\u0005\u0000"+
		"\u0000\u030c\u031a\u0005\n\u0000\u0000\u030d\u031a\u0005\u000b\u0000\u0000"+
		"\u030e\u030f\u0005\u000b\u0000\u0000\u030f\u031a\u0005\u0005\u0000\u0000"+
		"\u0310\u0311\u0005\n\u0000\u0000\u0311\u031a\u0005\u0005\u0000\u0000\u0312"+
		"\u0313\u0005\u000b\u0000\u0000\u0313\u031a\u0005\n\u0000\u0000\u0314\u0315"+
		"\u0005\u0007\u0000\u0000\u0315\u031a\u0005\u0005\u0000\u0000\u0316\u0317"+
		"\u0005\u000b\u0000\u0000\u0317\u0318\u0005\u0005\u0000\u0000\u0318\u031a"+
		"\u0005\n\u0000\u0000\u0319\u030b\u0001\u0000\u0000\u0000\u0319\u030c\u0001"+
		"\u0000\u0000\u0000\u0319\u030d\u0001\u0000\u0000\u0000\u0319\u030e\u0001"+
		"\u0000\u0000\u0000\u0319\u0310\u0001\u0000\u0000\u0000\u0319\u0312\u0001"+
		"\u0000\u0000\u0000\u0319\u0314\u0001\u0000\u0000\u0000\u0319\u0316\u0001"+
		"\u0000\u0000\u0000\u031aY\u0001\u0000\u0000\u0000\u031b\u031c\u0007\u000b"+
		"\u0000\u0000\u031c[\u0001\u0000\u0000\u0000\u031d\u031e\u0007\f\u0000"+
		"\u0000\u031e]\u0001\u0000\u0000\u0000\u031f\u0328\u0005\u0103\u0000\u0000"+
		"\u0320\u0328\u0005\u0104\u0000\u0000\u0321\u0322\u0005\u000f\u0000\u0000"+
		"\u0322\u0328\u0005\u0104\u0000\u0000\u0323\u0328\u0005\u0105\u0000\u0000"+
		"\u0324\u0328\u0005\u0106\u0000\u0000\u0325\u0328\u0003`0\u0000\u0326\u0328"+
		"\u0005P\u0000\u0000\u0327\u031f\u0001\u0000\u0000\u0000\u0327\u0320\u0001"+
		"\u0000\u0000\u0000\u0327\u0321\u0001\u0000\u0000\u0000\u0327\u0323\u0001"+
		"\u0000\u0000\u0000\u0327\u0324\u0001\u0000\u0000\u0000\u0327\u0325\u0001"+
		"\u0000\u0000\u0000\u0327\u0326\u0001\u0000\u0000\u0000\u0328_\u0001\u0000"+
		"\u0000\u0000\u0329\u032a\u0007\r\u0000\u0000\u032aa\u0001\u0000\u0000"+
		"\u0000\u032b\u0330\u0003d2\u0000\u032c\u032e\u0003h4\u0000\u032d\u032f"+
		"\u0003h4\u0000\u032e\u032d\u0001\u0000\u0000\u0000\u032e\u032f\u0001\u0000"+
		"\u0000\u0000\u032f\u0331\u0001\u0000\u0000\u0000\u0330\u032c\u0001\u0000"+
		"\u0000\u0000\u0330\u0331\u0001\u0000\u0000\u0000\u0331c\u0001\u0000\u0000"+
		"\u0000\u0332\u0335\u0003f3\u0000\u0333\u0335\u0005\u0103\u0000\u0000\u0334"+
		"\u0332\u0001\u0000\u0000\u0000\u0334\u0333\u0001\u0000\u0000\u0000\u0335"+
		"e\u0001\u0000\u0000\u0000\u0336\u033a\u0005\u0102\u0000\u0000\u0337\u033a"+
		"\u0003T*\u0000\u0338\u033a\u0003j5\u0000\u0339\u0336\u0001\u0000\u0000"+
		"\u0000\u0339\u0337\u0001\u0000\u0000\u0000\u0339\u0338\u0001\u0000\u0000"+
		"\u0000\u033ag\u0001\u0000\u0000\u0000\u033b\u033c\u0005\u0012\u0000\u0000"+
		"\u033c\u033d\u0003d2\u0000\u033di\u0001\u0000\u0000\u0000\u033e\u033f"+
		"\u0007\u000e\u0000\u0000\u033fk\u0001\u0000\u0000\u0000\u0340\u0363\u0005"+
		"\u0005\u0000\u0000\u0341\u0363\u0005\n\u0000\u0000\u0342\u0363\u0005\u000b"+
		"\u0000\u0000\u0343\u0363\u0005\u0013\u0000\u0000\u0344\u0363\u0005\u0014"+
		"\u0000\u0000\u0345\u0363\u0005\u0015\u0000\u0000\u0346\u0363\u0005\u0016"+
		"\u0000\u0000\u0347\u0363\u0005\u0017\u0000\u0000\u0348\u034a\u0005\u0019"+
		"\u0000\u0000\u0349\u034b\u0005*\u0000\u0000\u034a\u0349\u0001\u0000\u0000"+
		"\u0000\u034a\u034b\u0001\u0000\u0000\u0000\u034b\u0363\u0001\u0000\u0000"+
		"\u0000\u034c\u034e\u0005*\u0000\u0000\u034d\u034c\u0001\u0000\u0000\u0000"+
		"\u034d\u034e\u0001\u0000\u0000\u0000\u034e\u034f\u0001\u0000\u0000\u0000"+
		"\u034f\u0363\u0005\u010e\u0000\u0000\u0350\u0352\u0005*\u0000\u0000\u0351"+
		"\u0350\u0001\u0000\u0000\u0000\u0351\u0352\u0001\u0000\u0000\u0000\u0352"+
		"\u0353\u0001\u0000\u0000\u0000\u0353\u0363\u0005+\u0000\u0000\u0354\u0356"+
		"\u0005*\u0000\u0000\u0355\u0354\u0001\u0000\u0000\u0000\u0355\u0356\u0001"+
		"\u0000\u0000\u0000\u0356\u0357\u0001\u0000\u0000\u0000\u0357\u0363\u0005"+
		",\u0000\u0000\u0358\u035a\u0005*\u0000\u0000\u0359\u0358\u0001\u0000\u0000"+
		"\u0000\u0359\u035a\u0001\u0000\u0000\u0000\u035a\u035b\u0001\u0000\u0000"+
		"\u0000\u035b\u0363\u0005\u010c\u0000\u0000\u035c\u035e\u0005*\u0000\u0000"+
		"\u035d\u035c\u0001\u0000\u0000\u0000\u035d\u035e\u0001\u0000\u0000\u0000"+
		"\u035e\u035f\u0001\u0000\u0000\u0000\u035f\u0363\u0005\u010d\u0000\u0000"+
		"\u0360\u0361\u0005\u0112\u0000\u0000\u0361\u0363\u0005\u010e\u0000\u0000"+
		"\u0362\u0340\u0001\u0000\u0000\u0000\u0362\u0341\u0001\u0000\u0000\u0000"+
		"\u0362\u0342\u0001\u0000\u0000\u0000\u0362\u0343\u0001\u0000\u0000\u0000"+
		"\u0362\u0344\u0001\u0000\u0000\u0000\u0362\u0345\u0001\u0000\u0000\u0000"+
		"\u0362\u0346\u0001\u0000\u0000\u0000\u0362\u0347\u0001\u0000\u0000\u0000"+
		"\u0362\u0348\u0001\u0000\u0000\u0000\u0362\u034d\u0001\u0000\u0000\u0000"+
		"\u0362\u0351\u0001\u0000\u0000\u0000\u0362\u0355\u0001\u0000\u0000\u0000"+
		"\u0362\u0359\u0001\u0000\u0000\u0000\u0362\u035d\u0001\u0000\u0000\u0000"+
		"\u0362\u0360\u0001\u0000\u0000\u0000\u0363m\u0001\u0000\u0000\u0000\u0364"+
		"\u0365\u0007\u000f\u0000\u0000\u0365o\u0001\u0000\u0000\u0000\u0366\u0367"+
		"\u0003d2\u0000\u0367\u0368\u0005\u0012\u0000\u0000\u0368\u036a\u0001\u0000"+
		"\u0000\u0000\u0369\u0366\u0001\u0000\u0000\u0000\u0369\u036a\u0001\u0000"+
		"\u0000\u0000\u036a\u036b\u0001\u0000\u0000\u0000\u036b\u036c\u0003d2\u0000"+
		"\u036cq\u0001\u0000\u0000\u0000\u036d\u036e\u0003d2\u0000\u036es\u0001"+
		"\u0000\u0000\u0000\u036f\u0374\u0003^/\u0000\u0370\u0374\u0003b1\u0000"+
		"\u0371\u0374\u0003P(\u0000\u0372\u0374\u0003H$\u0000\u0373\u036f\u0001"+
		"\u0000\u0000\u0000\u0373\u0370\u0001\u0000\u0000\u0000\u0373\u0371\u0001"+
		"\u0000\u0000\u0000\u0373\u0372\u0001\u0000\u0000\u0000\u0374\u037e\u0001"+
		"\u0000\u0000\u0000\u0375\u037a\u0005\u0002\u0000\u0000\u0376\u037b\u0003"+
		"^/\u0000\u0377\u037b\u0003b1\u0000\u0378\u037b\u0003P(\u0000\u0379\u037b"+
		"\u0003H$\u0000\u037a\u0376\u0001\u0000\u0000\u0000\u037a\u0377\u0001\u0000"+
		"\u0000\u0000\u037a\u0378\u0001\u0000\u0000\u0000\u037a\u0379\u0001\u0000"+
		"\u0000\u0000\u037b\u037d\u0001\u0000\u0000\u0000\u037c\u0375\u0001\u0000"+
		"\u0000\u0000\u037d\u0380\u0001\u0000\u0000\u0000\u037e\u037c\u0001\u0000"+
		"\u0000\u0000\u037e\u037f\u0001\u0000\u0000\u0000\u037f\u038e\u0001\u0000"+
		"\u0000\u0000\u0380\u037e\u0001\u0000\u0000\u0000\u0381\u038e\u0005\u0006"+
		"\u0000\u0000\u0382\u0384\u0005D\u0000\u0000\u0383\u0382\u0001\u0000\u0000"+
		"\u0000\u0383\u0384\u0001\u0000\u0000\u0000\u0384\u0385\u0001\u0000\u0000"+
		"\u0000\u0385\u038a\u0003v;\u0000\u0386\u0387\u0005\u0002\u0000\u0000\u0387"+
		"\u0389\u0003v;\u0000\u0388\u0386\u0001\u0000\u0000\u0000\u0389\u038c\u0001"+
		"\u0000\u0000\u0000\u038a\u0388\u0001\u0000\u0000\u0000\u038a\u038b\u0001"+
		"\u0000\u0000\u0000\u038b\u038e\u0001\u0000\u0000\u0000\u038c\u038a\u0001"+
		"\u0000\u0000\u0000\u038d\u0373\u0001\u0000\u0000\u0000\u038d\u0381\u0001"+
		"\u0000\u0000\u0000\u038d\u0383\u0001\u0000\u0000\u0000\u038eu\u0001\u0000"+
		"\u0000\u0000\u038f\u0390\u0003H$\u0000\u0390w\u0001\u0000\u0000\u0000"+
		"yz\u0080\u0087\u008d\u0091\u0098\u00a0\u00a9\u00b0\u00b3\u00b6\u00be\u00c7"+
		"\u00d1\u00d4\u00d7\u00de\u00f1\u00f4\u00f8\u00fb\u00fe\u0114\u0117\u011b"+
		"\u011e\u0121\u0128\u013f\u0148\u014b\u014f\u0152\u0155\u0163\u0168\u016c"+
		"\u0170\u0179\u017c\u0180\u0183\u0186\u018c\u0192\u0197\u019a\u01a4\u01ab"+
		"\u01b3\u01b8\u01bc\u01bf\u01c2\u01c9\u01cc\u01d4\u01d7\u01dd\u01e3\u01e7"+
		"\u01f1\u01f3\u01f7\u01fe\u0209\u020b\u0216\u021b\u0226\u022b\u0231\u024e"+
		"\u0256\u025d\u0260\u0268\u0271\u0278\u027d\u0283\u0289\u028f\u0291\u0293"+
		"\u02a0\u02ac\u02b4\u02bc\u02c3\u02ca\u02ce\u02d3\u02dc\u02df\u02eb\u02f2"+
		"\u02f6\u02fa\u02fe\u0309\u0319\u0327\u032e\u0330\u0334\u0339\u034a\u034d"+
		"\u0351\u0355\u0359\u035d\u0362\u0369\u0373\u037a\u037e\u0383\u038a\u038d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}