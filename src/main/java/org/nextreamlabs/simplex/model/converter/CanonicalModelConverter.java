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

package org.nextreamlabs.simplex.model.converter;

import org.nextreamlabs.simplex.command.Command;
import org.nextreamlabs.simplex.model.LinearModel;
import org.nextreamlabs.simplex.model.component.*;
import org.nextreamlabs.simplex.ui.Ui;
import org.nextreamlabs.simplex.ui.command.SetConstraintConstantTermCommand;
import org.nextreamlabs.simplex.ui.command.SetConstraintSignCommand;
import org.nextreamlabs.simplex.ui.command.SetConstraintVariableCommand;
import org.nextreamlabs.simplex.ui.command.SetObjfuncVariableCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert a linear model into a canonical linear model
 */
public class CanonicalModelConverter implements ModelConverter {
  private LinearModel model;

  // { Constructors and Factories

  private CanonicalModelConverter(LinearModel model) {
    // { Preconditions
    assert (model != null) : "The model argument cannot be null";
    // }

    this.model = model;
  }

  public static ModelConverter create(LinearModel model) {
    return new CanonicalModelConverter(model);
  }

  // }

  /**
   * Convert from the model passed in the construction phase into a canonical linear model.
   * The solutions history is reset and only the current one will be kept
   * @return The new canonical linear model equivalent to the model field
   * @throws CannotConvertModelException
   */
  @Override
  public List<Command<Ui>> convert() throws CannotConvertModelException {
    List<Command<Ui>> commands = new ArrayList<Command<Ui>>();

    ImmutableLinearObjfunc objfunc = this.model.getObjfunc();
    ImmutableSolution solution = this.model.getCurrentSolution();
    List<ImmutableSolution> solutionHistory = new ArrayList<ImmutableSolution>();
    List<ImmutableLinearConstraint> constraints = this.model.getConstraints();

    // ==> Convertion based on the objective (it must be minimization in a standard linear model)
    if (!this.model.getObjective().equals(Objective.MIN)) {
      objfunc = objfunc.multiply(BigDecimal.valueOf(-1));
      if (solution != null) {
        solution = DefaultImmutableSolution.create(objfunc, solution.getVarsValues(),
            solution.getCoefficient().multiply(BigDecimal.valueOf(-1)));
      }
    }
    // ==> Convertion based on the constraints
    for (int i = 0; i < constraints.size(); i++) {
      // ==> All of the constant-terms must be positive
      ImmutableLinearConstraint constraint = constraints.get(i);
      if (constraint.getConstantTerm().compareTo(BigDecimal.ZERO) == -1) {
        constraint = constraint.multiply(BigDecimal.valueOf(-1));
      }
      // ==> All of the constraints must be disequations
      if (constraint.getSign().equals(ConstraintSign.Equal)) {
        constraints.set(i, constraint.duplicate(null, ConstraintSign.LessEqual, null));
        constraints.add(constraint.duplicate(null, ConstraintSign.GreaterEqual, null));
      }
    }

    solutionHistory.add(solution);

    this.model.setObjfunc(objfunc);
    this.model.setConstraints(constraints);
    this.model.setObjective(Objective.MIN);
    this.model.setSolutionHistory(solutionHistory);

    // { Create the commands
    for (Integer idx = 0; idx < constraints.size(); idx++) {
      ImmutableLinearConstraint constraint = constraints.get(idx);
      for (Variable var : constraint.getVariables()) {
        commands.add(new SetConstraintVariableCommand(idx, var.getId(), var.getCoefficient()));
      }
      commands.add(new SetConstraintSignCommand(idx, constraint.getSign()));
      commands.add(new SetConstraintConstantTermCommand(idx, constraint.getConstantTerm()));
    }

    for (Variable var : objfunc.getVariables()) {
      commands.add(new SetObjfuncVariableCommand(var.getId(), var.getCoefficient()));
    }
    // }

    return commands;
  }

  @Override
  public LinearModel getModel() { return this.model; }
}
