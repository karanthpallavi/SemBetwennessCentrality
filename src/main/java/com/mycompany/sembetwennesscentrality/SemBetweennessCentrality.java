/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sembetwennesscentrality;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Double.POSITIVE_INFINITY;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLPrimitive;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectImpl;

/**
 *
 * @author DrKM
 */
public class SemBetweennessCentrality {
    public static int numberOfShortestPathsThruVert = 0;
        public static double sumOfShortestSemanticPaths = 0.0;
        public static double sumOfShortestRegularPaths = 0.0;
    public static void main(String args[]) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException
    {
        String OntologyFile = new String(args[0]);
        System.out.println("The Ontology file which is preprocessed and used for computing Semantic Betweenness is "+OntologyFile);
        String FolderName = new String(args[1]);
        System.out.println("The folder containing ontology file is "+FolderName);
        int success = loadOntology(OntologyFile,FolderName);
    }
    
    public static int loadOntology(String fileName, String folderName) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException 
    {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // We load an ontology from the URI specified
        // on the command line
        File owlFile = new File(fileName);
        File folder = new File(folderName);
        AutoIRIMapper mapper=new AutoIRIMapper(folder, true);
        manager.addIRIMapper(mapper);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
        
        // Report information about the ontology
        System.out.println("Ontology Loaded...");
        //System.out.println("Document IRI: " + documentIRI);
        System.out.println("Ontology : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));
        int success = SemanticBetweennessCentrality(ontology,manager);
        folder.deleteOnExit();
        return 1;
    }
    
    public static int SemanticBetweennessCentrality(OWLOntology ontology, OWLOntologyManager manager) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException
    {
        //PrintStream o = new PrintStream(new File("E:/Pallavi/Ontology/CitationAnalysis_AuthorsCitations_vertex1.txt"));
        //PrintStream console = System.out;
        System.out.println("Call to Semantic Betweenness Centrality");
        TransformTriplesToGraph toGraph = new TransformTriplesToGraph(ontology);
        
        int vertex = 2;
                int numShortestRegPaths = 0;
                int numRegPathsThruVert = 0;
                double RegBetweennessCentrality = 0.0;
                // Regular Betweenness Centrality computation
                DirectedGraph<Integer,DefaultEdge> OWLNonWeightedGraph = toGraph.OWLNonWeightedGraph;
                int numVert = OWLNonWeightedGraph.vertexSet().size();
                System.out.println("Number of vertices in Non weighted Graph is "+numVert);
                FloydWarshallShortestPaths shortestPathsGraph = new FloydWarshallShortestPaths(OWLNonWeightedGraph);
                // Compute number of shortest paths between all pairs of vertices
                for(int i = 0; i<numVert;i++)
                {
                    for(int j = 0; j<numVert;j++)
                    {
                        if(i==j)
                            continue;
                        double shortestDistance = shortestPathsGraph.shortestDistance(i, j);
                        List shortestPathAsVertexList = shortestPathsGraph.getShortestPathAsVertexList(i, j);
                        if(Double.isInfinite(shortestDistance))
                        {
                            
                        } 
                        else {
                            //System.out.println("Shortest path exists between "+i+" and "+j);
                            if(shortestPathAsVertexList.contains(vertex))
                            {
                                numRegPathsThruVert++;
                                sumOfShortestRegularPaths = sumOfShortestRegularPaths + shortestDistance;
                            }
                            
                        }
                    }
                }
                numShortestRegPaths = shortestPathsGraph.getShortestPathsCount();
                RegBetweennessCentrality = (double)numRegPathsThruVert/numShortestRegPaths;
                System.out.println("Number of regular shortest paths is "+numShortestRegPaths);
                System.out.println("Number of paths passing through vertex "+vertex+" is "+numRegPathsThruVert);
                System.out.println("Regular BetweennessCentrality of vertex " + vertex+ " is "+RegBetweennessCentrality);
                SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> OWLGraph = toGraph.OWLGraph;
                AllDirectedPaths allPathsGraph = new AllDirectedPaths(OWLGraph);
                List<OWLObjectImpl> entitiesOfGraph = toGraph.entitiesOfGraph;
                List<com.mycompany.semanticdistance.OWLEdge> edgesOfGraph = toGraph.edgesOfGraph;
                numberOfShortestPathsThruVert = 0;
                int totalnumShortestPaths = 0;
                for(int i =0; i<entitiesOfGraph.size();i++)
                {
                for(int j = 0; j < entitiesOfGraph.size();j++)
            {
                if(i==j)
                    continue;
                //System.out.println("Values of i and j are "+ i + " and "+j);
                
                List allPaths = allPathsGraph.getAllPaths( i,j,true, 6);
                if(allPaths.size()>0)
                {
                    System.out.println(allPaths);  
                }
                else
                {
                    System.out.println("No path between source and destination, path length is infinity");
                }
                int numShortestPaths = computeShortestSemanticPath(allPaths,ontology,allPathsGraph,entitiesOfGraph,edgesOfGraph,OWLGraph,vertex);
                //System.out.println("The Semantic Distance between " + i + " and " + j + " is "+SemDist);
                totalnumShortestPaths+=numShortestPaths;
                System.out.println("Number of shortest semantic paths between 0 and "+ j+ " is "+numShortestPaths);
                System.out.println("Number of shortest paths passing through vertex "+ vertex + " is "+numberOfShortestPathsThruVert);
                
            }
                } // End of for i
                System.out.println("Total number of shortest paths between all pairs of vertices is "+totalnumShortestPaths);
        System.out.println("Number of shortest paths passing through vertex " + vertex+" is "+numberOfShortestPathsThruVert);
        double SemBetweennessCentrality = (double)numberOfShortestPathsThruVert/totalnumShortestPaths;
        System.out.println("Sum of shortest semantic paths passing thourgh vertex is "+sumOfShortestSemanticPaths);
        double avgShortestSemanticPaths = (double)sumOfShortestSemanticPaths/totalnumShortestPaths;
        System.out.println("Semantic Betweenness Centrality of vertex "+vertex+"is "+SemBetweennessCentrality);
        System.out.println("Regular BetweennessCentrality of vertex "+vertex+" is "+RegBetweennessCentrality);
        System.out.println("Sum of shortest regular paths passing through vertex is "+sumOfShortestRegularPaths);
        double avgShortestRegularPaths = (double)sumOfShortestRegularPaths/numShortestRegPaths;
        System.out.println("Average shortest semantic paths is "+avgShortestSemanticPaths);
        System.out.println("Average shortest regular paths is "+avgShortestRegularPaths);
        return 1;
    }
    
    public static int computeShortestSemanticPath(List allPaths, OWLOntology ontology, AllDirectedPaths allPathsGraph, List<OWLObjectImpl> entitiesOfGraph, List<com.mycompany.semanticdistance.OWLEdge> edgesOfGraph,SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> OWLGraph, int vertex )
    {
        NodeSet<OWLObjectPropertyExpression> superObjectProperties = null;
                        NodeSet<OWLObjectPropertyExpression> subObjectProperties=null;
                        //PrintStream console = System.out;
 
        // Assign o to output stream
        //System.setOut(o);
        double SemDistShort = 0.0;
        double SemDist = 0.0;
        // Initialize constants
        double k1=0.1;
        double k2 = 0.5;
        double k3 = 0.7;
        double k4 = 0.9;
        double alpha = 1.0;
        double beta = 0.05;
        double gamma = 1.0;
        int numberOfShortestPaths = 0;
        //int numPathsPassingThroughVert = 0;
        // Read List elements
        // Each Path
        Iterator listIter = allPaths.iterator();
        List<GraphWalk> allPathsString;
        allPathsString = new ArrayList<GraphWalk>();
        Reasoner.ReasonerFactory factory = new Reasoner.ReasonerFactory();
                                Configuration configuration=new Configuration();
                                configuration.throwInconsistentOntologyException=false;
                                OWLReasoner reasoner=factory.createReasoner(ontology, configuration);
        // Check if there is no path between source and destination
        if(allPaths.size()==0)
        {
            System.out.println("No path between source and destination found");
            SemDistShort = POSITIVE_INFINITY;
            return 0;
            //return SemDistShort;
        }
        
        int i = 1;
        while(listIter.hasNext())
        {
            //System.out.println("Path "+i);
            //System.out.println(listIter.next());
            GraphWalk path = (GraphWalk) listIter.next();
            allPathsString.add(path);
            // Process paths            
            i++;
        }
        System.out.println("Number of paths are "+i);
        System.out.println("Value of numberOfShortestPathsThruVert before this call is "+numberOfShortestPathsThruVert);
        List<Integer> ShortestPathIndices;
        ShortestPathIndices = new ArrayList<>();
        List<Double> semDistShort = new ArrayList<>();
        // For now, process the length of the last path - path number 121 
        // Later, run the same alg for all paths and find the shortest path
        for(int pathIter = 0; pathIter < i-1;pathIter++)
        {
            //SemDistShort = 0.0;
            SemDist = 0.0;
          GraphWalk pathToProcess = allPathsString.get(pathIter);
          // for now, comnted next line for processing and computing distance of all the paths
        //GraphWalk pathToProcess = allPathsString.get(i-2);
        //List vertexList = new List("<http://www.semanticweb.org/drkm/ontologies/2017/4/untitled-ontology-82#Ram>","<http://www.semanticweb.org/drkm/ontologies/2017/4/untitled-ontology-82#Orange>","<http://www.semanticweb.org/drkm/ontologies/2017/4/untitled-ontology-82#VitaminC>","<http://www.semanticweb.org/drkm/ontologies/2017/4/untitled-ontology-82#Scurvy>");
        //GraphWalk pathToProcess = new GraphWalk(allPathsGraph,)
            System.out.println("Processing path ");
            System.out.println(pathToProcess);
            
            // For this path, get vertex list and edge type to compute path length
            Integer startVertex = (Integer) pathToProcess.getStartVertex();
            List edgeList = pathToProcess.getEdgeList();
            System.out.println("Start vertex of this path is "+startVertex+ " ");
            System.out.println(edgeList);
            OWLObjectImpl startVert = entitiesOfGraph.get(startVertex);
            System.out.println(startVert);
            // To get next edges and get type, process length
            List vertexList = pathToProcess.getVertexList();
            for(int index = 0; index < vertexList.size();index++)
            {
                Integer firstVert = (Integer) vertexList.get(index);
                for(int nextVert = index+1; nextVert < vertexList.size();nextVert++)
                {
                    Integer secondVert = (Integer) vertexList.get(nextVert);
                    OWLObjectImpl firstVertObj = entitiesOfGraph.get(firstVert);
                    OWLObjectImpl secondVertObj = entitiesOfGraph.get(secondVert);
                    System.out.println("Edge between "+firstVertObj+ " and "+secondVertObj);
                    //SemDist+=
                    // Get edge weights
                    
                    //OWLEdge edge = new OWLEdge(firstVert,secondVert);
                    //int indexEdge = edgesOfGraph.indexOf(edge);
                    //System.out.println(indexEdge);
                    break;
                }   
            }
            
            for(int j = 0; j<edgeList.size();j++)
            {
                // Retreive edges and their weights in the path
                // Add weights to the distance
                
                DefaultWeightedEdge edge = (DefaultWeightedEdge) edgeList.get(j);
                double edgeWeight = OWLGraph.getEdgeWeight(edge);
                System.out.println(edgeWeight);
                SemDist+=edgeWeight;
                
                for(int nextEdge = j+1;nextEdge<edgeList.size();nextEdge++)
                {
                    DefaultWeightedEdge nextEdgeObj =  (DefaultWeightedEdge) edgeList.get(nextEdge);
                    double edgeWeightNext = OWLGraph.getEdgeWeight(nextEdgeObj);
                    if(edgeWeight==k1&&edgeWeightNext==k1)
                    {
                        // Two taxonomical edges
                        SemDist+=2*beta;
                        System.out.println("Two taxonomical edges turn");
                        // Find if its a zig zag movement
                        Integer edgeSource = OWLGraph.getEdgeSource(edge);
                        Integer edgeTarget = OWLGraph.getEdgeTarget(edge);
                        Integer nextEdgeTarget = OWLGraph.getEdgeTarget(nextEdgeObj);
                        OWLObjectImpl firstSource = entitiesOfGraph.get(edgeSource);
                        OWLObjectImpl secondSource = entitiesOfGraph.get(edgeTarget);
                        OWLObjectImpl secondTarget = entitiesOfGraph.get(nextEdgeTarget);
                        System.out.println("First Source is "+firstSource);
                        System.out.println("Second Source is "+secondSource);
                        System.out.println("Second Target is "+secondTarget);
                        
                        // Check if its going up and coming down the taxonomy
                        //OWLClass classSourceInQues = firstSource.
                        //System.out.println(classSourceInQues);
                        /*DefaultWeightedEdge nexttonextEdgeObj =  (DefaultWeightedEdge) edgeList.get(nextEdge+1);
                        double nextToNextEdgeWeight = OWLGraph.getEdgeWeight(nexttonextEdgeObj);
                        if(nextToNextEdgeWeight==k1)
                        {
                        Integer edgeSource = OWLGraph.getEdgeSource(edge);
                        Integer edgeTarget = OWLGraph.getEdgeTarget(edge);
                        Integer edgeTarget1 = OWLGraph.getEdgeTarget(nexttonextEdgeObj);
                        }*/
                    }
                    else if((edgeWeight == k2||edgeWeight == k3||edgeWeight == k4)  && edgeWeightNext == k1)
                    {//Object Property and Taxonomical
                        SemDist+=alpha;
                        System.out.println("Object Property and Taxonomical turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    else if(edgeWeight == k1 && (edgeWeightNext == k2||edgeWeightNext == k3||edgeWeightNext == k4))
                    {
                        //Taxonomical and Object
                        SemDist+=alpha;
                        System.out.println("Taxonomical and Object Property turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    else if(edgeWeight == k1 && (edgeWeightNext == k2||edgeWeightNext == k3||edgeWeightNext == k4))
                    {
                        SemDist+=alpha;
                        System.out.println("Taxonomical and Object Property turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    else if((edgeWeight == k2||edgeWeight == k3||edgeWeight == k4) && edgeWeightNext == k1)
                    {
                        SemDist+=alpha;
                        System.out.println("Object Property and Taxonomical turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    else if(edgeWeight == k2 && edgeWeightNext == k2)
                    {
                        // Distance between properties in the property hierarchy tree
                        // For now, 2.0
                        
                        
                        Integer edgeSource = OWLGraph.getEdgeSource(edge);
                        Integer edgeTarget = OWLGraph.getEdgeTarget(edge);
                        Integer nextEdgeTarget = OWLGraph.getEdgeTarget(nextEdgeObj);
                        OWLObjectImpl firstSource = entitiesOfGraph.get(edgeSource);
                        OWLObjectImpl secondSource = entitiesOfGraph.get(edgeTarget);
                        OWLObjectImpl secondTarget = entitiesOfGraph.get(nextEdgeTarget);
                        System.out.println("First Source is "+firstSource);
                        System.out.println("Second Source is "+secondSource);
                        System.out.println("Second Target is "+secondTarget);
                        // Get property between these source and targets
                        Set<OWLAxiom> referencingAxioms3 = ontology.getReferencingAxioms((OWLPrimitive) firstSource);
                        List<OWLObjectProperty> propertiesInTurn = new ArrayList<>();
                        for(OWLAxiom axiomInQues: referencingAxioms3)
                        {
                            if(axiomInQues.containsEntityInSignature((OWLEntity) secondSource))
                            {
                                //System.out.println("Axiom containing second object");
                                //System.out.println(axiomInQues);
                                Set<OWLObjectProperty> objectPropertiesInSignature = axiomInQues.getObjectPropertiesInSignature();
                                System.out.println("Size of properties set is "+objectPropertiesInSignature.size());
                                OWLObjectProperty prop = objectPropertiesInSignature.iterator().next();
                                System.out.println("Property 1 in turn is "+prop);
                                
                                superObjectProperties = reasoner.getSuperObjectProperties(prop, true);
                                subObjectProperties = reasoner.getSubObjectProperties(prop, true);
                                propertiesInTurn.add(prop);
                                break;
                            }
                        }
                        Set<OWLAxiom> referencingAxioms4 = ontology.getReferencingAxioms((OWLPrimitive) secondSource);
                        for(OWLAxiom axiomInQues: referencingAxioms4)
                        {
                            if(axiomInQues.containsEntityInSignature((OWLEntity) secondTarget))
                            {
                                //System.out.println("Axiom containing second object");
                                //System.out.println(axiomInQues);
                                Set<OWLObjectProperty> objectPropertiesInSignature = axiomInQues.getObjectPropertiesInSignature();
                                System.out.println("Size of properties set is "+objectPropertiesInSignature.size());
                                OWLObjectProperty prop = objectPropertiesInSignature.iterator().next();
                                System.out.println("Property 2 in turn is "+prop);
                                propertiesInTurn.add(prop);
                                break;
                            }
                        }
                        OWLObjectProperty prop1 = propertiesInTurn.get(0);
                        OWLObjectProperty prop2 = propertiesInTurn.get(1);
                        if(superObjectProperties.containsEntity(prop2))
                        {
                            SemDist+=1.0;
                                System.out.println("In hierarchy");
                        }
                        else if(subObjectProperties.containsEntity(prop2))
                        {
                            SemDist+=1.0;
                            System.out.println("In hierarchy");
                        }
                        else
                        {
                            SemDist+=2.0;
                            System.out.println("Not in hierarchy");
                        }
                        /*Stream<OWLSubObjectPropertyOfAxiom> objectSubPropertyAxiomsForSubProperty = ontology.objectSubPropertyAxiomsForSubProperty(prop1);
                        Stream<OWLSubObjectPropertyOfAxiom> objectSubPropertyAxiomsForSuperProperty = ontology.objectSubPropertyAxiomsForSuperProperty(prop1);
                        if(objectSubPropertyAxiomsForSubProperty.anyMatch((Predicate<? super OWLSubObjectPropertyOfAxiom>) prop2))
                        {
                            System.out.println("In hierarchy");
                        }
                        
                        else if(objectSubPropertyAxiomsForSuperProperty.anyMatch((Predicate<? super OWLSubObjectPropertyOfAxiom>) prop2))
                        {
                            System.out.println("In hierarchy");
                        }
                        else{
                            System.out.println("Not in this branch of property hierarchy");
                        }*/
                        //System.out.println("Ref Axioms: "+referencingAxioms3);
                        System.out.println("Object Property and Object Property turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    else if(edgeWeight == k3 && edgeWeightNext == k3)
                    {
                        // Distance between properties in the property hierarchy tree
                        Integer edgeSource = OWLGraph.getEdgeSource(edge);
                        Integer edgeTarget = OWLGraph.getEdgeTarget(edge);
                        Integer nextEdgeTarget = OWLGraph.getEdgeTarget(nextEdgeObj);
                        OWLObjectImpl firstSource = entitiesOfGraph.get(edgeSource);
                        OWLObjectImpl secondSource = entitiesOfGraph.get(edgeTarget);
                        OWLObjectImpl secondTarget = entitiesOfGraph.get(nextEdgeTarget);
                        System.out.println("First Source is "+firstSource);
                        System.out.println("Second Source is "+secondSource);
                        System.out.println("Second Target is "+secondTarget);
                        // Get property between these source and targets
                        Set<OWLAxiom> referencingAxioms3 = ontology.getReferencingAxioms((OWLPrimitive) firstSource);
                        List<OWLObjectProperty> propertiesInTurn = new ArrayList<>();
                        for(OWLAxiom axiomInQues: referencingAxioms3)
                        {
                            if(axiomInQues.containsEntityInSignature((OWLEntity) secondSource))
                            {
                                //System.out.println("Axiom containing second object");
                                //System.out.println(axiomInQues);
                                Set<OWLObjectProperty> objectPropertiesInSignature = axiomInQues.getObjectPropertiesInSignature();
                                System.out.println("Size of properties set is "+objectPropertiesInSignature.size());
                                OWLObjectProperty prop = objectPropertiesInSignature.iterator().next();
                                System.out.println("Property 1 in turn is "+prop);
                                /*Reasoner.ReasonerFactory factory = new Reasoner.ReasonerFactory();
                                Configuration configuration=new Configuration();
                                configuration.throwInconsistentOntologyException=false;
                                OWLReasoner reasoner=factory.createReasoner(ontology, configuration);*/
                                superObjectProperties = reasoner.getSuperObjectProperties(prop,true);
                                subObjectProperties = reasoner.getSubObjectProperties(prop,true);
                                propertiesInTurn.add(prop);
                                break;
                            }
                        }
                        Set<OWLAxiom> referencingAxioms4 = ontology.getReferencingAxioms((OWLPrimitive) secondSource);
                        for(OWLAxiom axiomInQues: referencingAxioms4)
                        {
                            if(axiomInQues.containsEntityInSignature((OWLEntity) secondTarget))
                            {
                                //System.out.println("Axiom containing second object");
                                //System.out.println(axiomInQues);
                                Set<OWLObjectProperty> objectPropertiesInSignature = axiomInQues.getObjectPropertiesInSignature();
                                System.out.println("Size of properties set is "+objectPropertiesInSignature.size());
                                OWLObjectProperty prop = objectPropertiesInSignature.iterator().next();
                                System.out.println("Property 2 in turn is "+prop);
                                propertiesInTurn.add(prop);
                                break;
                            }
                        }
                        OWLObjectProperty prop1 = propertiesInTurn.get(0);
                        OWLObjectProperty prop2 = propertiesInTurn.get(1);
                        if(superObjectProperties.containsEntity(prop2))
                        {
                            SemDist+=1.0;
                                System.out.println("In hierarchy");
                        }
                        else if(subObjectProperties.containsEntity(prop2))
                        {
                            SemDist+=1.0;
                            System.out.println("In hierarchy");
                        }
                        else
                        {
                            SemDist+=2.0;
                            System.out.println("Not in hierarchy");
                        }
                        /*Stream<OWLSubObjectPropertyOfAxiom> objectSubPropertyAxiomsForSubProperty = ontology.objectSubPropertyAxiomsForSubProperty(prop1);
                        Stream<OWLSubObjectPropertyOfAxiom> objectSubPropertyAxiomsForSuperProperty = ontology.objectSubPropertyAxiomsForSuperProperty(prop1);
                        //objectSubPropertyAxiomsForSubProperty.
                        if(objectSubPropertyAxiomsForSubProperty.anyMatch((Predicate<? super OWLSubObjectPropertyOfAxiom>) prop2))
                        {
                            System.out.println("In hierarchy");
                        }
                        
                        else if(objectSubPropertyAxiomsForSuperProperty.anyMatch((Predicate<? super OWLSubObjectPropertyOfAxiom>) prop2))
                        {
                            System.out.println("In hierarchy");
                        }
                        else{
                            System.out.println("Not in this branch of property hierarchy");
                        }*/
                        //System.out.println("Ref Axioms: "+referencingAxioms3);
                        System.out.println("Object Property and Object Property turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    else if(edgeWeight == k4 && edgeWeightNext == k4)
                    {
                        // Distance between properties in the property hierarchy tree
                        //SemDist+=2.0;
                        
                        Integer edgeSource = OWLGraph.getEdgeSource(edge);
                        Integer edgeTarget = OWLGraph.getEdgeTarget(edge);
                        Integer nextEdgeTarget = OWLGraph.getEdgeTarget(nextEdgeObj);
                        OWLObjectImpl firstSource = entitiesOfGraph.get(edgeSource);
                        OWLObjectImpl secondSource = entitiesOfGraph.get(edgeTarget);
                        OWLObjectImpl secondTarget = entitiesOfGraph.get(nextEdgeTarget);
                        System.out.println("First Source is "+firstSource);
                        System.out.println("Second Source is "+secondSource);
                        System.out.println("Second Target is "+secondTarget);
                        // Get property between these source and targets
                        Set<OWLAxiom> referencingAxioms3 = ontology.getReferencingAxioms((OWLPrimitive) firstSource);
                        List<OWLObjectProperty> propertiesInTurn = new ArrayList<>();
                        for(OWLAxiom axiomInQues: referencingAxioms3)
                        {
                            if(axiomInQues.containsEntityInSignature((OWLEntity) secondSource))
                            {
                                //System.out.println("Axiom containing second object");
                                //System.out.println(axiomInQues);
                                Set<OWLObjectProperty> objectPropertiesInSignature = axiomInQues.getObjectPropertiesInSignature();
                                System.out.println("Size of properties set is "+objectPropertiesInSignature.size());
                                OWLObjectProperty prop = objectPropertiesInSignature.iterator().next();
                                System.out.println("Property 1 in turn is "+prop);
                                /*Reasoner.ReasonerFactory factory = new Reasoner.ReasonerFactory();
                                Configuration configuration=new Configuration();
                                configuration.throwInconsistentOntologyException=false;
                                OWLReasoner reasoner=factory.createReasoner(ontology, configuration);*/
                                superObjectProperties = reasoner.getSuperObjectProperties(prop,true);
                                subObjectProperties = reasoner.getSubObjectProperties(prop,true);
                                propertiesInTurn.add(prop);
                                break;
                            }
                        }
                        Set<OWLAxiom> referencingAxioms4 = ontology.getReferencingAxioms((OWLPrimitive) secondSource);
                        for(OWLAxiom axiomInQues: referencingAxioms4)
                        {
                            if(axiomInQues.containsEntityInSignature((OWLEntity) secondTarget))
                            {
                                //System.out.println("Axiom containing second object");
                                //System.out.println(axiomInQues);
                                Set<OWLObjectProperty> objectPropertiesInSignature = axiomInQues.getObjectPropertiesInSignature();
                                //System.out.println("Size of properties set is "+objectPropertiesInSignature.size());
                                OWLObjectProperty prop = objectPropertiesInSignature.iterator().next();
                                System.out.println("Property 2 in turn is "+prop);
                                propertiesInTurn.add(prop);
                                break;
                            }
                        }
                        OWLObjectProperty prop1 = propertiesInTurn.get(0);
                        OWLObjectProperty prop2 = propertiesInTurn.get(1);
                        if(superObjectProperties.containsEntity(prop2))
                        {
                            SemDist+=1.0;
                                System.out.println("In hierarchy");
                        }
                        else if(subObjectProperties.containsEntity(prop2))
                        {
                            SemDist+=1.0;
                            System.out.println("In hierarchy");
                        }
                        else
                        {
                            SemDist+=2.0;
                            System.out.println("Not in hierarchy");
                        }
                        /*Stream<OWLSubObjectPropertyOfAxiom> objectSubPropertyAxiomsForSubProperty = ontology.objectSubPropertyAxiomsForSubProperty(prop1);
                        Stream<OWLSubObjectPropertyOfAxiom> objectSubPropertyAxiomsForSuperProperty = ontology.objectSubPropertyAxiomsForSuperProperty(prop1);
                        if(objectSubPropertyAxiomsForSubProperty.anyMatch((Predicate<? super OWLSubObjectPropertyOfAxiom>) prop2))
                        {
                            System.out.println("In hierarchy");
                        }
                        
                        else if(objectSubPropertyAxiomsForSuperProperty.anyMatch((Predicate<? super OWLSubObjectPropertyOfAxiom>) prop2))
                        {
                            System.out.println("In hierarchy");
                        }
                        else{
                            System.out.println("Not in this branch of property hierarchy");
                        }*/
                        //System.out.println("Ref Axioms: "+referencingAxioms3);
                        System.out.println("Object Property and Object Property turn");
                        System.out.println("Turn between "+edge+"and "+" "+nextEdgeObj);
                    }
                    break;
                }
                
            }
            System.out.println("SemDist is "+SemDist);
            System.out.println("Path processed is "+pathToProcess);
            if(edgeList.size()==1)
                {
                    List vertexList1 = pathToProcess.getVertexList();
                    SemDistShort = SemDist;
                    numberOfShortestPaths++;
                    if(vertexList1.contains(vertex))
                    {
                        numberOfShortestPathsThruVert++;
                        
                    }
                    sumOfShortestSemanticPaths=sumOfShortestSemanticPaths+SemDist;
                    // As only one edge, this is the shortest semantic path
                    System.out.println("Stop processing for other paths");
                    break;
                }
            // Detect up/down zigzag movements along taxonomical edges in the path
            // Check the values of SemDist and SemDistShort
            System.out.println("SemDist is "+SemDist+" SemDistShort is "+SemDistShort);
            /*if(SemDist == SemDistShort)
            {
                // Already path is shorter?
                numberOfShortestPathsThruVert--;
            }*/
            if(SemDist<SemDistShort||SemDistShort==0.0)
            {
                SemDistShort = SemDist;
                // Path got shorter
                List vertexList1 = pathToProcess.getVertexList();
                //Integer vertexInt = new Integer(vertex);
                /*if(vertexList1.contains(vertex))
                {
                    numberOfShortestPathsThruVert++;
                }
                numberOfShortestPaths++;*/
                ShortestPathIndices.add(pathIter);
                semDistShort.add(SemDist);
            }
            
            else if(SemDist==SemDistShort&&SemDist!=0)
            {
                //System.out.println("More than one semantic shortest paths exist");
                ShortestPathIndices.add(pathIter);
                semDistShort.add(SemDist);
                //numberOfShortestPaths++;
            }
            sumOfShortestSemanticPaths=sumOfShortestSemanticPaths+SemDist;
        //System.out.println("Path got shorter, SemDistShort is "+SemDistShort);
            
            //break;
            /*if(pathIter ==0)
        {
            SemDistShort = SemDist;
        }
        else if(SemDistShort>SemDist)
        {
            SemDistShort = SemDist;
        }*/
        }
        //System.out.println("Number of shortest paths is "+numberOfShortestPaths);
        // Find number of shortest paths
        Iterator shortestPathsIter = ShortestPathIndices.iterator();
        // SemDistShort has the shortest path length
        int index = 0;
        while(shortestPathsIter.hasNext())
        {
            // Take the index of each shortest path to get the distance
            int indexOfShortestPath = (int) shortestPathsIter.next();
            Double dist = semDistShort.get(index);
            if(SemDistShort==dist)
            {
                System.out.println("SemDistShort is "+SemDistShort+ " Dist is "+dist);
                numberOfShortestPaths++;
                // Get the path
                GraphWalk shortestPath = allPathsString.get(indexOfShortestPath);
                List vertexList = shortestPath.getVertexList();
                System.out.println("Vertex List is "+vertexList);
                System.out.println(vertexList);
                if(vertexList.contains(vertex))
                {
                    numberOfShortestPathsThruVert++;
                    
                }
            }
            index++;
        }
        // Print
        
        System.out.println("Number of shortest paths in this call is "+numberOfShortestPaths);
        System.out.println("Number of shortest paths through vertex "+vertex+"is "+numberOfShortestPathsThruVert);
        return numberOfShortestPaths;
    }
}
