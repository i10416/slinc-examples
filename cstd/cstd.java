
import jdk.incubator.foreign.SymbolLookup;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryLayout;
import java.lang.invoke.MethodHandle;
import jdk.incubator.foreign.CLinker;
import java.lang.invoke.MethodType;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.MemorySegment;
public class Main {
    public static void main(String...args) throws Throwable {
        FunctionDescriptor desc = FunctionDescriptor.of(
            CLinker.C_LONG,
            CLinker.C_POINTER
        );
        String sym = "strlen";
        SymbolLookup lookup = CLinker.systemLookup();
        MemoryAddress strlen = lookup.lookup(sym).get();
        MethodType mt = MethodType.methodType(long.class, MemoryAddress.class);
        MethodHandle handle = CLinker.getInstance().downcallHandle(
            strlen,
            mt,
            desc
        );
        try (ResourceScope scope = ResourceScope.newConfinedScope()) {
            MemorySegment cstr = CLinker.toCString("foo", scope);
            Object result = handle.invoke(cstr.address());
            assert ((long) result) == 3;
            System.out.println(result);
        }
    }
}
