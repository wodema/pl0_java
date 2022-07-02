
public class Table {

    public String name = "";
    public IdType idType;
    public int value;

    public int level;

    public int address;

    public void enter(IdType idType) {
        Constant.tx++;
        Constant.table[Constant.tx].name = Constant.id;
        Constant.table[Constant.tx].idType = idType;
        switch (idType) {
            case CONSTANT:
                if (Constant.num > Constant.MAX_ADDRESS) {
                    Error.ErrorCause(25);
                    Constant.num = 0;
                }
                Constant.table[Constant.tx].value = Constant.num;
                break;
            case VARIABLE:
                Constant.table[Constant.tx].level = Constant.level;
                Constant.table[Constant.tx].address = Constant.dx++;
                break;
            case PROCEDURE:
                Constant.table[Constant.tx].level = Constant.level;
                break;
        }
    }

    public int position(String id) {
        for (int i = 0; i < Constant.table.length; i++) {
            if (Constant.table[i].name.equals(id)) {
                return i;
            }
        }
        return 0;
    }

}


enum IdType {
    CONSTANT, VARIABLE, PROCEDURE
}
