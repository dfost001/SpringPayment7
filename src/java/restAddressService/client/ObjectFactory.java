/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.client;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Dinah3
 */
@XmlRegistry
public class ObjectFactory {
    
    private static final QName requestQName = new QName("", "request");
    private static final QName candidatesQName = new QName("", "candidates");
    
    public ObjectFactory(){
        
    }
    
     public Request createRequest() {
        return new Request();
    }
     
     /**
     * Create an instance of {@link Request.Address }
     * 
     * @return 
     */
    public Request.Address createRequestAddress() {
        return new Request.Address();
    }
     
      /**
     * Create an instance of {@link Candidates }
     * 
     * @return 
     */
    public Candidates createCandidates() {
        return new Candidates();
    }

    /**
     * Create an instance of {@link Candidates.Candidate }
     * 
     * @return 
     */
    public Candidates.Candidate createCandidatesCandidate() {
        return new Candidates.Candidate();
    }

    /**
     * Create an instance of {@link Candidates.Candidate.Components }
     * 
     * @return 
     */
    public Candidates.Candidate.Components createCandidatesCandidateComponents() {
        return new Candidates.Candidate.Components();
    }

    /**
     * Create an instance of {@link Candidates.Candidate.Metadata }
     * 
     * @return 
     */
    public Candidates.Candidate.Metadata createCandidatesCandidateMetadata() {
        return new Candidates.Candidate.Metadata();
    }

    /**
     * Create an instance of {@link Candidates.Candidate.Analysis }
     * 
     * @return 
     */
    public Candidates.Candidate.Analysis createCandidatesCandidateAnalysis() {
        return new Candidates.Candidate.Analysis();
    }
    
    @XmlElementDecl(namespace="", name="request")
    public JAXBElement<Request> createRequest(Request value){
        return new JAXBElement<Request>(requestQName, Request.class, value);
    }

    @XmlElementDecl(namespace="", name="candidates")
    public JAXBElement<Candidates> createRequest(Candidates value){
        return new JAXBElement<Candidates>(candidatesQName, Candidates.class, value);
    }
}
