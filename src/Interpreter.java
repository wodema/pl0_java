public class Interpreter {

    public int base(int[] stack, int currentLevel, int levelDiff) {
        int b = currentLevel;

        while (levelDiff > 0) {
            b = stack[b];
            levelDiff--;
        }
        return b;
    } // base

    // interprets and executes codes.
    void interpret() {
        int pc;        // program counter
        int[] stack = new int[Constant.MAX_STACK_SIZE]; // stack
        int top;       // top of stack
        int b;         // program, base, and top-stack register
        Instruction i; // instruction register

        System.out.println("Begin executing PL/0 program.");
        pc = 0;
        b = 1;
        top = 3;
        stack[1] = stack[2] = stack[3] = 0;
        do {
            i = Constant.code[pc++];
            switch (i.mnemonic) {
                case LIT:
                    stack[++top] = i.address;
                    break;
                case OPR:
                    switch (i.address) // operator
                    {
                        case 0:
                            top = b - 1;
                            pc = stack[top + 3];
                            b = stack[top + 2];
                            break;
                        case 1:
                            stack[top] = -stack[top];
                            break;
                        case 2:
                            top--;
                            stack[top] += stack[top + 1];
                            break;
                        case 3:
                            top--;
                            stack[top] -= stack[top + 1];
                            break;
                        case 4:
                            top--;
                            stack[top] *= stack[top + 1];
                            break;
                        case 5:
                            top--;
                            if (stack[top + 1] == 0) {
                                Constant.errorLog.println("Runtime Error: Divided by zero.");
                                Constant.errorLog.println("PL/0 program terminated.");
                                System.exit(1);
                            }
                            stack[top] /= stack[top + 1];
                            break;
                        case 6:
                            stack[top] %= 2;
                            break;
                        case 7:
                            top--;
                            stack[top] = (stack[top] == stack[top + 1])? 1: 0;
                            break;
                        case 8:
                            top--;
                            stack[top] = (stack[top] != stack[top + 1])? 1: 0;
                            break;
                        case 9:
                            top--;
                            stack[top] = (stack[top] < stack[top + 1])? 1: 0;
                            break;
                        case 10:
                            top--;
                            stack[top] = (stack[top] >= stack[top + 1])? 1: 0;
                            break;
                        case 11:
                            top--;
                            stack[top] = (stack[top] > stack[top + 1])? 1: 0;
                            break;
                        case 12:
                            top--;
                            stack[top] = (stack[top] <= stack[top + 1])? 1: 0;
                            break;
                    } // switch
                    break;
                case LOD:
                    stack[++top] = stack[base(stack, b, i.level) + i.address];
                    break;
                case STO:
                    stack[base(stack, b, i.level) + i.address] = stack[top];
                    System.out.println(stack[top]);
                    top--;
                    break;
                case CAL:
                    stack[top + 1] = base(stack, b, i.level);
                    // generate new block mark
                    stack[top + 2] = b;
                    stack[top + 3] = pc;
                    b = top + 1;
                    pc = i.address;
                    break;
                case INT:
                    top += i.address;
                    break;
                case JMP:
                    pc = i.address;
                    break;
                case JPC:
                    if (stack[top] == 0)
                        pc = i.address;
                    top--;
                    break;
            } // switch
        } while (pc != 0);
        System.out.println("End executing PL/0 program.");
    } // interpret

    void listCode(int from, int to) {
        int i;

        System.out.println();
        for (i = from; i < to; i++) {
            Instruction instruction = Constant.code[i];
            System.out.printf("%5d %s\t%d\t%d\n", i, instruction.mnemonic.name(), instruction.level, instruction.address);
        }
        System.out.println();
    } // listcode
}
