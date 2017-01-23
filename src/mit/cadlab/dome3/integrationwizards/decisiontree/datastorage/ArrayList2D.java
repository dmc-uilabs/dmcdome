package mit.cadlab.dome3.integrationwizards.decisiontree.datastorage;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Nov 7, 2006
 * Time: 6:40:40 PM
 * To change this template use Options | File Templates.
 */
public class ArrayList2D {
    private ArrayList masterArray;

    //array lengths must hold the desired lengths of each row
    public ArrayList2D(int[] lengths)
    {
        this.masterArray = new ArrayList();
        for(int i=0;i<lengths.length;i++)
            masterArray.add(initializedArrayList(lengths[i]));
    }

    public ArrayList2D(int rows, int columns)
    {
        masterArray = new ArrayList();
        for(int i=0;i<rows;i++)
            masterArray.add(initializedArrayList(columns));
    }

    //Todo: make sure you can't get an Array out of bounds error
    public void setEntry(Object entry, int row, int column)
    {
        ArrayList levelList = (ArrayList)masterArray.get(row);
        levelList.set(column,entry);
        masterArray.set(row,levelList);
    }

    public void addEntry(Object entry, int row)
    {
        ArrayList levelList = (ArrayList)masterArray.get(row);
        levelList.add(entry);
        masterArray.set(row,levelList);
    }

    public Object getEntry(int row, int column)
    {
        ArrayList levelList = (ArrayList)masterArray.get(row);
        if(levelList.get(column)==null)
            return new Integer(0);
        return levelList.get(column);
    }

    public ArrayList getRow(int rowNumber)
    {
        ArrayList row = (ArrayList)masterArray.get(rowNumber);
        return row;
    }

    private ArrayList initializedArrayList(int length)
    {
        ArrayList newArray = new ArrayList();
        for (int i=0;i<length;i++)
            newArray.add(new Integer(0));
        return newArray;
    }
}
