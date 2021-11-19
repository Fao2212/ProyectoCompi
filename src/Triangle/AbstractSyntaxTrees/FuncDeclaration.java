/*
 * @(#)FuncDeclaration.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package Triangle.AbstractSyntaxTrees;

import java.util.LinkedList;
import java.util.Queue;
import Triangle.SyntacticAnalyzer.SourcePosition;
import Triangle.CodeGenerator.PatchData;

public class FuncDeclaration extends Declaration {

  public FuncDeclaration (Identifier iAST, FormalParameterSequence fpsAST,
  		   TypeDenoter tAST, Expression eAST,
                   SourcePosition thePosition) {
    super (thePosition);
    I = iAST;
    FPS = fpsAST;
    T = tAST;
    E = eAST;
    patchQueue = new LinkedList<PatchData>();
  }

  public Object visit (Visitor v, Object o) {
    return v.visitFuncDeclaration(this, o);
  }

  // Methods to allow mutually recursive declarations (Austin)

  // Pushes the PatchData to the waiting queue
  public void pushToPatchQueue(PatchData p) {
    patchQueue.add(p);
  }

  // Pops the PatchData from the waiting queue
  public PatchData popFromPatchQueue(PatchData p) {
    return patchQueue.remove();
  }

  public Identifier I;
  public FormalParameterSequence FPS;
  public TypeDenoter T;
  public Expression E;
  // New queue to hold the data necessary to patch calls to mutually
  // recursive function or procedure declarations
  public Queue<PatchData> patchQueue; 
}
