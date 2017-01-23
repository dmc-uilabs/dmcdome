package mit.cadlab.dome3.plugin.catalog.core.dataobject;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CoreUnitTest;
import mit.cadlab.dome3.util.units.Quantity;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 13.
 */
public class CDataObjectTest {
    public static void testOperatorOverloading() {
        CoreUnitTest.initUnitSystem();

        CReal a = new CReal(5000, "m");
        CReal b = new CReal(2, "km");
        //CReal c = (CReal) a.plus(b);
        CReal c = (CReal) a.minus(b);
        CReal d = new CReal(0, "cm");
        Integer e = new Integer(100);
        Double f = new Double(100);
        CReal g = new CReal(3, CConstant.NO_UNIT_STR);
        CReal h = new CReal(2, "kg");
        CReal i = new CReal(0, "m.m");

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        d.leftShift(c);
        System.out.println(d);
        System.out.println(d.plus(e));
        System.out.println(d.minus(f));
        System.out.println("g1:" + d.plus(g));
        System.out.println("g2:" + g.plus(d));
        System.out.println(d.multiply(e));
        System.out.println(d.multiply(f));
        System.out.println(d.multiply(b));
        System.out.println(b.multiply(d));
        System.out.println(b.multiply(g));
        System.out.println("g3:" + g.multiply(b));
        System.out.println(h.multiply(b));
        System.out.println("i:" + i.leftShift(b.multiply(d)));
        System.out.println("b1:" + g.divide(b));
        System.out.println("b2:" + b.divide(g));
        CReal j = (CReal) ((CReal) b.divide(g)).divide(a);
        System.out.println("b3:" + j);
        System.out.println("eq unit1:" + j.getUnit().getUnit().equivalent(Quantity.NO_UNIT));
        System.out.println("eq unit2:" + j.getUnit().getUnit().equals(Quantity.NO_UNIT));
        System.out.println("eq unit3:" + j.hasNoUnit());
        System.out.println("g4:" + g);



//        System.out.println(d.multiply(new Double("4")));
//        System.out.println(d.div(new Double("5")));
//        System.out.println(e.div(new Double("10")));
    }

    public static void testVectorMatrix() {
        CVector vec = new CVector(CConstant.REAL_DATA_TYPE, 0);
        vec.addDouble(4.4);
        vec.addDouble(4.5);
        vec.addDouble(4.6);
        System.out.println(vec);

        vec.setDataType(CConstant.INTEGER_DATA_TYPE);
        System.out.println(vec);

        vec.addInt(2);
        vec.addInt(3);
        vec.addInt(4);

        vec.setDataType(CConstant.REAL_DATA_TYPE);
        System.out.println(vec);


        CMatrix mat = new CMatrix(CConstant.REAL_DATA_TYPE, 5, 5, "m");
        System.out.println(mat);
        mat.setDouble(0,0, 2.3);
        mat.setDouble(1,1, 3.4);
        System.out.println(mat);

        mat.setDouble(0,4, 9.0);
        mat.setDouble(1,3, 2.0);
        System.out.println(mat);
    }

    public static void main(String[] args) {
        testOperatorOverloading();
    }
}
