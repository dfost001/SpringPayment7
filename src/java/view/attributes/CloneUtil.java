/*
 * To do: boolean values may have getter "is" + Name
 */
package view.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.Map;



@Component
@Scope("application")
public class CloneUtil {
    
    @SuppressWarnings("rawtypes")
    public Object cloneCustomer(Object original) throws IllegalArgumentException {
        
        if(original == null)
            return null;
        
        //this.debug(original);

        Object cloned = null;

        Class<?> origCls = original.getClass();

        try {

            cloned = origCls.newInstance();          

            Field[] fields = getFieldList(origCls, new ArrayList<Field>());

          /*  System.out.println("CloneUtil#length of cloned fields=" 
                 + cls.getName() + ":" + fields.length);*/

            for (Field f : fields) {

                if (f.getType().getName().contains("Store")) {
                   // System.out.println("CloneUtil#skipping store");
                    continue;
                }

                String name = f.getName();
                
                //System.out.println("Field=" + name);
                
                Class<?> declaringClass = this.getDeclaringClassForMethod(f, origCls);
                
               // System.out.println("declaring class=" + declaringClass.getCanonicalName());

                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

                Method m = declaringClass.getDeclaredMethod(methodName);

                Object value = m.invoke(original);

               /* System.out.println("CloneUtil after invoke getter "
                        + f.getName() + "=" + value);*/ 
                
                Class<?> parameterType = m.getReturnType();
                
               // System.out.println("CloneUtil#parameterType=" + parameterType.getCanonicalName());
                
                if(isComplexValue(parameterType)){
                    
                    value = cloneCustomer(value); //recurse to fill nested object with values
                    
                }
                
               // parameterType = fixTimestamp(parameterType); //probably optional since getting return type revised
                
                Method setter = getSetter(name, declaringClass, parameterType);
                
               // value = fixTimestampToDate(value, parameterType);

                setter.invoke(cloned, value);
                
              

            }
        } catch (Exception ex) {
             throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        return cloned;
    }
    
    public static Field[] getFieldList(Class<?> cls, List<Field> list) {
        
       // System.out.println("CloneUtil#inside getFieldList:" + cls.getCanonicalName());
        
        Field[] fields = new Field[0];       
        
        if(cls == Object.class) //recursion returns
            
            return list.toArray(fields);
        
        else {
            
            fields = cls.getDeclaredFields();
            
            list.addAll(Arrays.asList(fields));
            
            return getFieldList(cls.getSuperclass(),list);
        }       
        
    }
    
    private Class<?> getDeclaringClassForMethod(Field field, Class<?> cls) {
        
        boolean found = false;
        
        if(cls == null) {
            throw new IllegalArgumentException(this.getClass().getCanonicalName()
                + "#getDeclaringClassForMethod: " + "field not found");
        }
        Field[] flds = cls.getDeclaredFields();
        for(Field f : flds) {
            if(f.equals(field)){
                found = true;
                break;
            }        
        }
        if(!found)            
            return getDeclaringClassForMethod(field,cls.getSuperclass());           
        return cls;
        
    }
    
	
	private Method getSetter(String fldName, Class<?> cls, Class<?> param) throws Exception{
		
		String methodName = "set" + fldName.substring(0,1).toUpperCase() + fldName.substring(1);
		
		Method m = cls.getDeclaredMethod(methodName, param);
				
		return m;
		
	}
        
        private Class<?> fixTimestamp(Class<?> parameter) {
            
            if(parameter == Timestamp.class)
               return java.util.Date.class;
            return parameter;
        }
        
        private Object fixTimestampToDate(Object value, Class<?> type) {
            
            if(type != Timestamp.class || value == null)
                return value;
            
            long stamp = ((Timestamp)value).getTime();
            
            return new Date(stamp);
        }
        
       
        
        /*
         * BigDecimal, BigInteger, Double, Float, Character
         */
         public static boolean isComplexValue(Class<?> cls)  {           
		
		//System.out.println("CloneUtil#isComplexValue " + fld.getName() + "=" + cls.getName());	
                
                boolean isComplex = true;
                
                
                if(cls.isArray())
                   isComplex = false;
                
                else if(Collection.class.isAssignableFrom(cls))
                    isComplex = false;
                
                else if(Map.class.isAssignableFrom(cls))
                   isComplex =  false;                    
		
                else if(cls == String.class)
			isComplex = false;
                
                else if(cls.getSuperclass() == Enum.class)
                        isComplex = false;
		
		else if(cls == Date.class)
			isComplex =  false;
		
		else if(cls == Short.class)
			isComplex =  false;
		
		else if(cls == Long.class)
			isComplex = false;
		
		else if(cls == Integer.class)
			isComplex =  false;
		
		else if(cls == Byte.class)
			isComplex =  false;
		
                else if(cls == Boolean.class)
                      isComplex = false;
                
                else if(cls == Timestamp.class)
                      isComplex =  false;
                
                else if(cls == boolean.class)
                      isComplex = false;
                
                else if(cls == Enum.class)
                      isComplex = false; 
                else if(cls == BigDecimal.class)
                      isComplex = false;
                
           // System.out.println("CloneUtil#isComplex=" + isComplex);
            
            return isComplex;
        }
	
        /*
         * Stub procedure. Not all types are evaluated. See isComplexValue
         */ 
	public static boolean isEmptyOrNullValue(Object value) {
            
            if(value == null)
                return true;
            
            Class<?> cls = value.getClass();
            
            String name = cls.getSimpleName();
            
           // System.out.println("CloneUtil#isEmptyValue: name=" + name);
            
            boolean empty = false;
            
            switch (name) {
                case "String":                 
                   if(value == null || ((String)value).isEmpty())
                        empty = true;
                   break;
                case "Short" :
                    if( value==null || ((short) value) == (short)0)
                        empty = true;
                    break;
                case "Integer"  : 
                    if( value == null || (Integer) value == 0)
                        empty = true;
                    break;
                case "Byte"  : 
                   if( value == null || ((Byte) value) == 0)
                        empty = true;
                   break;
                default: 
                    empty = false;    
            }
         //  System.out.println("CloneUtil#isEmptyValue:" + empty) ;
           return empty; 
        }  
        
       
       
    
}//end class
