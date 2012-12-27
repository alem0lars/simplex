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

package org.nextreamlabs.simplex.solver;

import org.nextreamlabs.simplex.model.converter.CannotConvertModelException;
import org.nextreamlabs.simplex.solver.failing_solver.FailingSolverCreator;
import org.nextreamlabs.simplex.solver.pivot_solver.PivotSolverCreator;

/**
 * A creator of solver creators
 */
public enum MetaSolverCreator implements SolverCreator {
  INVALID_SOLVER("failing_solver", ""),
  PIVOT_SOLVER("pivot_solver", "Pivot Solver");

  private final String id;
  private final String prettyName;

  MetaSolverCreator(String id, String prettyName) {
    this.id = id;
    this.prettyName = prettyName;
  }

  @Override
  public Solver create() throws CannotConvertModelException {
    SolverCreator solverCreator;

    if (this.id.equals("pivot_solver")) {
      solverCreator = new PivotSolverCreator();
    } else if (this.id.equals("failing_solver")) {
      solverCreator = new FailingSolverCreator();
    } else {
      throw new InvalidSolverCreator(this.id);
    }
    return solverCreator.create();
  }

  @Override
  public String toString() {
    return this.prettyName;
  }
}
