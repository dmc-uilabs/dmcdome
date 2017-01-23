package com.ford.kbedoor.sealanalysis.sealgap;

public class Edge
{
   public String partName;
   public int id;
   public String type;
   public String color;
   public double length;
   public String relatedToGroup = null ;

   public Edge(String partName,
               int id,
               String type,
               String color,
               double length)
   {
      this.partName = partName;
      this.id = id;
      this.type = type;
      this.color = color;
      this.length = length;
   }
}

