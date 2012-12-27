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

package org.nextreamlabs.simplex.model;

import org.nextreamlabs.simplex.model.component.*;
import org.nextreamlabs.simplex.model.converter.CannotConvertModelException;
import org.nextreamlabs.simplex.model.converter.CanonicalModelConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a canonical linear model
 */
public final class CanonicalLinearModel extends GeneralLinearModel {

  // { Constructors and Factories

  /**
   * Never call this method. If you need to create a new CanonicalLinearModel, use the factory
   * method instead, which also converts the arguments to create a canonical linear model
   * which is valid.
   * @param constraints The constraints
   * @param objfunc The objective-function
   * @param objective The objective for the model (e.g. minimize)
   */
  private CanonicalLinearModel(List<ImmutableLinearConstraint> constraints, ImmutableLinearObjfunc objfunc,
                             Objective objective, List<ImmutableSolution> solutionHistory) {
    super(constraints, objfunc, objective, solutionHistory);
  }

  /**
   * Create a new valid canonical linear model
   * @param constraints The constraints
   * @param objfunc The objective-function
   * @param objective The objective for the model (e.g. minimize)
   * @return The created model
   */
  public static LinearModel create(List<ImmutableLinearConstraint> constraints,
                                   ImmutableLinearObjfunc objfunc, Objective objective,
                                   List<ImmutableSolution> solutionHistory) {
    return new CanonicalLinearModel(constraints, objfunc, objective, solutionHistory);
  }

  /**
   * Convert the linear model passed as argument into a canonical linear model
   * @param model The model to be converted
   * @return The new model as canonical linear model equivalent to the model argument
   */
  public static LinearModel create(LinearModel model) {
    return CanonicalLinearModel.create(model.getConstraints(), model.getObjfunc(),
        model.getObjective(), model.getSolutionHistory());
  }

  // }

  @Override
  public LinearModel duplicate(List<ImmutableLinearConstraint> constraints,
                               ImmutableLinearObjfunc objfunc, Objective objective,
                               List<ImmutableSolution> solutionHistory) {
    LinearModel model = super.duplicate(constraints, objfunc, objective, solutionHistory);
    return CanonicalLinearModel.create(model);
  }

  /**
   * Check if the current model is a valid canonical linear model
   * @return Valid or not
   */
  @Override
  public Boolean isValid() {
    // ==> The objective must be minimization
    if (!this.objective.equals(Objective.MIN)) {
      return Boolean.FALSE;
    }
    Integer mandatorySize = null;
    if (this.constraints.size() > 0) {
      mandatorySize = this.constraints.get(0).getSize();
    }
    for (ImmutableLinearConstraint constraint : this.constraints) {
      // ==> All of the constraints must be disequations
      if (!constraint.getSign().equals(ConstraintSign.Equal)) {
        return Boolean.FALSE;
      }
      // ==> All of the constant-terms must be positive
      if (BigDecimalUtil.compare(constraint.getConstantTerm(), BigDecimal.ZERO) == -1) {
        return Boolean.FALSE;
      }
      // ==> All of the constraints must have the same size
      if (!constraint.getSize().equals(mandatorySize)) {
        return Boolean.FALSE;
      }
    }

    return Boolean.TRUE;
  }

}
