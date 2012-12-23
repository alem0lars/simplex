/**************************************************************************************************
 * Copyright (C) 2012  nextreamlabs                                                               *
 * This program is free software: you can redistribute it and/or modify                           *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 * This program is distributed in the hope that it will be useful,                                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 * You should have received a copy of the GNU General Public License                              *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.                          *
 **************************************************************************************************/

package org.nextreamlabs.simplex.model.component;

import org.nextreamlabs.simplex.model.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an immutable solution
 */
public final class DefaultImmutableSolution implements ImmutableSolution {
  private final ImmutableLinearObjfunc objfunc;
  private final Map<Integer, BigDecimal> varsValues;
  private final BigDecimal coefficient;

  // { Constructors and Factories

  private DefaultImmutableSolution(ImmutableLinearObjfunc objfunc, Map<Integer, BigDecimal> varsValues,
                                   BigDecimal coefficient) {
    // { Preconditions
    // ==> Arguments cannot be null
    assert (objfunc != null && varsValues != null && coefficient != null) :
        "Null arguments aren't allowed";
    // ==> Check varsValues must be of the same size of vars
    assert (objfunc.getSize() == varsValues.size()) : "Invalid varValues";
    // ==> Check that varsValues contains all of the variables present in vars
    for (Variable var : objfunc.getVariables()) {
      Boolean validVarId = Boolean.FALSE;
      for (Integer varId : varsValues.keySet()) {
        if (varId.equals(var.getId())) {
          validVarId = Boolean.TRUE;
          break;
        }
      }
      assert validVarId : "varValues doesn't map correctly the vars";
    }
    // }

    this.objfunc = objfunc;
    this.varsValues = new HashMap<Integer, BigDecimal>(varsValues);
    this.coefficient = coefficient;
  }

  /**
   * Create a new ImmutableSolution
   * @param objfunc The objective-function related to the solution
   * @param varsValues The values that assume the variables
   * @param coefficient The "global" coefficient for the solution
   *                    (e.g. useful for model conversion: "multiply the solution by -1")
   * @return The created ImmutableSolution
   */
  public static ImmutableSolution create(ImmutableLinearObjfunc objfunc, Map<Integer, BigDecimal> varsValues,
                                BigDecimal coefficient) {
    return new DefaultImmutableSolution(objfunc, varsValues, coefficient);
  }

  // }

  // { Getters and Setters

  @Override
  public List<Variable> getVariables() { return new ArrayList<Variable>(this.objfunc.getVariables()); }
  @Override
  public Map<Integer, BigDecimal> getVarsValues() {
    return new HashMap<Integer, BigDecimal>(this.varsValues);
  }
  @Override
  public BigDecimal getCoefficient() { return this.coefficient; }

  // }

  /**
   * Duplicate the current object
   * @param objfunc If not null, the objective-function for the new ImmutableLinearConstraint
   * @param varsValues If not null, the varsValues for the new ImmutableLinearConstraint
   * @param coefficient If not null, the coefficient for the new ImmutableLinearConstraint
   * @return The created DefaultImmutableSolution
   */
  @Override
  public ImmutableSolution duplicate(ImmutableLinearObjfunc objfunc, Map<Integer, BigDecimal> varsValues,
                            BigDecimal coefficient) {
    return DefaultImmutableSolution.create(
        ((objfunc == null) ? this.objfunc : objfunc),
        ((varsValues == null) ? this.varsValues : varsValues),
        ((coefficient == null) ? this.coefficient : coefficient));
  }

  @Override
  public ImmutableSolution removeVariable(Integer id, ImmutableLinearObjfunc objfunc) {
    Map<Integer, BigDecimal> varsValues = new HashMap<Integer, BigDecimal>(this.varsValues);
    if (varsValues.containsKey(id)) {
      varsValues.remove(id);
    }
    return DefaultImmutableSolution.create(objfunc, varsValues, this.coefficient);
  }

  /**
   * @return A new ImmutableSolution only with non-augmented variables
   */
  @Override
  public ImmutableSolution getNonAugmentedSolution() {
    List<Variable> vars = new ArrayList<Variable>(this.objfunc.getSize());

    for (Variable var : this.objfunc.getVariables()) {
      if (!var.isAugmented()) {
        vars.add(var);
      }
    }
    return DefaultImmutableSolution.create(this.objfunc.duplicate(vars), this.varsValues,
        this.coefficient);
  }

  /**
   * Compute the result
   * @return The computed result
   */
  @Override
  public BigDecimal getResult() {
    BigDecimal result = BigDecimal.ZERO;
    for (Variable var : this.objfunc.getVariables()) {
      result = result.add(
          var.getCoefficient()
              .multiply(this.varsValues.get(var.getId()))
              .multiply(this.coefficient));
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(BigDecimalUtil.toPrettyString(this.coefficient)).append(" * (");
    for (Integer i = 0; i < this.objfunc.getSize(); i++) {
      Variable var = this.objfunc.getVariables().get(i);
      BigDecimal resultValue = this.varsValues.get(var.getId()).multiply(var.getCoefficient());
      sb.append(BigDecimalUtil.toPrettyString(resultValue));
      if (!i.equals(this.objfunc.getSize() - 1)) {
        sb.append(", ");
      }
    }
    sb.append(") = ").append(BigDecimalUtil.toPrettyString(this.getResult()));

    return sb.toString();
  }
}
