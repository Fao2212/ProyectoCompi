/*
 * @(#)IdentificationTable.java                2.1 2003/10/07
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

package Triangle.ContextualAnalyzer;

import java.util.LinkedList;
import java.util.Stack;

import Triangle.AbstractSyntaxTrees.Declaration;
import Triangle.AbstractSyntaxTrees.Terminal;

public final class IdentificationTable {

  private int level;
  private IdEntry latest;
  private Stack<IdEntry> privateScopeMarkers;
  private Stack<IdEntry> publicScopeMarkers;
  private LinkedList<String> privateIdentifiers;
  private boolean privateScopeIsOpen;
  private int nestedLocalDeclarations;

  public IdentificationTable () {
    level = 0;
    latest = null;
    privateScopeMarkers = new Stack<IdEntry>();
    publicScopeMarkers = new Stack<IdEntry>();
    privateIdentifiers = new LinkedList<String>();
    privateScopeIsOpen = false;
    nestedLocalDeclarations = -1;
  }

  // Opens a new level in the identification table, 1 higher than the
  // current topmost level.

  public void openScope () {
    level ++;
  }

  public void beginLocal() {
    privateScopeIsOpen = true;
    nestedLocalDeclarations++;
    privateScopeMarkers.add(this.latest);
  }

  public void beginIn() {
    privateScopeIsOpen = false;
    publicScopeMarkers.add(this.latest);
  }

  public void endLocal() {
    // System.out.println("End local");
    IdEntry publicScopeMarker = publicScopeMarkers.pop();
    IdEntry entry = this.latest;
    while (!entry.previous.equals(publicScopeMarker)) {
      entry = entry.previous;
    }
    // Se reconecta con el marcador del alcance privado anterior
    entry.previous = privateScopeMarkers.pop();
    nestedLocalDeclarations--;
  }

  // Closes the topmost level in the identification table, discarding
  // all entries belonging to that level.

  public void closeScope () {

    IdEntry entry, local;

    // Presumably, idTable.level > 0.
    entry = this.latest;
    while (entry.level == this.level) {
      local = entry;
      entry = local.previous;
    }
    this.level--;
    this.latest = entry;
  }

  // Makes a new entry in the identification table for the given identifier
  // and attribute. The new entry belongs to the current level.
  // duplicated is set to to true iff there is already an entry for the
  // same identifier at the current level.

  public void enter (String id, Declaration attr) {

    IdEntry entry = this.latest;
    boolean present = false, searching = true;

    // Check for duplicate entry ...  
    while (searching) {
      if (entry == null || entry.level < this.level)
        searching = false;
      else if (entry.id.equals(id)) {
        present = true;
        searching = false;
      } else
        entry = entry.previous;
    }

    attr.duplicated = present;
    // Add new entry ...
    entry = new IdEntry(id, attr, this.level, this.latest);

    // Si la declaración es local se guarda su identificador 
    // por si se necesita en un mensaje de error. (Austin)
    if (privateScopeIsOpen || nestedLocalDeclarations > 0)
      privateIdentifiers.add(id);

    this.latest = entry;
  }

  // Finds an entry for the given identifier in the identification table,
  // if any. If there are several entries for that identifier, finds the
  // entry at the highest level, in accordance with the scope rules.
  // Returns null iff no entry is found.
  // otherwise returns the attribute field of the entry found.

  public Declaration retrieve (String id) {

    IdEntry entry;
    Declaration attr = null;
    boolean present = false, searching = true;

    entry = this.latest;
    while (searching) {
      if (entry == null)
        searching = false;
      else if (entry.id.equals(id)) {
        present = true;
        searching = false;
        attr = entry.attr;
      } else
        entry = entry.previous;
    }

    return attr;
  }


  // Se verifica si el identificador pasado por parámetro
  // fue declarado en un bloque de privado de una declaración
  // local revisando la lista de identificadores privados 
  // procesados hasta el momento. (Austin)

  public boolean isPrivateIdentifier(Terminal terminal) {
    return privateIdentifiers.contains(terminal.spelling);
  }
}
