package mit.cadlab.dome3.util;

import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.CommonAuxFile;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.util.xml.DomeXmlData;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import org.dom4j.Element;

/**
 * to be used by check out
 */


public class AuxiliaryFileUtils {

    public static Vector getAuxFileInfos(String modelxml) {
        DomeXmlData modelData = new DomeXmlData(DomeXmlData.MODEL, modelxml);
        Vector auxFs = modelData.getModelAuxFiles();
        return auxFs;
    }

    /**
     *
     * @param auxfileId
     * @param modelxml
     * @param option : 1: model 2:project
     * @return
     */
    public static String getDefaultLocation(String auxfileId, String modelxml,int option) {
        DomeXmlData modelData=null;
        if(option==1){
           modelData = new DomeXmlData(DomeXmlData.MODEL, modelxml);
        }
        else if(option==2){
           modelData = new DomeXmlData(DomeXmlData.PROJECT, modelxml);
        }
        if(modelData==null) return null;
        Vector auxFs = modelData.getModelAuxFiles();
        for (Iterator i = auxFs.iterator(); i.hasNext();) {
            AbstractAuxFile af = (AbstractAuxFile) i.next();
            if (af.getId().toString().equals(auxfileId)) return af.getFile().getPath();
        }
        return null; //not found
    }

    /**
     *
     * @param auxfileId
     * @param modelxml
     * @param newpath :should be validate before coming here
     * @return
     */
    public static boolean setLocation(String auxfileId, String modelxml, String newpath) {
        DomeXmlData modelData = new DomeXmlData(DomeXmlData.MODEL, modelxml);
        Vector auxFs = modelData.getModelAuxFiles();
        for (Iterator i = auxFs.iterator(); i.hasNext();) {
            AbstractAuxFile af = (AbstractAuxFile) i.next();
            if (af.getId().toString().equals(auxfileId)) {
                af.setFile(new File(newpath));
                return true;
            }
        }
        return false; //not found
    }


    public static String replaceAuxFilesLocation(Vector AuxFiles, String modelxml) {
        String result=modelxml;
        for (Iterator i = AuxFiles.iterator(); i.hasNext();) {
                AbstractAuxFile f = (AbstractAuxFile) i.next();
                result=replaceAuxFileLocation(result,f.getId().toString(),f.getName().toString(),f.getFile().getPath());
            }
         return result;
    }




    //a normal xml content of auxfile is
    //<auxfile id="d43e1634-b76e-1004-8b11-4c0378b5caf7" name="customGui.jar">
    //  <shouldUpload value="true"/>
    //  <fileType>file:///</fileType>
    //  <file>C:\dome3\development\scripts\db\customGui\customGui.jar</file>
    //</auxfile>
    //the XMLUtils.toPrettyString method adds indent to each line so it's unable to match whole phrase without do some tricks

    public static String replaceAuxFileLocation(String originalStr, String id, String name, String newLocation) {
        //first locate that result
        String result = originalStr;
        String queryStr = "<auxfile id=\"" + id + "\" name=\"" + name + "\">";
        debug(queryStr);
        int index = result.indexOf(queryStr);
        if (index == -1) {
            debug(new Integer(index));
            return result;
        }

        String queryStr2="<file>",queryStr3="</file>";
        //index of the first char of the query str
        int index2=result.indexOf(queryStr2,index); //refer to the "<"  of queryStr 2
        int index3=result.indexOf(queryStr3,index); //refer to the "<"  of queryStr 3

        String str_tobe_replaced=result.substring(index2+6,index3);
        debug("string to be replaced:"+str_tobe_replaced);
        debug(result.indexOf(str_tobe_replaced,index)+"");
        debug("new string"+newLocation);

       //version 1: bug:doesn't seem to return replaced string ---- result.replaceFirst(str_tobe_replaced,newLocation);
       //need to write a method to replace
       //version 2: bug:the plugin model file has same <file> </file> so the following thing will not work
       // return replace(result,str_tobe_replaced,newLocation);
       return replace(result,index2+6,str_tobe_replaced,newLocation);
       }



    //Qing-- add here for customGui files
     // a normal xml content of customfile is
     //   <customGuiInfo>
     //       <jarFilepath>C:\dome3\development\scripts\db\customGui\customGui.jar</jarFilepath>
     //       <shortName>test</shortName>
     //       <className>test.gui.CustomSimpleMathModelGui</className>
     //       <jarFileId>customGuiFile</jarFileId>
     //   </customGuiInfo>

    //the XMLUtils.toPrettyString method adds indent to each line so it's unable to match whole phrase without do some tricks

    public static String replaceCustomGuiFileLocation(String originalStr, String originalLocation, String newLocation) {
        //first locate that result
        String result = originalStr;
        String queryStr = "<jarFilepath>"+originalLocation+"</jarFilepath>";
        debug(queryStr);
        int index = result.indexOf(queryStr);
        if (index == -1) {
            debug(new Integer(index));
            return result;
        }

        String queryStr2="<jarFilepath>",queryStr3="</jarFilepath>";
        //index of the first char of the query str
        int index2=result.indexOf(queryStr2,index); //refer to the "<"  of queryStr 2
        int index3=result.indexOf(queryStr3,index); //refer to the "<"  of queryStr 3

        String str_tobe_replaced=result.substring(index2+13,index3);
        debug("string to be replaced:"+str_tobe_replaced);
        debug(result.indexOf(str_tobe_replaced,index)+"");
        debug("new string"+newLocation);

       //version 1: bug:doesn't seem to return replaced string ---- result.replaceFirst(str_tobe_replaced,newLocation);
       //need to write a method to replace
       //version 2: bug:the plugin model file has same <file> </file> so the following thing will not work
       // return replace(result,str_tobe_replaced,newLocation);
       return replace(result,index2+13,str_tobe_replaced,newLocation);
       }

    public static void debug(Object msg) {
        boolean showDebug = false;
        if (showDebug)
            System.out.println(msg);
    }

    public static String replace(String originalStr,int position, String sub_tobe_replaced,String newStr){
        int index=position;
        if(index==-1) return sub_tobe_replaced;

        String substr=originalStr.substring(0,index);

        StringBuffer result=new StringBuffer(substr);

        String endSubStr=originalStr.substring(index+sub_tobe_replaced.length(),originalStr.length());

        result.append(newStr);
        result.append(endSubStr);

        return result.toString();
    }

    public static String replace(String originalStr, String sub_tobe_replaced,String newStr){
        int index=originalStr.indexOf(sub_tobe_replaced);
        if(index==-1) return sub_tobe_replaced;

        String substr=originalStr.substring(0,index);

        StringBuffer result=new StringBuffer(substr);

        String endSubStr=originalStr.substring(index+sub_tobe_replaced.length(),originalStr.length());

        result.append(newStr);
        result.append(endSubStr);

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(replace("my name is steve, right?",11,"steve","qing"));
        System.out.println(replace("my name is steve, right?","steve","qing"));
    }

}

