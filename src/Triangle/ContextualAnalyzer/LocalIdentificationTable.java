package Triangle.ContextualAnalyzer;

import java.util.Stack;

import javafx.util.Pair;

public class LocalIdentificationTable {
    // Atributos
    private Stack<Pair<Integer,IdEntry>> privateDeclarations, publicDeclarations;
    private Integer privateBlockLevel, publicBlockLevel;
    private boolean privateScope;

    LocalIdentificationTable() {
        privateDeclarations = new Stack<Pair<Integer,IdEntry>>();
        publicDeclarations = new Stack<Pair<Integer,IdEntry>>();
        privateBlockLevel = 0;
        publicBlockLevel = 0;
        privateScope = false;
    }

    public void beginLocal() {
        privateBlockLevel++;
        privateScope = true;
    }

    public void beginIn() {
        publicBlockLevel++;
        privateScope = false;
    }

    public void endLocal() {
        Integer level;        
        do {
            Pair<Integer, IdEntry> latestPrivateDeclaration = privateDeclarations.pop();
            level = latestPrivateDeclaration.getKey();
        } while (level == privateBlockLevel);
        privateBlockLevel--;
        // Se saca la declaracion publica
        publicDeclarations.pop();
    }

    public Integer getPrivateBlockLevel() {
        return privateBlockLevel;
    }

    public Integer getPublicBlockLevel() {
        return publicBlockLevel;
    }
    
    public Pair<Integer,IdEntry> getLatestPrivateEntry() {
        return privateDeclarations.pop();
    }

    public Pair<Integer,IdEntry> getLatestPublicEntry() {
        return publicDeclarations.pop();
    }


    public void pushToPrivateScope(IdEntry entry) {
        privateDeclarations.add(new Pair<Integer,IdEntry>(privateBlockLevel, entry));
    }

    public void pushToPublicScope(IdEntry entry) {
        publicDeclarations.add(new Pair<Integer,IdEntry>(publicBlockLevel, entry));
    }

    public void openPrivateScope() {
        privateScope = true;
    }
    public void closePrivateScope() {
        privateScope = false;
    }

    public boolean isEmpty() {
        return privateDeclarations.empty() && publicDeclarations.empty();
    }
    public boolean isPrivateScope() {
        return privateScope;
    }
    public boolean isPublicScope() {
        return !privateScope;
    }
}
