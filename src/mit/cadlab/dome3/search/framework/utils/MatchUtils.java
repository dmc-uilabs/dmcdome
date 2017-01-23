package mit.cadlab.dome3.search.framework.utils;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.SimpleARG;
import mit.cadlab.dome3.search.graphmatching.GMAlgorithm;
import mit.cadlab.dome3.search.graphmatching.RefinedGMAlgorithm;
import mit.cadlab.dome3.search.framework.templatemanagement.Template;
import mit.cadlab.dome3.search.framework.utils.processing.InterfaceData;
import mit.cadlab.dome3.search.similarityassessment.NodeSimilarity;
import mit.cadlab.dome3.search.similarityassessment.GraphSimilarity;
import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.SimilarityMeasures;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Jan 16, 2006
 * Time: 9:46:19 PM
 * To change this template use Options | File Templates.
 */


public class MatchUtils {

    public static double getSimilarityScore(FuzzyARG templateGraph, SimpleARG ifaceGraph){
            //GMAlgorithm algorithm=new GMAlgorithm(templateGraph,ifaceGraph);
            RefinedGMAlgorithm algorithm=new RefinedGMAlgorithm(templateGraph,ifaceGraph);
	        double score = algorithm.getSimilarityScore(GraphSimilarity.Algorithm_StrictAlignment);
            return score;
             //return algorithm.getSimilarityScore(GraphSimilarity.Algorithm_looseAlignment);
    }

    public static String GenerateReportSimilarityScore(FuzzyARG templateGraph, SimpleARG ifaceGraph){
            //GMAlgorithm algorithm=new GMAlgorithm(templateGraph,ifaceGraph);
            RefinedGMAlgorithm algorithm=new RefinedGMAlgorithm(templateGraph,ifaceGraph);
            String matching_report=algorithm.matching();
            return matching_report+"\n"+algorithm.generateReport(GraphSimilarity.Algorithm_StrictAlignment);
      // return matching_report+"\n"+algorithm.generateReport(GraphSimilarity.Algorithm_looseAlignment);
   }


    /**
           * here should be where the threshold be added
           * @param template
           * @param ifaces: interfaces
           * @return
           */
        public static ArrayList rank(Template template, ArrayList ifaces) {
              ArrayList ranked_result = new ArrayList();
              double score=0;

              for (int i = 0; i < ifaces.size(); i++) {
                  InterfaceData iface=(InterfaceData) ifaces.get(i);
                  System.out.println("processing interface " + iface.getIfacename() + "******************* ");
                  //get mit.cadlab.dome3.search.datastructure.graph similarity score
                  double gscore = getSimilarityScore(template.getGraph(), iface.getGraph());

                  System.out.print("   mit.cadlab.dome3.search.datastructure.graph matching score= " + score);
                  //add interface name similarity score
                  double nscore = SimilarityMeasures.StringMatching_TFIDF_matching(template.getName(), iface.getIfacename());

                  System.out.println("   name matching score= " + nscore);

                  //score = gscore + nscore;
                  score = gscore;
                  //only collect those mit.cadlab.dome3.search.datastructure.graph matching non-zero ones
                  if (gscore > 0) {
                      if (ranked_result.size() == 0) {
                          ranked_result.add(new Object[]{ifaces.get(i), new Double(score)});
                      } else {
                          int position = -1;
                          for (int j = 0; j < ranked_result.size(); j++) {
                              Object[] previous = (Object[]) ranked_result.get(j);
                              if (score <= ((Double) previous[1]).doubleValue()) {
                                  position = j;
                                  break;
                              }
                          }
                          if (position == -1)
                              ranked_result.add(new Object[]{ifaces.get(i), new Double(score)});
                          else
                              ranked_result.add(position, new Object[]{ifaces.get(i), new Double(score)});
                      }
                  }
              }

              return ranked_result;
          }


}
