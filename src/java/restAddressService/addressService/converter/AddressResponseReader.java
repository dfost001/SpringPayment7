/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.addressService.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.MessageBodyReader;
import restAddressService.client.Candidates;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * Test IOException can be thrown from readFromStream after entity consumed
 */
public class AddressResponseReader implements MessageBodyReader<Candidates>{
    
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Candidates.class == type) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Candidates readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream entityStream) throws IOException {

        System.out.println("Entering MessageBodyReader");

        String editedXml = this.readFromStream(entityStream);
        
        System.out.println("AddressResponseReader#readFrom: editedXml=" + editedXml);

        Candidates candidates = (Candidates) this.unmarshall(editedXml, Candidates.class);

        return candidates;
    }

    private String readFromStream(InputStream entityStream) throws IOException {
        
        boolean testEx = false;
        
        String message = "";

        byte[] buffer = new byte[1024];
        
        String xml = "";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
      
        try {
            if (entityStream.available() == 0) {
                message = "Input stream contains no bytes.";
                throw new IOException(
                    doErrMsg(message, "readFromStream"));
            }

            int bytesRead = -1;

            while ((bytesRead = entityStream.read(buffer, 0, buffer.length)) > -1) {
                bos.write(buffer, 0, bytesRead);
            }
            
            xml = normalizeXmlDoc(bos.toString());
            
            if(testEx) {
                throw new IOException(doErrMsg("Testing IOException", "readFromStream"));
            }
            
        } catch (IOException ex) {
            message = "Error while reading Candidates.class from InputStream";
            throw new IOException(
                    doErrMsg(ex.getMessage() + ": " + message, "readFromStream"), ex);
        }

        return xml;
       
       //return bos.toString();

    }

    private String normalizeXmlDoc(String xml) throws IOException {

        int pos = xml.indexOf("<?xml");
        if (pos == -1) {
            throw new IOException(
               doErrMsg("Candidates XML doc could not be normalized", "normalizeXmlDoc"));
        }
        return xml.substring(pos);

    }

    private Object unmarshall(String xml, Class<?> cls) throws IOException {

        try {

            JAXBContext ctx = JAXBContext.newInstance(cls);

            Unmarshaller u = ctx.createUnmarshaller();

            Object o = u.unmarshal(new StreamSource(new StringReader(xml)));

            Candidates candidates = (Candidates) o;

            return candidates;

        } catch (JAXBException ex) {
            throw new IOException("Candidates could not be unmarshalled: " + xml, ex);
        } catch (ClassCastException ex) {
            throw new IOException("Cast from JAXBElement failed: " + xml, ex);
        }
    }
    
    private String doErrMsg(String message, String method) {
        
        String err = this.getClass().getCanonicalName() + "#" + method 
                + ": " + message;
        
        return err;
    }


}
