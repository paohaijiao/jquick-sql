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
		T__17=18, T__18=19, T__19=20, T__20=21, ON=22, REGEXP=23, LIKE=24, IS=25, 
		NULL=26, EXISTS=27, DESC=28, ASC=29, SELECT=30, FROM=31, WHERE=32, GROUP=33, 
		BY=34, HAVING=35, ORDER=36, LIMIT=37, OFFSET=38, AND=39, OR=40, XOR=41, 
		NOT=42, IN=43, BETWEEN=44, AS=45, WITH=46, RECURSIVE=47, ROLLUP=48, DRILLDOWN=49, 
		SLICE=50, DICE=51, PIVOT=52, JOIN=53, INNER=54, OUTER=55, LEFT=56, RIGHT=57, 
		FULL=58, CROSS=59, NATURAL=60, USE=61, KEY=62, FOR=63, UNION=64, ALL=65, 
		DISTINCT=66, TRUE=67, FALSE=68, IDENTIFIER=69, STRING_LITERAL=70, DECIMAL_LITERAL=71, 
		HEXADECIMAL_LITERAL=72, BIT_STRING=73, WS=74, COMMENT=75, LINE_COMMENT=76;
	public static final int
		RULE_query = 0, RULE_selectStatement = 1, RULE_selectExpression = 2, RULE_selectClause = 3, 
		RULE_whereClause = 4, RULE_groupByClause = 5, RULE_havingClause = 6, RULE_selectSpec = 7, 
		RULE_selectElements = 8, RULE_selectElement = 9, RULE_fromClause = 10, 
		RULE_joinClause = 11, RULE_tableNameItem = 12, RULE_tableNameSpec = 13, 
		RULE_joinType = 14, RULE_groupByItem = 15, RULE_orderByClause = 16, RULE_orderByExpression = 17, 
		RULE_limitClause = 18, RULE_limitOnly = 19, RULE_limitWithOffset = 20, 
		RULE_commonTableExpression = 21, RULE_columnNames = 22, RULE_initialQuery = 23, 
		RULE_recursivePart = 24, RULE_functionCall = 25, RULE_functionArgs = 26, 
		RULE_functionArg = 27, RULE_filterCondition = 28, RULE_predicate = 29, 
		RULE_expressionAtom = 30, RULE_expressions = 31, RULE_expression = 32, 
		RULE_mathOperator = 33, RULE_unaryOperator = 34, RULE_logicalOperator = 35, 
		RULE_comparisonOperator = 36, RULE_constant = 37, RULE_decimal_literal = 38, 
		RULE_null_literal = 39, RULE_dateLiteral = 40, RULE_format = 41, RULE_booleanLiteral = 42, 
		RULE_fullColumnName = 43, RULE_tableName = 44, RULE_dottedId = 45, RULE_uid = 46, 
		RULE_stringLiteral = 47, RULE_simpleId = 48, RULE_keyword = 49;
	private static String[] makeRuleNames() {
		return new String[] {
			"query", "selectStatement", "selectExpression", "selectClause", "whereClause", 
			"groupByClause", "havingClause", "selectSpec", "selectElements", "selectElement", 
			"fromClause", "joinClause", "tableNameItem", "tableNameSpec", "joinType", 
			"groupByItem", "orderByClause", "orderByExpression", "limitClause", "limitOnly", 
			"limitWithOffset", "commonTableExpression", "columnNames", "initialQuery", 
			"recursivePart", "functionCall", "functionArgs", "functionArg", "filterCondition", 
			"predicate", "expressionAtom", "expressions", "expression", "mathOperator", 
			"unaryOperator", "logicalOperator", "comparisonOperator", "constant", 
			"decimal_literal", "null_literal", "dateLiteral", "format", "booleanLiteral", 
			"fullColumnName", "tableName", "dottedId", "uid", "stringLiteral", "simpleId", 
			"keyword"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'*'", "'='", "'('", "')'", "'!'", "'/'", "'%'", 
			"'+'", "'-'", "'--'", "'~'", "'>'", "'<'", "'<='", "'>='", "'<>'", "'!='", 
			"'::'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "ON", "REGEXP", 
			"LIKE", "IS", "NULL", "EXISTS", "DESC", "ASC", "SELECT", "FROM", "WHERE", 
			"GROUP", "BY", "HAVING", "ORDER", "LIMIT", "OFFSET", "AND", "OR", "XOR", 
			"NOT", "IN", "BETWEEN", "AS", "WITH", "RECURSIVE", "ROLLUP", "DRILLDOWN", 
			"SLICE", "DICE", "PIVOT", "JOIN", "INNER", "OUTER", "LEFT", "RIGHT", 
			"FULL", "CROSS", "NATURAL", "USE", "KEY", "FOR", "UNION", "ALL", "DISTINCT", 
			"TRUE", "FALSE", "IDENTIFIER", "STRING_LITERAL", "DECIMAL_LITERAL", "HEXADECIMAL_LITERAL", 
			"BIT_STRING", "WS", "COMMENT", "LINE_COMMENT"
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
			setState(100);
			selectStatement();
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(101);
				match(T__0);
				}
			}

			setState(104);
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
			setState(122);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				_localctx = new CteQueryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(106);
				match(WITH);
				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==RECURSIVE) {
					{
					setState(107);
					match(RECURSIVE);
					}
				}

				setState(110);
				uid();
				setState(111);
				((CteQueryContext)_localctx).cte = commonTableExpression();
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(112);
					match(T__1);
					setState(113);
					commonTableExpression();
					}
					}
					setState(118);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(119);
				selectExpression();
				}
				break;
			case SELECT:
				_localctx = new SingleQueryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(121);
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
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			selectClause();
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
		public FromClauseContext fromClause() {
			return getRuleContext(FromClauseContext.class,0);
		}
		public SelectSpecContext selectSpec() {
			return getRuleContext(SelectSpecContext.class,0);
		}
		public List<JoinClauseContext> joinClause() {
			return getRuleContexts(JoinClauseContext.class);
		}
		public JoinClauseContext joinClause(int i) {
			return getRuleContext(JoinClauseContext.class,i);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public GroupByClauseContext groupByClause() {
			return getRuleContext(GroupByClauseContext.class,0);
		}
		public HavingClauseContext havingClause() {
			return getRuleContext(HavingClauseContext.class,0);
		}
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
		enterRule(_localctx, 6, RULE_selectClause);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(SELECT);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(127);
				selectSpec();
				}
			}

			setState(130);
			selectElements();
			setState(131);
			fromClause();
			setState(135);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(132);
					joinClause();
					}
					} 
				}
				setState(137);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(138);
				whereClause();
				}
				break;
			}
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(141);
				groupByClause();
				}
				break;
			}
			setState(145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(144);
				havingClause();
				}
				break;
			}
			setState(148);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(147);
				orderByClause();
				}
				break;
			}
			setState(151);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(150);
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
	public static class WhereClauseContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(JQuickSQLParser.WHERE, 0); }
		public FilterConditionContext filterCondition() {
			return getRuleContext(FilterConditionContext.class,0);
		}
		public WhereClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterWhereClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitWhereClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitWhereClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseContext whereClause() throws RecognitionException {
		WhereClauseContext _localctx = new WhereClauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_whereClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			match(WHERE);
			setState(154);
			filterCondition(0);
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
	public static class GroupByClauseContext extends ParserRuleContext {
		public TerminalNode GROUP() { return getToken(JQuickSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(JQuickSQLParser.BY, 0); }
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
		}
		public GroupByClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterGroupByClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitGroupByClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitGroupByClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupByClauseContext groupByClause() throws RecognitionException {
		GroupByClauseContext _localctx = new GroupByClauseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_groupByClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(GROUP);
			setState(157);
			match(BY);
			setState(158);
			expressions();
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
	public static class HavingClauseContext extends ParserRuleContext {
		public TerminalNode HAVING() { return getToken(JQuickSQLParser.HAVING, 0); }
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public HavingClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_havingClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterHavingClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitHavingClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitHavingClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HavingClauseContext havingClause() throws RecognitionException {
		HavingClauseContext _localctx = new HavingClauseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_havingClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(HAVING);
			setState(164);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(161);
					predicate(0);
					}
					} 
				}
				setState(166);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
		enterRule(_localctx, 14, RULE_selectSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			_la = _input.LA(1);
			if ( !(_la==ALL || _la==DISTINCT) ) {
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
		enterRule(_localctx, 16, RULE_selectElements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				{
				setState(169);
				((SelectElementsContext)_localctx).star = match(T__2);
				}
				break;
			case T__4:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case NULL:
			case SELECT:
			case NOT:
			case TRUE:
			case FALSE:
			case IDENTIFIER:
			case STRING_LITERAL:
			case DECIMAL_LITERAL:
				{
				setState(170);
				selectElement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(173);
				match(T__1);
				setState(174);
				selectElement();
				}
				}
				setState(179);
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
		enterRule(_localctx, 18, RULE_selectElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			expression();
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS || _la==IDENTIFIER) {
				{
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(181);
					match(AS);
					}
				}

				setState(184);
				uid();
				}
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
		public TableNameItemContext tableNameItem() {
			return getRuleContext(TableNameItemContext.class,0);
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
		enterRule(_localctx, 20, RULE_fromClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(FROM);
			setState(188);
			tableNameItem();
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
	public static class JoinClauseContext extends ParserRuleContext {
		public JoinTypeContext joinType() {
			return getRuleContext(JoinTypeContext.class,0);
		}
		public TerminalNode JOIN() { return getToken(JQuickSQLParser.JOIN, 0); }
		public TableNameItemContext tableNameItem() {
			return getRuleContext(TableNameItemContext.class,0);
		}
		public TerminalNode ON() { return getToken(JQuickSQLParser.ON, 0); }
		public List<FullColumnNameContext> fullColumnName() {
			return getRuleContexts(FullColumnNameContext.class);
		}
		public FullColumnNameContext fullColumnName(int i) {
			return getRuleContext(FullColumnNameContext.class,i);
		}
		public JoinClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterJoinClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitJoinClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitJoinClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JoinClauseContext joinClause() throws RecognitionException {
		JoinClauseContext _localctx = new JoinClauseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_joinClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			joinType();
			setState(191);
			match(JOIN);
			setState(192);
			tableNameItem();
			setState(193);
			match(ON);
			setState(194);
			fullColumnName();
			setState(195);
			match(T__3);
			setState(196);
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
	public static class TableNameItemContext extends ParserRuleContext {
		public TableNameSpecContext tableNameSpec() {
			return getRuleContext(TableNameSpecContext.class,0);
		}
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public TerminalNode AS() { return getToken(JQuickSQLParser.AS, 0); }
		public TableNameItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableNameItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterTableNameItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitTableNameItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitTableNameItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableNameItemContext tableNameItem() throws RecognitionException {
		TableNameItemContext _localctx = new TableNameItemContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_tableNameItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			tableNameSpec();
			setState(203);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(200);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(199);
					match(AS);
					}
				}

				setState(202);
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
	public static class TableNameSpecContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(JQuickSQLParser.IDENTIFIER, 0); }
		public TableNameSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableNameSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterTableNameSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitTableNameSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitTableNameSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TableNameSpecContext tableNameSpec() throws RecognitionException {
		TableNameSpecContext _localctx = new TableNameSpecContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_tableNameSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			match(IDENTIFIER);
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
		enterRule(_localctx, 28, RULE_joinType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2251799813685248000L) != 0)) ) {
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
	public static class GroupByItemContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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
		enterRule(_localctx, 30, RULE_groupByItem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			expression();
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
		enterRule(_localctx, 32, RULE_orderByClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(ORDER);
			setState(212);
			match(BY);
			setState(213);
			orderByExpression();
			setState(218);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(214);
					match(T__1);
					setState(215);
					orderByExpression();
					}
					} 
				}
				setState(220);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
		enterRule(_localctx, 34, RULE_orderByExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			expression();
			setState(223);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(222);
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
		enterRule(_localctx, 36, RULE_limitClause);
		try {
			setState(229);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				match(LIMIT);
				setState(226);
				limitOnly();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(227);
				match(LIMIT);
				setState(228);
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
		enterRule(_localctx, 38, RULE_limitOnly);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			((LimitOnlyContext)_localctx).limit = expression();
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
		enterRule(_localctx, 40, RULE_limitWithOffset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			((LimitWithOffsetContext)_localctx).offset = expression();
			setState(234);
			match(T__1);
			setState(235);
			((LimitWithOffsetContext)_localctx).limit = expression();
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
		enterRule(_localctx, 42, RULE_commonTableExpression);
		int _la;
		try {
			setState(260);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(237);
				uid();
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(238);
					columnNames();
					}
				}

				setState(241);
				match(AS);
				setState(242);
				match(T__4);
				setState(243);
				selectStatement();
				setState(244);
				match(T__5);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(246);
				uid();
				setState(248);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(247);
					columnNames();
					}
				}

				setState(250);
				match(AS);
				setState(251);
				match(T__4);
				setState(252);
				initialQuery();
				setState(253);
				match(UNION);
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL) {
					{
					setState(254);
					match(ALL);
					}
				}

				setState(257);
				recursivePart();
				setState(258);
				match(T__5);
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
		enterRule(_localctx, 44, RULE_columnNames);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			match(T__4);
			setState(263);
			uid();
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(264);
				match(T__1);
				setState(265);
				uid();
				}
				}
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(271);
			match(T__5);
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
		enterRule(_localctx, 46, RULE_initialQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
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
		enterRule(_localctx, 48, RULE_recursivePart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
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
		enterRule(_localctx, 50, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			uid();
			setState(278);
			match(T__4);
			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4399187373216L) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & 31L) != 0)) {
				{
				setState(279);
				functionArgs();
				}
			}

			setState(282);
			match(T__5);
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
		public List<FunctionArgContext> functionArg() {
			return getRuleContexts(FunctionArgContext.class);
		}
		public FunctionArgContext functionArg(int i) {
			return getRuleContext(FunctionArgContext.class,i);
		}
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
		enterRule(_localctx, 52, RULE_functionArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			functionArg();
			setState(289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(285);
				match(T__1);
				setState(286);
				functionArg();
				}
				}
				setState(291);
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
		enterRule(_localctx, 54, RULE_functionArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			expression();
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
	public static class FilterConditionContext extends ParserRuleContext {
		public List<FilterConditionContext> filterCondition() {
			return getRuleContexts(FilterConditionContext.class);
		}
		public FilterConditionContext filterCondition(int i) {
			return getRuleContext(FilterConditionContext.class,i);
		}
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode AND() { return getToken(JQuickSQLParser.AND, 0); }
		public TerminalNode OR() { return getToken(JQuickSQLParser.OR, 0); }
		public FilterConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filterCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterFilterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitFilterCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitFilterCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FilterConditionContext filterCondition() throws RecognitionException {
		return filterCondition(0);
	}

	private FilterConditionContext filterCondition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FilterConditionContext _localctx = new FilterConditionContext(_ctx, _parentState);
		FilterConditionContext _prevctx = _localctx;
		int _startState = 56;
		enterRecursionRule(_localctx, 56, RULE_filterCondition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(295);
				match(T__4);
				setState(296);
				filterCondition(0);
				setState(297);
				match(T__5);
				}
				break;
			case 2:
				{
				setState(299);
				predicate(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(310);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(308);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
					case 1:
						{
						_localctx = new FilterConditionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_filterCondition);
						setState(302);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						{
						setState(303);
						match(AND);
						setState(304);
						filterCondition(0);
						}
						}
						break;
					case 2:
						{
						_localctx = new FilterConditionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_filterCondition);
						setState(305);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						{
						setState(306);
						match(OR);
						setState(307);
						filterCondition(0);
						}
						}
						break;
					}
					} 
				}
				setState(312);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
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
	public static class ExisitsPredicateContext extends PredicateContext {
		public TerminalNode EXISTS() { return getToken(JQuickSQLParser.EXISTS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExisitsPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterExisitsPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitExisitsPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitExisitsPredicate(this);
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
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_predicate, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(317);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__4:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case NULL:
			case NOT:
			case TRUE:
			case FALSE:
			case IDENTIFIER:
			case STRING_LITERAL:
			case DECIMAL_LITERAL:
				{
				_localctx = new ExpressionAtomPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(314);
				expressionAtom(0);
				}
				break;
			case EXISTS:
				{
				_localctx = new ExisitsPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(315);
				match(EXISTS);
				setState(316);
				expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(364);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(362);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryComparisonPredicateContext(new PredicateContext(_parentctx, _parentState));
						((BinaryComparisonPredicateContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(319);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(320);
						comparisonOperator();
						setState(321);
						((BinaryComparisonPredicateContext)_localctx).right = predicate(7);
						}
						break;
					case 2:
						{
						_localctx = new BetweenPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(323);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(325);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(324);
							match(NOT);
							}
						}

						setState(327);
						match(BETWEEN);
						setState(328);
						predicate(0);
						setState(329);
						match(AND);
						setState(330);
						predicate(6);
						}
						break;
					case 3:
						{
						_localctx = new LikePredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(332);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(334);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(333);
							match(NOT);
							}
						}

						setState(336);
						match(LIKE);
						setState(337);
						predicate(4);
						}
						break;
					case 4:
						{
						_localctx = new RegexpPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(338);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(340);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(339);
							match(NOT);
							}
						}

						setState(342);
						((RegexpPredicateContext)_localctx).regex = match(REGEXP);
						setState(343);
						predicate(3);
						}
						break;
					case 5:
						{
						_localctx = new IsNullPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(344);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(345);
						match(IS);
						setState(347);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(346);
							match(NOT);
							}
						}

						setState(349);
						match(NULL);
						}
						break;
					case 6:
						{
						_localctx = new InPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(350);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(352);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(351);
							match(NOT);
							}
						}

						setState(354);
						match(IN);
						setState(355);
						match(T__4);
						setState(358);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
						case 1:
							{
							setState(356);
							selectStatement();
							}
							break;
						case 2:
							{
							setState(357);
							expressions();
							}
							break;
						}
						setState(360);
						match(T__5);
						}
						break;
					}
					} 
				}
				setState(366);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
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
		int _startState = 60;
		enterRecursionRule(_localctx, 60, RULE_expressionAtom, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				_localctx = new ConstantExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(368);
				constant();
				}
				break;
			case 2:
				{
				_localctx = new FullColumnNameExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(369);
				fullColumnName();
				}
				break;
			case 3:
				{
				_localctx = new FunctionCallExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(370);
				functionCall();
				}
				break;
			case 4:
				{
				_localctx = new NestedExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(371);
				match(T__4);
				setState(372);
				expression();
				setState(377);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(373);
					match(T__1);
					setState(374);
					expression();
					}
					}
					setState(379);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(380);
				match(T__5);
				}
				break;
			case 5:
				{
				_localctx = new SubqueryExperssionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(382);
				match(T__4);
				setState(383);
				selectStatement();
				setState(384);
				match(T__5);
				}
				break;
			case 6:
				{
				_localctx = new UnaryExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(386);
				unaryOperator();
				setState(387);
				expressionAtom(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(397);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MathExpressionAtomContext(new ExpressionAtomContext(_parentctx, _parentState));
					((MathExpressionAtomContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expressionAtom);
					setState(391);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(392);
					mathOperator();
					setState(393);
					((MathExpressionAtomContext)_localctx).right = expressionAtom(3);
					}
					} 
				}
				setState(399);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
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
		enterRule(_localctx, 62, RULE_expressions);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
			expression();
			setState(405);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(401);
					match(T__1);
					setState(402);
					expression();
					}
					} 
				}
				setState(407);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
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
	public static class PredicateExpressionContext extends ExpressionContext {
		public ExpressionAtomContext expressionAtom() {
			return getRuleContext(ExpressionAtomContext.class,0);
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
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_expression);
		int _la;
		try {
			setState(416);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				_localctx = new ParenExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(408);
				match(T__4);
				setState(409);
				expression();
				setState(410);
				match(T__5);
				}
				break;
			case 2:
				_localctx = new NotExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(412);
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
				setState(413);
				expression();
				}
				break;
			case 3:
				_localctx = new PredicateExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(414);
				expressionAtom(0);
				}
				break;
			case 4:
				_localctx = new SelectResultContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(415);
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
		enterRule(_localctx, 66, RULE_mathOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 7944L) != 0)) ) {
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
		enterRule(_localctx, 68, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 4398046522496L) != 0)) ) {
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
		enterRule(_localctx, 70, RULE_logicalOperator);
		try {
			setState(426);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(422);
				match(AND);
				}
				break;
			case XOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(423);
				match(XOR);
				}
				break;
			case OR:
				enterOuterAlt(_localctx, 3);
				{
				setState(424);
				match(OR);
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 4);
				{
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
		enterRule(_localctx, 72, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1032208L) != 0)) ) {
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
		enterRule(_localctx, 74, RULE_constant);
		try {
			setState(437);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(430);
				stringLiteral();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(431);
				decimal_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(432);
				match(T__10);
				setState(433);
				decimal_literal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(434);
				booleanLiteral();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(435);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(436);
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
		enterRule(_localctx, 76, RULE_decimal_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(439);
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
		enterRule(_localctx, 78, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(441);
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
		enterRule(_localctx, 80, RULE_dateLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			stringLiteral();
			setState(444);
			match(T__19);
			setState(445);
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
		enterRule(_localctx, 82, RULE_format);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
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
		enterRule(_localctx, 84, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(449);
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
		enterRule(_localctx, 86, RULE_fullColumnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			uid();
			setState(456);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				{
				setState(452);
				dottedId();
				setState(454);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
				case 1:
					{
					setState(453);
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
		enterRule(_localctx, 88, RULE_tableName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(461);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(458);
				((TableNameContext)_localctx).schema = uid();
				setState(459);
				match(T__20);
				}
				break;
			}
			setState(463);
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
		enterRule(_localctx, 90, RULE_dottedId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(465);
			match(T__20);
			setState(466);
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
		enterRule(_localctx, 92, RULE_uid);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
			simpleId();
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
		enterRule(_localctx, 94, RULE_stringLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
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
		public TerminalNode IDENTIFIER() { return getToken(JQuickSQLParser.IDENTIFIER, 0); }
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
		enterRule(_localctx, 96, RULE_simpleId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			match(IDENTIFIER);
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
		public TerminalNode KEY() { return getToken(JQuickSQLParser.KEY, 0); }
		public TerminalNode FOR() { return getToken(JQuickSQLParser.FOR, 0); }
		public TerminalNode ALL() { return getToken(JQuickSQLParser.ALL, 0); }
		public TerminalNode DISTINCT() { return getToken(JQuickSQLParser.DISTINCT, 0); }
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
		enterRule(_localctx, 98, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(474);
			_la = _input.LA(1);
			if ( !(((((_la - 30)) & ~0x3f) == 0 && ((1L << (_la - 30)) & 120259059711L) != 0)) ) {
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
		case 28:
			return filterCondition_sempred((FilterConditionContext)_localctx, predIndex);
		case 29:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 30:
			return expressionAtom_sempred((ExpressionAtomContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean filterCondition_sempred(FilterConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 3);
		case 5:
			return precpred(_ctx, 2);
		case 6:
			return precpred(_ctx, 7);
		case 7:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean expressionAtom_sempred(ExpressionAtomContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001L\u01dd\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0001"+
		"\u0000\u0001\u0000\u0003\u0000g\b\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0003\u0001m\b\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0005\u0001s\b\u0001\n\u0001\f\u0001v\t\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001{\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0003\u0003\u0081\b\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0005\u0003\u0086\b\u0003\n\u0003\f\u0003\u0089\t\u0003"+
		"\u0001\u0003\u0003\u0003\u008c\b\u0003\u0001\u0003\u0003\u0003\u008f\b"+
		"\u0003\u0001\u0003\u0003\u0003\u0092\b\u0003\u0001\u0003\u0003\u0003\u0095"+
		"\b\u0003\u0001\u0003\u0003\u0003\u0098\b\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0005\u0006\u00a3\b\u0006\n\u0006\f\u0006\u00a6\t\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0003\b\u00ac\b\b\u0001\b\u0001\b\u0005"+
		"\b\u00b0\b\b\n\b\f\b\u00b3\t\b\u0001\t\u0001\t\u0003\t\u00b7\b\t\u0001"+
		"\t\u0003\t\u00ba\b\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0003\f\u00c9\b\f\u0001\f\u0003\f\u00cc\b\f\u0001\r\u0001\r"+
		"\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u00d9\b\u0010\n\u0010"+
		"\f\u0010\u00dc\t\u0010\u0001\u0011\u0001\u0011\u0003\u0011\u00e0\b\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00e6\b\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0015\u0001\u0015\u0003\u0015\u00f0\b\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015"+
		"\u00f9\b\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0003\u0015\u0100\b\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015"+
		"\u0105\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016"+
		"\u010b\b\u0016\n\u0016\f\u0016\u010e\t\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0003\u0019\u0119\b\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0005\u001a\u0120\b\u001a\n\u001a\f\u001a\u0123\t\u001a"+
		"\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0003\u001c\u012d\b\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u0135\b\u001c"+
		"\n\u001c\f\u001c\u0138\t\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0003\u001d\u013e\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0146\b\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u014f\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u0155\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0003\u001d\u015c\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u0161\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u0167\b\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u016b\b\u001d"+
		"\n\u001d\f\u001d\u016e\t\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u0178"+
		"\b\u001e\n\u001e\f\u001e\u017b\t\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0003\u001e\u0186\b\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0005\u001e\u018c\b\u001e\n\u001e\f\u001e\u018f\t\u001e\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0005\u001f\u0194\b\u001f\n\u001f\f\u001f\u0197\t\u001f"+
		"\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0003 \u01a1"+
		"\b \u0001!\u0001!\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0003#\u01ab"+
		"\b#\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003"+
		"%\u01b6\b%\u0001&\u0001&\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001"+
		")\u0001)\u0001*\u0001*\u0001+\u0001+\u0001+\u0003+\u01c7\b+\u0003+\u01c9"+
		"\b+\u0001,\u0001,\u0001,\u0003,\u01ce\b,\u0001,\u0001,\u0001-\u0001-\u0001"+
		"-\u0001.\u0001.\u0001/\u0001/\u00010\u00010\u00011\u00011\u00011\u0000"+
		"\u00038:<2\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`b\u0000\t\u0001"+
		"\u0000AB\u0002\u0000668<\u0001\u0000\u001c\u001d\u0002\u0000\u0007\u0007"+
		"**\u0002\u0000\u0003\u0003\b\f\u0004\u0000\u0007\u0007\n\u000b\r\r**\u0002"+
		"\u0000\u0004\u0004\u000e\u0013\u0001\u0000CD\u0003\u0000\u001e*-?AB\u01ec"+
		"\u0000d\u0001\u0000\u0000\u0000\u0002z\u0001\u0000\u0000\u0000\u0004|"+
		"\u0001\u0000\u0000\u0000\u0006~\u0001\u0000\u0000\u0000\b\u0099\u0001"+
		"\u0000\u0000\u0000\n\u009c\u0001\u0000\u0000\u0000\f\u00a0\u0001\u0000"+
		"\u0000\u0000\u000e\u00a7\u0001\u0000\u0000\u0000\u0010\u00ab\u0001\u0000"+
		"\u0000\u0000\u0012\u00b4\u0001\u0000\u0000\u0000\u0014\u00bb\u0001\u0000"+
		"\u0000\u0000\u0016\u00be\u0001\u0000\u0000\u0000\u0018\u00c6\u0001\u0000"+
		"\u0000\u0000\u001a\u00cd\u0001\u0000\u0000\u0000\u001c\u00cf\u0001\u0000"+
		"\u0000\u0000\u001e\u00d1\u0001\u0000\u0000\u0000 \u00d3\u0001\u0000\u0000"+
		"\u0000\"\u00dd\u0001\u0000\u0000\u0000$\u00e5\u0001\u0000\u0000\u0000"+
		"&\u00e7\u0001\u0000\u0000\u0000(\u00e9\u0001\u0000\u0000\u0000*\u0104"+
		"\u0001\u0000\u0000\u0000,\u0106\u0001\u0000\u0000\u0000.\u0111\u0001\u0000"+
		"\u0000\u00000\u0113\u0001\u0000\u0000\u00002\u0115\u0001\u0000\u0000\u0000"+
		"4\u011c\u0001\u0000\u0000\u00006\u0124\u0001\u0000\u0000\u00008\u012c"+
		"\u0001\u0000\u0000\u0000:\u013d\u0001\u0000\u0000\u0000<\u0185\u0001\u0000"+
		"\u0000\u0000>\u0190\u0001\u0000\u0000\u0000@\u01a0\u0001\u0000\u0000\u0000"+
		"B\u01a2\u0001\u0000\u0000\u0000D\u01a4\u0001\u0000\u0000\u0000F\u01aa"+
		"\u0001\u0000\u0000\u0000H\u01ac\u0001\u0000\u0000\u0000J\u01b5\u0001\u0000"+
		"\u0000\u0000L\u01b7\u0001\u0000\u0000\u0000N\u01b9\u0001\u0000\u0000\u0000"+
		"P\u01bb\u0001\u0000\u0000\u0000R\u01bf\u0001\u0000\u0000\u0000T\u01c1"+
		"\u0001\u0000\u0000\u0000V\u01c3\u0001\u0000\u0000\u0000X\u01cd\u0001\u0000"+
		"\u0000\u0000Z\u01d1\u0001\u0000\u0000\u0000\\\u01d4\u0001\u0000\u0000"+
		"\u0000^\u01d6\u0001\u0000\u0000\u0000`\u01d8\u0001\u0000\u0000\u0000b"+
		"\u01da\u0001\u0000\u0000\u0000df\u0003\u0002\u0001\u0000eg\u0005\u0001"+
		"\u0000\u0000fe\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gh\u0001"+
		"\u0000\u0000\u0000hi\u0005\u0000\u0000\u0001i\u0001\u0001\u0000\u0000"+
		"\u0000jl\u0005.\u0000\u0000km\u0005/\u0000\u0000lk\u0001\u0000\u0000\u0000"+
		"lm\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0003\\.\u0000"+
		"ot\u0003*\u0015\u0000pq\u0005\u0002\u0000\u0000qs\u0003*\u0015\u0000r"+
		"p\u0001\u0000\u0000\u0000sv\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000"+
		"\u0000tu\u0001\u0000\u0000\u0000uw\u0001\u0000\u0000\u0000vt\u0001\u0000"+
		"\u0000\u0000wx\u0003\u0004\u0002\u0000x{\u0001\u0000\u0000\u0000y{\u0003"+
		"\u0004\u0002\u0000zj\u0001\u0000\u0000\u0000zy\u0001\u0000\u0000\u0000"+
		"{\u0003\u0001\u0000\u0000\u0000|}\u0003\u0006\u0003\u0000}\u0005\u0001"+
		"\u0000\u0000\u0000~\u0080\u0005\u001e\u0000\u0000\u007f\u0081\u0003\u000e"+
		"\u0007\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000"+
		"\u0000\u0000\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u0083\u0003\u0010"+
		"\b\u0000\u0083\u0087\u0003\u0014\n\u0000\u0084\u0086\u0003\u0016\u000b"+
		"\u0000\u0085\u0084\u0001\u0000\u0000\u0000\u0086\u0089\u0001\u0000\u0000"+
		"\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000"+
		"\u0000\u0088\u008b\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000"+
		"\u0000\u008a\u008c\u0003\b\u0004\u0000\u008b\u008a\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008e\u0001\u0000\u0000\u0000"+
		"\u008d\u008f\u0003\n\u0005\u0000\u008e\u008d\u0001\u0000\u0000\u0000\u008e"+
		"\u008f\u0001\u0000\u0000\u0000\u008f\u0091\u0001\u0000\u0000\u0000\u0090"+
		"\u0092\u0003\f\u0006\u0000\u0091\u0090\u0001\u0000\u0000\u0000\u0091\u0092"+
		"\u0001\u0000\u0000\u0000\u0092\u0094\u0001\u0000\u0000\u0000\u0093\u0095"+
		"\u0003 \u0010\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0094\u0095\u0001"+
		"\u0000\u0000\u0000\u0095\u0097\u0001\u0000\u0000\u0000\u0096\u0098\u0003"+
		"$\u0012\u0000\u0097\u0096\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000"+
		"\u0000\u0000\u0098\u0007\u0001\u0000\u0000\u0000\u0099\u009a\u0005 \u0000"+
		"\u0000\u009a\u009b\u00038\u001c\u0000\u009b\t\u0001\u0000\u0000\u0000"+
		"\u009c\u009d\u0005!\u0000\u0000\u009d\u009e\u0005\"\u0000\u0000\u009e"+
		"\u009f\u0003>\u001f\u0000\u009f\u000b\u0001\u0000\u0000\u0000\u00a0\u00a4"+
		"\u0005#\u0000\u0000\u00a1\u00a3\u0003:\u001d\u0000\u00a2\u00a1\u0001\u0000"+
		"\u0000\u0000\u00a3\u00a6\u0001\u0000\u0000\u0000\u00a4\u00a2\u0001\u0000"+
		"\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\r\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a7\u00a8\u0007\u0000\u0000"+
		"\u0000\u00a8\u000f\u0001\u0000\u0000\u0000\u00a9\u00ac\u0005\u0003\u0000"+
		"\u0000\u00aa\u00ac\u0003\u0012\t\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000"+
		"\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ac\u00b1\u0001\u0000\u0000\u0000"+
		"\u00ad\u00ae\u0005\u0002\u0000\u0000\u00ae\u00b0\u0003\u0012\t\u0000\u00af"+
		"\u00ad\u0001\u0000\u0000\u0000\u00b0\u00b3\u0001\u0000\u0000\u0000\u00b1"+
		"\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2"+
		"\u0011\u0001\u0000\u0000\u0000\u00b3\u00b1\u0001\u0000\u0000\u0000\u00b4"+
		"\u00b9\u0003@ \u0000\u00b5\u00b7\u0005-\u0000\u0000\u00b6\u00b5\u0001"+
		"\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001"+
		"\u0000\u0000\u0000\u00b8\u00ba\u0003\\.\u0000\u00b9\u00b6\u0001\u0000"+
		"\u0000\u0000\u00b9\u00ba\u0001\u0000\u0000\u0000\u00ba\u0013\u0001\u0000"+
		"\u0000\u0000\u00bb\u00bc\u0005\u001f\u0000\u0000\u00bc\u00bd\u0003\u0018"+
		"\f\u0000\u00bd\u0015\u0001\u0000\u0000\u0000\u00be\u00bf\u0003\u001c\u000e"+
		"\u0000\u00bf\u00c0\u00055\u0000\u0000\u00c0\u00c1\u0003\u0018\f\u0000"+
		"\u00c1\u00c2\u0005\u0016\u0000\u0000\u00c2\u00c3\u0003V+\u0000\u00c3\u00c4"+
		"\u0005\u0004\u0000\u0000\u00c4\u00c5\u0003V+\u0000\u00c5\u0017\u0001\u0000"+
		"\u0000\u0000\u00c6\u00cb\u0003\u001a\r\u0000\u00c7\u00c9\u0005-\u0000"+
		"\u0000\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000"+
		"\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00cc\u0003\\.\u0000"+
		"\u00cb\u00c8\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000"+
		"\u00cc\u0019\u0001\u0000\u0000\u0000\u00cd\u00ce\u0005E\u0000\u0000\u00ce"+
		"\u001b\u0001\u0000\u0000\u0000\u00cf\u00d0\u0007\u0001\u0000\u0000\u00d0"+
		"\u001d\u0001\u0000\u0000\u0000\u00d1\u00d2\u0003@ \u0000\u00d2\u001f\u0001"+
		"\u0000\u0000\u0000\u00d3\u00d4\u0005$\u0000\u0000\u00d4\u00d5\u0005\""+
		"\u0000\u0000\u00d5\u00da\u0003\"\u0011\u0000\u00d6\u00d7\u0005\u0002\u0000"+
		"\u0000\u00d7\u00d9\u0003\"\u0011\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000"+
		"\u00d9\u00dc\u0001\u0000\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000"+
		"\u00da\u00db\u0001\u0000\u0000\u0000\u00db!\u0001\u0000\u0000\u0000\u00dc"+
		"\u00da\u0001\u0000\u0000\u0000\u00dd\u00df\u0003@ \u0000\u00de\u00e0\u0007"+
		"\u0002\u0000\u0000\u00df\u00de\u0001\u0000\u0000\u0000\u00df\u00e0\u0001"+
		"\u0000\u0000\u0000\u00e0#\u0001\u0000\u0000\u0000\u00e1\u00e2\u0005%\u0000"+
		"\u0000\u00e2\u00e6\u0003&\u0013\u0000\u00e3\u00e4\u0005%\u0000\u0000\u00e4"+
		"\u00e6\u0003(\u0014\u0000\u00e5\u00e1\u0001\u0000\u0000\u0000\u00e5\u00e3"+
		"\u0001\u0000\u0000\u0000\u00e6%\u0001\u0000\u0000\u0000\u00e7\u00e8\u0003"+
		"@ \u0000\u00e8\'\u0001\u0000\u0000\u0000\u00e9\u00ea\u0003@ \u0000\u00ea"+
		"\u00eb\u0005\u0002\u0000\u0000\u00eb\u00ec\u0003@ \u0000\u00ec)\u0001"+
		"\u0000\u0000\u0000\u00ed\u00ef\u0003\\.\u0000\u00ee\u00f0\u0003,\u0016"+
		"\u0000\u00ef\u00ee\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000"+
		"\u0000\u00f0\u00f1\u0001\u0000\u0000\u0000\u00f1\u00f2\u0005-\u0000\u0000"+
		"\u00f2\u00f3\u0005\u0005\u0000\u0000\u00f3\u00f4\u0003\u0002\u0001\u0000"+
		"\u00f4\u00f5\u0005\u0006\u0000\u0000\u00f5\u0105\u0001\u0000\u0000\u0000"+
		"\u00f6\u00f8\u0003\\.\u0000\u00f7\u00f9\u0003,\u0016\u0000\u00f8\u00f7"+
		"\u0001\u0000\u0000\u0000\u00f8\u00f9\u0001\u0000\u0000\u0000\u00f9\u00fa"+
		"\u0001\u0000\u0000\u0000\u00fa\u00fb\u0005-\u0000\u0000\u00fb\u00fc\u0005"+
		"\u0005\u0000\u0000\u00fc\u00fd\u0003.\u0017\u0000\u00fd\u00ff\u0005@\u0000"+
		"\u0000\u00fe\u0100\u0005A\u0000\u0000\u00ff\u00fe\u0001\u0000\u0000\u0000"+
		"\u00ff\u0100\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000\u0000\u0000"+
		"\u0101\u0102\u00030\u0018\u0000\u0102\u0103\u0005\u0006\u0000\u0000\u0103"+
		"\u0105\u0001\u0000\u0000\u0000\u0104\u00ed\u0001\u0000\u0000\u0000\u0104"+
		"\u00f6\u0001\u0000\u0000\u0000\u0105+\u0001\u0000\u0000\u0000\u0106\u0107"+
		"\u0005\u0005\u0000\u0000\u0107\u010c\u0003\\.\u0000\u0108\u0109\u0005"+
		"\u0002\u0000\u0000\u0109\u010b\u0003\\.\u0000\u010a\u0108\u0001\u0000"+
		"\u0000\u0000\u010b\u010e\u0001\u0000\u0000\u0000\u010c\u010a\u0001\u0000"+
		"\u0000\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d\u010f\u0001\u0000"+
		"\u0000\u0000\u010e\u010c\u0001\u0000\u0000\u0000\u010f\u0110\u0005\u0006"+
		"\u0000\u0000\u0110-\u0001\u0000\u0000\u0000\u0111\u0112\u0003\u0002\u0001"+
		"\u0000\u0112/\u0001\u0000\u0000\u0000\u0113\u0114\u0003\u0002\u0001\u0000"+
		"\u01141\u0001\u0000\u0000\u0000\u0115\u0116\u0003\\.\u0000\u0116\u0118"+
		"\u0005\u0005\u0000\u0000\u0117\u0119\u00034\u001a\u0000\u0118\u0117\u0001"+
		"\u0000\u0000\u0000\u0118\u0119\u0001\u0000\u0000\u0000\u0119\u011a\u0001"+
		"\u0000\u0000\u0000\u011a\u011b\u0005\u0006\u0000\u0000\u011b3\u0001\u0000"+
		"\u0000\u0000\u011c\u0121\u00036\u001b\u0000\u011d\u011e\u0005\u0002\u0000"+
		"\u0000\u011e\u0120\u00036\u001b\u0000\u011f\u011d\u0001\u0000\u0000\u0000"+
		"\u0120\u0123\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000\u0000\u0000"+
		"\u0121\u0122\u0001\u0000\u0000\u0000\u01225\u0001\u0000\u0000\u0000\u0123"+
		"\u0121\u0001\u0000\u0000\u0000\u0124\u0125\u0003@ \u0000\u01257\u0001"+
		"\u0000\u0000\u0000\u0126\u0127\u0006\u001c\uffff\uffff\u0000\u0127\u0128"+
		"\u0005\u0005\u0000\u0000\u0128\u0129\u00038\u001c\u0000\u0129\u012a\u0005"+
		"\u0006\u0000\u0000\u012a\u012d\u0001\u0000\u0000\u0000\u012b\u012d\u0003"+
		":\u001d\u0000\u012c\u0126\u0001\u0000\u0000\u0000\u012c\u012b\u0001\u0000"+
		"\u0000\u0000\u012d\u0136\u0001\u0000\u0000\u0000\u012e\u012f\n\u0004\u0000"+
		"\u0000\u012f\u0130\u0005\'\u0000\u0000\u0130\u0135\u00038\u001c\u0000"+
		"\u0131\u0132\n\u0003\u0000\u0000\u0132\u0133\u0005(\u0000\u0000\u0133"+
		"\u0135\u00038\u001c\u0000\u0134\u012e\u0001\u0000\u0000\u0000\u0134\u0131"+
		"\u0001\u0000\u0000\u0000\u0135\u0138\u0001\u0000\u0000\u0000\u0136\u0134"+
		"\u0001\u0000\u0000\u0000\u0136\u0137\u0001\u0000\u0000\u0000\u01379\u0001"+
		"\u0000\u0000\u0000\u0138\u0136\u0001\u0000\u0000\u0000\u0139\u013a\u0006"+
		"\u001d\uffff\uffff\u0000\u013a\u013e\u0003<\u001e\u0000\u013b\u013c\u0005"+
		"\u001b\u0000\u0000\u013c\u013e\u0003@ \u0000\u013d\u0139\u0001\u0000\u0000"+
		"\u0000\u013d\u013b\u0001\u0000\u0000\u0000\u013e\u016c\u0001\u0000\u0000"+
		"\u0000\u013f\u0140\n\u0006\u0000\u0000\u0140\u0141\u0003H$\u0000\u0141"+
		"\u0142\u0003:\u001d\u0007\u0142\u016b\u0001\u0000\u0000\u0000\u0143\u0145"+
		"\n\u0005\u0000\u0000\u0144\u0146\u0005*\u0000\u0000\u0145\u0144\u0001"+
		"\u0000\u0000\u0000\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u0147\u0001"+
		"\u0000\u0000\u0000\u0147\u0148\u0005,\u0000\u0000\u0148\u0149\u0003:\u001d"+
		"\u0000\u0149\u014a\u0005\'\u0000\u0000\u014a\u014b\u0003:\u001d\u0006"+
		"\u014b\u016b\u0001\u0000\u0000\u0000\u014c\u014e\n\u0003\u0000\u0000\u014d"+
		"\u014f\u0005*\u0000\u0000\u014e\u014d\u0001\u0000\u0000\u0000\u014e\u014f"+
		"\u0001\u0000\u0000\u0000\u014f\u0150\u0001\u0000\u0000\u0000\u0150\u0151"+
		"\u0005\u0018\u0000\u0000\u0151\u016b\u0003:\u001d\u0004\u0152\u0154\n"+
		"\u0002\u0000\u0000\u0153\u0155\u0005*\u0000\u0000\u0154\u0153\u0001\u0000"+
		"\u0000\u0000\u0154\u0155\u0001\u0000\u0000\u0000\u0155\u0156\u0001\u0000"+
		"\u0000\u0000\u0156\u0157\u0005\u0017\u0000\u0000\u0157\u016b\u0003:\u001d"+
		"\u0003\u0158\u0159\n\u0007\u0000\u0000\u0159\u015b\u0005\u0019\u0000\u0000"+
		"\u015a\u015c\u0005*\u0000\u0000\u015b\u015a\u0001\u0000\u0000\u0000\u015b"+
		"\u015c\u0001\u0000\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000\u015d"+
		"\u016b\u0005\u001a\u0000\u0000\u015e\u0160\n\u0004\u0000\u0000\u015f\u0161"+
		"\u0005*\u0000\u0000\u0160\u015f\u0001\u0000\u0000\u0000\u0160\u0161\u0001"+
		"\u0000\u0000\u0000\u0161\u0162\u0001\u0000\u0000\u0000\u0162\u0163\u0005"+
		"+\u0000\u0000\u0163\u0166\u0005\u0005\u0000\u0000\u0164\u0167\u0003\u0002"+
		"\u0001\u0000\u0165\u0167\u0003>\u001f\u0000\u0166\u0164\u0001\u0000\u0000"+
		"\u0000\u0166\u0165\u0001\u0000\u0000\u0000\u0167\u0168\u0001\u0000\u0000"+
		"\u0000\u0168\u0169\u0005\u0006\u0000\u0000\u0169\u016b\u0001\u0000\u0000"+
		"\u0000\u016a\u013f\u0001\u0000\u0000\u0000\u016a\u0143\u0001\u0000\u0000"+
		"\u0000\u016a\u014c\u0001\u0000\u0000\u0000\u016a\u0152\u0001\u0000\u0000"+
		"\u0000\u016a\u0158\u0001\u0000\u0000\u0000\u016a\u015e\u0001\u0000\u0000"+
		"\u0000\u016b\u016e\u0001\u0000\u0000\u0000\u016c\u016a\u0001\u0000\u0000"+
		"\u0000\u016c\u016d\u0001\u0000\u0000\u0000\u016d;\u0001\u0000\u0000\u0000"+
		"\u016e\u016c\u0001\u0000\u0000\u0000\u016f\u0170\u0006\u001e\uffff\uffff"+
		"\u0000\u0170\u0186\u0003J%\u0000\u0171\u0186\u0003V+\u0000\u0172\u0186"+
		"\u00032\u0019\u0000\u0173\u0174\u0005\u0005\u0000\u0000\u0174\u0179\u0003"+
		"@ \u0000\u0175\u0176\u0005\u0002\u0000\u0000\u0176\u0178\u0003@ \u0000"+
		"\u0177\u0175\u0001\u0000\u0000\u0000\u0178\u017b\u0001\u0000\u0000\u0000"+
		"\u0179\u0177\u0001\u0000\u0000\u0000\u0179\u017a\u0001\u0000\u0000\u0000"+
		"\u017a\u017c\u0001\u0000\u0000\u0000\u017b\u0179\u0001\u0000\u0000\u0000"+
		"\u017c\u017d\u0005\u0006\u0000\u0000\u017d\u0186\u0001\u0000\u0000\u0000"+
		"\u017e\u017f\u0005\u0005\u0000\u0000\u017f\u0180\u0003\u0002\u0001\u0000"+
		"\u0180\u0181\u0005\u0006\u0000\u0000\u0181\u0186\u0001\u0000\u0000\u0000"+
		"\u0182\u0183\u0003D\"\u0000\u0183\u0184\u0003<\u001e\u0001\u0184\u0186"+
		"\u0001\u0000\u0000\u0000\u0185\u016f\u0001\u0000\u0000\u0000\u0185\u0171"+
		"\u0001\u0000\u0000\u0000\u0185\u0172\u0001\u0000\u0000\u0000\u0185\u0173"+
		"\u0001\u0000\u0000\u0000\u0185\u017e\u0001\u0000\u0000\u0000\u0185\u0182"+
		"\u0001\u0000\u0000\u0000\u0186\u018d\u0001\u0000\u0000\u0000\u0187\u0188"+
		"\n\u0002\u0000\u0000\u0188\u0189\u0003B!\u0000\u0189\u018a\u0003<\u001e"+
		"\u0003\u018a\u018c\u0001\u0000\u0000\u0000\u018b\u0187\u0001\u0000\u0000"+
		"\u0000\u018c\u018f\u0001\u0000\u0000\u0000\u018d\u018b\u0001\u0000\u0000"+
		"\u0000\u018d\u018e\u0001\u0000\u0000\u0000\u018e=\u0001\u0000\u0000\u0000"+
		"\u018f\u018d\u0001\u0000\u0000\u0000\u0190\u0195\u0003@ \u0000\u0191\u0192"+
		"\u0005\u0002\u0000\u0000\u0192\u0194\u0003@ \u0000\u0193\u0191\u0001\u0000"+
		"\u0000\u0000\u0194\u0197\u0001\u0000\u0000\u0000\u0195\u0193\u0001\u0000"+
		"\u0000\u0000\u0195\u0196\u0001\u0000\u0000\u0000\u0196?\u0001\u0000\u0000"+
		"\u0000\u0197\u0195\u0001\u0000\u0000\u0000\u0198\u0199\u0005\u0005\u0000"+
		"\u0000\u0199\u019a\u0003@ \u0000\u019a\u019b\u0005\u0006\u0000\u0000\u019b"+
		"\u01a1\u0001\u0000\u0000\u0000\u019c\u019d\u0007\u0003\u0000\u0000\u019d"+
		"\u01a1\u0003@ \u0000\u019e\u01a1\u0003<\u001e\u0000\u019f\u01a1\u0003"+
		"\u0006\u0003\u0000\u01a0\u0198\u0001\u0000\u0000\u0000\u01a0\u019c\u0001"+
		"\u0000\u0000\u0000\u01a0\u019e\u0001\u0000\u0000\u0000\u01a0\u019f\u0001"+
		"\u0000\u0000\u0000\u01a1A\u0001\u0000\u0000\u0000\u01a2\u01a3\u0007\u0004"+
		"\u0000\u0000\u01a3C\u0001\u0000\u0000\u0000\u01a4\u01a5\u0007\u0005\u0000"+
		"\u0000\u01a5E\u0001\u0000\u0000\u0000\u01a6\u01ab\u0005\'\u0000\u0000"+
		"\u01a7\u01ab\u0005)\u0000\u0000\u01a8\u01ab\u0005(\u0000\u0000\u01a9\u01ab"+
		"\u0001\u0000\u0000\u0000\u01aa\u01a6\u0001\u0000\u0000\u0000\u01aa\u01a7"+
		"\u0001\u0000\u0000\u0000\u01aa\u01a8\u0001\u0000\u0000\u0000\u01aa\u01a9"+
		"\u0001\u0000\u0000\u0000\u01abG\u0001\u0000\u0000\u0000\u01ac\u01ad\u0007"+
		"\u0006\u0000\u0000\u01adI\u0001\u0000\u0000\u0000\u01ae\u01b6\u0003^/"+
		"\u0000\u01af\u01b6\u0003L&\u0000\u01b0\u01b1\u0005\u000b\u0000\u0000\u01b1"+
		"\u01b6\u0003L&\u0000\u01b2\u01b6\u0003T*\u0000\u01b3\u01b6\u0003N\'\u0000"+
		"\u01b4\u01b6\u0003P(\u0000\u01b5\u01ae\u0001\u0000\u0000\u0000\u01b5\u01af"+
		"\u0001\u0000\u0000\u0000\u01b5\u01b0\u0001\u0000\u0000\u0000\u01b5\u01b2"+
		"\u0001\u0000\u0000\u0000\u01b5\u01b3\u0001\u0000\u0000\u0000\u01b5\u01b4"+
		"\u0001\u0000\u0000\u0000\u01b6K\u0001\u0000\u0000\u0000\u01b7\u01b8\u0005"+
		"G\u0000\u0000\u01b8M\u0001\u0000\u0000\u0000\u01b9\u01ba\u0005\u001a\u0000"+
		"\u0000\u01baO\u0001\u0000\u0000\u0000\u01bb\u01bc\u0003^/\u0000\u01bc"+
		"\u01bd\u0005\u0014\u0000\u0000\u01bd\u01be\u0003R)\u0000\u01beQ\u0001"+
		"\u0000\u0000\u0000\u01bf\u01c0\u0003^/\u0000\u01c0S\u0001\u0000\u0000"+
		"\u0000\u01c1\u01c2\u0007\u0007\u0000\u0000\u01c2U\u0001\u0000\u0000\u0000"+
		"\u01c3\u01c8\u0003\\.\u0000\u01c4\u01c6\u0003Z-\u0000\u01c5\u01c7\u0003"+
		"Z-\u0000\u01c6\u01c5\u0001\u0000\u0000\u0000\u01c6\u01c7\u0001\u0000\u0000"+
		"\u0000\u01c7\u01c9\u0001\u0000\u0000\u0000\u01c8\u01c4\u0001\u0000\u0000"+
		"\u0000\u01c8\u01c9\u0001\u0000\u0000\u0000\u01c9W\u0001\u0000\u0000\u0000"+
		"\u01ca\u01cb\u0003\\.\u0000\u01cb\u01cc\u0005\u0015\u0000\u0000\u01cc"+
		"\u01ce\u0001\u0000\u0000\u0000\u01cd\u01ca\u0001\u0000\u0000\u0000\u01cd"+
		"\u01ce\u0001\u0000\u0000\u0000\u01ce\u01cf\u0001\u0000\u0000\u0000\u01cf"+
		"\u01d0\u0003\\.\u0000\u01d0Y\u0001\u0000\u0000\u0000\u01d1\u01d2\u0005"+
		"\u0015\u0000\u0000\u01d2\u01d3\u0003\\.\u0000\u01d3[\u0001\u0000\u0000"+
		"\u0000\u01d4\u01d5\u0003`0\u0000\u01d5]\u0001\u0000\u0000\u0000\u01d6"+
		"\u01d7\u0005F\u0000\u0000\u01d7_\u0001\u0000\u0000\u0000\u01d8\u01d9\u0005"+
		"E\u0000\u0000\u01d9a\u0001\u0000\u0000\u0000\u01da\u01db\u0007\b\u0000"+
		"\u0000\u01dbc\u0001\u0000\u0000\u00002fltz\u0080\u0087\u008b\u008e\u0091"+
		"\u0094\u0097\u00a4\u00ab\u00b1\u00b6\u00b9\u00c8\u00cb\u00da\u00df\u00e5"+
		"\u00ef\u00f8\u00ff\u0104\u010c\u0118\u0121\u012c\u0134\u0136\u013d\u0145"+
		"\u014e\u0154\u015b\u0160\u0166\u016a\u016c\u0179\u0185\u018d\u0195\u01a0"+
		"\u01aa\u01b5\u01c6\u01c8\u01cd";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}