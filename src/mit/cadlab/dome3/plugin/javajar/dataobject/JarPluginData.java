package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * JarPluginData class provides a lot of functionality for Jar data objects
 * User: jmekler
 * Date: 10/4/11
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JarPluginData extends AbstractPluginData {

    protected JarPlugin plugin;
    protected String fieldName;
    protected Class fieldType;
    protected boolean isResult = false;

    public JarPluginData(JarPlugin plg, Parameter p, String fName) throws Exception {
        plugin = plg;
        parameter = p;
        fieldName = fName;
        setFieldType();
    }

    // get value from parameter or native jar field
    protected abstract Object getValue(boolean isNativeCall);

    // set value of parameter or native jar field
    protected abstract void setValue(Object value, boolean isNativeCall);

    // print out a description of the object
    public abstract String toString();

    // returns a list of classes that are supported by the data object
    protected abstract List<Class> getSupportedDataTypes();

    // sets the fieldType property
    private void setFieldType() throws Exception {
        Field field = plugin.getField(fieldName);
        if (!isFieldTypeSupported(field))
            throw new Exception(this.getClass() + " does not support Field type: " + field.getType());
        else
            fieldType = field.getType();
    }

    // checks to make sure that the specified field specified is of a supported data type
    private boolean isFieldTypeSupported(Field field) {
        return getSupportedDataTypes().contains(field.getType());
    }

    protected static Object convertToObject(List list, Class type, int[] dim) {

        if (type.isArray()) {
            // Determine base type of object
            Class baseType = type;
            while (baseType.isArray())
                baseType = baseType.getComponentType();

            // Create an array of specified dimensions
            Object array = Array.newInstance(baseType, dim);

            // Figure out how many elements are in each sub-array
            int[] numElements = new int[dim.length];
            int elementSize = 1;

            for (int i = numElements.length-1; i>=0; i--) {
                numElements[i] = elementSize;
                elementSize *= dim[i];
            }

            // Copy values from list to array and return
            for (int index=0; index<elementSize; index++) {
                setEntry(array, index, numElements, list);
            }

            return array;

        } else {
            // Type is just a scalar value... return the first entry from the list
            return list.get(0);
        }
    }

    // Recursive method that uses reflection to set the value of an n-dimensional array
    private static void setEntry(Object array, int index, int[] numElements, List values) {
        if (numElements.length > 1) {
            // select the (n-1) dimension array an call setEntry on this array
            Object newArray = Array.get(array, index / numElements[0]);
            List newValues = (List) values.get(index / numElements[0]);
            int[] newElements = Arrays.copyOfRange(numElements, 1, numElements.length);
            int newIndex = index % numElements[0];
            setEntry(newArray, newIndex, newElements, newValues);

        } else {
            Array.set(array, index / numElements[0], values.get( index / numElements[0] ));
        }
    }

    // return a list of Objects encapsulated in obj
    protected static List convertToList(Object obj) {
        List list = new Vector();
        Class thisClass = obj.getClass();
        Class compClass = thisClass.getComponentType();

        if (thisClass.isArray()) {

            if (compClass.isPrimitive()) {
                if (compClass.equals(byte.class))
                    return convertToList( ArrayUtils.toObject((byte[]) obj) );

                else if (compClass.equals(short.class))
                    return convertToList( ArrayUtils.toObject((short[]) obj) );

                else if (compClass.equals(int.class))
                    return convertToList( ArrayUtils.toObject((int[]) obj) );

                else if (compClass.equals(long.class))
                    return convertToList( ArrayUtils.toObject((long[]) obj) );

                else if (compClass.equals(float.class))
                    return convertToList( ArrayUtils.toObject((float[]) obj) );

                else if (compClass.equals(double.class))
                    return convertToList( ArrayUtils.toObject((double[]) obj) );

                else if (compClass.equals(boolean.class))
                    return convertToList( ArrayUtils.toObject((boolean[]) obj) );

                else if (compClass.equals(char.class))
                    return convertToList( ArrayUtils.toObject((char[]) obj) );

            } else if (compClass.isArray()) {
                for (Object subArray : (Object[]) obj)
                    list.add( convertToList(subArray) );

            } else {
                for (Object item : (Object[]) obj)
                    list.add(item);
            }

        } else {
            list.add(obj);
        }

        return list;
    }

//    // recursive method to flatten an object
//    private static void convertToList(Object o, List objectList) {
//        Class oClass = o.getClass();
//        Class compClass = oClass.getComponentType();
//
//        if (oClass.isArray()) {
//            // if object o's components are also arrays, make the recursive call
//            if (compClass.isArray()) {
//                for (Object item : (Object[])o) {
//                    convertToList(item, objectList);
//                }
//
//            } else if (compClass.isPrimitive()) {
//
//                if (compClass.equals(byte.class))
//                    convertToList( ArrayUtils.toObject((byte[]) o), objectList);
//
//                else if (compClass.equals(short.class))
//                    convertToList( ArrayUtils.toObject((short[]) o), objectList);
//
//                else if (compClass.equals(int.class))
//                    convertToList( ArrayUtils.toObject((int[]) o), objectList);
//
//                else if (compClass.equals(long.class))
//                    convertToList( ArrayUtils.toObject((long[]) o), objectList);
//
//                else if (compClass.equals(float.class))
//                    convertToList( ArrayUtils.toObject((float[]) o), objectList);
//
//                else if (compClass.equals(double.class))
//                    convertToList( ArrayUtils.toObject((double[]) o), objectList);
//
//                else if (compClass.equals(boolean.class))
//                    convertToList( ArrayUtils.toObject((boolean[]) o), objectList);
//
//                else if (compClass.equals(char.class))
//                    convertToList( ArrayUtils.toObject((char[]) o), objectList);
//
//            } else {
//                for (Object item : (Object[]) o) {
//                    objectList.add(item);
//                }
//            }
//        } else {
//            objectList.add(o);
//        }
//    }

    public void setJavaData() {
        setValue(getValue(true), false);
    }

    public void setNativeData() {
        setValue(getValue(false), true);
    }

    public boolean getIsResult() {
        return isResult;
    }

    public void setIsResult(boolean val) {
        isResult = val;
    }

    public void resetObjectPointer() {
        // Do nothing...
    }


}
