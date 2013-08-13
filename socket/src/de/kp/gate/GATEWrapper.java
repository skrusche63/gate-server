package de.kp.gate;

import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A wrapper for a GATE application
 *
 * This object parses arbitrary text using a GATE application supplied to the constructor.
 */
public class GATEWrapper {
	
   private CorpusController application;
   private Corpus corpus;

   /**
    * Initialize the GATE application
    *
    * @param gateHome path to an GATE application directory containing an application.xgapp in its root
    * @throws GateException
    * @throws IOException
    */
   public GATEWrapper(String gateHome) throws GateException, IOException {

	   Gate.runInSandbox(true);
	   Gate.setGateHome(new File(gateHome));
	   
	   Gate.setPluginsHome(new File(gateHome, "plugins"));
	   Gate.init();
	   
	   URL applicationURL = new URL("file:" + gateHome + "/application.xgapp");
	   System.out.println("GATE at: " + applicationURL);
	   
	   application = (CorpusController) PersistenceManager.loadObjectFromUrl(applicationURL);
	   corpus = Factory.newCorpus("GATE Corpus");
	   
	   application.setCorpus(corpus);
   
   }

   /**
    * Analyze text, returning annotations in XML
    * @param text the text to analyze
    * 
    * @return GATE XML annotation document
    * @throws ResourceInstantiationException
    * @throws ExecutionException
    */
   public String getAnnotation(String text) throws ResourceInstantiationException, ExecutionException {

	   Document document = Factory.newDocument(text);
	   annotateDocument(document);
	   
	   String xml = document.toXml();
	   Factory.deleteResource(document);
	   return xml;
   }

   private Document annotateDocument(Document document) throws ResourceInstantiationException, ExecutionException {
	   
      corpus.add(document);
      application.execute();
      
      corpus.clear();
      return document;
   
   }

   /**
    * Free resources associated with this object. This should be called before the object is deleted.
    */
   public void close() {
      Factory.deleteResource(corpus);
      Factory.deleteResource(application);
   }

}
