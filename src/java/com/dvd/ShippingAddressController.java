/*
 * Revised: selectedShipAddress removed from @SessionAttributes
 */
package com.dvd;

import com.cart.Cart;
import dao.exception.CustomerNotFoundException;
import exceptions.ConfirmCartException;
import exceptions.shipAddressController.ExpiredEditViewRequest;
import exceptions.SelectedShipAddressCompareException;
import exceptions.shipAddressController.UnequalHashTimeCurrentException;
import exceptions.shipAddressController.LoginIdChangedException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.customer.AddressTypeEnum;
import model.customer.Customer;
import model.customer.ShipAddress;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import validation.CompareAddressUtil2;
import validation.CustomerAttrsValidator;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;
import error_util.EhrLogger;

/**
 * Change view to create a form for each address in list, then the field values 
 * of the entity selected can be compared against the values for that entity ID
 * in the current updated customer relation.
 */
@Controller
@SessionAttributes({ConstantUtil.CUSTOMER_SESSION_KEY, ConstantUtil.CUST_BINDINGRESULT_KEY})
@Scope("request")
public class ShippingAddressController {
    
    /*
     * Iterated by the EL to create the view
     */    
    private List<? super PostalAddress> addressList;
    
    /*
     * @SessionAttributes on next target controller VerifySvcResultsController
     */
    public static final String SELECTED_POSTALADDRESS = "selectedShipAddress";  
    
    /* 
     * Key used by UpdateShipAddressController to extract entity from the session 
     * 
     */
    public static final String EDIT_SHIPADDRESS = "shipAddress";
    
    /*
     * Property read by UpdateShipAddressController to determine the type of edit
     * see enum SelectAddressAction
     */    
    private SelectShipAction selectShipAction; 
    
    public SelectShipAction getSelectShipAction() {
        return selectShipAction;
    }   
    
    /*
     * Added to RedirectAttributes on this controller (see addExpiredRequestAttributes)
     */
    private final String EXPIRED_REQUEST_MSG = "expiredMsg";
    
    /*
     * Edit request URL's
     */    
    private final String SHIPADDRESS_EDIT_FORWARD = "forward:/updateShipAddress/requestUpdateView";
    
    private final String SHIPADDRESS_ADD_URL = "forward:/updateShipAddress/create";
    
    private final String CUSTOMER_EDIT_FORWARD = "forward:/customerRequest";
    
    private final String ALERT_REDIRECT_URL = "redirect:/shippingAddress/showSelect" ;
    
    private final String HOME_URL = "/shippingAddress/showSelect" ;
    
    /*
     * Obtain Session see configureShipAddressUrl
     */    
    private HttpServletRequest servletRequest;
    
    private RedirectAttributes redirectAttributes;
    
    private String expiredName;
    
    public enum SelectShipAction {
        Create,
        Edit,
        Select,
        Delete,
        Show
    };     
    
    /*
     * Properties accessed by the EL
     */
    
    private Integer previousSelected;       
    
    private List<String> hashList;
    
    private String shipAddressNotFoundMessage;    
    
    /*
     * Dependencies
    */
    
    @Autowired
    private CustomerAttrsValidator attrsValidator;
    
    @Autowired
    private Cart cart;   
    
    @Autowired
    private CustomerAttributes customerAttrs; 
    
    @Autowired
    private CompareAddressUtil2 compareUtil;

    public String getShipAddressNotFoundMessage() {
        return this.shipAddressNotFoundMessage;
    }

    public List<? super PostalAddress> getAddressList() {
        return addressList;
    }
    
    public List<String> getHashList() {
        return hashList;
    }

    public Integer getPreviousSelected() {
        return previousSelected;
    }   
   
