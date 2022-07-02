import java.util.Collection;
import java.util.HashSet;

public class SymSet extends HashSet<SymbolType> {
    public SymSet() {}
    public SymSet(SymbolType symbol) {
        add(symbol);
    }
    public SymSet(Collection<? extends SymbolType> c) {
        super(c);
    }
}
