package com.github.paohaijiao.provider;
import com.github.paohaijiao.builder.JSONPathQueryBuilder;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.function.domain.JQuickBaseFunctionFunctionProvider;
import com.github.paohaijiao.model.JSONObject;
import com.github.paohaijiao.model.JSONPathResult;
import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;

import java.util.Arrays;
import java.util.List;

@Priority(PriorityConstants.SYSTEM_HIGH)
public class JQuickJSonPathFunctionFunctionProvider  extends JQuickBaseFunctionFunctionProvider {
    public JQuickJSonPathFunctionFunctionProvider() {
        super("jsonPath", "根据json路径表达式提取数据 - 用法: jsonPath(json字段，字段表达式)");
    }

    @Override
    public Object invoke(List<Object> args) {
        validateArgCount(args, 2);
        Object object = args.get(0);
        JAssert.notNull(object, "para1 require not null");
        String path = (String) args.get(1);
        JSONObject obj;
        if (object instanceof JSONObject) {
            obj = (JSONObject) object;
        } else if (object instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) object;
            obj = new JSONObject(map);
        } else {
            throw new IllegalArgumentException(
                    "jsonPath first argument must be a JSON object or row, but was: "
                            + (object == null ? "null" : object.getClass().getName()));
        }
        JSONPathResult result = JSONPathQueryBuilder.from(obj)
                .path(path)
                .execute();
        return result.getRawData();
    }
}
