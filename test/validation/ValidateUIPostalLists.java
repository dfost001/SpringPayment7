/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;
/*
 * The following configuration files are generating Runtime exceptions
 */
/*import configuration_1.ApplicationBeans;
import configuration_1.ExceptionResolverConfig;
import configuration_1.MvcBeans;
import configuration_1.MyWebMvcConfigurationSupport;
import configuration_1.TransactionConfig;
import configuration_1.ViewResolutionBeans;*/
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 * @author dinah
 */
@RunWith (Parameterized.class)
@ContextConfiguration(classes={TestConfig.class})
public class ValidateUIPostalLists {
    
    @Autowired
    private UiEvalUtil evalUtil;
    
    @ClassRule 
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule SPRING_METHOD_RULE = new SpringMethodRule();
    
    private Short countryId;
    private Short cityId;
    String district;
    String expectedErr;
    
    public ValidateUIPostalLists(Short country, Short city, String district, String expected) {
    
          countryId = country;
          cityId = city;
          this.district = district;
          expectedErr = expected;
          
    }
    @Test
    public void validateCountryCityDistrictFillTest() throws NoSuchMethodException {
        
       
        Method validate = this.getMethod();
        InvocationTargetException ite = assertThrows(InvocationTargetException.class,
                () -> validate.invoke(evalUtil, countryId, cityId, district));
        Throwable cause = ite.getCause();
        assertTrue(cause != null && cause instanceof IllegalArgumentException);
        assertTrue(cause != null && cause.getMessage().contains(expectedErr));
    }
    
    public Method getMethod() throws NoSuchMethodException {
        
        Class<UiEvalUtil> clsUiEval = UiEvalUtil.class;
        Method m = clsUiEval.getDeclaredMethod("validateCountryCityStateFill",
                Short.class, Short.class, String.class);
        m.setAccessible(true);
        return m;
    }
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
            {(short)0, (short)0, new String(), "Country selection failed" },
            { (short)103, (short)0, new String(), "City select fails"}    
          //  { (short)103, (short)26, new String(), "Country Id in selected City"}
         }               
        );
    }
    
}
