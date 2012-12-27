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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * It's the default implementation of an immutable objective-function
 */
public class DefaultImmutableLinearObjfunc implements ImmutableLinearObjfunc {

  private final List<Variable> vars;

  // { Constructors and Factories

  private DefaultImmutableLinearObjfunc(List<Variable> vars) {
    // { Preconditions
    assert (vars != null) : "The vars cannot be null";
    // }

    this.vars = vars;
  }

  public static ImmutableLinearObjfunc create(List<Variable> vars) {
    return new DefaultImmutableLinearObjfunc(vars);
  }
  public static ImmutableLinearObjfunc create() {
    return new DefaultImmutableLinearObjfunc(new ArrayList<Variable>());
  }

  // }

  @Override
  public ImmutableLinearObjfunc duplicate(List<Variable> vars) {
    return DefaultImmutableLinearObjfunc.create((vars == null) ? this.vars : vars);
  }

  /**
   * Find and return the variable with the id passed as argument
   * @param id The id to be found
   * @return The found variable or null if no variables have the id
   */
  @Override
  public Variable findVariableById(Integer id) {
    for (Variable var : this.vars) {
      if (var.getId().equals(id)) {
        return var;
      }
    }
    return null;
  }

  // { Getters and Setters

  @Override
  public Integer getSize() {
    return this.vars.size();
  }

  @Override
  public List<Variable> getVariables() {
    return new ArrayList<Variable>(this.vars);
  }

  // }

  /**
   * Multiply the current objective-function variables coefficients by the provided
   * scalar
   * @param scalar Value to be multiplied
   * @return The new ImmutableLinearObjfunc as the result of the multiplication
   */
  @Override
  public ImmutableLinearObjfunc multiply(BigDecimal scalar) {
    // { Preconditions
    assert (scalar != null) : "The scalar cannot be null";
    // }

    List<Variable> resultVars = new ArrayList<Variable>();
    for (Variable var : this.vars) {
      resultVars.add(
          SmartVariable.createFromExistingId(var.getId(), var.getCoefficient().multiply(scalar)));
    }

    return DefaultImmutableLinearObjfunc.create(resultVars);
  }

  /**
   * Add the provided variable in the current objective-function
   * @param var Variable to be added
   * @return The result of the addition of the provided variable into the current objective-function
   */
  @Override
  public ImmutableLinearObjfunc addVariable(Variable var) {
    List<Variable> vars = new ArrayList<Variable>(this.vars);
    vars.add(var);
    return DefaultImmutableLinearObjfunc.create(vars);
  }

  /**
   * Remove a variable from the current objective-function
   * @param id The id of the variable to be removed
   * @return The result of the removal
   */
  @Override
  public ImmutableLinearObjfunc removeVariable(Integer id) {
    Variable var = this.findVariableById(id);
    List<Variable> vars = new ArrayList<Variable>(this.vars);
    if (var != null) {
      vars.remove(var);
    }
    return DefaultImmutableLinearObjfunc.create(vars);
  }

  /**
   * Change the coefficient of a variable
   * @param id The id of the variable to be changed
   * @param coefficient The new coefficient for the variable
   * @return The result of the changement
   */
  @Override
  public ImmutableLinearObjfunc setVariable(Integer id, BigDecimal coefficient) {
    // { Preconditions
    Boolean found = Boolean.FALSE;
    for (Variable var : this.vars) {
      if (var.getId().equals(id)) {
        found = Boolean.TRUE;
      }
    }
    assert found : "Wrong id argument";
    // }

    List<Variable> vars = new ArrayList<Variable>(this.vars);
    for (Integer i = 0; i < vars.size(); i++) {
      if (vars.get(i).getId().equals(id)) {
        vars.set(i, SmartVariable.createFromExistingId(id, coefficient));
      }
    }
    return DefaultImmutableLinearObjfunc.create(vars);
  }

  @Override
  public Boolean hasVariableWithId(Integer id) {
    for (Variable var : this.vars) {
      if (var.getId().equals(id)) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (Integer i = 0; i < this.vars.size(); i++) {
      sb.append(this.vars.get(i).toString());
        if (!i.equals(this.vars.size() - 1)) {
          sb.append(" + ");
        }
    }

    return sb.toString();
  }

}
