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

/**
 * Immutable type that represents a mathematical sign for constraints
 * Valid values are:
 * - Equal to (=)
 * - Less than or Equal to (<=)
 * - Greater than or Equal to (>=)
 */
public enum ConstraintSign {
  Equal("="), LessEqual("<="), GreaterEqual(">=");

  ConstraintSign(String symbol) {
    this.symbol = symbol;
  }

  private final String symbol; // The mathematical representation

  @SuppressWarnings("PublicMethodNotExposedInInterface") // An interface is not needed
  /**
   * Invert the sign for the current ConstraintSign:
   * - Equal => Equal
   * - LessEqual => GreaterEqual
   * - GreaterEqual => LessEqual
   */
  public ConstraintSign invert() {
    return this.equals(Equal) ? Equal : (this.equals(LessEqual) ? GreaterEqual : LessEqual);
  }

  @Override
  public String toString() {
    return symbol;
  }
}
