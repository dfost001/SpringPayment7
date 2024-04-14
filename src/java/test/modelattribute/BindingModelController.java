/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modelattribute;

import formatter.CustomPropertyEditorRegistrar;

import formatter.MyDateEditor2;
import formatter.MySimpleDateEditor;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
/**
 *
 * @author Dinah
 */
@Controller
@SessionAttributes("bindModel")
public class BindingModelController {
    
    @Autowired
    private BirthDateValidator birthDateValidator;   
   
    
   /* @InitBinder
    public void initService(WebDataBinder binder) {
        
        binder.setConversionService(convService);
    }*/
    
    /*@Autowired
    private CustomPropertyEditorRegistrar editorRegistrar; */
    

   /* @InitBinder
    public void registerBinder(WebDataBinder binder){
        editorRegistrar.registerCustomEditors(binder);
    } */
    
   /*  @InitBinder
    public void initBinderDateStart(WebDataBinder binder){  
        
       BindingResult result = binder.getBindingResult();    
       
       binder.registerCustomEditor(Date.class, "startDate",
               new MyDateEditor2(result, "startDate", "bindModel"));
       
       binder.registerCustomEditor(Date.class, "birthDate",
               new MyDateEditor2(result, "birthDate", "bindModel"));
       
       binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
       
       //binder.setFieldMarkerPrefix("chkStore");        
      
    } */
    
   /* @InitBinder
    public void initDateEditor(WebDataBinder binder) {
        
        binder.registerCustomEditor(Date.class, new MySimpleDateEditor());
        
    }*/
            
    
   @InitBinder(value="bindModel")
    public void initBinderDateBirth(WebDataBinder binder){  
        
       binder.addValidators(birthDateValidator);
    }   
    
       
   
    @RequestMapping(value="/updateBindModel", method=RequestMethod.POST)
    public String testBinding(@Validated @ModelAttribute("bindModel")  BindModel bindModel,
            BindingResult result, ModelMap map) {
        
               
        
        if(result.hasErrors()) {
            map.addAttribute("msg", "Binding Result has errors.");
            printErrors(result);
        }
        else {
            map.addAttribute("msg", "There are no errors");
            System.out.println("BindingModelController:testBinding:There are no errors");
        }
        map.addAttribute("bindModel", bindModel);
        return "bind_example/bindModel";
        
    }   
    
   /* @RequestMapping(value="/testBinding", method=RequestMethod.POST)
    public String testBinding(@ModelAttribute("bindModel") BindModel bindModel,
             ModelMap map) {
        
               
       // birthDateValidator.validate(bindModel,result);
        
        if(result.hasErrors()) {
            map.addAttribute("msg", "Binding Result has errors.");
            printErrors(result);
        }
        else {
            map.addAttribute("msg", "There are no errors");
            System.out.println("BindingModelController:testBinding:There are no errors");
        }
        map.addAttribute("bindModel", bindModel);
        return "bind_example/bindModel";
        
    } */
    
   /* @RequestMapping(value="/createBind", method=RequestMethod.GET)
    public String createBindModel(ModelMap map){
        map.addAttribute("bindModel", new BindModel());
        return "bind_example/bindModel";
    } */
    
    private void printErrors(BindingResult result) {
        
        List<FieldError> list = result.getFieldErrors();
        
        String msg = "";
        
        for(FieldError f : list) {
            String field = f.getField();
            //String value = (String)f.getRejectedValue();
            String info = f.getDefaultMessage();
            
            msg += field + ": "  + info + "\n";
            
        }
        System.out.println(msg);
        
    }   
   
    
}// end class
