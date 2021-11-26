/*
 * @(#)Encoder.java                        2.1 2003/10/07
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

package Triangle.CodeGenerator;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import TAM.Instruction;
import TAM.Machine;
import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.AST;
import Triangle.AbstractSyntaxTrees.AnyTypeDenoter;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
import Triangle.AbstractSyntaxTrees.BinaryExpression;
import Triangle.AbstractSyntaxTrees.BinaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.BoolTypeDenoter;
import Triangle.AbstractSyntaxTrees.CallCommand;
import Triangle.AbstractSyntaxTrees.CallExpression;
import Triangle.AbstractSyntaxTrees.CharTypeDenoter;
import Triangle.AbstractSyntaxTrees.CharacterExpression;
import Triangle.AbstractSyntaxTrees.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.Declaration;
import Triangle.AbstractSyntaxTrees.DoUntilCommand;
import Triangle.AbstractSyntaxTrees.DoWhileCommand;
import Triangle.AbstractSyntaxTrees.DotVname;
import Triangle.AbstractSyntaxTrees.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.EmptyCommand;
import Triangle.AbstractSyntaxTrees.EmptyExpression;
import Triangle.AbstractSyntaxTrees.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.ErrorTypeDenoter;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.Identifier;
import Triangle.AbstractSyntaxTrees.IfCommand;
import Triangle.AbstractSyntaxTrees.IfExpression;
import Triangle.AbstractSyntaxTrees.InVarDecl;
import Triangle.AbstractSyntaxTrees.IntTypeDenoter;
import Triangle.AbstractSyntaxTrees.IntegerExpression;
import Triangle.AbstractSyntaxTrees.IntegerLiteral;
import Triangle.AbstractSyntaxTrees.LetCommand;
import Triangle.AbstractSyntaxTrees.LetExpression;
import Triangle.AbstractSyntaxTrees.LocalDeclaration;
import Triangle.AbstractSyntaxTrees.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Operator;
import Triangle.AbstractSyntaxTrees.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.RangeVarDecl;
import Triangle.AbstractSyntaxTrees.RecordExpression;
import Triangle.AbstractSyntaxTrees.RecordTypeDenoter;
import Triangle.AbstractSyntaxTrees.RecursiveProcFuncsDeclaration;
import Triangle.AbstractSyntaxTrees.RepeatForRange;
import Triangle.AbstractSyntaxTrees.RepeatForRangeUntil;
import Triangle.AbstractSyntaxTrees.RepeatForRangeWhile;
import Triangle.AbstractSyntaxTrees.RepeatIn;
import Triangle.AbstractSyntaxTrees.SequentialCommand;
import Triangle.AbstractSyntaxTrees.SequentialDeclaration;
import Triangle.AbstractSyntaxTrees.SequentialProcFuncDeclaration;
import Triangle.AbstractSyntaxTrees.SimpleTypeDenoter;
import Triangle.AbstractSyntaxTrees.SimpleVname;
import Triangle.AbstractSyntaxTrees.SingleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleArrayAggregate;
import Triangle.AbstractSyntaxTrees.SingleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.SingleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleRecordAggregate;
import Triangle.AbstractSyntaxTrees.SubscriptVname;
import Triangle.AbstractSyntaxTrees.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.TypeDenoter;
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.UntilCommand;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.VarInitializedDeclaration;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Vname;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;
import Triangle.SyntacticAnalyzer.SourcePosition;

public final class Encoder implements Visitor {


  // Commands
  public Object visitAssignCommand(AssignCommand ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.E.visit(this, frame);
    encodeStore(ast.V, new Frame (frame, valSize.intValue()),
		valSize.intValue());
    return null;
  }

  public Object visitCallCommand(CallCommand ast, Object o) {
    Frame frame = (Frame) o;
    Integer argsSize = (Integer) ast.APS.visit(this, frame);
    ast.I.visit(this, new Frame(frame.level, argsSize));
    return null;
  }

  public Object visitEmptyCommand(EmptyCommand ast, Object o) {
    return null;
  }

  public Object visitIfCommand(IfCommand ast, Object o) {
    Frame frame = (Frame) o;
    int jumpifAddr, jumpAddr;

    Integer valSize = (Integer) ast.E.visit(this, frame);
    jumpifAddr = nextInstrAddr;
    emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
    ast.C1.visit(this, frame);
    jumpAddr = nextInstrAddr;
    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    patch(jumpifAddr, nextInstrAddr);
    ast.C2.visit(this, frame);
    patch(jumpAddr, nextInstrAddr);
    return null;
  }

  public Object visitLetCommand(LetCommand ast, Object o) {
    Frame frame = (Frame) o;
    int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
    ast.C.visit(this, new Frame(frame, extraSize));
    if (extraSize > 0)
      emit(Machine.POPop, 0, 0, extraSize);
    return null;
  }

  public Object visitSequentialCommand(SequentialCommand ast, Object o) {
    ast.C1.visit(this, o);
    ast.C2.visit(this, o);
    return null;
  }

  public Object visitRepeatWhileCommand(WhileCommand ast, Object o) {
    Frame frame = (Frame) o;
    int jumpAddr, loopAddr;

    jumpAddr = nextInstrAddr;
    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    loopAddr = nextInstrAddr;
    ast.C.visit(this, frame);
    patch(jumpAddr, nextInstrAddr);
    ast.E.visit(this, frame);
    emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr);
    return null;
  }


  // Expressions
  public Object visitArrayExpression(ArrayExpression ast, Object o) {
    ast.type.visit(this, null);
    return ast.AA.visit(this, o);
  }

  public Object visitBinaryExpression(BinaryExpression ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.type.visit(this, null);
    int valSize1 = ((Integer) ast.E1.visit(this, frame)).intValue();
    Frame frame1 = new Frame(frame, valSize1);
    int valSize2 = ((Integer) ast.E2.visit(this, frame1)).intValue();
    Frame frame2 = new Frame(frame.level, valSize1 + valSize2);
    ast.O.visit(this, frame2);
    return valSize;
  }

  public Object visitCallExpression(CallExpression ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.type.visit(this, null);
    Integer argsSize = (Integer) ast.APS.visit(this, frame);
    ast.I.visit(this, new Frame(frame.level, argsSize));
    return valSize;
  }

  public Object visitCharacterExpression(CharacterExpression ast,
						Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.type.visit(this, null);
    emit(Machine.LOADLop, 0, 0, ast.CL.spelling.charAt(1));
    return valSize;
  }

  public Object visitEmptyExpression(EmptyExpression ast, Object o) {
    return new Integer(0);
  }

  public Object visitIfExpression(IfExpression ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize;
    int jumpifAddr, jumpAddr;

    ast.type.visit(this, null);
    ast.E1.visit(this, frame);
    jumpifAddr = nextInstrAddr;
    emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
    valSize = (Integer) ast.E2.visit(this, frame);
    jumpAddr = nextInstrAddr;
    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    patch(jumpifAddr, nextInstrAddr);
    valSize = (Integer) ast.E3.visit(this, frame);
    patch(jumpAddr, nextInstrAddr);
    return valSize;
  }

  public Object visitIntegerExpression(IntegerExpression ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.type.visit(this, null);
    emit(Machine.LOADLop, 0, 0, Integer.parseInt(ast.IL.spelling));
    return valSize;
  }

  public Object visitLetExpression(LetExpression ast, Object o) {
    Frame frame = (Frame) o;
    ast.type.visit(this, null);
    int extraSize = ((Integer) ast.D.visit(this, frame)).intValue();
    Frame frame1 = new Frame(frame, extraSize);
    Integer valSize = (Integer) ast.E.visit(this, frame1);
    if (extraSize > 0)
      emit(Machine.POPop, valSize.intValue(), 0, extraSize);
    return valSize;
  }

  public Object visitRecordExpression(RecordExpression ast, Object o){
    ast.type.visit(this, null);
    return ast.RA.visit(this, o);
  }

  public Object visitUnaryExpression(UnaryExpression ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.type.visit(this, null);
    ast.E.visit(this, frame);
    ast.O.visit(this, new Frame(frame.level, valSize.intValue()));
    return valSize;
  }

  public Object visitVnameExpression(VnameExpression ast, Object o) {
    Frame frame = (Frame) o;
    Integer valSize = (Integer) ast.type.visit(this, null);
    encodeFetch(ast.V, frame, valSize.intValue());
    return valSize;
  }


  // Declarations
  public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast,
					       Object o){
    return new Integer(0);
  }

  public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
    Frame frame = (Frame) o;
    int extraSize = 0;

    if (ast.E instanceof CharacterExpression) {
        CharacterLiteral CL = ((CharacterExpression) ast.E).CL;
        ast.entity = new KnownValue(Machine.characterSize,
                                 characterValuation(CL.spelling));
    } else if (ast.E instanceof IntegerExpression) {
        IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
        ast.entity = new KnownValue(Machine.integerSize,
				 Integer.parseInt(IL.spelling));
    } else {
      int valSize = ((Integer) ast.E.visit(this, frame)).intValue();
      ast.entity = new UnknownValue(valSize, frame.level, frame.size);
      extraSize = valSize;
    }
    writeTableDetails(ast);
    return new Integer(extraSize);
  }

  public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
    Frame frame = (Frame) o;
    int jumpAddr = nextInstrAddr;
    int argsSize = 0, valSize = 0;

    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    ast.entity = new KnownRoutine(Machine.closureSize, frame.level, nextInstrAddr);
    /* The code addresses in this ast's waiting queue are patched 
    (only for mutually recursive functions) (Austin) */
    patchRecursiveFuncDeclaration(ast, ((KnownRoutine)ast.entity).address );
    writeTableDetails(ast);
    if (frame.level == Machine.maxRoutineLevel)
      reporter.reportRestriction("can't nest routines more than 7 deep");
    else {
      Frame frame1 = new Frame(frame.level + 1, 0);
      argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
      Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
      valSize = ((Integer) ast.E.visit(this, frame2)).intValue();
    }
    emit(Machine.RETURNop, valSize, 0, argsSize);
    patch(jumpAddr, nextInstrAddr);
    return new Integer(0);
  }

  public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
    Frame frame = (Frame) o;
    int jumpAddr = nextInstrAddr;
    int argsSize = 0;

    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    ast.entity = new KnownRoutine (Machine.closureSize, frame.level,
                                nextInstrAddr);
    /* The code addresses in this ast's waiting queue are patched 
    (only for mutually recursive procedures) (Austin) */
    patchRecursiveProcDeclaration(ast, ((KnownRoutine)ast.entity).address );
    writeTableDetails(ast);
    if (frame.level == Machine.maxRoutineLevel)
      reporter.reportRestriction("can't nest routines so deeply");
    else {
      Frame frame1 = new Frame(frame.level + 1, 0);
      argsSize = ((Integer) ast.FPS.visit(this, frame1)).intValue();
      Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
      ast.C.visit(this, frame2);
    }
    emit(Machine.RETURNop, 0, 0, argsSize);
    patch(jumpAddr, nextInstrAddr);
    return new Integer(0);
  }

  public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
    Frame frame = (Frame) o;
    int extraSize1, extraSize2;

    extraSize1 = ((Integer) ast.D1.visit(this, frame)).intValue();
    Frame frame1 = new Frame (frame, extraSize1);
    extraSize2 = ((Integer) ast.D2.visit(this, frame1)).intValue();
    return new Integer(extraSize1 + extraSize2);
  }

  public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
    // just to ensure the type's representation is decided
    ast.T.visit(this, null);
    return new Integer(0);
  }

  public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast,
					      Object o) {
    return new Integer(0);
  }

  public Object visitVarDeclaration(VarDeclaration ast, Object o) {
    Frame frame = (Frame) o;
    int extraSize;

    extraSize = ((Integer) ast.T.visit(this, null)).intValue();
    emit(Machine.PUSHop, 0, 0, extraSize);
    ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
    writeTableDetails(ast);
    return new Integer(extraSize);
  }


  // Array Aggregates
  public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast,
					    Object o) {
    Frame frame = (Frame) o;
    int elemSize = ((Integer) ast.E.visit(this, frame)).intValue();
    Frame frame1 = new Frame(frame, elemSize);
    int arraySize = ((Integer) ast.AA.visit(this, frame1)).intValue();
    return new Integer(elemSize + arraySize);
  }

  public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
    return ast.E.visit(this, o);
  }


  // Record Aggregates
  public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast,
					     Object o) {
    Frame frame = (Frame) o;
    int fieldSize = ((Integer) ast.E.visit(this, frame)).intValue();
    Frame frame1 = new Frame (frame, fieldSize);
    int recordSize = ((Integer) ast.RA.visit(this, frame1)).intValue();
    return new Integer(fieldSize + recordSize);
  }

  public Object visitSingleRecordAggregate(SingleRecordAggregate ast,
					   Object o) {
    return ast.E.visit(this, o);
  }


  // Formal Parameters
  public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
    Frame frame = (Frame) o;
    int valSize = ((Integer) ast.T.visit(this, null)).intValue();
    ast.entity = new UnknownValue (valSize, frame.level, -frame.size - valSize);
    writeTableDetails(ast);
    return new Integer(valSize);
  }

  public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
    Frame frame = (Frame) o;
    int argsSize = Machine.closureSize;
    ast.entity = new UnknownRoutine (Machine.closureSize, frame.level,
				  -frame.size - argsSize);
    writeTableDetails(ast);
    return new Integer(argsSize);
  }

  public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
    Frame frame = (Frame) o;
    int argsSize = Machine.closureSize;
    ast.entity = new UnknownRoutine (Machine.closureSize, frame.level,
				  -frame.size - argsSize);
    writeTableDetails(ast);
    return new Integer(argsSize);
  }

  public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
    Frame frame = (Frame) o;
    ast.T.visit(this, null);
    ast.entity = new UnknownAddress (Machine.addressSize, frame.level,
				  -frame.size - Machine.addressSize);
    writeTableDetails(ast);
    return new Integer(Machine.addressSize);
  }


  public Object visitEmptyFormalParameterSequence(
	 EmptyFormalParameterSequence ast, Object o) {
    return new Integer(0);
  }

  public Object visitMultipleFormalParameterSequence(
 	 MultipleFormalParameterSequence ast, Object o) {
    Frame frame = (Frame) o;
    int argsSize1 = ((Integer) ast.FPS.visit(this, frame)).intValue();
    Frame frame1 = new Frame(frame, argsSize1);
    int argsSize2 = ((Integer) ast.FP.visit(this, frame1)).intValue();
    return new Integer(argsSize1 + argsSize2);
  }

  public Object visitSingleFormalParameterSequence(
	 SingleFormalParameterSequence ast, Object o) {
    return ast.FP.visit (this, o);
  }


  // Actual Parameters
  public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
    return ast.E.visit (this, o);
  }

  public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
    Frame frame = (Frame) o;
    if (ast.I.decl.entity instanceof KnownRoutine) {
      ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
      // static link, code address
      emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0);
      emit(Machine.LOADAop, 0, Machine.CBr, address.displacement);
    } else if (ast.I.decl.entity instanceof UnknownRoutine) {
      ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
      emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
           address.level), address.displacement);
    } else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
      int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
      // static link, code address
      emit(Machine.LOADAop, 0, Machine.SBr, 0);
      emit(Machine.LOADAop, 0, Machine.PBr, displacement);
    }
    return new Integer(Machine.closureSize);
  }

  public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
    Frame frame = (Frame) o;
    if (ast.I.decl.entity instanceof KnownRoutine) {
      ObjectAddress address = ((KnownRoutine) ast.I.decl.entity).address;
      // static link, code address
      emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0);
      emit(Machine.LOADAop, 0, Machine.CBr, address.displacement);
    } else if (ast.I.decl.entity instanceof UnknownRoutine) {
      ObjectAddress address = ((UnknownRoutine) ast.I.decl.entity).address;
      emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
           address.level), address.displacement);
    } else if (ast.I.decl.entity instanceof PrimitiveRoutine) {
      int displacement = ((PrimitiveRoutine) ast.I.decl.entity).displacement;
      // static link, code address
      emit(Machine.LOADAop, 0, Machine.SBr, 0);
      emit(Machine.LOADAop, 0, Machine.PBr, displacement);
    }
    return new Integer(Machine.closureSize);
  }

  public Object visitVarActualParameter(VarActualParameter ast, Object o) {
    encodeFetchAddress(ast.V, (Frame) o);
    return new Integer(Machine.addressSize);
  }


  public Object visitEmptyActualParameterSequence(
	 EmptyActualParameterSequence ast, Object o) {
    return new Integer(0);
  }

  public Object visitMultipleActualParameterSequence(
	 MultipleActualParameterSequence ast, Object o) {
    Frame frame = (Frame) o;
    int argsSize1 = ((Integer) ast.AP.visit(this, frame)).intValue();
    Frame frame1 = new Frame (frame, argsSize1);
    int argsSize2 = ((Integer) ast.APS.visit(this, frame1)).intValue();
    return new Integer(argsSize1 + argsSize2);
  }

  public Object visitSingleActualParameterSequence(
	 SingleActualParameterSequence ast, Object o) {
    return ast.AP.visit (this, o);
  }


  // Type Denoters
  public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
    return new Integer(0);
  }

  public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
    int typeSize;
    if (ast.entity == null) {
      int elemSize = ((Integer) ast.T.visit(this, null)).intValue();
      typeSize = Integer.parseInt(ast.IL.spelling) * elemSize;
      ast.entity = new TypeRepresentation(typeSize);
      writeTableDetails(ast);
    } else
      typeSize = ast.entity.size;
    return new Integer(typeSize);
  }

  public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
    if (ast.entity == null) {
      ast.entity = new TypeRepresentation(Machine.booleanSize);
      writeTableDetails(ast);
    }
    return new Integer(Machine.booleanSize);
  }

  public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
    if (ast.entity == null) {
      ast.entity = new TypeRepresentation(Machine.characterSize);
      writeTableDetails(ast);
    }
    return new Integer(Machine.characterSize);
  }

  public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
    return new Integer(0);
  }

  public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast,
					   Object o) {
    return new Integer(0);
  }

  public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
    if (ast.entity == null) {
      ast.entity = new TypeRepresentation(Machine.integerSize);
      writeTableDetails(ast);
    }
    return new Integer(Machine.integerSize);
  }

  public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
    int typeSize;
    if (ast.entity == null) {
      typeSize = ((Integer) ast.FT.visit(this, new Integer(0))).intValue();
      ast.entity = new TypeRepresentation(typeSize);
      writeTableDetails(ast);
    } else
      typeSize = ast.entity.size;
    return new Integer(typeSize);
  }


  public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast,
					      Object o) {
    int offset = ((Integer) o).intValue();
    int fieldSize;

    if (ast.entity == null) {
      fieldSize = ((Integer) ast.T.visit(this, null)).intValue();
      ast.entity = new Field (fieldSize, offset);
      writeTableDetails(ast);
    } else
      fieldSize = ast.entity.size;

    Integer offset1 = new Integer(offset + fieldSize);
    int recSize = ((Integer) ast.FT.visit(this, offset1)).intValue();
    return new Integer(fieldSize + recSize);
  }

  public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast,
					    Object o) {
    int offset = ((Integer) o).intValue();
    int fieldSize;

    if (ast.entity == null) {
      fieldSize = ((Integer) ast.T.visit(this, null)).intValue();
      ast.entity = new Field (fieldSize, offset);
      writeTableDetails(ast);
    } else
      fieldSize = ast.entity.size;

    return new Integer(fieldSize);
  }


  // Literals, Identifiers and Operators
  public Object visitCharacterLiteral(CharacterLiteral ast, Object o) {
    return null;
  }

  public Object visitIdentifier(Identifier ast, Object o) {
    Frame frame = (Frame) o;
    if (ast.decl.entity instanceof KnownRoutine) {
      ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
      emit(Machine.CALLop, displayRegister(frame.level, address.level),
	    Machine.CBr, address.displacement);
    } else if (ast.decl.entity instanceof UnknownRoutine) {
      ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
      emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
           address.level), address.displacement);
      emit(Machine.CALLIop, 0, 0, 0);
    } else if (ast.decl.entity instanceof PrimitiveRoutine) {
      int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
      if (displacement != Machine.idDisplacement)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
    } else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
      int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
      emit(Machine.LOADLop, 0, 0, frame.size / 2);
      emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
    } else if (ast.decl instanceof ProcDeclaration && ast.decl.entity == null) {

      /* The current frame level and code address (PatchData) are pushed to the declaration's patch waiting queue
         so that the call command to the yet unknown function or declaration is patched when the entry point to it
         is known (Austin) */
      ( (ProcDeclaration) ast.decl).pushToPatchQueue(new PatchData(frame.level, nextInstrAddr));

      /* A dummy call is made, it must be patched later with the entry point data of the function or declaration 
         that is declared further ahead in the code (Austin) */
      emit(Machine.CALLop, 0, Machine.CBr, 0);

    } else if (ast.decl instanceof FuncDeclaration && ast.decl.entity == null) {

      /* The current frame level and code address (PatchData) are pushed to the declaration's patch waiting queue
         so that the call command to the yet unknown function or declaration is patched when the entry point to it
         is known (Austin) */
      ( (FuncDeclaration) ast.decl).pushToPatchQueue(new PatchData(frame.level, nextInstrAddr));

      /* A dummy call is made, it must be patched later with the entry point data of the function or declaration 
         that is declared further ahead in the code (Austin) */
      emit(Machine.CALLop, 0, Machine.CBr, 0);

    }
    return null;
  }

  public Object visitIntegerLiteral(IntegerLiteral ast, Object o) {
    return null;
  }

  public Object visitOperator(Operator ast, Object o) {
    Frame frame = (Frame) o;
    if (ast.decl.entity instanceof KnownRoutine) {
      ObjectAddress address = ((KnownRoutine) ast.decl.entity).address;
      emit(Machine.CALLop, displayRegister (frame.level, address.level),
	   Machine.CBr, address.displacement);
    } else if (ast.decl.entity instanceof UnknownRoutine) {
      ObjectAddress address = ((UnknownRoutine) ast.decl.entity).address;
      emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level,
           address.level), address.displacement);
      emit(Machine.CALLIop, 0, 0, 0);
    } else if (ast.decl.entity instanceof PrimitiveRoutine) {
      int displacement = ((PrimitiveRoutine) ast.decl.entity).displacement;
      if (displacement != Machine.idDisplacement)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
    } else if (ast.decl.entity instanceof EqualityRoutine) { // "=" or "\="
      int displacement = ((EqualityRoutine) ast.decl.entity).displacement;
      emit(Machine.LOADLop, 0, 0, frame.size / 2);
      emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
    }
    return null;
  }


  // Value-or-variable names
  public Object visitDotVname(DotVname ast, Object o) {
    Frame frame = (Frame) o;
    RuntimeEntity baseObject = (RuntimeEntity) ast.V.visit(this, frame);
    ast.offset = ast.V.offset + ((Field) ast.I.decl.entity).fieldOffset;
                   // I.decl points to the appropriate record field
    ast.indexed = ast.V.indexed;
    return baseObject;
  }

  public Object visitSimpleVname(SimpleVname ast, Object o) {
    ast.offset = 0;
    ast.indexed = false;
    return ast.I.decl.entity;
  }

  public Object visitSubscriptVname(SubscriptVname ast, Object o) {
    Frame frame = (Frame) o;
    RuntimeEntity baseObject;
    int elemSize, indexSize;

    baseObject = (RuntimeEntity) ast.V.visit(this, frame);
    ast.offset = ast.V.offset;
    ast.indexed = ast.V.indexed;
    elemSize = ((Integer) ast.type.visit(this, null)).intValue();
    if (ast.E instanceof IntegerExpression) {
      IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
      ast.offset = ast.offset + Integer.parseInt(IL.spelling) * elemSize;
    } else {
      // v-name is indexed by a proper expression, not a literal
      if (ast.indexed)
        frame.size = frame.size + Machine.integerSize;
      indexSize = ((Integer) ast.E.visit(this, frame)).intValue();
      if (elemSize != 1) {
        emit(Machine.LOADLop, 0, 0, elemSize);
        emit(Machine.CALLop, Machine.SBr, Machine.PBr,
             Machine.multDisplacement);
      }
      if (ast.indexed)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      else
        ast.indexed = true;
    }
    return baseObject;
  }


  // Programs
  public Object visitProgram(Program ast, Object o) {
    return ast.C.visit(this, o);
  }

  public Encoder (ErrorReporter reporter) {
    this.reporter = reporter;
    nextInstrAddr = Machine.CB;
    elaborateStdEnvironment();
  }

  private ErrorReporter reporter;

  // Generates code to run a program.
  // showingTable is true if entity description details
  // are to be displayed.
  public final void encodeRun (Program theAST, boolean showingTable) {
    tableDetailsReqd = showingTable;
    theAST.visit(this, new Frame (0, 0));
    emit(Machine.HALTop, 0, 0, 0);
  }

  // Decides run-time representation of a standard constant.
  private final void elaborateStdConst (Declaration constDeclaration,
					int value) {

    if (constDeclaration instanceof ConstDeclaration) {
      ConstDeclaration decl = (ConstDeclaration) constDeclaration;
      int typeSize = ((Integer) decl.E.type.visit(this, null)).intValue();
      decl.entity = new KnownValue(typeSize, value);
      writeTableDetails(constDeclaration);
    }
  }

  // Decides run-time representation of a standard routine.
  private final void elaborateStdPrimRoutine (Declaration routineDeclaration,
                                          int routineOffset) {
    routineDeclaration.entity = new PrimitiveRoutine (Machine.closureSize, routineOffset);
    writeTableDetails(routineDeclaration);
  }

  private final void elaborateStdEqRoutine (Declaration routineDeclaration,
                                          int routineOffset) {
    routineDeclaration.entity = new EqualityRoutine (Machine.closureSize, routineOffset);
    writeTableDetails(routineDeclaration);
  }

  private final void elaborateStdRoutine (Declaration routineDeclaration,
                                          int routineOffset) {
    routineDeclaration.entity = new KnownRoutine (Machine.closureSize, 0, routineOffset);
    writeTableDetails(routineDeclaration);
  }

  private final void elaborateStdEnvironment() {
    tableDetailsReqd = false;
    elaborateStdConst(StdEnvironment.falseDecl, Machine.falseRep);
    elaborateStdConst(StdEnvironment.trueDecl, Machine.trueRep);
    elaborateStdPrimRoutine(StdEnvironment.notDecl, Machine.notDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.andDecl, Machine.andDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.orDecl, Machine.orDisplacement);
    elaborateStdConst(StdEnvironment.maxintDecl, Machine.maxintRep);
    elaborateStdPrimRoutine(StdEnvironment.addDecl, Machine.addDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.subtractDecl, Machine.subDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.multiplyDecl, Machine.multDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.divideDecl, Machine.divDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.moduloDecl, Machine.modDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.lessDecl, Machine.ltDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.notgreaterDecl, Machine.leDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.greaterDecl, Machine.gtDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.notlessDecl, Machine.geDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.chrDecl, Machine.idDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.ordDecl, Machine.idDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.eolDecl, Machine.eolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.eofDecl, Machine.eofDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.getDecl, Machine.getDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.putDecl, Machine.putDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.getintDecl, Machine.getintDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.putintDecl, Machine.putintDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.geteolDecl, Machine.geteolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.puteolDecl, Machine.puteolDisplacement);
    elaborateStdEqRoutine(StdEnvironment.equalDecl, Machine.eqDisplacement);
    elaborateStdEqRoutine(StdEnvironment.unequalDecl, Machine.neDisplacement);
  }

  // Saves the object program in the named file.

  public void saveObjectProgram(String objectName) {
    FileOutputStream objectFile = null;
    DataOutputStream objectStream = null;

    int addr;

    try {
      objectFile = new FileOutputStream (objectName);
      objectStream = new DataOutputStream (objectFile);

      addr = Machine.CB;
      for (addr = Machine.CB; addr < nextInstrAddr; addr++)
        Machine.code[addr].write(objectStream);
      objectFile.close();
    } catch (FileNotFoundException s) {
      System.err.println ("Error opening object file: " + s);
    } catch (IOException s) {
      System.err.println ("Error writing object file: " + s);
    }
  }

  boolean tableDetailsReqd;

  public static void writeTableDetails(AST ast) {
  }

  // OBJECT CODE

  // Implementation notes:
  // Object code is generated directly into the TAM Code Store, starting at CB.
  // The address of the next instruction is held in nextInstrAddr.

  private int nextInstrAddr;

  // Appends an instruction, with the given fields, to the object code.
  private void emit (int op, int n, int r, int d) {
    Instruction nextInstr = new Instruction();
    if (n > 255) {
        reporter.reportRestriction("length of operand can't exceed 255 words");
        n = 255; // to allow code generation to continue
    }
    nextInstr.op = op;
    nextInstr.n = n;
    nextInstr.r = r;
    nextInstr.d = d;
    if (nextInstrAddr == Machine.PB)
      reporter.reportRestriction("too many instructions for code segment");
    else {
        Machine.code[nextInstrAddr] = nextInstr;
        nextInstrAddr = nextInstrAddr + 1;
    }
  }

  // Patches the d-field of the instruction at address addr.
  private void patch (int addr, int d) {
    Machine.code[addr].d = d;
  }

  /* Patches the call instruction in the code for all the applied occurrences of 
     functions or procedures in a procedure declaration's waiting list. This
     is only necessary to allow proedures to "look ahead" for other
     procedures or functions in a mutually recursive declaration (Austin) */
  private void patchRecursiveProcDeclaration(ProcDeclaration d, ObjectAddress oa) {
      for (PatchData pd : d.patchQueue ) {
        Machine.code[pd.CA].n = displayRegister(pd.FL, oa.level);
        Machine.code[pd.CA].d = oa.displacement;
      } 
  }

  /* Patches the call instruction in the code for all the applied occurrences of 
     functions or procedures in a function declaration's waiting list. This
     is only necessary to allow proedures to "look ahead" for other
     procedures or functions in a mutually recursive declaration (Austin) */
  private void patchRecursiveFuncDeclaration(FuncDeclaration d, ObjectAddress oa) {
      for (PatchData pd : d.patchQueue ) {
        Machine.code[pd.CA].n = displayRegister(pd.FL, oa.level);
        Machine.code[pd.CA].d = oa.displacement;
      }
  }

  // DATA REPRESENTATION

  public int characterValuation (String spelling) {
  // Returns the machine representation of the given character literal.
    return spelling.charAt(1);
      // since the character literal is of the form 'x'}
  }

  // REGISTERS

  // Returns the register number appropriate for object code at currentLevel
  // to address a data object at objectLevel.
  private int displayRegister (int currentLevel, int objectLevel) {
    if (objectLevel == 0)
      return Machine.SBr;
    else if (currentLevel - objectLevel <= 6)
      return Machine.LBr + currentLevel - objectLevel; // LBr|L1r|...|L6r
    else {
      reporter.reportRestriction("can't access data more than 6 levels out");
      return Machine.L6r;  // to allow code generation to continue
    }
  }

  // Generates code to fetch the value of a named constant or variable
  // and push it on to the stack.
  // currentLevel is the routine level where the vname occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the constant or variable is fetched at run-time.
  // valSize is the size of the constant or variable's value.

  private void encodeStore(Vname V, Frame frame, int valSize) {

    RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
    // If indexed = true, code will have been generated to load an index value.
    if (valSize > 255) {
      reporter.reportRestriction("can't store values larger than 255 words");
      valSize = 255; // to allow code generation to continue
    }
    if (baseObject instanceof KnownAddress) {
      ObjectAddress address = ((KnownAddress) baseObject).address;
      if (V.indexed) {
        emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
             address.displacement + V.offset);
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        emit(Machine.STOREIop, valSize, 0, 0);
      } else {
        emit(Machine.STOREop, valSize, displayRegister(frame.level,
	     address.level), address.displacement + V.offset);
      }
    } else if (baseObject instanceof UnknownAddress) {
      ObjectAddress address = ((UnknownAddress) baseObject).address;
      emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
           address.level), address.displacement);
      if (V.indexed)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      if (V.offset != 0) {
        emit(Machine.LOADLop, 0, 0, V.offset);
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      }
      emit(Machine.STOREIop, valSize, 0, 0);
    }
  }

  // Generates code to fetch the value of a named constant or variable
  // and push it on to the stack.
  // currentLevel is the routine level where the vname occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the constant or variable is fetched at run-time.
  // valSize is the size of the constant or variable's value.

  private void encodeFetch(Vname V, Frame frame, int valSize) {

    RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
    // If indexed = true, code will have been generated to load an index value.
    if (valSize > 255) {
      reporter.reportRestriction("can't load values larger than 255 words");
      valSize = 255; // to allow code generation to continue
    }
    if (baseObject instanceof KnownValue) {
      // presumably offset = 0 and indexed = false
      int value = ((KnownValue) baseObject).value;
      emit(Machine.LOADLop, 0, 0, value);
    } else if ((baseObject instanceof UnknownValue) ||
               (baseObject instanceof KnownAddress)) {
      ObjectAddress address = (baseObject instanceof UnknownValue) ?
                              ((UnknownValue) baseObject).address :
                              ((KnownAddress) baseObject).address;
      if (V.indexed) {
        emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
             address.displacement + V.offset);
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        emit(Machine.LOADIop, valSize, 0, 0);
      } else
        emit(Machine.LOADop, valSize, displayRegister(frame.level,
	     address.level), address.displacement + V.offset);
    } else if (baseObject instanceof UnknownAddress) {
      ObjectAddress address = ((UnknownAddress) baseObject).address;
      emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level,
           address.level), address.displacement);
      if (V.indexed)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      if (V.offset != 0) {
        emit(Machine.LOADLop, 0, 0, V.offset);
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      }
      emit(Machine.LOADIop, valSize, 0, 0);
    }
  }

  // Generates code to compute and push the address of a named variable.
  // vname is the program phrase that names this variable.
  // currentLevel is the routine level where the vname occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the variable is addressed at run-time.

  private void encodeFetchAddress (Vname V, Frame frame) {

    RuntimeEntity baseObject = (RuntimeEntity) V.visit(this, frame);
    // If indexed = true, code will have been generated to load an index value.
    if (baseObject instanceof KnownAddress) {
      ObjectAddress address = ((KnownAddress) baseObject).address;
      emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level),
           address.displacement + V.offset);
      if (V.indexed)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
    } else if (baseObject instanceof UnknownAddress) {
      ObjectAddress address = ((UnknownAddress) baseObject).address;
      emit(Machine.LOADop, Machine.addressSize,displayRegister(frame.level,
           address.level), address.displacement);
      if (V.indexed)
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      if (V.offset != 0) {
        emit(Machine.LOADLop, 0, 0, V.offset);
        emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
      }
    }
  }

  /* Visitor methods that generate TAM code for the additions to the Triangle language
     that make up Traingle Ext (Austin) */

  /* Reserves space on the stack for the initialized variable by evaluating 
     the AST expression (Austin) */
  @Override
  public Object visitVarInitializedDeclaration(VarInitializedDeclaration ast, Object o) {

    // elaborate[[var I := E]]
    Frame frame = (Frame) o;
    // evaluate[[E]]
    Integer valSize = (Integer) ast.E.visit(this, frame);
    ast.entity = new KnownAddress(valSize, frame.level, frame.size);
    return new Integer(valSize);
  }

  /* Generates TAM code for a [repeat until Exp do Com end] command (Fernando) */
  @Override
  public Object visitRepeatUntilCommand(UntilCommand ast, Object o) {
    Frame frame = (Frame) o;
    int jumpAddr, loopAddr;

    //Save address to the Jump condition = EvalCond label
    jumpAddr = nextInstrAddr;
    //Jumps to the evalCond before executing the command
    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    //--------Command execution-----------
    //Saves the loop addres with the next instruction
    loopAddr = nextInstrAddr;
    ast.C.visit(this, frame);
    //Patch the jump address to the EvalCondition
    patch(jumpAddr, nextInstrAddr);
    //Evaluate the Until Expression
    ast.E.visit(this, frame);
    //Jumps to loop addres if false
    emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddr);
    return null;
  }

  @Override
  public Object visitRepeatDoWhileCommand(DoWhileCommand ast, Object o) {
    Frame frame = (Frame) o;
    int loopAddr;
    //--------Command execution-----------
    //Saves the loop addres with the next instruction
    loopAddr = nextInstrAddr;
    ast.C.visit(this, frame);
    //Evaluate the While Expression
    ast.E.visit(this, frame);
    //Jumps to loop addres if true
    emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr);
    return null;
  }

  @Override
  public Object visitRepeatDoUntilCommand(DoUntilCommand ast, Object o) {
    Frame frame = (Frame) o;
    int loopAddr;
    //--------Command execution-----------
    //Saves the loop addres with the next instruction
    loopAddr = nextInstrAddr;
    ast.C.visit(this, frame);
    //Evaluates the Until expression
    ast.E.visit(this, frame);
    //Jumps to loop addres if false
    emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, loopAddr);
    return null;
  }

  @Override
  public Object visitRepeatForRange(RepeatForRange ast, Object o) {
    Frame frame1 = (Frame) o;
    int loopAddr,jumpEvalAddr;
    Integer lastValue,controlVariable;
    // elaborate [[ Id in Exp ]]
    controlVariable = (Integer)ast.RVD.visit(this, frame1);
    // get the top value of the range from the seccond expression
    lastValue = (Integer)ast.E.visit(this, frame1);
    Frame frame2 = new Frame(frame1,lastValue+controlVariable);
    // This jump is used to evaluate de condition the first time before executing the command
    jumpEvalAddr = nextInstrAddr;
    emit(Machine.JUMPop, 0,Machine.CBr, 0);
    //--------Command execution-----------
    //Saves the loop addres with the next instruction
    loopAddr = nextInstrAddr;//Guarda la pos a la que debe volver
    ast.C.visit(this, frame2);
   //Loads to the top of the sttack the saved pos at ST[-2]
    emit(Machine.LOADop,Machine.integerSize,Machine.STr,-2*Machine.integerSize);
    //Increase the top of the stack
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.succDisplacement);
    //Store in ST[-3] the top of the stack (increased controlVariable)
    emit(Machine.STOREop,Machine.integerSize,Machine.STr,-3*Machine.integerSize);
    //Patch the value of the first jump to the EvalCond
    patch(jumpEvalAddr, nextInstrAddr);
    //Makes a copy of the 2 stored integer in stack loading them
    emit(Machine.LOADop, 2* Machine.integerSize, Machine.STr, -2* Machine.integerSize);
    //Does a less or equal comparation and store the resulting value in the stack top
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.leDisplacement);
    //Jump to loopAddr to continue the cycle
    emit(Machine.JUMPIFop,Machine.trueRep,Machine.CBr,loopAddr);
    //Pop all values from stack
    emit(Machine.POPop, 0, 0, 2);
    return null;
  }

  @Override
  public Object visitRepeatForRangeWhile(RepeatForRangeWhile ast, Object o) {
    Frame frame1 = (Frame) o;
    int loopAddr,jumpEvalAddr,whileAddr,whileCondSize;
    Integer lastValue,controlVariable;
    // elaborate [[ Id in Exp ]]
    controlVariable = (Integer)ast.RVD.visit(this, frame1);
    // get the top value of the range from the seccond expression
    lastValue = (Integer)ast.E1.visit(this, frame1);
    Frame frame2 = new Frame(frame1,lastValue+controlVariable);
    // This jump is used to evaluate de condition the first time before executing the command
    jumpEvalAddr = nextInstrAddr;
    emit(Machine.JUMPop, 0,Machine.CBr, 0);
    //--------Command execution-----------
    //Saves the loop addres with the next instruction
    loopAddr = nextInstrAddr;
    //Evaluate the while expression
    whileCondSize = (Integer)ast.E2.visit(this, frame2);
    //Saves while address for later patching = Exit label
    whileAddr = nextInstrAddr;
    //Jump to exit if while E2 evaluation is true 
    emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
    Frame frame3 = new Frame(frame2,whileCondSize);
    ast.C.visit(this, frame3);
    //Loads to the top of the sttack the saved pos at ST[-2]
    emit(Machine.LOADop,Machine.integerSize,Machine.STr,-2*Machine.integerSize);
    //Increase the top of the stack
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.succDisplacement);
    //Store in ST[-3] the top of the stack (increased controlVariable)
    emit(Machine.STOREop,Machine.integerSize,Machine.STr,-3*Machine.integerSize);
    //Patch the value of the first jump to the EvalCond
    patch(jumpEvalAddr, nextInstrAddr);
    //Makes a copy of the 2 stored integer in stack loading them
    emit(Machine.LOADop, 2* Machine.integerSize, Machine.STr, -2* Machine.integerSize);
    //Does a less or equal comparation and store the resulting value in the stack top
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.leDisplacement);
    //Jump to loopAddr to continue the cycle
    emit(Machine.JUMPIFop,Machine.trueRep,Machine.CBr,loopAddr);
    //Patch the whileAddress to the exit of the cycle
    patch(whileAddr, nextInstrAddr);
    //Pop all values from stack
    emit(Machine.POPop, 0, 0, 2);
    return null;
  }

  //Range
  @Override
  public Object visitRepeatForRangeUntil(RepeatForRangeUntil ast, Object o) {
    Frame frame1 = (Frame) o;
    int loopAddr,jumpEvalAddr,untilAddr,untilCondSize;
    Integer lastValue,controlVariable;
    // elaborate [[ Id in Exp ]]
    controlVariable = (Integer)ast.RVD.visit(this, frame1);
    // get the top value of the range from the seccond expression
    lastValue = (Integer)ast.E1.visit(this, frame1);
    Frame frame2 = new Frame(frame1,lastValue+controlVariable);
    // This jump is used to evaluate de condition the first time before executing the command
    jumpEvalAddr = nextInstrAddr;//Saves value in jumpEval for later patch = Jump to EvalCond Label
    emit(Machine.JUMPop, 0,Machine.CBr, 0);
    //--------Command execution-----------
    //Saves the loop addres with the next instruction
    loopAddr = nextInstrAddr;
    //Evaluate the until expression
    untilCondSize = (Integer)ast.E2.visit(this, frame2);
    //Saves until address for later patching = Exit label
    untilAddr = nextInstrAddr;
    //Jump to exit if until E2 evaluation is true 
    emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, 0);
    Frame frame3 = new Frame(frame2,untilCondSize);
    ast.C.visit(this, frame3);
    //Loads to the top of the sttack the saved pos at ST[-2]
    emit(Machine.LOADop,Machine.integerSize,Machine.STr,-2*Machine.integerSize);
    //Increase the top of the stack
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.succDisplacement);
    //Store in ST[-3] the top of the stack (increased controlVariable)
    emit(Machine.STOREop,Machine.integerSize,Machine.STr,-3*Machine.integerSize);
    //Patch the value of the first jump to the EvalCond
    patch(jumpEvalAddr, nextInstrAddr);
    //Makes a copy of the 2 stored integer in stack loading them
    emit(Machine.LOADop, 2* Machine.integerSize, Machine.STr, -2* Machine.integerSize);
    //Does a less or equal comparation and store the resulting value in the stack top
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.leDisplacement);
    //Jump to loopAddr to continue the cycle
    emit(Machine.JUMPIFop,Machine.trueRep,Machine.CBr,loopAddr);
    //Patch the untilAddress to the exit of the cycle
    patch(untilAddr, nextInstrAddr);
    //Pop all values from stack
    emit(Machine.POPop, 0, 0, 2);
    return null;
  }

  /* Generates TAM code for a [repeat for Id in Exp do Com end] command (Austin) */
  @Override
  public Object visitRepeatIn(RepeatIn ast, Object o) {
    Frame frame = (Frame) o;
    InVarDeclData controlVarData;
    int jumpEvalAddr, commandAddr, evalAddr;

    // elaborate [[ Id in Exp ]]
    controlVarData = (InVarDeclData) ast.IVD.visit(this, frame);

    // -------------------- COMMAND SECTION ---------------------
    Frame frame1 = new Frame (frame.level, controlVarData.declSize());
    jumpEvalAddr = nextInstrAddr;
    emit(Machine.JUMPop, 0, Machine.CBr, 0);
    commandAddr = nextInstrAddr;

    // Update de control variable for the loop
    emit(Machine.LOADop, Machine.addressSize, Machine.STr, -Machine.addressSize);    
    emit(Machine.LOADIop, controlVarData.ctrlVarSize, 0, 0);     
    emit(Machine.STOREop, controlVarData.ctrlVarSize, Machine.STr, -(2*Machine.addressSize + 2*controlVarData.ctrlVarSize)); 

    // execute [[Com]]
    ast.C.visit(this, frame1);

    // --------------- CONDITION EVALUATION SECTION ----------------

    // Update current array element displacement
    emit(Machine.LOADLop, 0, 0, controlVarData.ctrlVarSize);
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);

    // Patch the jump instruction to the evaluation section
    evalAddr = nextInstrAddr;
    patch(jumpEvalAddr, evalAddr);

    // Check if the loop has ended or not
    emit(Machine.LOADop, 2*Machine.addressSize, Machine.STr, -(2*Machine.addressSize));
    emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.geDisplacement);
    emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, commandAddr);

    // Cleanup the storage allocated by the InVarDecl
    emit(Machine.POPop, 0, 0, controlVarData.declSize());
    return null;
  }

  /* Generates TAM code to elaborate the declarations necessary for 
     a [repeat for Id in Exp do Com end] command (Austin) */
  @Override
  public Object visitInVarDecl(InVarDecl ast, Object o) {

    // elaborate [[ Id in Exp ]]
    Frame frame = (Frame) o;
    Integer arraySize, elemSize;

    // evaluate Exp
    arraySize = ((Integer) ast.E.visit(this, frame)); // Returns the size of the array expression
    ast.E.entity = new KnownAddress(arraySize, frame.level, frame.size);

    // elaborate Id 
    elemSize = (Integer) ast.T.visit(this, frame); 
    emit(Machine.PUSHop, 0, 0, elemSize); 
    ast.entity = new KnownAddress(elemSize, frame.level, frame.size + arraySize);

    if (ast.E instanceof VnameExpression) {
      // Loads the max displacement for the array
      encodeFetchAddress(((VnameExpression) ast.E).V, frame);
      emit(Machine.LOADLop, 0, 0, arraySize-elemSize);
      emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);

      // Loads the first element displacement
      encodeFetchAddress(((VnameExpression) ast.E).V, frame);     
    } 
    else {
       // Loads the max displacement for the array
      emit(Machine.LOADAop, 0, 
      displayRegister(frame.level, ((KnownAddress)ast.E.entity).address.level), 
      ((KnownAddress)ast.E.entity).address.displacement + (arraySize-elemSize));

      // Loads the first element displacement
      emit(Machine.LOADAop, 0, 
      displayRegister(frame.level, ((KnownAddress)ast.E.entity).address.level), 
      ((KnownAddress)ast.E.entity).address.displacement);
    }

    // Returns a pair with the size of the array and the size of the array element type
    return new InVarDeclData(arraySize, elemSize);
  }

  /* Visitor method for the main AST that encompases a recursive declaration, it calls the 
     body's sequential procedure-function declaration (Austin) */
  @Override
  public Object visitRecursiveProcFuncsDeclaration(RecursiveProcFuncsDeclaration ast, Object o) {
    Integer extraSize = (Integer) visitSequentialProcFuncDeclaration((SequentialProcFuncDeclaration) ast.PFD, o);
    return extraSize;
  }

  /* Visits a mutually recursive function or declaration followed by another one. This also allows
     for nested sequential recursive procedure-function declarations (Austin) */
  @Override
  public Object visitSequentialProcFuncDeclaration(SequentialProcFuncDeclaration ast, Object o) {

    Frame frame = (Frame) o;
    int extraSize1, extraSize2;

    extraSize1 = ((Integer) ast.D1.visit(this, frame)).intValue();
    Frame frame1 = new Frame (frame, extraSize1);
    extraSize2 = ((Integer) ast.D2.visit(this, frame1)).intValue();
    return new Integer(extraSize1 + extraSize2);
  }

  /* Método que recorre los bloques privado y público de una declaración local
     para asignar espacio y procesar las declaraciones allí contenidas (Austin) */
  @Override
  public Object visitLocalDeclaration(LocalDeclaration ast, Object o) {
    // elaborate [[local D1 in D2 end]]
    Frame frame = (Frame) o;
    int privateDeclSize, publicDeclSize;
    // elaborate D1
    privateDeclSize = ((Integer) ast.D1.visit(this, frame)).intValue();
    Frame frame1 = new Frame (frame, privateDeclSize);
    // elaborate D2
    publicDeclSize = ((Integer) ast.D2.visit(this, frame1)).intValue();
    return new Integer(privateDeclSize + publicDeclSize);
  }

  @Override
  public Object visitRangeVarDecl(RangeVarDecl ast, Object o) {
    Frame frame = (Frame) o;
    int extraSize = 0;
    int valSize = ((Integer) ast.E.visit(this, frame)).intValue();
    ast.entity = new KnownAddress(valSize, frame.level, frame.size);
    extraSize = valSize;
    writeTableDetails(ast);
    return new Integer(extraSize);
  }

}
