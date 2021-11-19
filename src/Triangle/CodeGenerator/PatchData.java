package Triangle.CodeGenerator;

/* This class holds the data necessary to patch the call instruction
   to mutually recursive procedure or function declarations that are
   declared further ahead in a recursive declaration. (Austin) */

public class PatchData {

    PatchData(int frameLevel, int codeAddress) {
        FL = frameLevel;
        CA = codeAddress;
    }

    public int FL;  // Frame level
    public int CA;  // Code address
}
