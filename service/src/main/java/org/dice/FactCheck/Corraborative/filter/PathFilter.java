package org.dice.FactCheck.Corraborative.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Statement;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.UIResult.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PathFilter implements PathFilterable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PathFilter.class);

    //private static int[] pathsMinJ;
    //private List<Path> results;
    

    public String path;
    public String intermediateNodes;
    public Statement inputStatement;
    public int pathLength;
    public String builder;
    public int count_predicate_Occurrence;
    public int count_subject_Triples;
    public int count_object_Triples;
    public Set<Node> SubjectType;
    public Set<Node> ObjectType;
    //private QueryExecutioner queryExecutioner;
    public int count_path_Predicate_Occurrence;

    public PathFilter(String path, String builder, Statement inputStatement, String intermediateNodes,
            int pathLength, int count_predicate_Occurrence, int count_subject_Triples, int count_object_Triples,
            Set<Node> SubjectType, Set<Node> ObjectType,int count_path_Predicate_Occurrence) {
        this.path = path;
        this.builder = builder;
        this.inputStatement = inputStatement;
        this.pathLength = pathLength;
        this.count_predicate_Occurrence = count_predicate_Occurrence;
        this.count_subject_Triples = count_subject_Triples;
        this.count_object_Triples = count_object_Triples;
        this.SubjectType = SubjectType;
        this.ObjectType = ObjectType;
        this.intermediateNodes = intermediateNodes;
        //this.queryExecutioner = queryExecutioner;
        this.count_path_Predicate_Occurrence=count_path_Predicate_Occurrence;
        
    }

//    public PathFilter() {
//    }
    
	@Override
	public List<PathFilter> filterPaths(List<PathFilter> pths, int[] pathsMinJ) {
		List<PathFilter> fltrdPths= new ArrayList<PathFilter>();
		for(PathFilter pth:pths) 
			 if( pth.count_path_Predicate_Occurrence >= pathsMinJ[pth.pathLength-1]) fltrdPths.add(pth);
	    return(fltrdPths) ;
	}

}
