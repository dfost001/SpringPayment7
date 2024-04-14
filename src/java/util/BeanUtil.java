/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import error_util.EhrLogger;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import view.attributes.CloneUtil;

/**
 *
 * @author dinah
 * 
 */
public class BeanUtil {
    
    /*
     * Can only be used for primary fields. Does not recurse through Object references. 
     */
    public static void evalNullOrEmptyFields(Class<?> cls, Object oinstance) {
        
        String uninitialized = "";
        
        Field[] fields = cls.getDeclaredFields();
       
        try {
            for (Field fld : fields) {
                int imodifiers = fld.getModifiers();
                if(Modifier.isFinal(imodifiers))
                    continue;
                String name = fld.getName();
                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method method = cls.getDeclaredMethod(methodName);
                Object value = method.invoke(oinstance);
                boolean invalid = CloneUtil.isEmptyOrNullValue(value);
                if(invalid)
                    uninitialized += name + ";";
             }
           
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
              
                throw new RuntimeException("BeanUtil#evalNullOrEmptyFields: " + e.getMessage(), e);
          }
        if(!uninitialized.isEmpty())
            doException(cls, "evalNullOrEmpty", uninitialized);
        
    }
    
    public static void throwFieldsNotInitialized(Class<?> cls, Object oinstance,
            String...fieldNames) {
        
        if(fieldNames == null)
             throw new IllegalArgumentException(EhrLogger.doError("BeanUtil", 
                     "throwFieldsNotInitialized", "fieldnames cannot be null"));
        
        String uninitialized = "";
        String debug = "";
        try {
            for (String name : fieldNames) {

                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

                Method method = cls.getDeclaredMethod(methodName);

                Object value = method.invoke(oinstance);

                if (CloneUtil.isEmptyOrNullValue(value)) {
                    uninitialized += name + ";";
                }
                else debug += name + ";";
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("BeanUtil#evalNullOrEmptyFields: " + e.getMessage(), e);
        }
        if (!uninitialized.isEmpty()) {
            uninitialized = uninitialized.substring(0, uninitialized.length()-1);
            doException(cls, "throwFieldsNotInitialized", uninitialized);
        }
        System.out.println("BeanUtil#throwFieldsNotInitialized:" +
                cls.getCanonicalName() + " exiting without error for " + debug);
    } 
    
     public static void throwEmptyCheckErrors(Class<?> cls, Object oinstance, 
             Errors errors, String...fieldNames) {
         
         if(fieldNames == null)
             throw new IllegalArgumentException(EhrLogger.doError("BeanUtil", 
                     "throwEmptyCheckErrors", "VarArg fieldnames cannot be null"));
         
         String uninitialized = "";
          try {
         
            for (String name : fieldNames) {

                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

                Method method = cls.getDeclaredMethod(methodName);

                Object value = method.invoke(oinstance);

                if (CloneUtil.isEmptyOrNullValue(value)) {
                    if(!resultHasFieldError(errors,name))
                        uninitialized += name + ";" ;
                }                
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("BeanUtil#throwEmptyCheckErrors: " + e.getMessage(), e);
        }
          
        if(!uninitialized.isEmpty())  
            doException(cls, "throwEmptyCheckErrors", uninitialized);        
    }
     
    private static boolean resultHasFieldError(Errors errors, String name) {
        
        if(!errors.hasFieldErrors()) return false;
        
        for(FieldError err : errors.getFieldErrors()) {
            if(err.getField().toLowerCase().contains(name.toLowerCase()))
                    return true;
        }
        
        return false;
    }
    
    public static void throwNotFullyInstantiated(Object obj, String info, 
            String ... exclude) {
        
        
        List<String> exList = new ArrayList<>();
        
        if(exclude != null)
              exList = Arrays.asList(exclude);
        
        try {
            
            String err = isFullyInstantiated(obj.getClass(), obj, exList);
            
            if(err != null) {
                err = info + ": " + err;
                throw new IllegalArgumentException(err);
                    
            }               
            
        } catch(IllegalAccessException | 
                NoSuchMethodException | InvocationTargetException e) {
            
            String msg = EhrLogger.doError("BeanUtil", "throwNotFullyInstantiated", e.getMessage());
            
            throw new IllegalArgumentException(msg, e);
        }        
    }
    /*
     * Bug: Cannot use the object#getClass to change recursion condition. Will always be 
     * the derived underlying class. Have to use the class definition parameter for the super class.
    */
    private static String isFullyInstantiated(Class<?> cls, Object obj, List<String> exclude)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        
        if(cls == Object.class) return null; //no error returned
        
        String err = evalFullyInstantiated(cls, obj, exclude);
        
        if(err == null) {
            
            Class<?> superClass = cls.getSuperclass();
            
           // System.out.println("isFullyInstantiated: superClass=" + superClass.getCanonicalName());
            
           // Object superObject = superClass.cast(obj); -- not required
            
            return isFullyInstantiated(superClass, obj, exclude);
        }
        
        return err;
    }
    
    private static String evalFullyInstantiated(Class<?> cls, Object obj, List<String> exclude)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        
        if(obj == null)
            return (cls.getCanonicalName() + " is not instantiated") ;
        
        String err = null;
        
        for(Field fld : cls.getDeclaredFields()) {
            
            //System.out.println("Field=" + fld.getName());
            
            if(exclude.contains(fld.getName()))
                continue;
            
            Method method = getMethod(fld.getName(), cls);
            
           // System.out.println("method=" + method.getName());
            
            Class<?> returnType = method.getReturnType();
            
            if(CloneUtil.isComplexValue(returnType)) {
                
                Object value = method.invoke(obj);
                
                err = evalFullyInstantiated(returnType, value, exclude);
                
                if(err != null)
                    break;
            }
        } //end for
        
        return err;
    }
    
    /*
     * Generate getter name to obtain a Method
    */
    private static Method getMethod(String name, Class<?> container) throws NoSuchMethodException  {
        
        Method method = null;
        
         String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

         method = container.getDeclaredMethod(methodName);
         
        return method;
    }
            
    private static void doException(Class<?> cls, String method, String fieldNames) {
        String message = "BeanUtil#" + method + ": " 
                + cls.getCanonicalName()
                + ": {" + fieldNames
                + "} - uninitialized property(s).";
        throw new IllegalStateException(message);
        
    }
}
