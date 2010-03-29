package com.jetbrains.python.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyReturnStatement;

/**
 * @author yole
 */
public class PyReturnStatementImpl extends PyElementImpl implements PyReturnStatement {
  public PyReturnStatementImpl(ASTNode astNode) {
    super(astNode);
  }

  protected void acceptPyVisitor(PyElementVisitor pyVisitor) {
    pyVisitor.visitPyReturnStatement(this);
  }

  @Nullable
  public PyExpression getExpression() {
    return childToPsi(PyElementTypes.EXPRESSIONS, 0);
  }
}
