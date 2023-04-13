
import jdk.incubator.foreign.SymbolLookup;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryLayout;
import java.lang.invoke.MethodHandle;
import jdk.incubator.foreign.CLinker;
import java.lang.invoke.MethodType;
import jdk.incubator.foreign.MemoryAddress;

public class Main {
    public static void main(String...args) throws Throwable {
        FunctionDescriptor desc = FunctionDescriptor.of(
            CLinker.C_INT,
            CLinker.C_INT,
            CLinker.C_INT
        );
        
        String sym = "add";
        System.load("/path/to/libadd.dylib");
        SymbolLookup lookup = SymbolLookup.loaderLookup();
        MemoryAddress add = lookup.lookup(sym).get();
        MethodType mt = MethodType.methodType(int.class, int.class,int.class);
        MethodHandle handle = CLinker.getInstance().downcallHandle(
            add,
            mt,
            desc
        );
        Object result = handle.invoke(21,21);
        System.out.println(result);
    }
}








