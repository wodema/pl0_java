import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

public class LexicalAnalyzer {

    private final SymbolType[] reservedWord = new SymbolType[Constant.MAX_RESERVED_WORD_LENGTH];

    private final SymbolType[] operator = new SymbolType[22];

    private final List<String> lines;

    private int lineIndex = 0;

    public LexicalAnalyzer(BufferedReader in) {
        lines = in.lines().collect(Collectors.toList());

        reservedWord[0] = SymbolType.SYM_BEGIN;
        reservedWord[1] = SymbolType.SYM_END;
        reservedWord[2] = SymbolType.SYM_IF;
        reservedWord[3] = SymbolType.SYM_THEN;
        reservedWord[4] = SymbolType.SYM_ELSE;
        reservedWord[5] = SymbolType.SYM_DO;
        reservedWord[6] = SymbolType.SYM_ELSE;
        reservedWord[7] = SymbolType.SYM_FOR;
        reservedWord[8] = SymbolType.SYM_UNTIL;
        reservedWord[9] = SymbolType.SYM_STEP;
        reservedWord[10] = SymbolType.SYM_RETURN;
        reservedWord[11] = SymbolType.SYM_CALL;
        reservedWord[12] = SymbolType.SYM_CONST;
        reservedWord[13] = SymbolType.SYM_VAR;
        reservedWord[14] = SymbolType.SYM_PROCEDURE;
        reservedWord[15] = SymbolType.SYM_ODD;

        operator[0] = SymbolType.SYM_PLUS;
        operator[1] = SymbolType.SYM_MINUS;
        operator[2] = SymbolType.SYM_TIMES;
        operator[3] = SymbolType.SYM_SLASH;
        operator[4] = SymbolType.SYM_EQU;
        operator[5] = SymbolType.SYM_LES;
        operator[6] = SymbolType.SYM_LEQ;
        operator[7] = SymbolType.SYM_GTR;
        operator[8] = SymbolType.SYM_GEQ;
        operator[9] = SymbolType.SYM_LPAREN;
        operator[10] = SymbolType.SYM_RPAREN;
        operator[11] = SymbolType.SYM_COMMA;
        operator[12] = SymbolType.SYM_SEMICOLON;
        operator[13] = SymbolType.SYM_PERIOD;
        operator[14] = SymbolType.SYM_BECOMES;
        operator[15] = SymbolType.SYM_TIMES_EQU;
        operator[16] = SymbolType.SYM_SLASH_EQU;
        operator[17] = SymbolType.SYM_AND;
        operator[18] = SymbolType.SYM_OR;
        operator[19] = SymbolType.SYM_NOTE;
        operator[20] = SymbolType.SYM_NOTE_LINES;
        operator[21] = SymbolType.SYM_NOTE_LINES_END;
    }

    public void note() {
        Constant.cc = Constant.ll = 0;
        Constant.symbol = SymbolType.SYM_NULL;
        getCh();
        getSym();
    }

    public void noteLines() {
        do {
            do {
                getCh();
            } while (Constant.ch != '*');
            matchNoteLineEnd();
        } while (Constant.symbol != SymbolType.SYM_NOTE_LINES_END);
        getCh();
        getSym();
    }


