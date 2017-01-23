package mit.cadlab.dome3.search.graphmatching;

import mit.cadlab.dome3.util.DSet;

/**
 *
 */
public class Path {
    public DSet vertices_sequence = new DSet();

    public Path() {

    }

    public Object clone() {
        //copy vertices_sequence
        Path cloned = new Path();
        for (int i = 0; i < vertices_sequence.size(); i++) {
            cloned.addVertex(vertices_sequence.get(i));
        }
        return cloned;
    }

    public void addVertex(Object obj) {
        if (!vertices_sequence.contains(obj))
            vertices_sequence.add(obj);
    }

    public boolean contains(Object v) {
        return (vertices_sequence.contains(v));
    }

    public Integer length() {
        return new Integer(vertices_sequence.size());
    }

    public String toString() {

        String s = "Path:";
        for (int i = 0; i < vertices_sequence.size(); i++) {
            if (i == vertices_sequence.size() - 1)
                s = s + vertices_sequence.get(i);
            else
                s = s + vertices_sequence.get(i) + "-->";
        }
        return s;
    }

    public Object next(Object vertex){
        if(vertices_sequence==null||vertices_sequence.size()==0){
            return null;
        }
        if(vertices_sequence.contains(vertex)){
           int index= vertices_sequence.indexOf(vertex);
           if(index==vertices_sequence.size()-1)//last one
            return null;
           else
            return vertices_sequence.get(index+1);
        }
        return null;
    }

    public Object previous(Object vertex){
          if(vertices_sequence==null||vertices_sequence.size()==0){
              return null;
          }
          if(vertices_sequence.contains(vertex)){
             int index= vertices_sequence.indexOf(vertex);
             if(index==0)//last one
              return null;
             else
              return vertices_sequence.get(index-1);
          }
          return null;
      }

    public void remove_last_added() {
        if (!(vertices_sequence == null) &&vertices_sequence.size() != 0)
            vertices_sequence.remove(vertices_sequence.size() - 1);
    }
}
