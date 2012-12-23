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

package org.nextreamlabs.simplex.solver.pivot_solver;

/**
 * Contract for a pivot solver helper
 */
public interface PivotSolverStepHelper {

  public Boolean fixModel();

  public void findInitialSolution() throws UnlimitedProblemException;
  public Boolean isSolutionOptimal();
  public Boolean checkInitialSolution();

  public Integer[] findPivot() throws UnlimitedProblemException;
  public void doPivot(Integer pivotHoriz, Integer pivotVert);

  public String baseToString();

}
