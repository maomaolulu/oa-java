package com.ruoyi.activiti.utils;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程工具类
 * @author zx
 * @date 2022/1/10 10:22
 */
public class ProcessUtil {
    private ProcessUtil() {
    }
    @Autowired
    private RuntimeService runtimeService;
    /**
     * 自定义juel解析
     * 判断当前是否是合适的路径
     * @param expressionText    表达式
     * @param variables 运行时变量
     * @return true符合条件条件的路径
     */
    public static boolean isTargetTask(final String expressionText, Map<String, Object> variables) {

    ExpressionFactory factory = new ExpressionFactoryImpl();
    SimpleContext context = new SimpleContext();

    for (String key : variables.keySet()) {
        context.setVariable(key, factory.createValueExpression(variables.get(key), variables.get(key).getClass()));
    }
    try {
        ValueExpression e = factory.createValueExpression(context, expressionText, boolean.class);
        return (boolean) e.getValue(context);
    } catch (Exception ex) {
        return false;
    }
    }

    /**
     * TODO 局部变量判断
     * 判断当前是否是合适的路径
     *
     * @param processInstanceId 实例ID
     * @return true符合条件条件的路径
     */
    public boolean isTargetTask(String processInstanceId, final PvmTransition transition) {

        final ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(processInstanceId)
                .singleResult();
        Boolean result = ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(
                new Command<Boolean>() {
                    @Override
                    public Boolean execute(CommandContext commandContext) {
                        UelExpressionCondition flowCondition = (UelExpressionCondition) transition.getProperty("condition");
                        boolean evel_ret = flowCondition.evaluate(transition.getId(), execution);
                        return evel_ret;
                    }
                });
        return result;
    }

    public static void main(String[] args) {
        Map<String, Object> variables = new HashMap<>(2);
        variables.put("a",100.8);
        variables.put("b",100.4);
        System.out.println(isTargetTask("${a>b}", variables));
    }

}