   /*
    * Note: setPreviousSelectedIndex will evaluate a null session address, and
    * must be invoked before assignAddressNotFoundMessage
    */
   @RequestMapping(value="/shippingAddress/showSelect", method=RequestMethod.GET)
    public String doShowSelectAddress(
            @ModelAttribute(ConstantUtil.CUSTOMER_SESSION_KEY) Customer customer, 
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult bindingResult, 
            HttpSession session, ModelMap map)
            throws ConfirmCartException,
            SelectedShipAddressCompareException,
            CustomerNotFoundException {            
        
        this.processCustomer(customer, bindingResult, SelectShipAction.Show);
        
        this.createAddressList(customer, customer.getShipAddressList());
        
        hashList = compareUtil.generateHashList(this.addressList); 
        
        customerAttrs.setFormTime();
        
        EhrLogger.printToConsole(this.getClass(),"doShowSelectAddress", 
                 "showSelect executing: Current Time=" + customerAttrs.getFormTime());
        
        this.setPreviousSelectedIndex(session, customer);  
        
        this.assignAddressNotFoundMessage(map, session, customer);
        
        addSelectModelAttributes(map,customer);       
                
        return "selectShippingAddress";
    }

  @RequestMapping(value="/shippingAddress/create", method=RequestMethod.POST)
    public String doShipAddressNew(@RequestParam("formTime") Long time,
            @ModelAttribute("customer") Customer customer,
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult customerResult, 
            ModelMap map, RedirectAttributes redirectAttrs)
                      throws ConfirmCartException {
        
        this.selectShipAction = SelectShipAction.Create;
        
        this.redirectAttributes = redirectAttrs;
        
        this.expiredName = "A new record" ;
        
        this.processCustomer(customer, customerResult, selectShipAction); 
        
        if(!time.equals(customerAttrs.getFormTime())) {
            
           this.addExpiredMsgRequestAttribute(AddressTypeEnum.ShipAddress, null);
            
           return this.ALERT_REDIRECT_URL;
            
        }
        
        customerAttrs.setShipAddressSuccessMsg("");
        
        return this.SHIPADDRESS_ADD_URL;
    }


   @RequestMapping(value="/shippingAddress/customerAction/{index}", method=RequestMethod.POST)
    public String doSelectCustomerAction(@RequestParam("selectedAddress") String chkIdentifier,
            @RequestParam("hashCode") String hash,
            @RequestParam("formTime")Long formTime,
            @RequestParam("addressNameUpdated") String expiredName,
            @RequestParam("cmdName") SelectShipAction actionTypeEnum,
            @ModelAttribute(ConstantUtil.CUSTOMER_SESSION_KEY) Customer customer, 
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult bindingResult,
            HttpServletRequest request,            
            ModelMap  map,             
            RedirectAttributes redirectAttrs)
            throws ConfirmCartException {         
      
        String url = ""; 
        
        this.servletRequest = request;
        
        this.redirectAttributes = redirectAttrs;
        
        this.selectShipAction = actionTypeEnum; //property read by UpdateShipAddressController
        
        this.expiredName = expiredName;
        
        this.processCustomer(customer, bindingResult, actionTypeEnum);
                   
        this.createAddressList(customer, customer.getShipAddressList());
        
        Integer index = Integer.parseInt(chkIdentifier.substring(chkIdentifier.indexOf("_") + 1));
        
        Short id = Short.parseShort(chkIdentifier.substring(0,chkIdentifier.indexOf("_"))); 

        AddressTypeEnum addressType = index == 0 ?  AddressTypeEnum.Customer : AddressTypeEnum.ShipAddress ;   
        
           switch(actionTypeEnum){
              
               case Edit : 
               case Delete :  
                   
                   url = this.processEdit(id, hash, formTime, addressType, customer);                  
                   
                   break; 
                   
               case Select :                        
                    
                    url = "redirect:/customer/verifySvcResults";                
                   
                        
                    PostalAddress postalAddress = this.processSelect(addressType,
                                hash, id, formTime, customer); //throws IllegalArgument
                        
                    customerAttrs.setShipAddressSelected(true);  //Set flag                  
                        
                    redirectAttrs.addFlashAttribute(SELECTED_POSTALADDRESS, postalAddress);           
                    
                    break;             
               default: 
                  
                   this.throwIllegalArg("doSelectCustomerAction", 
                           "Converter for enum 'SelectShipAction' failed to throw exception.");
                   
           } //end switch  
       
        EhrLogger.printToConsole(this.getClass(),"doSelectCustomerAction", 
                 "exiting: Current Time=" + customerAttrs.getFormTime()
                 + " Paremeter Time=" + formTime + " URL=" + request.getRequestURL().toString());    
          
       return url; 
    } 

