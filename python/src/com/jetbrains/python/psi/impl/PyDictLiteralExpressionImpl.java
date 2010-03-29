package com.jetbrains.python.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyKeyValueExpression;
import com.jetbrains.python.psi.types.PyType;

public class PyDictLiteralExpressionImpl extends PyElementImpl implements PyDictLiteralExpression {
  private static final TokenSet KEY_VALUE_EXPRESSIONS = TokenSet.create(PyElementTypes.KEY_VALUE_EXPRESSION);

  public PyDictLiteralExpressionImpl(ASTNode astNode) {
    super(astNode);
  }

  public PyKeyValueExpression[] getElements() {
    return childrenToPsi(KEY_VALUE_EXPRESSIONS, PyKeyValueExpression.EMPTY_ARRAY);
  }

  public PyType getType() {
    return PyBuiltinCache.getInstance(this).getDictType();
  }

  @Override
  protected void acceptPyVisitor(PyElementVisitor pyVisitor) {
    pyVisitor.visitPyDictLiteralExpression(this);
  }
}
