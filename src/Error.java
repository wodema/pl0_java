public class Error {
    private static final String[] errorMsg = {
        /*  0 */    "",
        /*  1 */    "Found ':=' when expecting '='.",
        /*  2 */    "There must be a number to follow '='.",
        /*  3 */    "There must be an '=' to follow the identifier.",
        /*  4 */    "There must be an identifier to follow 'const', 'var', or 'procedure'.",
        /*  5 */    "Missing ',' or ';'.",
        /*  6 */    "Incorrect procedure name.",
        /*  7 */    "Statement expected.",
        /*  8 */    "Follow the statement is an incorrect symbol.",
        /*  9 */    "'.' expected.",
        /* 10 */    "';' expected.",
        /* 11 */    "Undeclared identifier.",
        /* 12 */    "Illegal assignment.",
        /* 13 */    "':=' expected.",
        /* 14 */    "There must be an identifier to follow the 'call'.",
        /* 15 */    "A constant or variable can not be called.",
        /* 16 */    "'then' expected.",
        /* 17 */    "';' or 'end' expected.",
        /* 18 */    "'do' expected.",
        /* 19 */    "Incorrect symbol.",
        /* 20 */    "Relative operators expected.",
        /* 21 */    "Procedure identifier can not be in an expression.",
        /* 22 */    "Missing ')'.",
        /* 23 */    "The symbol can not be followed by a factor.",
        /* 24 */    "The symbol can not be as the beginning of an expression.",
        /* 25 */    "The number is too great.",
        /* 26 */    "The length of identifier is too long.",
        /* 27 */    "syntax error in for statement",
        /* 28 */    "",
        /* 29 */    "",
        /* 30 */    "",
        /* 31 */    "",
        /* 32 */    "There are too many levels."
    };


    public static void ErrorCause(int errorNumber) {
        System.out.print("      ");
        for (int i = 1; i <= Constant.cc - 1; i++)
            System.out.print(" ");
        System.out.println();
        System.out.printf("Error %3d: %s\n", errorNumber, errorMsg[errorNumber]);
        Constant.err++;
    }



}
