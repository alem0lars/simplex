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
import java.util.List;
import java.util.Map;

/**
 * User: Alessandro Molari (molari.alessandro@gmail.com)
 */
public interface ImmutableSolution {
  public ImmutableSolution duplicate(ImmutableLinearObjfunc objfunc, Map<Integer, BigDecimal> varsValues,
                                                     BigDecimal coefficient);

  public ImmutableSolution removeVariable(Integer id, ImmutableLinearObjfunc objfunc);

  public ImmutableSolution getNonAugmentedSolution();
  public BigDecimal getResult();

  public Map<Integer, BigDecimal> getVarsValues();
  public List<Variable> getVariables();
  public BigDecimal getCoefficient();
}
