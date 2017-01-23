package com.ford.kbedoor.sealanalysis.sealgap;

public class Face {
    public String partName;
    public int id;
    public String type;
    public String color;
    public String surfaceType;
    public String relatedToGroup = null;

    public Face(String partName,
                int id,
                String type,
                String color,
                String sType) {
        this.partName = partName;
        this.id = id;
        this.type = type;
        this.color = color;
        this.surfaceType = sType;
    }
}

