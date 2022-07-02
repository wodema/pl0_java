import java.io.*;

public class Compiler {

    public static void main(String[] args) {
        String filename = "wordTest.pl0";
        try {
            BufferedReader fin = new BufferedReader(new FileReader(filename));
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fin);
            Table table = new Table();
            Interpreter interpreter = new Interpreter();
            Parser parser = new Parser(lexicalAnalyzer, table, interpreter);
            lexicalAnalyzer.getSym();
            compile(parser, interpreter);
        } catch (IOException e) {
            System.out.println("Compile failed!");
        }
    }

    public static void compile(Parser parser, Interpreter interpreter) {
        parser.block(Constant.set);
        if (Constant.symbol != SymbolType.SYM_PERIOD) {
            Error.ErrorCause(9);
        }
        if (Constant.err != 0) {
            System.out.printf("There are %d error(s) in PL/0 program.\n", Constant.err);
        } else {
            interpreter.interpret();
            try (FileWriter fileWriter = new FileWriter("hbin.txt")) {
                for (int i = 0; i < Constant.cx; i++) {
                    Instruction instruction = Constant.code[i];
                    fileWriter.write(instruction.toString() + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        interpreter.listCode(0, Constant.cx);
    }
}
