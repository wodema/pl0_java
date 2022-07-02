import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

public class Constant {
    // 最大保留字数量
    public static final int MAX_RESERVED_WORD_LENGTH = 16;
    // 最大标识符表长
    public static final int MAX_TABLE_LENGTH = 500;
    // 最大数字位数
    public static final int MAX_NUMBER_LENGTH = 14;
    // 最大关键字数
    public static final int MAX_SYMBOL_LENGTH = 20;
    // 最大过程表长
    public static final int MAX_ID_LENGTH = 10;
    // 最大地址
    public static final int MAX_ADDRESS = 32767;
    // 嵌套块的最大深度
    public static final int MAX_LEVEL = 32;
    // 最大代码行数
    public static final int MAX_CODE = 500;
    // 最大栈容量
    public static final int MAX_STACK_SIZE = 1000;


    public static char ch = ' ';
    public static SymbolType symbol = SymbolType.SYM_NULL;
    public static String id;
    //地址
    public static int num;
    public static int cc;
    public static int ll;
//    public static int kk;
    public static int err;
    //代码行数
    public static int cx;
    public static int level;
    //Table表索引下标
    public static int tx;
    //指令地址
    public static int dx;
    public static char[] line;
    public static Instruction[] code = new Instruction[MAX_CODE];

    static {
        for (int i = 0; i < MAX_CODE; i++) {
            code[i] = new Instruction();
        }
    }

    public static Table[] table = new Table[MAX_TABLE_LENGTH];

    static {
        for (int i = 0; i < MAX_TABLE_LENGTH; i++) {
            table[i] = new Table();
        }
    }


    public static SymSet phi = new SymSet(SymbolType.SYM_NULL);
    public static SymSet relset = new SymSet(
            Arrays.asList(SymbolType.SYM_EQU, SymbolType.SYM_NEQ, SymbolType.SYM_LES, SymbolType.SYM_LEQ, SymbolType.SYM_GTR, SymbolType.SYM_GEQ, SymbolType.SYM_NULL)
    );
    public static SymSet declbegsys = new SymSet(
            Arrays.asList(SymbolType.SYM_CONST, SymbolType.SYM_VAR, SymbolType.SYM_PROCEDURE, SymbolType.SYM_NULL)
    );
    public static SymSet statbegsys = new SymSet(
            Arrays.asList(SymbolType.SYM_BEGIN, SymbolType.SYM_CALL, SymbolType.SYM_IF, SymbolType.SYM_WHILE, SymbolType.SYM_NULL)
    );
    public static SymSet facbegsys = new SymSet(
            Arrays.asList(SymbolType.SYM_IDENTIFIER, SymbolType.SYM_NUMBER, SymbolType.SYM_LPAREN, SymbolType.SYM_NULL)
    );

    public static SymSet set1 = new SymSet(
            Arrays.asList(SymbolType.SYM_PERIOD, SymbolType.SYM_NULL)
    );

    public static SymSet set2 = new SymSet(declbegsys){{
       addAll(statbegsys);
    }};

    public static SymSet set = new SymSet(){{
        addAll(set1);
        addAll(set2);
    }};

    public static PrintStream errorLog;

    static {
        try {
            errorLog = new PrintStream("error.log");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