    @RequestMapping(value=UpdateShipAddressController.PREVIOUS_DELETED_URL,
            method=RequestMethod.GET)
    public String handlePreviousDeletionExpiredUpdate
                            (ModelMap map, RedirectAttributes redirectAttrs) {
                                
        String msg = (String)map.get(ConstantUtil.SHIPADDRESS_PREVIOUS_DELETION_MSG)  ;                      
        
        if(msg != null) {
           msg = "PREVIOUS DELETION: " + msg;
           redirectAttrs.addFlashAttribute(ConstantUtil.SHIPADDRESS_PREVIOUS_DELETION_MSG, msg);
        }
        
        return "redirect:/shippingAddress/showSelect";
                                
    }
    
    @RequestMapping(value="/shippingAddress/handleRedirectAddressCompareException",
                    method= RequestMethod.GET)
    public String handleCompareExceptionRedirect(HttpSession session, RedirectAttributes redirectAttrs){
        
        System.out.println("ShippingAddressController#handleCompareExceptionRedirect: executing");
        
        SelectedShipAddressCompareException exception = (SelectedShipAddressCompareException)
                session.getAttribute(CompareAddressUtil2.COMPARE_EXCEPTION);
        
        if(exception != null)        
            redirectAttrs.addFlashAttribute(CompareAddressUtil2.COMPARE_EXCEPTION,exception);         
        
        session.removeAttribute(CompareAddressUtil2.COMPARE_EXCEPTION);          
        
        return "redirect:/shippingAddress/showSelect";
        
    }    
    
    @RequestMapping(value="/shippingAddress/handlePayPalAddressError", method=RequestMethod.GET)
    public String handlePayPalValidationErrorRedirect(){
        
        return "redirect:/shippingAddress/showSelect" ;
        
    }
     
      private void processCustomer(Customer customer, BindingResult errors, 
            SelectShipAction command) 
              throws ConfirmCartException, CustomerNotFoundException, IllegalArgumentException
      {     
        
        this.throwConfirmCartException(customer, errors, command);
        compareUtil.compareCustomerToDb(customer, "ShippingAddressController#processCustomer"); //throws IllegalArgumentException     
    
      }          
      
    private void throwConfirmCartException(Customer customer, BindingResult errors, 
            SelectShipAction command) throws ConfirmCartException {
        
         try {
            
            attrsValidator.evaluateConfirmCartState(customer, cart, errors);
            
        } catch(ConfirmCartException ex){
            
            switch(command) {
                
                case Show:
                case Edit:
                case Create:
                case Delete:    
                    
                    throw ex;
                    
                case Select:
                    
                    throw new IllegalArgumentException(
                       EhrLogger.doError(this.getClass().getCanonicalName(),
                       "processAddressList", ex.getMessage())); 
                    
                default : 
                    
                    throw new IllegalArgumentException(EhrLogger.doError(
                       this.getClass().getCanonicalName(), "throwInvalidPaymentState", 
                       "Unknown switch command" ));    
                    
            }//end switch
        } //end catch 
    } //end evaluate
      
     private void createAddressList(Customer customer, List<ShipAddress> shipAddressList) {
        
        addressList = new ArrayList<>();
        
        addressList.add(0, customer);  
        
        if(shipAddressList == null || shipAddressList.isEmpty())
            return;        
       
       addressList.addAll(shipAddressList);
        
    }  
     
