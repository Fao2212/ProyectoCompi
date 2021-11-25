package Triangle.CodeGenerator;

public class InVarDeclData {

    InVarDeclData(int pArraySize, int pElemSize) 
    {
        arraySize = pArraySize;
        ctrlVarSize = pElemSize;
    }

    public int declSize() {
        return arraySize + ctrlVarSize + 2;    // The two is for the displacement addresses at the stack top
    }

    public int arrayElemCount() {
        return arraySize/ctrlVarSize;
    }

    public int arraySize;  // Array expression size
    public int ctrlVarSize;  // Control variable size
}
