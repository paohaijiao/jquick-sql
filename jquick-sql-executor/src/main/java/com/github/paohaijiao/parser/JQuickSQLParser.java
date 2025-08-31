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
		T__17=18, T__18=19, T__19=20, ON=21, REGEXP=22, LIKE=23, IS=24, NULL=25, 
		EXISTS=26, DESC=27, ASC=28, SELECT=29, FROM=30, WHERE=31, GROUP=32, BY=33, 
		HAVING=34, ORDER=35, LIMIT=36, OFFSET=37, AND=38, OR=39, XOR=40, NOT=41, 
		IN=42, BETWEEN=43, AS=44, WITH=45, RECURSIVE=46, ROLLUP=47, DRILLDOWN=48, 
		SLICE=49, DICE=50, PIVOT=51, JOIN=52, INNER=53, OUTER=54, LEFT=55, RIGHT=56, 
		FULL=57, CROSS=58, NATURAL=59, USE=60, KEY=61, FOR=62, UNION=63, MINUS=64, 
		INTERSECT=65, ALL=66, DISTINCT=67, TRUE=68, FALSE=69, IDENTIFIER=70, STRING_LITERAL=71, 
		DECIMAL_LITERAL=72, HEXADECIMAL_LITERAL=73, BIT_STRING=74, WS=75, COMMENT=76, 
		LINE_COMMENT=77;
	public static final int
		RULE_query = 0, RULE_selectStatement = 1, RULE_selectExpression = 2, RULE_datasetOp = 3, 
		RULE_olapOperation = 4, RULE_olapClauseItem = 5, RULE_rollupDimensions = 6, 
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
			"query", "selectStatement", "selectExpression", "datasetOp", "olapOperation", 
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
			"'+'", "'-'", "'--'", "'~'", "'>'", "'<'", "'<='", "'>='", "'!='", "'::'", 
			"'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "ON", "REGEXP", 
			"LIKE", "IS", "NULL", "EXISTS", "DESC", "ASC", "SELECT", "FROM", "WHERE", 
			"GROUP", "BY", "HAVING", "ORDER", "LIMIT", "OFFSET", "AND", "OR", "XOR", 
			"NOT", "IN", "BETWEEN", "AS", "WITH", "RECURSIVE", "ROLLUP", "DRILLDOWN", 
			"SLICE", "DICE", "PIVOT", "JOIN", "INNER", "OUTER", "LEFT", "RIGHT", 
			"FULL", "CROSS", "NATURAL", "USE", "KEY", "FOR", "UNION", "MINUS", "INTERSECT", 
			"ALL", "DISTINCT", "TRUE", "FALSE", "IDENTIFIER", "STRING_LITERAL", "DECIMAL_LITERAL", 
			"HEXADECIMAL_LITERAL", "BIT_STRING", "WS", "COMMENT", "LINE_COMMENT"
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
		public DatasetOpContext datasetOp() {
			return getRuleContext(DatasetOpContext.class,0);
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
				datasetOp();
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
	public static class DatasetOpContext extends ParserRuleContext {
		public List<SelectClauseContext> selectClause() {
			return getRuleContexts(SelectClauseContext.class);
		}
		public SelectClauseContext selectClause(int i) {
			return getRuleContext(SelectClauseContext.class,i);
		}
		public TerminalNode UNION() { return getToken(JQuickSQLParser.UNION, 0); }
		public TerminalNode MINUS() { return getToken(JQuickSQLParser.MINUS, 0); }
		public TerminalNode INTERSECT() { return getToken(JQuickSQLParser.INTERSECT, 0); }
		public DatasetOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datasetOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).enterDatasetOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JQuickSQLListener ) ((JQuickSQLListener)listener).exitDatasetOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JQuickSQLVisitor ) return ((JQuickSQLVisitor<? extends T>)visitor).visitDatasetOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DatasetOpContext datasetOp() throws RecognitionException {
		DatasetOpContext _localctx = new DatasetOpContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_datasetOp);
		try {
			setState(156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(143);
				selectClause();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				selectClause();
				setState(145);
				match(UNION);
				setState(146);
				selectClause();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(148);
				selectClause();
				setState(149);
				match(MINUS);
				setState(150);
				selectClause();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(152);
				selectClause();
				setState(153);
				match(INTERSECT);
				setState(154);
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
		public TerminalNode SELECT() { return getToken(JQuickSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public FromClauseContext fromClause() {
			return getRuleContext(FromClauseContext.class,0);
		}
		public List<OlapClauseItemContext> olapClauseItem() {
			return getRuleContexts(OlapClauseItemContext.class);
		}
		public OlapClauseItemContext olapClauseItem(int i) {
			return getRuleContext(OlapClauseItemContext.class,i);
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
		enterRule(_localctx, 8, RULE_olapOperation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(SELECT);
			setState(159);
			selectElements();
			setState(160);
			fromClause();
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4362862139015168L) != 0)) {
				{
				{
				setState(161);
				olapClauseItem();
				}
				}
				setState(166);
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
			setState(192);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROLLUP:
				_localctx = new RollupOperationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(167);
				match(ROLLUP);
				setState(168);
				match(T__2);
				setState(169);
				rollupDimensions();
				setState(170);
				match(T__3);
				}
				break;
			case DRILLDOWN:
				_localctx = new DrilldownOperationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(172);
				match(DRILLDOWN);
				setState(173);
				match(T__2);
				setState(174);
				drilldownDimensions();
				setState(175);
				match(T__3);
				}
				break;
			case SLICE:
				_localctx = new SliceOperationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(177);
				match(SLICE);
				setState(178);
				match(T__2);
				setState(179);
				sliceCondition();
				setState(180);
				match(T__3);
				}
				break;
			case DICE:
				_localctx = new DiceOperationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(182);
				match(DICE);
				setState(183);
				match(T__2);
				setState(184);
				diceConditions();
				setState(185);
				match(T__3);
				}
				break;
			case PIVOT:
				_localctx = new PivotOperationContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(187);
				match(PIVOT);
				setState(188);
				match(T__2);
				setState(189);
				pivotSpec();
				setState(190);
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
			setState(194);
			expression();
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(195);
				match(T__1);
				setState(196);
				expression();
				}
				}
				setState(201);
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
			setState(202);
			expression();
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(203);
				match(T__1);
				setState(204);
				expression();
				}
				}
				setState(209);
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
			setState(210);
			uid();
			setState(211);
			match(T__4);
			setState(212);
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
			setState(214);
			diceCondition();
			setState(219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(215);
				match(T__1);
				setState(216);
				diceCondition();
				}
				}
				setState(221);
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
			setState(222);
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
		public UidContext aggregator;
		public List<UidContext> uid() {
			return getRuleContexts(UidContext.class);
		}
		public UidContext uid(int i) {
			return getRuleContext(UidContext.class,i);
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
			setState(224);
			((PivotSpecContext)_localctx).pivotColumn = uid();
			setState(225);
			match(T__1);
			setState(226);
			((PivotSpecContext)_localctx).valueColumn = uid();
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(227);
				match(T__1);
				setState(228);
				((PivotSpecContext)_localctx).aggregator = uid();
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
			setState(231);
			match(SELECT);
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALL || _la==DISTINCT) {
				{
				setState(232);
				selectSpec();
				}
			}

			setState(235);
			selectElements();
			setState(236);
			fromClause();
			setState(240);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(237);
					joinClause();
					}
					} 
				}
				setState(242);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(244);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(243);
				whereClause();
				}
				break;
			}
			setState(247);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(246);
				groupByClause();
				}
				break;
			}
			setState(250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(249);
				havingClause();
				}
				break;
			}
			setState(253);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(252);
				orderByClause();
				}
				break;
			}
			setState(256);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(255);
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
			setState(258);
			match(WHERE);
			setState(259);
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
			setState(261);
			match(GROUP);
			setState(262);
			match(BY);
			setState(263);
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
			setState(265);
			match(HAVING);
			setState(266);
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
			setState(268);
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
			setState(272);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
				{
				setState(270);
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
				setState(271);
				selectElement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(274);
				match(T__1);
				setState(275);
				selectElement();
				}
				}
				setState(280);
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
			setState(281);
			expression();
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS || _la==IDENTIFIER) {
				{
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(282);
					match(AS);
					}
				}

				setState(285);
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
			setState(288);
			match(FROM);
			setState(289);
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
			setState(291);
			joinType();
			setState(292);
			match(JOIN);
			setState(293);
			tableNameItem();
			setState(299);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(294);
				match(ON);
				setState(295);
				fullColumnName();
				setState(296);
				match(T__4);
				setState(297);
				fullColumnName();
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
			setState(314);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				tableNameSpec();
				setState(306);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(303);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==AS) {
						{
						setState(302);
						match(AS);
						}
					}

					setState(305);
					uid();
					}
					break;
				}
				}
				break;
			case T__2:
				enterOuterAlt(_localctx, 2);
				{
				setState(308);
				match(T__2);
				setState(309);
				selectExpression();
				setState(310);
				match(T__3);
				setState(311);
				match(AS);
				setState(312);
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
			setState(316);
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
			setState(318);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1125899906842624000L) != 0)) ) {
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
			setState(320);
			match(ORDER);
			setState(321);
			match(BY);
			setState(322);
			orderByExpression();
			setState(327);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(323);
					match(T__1);
					setState(324);
					orderByExpression();
					}
					} 
				}
				setState(329);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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
			setState(330);
			expression();
			setState(332);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(331);
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
			setState(338);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(334);
				match(LIMIT);
				setState(335);
				limitOnly();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(336);
				match(LIMIT);
				setState(337);
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
			setState(340);
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
			setState(342);
			((LimitWithOffsetContext)_localctx).offset = expression();
			setState(343);
			match(T__1);
			setState(344);
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
			setState(369);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(346);
				uid();
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(347);
					columnNames();
					}
				}

				setState(350);
				match(AS);
				setState(351);
				match(T__2);
				setState(352);
				selectStatement();
				setState(353);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(355);
				uid();
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(356);
					columnNames();
					}
				}

				setState(359);
				match(AS);
				setState(360);
				match(T__2);
				setState(361);
				initialQuery();
				setState(362);
				match(UNION);
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ALL) {
					{
					setState(363);
					match(ALL);
					}
				}

				setState(366);
				recursivePart();
				setState(367);
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
			setState(371);
			match(T__2);
			setState(372);
			uid();
			setState(377);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(373);
				match(T__1);
				setState(374);
				uid();
				}
				}
				setState(379);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(380);
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
			setState(382);
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
			setState(384);
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
			setState(386);
			uid();
			setState(387);
			match(T__2);
			setState(389);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2199593692360L) != 0) || ((((_la - 68)) & ~0x3f) == 0 && ((1L << (_la - 68)) & 31L) != 0)) {
				{
				setState(388);
				arg();
				}
			}

			setState(391);
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
			setState(395);
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
				setState(393);
				functionArgs();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(394);
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
			setState(397);
			functionArg();
			setState(402);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(398);
				match(T__1);
				setState(399);
				functionArg();
				}
				}
				setState(404);
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
			setState(405);
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
			setState(413);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(408);
				match(T__2);
				setState(409);
				filterCondition(0);
				setState(410);
				match(T__3);
				}
				break;
			case 2:
				{
				setState(412);
				predicate(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(423);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(421);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
					case 1:
						{
						_localctx = new FilterConditionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_filterCondition);
						setState(415);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						{
						setState(416);
						match(AND);
						setState(417);
						filterCondition(0);
						}
						}
						break;
					case 2:
						{
						_localctx = new FilterConditionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_filterCondition);
						setState(418);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						{
						setState(419);
						match(OR);
						setState(420);
						filterCondition(0);
						}
						}
						break;
					}
					} 
				}
				setState(425);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
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
			setState(430);
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

				setState(427);
				expressionAtom(0);
				}
				break;
			case EXISTS:
				{
				_localctx = new ExisitsPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(428);
				match(EXISTS);
				setState(429);
				expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(477);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(475);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryComparisonPredicateContext(new PredicateContext(_parentctx, _parentState));
						((BinaryComparisonPredicateContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(432);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(433);
						comparisonOperator();
						setState(434);
						((BinaryComparisonPredicateContext)_localctx).right = predicate(7);
						}
						break;
					case 2:
						{
						_localctx = new BetweenPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(436);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(438);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(437);
							match(NOT);
							}
						}

						setState(440);
						match(BETWEEN);
						setState(441);
						predicate(0);
						setState(442);
						match(AND);
						setState(443);
						predicate(6);
						}
						break;
					case 3:
						{
						_localctx = new LikePredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(445);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(447);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(446);
							match(NOT);
							}
						}

						setState(449);
						match(LIKE);
						setState(450);
						predicate(4);
						}
						break;
					case 4:
						{
						_localctx = new RegexpPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(451);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(453);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(452);
							match(NOT);
							}
						}

						setState(455);
						((RegexpPredicateContext)_localctx).regex = match(REGEXP);
						setState(456);
						predicate(3);
						}
						break;
					case 5:
						{
						_localctx = new IsNullPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(457);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(458);
						match(IS);
						setState(460);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(459);
							match(NOT);
							}
						}

						setState(462);
						match(NULL);
						}
						break;
					case 6:
						{
						_localctx = new InPredicateContext(new PredicateContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(463);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(465);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(464);
							match(NOT);
							}
						}

						setState(467);
						match(IN);
						setState(468);
						match(T__2);
						setState(471);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
						case 1:
							{
							setState(469);
							selectStatement();
							}
							break;
						case 2:
							{
							setState(470);
							expressions();
							}
							break;
						}
						setState(473);
						match(T__3);
						}
						break;
					}
					} 
				}
				setState(479);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
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
			setState(502);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				_localctx = new ConstantExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(481);
				constant();
				}
				break;
			case 2:
				{
				_localctx = new FullColumnNameExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(482);
				fullColumnName();
				}
				break;
			case 3:
				{
				_localctx = new FunctionCallExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(483);
				functionCall();
				}
				break;
			case 4:
				{
				_localctx = new NestedExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(484);
				match(T__2);
				setState(485);
				expression();
				setState(490);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(486);
					match(T__1);
					setState(487);
					expression();
					}
					}
					setState(492);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(493);
				match(T__3);
				}
				break;
			case 5:
				{
				_localctx = new SubqueryExperssionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(495);
				match(T__2);
				setState(496);
				selectStatement();
				setState(497);
				match(T__3);
				}
				break;
			case 6:
				{
				_localctx = new UnaryExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(499);
				unaryOperator();
				setState(500);
				expressionAtom(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(510);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MathExpressionAtomContext(new ExpressionAtomContext(_parentctx, _parentState));
					((MathExpressionAtomContext)_localctx).left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expressionAtom);
					setState(504);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(505);
					mathOperator();
					setState(506);
					((MathExpressionAtomContext)_localctx).right = expressionAtom(3);
					}
					} 
				}
				setState(512);
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
			setState(513);
			expression();
			setState(518);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(514);
					match(T__1);
					setState(515);
					expression();
					}
					} 
				}
				setState(520);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
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
			setState(529);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				_localctx = new ParenExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(521);
				match(T__2);
				setState(522);
				expression();
				setState(523);
				match(T__3);
				}
				break;
			case 2:
				_localctx = new NotExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(525);
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
				setState(526);
				expression();
				}
				break;
			case 3:
				_localctx = new PredicateExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(527);
				expressionAtom(0);
				}
				break;
			case 4:
				_localctx = new SelectResultContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(528);
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
			setState(531);
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
			setState(533);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2199023266944L) != 0)) ) {
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
			setState(539);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(535);
				match(AND);
				}
				break;
			case XOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(536);
				match(XOR);
				}
				break;
			case OR:
				enterOuterAlt(_localctx, 3);
				{
				setState(537);
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
			setState(541);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 507936L) != 0)) ) {
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
			setState(550);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(543);
				stringLiteral();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(544);
				decimal_literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(545);
				match(T__10);
				setState(546);
				decimal_literal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(547);
				booleanLiteral();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(548);
				null_literal();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(549);
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
			setState(552);
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
			setState(554);
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
			setState(556);
			stringLiteral();
			setState(557);
			match(T__18);
			setState(558);
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
			setState(560);
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
			setState(562);
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
			setState(564);
			uid();
			setState(566);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(565);
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
			setState(568);
			match(T__19);
			setState(569);
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
			setState(571);
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
			setState(573);
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
			setState(575);
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
			setState(577);
			_la = _input.LA(1);
			if ( !(((((_la - 29)) & ~0x3f) == 0 && ((1L << (_la - 29)) & 429496705023L) != 0)) ) {
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
		"\u0004\u0001M\u0244\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0003\u0003\u009d\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0005\u0004\u00a3\b\u0004\n\u0004\f\u0004\u00a6\t\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0003\u0005\u00c1\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005"+
		"\u0006\u00c6\b\u0006\n\u0006\f\u0006\u00c9\t\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0005\u0007\u00ce\b\u0007\n\u0007\f\u0007\u00d1\t\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u00da\b\t\n"+
		"\t\f\t\u00dd\t\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\u00e6\b\u000b\u0001\f\u0001\f\u0003\f\u00ea"+
		"\b\f\u0001\f\u0001\f\u0001\f\u0005\f\u00ef\b\f\n\f\f\f\u00f2\t\f\u0001"+
		"\f\u0003\f\u00f5\b\f\u0001\f\u0003\f\u00f8\b\f\u0001\f\u0003\f\u00fb\b"+
		"\f\u0001\f\u0003\f\u00fe\b\f\u0001\f\u0003\f\u0101\b\f\u0001\r\u0001\r"+
		"\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0003"+
		"\u0011\u0111\b\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u0115\b\u0011"+
		"\n\u0011\f\u0011\u0118\t\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u011c"+
		"\b\u0012\u0001\u0012\u0003\u0012\u011f\b\u0012\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u012c\b\u0014\u0001\u0015"+
		"\u0001\u0015\u0003\u0015\u0130\b\u0015\u0001\u0015\u0003\u0015\u0133\b"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0003\u0015\u013b\b\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0005"+
		"\u0018\u0146\b\u0018\n\u0018\f\u0018\u0149\t\u0018\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u014d\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0003\u001a\u0153\b\u001a\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0003\u001d\u015d\b\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0003\u001d\u0166\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0003\u001d\u016d\b\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0003\u001d\u0172\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0005\u001e\u0178\b\u001e\n\u001e\f\u001e\u017b\t\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001"+
		"!\u0001!\u0003!\u0186\b!\u0001!\u0001!\u0001\"\u0001\"\u0003\"\u018c\b"+
		"\"\u0001#\u0001#\u0001#\u0005#\u0191\b#\n#\f#\u0194\t#\u0001$\u0001$\u0001"+
		"%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003%\u019e\b%\u0001%\u0001%\u0001"+
		"%\u0001%\u0001%\u0001%\u0005%\u01a6\b%\n%\f%\u01a9\t%\u0001&\u0001&\u0001"+
		"&\u0001&\u0003&\u01af\b&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003"+
		"&\u01b7\b&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u01c0"+
		"\b&\u0001&\u0001&\u0001&\u0001&\u0003&\u01c6\b&\u0001&\u0001&\u0001&\u0001"+
		"&\u0001&\u0003&\u01cd\b&\u0001&\u0001&\u0001&\u0003&\u01d2\b&\u0001&\u0001"+
		"&\u0001&\u0001&\u0003&\u01d8\b&\u0001&\u0001&\u0005&\u01dc\b&\n&\f&\u01df"+
		"\t&\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0005"+
		"\'\u01e9\b\'\n\'\f\'\u01ec\t\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'"+
		"\u0001\'\u0001\'\u0001\'\u0001\'\u0003\'\u01f7\b\'\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0005\'\u01fd\b\'\n\'\f\'\u0200\t\'\u0001(\u0001(\u0001(\u0005"+
		"(\u0205\b(\n(\f(\u0208\t(\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001"+
		")\u0001)\u0003)\u0212\b)\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0001"+
		",\u0001,\u0003,\u021c\b,\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001"+
		".\u0001.\u0001.\u0003.\u0227\b.\u0001/\u0001/\u00010\u00010\u00011\u0001"+
		"1\u00011\u00011\u00012\u00012\u00013\u00013\u00014\u00014\u00034\u0237"+
		"\b4\u00015\u00015\u00015\u00016\u00016\u00017\u00017\u00018\u00018\u0001"+
		"9\u00019\u00019\u0000\u0003JLN:\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPR"+
		"TVXZ\\^`bdfhjlnpr\u0000\t\u0001\u0000BC\u0002\u0000557;\u0001\u0000\u001b"+
		"\u001c\u0002\u0000\u0007\u0007))\u0002\u0000\u0006\u0006\b\f\u0004\u0000"+
		"\u0007\u0007\n\u000b\r\r))\u0002\u0000\u0005\u0005\u000e\u0012\u0001\u0000"+
		"DE\u0003\u0000\u001d),>BC\u0258\u0000t\u0001\u0000\u0000\u0000\u0002\u0089"+
		"\u0001\u0000\u0000\u0000\u0004\u008d\u0001\u0000\u0000\u0000\u0006\u009c"+
		"\u0001\u0000\u0000\u0000\b\u009e\u0001\u0000\u0000\u0000\n\u00c0\u0001"+
		"\u0000\u0000\u0000\f\u00c2\u0001\u0000\u0000\u0000\u000e\u00ca\u0001\u0000"+
		"\u0000\u0000\u0010\u00d2\u0001\u0000\u0000\u0000\u0012\u00d6\u0001\u0000"+
		"\u0000\u0000\u0014\u00de\u0001\u0000\u0000\u0000\u0016\u00e0\u0001\u0000"+
		"\u0000\u0000\u0018\u00e7\u0001\u0000\u0000\u0000\u001a\u0102\u0001\u0000"+
		"\u0000\u0000\u001c\u0105\u0001\u0000\u0000\u0000\u001e\u0109\u0001\u0000"+
		"\u0000\u0000 \u010c\u0001\u0000\u0000\u0000\"\u0110\u0001\u0000\u0000"+
		"\u0000$\u0119\u0001\u0000\u0000\u0000&\u0120\u0001\u0000\u0000\u0000("+
		"\u0123\u0001\u0000\u0000\u0000*\u013a\u0001\u0000\u0000\u0000,\u013c\u0001"+
		"\u0000\u0000\u0000.\u013e\u0001\u0000\u0000\u00000\u0140\u0001\u0000\u0000"+
		"\u00002\u014a\u0001\u0000\u0000\u00004\u0152\u0001\u0000\u0000\u00006"+
		"\u0154\u0001\u0000\u0000\u00008\u0156\u0001\u0000\u0000\u0000:\u0171\u0001"+
		"\u0000\u0000\u0000<\u0173\u0001\u0000\u0000\u0000>\u017e\u0001\u0000\u0000"+
		"\u0000@\u0180\u0001\u0000\u0000\u0000B\u0182\u0001\u0000\u0000\u0000D"+
		"\u018b\u0001\u0000\u0000\u0000F\u018d\u0001\u0000\u0000\u0000H\u0195\u0001"+
		"\u0000\u0000\u0000J\u019d\u0001\u0000\u0000\u0000L\u01ae\u0001\u0000\u0000"+
		"\u0000N\u01f6\u0001\u0000\u0000\u0000P\u0201\u0001\u0000\u0000\u0000R"+
		"\u0211\u0001\u0000\u0000\u0000T\u0213\u0001\u0000\u0000\u0000V\u0215\u0001"+
		"\u0000\u0000\u0000X\u021b\u0001\u0000\u0000\u0000Z\u021d\u0001\u0000\u0000"+
		"\u0000\\\u0226\u0001\u0000\u0000\u0000^\u0228\u0001\u0000\u0000\u0000"+
		"`\u022a\u0001\u0000\u0000\u0000b\u022c\u0001\u0000\u0000\u0000d\u0230"+
		"\u0001\u0000\u0000\u0000f\u0232\u0001\u0000\u0000\u0000h\u0234\u0001\u0000"+
		"\u0000\u0000j\u0238\u0001\u0000\u0000\u0000l\u023b\u0001\u0000\u0000\u0000"+
		"n\u023d\u0001\u0000\u0000\u0000p\u023f\u0001\u0000\u0000\u0000r\u0241"+
		"\u0001\u0000\u0000\u0000tv\u0003\u0002\u0001\u0000uw\u0005\u0001\u0000"+
		"\u0000vu\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wx\u0001\u0000"+
		"\u0000\u0000xy\u0005\u0000\u0000\u0001y\u0001\u0001\u0000\u0000\u0000"+
		"z|\u0005-\u0000\u0000{}\u0005.\u0000\u0000|{\u0001\u0000\u0000\u0000|"+
		"}\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u0083\u0003:\u001d"+
		"\u0000\u007f\u0080\u0005\u0002\u0000\u0000\u0080\u0082\u0003:\u001d\u0000"+
		"\u0081\u007f\u0001\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000"+
		"\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000"+
		"\u0084\u0086\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0003\u0004\u0002\u0000\u0087\u008a\u0001\u0000\u0000\u0000"+
		"\u0088\u008a\u0003\u0004\u0002\u0000\u0089z\u0001\u0000\u0000\u0000\u0089"+
		"\u0088\u0001\u0000\u0000\u0000\u008a\u0003\u0001\u0000\u0000\u0000\u008b"+
		"\u008e\u0003\u0006\u0003\u0000\u008c\u008e\u0003\b\u0004\u0000\u008d\u008b"+
		"\u0001\u0000\u0000\u0000\u008d\u008c\u0001\u0000\u0000\u0000\u008e\u0005"+
		"\u0001\u0000\u0000\u0000\u008f\u009d\u0003\u0018\f\u0000\u0090\u0091\u0003"+
		"\u0018\f\u0000\u0091\u0092\u0005?\u0000\u0000\u0092\u0093\u0003\u0018"+
		"\f\u0000\u0093\u009d\u0001\u0000\u0000\u0000\u0094\u0095\u0003\u0018\f"+
		"\u0000\u0095\u0096\u0005@\u0000\u0000\u0096\u0097\u0003\u0018\f\u0000"+
		"\u0097\u009d\u0001\u0000\u0000\u0000\u0098\u0099\u0003\u0018\f\u0000\u0099"+
		"\u009a\u0005A\u0000\u0000\u009a\u009b\u0003\u0018\f\u0000\u009b\u009d"+
		"\u0001\u0000\u0000\u0000\u009c\u008f\u0001\u0000\u0000\u0000\u009c\u0090"+
		"\u0001\u0000\u0000\u0000\u009c\u0094\u0001\u0000\u0000\u0000\u009c\u0098"+
		"\u0001\u0000\u0000\u0000\u009d\u0007\u0001\u0000\u0000\u0000\u009e\u009f"+
		"\u0005\u001d\u0000\u0000\u009f\u00a0\u0003\"\u0011\u0000\u00a0\u00a4\u0003"+
		"&\u0013\u0000\u00a1\u00a3\u0003\n\u0005\u0000\u00a2\u00a1\u0001\u0000"+
		"\u0000\u0000\u00a3\u00a6\u0001\u0000\u0000\u0000\u00a4\u00a2\u0001\u0000"+
		"\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\t\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005/\u0000\u0000"+
		"\u00a8\u00a9\u0005\u0003\u0000\u0000\u00a9\u00aa\u0003\f\u0006\u0000\u00aa"+
		"\u00ab\u0005\u0004\u0000\u0000\u00ab\u00c1\u0001\u0000\u0000\u0000\u00ac"+
		"\u00ad\u00050\u0000\u0000\u00ad\u00ae\u0005\u0003\u0000\u0000\u00ae\u00af"+
		"\u0003\u000e\u0007\u0000\u00af\u00b0\u0005\u0004\u0000\u0000\u00b0\u00c1"+
		"\u0001\u0000\u0000\u0000\u00b1\u00b2\u00051\u0000\u0000\u00b2\u00b3\u0005"+
		"\u0003\u0000\u0000\u00b3\u00b4\u0003\u0010\b\u0000\u00b4\u00b5\u0005\u0004"+
		"\u0000\u0000\u00b5\u00c1\u0001\u0000\u0000\u0000\u00b6\u00b7\u00052\u0000"+
		"\u0000\u00b7\u00b8\u0005\u0003\u0000\u0000\u00b8\u00b9\u0003\u0012\t\u0000"+
		"\u00b9\u00ba\u0005\u0004\u0000\u0000\u00ba\u00c1\u0001\u0000\u0000\u0000"+
		"\u00bb\u00bc\u00053\u0000\u0000\u00bc\u00bd\u0005\u0003\u0000\u0000\u00bd"+
		"\u00be\u0003\u0016\u000b\u0000\u00be\u00bf\u0005\u0004\u0000\u0000\u00bf"+
		"\u00c1\u0001\u0000\u0000\u0000\u00c0\u00a7\u0001\u0000\u0000\u0000\u00c0"+
		"\u00ac\u0001\u0000\u0000\u0000\u00c0\u00b1\u0001\u0000\u0000\u0000\u00c0"+
		"\u00b6\u0001\u0000\u0000\u0000\u00c0\u00bb\u0001\u0000\u0000\u0000\u00c1"+
		"\u000b\u0001\u0000\u0000\u0000\u00c2\u00c7\u0003R)\u0000\u00c3\u00c4\u0005"+
		"\u0002\u0000\u0000\u00c4\u00c6\u0003R)\u0000\u00c5\u00c3\u0001\u0000\u0000"+
		"\u0000\u00c6\u00c9\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000"+
		"\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8\r\u0001\u0000\u0000\u0000"+
		"\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cf\u0003R)\u0000\u00cb\u00cc"+
		"\u0005\u0002\u0000\u0000\u00cc\u00ce\u0003R)\u0000\u00cd\u00cb\u0001\u0000"+
		"\u0000\u0000\u00ce\u00d1\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000"+
		"\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u000f\u0001\u0000"+
		"\u0000\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d2\u00d3\u0003l6\u0000"+
		"\u00d3\u00d4\u0005\u0005\u0000\u0000\u00d4\u00d5\u0003R)\u0000\u00d5\u0011"+
		"\u0001\u0000\u0000\u0000\u00d6\u00db\u0003\u0014\n\u0000\u00d7\u00d8\u0005"+
		"\u0002\u0000\u0000\u00d8\u00da\u0003\u0014\n\u0000\u00d9\u00d7\u0001\u0000"+
		"\u0000\u0000\u00da\u00dd\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000"+
		"\u0000\u0000\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc\u0013\u0001\u0000"+
		"\u0000\u0000\u00dd\u00db\u0001\u0000\u0000\u0000\u00de\u00df\u0003L&\u0000"+
		"\u00df\u0015\u0001\u0000\u0000\u0000\u00e0\u00e1\u0003l6\u0000\u00e1\u00e2"+
		"\u0005\u0002\u0000\u0000\u00e2\u00e5\u0003l6\u0000\u00e3\u00e4\u0005\u0002"+
		"\u0000\u0000\u00e4\u00e6\u0003l6\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000"+
		"\u00e5\u00e6\u0001\u0000\u0000\u0000\u00e6\u0017\u0001\u0000\u0000\u0000"+
		"\u00e7\u00e9\u0005\u001d\u0000\u0000\u00e8\u00ea\u0003 \u0010\u0000\u00e9"+
		"\u00e8\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea"+
		"\u00eb\u0001\u0000\u0000\u0000\u00eb\u00ec\u0003\"\u0011\u0000\u00ec\u00f0"+
		"\u0003&\u0013\u0000\u00ed\u00ef\u0003(\u0014\u0000\u00ee\u00ed\u0001\u0000"+
		"\u0000\u0000\u00ef\u00f2\u0001\u0000\u0000\u0000\u00f0\u00ee\u0001\u0000"+
		"\u0000\u0000\u00f0\u00f1\u0001\u0000\u0000\u0000\u00f1\u00f4\u0001\u0000"+
		"\u0000\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000\u00f3\u00f5\u0003\u001a"+
		"\r\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000"+
		"\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000\u00f6\u00f8\u0003\u001c\u000e"+
		"\u0000\u00f7\u00f6\u0001\u0000\u0000\u0000\u00f7\u00f8\u0001\u0000\u0000"+
		"\u0000\u00f8\u00fa\u0001\u0000\u0000\u0000\u00f9\u00fb\u0003\u001e\u000f"+
		"\u0000\u00fa\u00f9\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000"+
		"\u0000\u00fb\u00fd\u0001\u0000\u0000\u0000\u00fc\u00fe\u00030\u0018\u0000"+
		"\u00fd\u00fc\u0001\u0000\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000"+
		"\u00fe\u0100\u0001\u0000\u0000\u0000\u00ff\u0101\u00034\u001a\u0000\u0100"+
		"\u00ff\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000\u0000\u0000\u0101"+
		"\u0019\u0001\u0000\u0000\u0000\u0102\u0103\u0005\u001f\u0000\u0000\u0103"+
		"\u0104\u0003J%\u0000\u0104\u001b\u0001\u0000\u0000\u0000\u0105\u0106\u0005"+
		" \u0000\u0000\u0106\u0107\u0005!\u0000\u0000\u0107\u0108\u0003P(\u0000"+
		"\u0108\u001d\u0001\u0000\u0000\u0000\u0109\u010a\u0005\"\u0000\u0000\u010a"+
		"\u010b\u0003J%\u0000\u010b\u001f\u0001\u0000\u0000\u0000\u010c\u010d\u0007"+
		"\u0000\u0000\u0000\u010d!\u0001\u0000\u0000\u0000\u010e\u0111\u0005\u0006"+
		"\u0000\u0000\u010f\u0111\u0003$\u0012\u0000\u0110\u010e\u0001\u0000\u0000"+
		"\u0000\u0110\u010f\u0001\u0000\u0000\u0000\u0111\u0116\u0001\u0000\u0000"+
		"\u0000\u0112\u0113\u0005\u0002\u0000\u0000\u0113\u0115\u0003$\u0012\u0000"+
		"\u0114\u0112\u0001\u0000\u0000\u0000\u0115\u0118\u0001\u0000\u0000\u0000"+
		"\u0116\u0114\u0001\u0000\u0000\u0000\u0116\u0117\u0001\u0000\u0000\u0000"+
		"\u0117#\u0001\u0000\u0000\u0000\u0118\u0116\u0001\u0000\u0000\u0000\u0119"+
		"\u011e\u0003R)\u0000\u011a\u011c\u0005,\u0000\u0000\u011b\u011a\u0001"+
		"\u0000\u0000\u0000\u011b\u011c\u0001\u0000\u0000\u0000\u011c\u011d\u0001"+
		"\u0000\u0000\u0000\u011d\u011f\u0003l6\u0000\u011e\u011b\u0001\u0000\u0000"+
		"\u0000\u011e\u011f\u0001\u0000\u0000\u0000\u011f%\u0001\u0000\u0000\u0000"+
		"\u0120\u0121\u0005\u001e\u0000\u0000\u0121\u0122\u0003*\u0015\u0000\u0122"+
		"\'\u0001\u0000\u0000\u0000\u0123\u0124\u0003.\u0017\u0000\u0124\u0125"+
		"\u00054\u0000\u0000\u0125\u012b\u0003*\u0015\u0000\u0126\u0127\u0005\u0015"+
		"\u0000\u0000\u0127\u0128\u0003h4\u0000\u0128\u0129\u0005\u0005\u0000\u0000"+
		"\u0129\u012a\u0003h4\u0000\u012a\u012c\u0001\u0000\u0000\u0000\u012b\u0126"+
		"\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c)\u0001"+
		"\u0000\u0000\u0000\u012d\u0132\u0003,\u0016\u0000\u012e\u0130\u0005,\u0000"+
		"\u0000\u012f\u012e\u0001\u0000\u0000\u0000\u012f\u0130\u0001\u0000\u0000"+
		"\u0000\u0130\u0131\u0001\u0000\u0000\u0000\u0131\u0133\u0003l6\u0000\u0132"+
		"\u012f\u0001\u0000\u0000\u0000\u0132\u0133\u0001\u0000\u0000\u0000\u0133"+
		"\u013b\u0001\u0000\u0000\u0000\u0134\u0135\u0005\u0003\u0000\u0000\u0135"+
		"\u0136\u0003\u0004\u0002\u0000\u0136\u0137\u0005\u0004\u0000\u0000\u0137"+
		"\u0138\u0005,\u0000\u0000\u0138\u0139\u0003l6\u0000\u0139\u013b\u0001"+
		"\u0000\u0000\u0000\u013a\u012d\u0001\u0000\u0000\u0000\u013a\u0134\u0001"+
		"\u0000\u0000\u0000\u013b+\u0001\u0000\u0000\u0000\u013c\u013d\u0005F\u0000"+
		"\u0000\u013d-\u0001\u0000\u0000\u0000\u013e\u013f\u0007\u0001\u0000\u0000"+
		"\u013f/\u0001\u0000\u0000\u0000\u0140\u0141\u0005#\u0000\u0000\u0141\u0142"+
		"\u0005!\u0000\u0000\u0142\u0147\u00032\u0019\u0000\u0143\u0144\u0005\u0002"+
		"\u0000\u0000\u0144\u0146\u00032\u0019\u0000\u0145\u0143\u0001\u0000\u0000"+
		"\u0000\u0146\u0149\u0001\u0000\u0000\u0000\u0147\u0145\u0001\u0000\u0000"+
		"\u0000\u0147\u0148\u0001\u0000\u0000\u0000\u01481\u0001\u0000\u0000\u0000"+
		"\u0149\u0147\u0001\u0000\u0000\u0000\u014a\u014c\u0003R)\u0000\u014b\u014d"+
		"\u0007\u0002\u0000\u0000\u014c\u014b\u0001\u0000\u0000\u0000\u014c\u014d"+
		"\u0001\u0000\u0000\u0000\u014d3\u0001\u0000\u0000\u0000\u014e\u014f\u0005"+
		"$\u0000\u0000\u014f\u0153\u00036\u001b\u0000\u0150\u0151\u0005$\u0000"+
		"\u0000\u0151\u0153\u00038\u001c\u0000\u0152\u014e\u0001\u0000\u0000\u0000"+
		"\u0152\u0150\u0001\u0000\u0000\u0000\u01535\u0001\u0000\u0000\u0000\u0154"+
		"\u0155\u0003R)\u0000\u01557\u0001\u0000\u0000\u0000\u0156\u0157\u0003"+
		"R)\u0000\u0157\u0158\u0005\u0002\u0000\u0000\u0158\u0159\u0003R)\u0000"+
		"\u01599\u0001\u0000\u0000\u0000\u015a\u015c\u0003l6\u0000\u015b\u015d"+
		"\u0003<\u001e\u0000\u015c\u015b\u0001\u0000\u0000\u0000\u015c\u015d\u0001"+
		"\u0000\u0000\u0000\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u015f\u0005"+
		",\u0000\u0000\u015f\u0160\u0005\u0003\u0000\u0000\u0160\u0161\u0003\u0002"+
		"\u0001\u0000\u0161\u0162\u0005\u0004\u0000\u0000\u0162\u0172\u0001\u0000"+
		"\u0000\u0000\u0163\u0165\u0003l6\u0000\u0164\u0166\u0003<\u001e\u0000"+
		"\u0165\u0164\u0001\u0000\u0000\u0000\u0165\u0166\u0001\u0000\u0000\u0000"+
		"\u0166\u0167\u0001\u0000\u0000\u0000\u0167\u0168\u0005,\u0000\u0000\u0168"+
		"\u0169\u0005\u0003\u0000\u0000\u0169\u016a\u0003>\u001f\u0000\u016a\u016c"+
		"\u0005?\u0000\u0000\u016b\u016d\u0005B\u0000\u0000\u016c\u016b\u0001\u0000"+
		"\u0000\u0000\u016c\u016d\u0001\u0000\u0000\u0000\u016d\u016e\u0001\u0000"+
		"\u0000\u0000\u016e\u016f\u0003@ \u0000\u016f\u0170\u0005\u0004\u0000\u0000"+
		"\u0170\u0172\u0001\u0000\u0000\u0000\u0171\u015a\u0001\u0000\u0000\u0000"+
		"\u0171\u0163\u0001\u0000\u0000\u0000\u0172;\u0001\u0000\u0000\u0000\u0173"+
		"\u0174\u0005\u0003\u0000\u0000\u0174\u0179\u0003l6\u0000\u0175\u0176\u0005"+
		"\u0002\u0000\u0000\u0176\u0178\u0003l6\u0000\u0177\u0175\u0001\u0000\u0000"+
		"\u0000\u0178\u017b\u0001\u0000\u0000\u0000\u0179\u0177\u0001\u0000\u0000"+
		"\u0000\u0179\u017a\u0001\u0000\u0000\u0000\u017a\u017c\u0001\u0000\u0000"+
		"\u0000\u017b\u0179\u0001\u0000\u0000\u0000\u017c\u017d\u0005\u0004\u0000"+
		"\u0000\u017d=\u0001\u0000\u0000\u0000\u017e\u017f\u0003\u0002\u0001\u0000"+
		"\u017f?\u0001\u0000\u0000\u0000\u0180\u0181\u0003\u0002\u0001\u0000\u0181"+
		"A\u0001\u0000\u0000\u0000\u0182\u0183\u0003l6\u0000\u0183\u0185\u0005"+
		"\u0003\u0000\u0000\u0184\u0186\u0003D\"\u0000\u0185\u0184\u0001\u0000"+
		"\u0000\u0000\u0185\u0186\u0001\u0000\u0000\u0000\u0186\u0187\u0001\u0000"+
		"\u0000\u0000\u0187\u0188\u0005\u0004\u0000\u0000\u0188C\u0001\u0000\u0000"+
		"\u0000\u0189\u018c\u0003F#\u0000\u018a\u018c\u0005\u0006\u0000\u0000\u018b"+
		"\u0189\u0001\u0000\u0000\u0000\u018b\u018a\u0001\u0000\u0000\u0000\u018c"+
		"E\u0001\u0000\u0000\u0000\u018d\u0192\u0003H$\u0000\u018e\u018f\u0005"+
		"\u0002\u0000\u0000\u018f\u0191\u0003H$\u0000\u0190\u018e\u0001\u0000\u0000"+
		"\u0000\u0191\u0194\u0001\u0000\u0000\u0000\u0192\u0190\u0001\u0000\u0000"+
		"\u0000\u0192\u0193\u0001\u0000\u0000\u0000\u0193G\u0001\u0000\u0000\u0000"+
		"\u0194\u0192\u0001\u0000\u0000\u0000\u0195\u0196\u0003R)\u0000\u0196I"+
		"\u0001\u0000\u0000\u0000\u0197\u0198\u0006%\uffff\uffff\u0000\u0198\u0199"+
		"\u0005\u0003\u0000\u0000\u0199\u019a\u0003J%\u0000\u019a\u019b\u0005\u0004"+
		"\u0000\u0000\u019b\u019e\u0001\u0000\u0000\u0000\u019c\u019e\u0003L&\u0000"+
		"\u019d\u0197\u0001\u0000\u0000\u0000\u019d\u019c\u0001\u0000\u0000\u0000"+
		"\u019e\u01a7\u0001\u0000\u0000\u0000\u019f\u01a0\n\u0004\u0000\u0000\u01a0"+
		"\u01a1\u0005&\u0000\u0000\u01a1\u01a6\u0003J%\u0000\u01a2\u01a3\n\u0003"+
		"\u0000\u0000\u01a3\u01a4\u0005\'\u0000\u0000\u01a4\u01a6\u0003J%\u0000"+
		"\u01a5\u019f\u0001\u0000\u0000\u0000\u01a5\u01a2\u0001\u0000\u0000\u0000"+
		"\u01a6\u01a9\u0001\u0000\u0000\u0000\u01a7\u01a5\u0001\u0000\u0000\u0000"+
		"\u01a7\u01a8\u0001\u0000\u0000\u0000\u01a8K\u0001\u0000\u0000\u0000\u01a9"+
		"\u01a7\u0001\u0000\u0000\u0000\u01aa\u01ab\u0006&\uffff\uffff\u0000\u01ab"+
		"\u01af\u0003N\'\u0000\u01ac\u01ad\u0005\u001a\u0000\u0000\u01ad\u01af"+
		"\u0003R)\u0000\u01ae\u01aa\u0001\u0000\u0000\u0000\u01ae\u01ac\u0001\u0000"+
		"\u0000\u0000\u01af\u01dd\u0001\u0000\u0000\u0000\u01b0\u01b1\n\u0006\u0000"+
		"\u0000\u01b1\u01b2\u0003Z-\u0000\u01b2\u01b3\u0003L&\u0007\u01b3\u01dc"+
		"\u0001\u0000\u0000\u0000\u01b4\u01b6\n\u0005\u0000\u0000\u01b5\u01b7\u0005"+
		")\u0000\u0000\u01b6\u01b5\u0001\u0000\u0000\u0000\u01b6\u01b7\u0001\u0000"+
		"\u0000\u0000\u01b7\u01b8\u0001\u0000\u0000\u0000\u01b8\u01b9\u0005+\u0000"+
		"\u0000\u01b9\u01ba\u0003L&\u0000\u01ba\u01bb\u0005&\u0000\u0000\u01bb"+
		"\u01bc\u0003L&\u0006\u01bc\u01dc\u0001\u0000\u0000\u0000\u01bd\u01bf\n"+
		"\u0003\u0000\u0000\u01be\u01c0\u0005)\u0000\u0000\u01bf\u01be\u0001\u0000"+
		"\u0000\u0000\u01bf\u01c0\u0001\u0000\u0000\u0000\u01c0\u01c1\u0001\u0000"+
		"\u0000\u0000\u01c1\u01c2\u0005\u0017\u0000\u0000\u01c2\u01dc\u0003L&\u0004"+
		"\u01c3\u01c5\n\u0002\u0000\u0000\u01c4\u01c6\u0005)\u0000\u0000\u01c5"+
		"\u01c4\u0001\u0000\u0000\u0000\u01c5\u01c6\u0001\u0000\u0000\u0000\u01c6"+
		"\u01c7\u0001\u0000\u0000\u0000\u01c7\u01c8\u0005\u0016\u0000\u0000\u01c8"+
		"\u01dc\u0003L&\u0003\u01c9\u01ca\n\u0007\u0000\u0000\u01ca\u01cc\u0005"+
		"\u0018\u0000\u0000\u01cb\u01cd\u0005)\u0000\u0000\u01cc\u01cb\u0001\u0000"+
		"\u0000\u0000\u01cc\u01cd\u0001\u0000\u0000\u0000\u01cd\u01ce\u0001\u0000"+
		"\u0000\u0000\u01ce\u01dc\u0005\u0019\u0000\u0000\u01cf\u01d1\n\u0004\u0000"+
		"\u0000\u01d0\u01d2\u0005)\u0000\u0000\u01d1\u01d0\u0001\u0000\u0000\u0000"+
		"\u01d1\u01d2\u0001\u0000\u0000\u0000\u01d2\u01d3\u0001\u0000\u0000\u0000"+
		"\u01d3\u01d4\u0005*\u0000\u0000\u01d4\u01d7\u0005\u0003\u0000\u0000\u01d5"+
		"\u01d8\u0003\u0002\u0001\u0000\u01d6\u01d8\u0003P(\u0000\u01d7\u01d5\u0001"+
		"\u0000\u0000\u0000\u01d7\u01d6\u0001\u0000\u0000\u0000\u01d8\u01d9\u0001"+
		"\u0000\u0000\u0000\u01d9\u01da\u0005\u0004\u0000\u0000\u01da\u01dc\u0001"+
		"\u0000\u0000\u0000\u01db\u01b0\u0001\u0000\u0000\u0000\u01db\u01b4\u0001"+
		"\u0000\u0000\u0000\u01db\u01bd\u0001\u0000\u0000\u0000\u01db\u01c3\u0001"+
		"\u0000\u0000\u0000\u01db\u01c9\u0001\u0000\u0000\u0000\u01db\u01cf\u0001"+
		"\u0000\u0000\u0000\u01dc\u01df\u0001\u0000\u0000\u0000\u01dd\u01db\u0001"+
		"\u0000\u0000\u0000\u01dd\u01de\u0001\u0000\u0000\u0000\u01deM\u0001\u0000"+
		"\u0000\u0000\u01df\u01dd\u0001\u0000\u0000\u0000\u01e0\u01e1\u0006\'\uffff"+
		"\uffff\u0000\u01e1\u01f7\u0003\\.\u0000\u01e2\u01f7\u0003h4\u0000\u01e3"+
		"\u01f7\u0003B!\u0000\u01e4\u01e5\u0005\u0003\u0000\u0000\u01e5\u01ea\u0003"+
		"R)\u0000\u01e6\u01e7\u0005\u0002\u0000\u0000\u01e7\u01e9\u0003R)\u0000"+
		"\u01e8\u01e6\u0001\u0000\u0000\u0000\u01e9\u01ec\u0001\u0000\u0000\u0000"+
		"\u01ea\u01e8\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001\u0000\u0000\u0000"+
		"\u01eb\u01ed\u0001\u0000\u0000\u0000\u01ec\u01ea\u0001\u0000\u0000\u0000"+
		"\u01ed\u01ee\u0005\u0004\u0000\u0000\u01ee\u01f7\u0001\u0000\u0000\u0000"+
		"\u01ef\u01f0\u0005\u0003\u0000\u0000\u01f0\u01f1\u0003\u0002\u0001\u0000"+
		"\u01f1\u01f2\u0005\u0004\u0000\u0000\u01f2\u01f7\u0001\u0000\u0000\u0000"+
		"\u01f3\u01f4\u0003V+\u0000\u01f4\u01f5\u0003N\'\u0001\u01f5\u01f7\u0001"+
		"\u0000\u0000\u0000\u01f6\u01e0\u0001\u0000\u0000\u0000\u01f6\u01e2\u0001"+
		"\u0000\u0000\u0000\u01f6\u01e3\u0001\u0000\u0000\u0000\u01f6\u01e4\u0001"+
		"\u0000\u0000\u0000\u01f6\u01ef\u0001\u0000\u0000\u0000\u01f6\u01f3\u0001"+
		"\u0000\u0000\u0000\u01f7\u01fe\u0001\u0000\u0000\u0000\u01f8\u01f9\n\u0002"+
		"\u0000\u0000\u01f9\u01fa\u0003T*\u0000\u01fa\u01fb\u0003N\'\u0003\u01fb"+
		"\u01fd\u0001\u0000\u0000\u0000\u01fc\u01f8\u0001\u0000\u0000\u0000\u01fd"+
		"\u0200\u0001\u0000\u0000\u0000\u01fe\u01fc\u0001\u0000\u0000\u0000\u01fe"+
		"\u01ff\u0001\u0000\u0000\u0000\u01ffO\u0001\u0000\u0000\u0000\u0200\u01fe"+
		"\u0001\u0000\u0000\u0000\u0201\u0206\u0003R)\u0000\u0202\u0203\u0005\u0002"+
		"\u0000\u0000\u0203\u0205\u0003R)\u0000\u0204\u0202\u0001\u0000\u0000\u0000"+
		"\u0205\u0208\u0001\u0000\u0000\u0000\u0206\u0204\u0001\u0000\u0000\u0000"+
		"\u0206\u0207\u0001\u0000\u0000\u0000\u0207Q\u0001\u0000\u0000\u0000\u0208"+
		"\u0206\u0001\u0000\u0000\u0000\u0209\u020a\u0005\u0003\u0000\u0000\u020a"+
		"\u020b\u0003R)\u0000\u020b\u020c\u0005\u0004\u0000\u0000\u020c\u0212\u0001"+
		"\u0000\u0000\u0000\u020d\u020e\u0007\u0003\u0000\u0000\u020e\u0212\u0003"+
		"R)\u0000\u020f\u0212\u0003N\'\u0000\u0210\u0212\u0003\u0018\f\u0000\u0211"+
		"\u0209\u0001\u0000\u0000\u0000\u0211\u020d\u0001\u0000\u0000\u0000\u0211"+
		"\u020f\u0001\u0000\u0000\u0000\u0211\u0210\u0001\u0000\u0000\u0000\u0212"+
		"S\u0001\u0000\u0000\u0000\u0213\u0214\u0007\u0004\u0000\u0000\u0214U\u0001"+
		"\u0000\u0000\u0000\u0215\u0216\u0007\u0005\u0000\u0000\u0216W\u0001\u0000"+
		"\u0000\u0000\u0217\u021c\u0005&\u0000\u0000\u0218\u021c\u0005(\u0000\u0000"+
		"\u0219\u021c\u0005\'\u0000\u0000\u021a\u021c\u0001\u0000\u0000\u0000\u021b"+
		"\u0217\u0001\u0000\u0000\u0000\u021b\u0218\u0001\u0000\u0000\u0000\u021b"+
		"\u0219\u0001\u0000\u0000\u0000\u021b\u021a\u0001\u0000\u0000\u0000\u021c"+
		"Y\u0001\u0000\u0000\u0000\u021d\u021e\u0007\u0006\u0000\u0000\u021e[\u0001"+
		"\u0000\u0000\u0000\u021f\u0227\u0003n7\u0000\u0220\u0227\u0003^/\u0000"+
		"\u0221\u0222\u0005\u000b\u0000\u0000\u0222\u0227\u0003^/\u0000\u0223\u0227"+
		"\u0003f3\u0000\u0224\u0227\u0003`0\u0000\u0225\u0227\u0003b1\u0000\u0226"+
		"\u021f\u0001\u0000\u0000\u0000\u0226\u0220\u0001\u0000\u0000\u0000\u0226"+
		"\u0221\u0001\u0000\u0000\u0000\u0226\u0223\u0001\u0000\u0000\u0000\u0226"+
		"\u0224\u0001\u0000\u0000\u0000\u0226\u0225\u0001\u0000\u0000\u0000\u0227"+
		"]\u0001\u0000\u0000\u0000\u0228\u0229\u0005H\u0000\u0000\u0229_\u0001"+
		"\u0000\u0000\u0000\u022a\u022b\u0005\u0019\u0000\u0000\u022ba\u0001\u0000"+
		"\u0000\u0000\u022c\u022d\u0003n7\u0000\u022d\u022e\u0005\u0013\u0000\u0000"+
		"\u022e\u022f\u0003d2\u0000\u022fc\u0001\u0000\u0000\u0000\u0230\u0231"+
		"\u0003n7\u0000\u0231e\u0001\u0000\u0000\u0000\u0232\u0233\u0007\u0007"+
		"\u0000\u0000\u0233g\u0001\u0000\u0000\u0000\u0234\u0236\u0003l6\u0000"+
		"\u0235\u0237\u0003j5\u0000\u0236\u0235\u0001\u0000\u0000\u0000\u0236\u0237"+
		"\u0001\u0000\u0000\u0000\u0237i\u0001\u0000\u0000\u0000\u0238\u0239\u0005"+
		"\u0014\u0000\u0000\u0239\u023a\u0003l6\u0000\u023ak\u0001\u0000\u0000"+
		"\u0000\u023b\u023c\u0003p8\u0000\u023cm\u0001\u0000\u0000\u0000\u023d"+
		"\u023e\u0005G\u0000\u0000\u023eo\u0001\u0000\u0000\u0000\u023f\u0240\u0005"+
		"F\u0000\u0000\u0240q\u0001\u0000\u0000\u0000\u0241\u0242\u0007\b\u0000"+
		"\u0000\u0242s\u0001\u0000\u0000\u0000:v|\u0083\u0089\u008d\u009c\u00a4"+
		"\u00c0\u00c7\u00cf\u00db\u00e5\u00e9\u00f0\u00f4\u00f7\u00fa\u00fd\u0100"+
		"\u0110\u0116\u011b\u011e\u012b\u012f\u0132\u013a\u0147\u014c\u0152\u015c"+
		"\u0165\u016c\u0171\u0179\u0185\u018b\u0192\u019d\u01a5\u01a7\u01ae\u01b6"+
		"\u01bf\u01c5\u01cc\u01d1\u01d7\u01db\u01dd\u01ea\u01f6\u01fe\u0206\u0211"+
		"\u021b\u0226\u0236";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}