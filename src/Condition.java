public class Condition{
    private final String targetColumn;
    private final String operator;
    private final String conditionValue;

    public Condition(String targetColumn, String operator, String conditionValue){
        this.targetColumn = targetColumn;
        this.operator = operator;
        this.conditionValue = conditionValue;
    }

    public Condition(){
        this.targetColumn = "";
        this.operator = "";
        this.conditionValue = "";
    }

    public boolean isEmpty(){
        return this.targetColumn.isEmpty() && this.operator.isEmpty() && this.conditionValue.isEmpty();
    }
    public String getColumn() {
        return targetColumn;
    }
    public String getOperator() {
        return operator;
    }
    public String getConditionValue() {
        return conditionValue;
    }
}
