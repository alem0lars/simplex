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

import org.apache.commons.lang3.mutable.MutableInt;
import org.nextreamlabs.simplex.model.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Variable with an id and a coefficient
 * The id is automatically updated; the other fields are immutable
 * (e.g. 5*x_1 is the Variable with id=1 and coefficient=5)
 */
public class SmartVariable implements Variable {

  private static final Map<MutableInt, Boolean> idsRegister;

  private BigDecimal coefficient;
  private final MutableInt id;
  private Boolean augmented;

  static {
    idsRegister = new HashMap<MutableInt, Boolean>();
  }

  // { Constructors and Factories

  private SmartVariable(MutableInt id, BigDecimal coefficient, Boolean augmented) {
    // { Preconditions
    assert (coefficient != null) : "The coefficient argument cannot be null";
    assert (augmented != null) : "The augmented argument cannot be null";
    // }

    this.coefficient = coefficient;
    this.id = id;
    this.augmented = augmented;
  }

  /**
   * Create a new variable assigning the next available id
   * @param coefficient The coefficient for the variable
   * @param augmented The variable has to be augmented or not?
   * @return The created variable
   */
  public static Variable create(BigDecimal coefficient, Boolean augmented) {
    return new SmartVariable(assignId(augmented), coefficient, augmented);
  }

  /**
   * Create a new variable using an id already assigned
   * @param coefficient The coefficient for the variable
   * @return The created variable
   */
  public static Variable createFromExistingId(Integer id, BigDecimal coefficient) {
    // { Preconditions
    assert (id != null) : "An id cannot be null";
    Boolean found = Boolean.FALSE;
    for (MutableInt assignedId : idsRegister.keySet()) {
      if (assignedId.intValue() == id) {
        found = Boolean.TRUE;
      }
    }
    assert found : "The id " + id + " doesn't exist yet";
    // }

    SmartVariable var = null; // It will be never be null after the cycle because of preconditions
    for (MutableInt assignedId : idsRegister.keySet()) {
      if (assignedId.intValue() == id) {
        var = new SmartVariable(assignedId, coefficient, idsRegister.get(assignedId));
      }
    }
    return var;
  }

  /**
   * Create a new variable
   * @param id The id for the new variable
   * @param coefficient The coefficient for the new variable
   * @param augmented The new variable is augmented or not
   * @return The created variable
   */
  public static Variable create(Integer id, BigDecimal coefficient, Boolean augmented) {
    for (MutableInt usedId : idsRegister.keySet()) {
      if (usedId.intValue() == id) {
        assert augmented.equals(idsRegister.get(usedId));
        return createFromExistingId(id, coefficient);
      }
    }
    return new SmartVariable(assignId(id, augmented), coefficient, augmented);
  }

  // }

  // { Getters and Setters

  @Override
  public Integer getId() { return this.id.intValue(); }
  @Override
  public BigDecimal getCoefficient() { return this.coefficient; }
  @Override
  public Boolean isAugmented() { return this.augmented; }

  // }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(BigDecimalUtil.toPrettyString(this.coefficient)).append(" * ");
    if (this.augmented) {
      sb.append("{");
    }
    sb.append("X_").append(this.getId());
    if (this.augmented) {
      sb.append("}");
    }

    return sb.toString();
  }

  /**
   * Assign the next available id
   * @param augmented The id to be assigned is for an augmented variable or not
   * @return The assigned id
   */
  private static MutableInt assignId(Boolean augmented) {
    // { Preconditions
    assert (augmented != null) : "The augmented argument cannot be null";
    // }

    MutableInt id = new MutableInt(0);

    if (!idsRegister.isEmpty()) { // Handle possible conflicts
      if (augmented) { // The id to be assigned is augmented
        // ==> Find the max id already assigned and set the id to be assigned as the maximum
        //     already assigned
        for (MutableInt assignedId : idsRegister.keySet()) {
          if (assignedId.compareTo(id) == 1) {
            id.setValue(assignedId.intValue());
          }
        }
        // The id to be assigned is the successive to those already assigned
        id.add(1);
      } else { // The id to be assigned isn't augmented
        // ==> Shift the assigned augmented ids to create a hole for the non-augmented id to be
        //     assigned
        for (MutableInt assignedId : idsRegister.keySet()) {
          if (idsRegister.get(assignedId)) { // The current assignedId is augmented => increment it
            assignedId.add(1);
          } else { // The current assignedId isn't augmented => update the id to be assigned
            if (assignedId.compareTo(id) == 1) {
              id.setValue(assignedId.intValue());
            }
          }
          // The id to be assigned is the successive to those non-augmented but already assigned
          id.add(1);
        }
      }
    }

    idsRegister.put(id, augmented);
    return id;
  }

  /**
   * Assign the passed id
   * @param id The id to be assigned
   * @param augmented The id to be assigned is for an augmented variable or not
   * @return The created and assigned id
   */
  private static MutableInt assignId(Integer id, Boolean augmented) {
    // { Preconditions
    assert (id != null) : "The id argument cannot be null";
    assert (augmented != null) : "The augmented argument cannot be null";
    for (MutableInt assignedId : idsRegister.keySet()) {
      assert id != assignedId.intValue() : "The id is already present";
    }
    // }

    MutableInt idToBeAssigned = new MutableInt(id);

    if (!augmented) { // The id isn't augmented
      // ==> Shift the assigned augmented ids to create a hole for the non-augmented id to be
      //     assigned
      for (MutableInt assignedId : idsRegister.keySet()) {
        if (idsRegister.get(assignedId)) { // The current assignedId is augmented
          assignedId.add(1);
        }
      }
    }

    idsRegister.put(idToBeAssigned, augmented);
    return idToBeAssigned;
  }

}
