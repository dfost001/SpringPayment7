/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.util.Date;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

/**
 *
 * @author Dinah
 */
public class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar{

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        
        registry.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        
        registry.registerCustomEditor(Date.class, new MySimpleDateEditor());         
        
    }
    
}