    public void getCh() {
        try {
            if (Constant.cc == Constant.ll) {
                String read = lines.get(lineIndex++);
                while ("".equals(read) || read == null) {
                    read = lines.get(lineIndex++);
                }
                read = read.toLowerCase() + "\n";
                Constant.ll = read.length();
                Constant.cc = 0;
                Constant.line = read.toCharArray();
//                System.out.println(Constant.cx + " " + read);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("PROGRAM INCOMPLETE");
            System.exit(1);
        }
        Constant.ch = Constant.line[Constant.cc];
        Constant.cc++;
    }


    public void getSym() {
        while(Character.isWhitespace(Constant.ch)) {
            getCh();
        }
        if (isAlpha(Constant.ch)) {
            matchReservedWord();
        } else if (isDigit(Constant.ch)) {
            matchDigit();
        } else {
            matchOperatorOrOther();
        }
    }

    private void matchNoteLineEnd() {
        getCh();
        if (Constant.ch == '/') {
            Constant.symbol = SymbolType.SYM_NOTE_LINES_END;
        }
    }
    private void matchReservedWord() {
        StringBuilder sb = new StringBuilder(Constant.MAX_ID_LENGTH);
        int location = 0;
        do {
            if (location < Constant.MAX_ID_LENGTH) {
                sb.append(Constant.ch);
                location++;
            } else {
                Error.ErrorCause(26);
                System.exit(1);
            }
            getCh();
        } while (isAlpha(Constant.ch) || isDigit(Constant.ch));
        // ?????????????????????????????????
        Constant.id = sb.toString();
        int find = 0;
        for (; find < Constant.MAX_RESERVED_WORD_LENGTH; find++) {
            if (reservedWord[find].getValue().equals(Constant.id)) {
                break;
            }
        }
        if (find != Constant.MAX_RESERVED_WORD_LENGTH) {
            Constant.symbol = reservedWord[find];
            System.out.println("?????????????????????" + Constant.symbol.getValue());
        } else {
            Constant.symbol = SymbolType.SYM_IDENTIFIER;
        }
    }
    private void matchDigit() {
        int numLength = Constant.num = 0;
        Constant.symbol = SymbolType.SYM_NUMBER;
        do
        {
            Constant.num = Constant.num * 10 + Constant.ch - '0';
            numLength++;
            getCh();
        } while (isDigit(Constant.ch));
        if (numLength > Constant.MAX_NUMBER_LENGTH) {
            Error.ErrorCause(25);
        }
    }
    private void matchOperatorOrOther() {
        switch (Constant.ch) {
            case ':':
                getCh();
                if (Constant.ch == '=') {
                    Constant.symbol = SymbolType.SYM_BECOMES; // :=
                    getCh();
                } else {
                    matchIllegalChar();     // illegal?
                }
                break;
            case '<':
                getCh();
                if (Constant.ch == '=') {
                    Constant.symbol = SymbolType.SYM_LEQ;     // <=
                    getCh();
                } else {
                    Constant.symbol = SymbolType.SYM_LES;     // <
                }
                break;
            case '>':
                getCh();
                if (Constant.ch == '=') {
                    Constant.symbol = SymbolType.SYM_GEQ;     // >=
                    getCh();
                } else {
                    Constant.symbol = SymbolType.SYM_GTR;     // >
                }
                break;
            case '!':
                getCh();
                if (Constant.ch == '=') {
                    Constant.symbol = SymbolType.SYM_NEQ;
                    getCh();
                } else {
                    matchIllegalChar();
                }
                break;
            case '|':
                getCh();
                if (Constant.ch == '|') {
                    Constant.symbol = SymbolType.SYM_OR;
                    System.out.println("?????????????????????" + Constant.symbol.getValue());
                    getCh();
                } else {
                    Constant.symbol = SymbolType.SYM_NULL;
                }
                break;
            case '*':
                getCh();
                if (Constant.ch == '=') {
                    Constant.symbol = SymbolType.SYM_TIMES_EQU;
                    System.out.println("?????????????????????" + Constant.symbol.getValue());
                    getCh();
                } else if (Constant.ch == '/') {
                    Constant.symbol = SymbolType.SYM_NOTE_LINES_END;
                    getCh();
                } else {
                    Constant.symbol = SymbolType.SYM_TIMES;
                }
                break;
            case '/':
                getCh();
                if (Constant.ch == '=') {
                    Constant.symbol = SymbolType.SYM_SLASH_EQU;
                    System.out.println("?????????????????????" + Constant.symbol.getValue());
                    getCh();
                } else if (Constant.ch == '/') {
                    Constant.symbol = SymbolType.SYM_NOTE;
                    System.out.println("?????????????????????" + Constant.symbol.getValue());
                    note();
                } else if (Constant.ch == '*') {
                    Constant.symbol = SymbolType.SYM_NOTE_LINES;
                    noteLines();
                } else {
                    Constant.symbol = SymbolType.SYM_SLASH;
                }
                break;
            default:
                int find = 0;
                for (; find < Constant.MAX_SYMBOL_LENGTH; find++) {
                    if (operator[find].getValue().equals(Constant.ch + "")) {
                        break;
                    }
                }
                if (find != Constant.MAX_SYMBOL_LENGTH) {
                    Constant.symbol = operator[find];
                    System.out.println("?????????????????????" + Constant.symbol.getValue());
                    if (Constant.symbol != SymbolType.SYM_PERIOD) {
                        getCh();
                    }
                } else {
                    matchIllegalChar();
                }

        }
    }

    private boolean isAlpha(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private void matchIllegalChar() {
        System.out.println("Fatal Error: Unknown character.");
        System.exit(1);
    }

}
