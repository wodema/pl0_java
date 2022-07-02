public enum SymbolType {
    SYM_NULL(""),
    SYM_IDENTIFIER(""),
    SYM_NUMBER(""),
    SYM_ODD("odd"),
    SYM_PLUS("+"),
    SYM_MINUS("-"),
    SYM_TIMES("*"),
    SYM_SLASH("/"),
    SYM_EQU("="),
    SYM_NEQ("!="),
    SYM_LES("<"),
    SYM_LEQ("<="),
    SYM_GTR(">"),
    SYM_GEQ(">="),
    SYM_LPAREN("("),
    SYM_RPAREN(")"),
    SYM_COMMA(","),
    SYM_SEMICOLON(";"),
    SYM_PERIOD("."),
    SYM_BECOMES(":="),
    SYM_BEGIN("begin"),
    SYM_END("end"),
    SYM_IF("if"),
    SYM_THEN("then"),
    SYM_WHILE("while"),
    SYM_CALL("call"),
    SYM_CONST("const"),
    SYM_VAR("var"),
    SYM_PROCEDURE("procedure"),

    //新增关键字
    SYM_DO("do"),
    SYM_ELSE("else"),
    SYM_FOR("for"),
    SYM_UNTIL("until"),
    SYM_STEP("step"),
    SYM_RETURN("return"),
    SYM_TIMES_EQU("*="),
    SYM_SLASH_EQU("/="),
    SYM_AND("&"),
    SYM_OR("||"),
    SYM_NOTE("//"),
    SYM_NOTE_LINE("/*"),
    SYM_NOTE_LINE_END("*/");
    private final String value;

    public String getValue() {
        return value;
    }

    SymbolType(String value) {
        this.value = value;
    }

}
