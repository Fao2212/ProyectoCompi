package Triangle.CodeGenerator;

public class InVarDeclData {

    InVarDeclData(int pArraySize, int pElemSize) 
    {
        arraySize = pArraySize;
        elemSize = pElemSize;
    }

    public int declSize() {
        return arraySize + elemSize + 2;    // The two is for the displacement addresses at the stack top
    }

    public int arrayElemCount() {
        return arraySize/elemSize;
    }

    public int arraySize;  // Array expression size
    public int elemSize;  // Element size
}
