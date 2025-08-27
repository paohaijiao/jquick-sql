// Generated from D:/my/jthornruleGrammer/sql/JQuickSQL.g4 by ANTLR 4.13.2
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
		RULE_query = 0, RULE_selectStatement = 1, RULE_selectExpression = 2, RULE_olapOperation = 3, 
		RULE_olapClause = 4, RULE_olapClauseItem = 5, RULE_rollupDimensions = 6, 
		RULE_drilldownDimensions = 7, RULE_sliceCondition = 8, RULE_diceConditions = 9, 
		RULE_diceCondition = 10, RULE_pivotSpec = 11, RULE_selectClause = 12, 
		RULE_whereClause = 13, RULE_groupByClause = 14, RULE_havingClause = 15, 
		RULE_selectSpec = 16, RULE_selectElements = 17, RULE_selectElement = 18, 
		RULE_fromClause = 19, RULE_joinClause = 20, RULE_tableNameItem = 21, RULE_tableNameSpec = 22, 
		RULE_joinType = 23, RULE_orderByClause = 24, RULE_orderByExpression = 25, 
		RULE_limitClause = 26, RULE_limitOnly = 27, RULE_limitWithOffset = 28, 
		RULE_commonTableExpression = 29, RULE_columnNames = 30, RULE_initialQuery = 31, 
		RULE_recursivePart = 32, RULE_functionCall = 33, RULE_arg = 34, RULE_functionArgs = 35, 
		RULE_functionArg = 36, RULE_filterCondition = 37, RULE_predicate = 38, 
		RULE_expressionAtom = 39, RULE_expressions = 40, RULE_expression = 41, 
		RULE_mathOperator = 42, RULE_unaryOperator = 43, RULE_logicalOperator = 44, 
		RULE_comparisonOperator = 45, RULE_constant = 46, RULE_decimal_literal = 47, 
		RULE_null_literal = 48, RULE_dateLiteral = 49, RULE_format = 50, RULE_booleanLiteral = 51, 
		RULE_fullColumnName = 52, RULE_dottedId = 53, RULE_uid = 54, RULE_stringLiteral = 55, 
		RULE_simpleId = 56, RULE_keyword = 57;
	private static String[] makeRuleNames() {
		return new String[] {
			"query", "selectStatement", "selectExpression", "olapOperation", "olapClause", 
			"olapClauseItem", "rollupDimensions", "drilldownDimensions", "sliceCondition", 
			"diceConditions", "diceCondition", "pivotSpec", "selectClause", "whereClause", 
			"groupByClause", "havingClause", "selectSpec", "selectElements", "selectElement", 
			"fromClause", "joinClause", "tableNameItem", "tableNameSpec", "joinType", 
			"orderByClause", "orderByExpression", "limitClause", "limitOnly", "limitWithOffset", 
			"commonTableExpression", "columnNames", "initialQuery", "recursivePart", 
			"functionCall", "arg", "functionArgs", "functionArg", "filterCondition", 
			"predicate", "expressionAtom", "expressions", "expression", "mathOperator", 
			"unaryOperator", "logicalOperator", "comparisonOperator", "constant", 
			"decimal_literal", "null_literal", "dateLiteral", "format", "booleanLiteral", 
			"fullColumnName", "dottedId", "uid", "stringLiteral", "simpleId", "keyword"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'('", "')'", "'='", "'*'", "'!'", "'/'", "'%'", 
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
			setState(116);
			selectStatement();
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(117);
				match(T__0);
				}
			}

			setState(120);
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
			setState(137);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WITH:
				_localctx = new CteQueryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				match(WITH);
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==RECURSIVE) {
					{
					setState(123);
					match(RECURSIVE);
					}
				}

				setState(126);
				((CteQueryContext)_localctx).cte = commonTableExpression();
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(127);
					match(T__1);
					setState(128);
					commonTableExpression();
					}
					}
					setState(133);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(134);
				selectExpression();
				}
				break;
			case SELECT:
				_localctx = new SingleQueryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(136);
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
		public OlapOperationContext olapOperation() {
			return getRuleContext(OlapOperationContext.class,0);
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
			setState(141);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(139);
				selectClause();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
				olapOperation();
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
		public SelectClauseContext selectClause() {
			return getRuleContext(SelectClauseContext.class,0);
		}
		public OlapClauseContext olapClause() {
			return getRuleContext(OlapClauseContext.class,0);
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
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			selectClause();
			setState(145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(144);
				olapClause();
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
	public static class OlapClauseContext extends ParserRuleContext {
		public List<OlapClauseItemContext> olapClauseItem() {
			return getRuleContexts(OlapClauseItemContext.class);
		}
		public OlapClauseItemContext olapClauseItem(int i) {
			return getRuleContext(OlapClauseItemContext.class,i);
		}
		public OlapClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_olapClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterOlapClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitOlapClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitOlapClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OlapClauseContext olapClause() throws RecognitionException {
		OlapClauseContext _localctx = new OlapClauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_olapClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8725724278030336L) != 0)) {
				{
				{
				setState(147);
				olapClauseItem();
				}
				}
				setState(152);
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
	public static class OlapClauseItemContext extends ParserRuleContext {
		public OlapClauseItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_olapClauseItem; }
	 
		public OlapClauseItemContext() { }
		public void copyFrom(OlapClauseItemContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PivotOperationContext extends OlapClauseItemContext {
		public TerminalNode PIVOT() { return getToken(JQuickSQLParser.PIVOT, 0); }
		public PivotSpecContext pivotSpec() {
			return getRuleContext(PivotSpecContext.class,0);
		}
		public PivotOperationContext(OlapClauseItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPivotOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPivotOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPivotOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RollupOperationContext extends OlapClauseItemContext {
		public TerminalNode ROLLUP() { return getToken(JQuickSQLParser.ROLLUP, 0); }
		public RollupDimensionsContext rollupDimensions() {
			return getRuleContext(RollupDimensionsContext.class,0);
		}
		public RollupOperationContext(OlapClauseItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterRollupOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitRollupOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitRollupOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DiceOperationContext extends OlapClauseItemContext {
		public TerminalNode DICE() { return getToken(JQuickSQLParser.DICE, 0); }
		public DiceConditionsContext diceConditions() {
			return getRuleContext(DiceConditionsContext.class,0);
		}
		public DiceOperationContext(OlapClauseItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDiceOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDiceOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDiceOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SliceOperationContext extends OlapClauseItemContext {
		public TerminalNode SLICE() { return getToken(JQuickSQLParser.SLICE, 0); }
		public SliceConditionContext sliceCondition() {
			return getRuleContext(SliceConditionContext.class,0);
		}
		public SliceOperationContext(OlapClauseItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterSliceOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitSliceOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitSliceOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DrilldownOperationContext extends OlapClauseItemContext {
		public TerminalNode DRILLDOWN() { return getToken(JQuickSQLParser.DRILLDOWN, 0); }
		public DrilldownDimensionsContext drilldownDimensions() {
			return getRuleContext(DrilldownDimensionsContext.class,0);
		}
		public DrilldownOperationContext(OlapClauseItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDrilldownOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDrilldownOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDrilldownOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OlapClauseItemContext olapClauseItem() throws RecognitionException {
		OlapClauseItemContext _localctx = new OlapClauseItemContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_olapClauseItem);
		try {
			setState(178);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROLLUP:
				_localctx = new RollupOperationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(153);
				match(ROLLUP);
				setState(154);
				match(T__2);
				setState(155);
				rollupDimensions();
				setState(156);
				match(T__3);
				}
				break;
			case DRILLDOWN:
				_localctx = new DrilldownOperationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				match(DRILLDOWN);
				setState(159);
				match(T__2);
				setState(160);
				drilldownDimensions();
				setState(161);
				match(T__3);
				}
				break;
			case SLICE:
				_localctx = new SliceOperationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(163);
				match(SLICE);
				setState(164);
				match(T__2);
				setState(165);
				sliceCondition();
				setState(166);
				match(T__3);
				}
				break;
			case DICE:
				_localctx = new DiceOperationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(168);
				match(DICE);
				setState(169);
				match(T__2);
				setState(170);
				diceConditions();
				setState(171);
				match(T__3);
				}
				break;
			case PIVOT:
				_localctx = new PivotOperationContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(173);
				match(PIVOT);
				setState(174);
				match(T__2);
				setState(175);
				pivotSpec();
				setState(176);
				match(T__3);
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
	public static class RollupDimensionsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public RollupDimensionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rollupDimensions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterRollupDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitRollupDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitRollupDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RollupDimensionsContext rollupDimensions() throws RecognitionException {
		RollupDimensionsContext _localctx = new RollupDimensionsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_rollupDimensions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			expression();
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(181);
				match(T__1);
				setState(182);
				expression();
				}
				}
				setState(187);
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
	public static class DrilldownDimensionsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public DrilldownDimensionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drilldownDimensions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDrilldownDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDrilldownDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDrilldownDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DrilldownDimensionsContext drilldownDimensions() throws RecognitionException {
		DrilldownDimensionsContext _localctx = new DrilldownDimensionsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_drilldownDimensions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			expression();
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(189);
				match(T__1);
				setState(190);
				expression();
				}
				}
				setState(195);
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
	public static class SliceConditionContext extends ParserRuleContext {
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
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
			setState(196);
			uid();
			setState(197);
			match(T__4);
			setState(198);
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
	public static class DiceConditionsContext extends ParserRuleContext {
		public List<DiceConditionContext> diceCondition() {
			return getRuleContexts(DiceConditionContext.class);
		}
		public DiceConditionContext diceCondition(int i) {
			return getRuleContext(DiceConditionContext.class,i);
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
		enterRule(_localctx, 18, RULE_diceConditions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			diceCondition();
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(201);
				match(T__1);
				setState(202);
				diceCondition();
				}
				}
				setState(207);
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
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
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
		enterRule(_localctx, 20, RULE_diceCondition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			predicate(0);
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
	public static class PivotSpecContext extends ParserRuleContext {
		public UidContext pivotColumn;
		public UidContext valueColumn;
		public FunctionCallContext aggregator;
		public List<UidContext> uid() {
			return getRuleContexts(UidContext.class);
		}
		public UidContext uid(int i) {
			return getRuleContext(UidContext.class,i);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public PivotSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pivotSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterPivotSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitPivotSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitPivotSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PivotSpecContext pivotSpec() throws RecognitionException {
		PivotSpecContext _localctx = new PivotSpecContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_pivotSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			((PivotSpecContext)_localctx).pivotColumn = uid();
			setState(211);
			match(T__1);
			setState(212);
			((PivotSpecContext)_localctx).valueColumn = uid();
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(213);
				match(T__1);
				setState(214);
				((PivotSpecContext)_localctx).aggregator = functionCall();
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
		enterRule(_localctx, 24, RULE_selectClause);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(SELECT);
			setState(219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(218);
				selectSpec();
				}
			}

			setState(221);
			selectElements();
			setState(222);
			fromClause();
			setState(226);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(223);
					joinClause();
					}
					} 
				}
				setState(228);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(230);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(229);
				whereClause();
				}
				break;
			}
			setState(233);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(232);
				groupByClause();
				}
				break;
			}
			setState(236);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(235);
				havingClause();
				}
				break;
			}
			setState(239);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(238);
				orderByClause();
				}
				break;
			}
			setState(242);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(241);
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
		enterRule(_localctx, 26, RULE_whereClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			match(WHERE);
			setState(245);
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
		enterRule(_localctx, 28, RULE_groupByClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			match(GROUP);
			setState(248);
			match(BY);
			setState(249);
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
		public FilterConditionContext filterCondition() {
			return getRuleContext(FilterConditionContext.class,0);
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
		enterRule(_localctx, 30, RULE_havingClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(HAVING);
			setState(252);
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
		enterRule(_localctx, 32, RULE_selectSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
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
		enterRule(_localctx, 34, RULE_selectElements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
				{
				setState(256);
				((SelectElementsContext)_localctx).star = match(T__5);
				}
				break;
			case T__2:
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
				setState(257);
				selectElement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(260);
				match(T__1);
				setState(261);
				selectElement();
				}
				}
				setState(266);
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
		enterRule(_localctx, 36, RULE_selectElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			expression();
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS || _la==IDENTIFIER) {
				{
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(268);
					match(AS);
					}
				}

				setState(271);
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
		enterRule(_localctx, 38, RULE_fromClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			match(FROM);
			setState(275);
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
		enterRule(_localctx, 40, RULE_joinClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			joinType();
			setState(278);
			match(JOIN);
			setState(279);
			tableNameItem();
			setState(280);
			match(ON);
			setState(281);
			fullColumnName();
			setState(282);
			match(T__4);
			setState(283);
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
		public SelectExpressionContext selectExpression() {
			return getRuleContext(SelectExpressionContext.class,0);
		}
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
		enterRule(_localctx, 42, RULE_tableNameItem);
		int _la;
		try {
			setState(298);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(285);
				tableNameSpec();
				setState(290);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(287);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==AS) {
						{
						setState(286);
						match(AS);
						}
					}

					setState(289);
					uid();
					}
					break;
				}
				}
				break;
			case T__2:
				enterOuterAlt(_localctx, 2);
				{
				setState(292);
				match(T__2);
				setState(293);
				selectExpression();
				setState(294);
				match(T__3);
				setState(295);
				match(AS);
				setState(296);
				uid();
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
		enterRule(_localctx, 44, RULE_tableNameSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
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
		enterRule(_localctx, 46, RULE_joinType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
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
		enterRule(_localctx, 48, RULE_orderByClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(ORDER);
			setState(305);
			match(BY);
			setState(306);
			orderByExpression();
			setState(311);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(307);
					match(T__1);
					setState(308);
					orderByExpression();
					}
					} 
				}
				setState(313);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
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
		enterRule(_localctx, 50, RULE_orderByExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			expression();
			setState(316);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(315);
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
		enterRule(_localctx, 52, RULE_limitClause);
		try {
			setState(322);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(318);
				match(LIMIT);
				setState(319);
				limitOnly();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(320);
				match(LIMIT);
				setState(321);
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
		enterRule(_localctx, 54, RULE_limitOnly);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
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
		enterRule(_localctx, 56, RULE_limitWithOffset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			((LimitWithOffsetContext)_localctx).offset = expression();
			setState(327);
			match(T__1);
			setState(328);
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
		enterRule(_localctx, 58, RULE_commonTableExpression);
		int _la;
		try {
			setState(353);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(330);
				uid();
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(331);
					columnNames();
					}
				}

				setState(334);
				match(AS);
				setState(335);
				match(T__2);
				setState(336);
				selectStatement();
				setState(337);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(339);
				uid();
				setState(341);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(340);
					columnNames();
					}
				}

				setState(343);
				match(AS);
				setState(344);
				match(T__2);
				setState(345);
				initialQuery();
				setState(346);
				match(UNION);
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL) {
					{
					setState(347);
					match(ALL);
					}
				}

				setState(350);
				recursivePart();
				setState(351);
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
		enterRule(_localctx, 60, RULE_columnNames);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
			match(T__2);
			setState(356);
			uid();
			setState(361);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(357);
				match(T__1);
				setState(358);
				uid();
				}
				}
				setState(363);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(364);
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
		enterRule(_localctx, 62, RULE_initialQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366);
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
		enterRule(_localctx, 64, RULE_recursivePart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(368);
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
		public ArgContext arg() {
			return getRuleContext(ArgContext.class,0);
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
		enterRule(_localctx, 66, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(370);
			uid();
			setState(371);
			match(T__2);
			setState(373);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4399187373256L) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & 31L) != 0)) {
				{
				setState(372);
				arg();
				}
			}

			setState(375);
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
	public static class ArgContext extends ParserRuleContext {
		public FunctionArgsContext functionArgs() {
			return getRuleContext(FunctionArgsContext.class,0);
		}
		public ArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgContext arg() throws RecognitionException {
		ArgContext _localctx = new ArgContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_arg);
		try {
			setState(379);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
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
				enterOuterAlt(_localctx, 1);
				{
				setState(377);
				functionArgs();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(378);
				match(T__5);
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
		enterRule(_localctx, 70, RULE_functionArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(381);
			functionArg();
			setState(386);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(382);
				match(T__1);
				setState(383);
				functionArg();
				}
				}
				setState(388);
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
		enterRule(_localctx, 72, RULE_functionArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
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
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_filterCondition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(392);
				match(T__2);
				setState(393);
				filterCondition(0);
				setState(394);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(396);
				predicate(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(407);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(405);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
					case 1:
						{
						_localctx = new FilterConditionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_filterCondition);
						setState(399);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						{
						setState(400);
						match(AND);
						setState(401);
						filterCondition(0);
						}
						}
						break;
					case 2:
						{
						_localctx = new FilterConditionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_filterCondition);
						setState(402);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						{
						setState(403);
						match(OR);
						setState(404);
						filterCondition(0);
						}
						}
						break;
					}
					} 
				}
				setState(409);
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
		int _startState = 76;
		enterRecursionRule(_localctx, 76, RULE_predicate, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
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

				setState(411);
				expressionAtom(0);
				}
				break;
			case EXISTS:
				{
				_localctx = new ExisitsPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(412);
				match(EXISTS);
				setState(413);
				expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(461);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(459);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryComparisonPredicateContext(new PredicateContext(_parentctx, _parentState));
						((BinaryComparisonPredicateContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(416);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(417);
						comparisonOperator();
						setState(418);
						((BinaryComparisonPredicateContext)_localctx).right = predicate(7);
						}
						break;
					case 2:
						{
						_localctx = new BetweenPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(420);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(422);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(421);
							match(NOT);
							}
						}

						setState(424);
						match(BETWEEN);
						setState(425);
						predicate(0);
						setState(426);
						match(AND);
						setState(427);
						predicate(6);
						}
						break;
					case 3:
						{
						_localctx = new LikePredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(429);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(431);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(430);
							match(NOT);
							}
						}

						setState(433);
						match(LIKE);
						setState(434);
						predicate(4);
						}
						break;
					case 4:
						{
						_localctx = new RegexpPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(435);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(437);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(436);
							match(NOT);
							}
						}

						setState(439);
						((RegexpPredicateContext)_localctx).regex = match(REGEXP);
						setState(440);
						predicate(3);
						}
						break;
					case 5:
						{
						_localctx = new IsNullPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(441);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(442);
						match(IS);
						setState(444);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(443);
							match(NOT);
							}
						}

						setState(446);
						match(NULL);
						}
						break;
					case 6:
						{
						_localctx = new InPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(447);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(449);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(448);
							match(NOT);
							}
						}

						setState(451);
						match(IN);
						setState(452);
						match(T__2);
						setState(455);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
						case 1:
							{
							setState(453);
							selectStatement();
							}
							break;
						case 2:
							{
							setState(454);
							expressions();
							}
							break;
						}
						setState(457);
						match(T__3);
						}
						break;
					}
					} 
				}
				setState(463);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
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
		int _startState = 78;
		enterRecursionRule(_localctx, 78, RULE_expressionAtom, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(486);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				{
				_localctx = new ConstantExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(465);
				constant();
				}
				break;
			case 2:
				{
				_localctx = new FullColumnNameExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(466);
				fullColumnName();
				}
				break;
			case 3:
				{
				_localctx = new FunctionCallExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(467);
				functionCall();
				}
				break;
			case 4:
				{
				_localctx = new NestedExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(468);
				match(T__2);
				setState(469);
				expression();
				setState(474);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(470);
					match(T__1);
					setState(471);
					expression();
					}
					}
					setState(476);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(477);
				match(T__3);
				}
				break;
			case 5:
				{
				_localctx = new SubqueryExperssionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(479);
				match(T__2);
				setState(480);
				selectStatement();
				setState(481);
				match(T__3);
				}
				break;
			case 6:
				{
				_localctx = new UnaryExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(483);
				unaryOperator();
				setState(484);
				expressionAtom(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(494);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MathExpressionAtomContext(new ExpressionAtomContext(_parentctx, _parentState));
					((MathExpressionAtomContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expressionAtom);
					setState(488);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(489);
					mathOperator();
					setState(490);
					((MathExpressionAtomContext)_localctx).right = expressionAtom(3);
					}
					} 
				}
				setState(496);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
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
		enterRule(_localctx, 80, RULE_expressions);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(497);
			expression();
			setState(502);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(498);
					match(T__1);
					setState(499);
					expression();
					}
					} 
				}
				setState(504);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
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
		enterRule(_localctx, 82, RULE_expression);
		int _la;
		try {
			setState(513);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				_localctx = new ParenExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(505);
				match(T__2);
				setState(506);
				expression();
				setState(507);
				match(T__3);
				}
				break;
			case 2:
				_localctx = new NotExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(509);
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
				setState(510);
				expression();
				}
				break;
			case 3:
				_localctx = new PredicateExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(511);
				expressionAtom(0);
				}
				break;
			case 4:
				_localctx = new SelectResultContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(512);
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
		enterRule(_localctx, 84, RULE_mathOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8000L) != 0)) ) {
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
		enterRule(_localctx, 86, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(517);
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
		enterRule(_localctx, 88, RULE_logicalOperator);
		try {
			setState(523);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(519);
				match(AND);
				}
				break;
			case XOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(520);
				match(XOR);
				}
				break;
			case OR:
				enterOuterAlt(_localctx, 3);
				{
				setState(521);
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
		enterRule(_localctx, 90, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(525);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1032224L) != 0)) ) {
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
		enterRule(_localctx, 92, RULE_constant);
		try {
			setState(534);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(527);
				stringLiteral();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(528);
				decimal_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(529);
				match(T__10);
				setState(530);
				decimal_literal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(531);
				booleanLiteral();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(532);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(533);
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
		enterRule(_localctx, 94, RULE_decimal_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(536);
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
		enterRule(_localctx, 96, RULE_null_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(538);
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
		enterRule(_localctx, 98, RULE_dateLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			stringLiteral();
			setState(541);
			match(T__19);
			setState(542);
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
		enterRule(_localctx, 100, RULE_format);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(544);
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
		enterRule(_localctx, 102, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
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
		public DottedIdContext dottedId() {
			return getRuleContext(DottedIdContext.class,0);
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
		enterRule(_localctx, 104, RULE_fullColumnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(548);
			uid();
			setState(550);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(549);
				dottedId();
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
		enterRule(_localctx, 106, RULE_dottedId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(552);
			match(T__20);
			setState(553);
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
		enterRule(_localctx, 108, RULE_uid);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(555);
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
		enterRule(_localctx, 110, RULE_stringLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
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
		enterRule(_localctx, 112, RULE_simpleId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(559);
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
		enterRule(_localctx, 114, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
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
		case 37:
			return filterCondition_sempred((FilterConditionContext)_localctx, predIndex);
		case 38:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 39:
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
		"\u0004\u0001L\u0234\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0001\u0000\u0001\u0000\u0003\u0000"+
		"w\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001"+
		"}\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001\u0082\b\u0001"+
		"\n\u0001\f\u0001\u0085\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003"+
		"\u0001\u008a\b\u0001\u0001\u0002\u0001\u0002\u0003\u0002\u008e\b\u0002"+
		"\u0001\u0003\u0001\u0003\u0003\u0003\u0092\b\u0003\u0001\u0004\u0005\u0004"+
		"\u0095\b\u0004\n\u0004\f\u0004\u0098\t\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u00b3"+
		"\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00b8\b\u0006"+
		"\n\u0006\f\u0006\u00bb\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0005"+
		"\u0007\u00c0\b\u0007\n\u0007\f\u0007\u00c3\t\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u00cc\b\t\n\t\f\t\u00cf\t\t"+
		"\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u00d8\b\u000b\u0001\f\u0001\f\u0003\f\u00dc\b\f\u0001"+
		"\f\u0001\f\u0001\f\u0005\f\u00e1\b\f\n\f\f\f\u00e4\t\f\u0001\f\u0003\f"+
		"\u00e7\b\f\u0001\f\u0003\f\u00ea\b\f\u0001\f\u0003\f\u00ed\b\f\u0001\f"+
		"\u0003\f\u00f0\b\f\u0001\f\u0003\f\u00f3\b\f\u0001\r\u0001\r\u0001\r\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0003\u0011\u0103"+
		"\b\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u0107\b\u0011\n\u0011\f\u0011"+
		"\u010a\t\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u010e\b\u0012\u0001"+
		"\u0012\u0003\u0012\u0111\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0003\u0015\u0120\b\u0015\u0001"+
		"\u0015\u0003\u0015\u0123\b\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u012b\b\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0005\u0018\u0136\b\u0018\n\u0018\f\u0018\u0139\t\u0018"+
		"\u0001\u0019\u0001\u0019\u0003\u0019\u013d\b\u0019\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0003\u001a\u0143\b\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d"+
		"\u0003\u001d\u014d\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0156\b\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u015d\b\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0162\b\u001d\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u0168\b\u001e\n\u001e"+
		"\f\u001e\u016b\t\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f"+
		"\u0001 \u0001 \u0001!\u0001!\u0001!\u0003!\u0176\b!\u0001!\u0001!\u0001"+
		"\"\u0001\"\u0003\"\u017c\b\"\u0001#\u0001#\u0001#\u0005#\u0181\b#\n#\f"+
		"#\u0184\t#\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003"+
		"%\u018e\b%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0005%\u0196\b%\n"+
		"%\f%\u0199\t%\u0001&\u0001&\u0001&\u0001&\u0003&\u019f\b&\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0003&\u01a7\b&\u0001&\u0001&\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0003&\u01b0\b&\u0001&\u0001&\u0001&\u0001&\u0003"+
		"&\u01b6\b&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u01bd\b&\u0001&\u0001"+
		"&\u0001&\u0003&\u01c2\b&\u0001&\u0001&\u0001&\u0001&\u0003&\u01c8\b&\u0001"+
		"&\u0001&\u0005&\u01cc\b&\n&\f&\u01cf\t&\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0005\'\u01d9\b\'\n\'\f\'\u01dc\t\'"+
		"\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0003\'\u01e7\b\'\u0001\'\u0001\'\u0001\'\u0001\'\u0005\'\u01ed\b\'"+
		"\n\'\f\'\u01f0\t\'\u0001(\u0001(\u0001(\u0005(\u01f5\b(\n(\f(\u01f8\t"+
		"(\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0003)\u0202"+
		"\b)\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0003,\u020c"+
		"\b,\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0003"+
		".\u0217\b.\u0001/\u0001/\u00010\u00010\u00011\u00011\u00011\u00011\u0001"+
		"2\u00012\u00013\u00013\u00014\u00014\u00034\u0227\b4\u00015\u00015\u0001"+
		"5\u00016\u00016\u00017\u00017\u00018\u00018\u00019\u00019\u00019\u0000"+
		"\u0003JLN:\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnpr\u0000"+
		"\t\u0001\u0000AB\u0002\u0000668<\u0001\u0000\u001c\u001d\u0002\u0000\u0007"+
		"\u0007**\u0002\u0000\u0006\u0006\b\f\u0004\u0000\u0007\u0007\n\u000b\r"+
		"\r**\u0002\u0000\u0005\u0005\u000e\u0013\u0001\u0000CD\u0003\u0000\u001e"+
		"*-?AB\u0245\u0000t\u0001\u0000\u0000\u0000\u0002\u0089\u0001\u0000\u0000"+
		"\u0000\u0004\u008d\u0001\u0000\u0000\u0000\u0006\u008f\u0001\u0000\u0000"+
		"\u0000\b\u0096\u0001\u0000\u0000\u0000\n\u00b2\u0001\u0000\u0000\u0000"+
		"\f\u00b4\u0001\u0000\u0000\u0000\u000e\u00bc\u0001\u0000\u0000\u0000\u0010"+
		"\u00c4\u0001\u0000\u0000\u0000\u0012\u00c8\u0001\u0000\u0000\u0000\u0014"+
		"\u00d0\u0001\u0000\u0000\u0000\u0016\u00d2\u0001\u0000\u0000\u0000\u0018"+
		"\u00d9\u0001\u0000\u0000\u0000\u001a\u00f4\u0001\u0000\u0000\u0000\u001c"+
		"\u00f7\u0001\u0000\u0000\u0000\u001e\u00fb\u0001\u0000\u0000\u0000 \u00fe"+
		"\u0001\u0000\u0000\u0000\"\u0102\u0001\u0000\u0000\u0000$\u010b\u0001"+
		"\u0000\u0000\u0000&\u0112\u0001\u0000\u0000\u0000(\u0115\u0001\u0000\u0000"+
		"\u0000*\u012a\u0001\u0000\u0000\u0000,\u012c\u0001\u0000\u0000\u0000."+
		"\u012e\u0001\u0000\u0000\u00000\u0130\u0001\u0000\u0000\u00002\u013a\u0001"+
		"\u0000\u0000\u00004\u0142\u0001\u0000\u0000\u00006\u0144\u0001\u0000\u0000"+
		"\u00008\u0146\u0001\u0000\u0000\u0000:\u0161\u0001\u0000\u0000\u0000<"+
		"\u0163\u0001\u0000\u0000\u0000>\u016e\u0001\u0000\u0000\u0000@\u0170\u0001"+
		"\u0000\u0000\u0000B\u0172\u0001\u0000\u0000\u0000D\u017b\u0001\u0000\u0000"+
		"\u0000F\u017d\u0001\u0000\u0000\u0000H\u0185\u0001\u0000\u0000\u0000J"+
		"\u018d\u0001\u0000\u0000\u0000L\u019e\u0001\u0000\u0000\u0000N\u01e6\u0001"+
		"\u0000\u0000\u0000P\u01f1\u0001\u0000\u0000\u0000R\u0201\u0001\u0000\u0000"+
		"\u0000T\u0203\u0001\u0000\u0000\u0000V\u0205\u0001\u0000\u0000\u0000X"+
		"\u020b\u0001\u0000\u0000\u0000Z\u020d\u0001\u0000\u0000\u0000\\\u0216"+
		"\u0001\u0000\u0000\u0000^\u0218\u0001\u0000\u0000\u0000`\u021a\u0001\u0000"+
		"\u0000\u0000b\u021c\u0001\u0000\u0000\u0000d\u0220\u0001\u0000\u0000\u0000"+
		"f\u0222\u0001\u0000\u0000\u0000h\u0224\u0001\u0000\u0000\u0000j\u0228"+
		"\u0001\u0000\u0000\u0000l\u022b\u0001\u0000\u0000\u0000n\u022d\u0001\u0000"+
		"\u0000\u0000p\u022f\u0001\u0000\u0000\u0000r\u0231\u0001\u0000\u0000\u0000"+
		"tv\u0003\u0002\u0001\u0000uw\u0005\u0001\u0000\u0000vu\u0001\u0000\u0000"+
		"\u0000vw\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0005\u0000"+
		"\u0000\u0001y\u0001\u0001\u0000\u0000\u0000z|\u0005.\u0000\u0000{}\u0005"+
		"/\u0000\u0000|{\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~\u0083\u0003:\u001d\u0000\u007f\u0080\u0005\u0002\u0000"+
		"\u0000\u0080\u0082\u0003:\u001d\u0000\u0081\u007f\u0001\u0000\u0000\u0000"+
		"\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000"+
		"\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0086\u0001\u0000\u0000\u0000"+
		"\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0087\u0003\u0004\u0002\u0000"+
		"\u0087\u008a\u0001\u0000\u0000\u0000\u0088\u008a\u0003\u0004\u0002\u0000"+
		"\u0089z\u0001\u0000\u0000\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u008a"+
		"\u0003\u0001\u0000\u0000\u0000\u008b\u008e\u0003\u0018\f\u0000\u008c\u008e"+
		"\u0003\u0006\u0003\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008c"+
		"\u0001\u0000\u0000\u0000\u008e\u0005\u0001\u0000\u0000\u0000\u008f\u0091"+
		"\u0003\u0018\f\u0000\u0090\u0092\u0003\b\u0004\u0000\u0091\u0090\u0001"+
		"\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0007\u0001"+
		"\u0000\u0000\u0000\u0093\u0095\u0003\n\u0005\u0000\u0094\u0093\u0001\u0000"+
		"\u0000\u0000\u0095\u0098\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\t\u0001\u0000\u0000"+
		"\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0099\u009a\u00050\u0000\u0000"+
		"\u009a\u009b\u0005\u0003\u0000\u0000\u009b\u009c\u0003\f\u0006\u0000\u009c"+
		"\u009d\u0005\u0004\u0000\u0000\u009d\u00b3\u0001\u0000\u0000\u0000\u009e"+
		"\u009f\u00051\u0000\u0000\u009f\u00a0\u0005\u0003\u0000\u0000\u00a0\u00a1"+
		"\u0003\u000e\u0007\u0000\u00a1\u00a2\u0005\u0004\u0000\u0000\u00a2\u00b3"+
		"\u0001\u0000\u0000\u0000\u00a3\u00a4\u00052\u0000\u0000\u00a4\u00a5\u0005"+
		"\u0003\u0000\u0000\u00a5\u00a6\u0003\u0010\b\u0000\u00a6\u00a7\u0005\u0004"+
		"\u0000\u0000\u00a7\u00b3\u0001\u0000\u0000\u0000\u00a8\u00a9\u00053\u0000"+
		"\u0000\u00a9\u00aa\u0005\u0003\u0000\u0000\u00aa\u00ab\u0003\u0012\t\u0000"+
		"\u00ab\u00ac\u0005\u0004\u0000\u0000\u00ac\u00b3\u0001\u0000\u0000\u0000"+
		"\u00ad\u00ae\u00054\u0000\u0000\u00ae\u00af\u0005\u0003\u0000\u0000\u00af"+
		"\u00b0\u0003\u0016\u000b\u0000\u00b0\u00b1\u0005\u0004\u0000\u0000\u00b1"+
		"\u00b3\u0001\u0000\u0000\u0000\u00b2\u0099\u0001\u0000\u0000\u0000\u00b2"+
		"\u009e\u0001\u0000\u0000\u0000\u00b2\u00a3\u0001\u0000\u0000\u0000\u00b2"+
		"\u00a8\u0001\u0000\u0000\u0000\u00b2\u00ad\u0001\u0000\u0000\u0000\u00b3"+
		"\u000b\u0001\u0000\u0000\u0000\u00b4\u00b9\u0003R)\u0000\u00b5\u00b6\u0005"+
		"\u0002\u0000\u0000\u00b6\u00b8\u0003R)\u0000\u00b7\u00b5\u0001\u0000\u0000"+
		"\u0000\u00b8\u00bb\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000"+
		"\u0000\u00b9\u00ba\u0001\u0000\u0000\u0000\u00ba\r\u0001\u0000\u0000\u0000"+
		"\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bc\u00c1\u0003R)\u0000\u00bd\u00be"+
		"\u0005\u0002\u0000\u0000\u00be\u00c0\u0003R)\u0000\u00bf\u00bd\u0001\u0000"+
		"\u0000\u0000\u00c0\u00c3\u0001\u0000\u0000\u0000\u00c1\u00bf\u0001\u0000"+
		"\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u000f\u0001\u0000"+
		"\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c4\u00c5\u0003l6\u0000"+
		"\u00c5\u00c6\u0005\u0005\u0000\u0000\u00c6\u00c7\u0003R)\u0000\u00c7\u0011"+
		"\u0001\u0000\u0000\u0000\u00c8\u00cd\u0003\u0014\n\u0000\u00c9\u00ca\u0005"+
		"\u0002\u0000\u0000\u00ca\u00cc\u0003\u0014\n\u0000\u00cb\u00c9\u0001\u0000"+
		"\u0000\u0000\u00cc\u00cf\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000"+
		"\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u0013\u0001\u0000"+
		"\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000\u00d0\u00d1\u0003L&\u0000"+
		"\u00d1\u0015\u0001\u0000\u0000\u0000\u00d2\u00d3\u0003l6\u0000\u00d3\u00d4"+
		"\u0005\u0002\u0000\u0000\u00d4\u00d7\u0003l6\u0000\u00d5\u00d6\u0005\u0002"+
		"\u0000\u0000\u00d6\u00d8\u0003B!\u0000\u00d7\u00d5\u0001\u0000\u0000\u0000"+
		"\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u0017\u0001\u0000\u0000\u0000"+
		"\u00d9\u00db\u0005\u001e\u0000\u0000\u00da\u00dc\u0003 \u0010\u0000\u00db"+
		"\u00da\u0001\u0000\u0000\u0000\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc"+
		"\u00dd\u0001\u0000\u0000\u0000\u00dd\u00de\u0003\"\u0011\u0000\u00de\u00e2"+
		"\u0003&\u0013\u0000\u00df\u00e1\u0003(\u0014\u0000\u00e0\u00df\u0001\u0000"+
		"\u0000\u0000\u00e1\u00e4\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001\u0000"+
		"\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000\u00e3\u00e6\u0001\u0000"+
		"\u0000\u0000\u00e4\u00e2\u0001\u0000\u0000\u0000\u00e5\u00e7\u0003\u001a"+
		"\r\u0000\u00e6\u00e5\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000"+
		"\u0000\u00e7\u00e9\u0001\u0000\u0000\u0000\u00e8\u00ea\u0003\u001c\u000e"+
		"\u0000\u00e9\u00e8\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000"+
		"\u0000\u00ea\u00ec\u0001\u0000\u0000\u0000\u00eb\u00ed\u0003\u001e\u000f"+
		"\u0000\u00ec\u00eb\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000\u0000"+
		"\u0000\u00ed\u00ef\u0001\u0000\u0000\u0000\u00ee\u00f0\u00030\u0018\u0000"+
		"\u00ef\u00ee\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000"+
		"\u00f0\u00f2\u0001\u0000\u0000\u0000\u00f1\u00f3\u00034\u001a\u0000\u00f2"+
		"\u00f1\u0001\u0000\u0000\u0000\u00f2\u00f3\u0001\u0000\u0000\u0000\u00f3"+
		"\u0019\u0001\u0000\u0000\u0000\u00f4\u00f5\u0005 \u0000\u0000\u00f5\u00f6"+
		"\u0003J%\u0000\u00f6\u001b\u0001\u0000\u0000\u0000\u00f7\u00f8\u0005!"+
		"\u0000\u0000\u00f8\u00f9\u0005\"\u0000\u0000\u00f9\u00fa\u0003P(\u0000"+
		"\u00fa\u001d\u0001\u0000\u0000\u0000\u00fb\u00fc\u0005#\u0000\u0000\u00fc"+
		"\u00fd\u0003J%\u0000\u00fd\u001f\u0001\u0000\u0000\u0000\u00fe\u00ff\u0007"+
		"\u0000\u0000\u0000\u00ff!\u0001\u0000\u0000\u0000\u0100\u0103\u0005\u0006"+
		"\u0000\u0000\u0101\u0103\u0003$\u0012\u0000\u0102\u0100\u0001\u0000\u0000"+
		"\u0000\u0102\u0101\u0001\u0000\u0000\u0000\u0103\u0108\u0001\u0000\u0000"+
		"\u0000\u0104\u0105\u0005\u0002\u0000\u0000\u0105\u0107\u0003$\u0012\u0000"+
		"\u0106\u0104\u0001\u0000\u0000\u0000\u0107\u010a\u0001\u0000\u0000\u0000"+
		"\u0108\u0106\u0001\u0000\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000"+
		"\u0109#\u0001\u0000\u0000\u0000\u010a\u0108\u0001\u0000\u0000\u0000\u010b"+
		"\u0110\u0003R)\u0000\u010c\u010e\u0005-\u0000\u0000\u010d\u010c\u0001"+
		"\u0000\u0000\u0000\u010d\u010e\u0001\u0000\u0000\u0000\u010e\u010f\u0001"+
		"\u0000\u0000\u0000\u010f\u0111\u0003l6\u0000\u0110\u010d\u0001\u0000\u0000"+
		"\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111%\u0001\u0000\u0000\u0000"+
		"\u0112\u0113\u0005\u001f\u0000\u0000\u0113\u0114\u0003*\u0015\u0000\u0114"+
		"\'\u0001\u0000\u0000\u0000\u0115\u0116\u0003.\u0017\u0000\u0116\u0117"+
		"\u00055\u0000\u0000\u0117\u0118\u0003*\u0015\u0000\u0118\u0119\u0005\u0016"+
		"\u0000\u0000\u0119\u011a\u0003h4\u0000\u011a\u011b\u0005\u0005\u0000\u0000"+
		"\u011b\u011c\u0003h4\u0000\u011c)\u0001\u0000\u0000\u0000\u011d\u0122"+
		"\u0003,\u0016\u0000\u011e\u0120\u0005-\u0000\u0000\u011f\u011e\u0001\u0000"+
		"\u0000\u0000\u011f\u0120\u0001\u0000\u0000\u0000\u0120\u0121\u0001\u0000"+
		"\u0000\u0000\u0121\u0123\u0003l6\u0000\u0122\u011f\u0001\u0000\u0000\u0000"+
		"\u0122\u0123\u0001\u0000\u0000\u0000\u0123\u012b\u0001\u0000\u0000\u0000"+
		"\u0124\u0125\u0005\u0003\u0000\u0000\u0125\u0126\u0003\u0004\u0002\u0000"+
		"\u0126\u0127\u0005\u0004\u0000\u0000\u0127\u0128\u0005-\u0000\u0000\u0128"+
		"\u0129\u0003l6\u0000\u0129\u012b\u0001\u0000\u0000\u0000\u012a\u011d\u0001"+
		"\u0000\u0000\u0000\u012a\u0124\u0001\u0000\u0000\u0000\u012b+\u0001\u0000"+
		"\u0000\u0000\u012c\u012d\u0005E\u0000\u0000\u012d-\u0001\u0000\u0000\u0000"+
		"\u012e\u012f\u0007\u0001\u0000\u0000\u012f/\u0001\u0000\u0000\u0000\u0130"+
		"\u0131\u0005$\u0000\u0000\u0131\u0132\u0005\"\u0000\u0000\u0132\u0137"+
		"\u00032\u0019\u0000\u0133\u0134\u0005\u0002\u0000\u0000\u0134\u0136\u0003"+
		"2\u0019\u0000\u0135\u0133\u0001\u0000\u0000\u0000\u0136\u0139\u0001\u0000"+
		"\u0000\u0000\u0137\u0135\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000"+
		"\u0000\u0000\u01381\u0001\u0000\u0000\u0000\u0139\u0137\u0001\u0000\u0000"+
		"\u0000\u013a\u013c\u0003R)\u0000\u013b\u013d\u0007\u0002\u0000\u0000\u013c"+
		"\u013b\u0001\u0000\u0000\u0000\u013c\u013d\u0001\u0000\u0000\u0000\u013d"+
		"3\u0001\u0000\u0000\u0000\u013e\u013f\u0005%\u0000\u0000\u013f\u0143\u0003"+
		"6\u001b\u0000\u0140\u0141\u0005%\u0000\u0000\u0141\u0143\u00038\u001c"+
		"\u0000\u0142\u013e\u0001\u0000\u0000\u0000\u0142\u0140\u0001\u0000\u0000"+
		"\u0000\u01435\u0001\u0000\u0000\u0000\u0144\u0145\u0003R)\u0000\u0145"+
		"7\u0001\u0000\u0000\u0000\u0146\u0147\u0003R)\u0000\u0147\u0148\u0005"+
		"\u0002\u0000\u0000\u0148\u0149\u0003R)\u0000\u01499\u0001\u0000\u0000"+
		"\u0000\u014a\u014c\u0003l6\u0000\u014b\u014d\u0003<\u001e\u0000\u014c"+
		"\u014b\u0001\u0000\u0000\u0000\u014c\u014d\u0001\u0000\u0000\u0000\u014d"+
		"\u014e\u0001\u0000\u0000\u0000\u014e\u014f\u0005-\u0000\u0000\u014f\u0150"+
		"\u0005\u0003\u0000\u0000\u0150\u0151\u0003\u0002\u0001\u0000\u0151\u0152"+
		"\u0005\u0004\u0000\u0000\u0152\u0162\u0001\u0000\u0000\u0000\u0153\u0155"+
		"\u0003l6\u0000\u0154\u0156\u0003<\u001e\u0000\u0155\u0154\u0001\u0000"+
		"\u0000\u0000\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u0157\u0001\u0000"+
		"\u0000\u0000\u0157\u0158\u0005-\u0000\u0000\u0158\u0159\u0005\u0003\u0000"+
		"\u0000\u0159\u015a\u0003>\u001f\u0000\u015a\u015c\u0005@\u0000\u0000\u015b"+
		"\u015d\u0005A\u0000\u0000\u015c\u015b\u0001\u0000\u0000\u0000\u015c\u015d"+
		"\u0001\u0000\u0000\u0000\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u015f"+
		"\u0003@ \u0000\u015f\u0160\u0005\u0004\u0000\u0000\u0160\u0162\u0001\u0000"+
		"\u0000\u0000\u0161\u014a\u0001\u0000\u0000\u0000\u0161\u0153\u0001\u0000"+
		"\u0000\u0000\u0162;\u0001\u0000\u0000\u0000\u0163\u0164\u0005\u0003\u0000"+
		"\u0000\u0164\u0169\u0003l6\u0000\u0165\u0166\u0005\u0002\u0000\u0000\u0166"+
		"\u0168\u0003l6\u0000\u0167\u0165\u0001\u0000\u0000\u0000\u0168\u016b\u0001"+
		"\u0000\u0000\u0000\u0169\u0167\u0001\u0000\u0000\u0000\u0169\u016a\u0001"+
		"\u0000\u0000\u0000\u016a\u016c\u0001\u0000\u0000\u0000\u016b\u0169\u0001"+
		"\u0000\u0000\u0000\u016c\u016d\u0005\u0004\u0000\u0000\u016d=\u0001\u0000"+
		"\u0000\u0000\u016e\u016f\u0003\u0002\u0001\u0000\u016f?\u0001\u0000\u0000"+
		"\u0000\u0170\u0171\u0003\u0002\u0001\u0000\u0171A\u0001\u0000\u0000\u0000"+
		"\u0172\u0173\u0003l6\u0000\u0173\u0175\u0005\u0003\u0000\u0000\u0174\u0176"+
		"\u0003D\"\u0000\u0175\u0174\u0001\u0000\u0000\u0000\u0175\u0176\u0001"+
		"\u0000\u0000\u0000\u0176\u0177\u0001\u0000\u0000\u0000\u0177\u0178\u0005"+
		"\u0004\u0000\u0000\u0178C\u0001\u0000\u0000\u0000\u0179\u017c\u0003F#"+
		"\u0000\u017a\u017c\u0005\u0006\u0000\u0000\u017b\u0179\u0001\u0000\u0000"+
		"\u0000\u017b\u017a\u0001\u0000\u0000\u0000\u017cE\u0001\u0000\u0000\u0000"+
		"\u017d\u0182\u0003H$\u0000\u017e\u017f\u0005\u0002\u0000\u0000\u017f\u0181"+
		"\u0003H$\u0000\u0180\u017e\u0001\u0000\u0000\u0000\u0181\u0184\u0001\u0000"+
		"\u0000\u0000\u0182\u0180\u0001\u0000\u0000\u0000\u0182\u0183\u0001\u0000"+
		"\u0000\u0000\u0183G\u0001\u0000\u0000\u0000\u0184\u0182\u0001\u0000\u0000"+
		"\u0000\u0185\u0186\u0003R)\u0000\u0186I\u0001\u0000\u0000\u0000\u0187"+
		"\u0188\u0006%\uffff\uffff\u0000\u0188\u0189\u0005\u0003\u0000\u0000\u0189"+
		"\u018a\u0003J%\u0000\u018a\u018b\u0005\u0004\u0000\u0000\u018b\u018e\u0001"+
		"\u0000\u0000\u0000\u018c\u018e\u0003L&\u0000\u018d\u0187\u0001\u0000\u0000"+
		"\u0000\u018d\u018c\u0001\u0000\u0000\u0000\u018e\u0197\u0001\u0000\u0000"+
		"\u0000\u018f\u0190\n\u0004\u0000\u0000\u0190\u0191\u0005\'\u0000\u0000"+
		"\u0191\u0196\u0003J%\u0000\u0192\u0193\n\u0003\u0000\u0000\u0193\u0194"+
		"\u0005(\u0000\u0000\u0194\u0196\u0003J%\u0000\u0195\u018f\u0001\u0000"+
		"\u0000\u0000\u0195\u0192\u0001\u0000\u0000\u0000\u0196\u0199\u0001\u0000"+
		"\u0000\u0000\u0197\u0195\u0001\u0000\u0000\u0000\u0197\u0198\u0001\u0000"+
		"\u0000\u0000\u0198K\u0001\u0000\u0000\u0000\u0199\u0197\u0001\u0000\u0000"+
		"\u0000\u019a\u019b\u0006&\uffff\uffff\u0000\u019b\u019f\u0003N\'\u0000"+
		"\u019c\u019d\u0005\u001b\u0000\u0000\u019d\u019f\u0003R)\u0000\u019e\u019a"+
		"\u0001\u0000\u0000\u0000\u019e\u019c\u0001\u0000\u0000\u0000\u019f\u01cd"+
		"\u0001\u0000\u0000\u0000\u01a0\u01a1\n\u0006\u0000\u0000\u01a1\u01a2\u0003"+
		"Z-\u0000\u01a2\u01a3\u0003L&\u0007\u01a3\u01cc\u0001\u0000\u0000\u0000"+
		"\u01a4\u01a6\n\u0005\u0000\u0000\u01a5\u01a7\u0005*\u0000\u0000\u01a6"+
		"\u01a5\u0001\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000\u0000\u0000\u01a7"+
		"\u01a8\u0001\u0000\u0000\u0000\u01a8\u01a9\u0005,\u0000\u0000\u01a9\u01aa"+
		"\u0003L&\u0000\u01aa\u01ab\u0005\'\u0000\u0000\u01ab\u01ac\u0003L&\u0006"+
		"\u01ac\u01cc\u0001\u0000\u0000\u0000\u01ad\u01af\n\u0003\u0000\u0000\u01ae"+
		"\u01b0\u0005*\u0000\u0000\u01af\u01ae\u0001\u0000\u0000\u0000\u01af\u01b0"+
		"\u0001\u0000\u0000\u0000\u01b0\u01b1\u0001\u0000\u0000\u0000\u01b1\u01b2"+
		"\u0005\u0018\u0000\u0000\u01b2\u01cc\u0003L&\u0004\u01b3\u01b5\n\u0002"+
		"\u0000\u0000\u01b4\u01b6\u0005*\u0000\u0000\u01b5\u01b4\u0001\u0000\u0000"+
		"\u0000\u01b5\u01b6\u0001\u0000\u0000\u0000\u01b6\u01b7\u0001\u0000\u0000"+
		"\u0000\u01b7\u01b8\u0005\u0017\u0000\u0000\u01b8\u01cc\u0003L&\u0003\u01b9"+
		"\u01ba\n\u0007\u0000\u0000\u01ba\u01bc\u0005\u0019\u0000\u0000\u01bb\u01bd"+
		"\u0005*\u0000\u0000\u01bc\u01bb\u0001\u0000\u0000\u0000\u01bc\u01bd\u0001"+
		"\u0000\u0000\u0000\u01bd\u01be\u0001\u0000\u0000\u0000\u01be\u01cc\u0005"+
		"\u001a\u0000\u0000\u01bf\u01c1\n\u0004\u0000\u0000\u01c0\u01c2\u0005*"+
		"\u0000\u0000\u01c1\u01c0\u0001\u0000\u0000\u0000\u01c1\u01c2\u0001\u0000"+
		"\u0000\u0000\u01c2\u01c3\u0001\u0000\u0000\u0000\u01c3\u01c4\u0005+\u0000"+
		"\u0000\u01c4\u01c7\u0005\u0003\u0000\u0000\u01c5\u01c8\u0003\u0002\u0001"+
		"\u0000\u01c6\u01c8\u0003P(\u0000\u01c7\u01c5\u0001\u0000\u0000\u0000\u01c7"+
		"\u01c6\u0001\u0000\u0000\u0000\u01c8\u01c9\u0001\u0000\u0000\u0000\u01c9"+
		"\u01ca\u0005\u0004\u0000\u0000\u01ca\u01cc\u0001\u0000\u0000\u0000\u01cb"+
		"\u01a0\u0001\u0000\u0000\u0000\u01cb\u01a4\u0001\u0000\u0000\u0000\u01cb"+
		"\u01ad\u0001\u0000\u0000\u0000\u01cb\u01b3\u0001\u0000\u0000\u0000\u01cb"+
		"\u01b9\u0001\u0000\u0000\u0000\u01cb\u01bf\u0001\u0000\u0000\u0000\u01cc"+
		"\u01cf\u0001\u0000\u0000\u0000\u01cd\u01cb\u0001\u0000\u0000\u0000\u01cd"+
		"\u01ce\u0001\u0000\u0000\u0000\u01ceM\u0001\u0000\u0000\u0000\u01cf\u01cd"+
		"\u0001\u0000\u0000\u0000\u01d0\u01d1\u0006\'\uffff\uffff\u0000\u01d1\u01e7"+
		"\u0003\\.\u0000\u01d2\u01e7\u0003h4\u0000\u01d3\u01e7\u0003B!\u0000\u01d4"+
		"\u01d5\u0005\u0003\u0000\u0000\u01d5\u01da\u0003R)\u0000\u01d6\u01d7\u0005"+
		"\u0002\u0000\u0000\u01d7\u01d9\u0003R)\u0000\u01d8\u01d6\u0001\u0000\u0000"+
		"\u0000\u01d9\u01dc\u0001\u0000\u0000\u0000\u01da\u01d8\u0001\u0000\u0000"+
		"\u0000\u01da\u01db\u0001\u0000\u0000\u0000\u01db\u01dd\u0001\u0000\u0000"+
		"\u0000\u01dc\u01da\u0001\u0000\u0000\u0000\u01dd\u01de\u0005\u0004\u0000"+
		"\u0000\u01de\u01e7\u0001\u0000\u0000\u0000\u01df\u01e0\u0005\u0003\u0000"+
		"\u0000\u01e0\u01e1\u0003\u0002\u0001\u0000\u01e1\u01e2\u0005\u0004\u0000"+
		"\u0000\u01e2\u01e7\u0001\u0000\u0000\u0000\u01e3\u01e4\u0003V+\u0000\u01e4"+
		"\u01e5\u0003N\'\u0001\u01e5\u01e7\u0001\u0000\u0000\u0000\u01e6\u01d0"+
		"\u0001\u0000\u0000\u0000\u01e6\u01d2\u0001\u0000\u0000\u0000\u01e6\u01d3"+
		"\u0001\u0000\u0000\u0000\u01e6\u01d4\u0001\u0000\u0000\u0000\u01e6\u01df"+
		"\u0001\u0000\u0000\u0000\u01e6\u01e3\u0001\u0000\u0000\u0000\u01e7\u01ee"+
		"\u0001\u0000\u0000\u0000\u01e8\u01e9\n\u0002\u0000\u0000\u01e9\u01ea\u0003"+
		"T*\u0000\u01ea\u01eb\u0003N\'\u0003\u01eb\u01ed\u0001\u0000\u0000\u0000"+
		"\u01ec\u01e8\u0001\u0000\u0000\u0000\u01ed\u01f0\u0001\u0000\u0000\u0000"+
		"\u01ee\u01ec\u0001\u0000\u0000\u0000\u01ee\u01ef\u0001\u0000\u0000\u0000"+
		"\u01efO\u0001\u0000\u0000\u0000\u01f0\u01ee\u0001\u0000\u0000\u0000\u01f1"+
		"\u01f6\u0003R)\u0000\u01f2\u01f3\u0005\u0002\u0000\u0000\u01f3\u01f5\u0003"+
		"R)\u0000\u01f4\u01f2\u0001\u0000\u0000\u0000\u01f5\u01f8\u0001\u0000\u0000"+
		"\u0000\u01f6\u01f4\u0001\u0000\u0000\u0000\u01f6\u01f7\u0001\u0000\u0000"+
		"\u0000\u01f7Q\u0001\u0000\u0000\u0000\u01f8\u01f6\u0001\u0000\u0000\u0000"+
		"\u01f9\u01fa\u0005\u0003\u0000\u0000\u01fa\u01fb\u0003R)\u0000\u01fb\u01fc"+
		"\u0005\u0004\u0000\u0000\u01fc\u0202\u0001\u0000\u0000\u0000\u01fd\u01fe"+
		"\u0007\u0003\u0000\u0000\u01fe\u0202\u0003R)\u0000\u01ff\u0202\u0003N"+
		"\'\u0000\u0200\u0202\u0003\u0018\f\u0000\u0201\u01f9\u0001\u0000\u0000"+
		"\u0000\u0201\u01fd\u0001\u0000\u0000\u0000\u0201\u01ff\u0001\u0000\u0000"+
		"\u0000\u0201\u0200\u0001\u0000\u0000\u0000\u0202S\u0001\u0000\u0000\u0000"+
		"\u0203\u0204\u0007\u0004\u0000\u0000\u0204U\u0001\u0000\u0000\u0000\u0205"+
		"\u0206\u0007\u0005\u0000\u0000\u0206W\u0001\u0000\u0000\u0000\u0207\u020c"+
		"\u0005\'\u0000\u0000\u0208\u020c\u0005)\u0000\u0000\u0209\u020c\u0005"+
		"(\u0000\u0000\u020a\u020c\u0001\u0000\u0000\u0000\u020b\u0207\u0001\u0000"+
		"\u0000\u0000\u020b\u0208\u0001\u0000\u0000\u0000\u020b\u0209\u0001\u0000"+
		"\u0000\u0000\u020b\u020a\u0001\u0000\u0000\u0000\u020cY\u0001\u0000\u0000"+
		"\u0000\u020d\u020e\u0007\u0006\u0000\u0000\u020e[\u0001\u0000\u0000\u0000"+
		"\u020f\u0217\u0003n7\u0000\u0210\u0217\u0003^/\u0000\u0211\u0212\u0005"+
		"\u000b\u0000\u0000\u0212\u0217\u0003^/\u0000\u0213\u0217\u0003f3\u0000"+
		"\u0214\u0217\u0003`0\u0000\u0215\u0217\u0003b1\u0000\u0216\u020f\u0001"+
		"\u0000\u0000\u0000\u0216\u0210\u0001\u0000\u0000\u0000\u0216\u0211\u0001"+
		"\u0000\u0000\u0000\u0216\u0213\u0001\u0000\u0000\u0000\u0216\u0214\u0001"+
		"\u0000\u0000\u0000\u0216\u0215\u0001\u0000\u0000\u0000\u0217]\u0001\u0000"+
		"\u0000\u0000\u0218\u0219\u0005G\u0000\u0000\u0219_\u0001\u0000\u0000\u0000"+
		"\u021a\u021b\u0005\u001a\u0000\u0000\u021ba\u0001\u0000\u0000\u0000\u021c"+
		"\u021d\u0003n7\u0000\u021d\u021e\u0005\u0014\u0000\u0000\u021e\u021f\u0003"+
		"d2\u0000\u021fc\u0001\u0000\u0000\u0000\u0220\u0221\u0003n7\u0000\u0221"+
		"e\u0001\u0000\u0000\u0000\u0222\u0223\u0007\u0007\u0000\u0000\u0223g\u0001"+
		"\u0000\u0000\u0000\u0224\u0226\u0003l6\u0000\u0225\u0227\u0003j5\u0000"+
		"\u0226\u0225\u0001\u0000\u0000\u0000\u0226\u0227\u0001\u0000\u0000\u0000"+
		"\u0227i\u0001\u0000\u0000\u0000\u0228\u0229\u0005\u0015\u0000\u0000\u0229"+
		"\u022a\u0003l6\u0000\u022ak\u0001\u0000\u0000\u0000\u022b\u022c\u0003"+
		"p8\u0000\u022cm\u0001\u0000\u0000\u0000\u022d\u022e\u0005F\u0000\u0000"+
		"\u022eo\u0001\u0000\u0000\u0000\u022f\u0230\u0005E\u0000\u0000\u0230q"+
		"\u0001\u0000\u0000\u0000\u0231\u0232\u0007\b\u0000\u0000\u0232s\u0001"+
		"\u0000\u0000\u00009v|\u0083\u0089\u008d\u0091\u0096\u00b2\u00b9\u00c1"+
		"\u00cd\u00d7\u00db\u00e2\u00e6\u00e9\u00ec\u00ef\u00f2\u0102\u0108\u010d"+
		"\u0110\u011f\u0122\u012a\u0137\u013c\u0142\u014c\u0155\u015c\u0161\u0169"+
		"\u0175\u017b\u0182\u018d\u0195\u0197\u019e\u01a6\u01af\u01b5\u01bc\u01c1"+
		"\u01c7\u01cb\u01cd\u01da\u01e6\u01ee\u01f6\u0201\u020b\u0216\u0226";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}