// Generated from D:/idea/jthornruleGrammer/sql/JQuickSQL.g4 by ANTLR 4.13.2
package com.github.paohaijiao.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JQuickSQLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JQuickSQLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery(JQuickSQLParser.QueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cteQuery}
	 * labeled alternative in {@link JQuickSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCteQuery(JQuickSQLParser.CteQueryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleQuery}
	 * labeled alternative in {@link JQuickSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleQuery(JQuickSQLParser.SingleQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#selectExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#selectClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectClause(JQuickSQLParser.SelectClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#whereClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereClause(JQuickSQLParser.WhereClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#groupByClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByClause(JQuickSQLParser.GroupByClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#havingClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHavingClause(JQuickSQLParser.HavingClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#selectSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectSpec(JQuickSQLParser.SelectSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#selectElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElements(JQuickSQLParser.SelectElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#selectElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElement(JQuickSQLParser.SelectElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#fromClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFromClause(JQuickSQLParser.FromClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#joinClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinClause(JQuickSQLParser.JoinClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#tableNameItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableNameItem(JQuickSQLParser.TableNameItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#tableNameSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableNameSpec(JQuickSQLParser.TableNameSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#joinType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinType(JQuickSQLParser.JoinTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#groupByItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByItem(JQuickSQLParser.GroupByItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#orderByClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderByClause(JQuickSQLParser.OrderByClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#orderByExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderByExpression(JQuickSQLParser.OrderByExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#limitClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimitClause(JQuickSQLParser.LimitClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#limitOnly}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimitOnly(JQuickSQLParser.LimitOnlyContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#limitWithOffset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimitWithOffset(JQuickSQLParser.LimitWithOffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#commonTableExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommonTableExpression(JQuickSQLParser.CommonTableExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#columnNames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnNames(JQuickSQLParser.ColumnNamesContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#initialQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialQuery(JQuickSQLParser.InitialQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#recursivePart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecursivePart(JQuickSQLParser.RecursivePartContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(JQuickSQLParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#functionArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#functionArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArg(JQuickSQLParser.FunctionArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#filterCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilterCondition(JQuickSQLParser.FilterConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionAtomPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exisitsPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExisitsPredicate(JQuickSQLParser.ExisitsPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryComparisonPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryComparisonPredicate(JQuickSQLParser.BinaryComparisonPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInPredicate(JQuickSQLParser.InPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code betweenPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBetweenPredicate(JQuickSQLParser.BetweenPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isNullPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsNullPredicate(JQuickSQLParser.IsNullPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code likePredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLikePredicate(JQuickSQLParser.LikePredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code regexpPredicate}
	 * labeled alternative in {@link JQuickSQLParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpressionAtom(JQuickSQLParser.UnaryExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code subqueryExperssionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubqueryExperssionAtom(JQuickSQLParser.SubqueryExperssionAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constantExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantExpressionAtom(JQuickSQLParser.ConstantExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCallExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallExpressionAtom(JQuickSQLParser.FunctionCallExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fullColumnNameExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullColumnNameExpressionAtom(JQuickSQLParser.FullColumnNameExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedExpressionAtom(JQuickSQLParser.NestedExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathExpressionAtom}
	 * labeled alternative in {@link JQuickSQLParser#expressionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathExpressionAtom(JQuickSQLParser.MathExpressionAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#expressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressions(JQuickSQLParser.ExpressionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpression(JQuickSQLParser.ParenExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(JQuickSQLParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code predicateExpression}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicateExpression(JQuickSQLParser.PredicateExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectResult}
	 * labeled alternative in {@link JQuickSQLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectResult(JQuickSQLParser.SelectResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#mathOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperator(JQuickSQLParser.MathOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#unaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(JQuickSQLParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#logicalOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOperator(JQuickSQLParser.LogicalOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(JQuickSQLParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#decimal_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimal_literal(JQuickSQLParser.Decimal_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#null_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNull_literal(JQuickSQLParser.Null_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#dateLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateLiteral(JQuickSQLParser.DateLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#format}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormat(JQuickSQLParser.FormatContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#booleanLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(JQuickSQLParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#fullColumnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullColumnName(JQuickSQLParser.FullColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#tableName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableName(JQuickSQLParser.TableNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#dottedId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDottedId(JQuickSQLParser.DottedIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#uid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUid(JQuickSQLParser.UidContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(JQuickSQLParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#simpleId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleId(JQuickSQLParser.SimpleIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link JQuickSQLParser#keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyword(JQuickSQLParser.KeywordContext ctx);
}