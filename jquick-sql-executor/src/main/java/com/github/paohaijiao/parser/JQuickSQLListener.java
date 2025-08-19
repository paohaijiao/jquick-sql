// Generated from D:/idea/jthornruleGrammer/sql/JQuickSQL.g4 by ANTLR 4.13.2
package com.github.paohaijiao.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JQuickSQLParser}.
 */
public interface JQuickSQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(JQuickSQLParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(JQuickSQLParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code cteQuery}
	 * labeled alternative in {@link JQuickSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterCteQuery(JQuickSQLParser.CteQueryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code cteQuery}
	 * labeled alternative in {@link JQuickSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitCteQuery(JQuickSQLParser.CteQueryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleQuery}
	 * labeled alternative in {@link JQuickSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSingleQuery(JQuickSQLParser.SingleQueryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleQuery}
	 * labeled alternative in {@link JQuickSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSingleQuery(JQuickSQLParser.SingleQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#selectExpression}.
	 * @param ctx the parse tree
	 */
	void enterSelectExpression(JQuickSQLParser.SelectExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#selectExpression}.
	 * @param ctx the parse tree
	 */
	void exitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#olapOperation}.
	 * @param ctx the parse tree
	 */
	void enterOlapOperation(JQuickSQLParser.OlapOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#olapOperation}.
	 * @param ctx the parse tree
	 */
	void exitOlapOperation(JQuickSQLParser.OlapOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#rollUp}.
	 * @param ctx the parse tree
	 */
	void enterRollUp(JQuickSQLParser.RollUpContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#rollUp}.
	 * @param ctx the parse tree
	 */
	void exitRollUp(JQuickSQLParser.RollUpContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#drillDown}.
	 * @param ctx the parse tree
	 */
	void enterDrillDown(JQuickSQLParser.DrillDownContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#drillDown}.
	 * @param ctx the parse tree
	 */
	void exitDrillDown(JQuickSQLParser.DrillDownContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#drillDownDimensions}.
	 * @param ctx the parse tree
	 */
	void enterDrillDownDimensions(JQuickSQLParser.DrillDownDimensionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#drillDownDimensions}.
	 * @param ctx the parse tree
	 */
	void exitDrillDownDimensions(JQuickSQLParser.DrillDownDimensionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#slice}.
	 * @param ctx the parse tree
	 */
	void enterSlice(JQuickSQLParser.SliceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#slice}.
	 * @param ctx the parse tree
	 */
	void exitSlice(JQuickSQLParser.SliceContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#sliceCondition}.
	 * @param ctx the parse tree
	 */
	void enterSliceCondition(JQuickSQLParser.SliceConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#sliceCondition}.
	 * @param ctx the parse tree
	 */
	void exitSliceCondition(JQuickSQLParser.SliceConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#dice}.
	 * @param ctx the parse tree
	 */
	void enterDice(JQuickSQLParser.DiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#dice}.
	 * @param ctx the parse tree
	 */
	void exitDice(JQuickSQLParser.DiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#diceConditions}.
	 * @param ctx the parse tree
	 */
	void enterDiceConditions(JQuickSQLParser.DiceConditionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#diceConditions}.
	 * @param ctx the parse tree
	 */
	void exitDiceConditions(JQuickSQLParser.DiceConditionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#diceCondition}.
	 * @param ctx the parse tree
	 */
	void enterDiceCondition(JQuickSQLParser.DiceConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#diceCondition}.
	 * @param ctx the parse tree
	 */
	void exitDiceCondition(JQuickSQLParser.DiceConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#pivot}.
	 * @param ctx the parse tree
	 */
	void enterPivot(JQuickSQLParser.PivotContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#pivot}.
	 * @param ctx the parse tree
	 */
	void exitPivot(JQuickSQLParser.PivotContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#pivotAggregate}.
	 * @param ctx the parse tree
	 */
	void enterPivotAggregate(JQuickSQLParser.PivotAggregateContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#pivotAggregate}.
	 * @param ctx the parse tree
	 */
	void exitPivotAggregate(JQuickSQLParser.PivotAggregateContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#pivotColumn}.
	 * @param ctx the parse tree
	 */
	void enterPivotColumn(JQuickSQLParser.PivotColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#pivotColumn}.
	 * @param ctx the parse tree
	 */
	void exitPivotColumn(JQuickSQLParser.PivotColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#pivotValues}.
	 * @param ctx the parse tree
	 */
	void enterPivotValues(JQuickSQLParser.PivotValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#pivotValues}.
	 * @param ctx the parse tree
	 */
	void exitPivotValues(JQuickSQLParser.PivotValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#selectClause}.
	 * @param ctx the parse tree
	 */
	void enterSelectClause(JQuickSQLParser.SelectClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#selectClause}.
	 * @param ctx the parse tree
	 */
	void exitSelectClause(JQuickSQLParser.SelectClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#selectSpec}.
	 * @param ctx the parse tree
	 */
	void enterSelectSpec(JQuickSQLParser.SelectSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#selectSpec}.
	 * @param ctx the parse tree
	 */
	void exitSelectSpec(JQuickSQLParser.SelectSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#selectElements}.
	 * @param ctx the parse tree
	 */
	void enterSelectElements(JQuickSQLParser.SelectElementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#selectElements}.
	 * @param ctx the parse tree
	 */
	void exitSelectElements(JQuickSQLParser.SelectElementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void enterSelectElement(JQuickSQLParser.SelectElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void exitSelectElement(JQuickSQLParser.SelectElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void enterFromClause(JQuickSQLParser.FromClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void exitFromClause(JQuickSQLParser.FromClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#tableSources}.
	 * @param ctx the parse tree
	 */
	void enterTableSources(JQuickSQLParser.TableSourcesContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#tableSources}.
	 * @param ctx the parse tree
	 */
	void exitTableSources(JQuickSQLParser.TableSourcesContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#tableSource}.
	 * @param ctx the parse tree
	 */
	void enterTableSource(JQuickSQLParser.TableSourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#tableSource}.
	 * @param ctx the parse tree
	 */
	void exitTableSource(JQuickSQLParser.TableSourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#tableSourceItem}.
	 * @param ctx the parse tree
	 */
	void enterTableSourceItem(JQuickSQLParser.TableSourceItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#tableSourceItem}.
	 * @param ctx the parse tree
	 */
	void exitTableSourceItem(JQuickSQLParser.TableSourceItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#joinPart}.
	 * @param ctx the parse tree
	 */
	void enterJoinPart(JQuickSQLParser.JoinPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#joinPart}.
	 * @param ctx the parse tree
	 */
	void exitJoinPart(JQuickSQLParser.JoinPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#joinType}.
	 * @param ctx the parse tree
	 */
	void enterJoinType(JQuickSQLParser.JoinTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#joinType}.
	 * @param ctx the parse tree
	 */
	void exitJoinType(JQuickSQLParser.JoinTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#uidList}.
	 * @param ctx the parse tree
	 */
	void enterUidList(JQuickSQLParser.UidListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#uidList}.
	 * @param ctx the parse tree
	 */
	void exitUidList(JQuickSQLParser.UidListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#groupByItem}.
	 * @param ctx the parse tree
	 */
	void enterGroupByItem(JQuickSQLParser.GroupByItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#groupByItem}.
	 * @param ctx the parse tree
	 */
	void exitGroupByItem(JQuickSQLParser.GroupByItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#havingExpr}.
	 * @param ctx the parse tree
	 */
	void enterHavingExpr(JQuickSQLParser.HavingExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#havingExpr}.
	 * @param ctx the parse tree
	 */
	void exitHavingExpr(JQuickSQLParser.HavingExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#orderByClause}.
	 * @param ctx the parse tree
	 */
	void enterOrderByClause(JQuickSQLParser.OrderByClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#orderByClause}.
	 * @param ctx the parse tree
	 */
	void exitOrderByClause(JQuickSQLParser.OrderByClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#orderByExpression}.
	 * @param ctx the parse tree
	 */
	void enterOrderByExpression(JQuickSQLParser.OrderByExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#orderByExpression}.
	 * @param ctx the parse tree
	 */
	void exitOrderByExpression(JQuickSQLParser.OrderByExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#limitClause}.
	 * @param ctx the parse tree
	 */
	void enterLimitClause(JQuickSQLParser.LimitClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#limitClause}.
	 * @param ctx the parse tree
	 */
	void exitLimitClause(JQuickSQLParser.LimitClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#limitOnly}.
	 * @param ctx the parse tree
	 */
	void enterLimitOnly(JQuickSQLParser.LimitOnlyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#limitOnly}.
	 * @param ctx the parse tree
	 */
	void exitLimitOnly(JQuickSQLParser.LimitOnlyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#limitWithOffset}.
	 * @param ctx the parse tree
	 */
	void enterLimitWithOffset(JQuickSQLParser.LimitWithOffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#limitWithOffset}.
	 * @param ctx the parse tree
	 */
	void exitLimitWithOffset(JQuickSQLParser.LimitWithOffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#commonTableExpression}.
	 * @param ctx the parse tree
	 */
	void enterCommonTableExpression(JQuickSQLParser.CommonTableExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#commonTableExpression}.
	 * @param ctx the parse tree
	 */
	void exitCommonTableExpression(JQuickSQLParser.CommonTableExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#columnNames}.
	 * @param ctx the parse tree
	 */
	void enterColumnNames(JQuickSQLParser.ColumnNamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#columnNames}.
	 * @param ctx the parse tree
	 */
	void exitColumnNames(JQuickSQLParser.ColumnNamesContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#initialQuery}.
	 * @param ctx the parse tree
	 */
	void enterInitialQuery(JQuickSQLParser.InitialQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#initialQuery}.
	 * @param ctx the parse tree
	 */
	void exitInitialQuery(JQuickSQLParser.InitialQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#recursivePart}.
	 * @param ctx the parse tree
	 */
	void enterRecursivePart(JQuickSQLParser.RecursivePartContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#recursivePart}.
	 * @param ctx the parse tree
	 */
	void exitRecursivePart(JQuickSQLParser.RecursivePartContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#expressions}.
	 * @param ctx the parse tree
	 */
	void enterExpressions(JQuickSQLParser.ExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#expressions}.
	 * @param ctx the parse tree
	 */
	void exitExpressions(JQuickSQLParser.ExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(JQuickSQLParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(JQuickSQLParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#compOperator}.
	 * @param ctx the parse tree
	 */
	void enterCompOperator(JQuickSQLParser.CompOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#compOperator}.
	 * @param ctx the parse tree
	 */
	void exitCompOperator(JQuickSQLParser.CompOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#aggregateFunction}.
	 * @param ctx the parse tree
	 */
	void enterAggregateFunction(JQuickSQLParser.AggregateFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#aggregateFunction}.
	 * @param ctx the parse tree
	 */
	void exitAggregateFunction(JQuickSQLParser.AggregateFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#functionArgs}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#functionArgs}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArg(JQuickSQLParser.FunctionArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArg(JQuickSQLParser.FunctionArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionAtomPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionAtomPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryComparisonPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterBinaryComparisonPredicate(JQuickSQLParser.BinaryComparisonPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryComparisonPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitBinaryComparisonPredicate(JQuickSQLParser.BinaryComparisonPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterInPredicate(JQuickSQLParser.InPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitInPredicate(JQuickSQLParser.InPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exisitsExpression}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterExisitsExpression(JQuickSQLParser.ExisitsExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exisitsExpression}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitExisitsExpression(JQuickSQLParser.ExisitsExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code betweenPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterBetweenPredicate(JQuickSQLParser.BetweenPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code betweenPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitBetweenPredicate(JQuickSQLParser.BetweenPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code isNullPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterIsNullPredicate(JQuickSQLParser.IsNullPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code isNullPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitIsNullPredicate(JQuickSQLParser.IsNullPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code likePredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterLikePredicate(JQuickSQLParser.LikePredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code likePredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitLikePredicate(JQuickSQLParser.LikePredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionAtomPredicateWithLocalId}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAtomPredicateWithLocalId(JQuickSQLParser.ExpressionAtomPredicateWithLocalIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionAtomPredicateWithLocalId}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAtomPredicateWithLocalId(JQuickSQLParser.ExpressionAtomPredicateWithLocalIdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code regexpPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code regexpPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpressionAtom(JQuickSQLParser.UnaryExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpressionAtom(JQuickSQLParser.UnaryExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code subqueryExperssionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterSubqueryExperssionAtom(JQuickSQLParser.SubqueryExperssionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code subqueryExperssionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitSubqueryExperssionAtom(JQuickSQLParser.SubqueryExperssionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constantExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpressionAtom(JQuickSQLParser.ConstantExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constantExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpressionAtom(JQuickSQLParser.ConstantExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCallExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallExpressionAtom(JQuickSQLParser.FunctionCallExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCallExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallExpressionAtom(JQuickSQLParser.FunctionCallExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fullColumnNameExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterFullColumnNameExpressionAtom(JQuickSQLParser.FullColumnNameExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fullColumnNameExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitFullColumnNameExpressionAtom(JQuickSQLParser.FullColumnNameExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterNestedExpressionAtom(JQuickSQLParser.NestedExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitNestedExpressionAtom(JQuickSQLParser.NestedExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mathExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterMathExpressionAtom(JQuickSQLParser.MathExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mathExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitMathExpressionAtom(JQuickSQLParser.MathExpressionAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectResult}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSelectResult(JQuickSQLParser.SelectResultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectResult}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSelectResult(JQuickSQLParser.SelectResultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNotExpression(JQuickSQLParser.NotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNotExpression(JQuickSQLParser.NotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenExpression(JQuickSQLParser.ParenExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenExpression(JQuickSQLParser.ParenExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpression(JQuickSQLParser.LogicalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpression(JQuickSQLParser.LogicalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code predicateExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPredicateExpression(JQuickSQLParser.PredicateExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code predicateExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPredicateExpression(JQuickSQLParser.PredicateExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void enterMathOperator(JQuickSQLParser.MathOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void exitMathOperator(JQuickSQLParser.MathOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(JQuickSQLParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(JQuickSQLParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOperator(JQuickSQLParser.LogicalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOperator(JQuickSQLParser.LogicalOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(JQuickSQLParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(JQuickSQLParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#decimal_literal}.
	 * @param ctx the parse tree
	 */
	void enterDecimal_literal(JQuickSQLParser.Decimal_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#decimal_literal}.
	 * @param ctx the parse tree
	 */
	void exitDecimal_literal(JQuickSQLParser.Decimal_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#null_literal}.
	 * @param ctx the parse tree
	 */
	void enterNull_literal(JQuickSQLParser.Null_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#null_literal}.
	 * @param ctx the parse tree
	 */
	void exitNull_literal(JQuickSQLParser.Null_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#dateLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDateLiteral(JQuickSQLParser.DateLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#dateLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDateLiteral(JQuickSQLParser.DateLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#format}.
	 * @param ctx the parse tree
	 */
	void enterFormat(JQuickSQLParser.FormatContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#format}.
	 * @param ctx the parse tree
	 */
	void exitFormat(JQuickSQLParser.FormatContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(JQuickSQLParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(JQuickSQLParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void enterFullColumnName(JQuickSQLParser.FullColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void exitFullColumnName(JQuickSQLParser.FullColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#tableName}.
	 * @param ctx the parse tree
	 */
	void enterTableName(JQuickSQLParser.TableNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#tableName}.
	 * @param ctx the parse tree
	 */
	void exitTableName(JQuickSQLParser.TableNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#schemaName}.
	 * @param ctx the parse tree
	 */
	void enterSchemaName(JQuickSQLParser.SchemaNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#schemaName}.
	 * @param ctx the parse tree
	 */
	void exitSchemaName(JQuickSQLParser.SchemaNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#dottedId}.
	 * @param ctx the parse tree
	 */
	void enterDottedId(JQuickSQLParser.DottedIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#dottedId}.
	 * @param ctx the parse tree
	 */
	void exitDottedId(JQuickSQLParser.DottedIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#uid}.
	 * @param ctx the parse tree
	 */
	void enterUid(JQuickSQLParser.UidContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#uid}.
	 * @param ctx the parse tree
	 */
	void exitUid(JQuickSQLParser.UidContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(JQuickSQLParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(JQuickSQLParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#simpleId}.
	 * @param ctx the parse tree
	 */
	void enterSimpleId(JQuickSQLParser.SimpleIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#simpleId}.
	 * @param ctx the parse tree
	 */
	void exitSimpleId(JQuickSQLParser.SimpleIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link JQuickSQLParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(JQuickSQLParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link JQuickSQLParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(JQuickSQLParser.KeywordContext ctx);
}