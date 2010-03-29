package com.jetbrains.python.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.psi.PyNumericLiteralExpression;
import com.jetbrains.python.psi.types.PyType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yole
 */
public class PyNumericLiteralExpressionImpl extends PyElementImpl
    implements PyNumericLiteralExpression {
  //TOLATER: imaginary numbers
  private static final Pattern PATTERN_INT = Pattern.compile(
      "(?:"
          + "([1-9]\\d*)"
          + "|(0)"
          + "|(0[0-7]+)"
          + "|(?:0x([0-9a-f]+))"
          + ")L?", Pattern.CASE_INSENSITIVE);

  private static final Pattern PATTERN_FLOAT = Pattern.compile(
      "(" // 1
          + "(\\d+)" // 2
          + "(?:\\.(\\d+)?)?" // 3
          + "|\\.(\\d+))" // 4
          + "(e(\\+|-)?(\\d))?", // 5, 6, 7
      Pattern.CASE_INSENSITIVE);

    public PyNumericLiteralExpressionImpl(ASTNode astNode) {
        super(astNode);
    }

    @Override protected void acceptPyVisitor(PyElementVisitor pyVisitor) {
        pyVisitor.visitPyNumericLiteralExpression(this);
    }

    public Long getLongValue() {
      BigInteger value = getBigIntegerValue();
      long longValue = value.longValue();
      if (BigInteger.valueOf(longValue).equals(value)) {
        return longValue;
      } else {
        return null;
      }
    }

  public BigInteger getBigIntegerValue() {
    ASTNode node = getNode();
    String text = node.getText();
    IElementType type = node.getElementType();
    if (type == PyElementTypes.INTEGER_LITERAL_EXPRESSION) {
      return getBigIntegerValue(text);
    } else {
      return getBigDecimalValue().toBigInteger();
    }
  }

  public BigDecimal getBigDecimalValue() {
    ASTNode node = getNode();
    String text = node.getText();
    IElementType type = node.getElementType();
    if (type == PyElementTypes.INTEGER_LITERAL_EXPRESSION) {
      return new BigDecimal(getBigIntegerValue(text));
    }
    Matcher m = PATTERN_FLOAT.matcher(text);
    boolean matches = m.matches();
    assert matches;
    BigDecimal whole;
    if (m.group(2) != null) {
      whole = new BigDecimal(m.group(2));
      String fractionStr = m.group(3);
      BigDecimal fraction = BigDecimal.ZERO;
      if (fractionStr != null) {
        fraction = new BigDecimal("0." + fractionStr);
      }
      whole = whole.add(fraction);
    } else if (m.group(4) != null) {
      whole = new BigDecimal("0." + m.group(4));
    } else {
      throw new IllegalStateException("Cannot parse BigDecimal for " + text);
    }
    if (m.group(5) != null) {
      String sign = m.group(6);
      if (sign == null) sign = "+";
      String exp = m.group(7);
      whole = whole.multiply(new BigDecimal("1e" + sign + exp));
    }
    return whole;
  }

  private static @Nullable BigInteger getBigIntegerValue(String text) {
    Matcher m = PATTERN_INT.matcher(text);
    if (!m.matches()) return null;
    int radix;
    if (m.group(1) != null) {
      radix = 10;
    } else if (m.group(2) != null) {
      return BigInteger.ZERO;
    } else if (m.group(3) != null) {
      radix = 8;
    } else if (m.group(4) != null) {
      radix = 16;
    } else {
      throw new IllegalStateException("No radix found: " + text);
    }
    return new BigInteger(text, radix);
  }

  public PyType getType() {
    ASTNode node = getNode();
    IElementType type = node.getElementType();
    if (type == PyElementTypes.INTEGER_LITERAL_EXPRESSION) {
      return PyBuiltinCache.getInstance(this).getIntType();
    } else if (type == PyElementTypes.FLOAT_LITERAL_EXPRESSION) {
      return PyBuiltinCache.getInstance(this).getFloatType();
    }
    else if (type == PyElementTypes.IMAGINARY_LITERAL_EXPRESSION) {
      return PyBuiltinCache.getInstance(this).getComplexType();
    }
    return null;
  }
}
