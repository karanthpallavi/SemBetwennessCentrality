/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sembetwennesscentrality;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import com.mycompany.semanticdistance.OWLEdge;
import com.mycompany.semanticdistance.IdentityArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectImpl;

/**
 *
 * @author DrKM
 */
public class TransformTriplesToGraph {
    public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> OWLGraph;
    public DirectedGraph<OWLEntity, DefaultEdge> OWLPropertyGraph;
    public DirectedGraph<Integer,DefaultEdge> OWLNonWeightedGraph;
    public List<OWLObjectImpl> entitiesOfGraph = new IdentityArrayList<>();
    public Set<OWLEntity> entitiesOfPropertyGraph = new HashSet<>();
    public List<OWLEdge> edgesOfGraph = new ArrayList<>();
    public List<String> axiomTypes = new ArrayList<>();
    public TransformTriplesToGraph(OWLOntology ontology)
    {
        double k1 = 0.1;
        double k2 = 0.5;
        double k3 = 0.7;
        double k4 = 0.9;
        System.out.println("In TransformTriplesToGraph");
        Set<OWLAxiom> axioms = ontology.getAxioms(true);
        OWLGraph = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        OWLNonWeightedGraph = new DefaultDirectedGraph<Integer,DefaultEdge>(DefaultEdge.class);
        for(OWLAxiom eachAxiom:axioms)
        {
            String axiomType = eachAxiom.getAxiomType().getName();
            if(axiomType.equals("ClassAssertion"))
            {
                Set<OWLEntity> entitiesInAxiom = eachAxiom.getSignature();
                Object[] toArray = entitiesInAxiom.toArray();
                Iterator entityIter = entitiesInAxiom.iterator();
                while(entityIter.hasNext())
                {
                    OWLEntity entity1 = (OWLEntity) entityIter.next();
                    OWLEntity entity2 = (OWLEntity) entityIter.next();
                   
                    System.out.println("In class assertion");
                    System.out.println(entity1);
                    System.out.println(entity2);
                    if(!entity1.isTopEntity())
                    {
                        if(!entity2.isTopEntity())
                        {
                            //OWLEdge edge = new OWLEdge(entity1,entity2);
                            //OWLEdge edge1 = new OWLEdge(entity2,entity1);
                            int indexOfEntity1 = 0;
                            int indexOfEntity2 = 0;
                            if(!entitiesOfGraph.contains(entity1))
                            {
                                System.out.println("Not found entity1, adding");
                                entitiesOfGraph.add((OWLObjectImpl)entity1);
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            else
                            {
                                // entity1 already in the list, get index
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            if(!entitiesOfGraph.contains(entity2))
                            {
                                System.out.println("Not found entity2, adding");
                                entitiesOfGraph.add((OWLObjectImpl)entity2);
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            else
                            {
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            OWLEdge edge = new OWLEdge(indexOfEntity2, indexOfEntity1);
                            System.out.println("Entity2 " + entity2 + " : " + indexOfEntity2);
                            System.out.println("Entity1 "+ entity1 + " : " + indexOfEntity1);
                            OWLNonWeightedGraph.addEdge(indexOfEntity2, indexOfEntity1);
                            OWLNonWeightedGraph.addEdge(indexOfEntity1, indexOfEntity2);
                             DefaultWeightedEdge e1 = OWLGraph.addEdge(indexOfEntity2, indexOfEntity1);
                             OWLGraph.setEdgeWeight(e1, k1);
                            edgesOfGraph.add(edge);
                            axiomTypes.add(axiomType);
                             DefaultWeightedEdge e2 = OWLGraph.addEdge(indexOfEntity1, indexOfEntity2);
                             OWLGraph.setEdgeWeight(e2, k1);
                            OWLEdge edge1 = new OWLEdge(indexOfEntity1, indexOfEntity2);
                            edgesOfGraph.add(edge1);
                            axiomTypes.add(axiomType); 
                        } // end of topEntity
                    } // end of if topEntity
                } // end of while iter
            } // end of if class assertion
            else if(axiomType.equals("ObjectPropertyAssertion"))
            {
                OWLObjectPropertyAssertionAxiom axiomAssertion = (OWLObjectPropertyAssertionAxiom) eachAxiom;
                OWLIndividual subj = axiomAssertion.getSubject();
                OWLIndividual obj = axiomAssertion.getObject();
                OWLEntity entity1 = (OWLEntity)subj;
                OWLEntity entity2 = (OWLEntity)obj;
                int indexOfEnt1 = 0;
                int indexOfEnt2 = 0;
                System.out.println("Subject in property is "+entity1);
                System.out.println("Object in property is "+entity2);
                
                if(!entitiesOfGraph.contains(entity1))
                {
                    entitiesOfGraph.add((OWLNamedIndividualImpl) subj);
                    indexOfEnt1 = entitiesOfGraph.indexOf(entity1);
                    System.out.println("Entity "+entity1+" Index: "+indexOfEnt1);
                    OWLGraph.addVertex(indexOfEnt1);
                    OWLNonWeightedGraph.addVertex(indexOfEnt1);
                }
                else
                {
                    indexOfEnt1 = entitiesOfGraph.indexOf(entity1);
                    System.out.println("Entity "+entity1+" Index: "+indexOfEnt1);
                    OWLGraph.addVertex(indexOfEnt1);
                    OWLNonWeightedGraph.addVertex(indexOfEnt1);
                }
                if(!entitiesOfGraph.contains(entity2))
                {
                    entitiesOfGraph.add((OWLObjectImpl)entity2);
                    indexOfEnt2 = entitiesOfGraph.indexOf(entity2);
                    System.out.println("Entity "+entity2+" Index: "+indexOfEnt2);
                    OWLGraph.addVertex(indexOfEnt2);
                    OWLNonWeightedGraph.addVertex(indexOfEnt2);
                }
                else
                {
                    indexOfEnt2 = entitiesOfGraph.indexOf(entity2);
                    OWLGraph.addVertex(indexOfEnt2);
                    System.out.println("Entity "+entity2+" Index: "+indexOfEnt2);
                    OWLNonWeightedGraph.addVertex(indexOfEnt2);
                }
                OWLNonWeightedGraph.addEdge(indexOfEnt1, indexOfEnt2);
                DefaultWeightedEdge e1 = OWLGraph.addEdge(indexOfEnt1, indexOfEnt2);
                if(e1!=null)
                {
                    OWLGraph.setEdgeWeight(e1, k3);
                }
                OWLEdge edge = new OWLEdge(indexOfEnt1, indexOfEnt2);
                edgesOfGraph.add(edge);
                axiomTypes.add(axiomType);
            }// end of if ObjectPropertyAssertion
            else if(axiomType.equals("SubClassOf"))
            {
                Set<OWLEntity> entitiesInAxiom = eachAxiom.getSignature();
                Iterator iterEntity = entitiesInAxiom.iterator();
                if(entitiesInAxiom.size()>0)
                {
                    int numberOfEntitiesInAxiom = entitiesInAxiom.size();
                    System.out.println("Axiom Size: "+numberOfEntitiesInAxiom);
                    if(numberOfEntitiesInAxiom==3)
                    {
                        eachAxiom.accept( new OWLObjectVisitor() {
					
					// found the subClassOf axiom  
				    public void visit( OWLSubClassOfAxiom subClassAxiom ) {
				    	
				    	// create an object visitor to read the underlying (subClassOf) restrictions
				    	subClassAxiom.getSuperClass().accept( new OWLObjectVisitor() {
				    		
						    public void visit( OWLObjectSomeValuesFrom someValuesFromAxiom ) {
						    	//printQuantifiedRestriction( oc, someValuesFromAxiom );
                                                        OWLQuantifiedObjectRestriction restriction = someValuesFromAxiom;
                                                        System.out.println("SomeValuesFrom Restriction");
                                                       //chooseWeight = true;
                                                        OWLEntity entityTwo = (OWLEntity) restriction.getFiller();
                                                        System.out.println(entityTwo);
                                                        OWLEntity entity1 = (OWLEntity) iterEntity.next();
                                                        OWLEntity entity2 = (OWLEntity) iterEntity.next();
                                                        OWLEntity entity3 = (OWLEntity) iterEntity.next();
                                                        System.out.println("Entity1"+ entity1);
                                                        System.out.println("Entity2"+ entity2);
                                                        System.out.println("Entity3"+ entity3);
                                                        if(!entity1.isTopEntity())
                                                        {
                                                            if(!entity2.isTopEntity())
                                                            {
                                                                int indexOfEntity1 = 0;
                            int indexOfEntity2 = 0;
                            if(!entitiesOfGraph.contains(entity1))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)entity1);
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            else
                            {
                                // entity1 already in the list, get index
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            if(!entitiesOfGraph.contains(entity2))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)entity2);
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            else
                            {
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            
                            if(entity2.containsEntityInSignature(entityTwo))
                            {
                                OWLNonWeightedGraph.addEdge(indexOfEntity1, indexOfEntity2);
                                DefaultWeightedEdge e2;
                            e2 = OWLGraph.addEdge(indexOfEntity1, indexOfEntity2);
                            if(e2!=null)
                            {
                                OWLGraph.setEdgeWeight(e2, k4);
                            }
                            
                            axiomTypes.add(axiomType);
                            OWLEdge edge = new OWLEdge(indexOfEntity1, indexOfEntity2);
                            edgesOfGraph.add(edge);
                            System.out.println("Indices of vertices are "+indexOfEntity1+ " and "+indexOfEntity2);
                            System.out.println("Adding edge between entities "+entity1+" and"+entity2);
                            }
                            else
                            {
                                OWLNonWeightedGraph.addEdge(indexOfEntity2, indexOfEntity1);
                                DefaultWeightedEdge e2;
                            e2 = OWLGraph.addEdge(indexOfEntity2, indexOfEntity1);
                            if(e2!=null)
                            {
                                OWLGraph.setEdgeWeight(e2, k4);
                            }
                            
                            axiomTypes.add(axiomType);
                            OWLEdge edge = new OWLEdge(indexOfEntity2, indexOfEntity1);
                            edgesOfGraph.add(edge);
                            System.out.println("Indices of vertices are "+indexOfEntity2+ " and "+indexOfEntity1);
                            System.out.println("Adding edge between entities "+entity2+" and"+entity1);
                            }
                            
                            //System.out.println("Added vertices are");
                            //System.out.println(OWLGraph.vertexSet());
                                                            }
                                                        }
						    }
                                                    public void visit( OWLObjectAllValuesFrom allValuesFromAxiom)
                                                    {
                                                        OWLObjectAllValuesFrom restriction = allValuesFromAxiom;
                                                        OWLEntity entityTwo = (OWLEntity) restriction.getFiller();
                                                        System.out.println(entityTwo);
                                                        OWLEntity entity1 = (OWLEntity) iterEntity.next();
                                                        OWLEntity entity2 = (OWLEntity) iterEntity.next();
                                                        OWLEntity entity3 = (OWLEntity) iterEntity.next();
                                                        System.out.println("Entity1"+ entity1);
                                                        System.out.println("Entity2"+ entity2);
                                                        System.out.println("Entity3"+ entity3);
                                                        System.out.println("AllValuesFrom Restriction");
                                                        
                                                        if(!entity1.isTopEntity())
                                                        {
                                                            if(!entity2.isTopEntity())
                                                            {
                                                                int indexOfEntity1 = 0;
                            int indexOfEntity2 = 0;
                            if(!entitiesOfGraph.contains(entity1))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)entity1);
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            else
                            {
                                // entity1 already in the list, get index
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            if(!entitiesOfGraph.contains(entity2))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)entity2);
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            else
                            {
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            if(entity2.containsEntityInSignature(entityTwo))
                            {
                                OWLNonWeightedGraph.addEdge(indexOfEntity1, indexOfEntity2);
                                DefaultWeightedEdge e2;
                            e2 = OWLGraph.addEdge(indexOfEntity1, indexOfEntity2);
                            if(e2!=null)
                            {
                                OWLGraph.setEdgeWeight(e2, k4);
                            }
                            
                            axiomTypes.add(axiomType);
                            OWLEdge edge = new OWLEdge(indexOfEntity1, indexOfEntity2);
                            edgesOfGraph.add(edge);
                            System.out.println("Indices of vertices are "+indexOfEntity1+ " and "+indexOfEntity2);
                            System.out.println("Adding edge between entities "+entity1+" and"+entity2);
                            }
                            else
                            {
                                OWLNonWeightedGraph.addEdge(indexOfEntity2, indexOfEntity1);
                                DefaultWeightedEdge e2;
                            e2 = OWLGraph.addEdge(indexOfEntity2, indexOfEntity1);
                            if(e2!=null)
                            {
                                OWLGraph.setEdgeWeight(e2, k4);
                            }
                            
                            axiomTypes.add(axiomType);
                            OWLEdge edge = new OWLEdge(indexOfEntity2, indexOfEntity1);
                            edgesOfGraph.add(edge);
                            System.out.println("Indices of vertices are "+indexOfEntity2+ " and "+indexOfEntity1);
                            System.out.println("Adding edge between entities "+entity2+" and"+entity1);
                            }
                                                            }
                                                        }
						    }
                                                    
                                                    public void visit( OWLObjectHasValue hasValueAxiom)
                                                    {
                                                        OWLObjectHasValue restriction = hasValueAxiom;
                                                        OWLEntity entity1 = (OWLEntity) iterEntity.next();
                                                        OWLEntity entity2 = (OWLEntity) iterEntity.next();
                                                        OWLEntity entity3 = (OWLEntity) iterEntity.next();
                                                        OWLObjectPropertyExpression property = restriction.getProperty();
                                                        System.out.println("Property is "+property);
                                                        boolean entity1NotProp = false;
                                                        boolean entity2NotProp = false;
                                                        int indexOfEntity1 = 0;
                                                        int indexOfEntity2 = 0;
                                                        int indexOfEntity3 = 0;
                                                        if(!entity1.equals(property))
                                                        {
                                                            entity1NotProp = true;
                                                            
                                                            if(!entitiesOfGraph.contains(entity1))
                                                            {
                                                                entitiesOfGraph.add((OWLObjectImpl)entity1);
                                                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                                                OWLGraph.addVertex(indexOfEntity1);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                                                            }
                                                            else
                                                            {
                                                                // entity1 already in the list, get index
                                                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                                                OWLGraph.addVertex(indexOfEntity1);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                                                            }
                                                        }
                                                        if(!entity2.equals(property))
                                                        {
                                                            entity2NotProp = true;
                                                            if(!entitiesOfGraph.contains(entity2))
                                                            {
                                                                entitiesOfGraph.add((OWLObjectImpl)entity2);
                                                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                                                OWLGraph.addVertex(indexOfEntity2);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                                                            }
                                                            else
                                                            {
                                                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                                                OWLGraph.addVertex(indexOfEntity2);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                                                            }
                                                        }
                                                        if(!(entity1NotProp&&entity2NotProp))
                                                        {
                                                            if(!entitiesOfGraph.contains(entity3))
                                                            {
                                                                entitiesOfGraph.add((OWLObjectImpl)entity3);
                                                                indexOfEntity3 = entitiesOfGraph.indexOf(entity3);
                                                                System.out.println("Entity "+entity3+" Index: "+indexOfEntity3);
                                                                OWLGraph.addVertex(indexOfEntity3);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity3);
                                                            }
                                                            else
                                                            {
                                                                // entity1 already in the list, get index
                                                                indexOfEntity3 = entitiesOfGraph.indexOf(entity3);
                                                                System.out.println("Entity "+entity3+" Index: "+indexOfEntity3);
                                                                OWLGraph.addVertex(indexOfEntity3);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity3);
                                                            }
                                                        }
                                                        
                                                        if(entity1NotProp)
                                                        {
                                                            if(entity2NotProp)
                                                            {
                                                                OWLNonWeightedGraph.addEdge(indexOfEntity1, indexOfEntity2);
                                                                DefaultWeightedEdge e2;
                                                                e2 = OWLGraph.addEdge(indexOfEntity1, indexOfEntity2);
                                                                if(e2!=null)
                                                                {
                                                                    OWLGraph.setEdgeWeight(e2, k2);
                                                                }
                            
                                                                axiomTypes.add(axiomType);
                                                                OWLEdge edge = new OWLEdge(indexOfEntity1, indexOfEntity2);
                                                                edgesOfGraph.add(edge);
                                                                System.out.println("Indices of vertices are "+indexOfEntity1+ " and "+indexOfEntity2);
                                                                System.out.println("Adding edge between entities "+entity1+" and"+entity2);
                                                            }
                                                            else
                                                            {
                                                                OWLNonWeightedGraph.addEdge(indexOfEntity1, indexOfEntity3);
                                                                DefaultWeightedEdge e2;
                                                                e2 = OWLGraph.addEdge(indexOfEntity1, indexOfEntity3);
                                                                if(e2!=null)
                                                                {
                                                                    OWLGraph.setEdgeWeight(e2, k2);
                                                                }
                            
                                                                axiomTypes.add(axiomType);
                                                                OWLEdge edge = new OWLEdge(indexOfEntity1, indexOfEntity3);
                                                                edgesOfGraph.add(edge);
                                                                System.out.println("Indices of vertices are "+indexOfEntity1+" and "+indexOfEntity3);
                                                                System.out.println("Adding edge between entities "+entity1+" and"+entity3);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            OWLNonWeightedGraph.addEdge(indexOfEntity2, indexOfEntity3);
                                                                DefaultWeightedEdge e2;
                                                                e2 = OWLGraph.addEdge(indexOfEntity2, indexOfEntity3);
                                                                if(e2!=null)
                                                                {
                                                                    OWLGraph.setEdgeWeight(e2, k2);
                                                                }
                            
                                                                axiomTypes.add(axiomType);
                                                                OWLEdge edge = new OWLEdge(indexOfEntity2, indexOfEntity3);
                                                                edgesOfGraph.add(edge);
                                                                System.out.println("Indices of vertices are "+indexOfEntity2+" and "+indexOfEntity3);
                                                                System.out.println("Adding edge between entities "+entity2+" and"+entity3);
                                                        }
                                                    }
                                                    
                                                });
                                                }
                                        });
                        
                    }
                    else if(numberOfEntitiesInAxiom>3)
                    {
                        eachAxiom.accept( new OWLObjectVisitor() {
					
					// found the subClassOf axiom  
				    public void visit( OWLSubClassOfAxiom subClassAxiom ) {
				    	
                                        // create an object visitor to read the underlying (subClassOf) restrictions
                                        OWLClassExpression subClass = subClassAxiom.getSubClass();
                                        System.out.println("Superclass" +subClass);
                                        int indexOfMainClass = 0;
                                        if(!entitiesOfGraph.contains(subClass))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)subClass);
                                indexOfMainClass = entitiesOfGraph.indexOf(subClass);
                                System.out.println("Entity "+subClass+" Index: "+indexOfMainClass);
                                OWLGraph.addVertex(indexOfMainClass);
                                OWLNonWeightedGraph.addVertex(indexOfMainClass);
                            }
                            else
                            {
                                // entity1 already in the list, get index
                                indexOfMainClass = entitiesOfGraph.indexOf(subClass);
                                System.out.println("Entity "+subClass+" Index: "+indexOfMainClass);
                                OWLGraph.addVertex(indexOfMainClass);
                                OWLNonWeightedGraph.addVertex(indexOfMainClass);
                            }
				    	subClassAxiom.getSuperClass().accept( new OWLObjectVisitor() {
                                            public void visit( OWLObjectAllValuesFrom allValuesFromAxiom)
                                                    {
                                                        //OWLEntity entity1 = (OWLEntity) iterEntity.next();
                                                        //System.out.println("Entity1: "+entity1);
                                                        OWLObjectAllValuesFrom restriction = allValuesFromAxiom;
                                                        System.out.println("Fillers of allValuesFrom");
                                                        System.out.println(restriction.getFiller());
                                                        Set<OWLClass> classesInUnion = restriction.getClassesInSignature();
                                                        Iterator iter = classesInUnion.iterator();
                                                        int indexOfEntity = 0;
                                                        while(iter.hasNext())
                                                        {
                                                            OWLEntity entity = (OWLEntity) iter.next();
                                                            System.out.println(entity);
                                                            if(!entitiesOfGraph.contains(entity))
                                                            {
                                                                entitiesOfGraph.add((OWLObjectImpl)entity);
                                                                indexOfEntity = entitiesOfGraph.indexOf(entity);
                                                                System.out.println("Entity "+entity+" Index: "+indexOfEntity);
                                                                OWLGraph.addVertex(indexOfEntity);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity);
                                                            }
                                                            else
                                                            {
                                                                // entity1 already in the list, get index
                                                                indexOfEntity = entitiesOfGraph.indexOf(entity);
                                                                System.out.println("Entity "+entity+" Index: "+indexOfEntity);
                                                                OWLGraph.addVertex(indexOfEntity);
                                                                OWLNonWeightedGraph.addVertex(indexOfEntity);
                                                            }
                                                            int index = entitiesOfGraph.indexOf(subClass);
                                                            OWLNonWeightedGraph.addEdge(index, indexOfEntity);
                                                            DefaultWeightedEdge e2;
                                                            e2 = OWLGraph.addEdge(index, indexOfEntity);
                                                            if(e2!=null)
                                                            {
                                                                OWLGraph.setEdgeWeight(e2, k4);
                                                            }
                            
                                                            axiomTypes.add(axiomType);
                                                            OWLEdge edge = new OWLEdge(index, indexOfEntity);
                                                            edgesOfGraph.add(edge);
                                                            System.out.println("Indices of vertices are "+index+ " and "+indexOfEntity);
                                                            System.out.println("Adding edge between entities "+subClass+" and"+entity);
                                                            
                                                        }
                                                    }
                                        });
                                                }
                        });
                        
                    }
                    else
                    {
                    OWLEntity entity1 = (OWLEntity) iterEntity.next();
                    OWLEntity entity2 = (OWLEntity) iterEntity.next();
                    if(!entity1.isTopEntity())
                    {
                        if(!entity2.isTopEntity())
                        {
                            // Commenting below two lines as we are now having graph of vertices only and not OWLEntity and OWLEdge
                            //OWLEdge edge = new OWLEdge(entity1,entity2);
                            //OWLEdge edge1 = new OWLEdge(entity2,entity1);
                            /*edgesOfGraph.add(edge);
                            edgesOfGraph.add(edge1);
                            entitiesOfGraph.add(entity1);
                            entitiesOfGraph.add(entity2);*/
                            int indexOfEntity1 = 0;
                            int indexOfEntity2 = 0;
                            if(!entitiesOfGraph.contains(entity1))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)entity1);
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            else
                            {
                                // entity1 already in the list, get index
                                indexOfEntity1 = entitiesOfGraph.indexOf(entity1);
                                System.out.println("Entity "+entity1+" Index: "+indexOfEntity1);
                                OWLGraph.addVertex(indexOfEntity1);
                                OWLNonWeightedGraph.addVertex(indexOfEntity1);
                            }
                            if(!entitiesOfGraph.contains(entity2))
                            {
                                entitiesOfGraph.add((OWLObjectImpl)entity2);
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            else
                            {
                                indexOfEntity2 = entitiesOfGraph.indexOf(entity2);
                                System.out.println("Entity "+entity2+" Index: "+indexOfEntity2);
                                OWLGraph.addVertex(indexOfEntity2);
                                OWLNonWeightedGraph.addVertex(indexOfEntity2);
                            }
                            System.out.println("Indices of vertices are "+indexOfEntity1+ " and "+indexOfEntity2);
                            System.out.println("Adding edge between entities "+entity1+" and"+entity2);
                            System.out.println("Added vertices are");
                            System.out.println(OWLGraph.vertexSet());
                            OWLNonWeightedGraph.addEdge(indexOfEntity2, indexOfEntity1);
                            OWLNonWeightedGraph.addEdge(indexOfEntity1, indexOfEntity2);
                            DefaultWeightedEdge e2;
                            e2 = OWLGraph.addEdge(indexOfEntity1, indexOfEntity2);
                            if(e2!=null)
                            {
                                OWLGraph.setEdgeWeight(e2, k1);
                            }
                            
                            axiomTypes.add(axiomType);
                            OWLEdge edge = new OWLEdge(indexOfEntity2, indexOfEntity1);
                            DefaultWeightedEdge e1 = OWLGraph.addEdge(indexOfEntity2, indexOfEntity1);
                            System.out.println("Added edges are");
                            System.out.println(OWLGraph.edgeSet());
                            if(e1!=null)
                            {
                                OWLGraph.setEdgeWeight(e1, k1);
                            }
                            edgesOfGraph.add(edge);
                            axiomTypes.add(axiomType);
                            OWLEdge edge1 = new OWLEdge(indexOfEntity1, indexOfEntity2);
                            edgesOfGraph.add(edge1);
                        }
                        }
                    }// End of else (Axiom Size ==2 and not 3)
                }
            } // end of if subClassOf
        } // end of for loop
        System.out.println("Graph formed");
            //System.out.println(OWLGraph);
            System.out.println("Edges are:");
            System.out.println(OWLGraph.edgeSet());
            System.out.println("Vertices are:");
            System.out.println(OWLGraph.vertexSet());
            
            // Construct OWL Property Hierarchy Tree
            OWLPropertyGraph = new DefaultDirectedGraph<OWLEntity, DefaultEdge>(DefaultEdge.class);
            Iterator iterEnt = entitiesOfPropertyGraph.iterator();
            //Iterator iterEdg = edgesOfPropertyGraph.iterator();
            
            while(iterEnt.hasNext())
            {
                OWLEntity entity = (OWLEntity) iterEnt.next();
                OWLPropertyGraph.addVertex(entity);
            }
            System.out.println("Property hierarchy graph formed");
            System.out.println("Edges in property tree are");
            System.out.println(OWLPropertyGraph.edgeSet());
            
            System.out.println("Entities of Graph ");
            System.out.println(entitiesOfGraph);
            
            
            // Write to file
            BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			//String content = "This is the content to write into file\n";
                        //fw = new FileWriter("E:/Pallavi/Ontology/OWLentities_Food_Betweenness.txt");
                        //fw = new FileWriter("E:/Pallavi/Ontology/OWLentities_CitationNetworkTopCitedTopTwoPercent_Betweenness.txt");
                        fw = new FileWriter("E:/Pallavi/Ontology/OWLentities_MahabharataInferredV2_Betweenness.txt");
			//fw = new FileWriter("E:/Pallavi/Ontology/OWLentities_Mahabharata_Betweenness.txt");
                        //fw = new FileWriter("E:/Pallavi/Ontology/OWLentities_Movie_Betweenness.txt");
			bw = new BufferedWriter(fw);
			//bw.write(content);
                        bw.write(entitiesOfGraph.toString());

			System.out.println("Done writing OWL Entities to file");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

    } // end of constructor
    
    private static void print(OWLObjectPropertyAssertionAxiom a) {
    OWLObjectVisitor v = new OWLObjectVisitor() {
        @Override
        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            System.out.println(
                axiom.getProperty() + " " + axiom.getSubject() + " " + axiom.getObject());
        }
    };
    a.accept(v);
    } // end of print
}
class myComp implements Comparator<OWLEntity>
{
    @Override
    public int compare(OWLEntity entity1, OWLEntity entity2)
    {
        return entity1.compareTo(entity2);
    }
}
