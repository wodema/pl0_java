

public class Instruction {
    public Mnemonic mnemonic;
    public int level;
    public int address;

    @Override
    public String toString() {
        return  mnemonic + " " + level + " " + address;
    }
}