    private String processEdit(Short paramId, String hash, Long time,
           AddressTypeEnum addressType, 
           Customer customer)  {
       
        try {
            
           EhrLogger.printToConsole(this.getClass(), "processEdit", 
                   "paramId=" + paramId + " Customer=" + customer.getCustomerId());
            
            this.evalId(paramId, addressType, customer, time, hash);           
        
        } catch (LoginIdChangedException e) {
            
            this.addExpiredMsgRequestAttribute(addressType, e.getMessage());            
            return this.ALERT_REDIRECT_URL;
            
        } catch (ExpiredEditViewRequest e) {
            
             this.addExpiredMsgRequestAttribute(addressType, e.getMessage());            
             return this.ALERT_REDIRECT_URL;
             
        } catch (UnequalHashTimeCurrentException e) {
            
            this.throwIllegalArg("processEdit", e.getMessage());
            
        }        
       return this.configureEditUrl(addressType, paramId);       
   } //end processEdit
    
     private void addExpiredMsgRequestAttribute(AddressTypeEnum type, String detail) {      
        
        String expiredMessage = type + " " + 
                this.getSelectShipAction() + ": ";      
        
        expiredMessage += detail;
        
        this.redirectAttributes.addFlashAttribute(this.EXPIRED_REQUEST_MSG, expiredMessage);        
    } 
    /*
     * Invoked from processEdit after ensuring ID param finds an entity
    */
    private String configureEditUrl(AddressTypeEnum type, Short id) {
        
        String url = "";
        
        if(AddressTypeEnum.Customer.equals(type)) {
            
            url = this.CUSTOMER_EDIT_FORWARD;
            
            this.customerAttrs.setCancelCustEditUrl(this.HOME_URL);
        }
        
        else if(AddressTypeEnum.ShipAddress.equals(type)) {
            
            int index = this.searchList(id, type);
            
            if(index == -1)
                throwIllegalArg("configureEditUrl", "Error-checking failed to trap"
                    + " ShipAddress cannot be searched in related Customer list. ");
            
            ShipAddress shipAddress = (ShipAddress)this.addressList.get(index);
            
            servletRequest.getSession().setAttribute
                   (ShippingAddressController.EDIT_SHIPADDRESS, shipAddress);
            
            url = this.SHIPADDRESS_EDIT_FORWARD;
            
        }
        
        this.clearUpdateMessages();
        
        return url;        
    }
   
   private PostalAddress processSelect(AddressTypeEnum addressType,
            String hash, Short id, Long formTime, Customer customer) {        
      
        
        try {
            
            evalId(id, addressType, customer, formTime, hash);
            
        } catch (UnequalHashTimeCurrentException | 
                 LoginIdChangedException | ExpiredEditViewRequest e) {
            
            this.throwIllegalArg("processSelect", e.getMessage());
        }           
        
        int index = searchList(id, addressType);
       
        if(index == -1) {
           
           this.throwIllegalArg("processSelect", "ShipAddress not found in related list");
       }  

       this.clearUpdateMessages();         
       
       return (PostalAddress)this.addressList.get(index);
        
        
    } //end throw  
   
    private void evalId(Short paramId,AddressTypeEnum addressType, 
            Customer modelCustomer, Long formTime, String formHash)
               throws UnequalHashTimeCurrentException, 
                      LoginIdChangedException,
                      ExpiredEditViewRequest {
        
        if (addressType.equals(AddressTypeEnum.ShipAddress)) {

                this.compareUtil.evalShipIdParam(modelCustomer, paramId, formTime, formHash);

        } else if (addressType.equals(AddressTypeEnum.Customer)) {

                this.compareUtil.evalCustomerIdParam(paramId, modelCustomer, formTime, formHash);
        }
        
    } 
  
    
   private int searchList(Short id, AddressTypeEnum addressType) {
       
       int found = -1;
       
       if(addressType == AddressTypeEnum.Customer)
           return 0;
       
       for (int i = 1; i < this.addressList.size(); i++) {

            ShipAddress shipAddress = (ShipAddress)addressList.get(i);
               
            if (shipAddress.getShipId().equals(id)) {
                    
                    found = i;
                    break;
            }
       }
       return found;
   } //end search    
    
