/*
 * Copyright (c) 1998 The Regenstrief Institute.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * Written by Gunther Schadow.
 *
 * $Id: UnitAtom.java,v 1.5 2007/02/27 17:35:12 sittha Exp $
 */

package edu.iupui.rg.ucum.units;

import java.lang.*;
import java.io.*;
import java.util.*;

/** This class is a specialization of <CODE>class Unit</CODE>. Every
 * instance of this class is stored in a static table or
 * database. Definition and retreival of unit atoms are performed by
 * static member functions. This class is rarely used directly by the
 * programmer but is essential for the unit parser.
 *
 * @author Gunther Schadow
 * @version $Id: UnitAtom.java,v 1.5 2007/02/27 17:35:12 sittha Exp $
 * */
public class UnitAtom
        extends Unit {
    private boolean metric = false;
    public static final boolean METRIC = true;
    public static final boolean NONMETRIC = false;

    private static Hashtable all = new Hashtable();
    private static Hashtable lla = new Hashtable();
    private static Hashtable categorizedUnits = new Hashtable();

    static void reset() {
        all = new Hashtable();
        lla = new Hashtable();
    }

    /** Defines a new UnitAtom name as &lt;nu, u_vec&gt;.
     *
     * @throws IllegalArgumentException  if the unit already exists.
     * */
    private UnitAtom(String _name, double _nu, Dimension _u_vec,
                     Function _cnv, double _cnv_pfx, boolean _metric, String _description, String _category) {
        super(_name, _nu, _u_vec, _cnv, _cnv_pfx, _description, _category);
        metric = _metric;

        // System.out.print("define atom "); dump();

        // never give handles to the hashtable out, we have to archive a clone
        UnitAtom u = (UnitAtom) all.put(name, clone());

        if (u != null) {
            all.put(name, u);
            throw new IllegalArgumentException("unit `" + name + "' already defined");
        }

        if (cnv == null && nu == 1) // coherent units are hashable from their dimensions
            lla.put(u_vec.toString(), this);
    }

    public UnitAtom(String _name, double _nu, Dimension _u_vec,
                    Function _cnv, double _cnv_pfx, String _description, String _category) {
        this(_name, _nu, _u_vec, _cnv, _cnv_pfx,
                (_cnv == null ? METRIC : NONMETRIC), _description, _category);
    }

    public UnitAtom(String _name, double _nu, Dimension _u_vec, boolean _metric) {
        this(_name, _nu, _u_vec, null, 1, _metric, "", "");
    }

    public UnitAtom(String _name, double _nu, Dimension _u_vec, String _description, String _category) {
        this(_name, _nu, _u_vec, null, 1, METRIC, _description, _category);
    }

    private UnitAtom() {
        super();
    }

    /** Define a new UnitAtom name as &lt;nu, u_vec&gt;.
     *
     * @throws IllegalArgumentException  if the unit already exists.
     * */
    public UnitAtom(String _name, Unit u) {
        this(_name, u.nu, u.u_vec, u.cnv, u.cnv_pfx,
                (u instanceof UnitAtom ? ((UnitAtom) u).metric :
                (u.cnv == null ? METRIC : NONMETRIC)), u.description, u.category);
    }

    public UnitAtom(String _name, Unit u, Function _cnv, double _cnv_pfx, String _description, String _category) {
        this(_name, u.nu, u.u_vec,
                (u.cnv == null ? _cnv :
                (_cnv != null ? hick_hack(_name, u.name) : u.cnv)),
                (u.cnv != null ? u.cnv_pfx :
                (_cnv != null ? _cnv_pfx : 1)), _description, _category);
    }

    /** This is here in order to cheat Java on what can be done before
     *  calling self-constructors */
    private static Function hick_hack(String n1, String n2) {
        throw new IllegalArgumentException("non-ratio unit" + n1 +
                "defined in terms of other non-ratio unit" + n2);
    }

    public UnitAtom(String _name, Unit u,
                    Function _cnv, double _cnv_pfx,
                    boolean _metric, String _description, String _category) {
        this(_name, u.nu, u.u_vec,
                (u.cnv == null ? _cnv :
                (_cnv != null ? hick_hack(_name, u.name) : u.cnv)),
                (u.cnv != null ? u.cnv_pfx :
                (_cnv != null ? _cnv_pfx : 1)), _metric, _description, _category);
    }

    /** Instantiate a known UnitAtom from its name;.
     *
     * @throws IllegalArgumentException  if the name is unknown.
     * */
    public UnitAtom(String s) {
        super();

        if (!caseSensitive())
            s = s.toUpperCase();

        UnitAtom u = (UnitAtom) all.get(s);

        // System.out.print("unit atom found: "); u.dump();

        if (u == null)
            throw new IllegalArgumentException("unit `" + s + "' is unknown");

        assign(u);
    }

    public Unit assign(Unit u) {
        super.assign(u);
        if (u instanceof UnitAtom)
            metric = ((UnitAtom) u).metric;
        else
            metric = true;
        return this;
    }

    public Object clone() {
        UnitAtom that = new UnitAtom();
        that.assign(this);
        return that;
    }

    /** Tests if the units atom name is defined. */
    public static boolean isDefined(String s) {
        if (!Unit.caseSensitive())
            s = s.toUpperCase();

        // System.out.println("UnitAtom.isDefined(" + s + ") returns " + all.containsKey(s));

        return all.containsKey(s);
    }

    /** Tests if the units atom is metric. */
    public boolean isMetric() {
        return metric;
    }

    /** Get a coherent Unit for dimension d
     * @return the unit atom or null if no such atom defined
     **/
    public static UnitAtom forDimension(Dimension d) {
        return (UnitAtom) lla.get(d.toString());
    }

    public void dump() {
        System.out.print("unit " + name + " <" + nu + ", " + u_vec.toString() + ">");
        if (cnv != null)
            System.out.print(" " + cnv.name);
        if (metric)
            System.out.println(" metric");
        else
            System.out.println(" non-metric");
    }

    /** Saves all static data (i.e. table of units all and lla) */
    static void save(ObjectOutput oos)
            throws IOException {
        oos.writeObject(all);
        oos.writeObject(lla);
    }

    /** Loads static data (i.e. table of units all and lla) */
    static void load(ObjectInput ois)
            throws IOException, ClassNotFoundException {
        all = (Hashtable) ois.readObject();
        lla = (Hashtable) ois.readObject();
    }

    //get all unit objects
    public static Enumeration elements() {
        return all.elements();
    }

    //get all units' name(abbreviation)
    public static Enumeration keys() {
        return all.keys();
    }

    //get all unit objects
    public static Collection getAllUnits() {
        return all.values();
    }

    //get a unit object according to its name(abbreviation)
    public static UnitAtom getUnit(String key) {
        return (UnitAtom) all.get(key);
    }

    //get the description of one unit according to its name(abbreviation)
    public static String getUnitDescription(String key) {
        UnitAtom atom = (UnitAtom) all.get(key);
        return atom == null ? key : atom.description;
    }

    //get the category of one unit according to its name(abbreviation)
    public static String getUnitCategory(String key) {
        return ((UnitAtom) all.get(key)).category;
    }

    public static void unitsCategorizing() {
        for (Enumeration e = all.elements(); e.hasMoreElements();) {
            UnitAtom currentUnit = (UnitAtom) e.nextElement();
            String currentCategory = currentUnit.category;
            if (currentUnit.category.equals("")) {
                currentCategory = "no-category";
            }

            if (categorizedUnits.containsKey(currentCategory)) {
                ((List) categorizedUnits.get(currentCategory)).add(currentUnit);
            } else {
                List newList = new ArrayList();
                newList.add(currentUnit);
                categorizedUnits.put(currentCategory, newList);
            }

        }
    }

    public static HashMap getCategorizedUnitDescriptions() {
        HashMap map = new HashMap();
        for (Enumeration e = all.elements(); e.hasMoreElements();) {
            UnitAtom currentUnit = (UnitAtom) e.nextElement();
            String currentCategory = currentUnit.category;
            if (currentUnit.category.equals("")) {
                currentCategory = "no-category";
            }
            if (map.containsKey(currentCategory)) {
                ((List) map.get(currentCategory)).add(currentUnit.description);
            } else {
                List newList = new ArrayList();
                newList.add(currentUnit.description);
                map.put(currentCategory, newList);
            }
        }
        return map;
    }

    public static List getAllUnitDescriptions() {
        List list = new ArrayList();
        for (Enumeration e = all.elements(); e.hasMoreElements();) {
            UnitAtom currentUnit = (UnitAtom) e.nextElement();
            list.add(currentUnit.description);
        }
        return list;
    }

    public static List getUnitsOfOneCategory(String category) {
        if (category.equals("")) {
            category = "no-category";
        }
        if (categorizedUnits.containsKey(category)) {
            return (List) categorizedUnits.get(category);
        } else {
            return Collections.EMPTY_LIST;
        }

    }

    public static String getUnitNameFromDescription(String desc) {
        for (Enumeration e = all.elements(); e.hasMoreElements();) {
            UnitAtom currUnit = (UnitAtom) e.nextElement();
            if (desc.equals(currUnit.description))
                return currUnit.getName();
        }
        return null;
    }

    public static String getUnitDescriptionFromName(String name) {
        for (Enumeration e = all.elements(); e.hasMoreElements();) {
            UnitAtom currUnit = (UnitAtom) e.nextElement();
            if (name.equals(currUnit.name))
                return currUnit.description;
        }
        return null;
    }

    public static Hashtable getCategorizedUnitsTable() {
        return categorizedUnits;
    }

    public static List getCategories() {
        return new ArrayList(categorizedUnits.keySet());
    }

    public static List getUnits(String category, String system) {
        List resultList = new ArrayList();
        if (category.equals("")) {
            category = "no-category";
        }
        if (categorizedUnits.containsKey(category)) {
            if (system.equals("all")) {
                resultList = (List) categorizedUnits.get(category);
            } else {
                List temp = (List) categorizedUnits.get(category);
                for (int i = 0; i < temp.size(); i++) {
                    UnitAtom myUnit = (UnitAtom) temp.get(i);
                    if (myUnit.metric == true & system.equals("metric")) {
                        resultList.add(myUnit);
                    }
                    if (myUnit.metric == false & system.equals("nonmetric")) {
                        resultList.add(myUnit);
                    }
                }
            }
        }
        return resultList;
    }
}
