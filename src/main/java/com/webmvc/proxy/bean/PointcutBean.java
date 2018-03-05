package com.webmvc.proxy.bean;

/**
 * Created by A550V
 * 2018/3/4 15:48
 */
public class PointcutBean {

    private String targetReturnType;
    private String targetBeanName;
    private String targetMethodName;
    private String expression;


    public String getTargetReturnType() {
        return targetReturnType;
    }

    public void setTargetReturnType(String returnType) {
        this.targetReturnType = returnType;
    }

    public String getTargetBeanName() {
        return targetBeanName;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointcutBean that = (PointcutBean) o;

        if (targetReturnType != null ? !targetReturnType.equals(that.targetReturnType) : that.targetReturnType != null)
            return false;
        if (targetBeanName != null ? !targetBeanName.equals(that.targetBeanName) : that.targetBeanName != null)
            return false;
        if (targetMethodName != null ? !targetMethodName.equals(that.targetMethodName) : that.targetMethodName != null)
            return false;
        return expression != null ? expression.equals(that.expression) : that.expression == null;
    }

    @Override
    public int hashCode() {
        int result = targetReturnType != null ? targetReturnType.hashCode() : 0;
        result = 31 * result + (targetBeanName != null ? targetBeanName.hashCode() : 0);
        result = 31 * result + (targetMethodName != null ? targetMethodName.hashCode() : 0);
        result = 31 * result + (expression != null ? expression.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PointcutBean{" +
                "targetReturnType='" + targetReturnType + '\'' +
                ", targetBeanName='" + targetBeanName + '\'' +
                ", targetMethodName='" + targetMethodName + '\'' +
                ", expression='" + expression + '\'' +
                '}';
    }
}
