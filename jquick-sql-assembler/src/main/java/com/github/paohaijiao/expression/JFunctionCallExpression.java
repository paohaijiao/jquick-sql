/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.expression;

import com.github.paohaijiao.enums.JExpressionType;
import lombok.Data;

import java.util.List;

/**
 * packageName com.github.paohaijiao.expression
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Data
public class JFunctionCallExpression extends JExpression {

    private String functionName;

    private List<JExpression> arguments;

    private boolean distinct;


    public JFunctionCallExpression(String functionName, List<JExpression> arguments) {
        this.type = JExpressionType.FUNCTION;
        this.functionName = functionName;
        this.arguments = arguments;
    }


//    @Override
//    public <T> T accept(ExpressionVisitor<T> visitor) {
//        return visitor.visit(this);
//    }
}
