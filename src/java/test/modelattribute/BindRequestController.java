/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modelattribute;

import formatter.CustomPropertyEditorRegistrar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
/**
 *
 * @author Dinah
 * @SessionAttributes required if update handler on a different controller to
 * prevent Spring exception when binding to @ModelAttribute parameter
 * Therefore, only a single login with this configuration.
 * To do: Test @ModelAttribute at method-level on same controller with parameter-level annotation.
 */
@Controller
@SessionAttributes("bindModel")
public class BindRequestController {
    
  /*  @Autowired
    private CustomPropertyEditorRegistrar registrar; */
    
  /*  @InitBinder
    public void addCustomEditor(WebDataBinder binder) {
        
        registrar.registerCustomEditors(binder);
    }*/
    
    @ModelAttribute("bindModel")
    public void processBindModel(HttpServletRequest request, ModelMap map) {
        
        String message = "";
        
        BindModel bindModel = null;
        
       if(request.getParameter("cmdSelected").contains("Retrieve")) {
           bindModel = initializeBind();
           message = "BindRequestController#processBindModel: retrieve";
       }
       else {
           bindModel = new BindModel();
           message = "BindRequestController#processBindModel: create";
       }
       
       map.addAttribute("bindModel", bindModel);
       map.addAttribute("message", message);
    }
    
    @RequestMapping(value="/testBinding", method=RequestMethod.POST)
    public String retrieveModel() {
        
        return "bind_example/bindModel";
        
    }
    
    
    
    private BindModel initializeBind(){
        
        GregorianCalendar cal = new GregorianCalendar(1953,4,30);
        
        BindModel model = new BindModel();
        model.setAmount(new BigDecimal("10.00"));
        model.setBirthDate(new Date(cal.getTimeInMillis()));
        model.setCard("1234123412341234");
        model.setMonth(5);
        model.setYear(2020);
        model.setName("Dinah Foster");
        model.setPhone("5108368740");
        model.setStartDate(new Date());
        model.setStoreInfo(Boolean.TRUE);
        model.setType(BindModel.Card_Type.Visa);
        
        return model;
    }
    
}
