

import java.util.Arrays;

public class Parser {
    private final LexicalAnalyzer lexicalAnalyzer;

    private final Table table;

    private final Interpreter interpreter;


    public Parser(LexicalAnalyzer lexicalAnalyzer, Table table, Interpreter interpreter) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.table = table;
        this.interpreter = interpreter;
    }


    public void gen(Mnemonic mnemonic, int level, int address) {
        if (Constant.cx > Constant.MAX_CODE) {
            System.out.println("Fatal Error: Program too long.");
            System.exit(1);
        }
        Constant.code[Constant.cx].mnemonic = mnemonic;
        Constant.code[Constant.cx].level = level;
        Constant.code[Constant.cx++].address = address;
    }

    public void test(SymSet s1, SymSet s2, int n) {
        SymSet s;

        if (! s1.contains(Constant.symbol)) {
            Error.ErrorCause(n);
            s = new SymSet(s1);
            s.addAll(s2);
            while(! s.contains(Constant.symbol))
                lexicalAnalyzer.getSym();
        }
    } // test

    public void constDeclaration() {
        if (Constant.symbol == SymbolType.SYM_IDENTIFIER) {
            lexicalAnalyzer.getSym();
            if (Constant.symbol == SymbolType.SYM_EQU || Constant.symbol == SymbolType.SYM_BECOMES) {
                if (Constant.symbol == SymbolType.SYM_EQU)
                    Error.ErrorCause(1); // Found ':=' when expecting '='.
                lexicalAnalyzer.getSym();
                if (Constant.symbol == SymbolType.SYM_NUMBER) {
                    table.enter(IdType.CONSTANT);
                    lexicalAnalyzer.getSym();
                } else {
                    Error.ErrorCause(2); // There must be a number to follow '='.
                }
            } else {
                Error.ErrorCause(3); // There must be an '=' to follow the identifier.
            }
        } else {
            Error.ErrorCause(4);
        }
        // There must be an identifier to follow 'const', 'var', or 'procedure'.
    }

    public void varDeclaration() {
        if (Constant.symbol == SymbolType.SYM_IDENTIFIER) {
            table.enter(IdType.VARIABLE);
            lexicalAnalyzer.getSym();
        } else {
            Error.ErrorCause(4); // There must be an identifier to follow 'const', 'var', or 'procedure'.
        }
    }

    void factor(SymSet fsys) {
        int i;
        SymSet set = new SymSet(fsys);

        test(Constant.facbegsys, fsys, 24); // The symbol can not be as the beginning of an expression.

        while (Constant.facbegsys.contains(Constant.symbol)) {
            if (Constant.symbol == SymbolType.SYM_IDENTIFIER) {
                if ((i = table.position(Constant.id)) == 0) {
                    Error.ErrorCause(11); // Undeclared identifier.
                } else {
                    switch (Constant.table[i].idType) {
                        case CONSTANT:
                            gen(Mnemonic.LIT, 0, Constant.table[i].value);
                            break;
                        case VARIABLE:
                            gen(Mnemonic.LOD, Constant.level - Constant.table[i].level, Constant.table[i].address);
                            break;
                        case PROCEDURE:
                            Error.ErrorCause(21); // Procedure identifier can not be in an expression.
                            break;
                    } // switch
                }
                lexicalAnalyzer.getSym();
            } else if (Constant.symbol == SymbolType.SYM_NUMBER) {
                if (Constant.num > Constant.MAX_ADDRESS) {
                    Error.ErrorCause(25); // The number is too great.
                    Constant.num = 0;
                }
                gen(Mnemonic.LIT, 0, Constant.num);
                lexicalAnalyzer.getSym();
            } else if (Constant.symbol == SymbolType.SYM_LPAREN) {
                lexicalAnalyzer.getSym();
                set.add(SymbolType.SYM_RPAREN);
                set.add(SymbolType.SYM_NULL);
                expression(set);
                if (Constant.symbol == SymbolType.SYM_RPAREN) {
                    lexicalAnalyzer.getSym();
                } else {
                    Error.ErrorCause(22); // Missing ')'.
                }
            }
            set = new SymSet();
            set.add(SymbolType.SYM_NULL);
            set.add(SymbolType.SYM_LPAREN);
            test(fsys, set, 23);
        } // while
    } // factor

    public void term(SymSet fsys) {
        SymbolType mulop;
        SymSet set = new SymSet(fsys);
        set.add(SymbolType.SYM_TIMES);
        set.add(SymbolType.SYM_SLASH);
        set.add(SymbolType.SYM_NULL);
        factor(set);
        while (Constant.symbol == SymbolType.SYM_TIMES || Constant.symbol == SymbolType.SYM_SLASH) {
            mulop = Constant.symbol;
            lexicalAnalyzer.getSym();
            factor(set);
            if (mulop == SymbolType.SYM_TIMES) {
                gen(Mnemonic.OPR, 0, OprCode.OPR_MUL.ordinal());
            } else {
                gen(Mnemonic.OPR, 0, OprCode.OPR_DIV.ordinal());
            }
        } // while
    }

    public void expression(SymSet fsys) {
        SymbolType addop;
        SymSet set = new SymSet(fsys);
        set.add(SymbolType.SYM_PLUS);
        set.add(SymbolType.SYM_MINUS);
        set.add(SymbolType.SYM_NULL);

        if (Constant.symbol == SymbolType.SYM_PLUS || Constant.symbol == SymbolType.SYM_MINUS) {
            addop = Constant.symbol;
            lexicalAnalyzer.getSym();
            term(set);
            if (addop == SymbolType.SYM_MINUS) {
                gen(Mnemonic.OPR, 0, OprCode.OPR_NEG.ordinal());
            }
        } else {
            term(set);
        }

        while (Constant.symbol == SymbolType.SYM_PLUS || Constant.symbol == SymbolType.SYM_MINUS) {
            addop = Constant.symbol;
            lexicalAnalyzer.getSym();
            term(set);
            if (addop == SymbolType.SYM_PLUS) {
                gen(Mnemonic.OPR, 0, OprCode.OPR_ADD.ordinal());
            } else {
                gen(Mnemonic.OPR, 0, OprCode.OPR_MIN.ordinal());
            }
        } // while
    }

    public void condition(SymSet fsys) {
        SymbolType relop;
        SymSet set;

        if (Constant.symbol == SymbolType.SYM_ODD) {
            lexicalAnalyzer.getSym();
            expression(fsys);
            gen(Mnemonic.OPR, 0, OprCode.OPR_ODD.ordinal());
        } else {
            set = new SymSet(fsys);
            set.addAll(Constant.relset);
            expression(set);
            if (! Constant.relset.contains(Constant.symbol)) {
                Error.ErrorCause(20);
            } else {
                relop = Constant.symbol;
                lexicalAnalyzer.getSym();
                expression(fsys);
                switch (relop) {
                    case SYM_EQU:
                        gen(Mnemonic.OPR, 0, OprCode.OPR_EQU.ordinal());
                        break;
                    case SYM_NEQ:
                        gen(Mnemonic.OPR, 0, OprCode.OPR_NEQ.ordinal());
                        break;
                    case SYM_LES:
                        gen(Mnemonic.OPR, 0, OprCode.OPR_LES.ordinal());
                        break;
                    case SYM_GEQ:
                        gen(Mnemonic.OPR, 0, OprCode.OPR_GEQ.ordinal());
                        break;
                    case SYM_GTR:
                        gen(Mnemonic.OPR, 0, OprCode.OPR_GTR.ordinal());
                        break;
                    case SYM_LEQ:
                        gen(Mnemonic.OPR, 0, OprCode.OPR_LEQ.ordinal());
                        break;
                } // switch
            } // else
        } // else
    } // condition

    //////////////////////////////////////////////////////////////////////
    void statement(SymSet fsys) {
        int i, cx1, cx2;
        SymSet set1, set;

        if (Constant.symbol == SymbolType.SYM_IDENTIFIER) { // variable assignment
            if ((i = table.position(Constant.id)) == 0) {
                Error.ErrorCause(11); // Undeclared identifier.
            } else if (Constant.table[i].idType != IdType.VARIABLE) {
                Error.ErrorCause(12); // Illegal assignment.
                i = 0;
            }
            lexicalAnalyzer.getSym();
            if (Constant.symbol == SymbolType.SYM_BECOMES) {
                lexicalAnalyzer.getSym();
                expression(fsys);
            } else if (Constant.symbol == SymbolType.SYM_TIMES_EQU || Constant.symbol == SymbolType.SYM_SLASH_EQU){
                int opr = Constant.symbol == SymbolType.SYM_TIMES_EQU? OprCode.OPR_MUL.ordinal(): OprCode.OPR_DIV.ordinal();
                lexicalAnalyzer.getSym();
                set = new SymSet(fsys);
                gen(Mnemonic.LOD, Constant.level - Constant.table[i].level, Constant.table[i].address);
                expression(set);
                gen(Mnemonic.OPR, 0, opr);
            } else {
                Error.ErrorCause(13); // ':=' expected.
            }
            if (i != 0) {
                gen(Mnemonic.STO, Constant.level - Constant.table[i].level, Constant.table[i].address);
            }
        } else if (Constant.symbol == SymbolType.SYM_CALL) { // procedure call
            lexicalAnalyzer.getSym();
            if (Constant.symbol != SymbolType.SYM_IDENTIFIER) {
                Error.ErrorCause(14); // There must be an identifier to follow the 'call'.
            } else {
                if ((i = table.position(Constant.id)) != 0) {
                    Error.ErrorCause(11); // Undeclared identifier.
                } else if (Constant.table[i].idType == IdType.PROCEDURE) {
                    gen(Mnemonic.CAL, Constant.level - Constant.table[i].level, Constant.table[i].address);
                } else {
                    Error.ErrorCause(15); // A constant or variable can not be called.
                }
                lexicalAnalyzer.getSym();
            }
        } else if (Constant.symbol == SymbolType.SYM_IF) { // if statement
            lexicalAnalyzer.getSym();
            //////
            set1 = new SymSet(
                    Arrays.asList(SymbolType.SYM_THEN, SymbolType.SYM_DO, SymbolType.SYM_NULL)
            );
            set = new SymSet(fsys);
            set.addAll(set1);
            condition(set);
            if (Constant.symbol == SymbolType.SYM_THEN) {
                lexicalAnalyzer.getSym();
            } else {
                Error.ErrorCause(16); // 'then' expected.
            }
            cx1 = Constant.cx;
            gen(Mnemonic.JPC, 0, 0);
            statement(fsys);
            cx2 = Constant.cx;
            gen(Mnemonic.JMP, 0, 0);
            Constant.code[cx1].address = Constant.cx;
            if (Constant.symbol == SymbolType.SYM_ELSE) {
                lexicalAnalyzer.getSym();
                statement(fsys);
                Constant.code[cx2].address = Constant.cx;
            }
        } else if (Constant.symbol == SymbolType.SYM_BEGIN) { // block
            lexicalAnalyzer.getSym();
            set1 = new SymSet(
                    Arrays.asList(SymbolType.SYM_SEMICOLON, SymbolType.SYM_END, SymbolType.SYM_NULL)
            );
            set = new SymSet(fsys);
            set.addAll(set1);
            statement(set);
            while (Constant.symbol == SymbolType.SYM_SEMICOLON || Constant.statbegsys.contains(Constant.symbol)) {
                if (Constant.symbol == SymbolType.SYM_SEMICOLON) {
                    lexicalAnalyzer.getSym();
                } else {
                    Error.ErrorCause(10);
                }
                statement(set);
            } // while
            if (Constant.symbol == SymbolType.SYM_END) {
                lexicalAnalyzer.getSym();
            } else {
                Error.ErrorCause(17); // ';' or 'end' expected.
            }
        } else if (Constant.symbol == SymbolType.SYM_WHILE) { // while statement
            cx1 = Constant.cx;
            lexicalAnalyzer.getSym();
            set1 = new SymSet(
                    Arrays.asList(SymbolType.SYM_DO, SymbolType.SYM_NULL)
            );
            set = new SymSet(fsys);
            set.addAll(set1);
            condition(set);
            cx2 = Constant.cx;
            gen(Mnemonic.JPC, 0, 0);
            if (Constant.symbol == SymbolType.SYM_DO) {
                lexicalAnalyzer.getSym();
            } else {
                Error.ErrorCause(18); // 'do' expected.
            }
            statement(fsys);
            gen(Mnemonic.JMP, 0, cx1);
            Constant.code[cx2].address = Constant.cx;
        } else if (Constant.symbol == SymbolType.SYM_FOR) {
            lexicalAnalyzer.getSym();
            if(Constant.symbol == SymbolType.SYM_IDENTIFIER) {
                i = table.position(Constant.id);
                if (i == 0) {
                    Error.ErrorCause(11);
                } else if (Constant.table[i].idType != IdType.VARIABLE) {
                    Error.ErrorCause(12);
                    i = 0;
                }
                lexicalAnalyzer.getSym();
                if (Constant.symbol == SymbolType.SYM_BECOMES) {
                    lexicalAnalyzer.getSym();
                    set = new SymSet(fsys);
                    set1 = new SymSet(
                            Arrays.asList(SymbolType.SYM_UNTIL, SymbolType.SYM_DO, SymbolType.SYM_STEP)
                    );
                    set.addAll(set1);
                    expression(set);
                    if (i != 0) {
                        gen(Mnemonic.STO, Constant.level - Constant.table[i].level, Constant.table[i].address);
                    }
                    cx1 = Constant.cx;
                    gen(Mnemonic.JMP, 0, 0);
                    cx2 = Constant.cx;
                    if (Constant.symbol == SymbolType.SYM_STEP) {
                        lexicalAnalyzer.getSym();
                        gen(Mnemonic.LOD, Constant.level - Constant.table[i].level, Constant.table[i].address);
                        expression(set);
                        gen(Mnemonic.OPR, 0, OprCode.OPR_ADD.ordinal());
                        gen(Mnemonic.STO, Constant.level - Constant.table[i].level, Constant.table[i].address);
                        if (Constant.symbol == SymbolType.SYM_UNTIL) {
                            Constant.code[cx1].address = Constant.cx; //地址回填，跳到until这里进行比较
                            lexicalAnalyzer.getSym();
                            gen(Mnemonic.LOD, Constant.level - Constant.table[i].level, Constant.table[i].address);
                            expression(set);
                            gen(Mnemonic.OPR, 0, OprCode.OPR_LEQ.ordinal());
                            cx1 = Constant.cx;
                            gen(Mnemonic.JPC, 0, 0);
                            if (Constant.symbol == SymbolType.SYM_DO) {
                                lexicalAnalyzer.getSym();
                                statement(fsys);
                                gen(Mnemonic.JMP, 0, cx2); //跳回step
                                Constant.code[cx1].address = Constant.cx;
                            } else {
                                Error.ErrorCause(27);
                            }
                        } else {
                            Error.ErrorCause(27);
                        }
                    } else {
                        Error.ErrorCause(27);
                    }
                } else {
                    Error.ErrorCause(13);
                }
            } else {
                Error.ErrorCause(28);
            }
        }
        test(fsys, Constant.phi, 19);
    }

    public void block(SymSet fsys) {
        int cx0; // initial code index
        int block_dx;
        int savedTx;
        SymSet set1, set;

        Constant.dx = 3;
        block_dx = Constant.dx;
        Table mk = Constant.table[Constant.tx];
        mk.address = Constant.cx;
        gen(Mnemonic.JMP, 0, 0);
        if (Constant.level > Constant.MAX_LEVEL) {
            Error.ErrorCause(32); // There are too many levels.
        }
        do {
            if (Constant.symbol == SymbolType.SYM_CONST) { // constant declarations
                lexicalAnalyzer.getSym();
                do {
                    constDeclaration();
                    while (Constant.symbol == SymbolType.SYM_COMMA) {
                        lexicalAnalyzer.getSym();
                        constDeclaration();
                    }
                    if (Constant.symbol == SymbolType.SYM_SEMICOLON) {
                        lexicalAnalyzer.getSym();
                    } else {
                        Error.ErrorCause(5); // Missing ',' or ';'.
                    }
                } while (Constant.symbol == SymbolType.SYM_IDENTIFIER);
            } // if

            if (Constant.symbol == SymbolType.SYM_VAR) { // variable declarations
                lexicalAnalyzer.getSym();
                do {
                    varDeclaration();
                    while (Constant.symbol == SymbolType.SYM_COMMA) {
                        lexicalAnalyzer.getSym();
                        varDeclaration();
                    }
                    if (Constant.symbol == SymbolType.SYM_SEMICOLON) {
                        lexicalAnalyzer.getSym();
                    } else {
                        Error.ErrorCause(5); // Missing ',' or ';'.
                    }
                } while (Constant.symbol == SymbolType.SYM_IDENTIFIER);
//			block = dx;
            } // if

            while (Constant.symbol == SymbolType.SYM_PROCEDURE) { // procedure declarations
                lexicalAnalyzer.getSym();
                if (Constant.symbol == SymbolType.SYM_IDENTIFIER) {
                    table.enter(IdType.PROCEDURE);
                    lexicalAnalyzer.getSym();
                } else {
                    Error.ErrorCause(4); // There must be an identifier to follow 'const', 'var', or 'procedure'.
                }
                if (Constant.symbol == SymbolType.SYM_SEMICOLON) {
                    lexicalAnalyzer.getSym();
                } else {
                    Error.ErrorCause(5); // Missing ',' or ';'.
                }

                Constant.level++;
                savedTx = Constant.tx;
                set1 = new SymSet(
                        Arrays.asList(SymbolType.SYM_SEMICOLON, SymbolType.SYM_NULL)
                );
                set = new SymSet(fsys);
                set.addAll(set1);
                block(set);
                Constant.tx = savedTx;
                Constant.level--;

                if (Constant.symbol == SymbolType.SYM_SEMICOLON) {
                    lexicalAnalyzer.getSym();
                    set1 = new SymSet(
                            Arrays.asList(SymbolType.SYM_IDENTIFIER, SymbolType.SYM_PROCEDURE,SymbolType.SYM_NULL)
                    );
                    set = new SymSet(set1);
                    set.addAll(Constant.statbegsys);
                    test(set, fsys, 6);
                } else {
                    Error.ErrorCause(5); // Missing ',' or ';'.
                }
            } // while
            set1 = new SymSet(
                    Arrays.asList(SymbolType.SYM_IDENTIFIER, SymbolType.SYM_NULL)
            );
            set = new SymSet(set1);
            set.addAll(Constant.statbegsys);
            test(set, Constant.declbegsys, 7);
        } while (Constant.declbegsys.contains(Constant.symbol));

        Constant.code[mk.address].address = Constant.cx;
        mk.address = Constant.cx;
        cx0 = Constant.cx;
        gen(Mnemonic.INT, 0, block_dx);
        set1 = new SymSet(
                Arrays.asList(SymbolType.SYM_SEMICOLON, SymbolType.SYM_END, SymbolType.SYM_NULL, SymbolType.SYM_ELSE)
        );
        set = new SymSet(fsys);
        set.addAll(set1);
        statement(set);
        gen(Mnemonic.OPR, 0, OprCode.OPR_RET.ordinal()); // return
        test(fsys, Constant.phi, 8); // test for error: Follow the statement is an incorrect symbol.
        interpreter.listCode(cx0, Constant.cx);
    }

}