   /*
    * Is it possible to check consistency between session and Db if ShipAddress
    * not selected?
    */
    private void setPreviousSelectedIndex(HttpSession session, Customer customer) {
        
       previousSelected = null;
        
       PostalAddress address = (PostalAddress)session.getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
            
       compareUtil.checkInconsistentSelectedFlag(address);    
        
       if (address == null) {
            
                this.previousSelected = 0;
                return;
        }
       
        if(Customer.class.isAssignableFrom(address.getClass())){
            
           /* Possible to compare ID of customer selected to model */
            
            this.previousSelected = 0;
            return;
        }       
        
        ShipAddress selectedAddress = (ShipAddress)address;  //Session selected attribute   
        
        compareUtil.throwInconsistentSessionToDbDeletion(customer, selectedAddress);
        
        previousSelected = this.searchList(selectedAddress.getShipId(), AddressTypeEnum.ShipAddress);       
              
    }
    
    /*
     * Order of evaluation is important
     */
    private void assignAddressNotFoundMessage(ModelMap model,
            HttpSession session, Customer customer) 
            throws SelectedShipAddressCompareException {     
                     
        
       if(model.containsKey(ConstantUtil.SHIPADDRESS_UPDATED)
               || model.containsKey(ConstantUtil.CUSTOMER_UPDATED)){
           
           return;   //display customerAttributes#message only
            
       } else if(model.containsKey(this.EXPIRED_REQUEST_MSG)) {             
           
            shipAddressNotFoundMessage = (String)model.get(this.EXPIRED_REQUEST_MSG);     
            
       } else if(model.containsKey(CompareAddressUtil2.COMPARE_EXCEPTION)) {
           
           SelectedShipAddressCompareException ex = (SelectedShipAddressCompareException)
                   model.get(CompareAddressUtil2.COMPARE_EXCEPTION);
           
           shipAddressNotFoundMessage = ex.getInvokingTitle() + ": "  
                   + ex.getMessage();
           
       } else {
            if (!customerAttrs.isShipAddressSelected()) {
                return;
            }

            PostalAddress postalAddress = (PostalAddress) session.getAttribute(SELECTED_POSTALADDRESS);

            if (ShipAddress.class.isAssignableFrom(postalAddress.getClass())) {
                
               // compareUtil.throwInconsistentSessionToDbDeletion(customer, (ShipAddress)postalAddress);
                compareUtil.throwCompareDbToSelectedShipAddress((ShipAddress) postalAddress, 
                        customer, "Select Shipping Address"); //throws if compare exception
                
            } else if (Customer.class.isAssignableFrom(postalAddress.getClass())) {
                
                compareUtil.throwCompareModelCustomerToSelectedPostalAddress(customer,
                        postalAddress, "Address Selection Compare"); // throws if compare exception
            }
        }
    } //end init    
    
 
    private void clearUpdateMessages() {
        this.customerAttrs.setShipAddressSuccessMsg("");
        this.customerAttrs.setSuccessMessage("");
    }
    
    private void addSelectModelAttributes(ModelMap map, Customer customer){
        
        map.addAttribute("customer", customer); //Not referenced?
        map.addAttribute("cart", cart); //widget       
        map.addAttribute("customerAttributes", customerAttrs); //success message, expiredTime
        map.addAttribute("shippingAddressController", this); //shipList, previousSelected 
        
    }    
    
    private void throwIllegalArg(String method, String message) {
        
        throw new IllegalArgumentException(EhrLogger.doError(
                   this.getClass().getCanonicalName(), 
                   method, message));
    }
    
    private PostalAddress castAddressToType(PostalAddress postal, AddressTypeEnum type) {
        
        if(type.equals(AddressTypeEnum.Customer))
            return (Customer)postal;
        return (ShipAddress)postal;
    }
    
    
    
}//end controller
